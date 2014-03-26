(ns minty.models.payment
  (:require [clojure.java.jdbc :as sql]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clj-time.format :as d]
            [minty.models.db :as db]))


(defn getAllPayments [range]
  (let [range (Integer/parseInt range)
        payments (into [] (sql/query db/db
                                     [(str "SELECT p.id AS payment_id, p.paid_to, p.amount, b.id AS bucket_id, b.name, p.on_date
                                      FROM payments AS p
                                      LEFT JOIN buckets as b ON p.bucket_id = b.id
                                      WHERE p.on_date <= CURRENT_DATE
                                            AND p.on_date > (CURRENT_DATE - INTERVAL '" range " days')::date
                                            AND p.amount < 0
                                      ORDER BY on_date DESC")]))]
    (map (fn [p] (assoc p :on_date (str (:on_date p)))) payments)))

(defn getAllRules []
  (into [] (sql/query db/db
                      ["select r.id as rule_id, r.regex, r.bucket_id, b.name
                        from rules as r
                        left join buckets as b on r.bucket_id = b.id"])))

(defn match-bucket-to-payment [buckets payment-groups]
  (map (fn [b]
         (if-let [amount (-> (filter (fn [[bid _]] (= bid (:bucket_id b)))
                                     payment-groups)
                             first
                             second)]
           (let [bound-amount (if (nil? (:amount b)) 0 (:amount b))]
             (assoc b :amount (+ amount bound-amount)))
           (assoc b :amount 0)))
       buckets))

(defn sum-payments-in-group [payment-group]
  (map (fn [[k v]] [k (reduce #(+ %1 (:amount %2)) 0 v)]) payment-group))

(defn sum-buckets [buckets]
  (let [amounts (map :amount buckets)
        amounts (map #(if (nil? %) 0 %) amounts)]
    (reduce + amounts)))

(defn allBuckets [range]
  (let [payments (group-by :bucket_id (grouped-payments range))
        payment-counts (sum-payments-in-group payments)
        buckets (into [] (sql/query db/db
                                    ["select b.id as bucket_id, b.name, sum(p.amount) as amount
                                      from buckets as b
                                      left join payments as p on b.id = p.bucket_id group by b.id"]))
        buckets (match-bucket-to-payment buckets payment-counts)
        total-amount (sum-buckets buckets)]
    (println (str "Total: " total-amount))
    (println (str "Amounts: " (reduce str (map :amount buckets))))
    (map #(assoc % :percentage (* (with-precision 3 (/ (:amount %) total-amount)) 100)) buckets)))

(defn- match-rule-to-payment [rules payment]
  (let [rule-match
        (first (filter (fn [rule]
                         (.contains (:paid_to payment) (:regex rule)))
                       rules))]
    (if rule-match
      (merge payment rule-match)
      payment)))

(defn grouped-payments [range]
  (let [payments (getAllPayments range)
        rules (getAllRules)]
    (map (fn [p] 
           (if (:bucket_id p)
             p
             (match-rule-to-payment rules p)))
         payments)))

(defn getSummedRules [range]
  (let [rules (getAllRules)
        payments (group-by :rule_id (grouped-payments range))
        summed_payments (sum-payments-in-group payments)]
    (map (fn [rule] (assoc rule :amount (second (first (filter (fn [[rule_id _]] (= (:rule_id rule) rule_id)) summed_payments))))) rules)))

(defn createRule [regex bucket_id]
  (sql/insert! db/db :rules [:regex :bucket_id] [regex bucket_id]))

(defn deleteRule [id]
  (sql/delete! db/db :rules ["id = ?" id]))

(defn createPayment [amount paid_to date]
  (sql/insert! db/db :payments [:amount :paid_to :on_date]
               [(Float/parseFloat amount)
                paid_to
                (new java.sql.Date (.getTime (.parse (java.text.SimpleDateFormat. "MM/dd/yyyy") date)))]))


(defn deletePayment [id]
  (sql/delete! db/db :payments ["id = ?" id]))

(defn getBucket [id]
  (sql/query db/db ["select * from buckets where id = ?" id]))

(defn createBucket [name]
  (sql/insert! db/db :buckets [:name] [name]))

(defn deleteBucket [id]
  (sql/delete! db/db :buckets ["id = ?" id]))

(defn moveToBucket [line-id bucket-id]
  (sql/update! db/db :payments {:bucket_id bucket-id} ["id = ?" line-id]))

(defn save-payments []
  (with-open [in-file (io/reader "resources/data.csv")]
    (doall
     (if-let [c (csv/read-csv in-file)]
       (map (fn [[_ date _ paid_to amount _]]
              (createPayment amount paid_to date))
            (drop 1 c))))))

(comment
  (moveToBucket 1 1)
  (moveToBucket 1 2)
  (save-payments)
  (createBucket "b1")
  (createRule "test" 1)
  (createPayment "44" "test" "12/12/1999")
  (getAllPayments)
  )


