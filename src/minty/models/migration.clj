(ns minty.models.migration
  (:require [minty.models.db :as db]
            [clojure.java.jdbc :as sql]
            [minty.models.payment :as model]))

(def table-list ["payments" "buckets" "rules"])

(defn commify [col]
  (->> col
   (map #(str "'" % "'"))
   (reduce #(str %1 ", " %2))))

(defn table-exists? [t]
  (-> (sql/query db/db
                 [(str "select count(*) from information_schema.tables "
                       "where table_name = '" t "'")])
      first :count))

(defn drop-all []
  (print "Dropping all tables") (flush)
  (map #(if (table-exists? %)
          (sql/db-do-commands db/db (sql/drop-table-ddl (keyword %))))
       table-list))

(defn create-some []
  (model/createBucket "Test")
  (model/createPayment 100 "Jack"))

(defn migrate []
  (drop-all)
  (print "Creating database structure...") (flush)
  (sql/db-do-commands db/db
                      (sql/create-table-ddl :rules
                                            [:id :serial "PRIMARY KEY"]
                                            [:regex :varchar "NOT NULL"]
                                            [:created_at :timestamp
                                             "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"])
                      (sql/create-table-ddl :buckets
                                            [:id :serial "PRIMARY KEY"]
                                            [:name :varchar "NOT NULL"]
                                            [:created_at :timestamp
                                             "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"])
                      (sql/create-table-ddl :payments
                                            [:id :serial "PRIMARY KEY"]
                                            [:paid_to :varchar "NOT NULL"]
                                            [:amount :decimal "NOT NULL"]
                                            [:bucket_id :integer]
                                            [:created_at :timestamp
                                             "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))
  (create-some)
  (println " done"))

(comment
  (migrate)
  )
