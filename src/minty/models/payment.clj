(ns minty.models.payment
  (:require [clojure.java.jdbc :as sql]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clj-time.format :as d]
            [minty.models.db :as db]))


(defn getAllPayments []
  (into [] (sql/query db/db
                      ["select p.id, p.paid_to, p.amount, b.id as bucket_id, b.name
                        from payments as p
                        left join buckets as b on p.bucket_id = b.id"])))

(defn getAllRules []
  (into [] (sql/query db/db
                      ["select r.id, r.regex, r.bucket_id, b.name
                        from rules as r
                        left join buckets as b on r.bucket_id = b.id"])))

(defn match-bucket-to-payment [buckets payment-groups]
  (map (fn [b]
         (if-let [amount (-> (filter (fn [[bid _]] (= bid (:id b)))
                                     payment-groups)
                             first
                             second)]
           (let [bound-amount (if (nil? (:amount b)) 0 (:amount b))]
             (assoc b :amount (+ amount bound-amount)))
           b))
       buckets))

(defn allBuckets []
  (let [payments (group-by :bucket_id (grouped-payments))
        payment-counts (map (fn [[k v]] [k (reduce #(+ %1 (:amount %2)) 0 v)]) payments)
        buckets (into [] (sql/query db/db
                                    ["select b.id, b.name, sum(p.amount) as amount
                                      from buckets as b
                                      left join payments as p on b.id = p.bucket_id group by b.id"]))]
    (match-bucket-to-payment buckets payment-counts)))

(defn- match-rule-to-payment [rules payment]
  (let [rule-match
        (first (filter (fn [rule]
                         (.contains (:paid_to payment) (:regex rule)))
                       rules))]
    (if rule-match
      (merge payment rule-match)
      payment)))

(defn grouped-payments []
  (let [payments (getAllPayments)
        rules (getAllRules)]
    (map (fn [p] 
           (if (:bucket_id p)
             p
             (match-rule-to-payment rules p)))
         payments)))

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


