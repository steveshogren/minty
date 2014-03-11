(ns minty.models.migration
  (:require [clojure.java.jdbc :as sql]))

(def db (or (System/getenv "DATABASE_URL")
            (System/getenv "HEROKU_POSTGRESQL_AMBER_URL")
            "postgresql://localhost:5432/shouter"))

(def table-list ["payments", "buckets"])

(defn drop-all []
  (sql/execute! db [(sql/drop-table-ddl "buckets")])
  (sql/execute! db [(sql/drop-table-ddl "payments")]))

(defn commify [col]
  (->> col
   (map #(str "'" % "'"))
   (reduce #(str %1 ", " %2))))

(defn migrated? []
  (-> (sql/query db
                 [(str "select count(*) from information_schema.tables "
                       "where table_name in (" (commify table-list) ")")])
      first :count (= (count table-list))))

(defn migrate []
  (drop-all)
  (when (not (migrated?))
    (print "Creating database structure...") (flush)
    (sql/db-do-commands db
                        (sql/create-table-ddl
                         :buckets
                         [:id :serial "PRIMARY KEY"]
                         [:name :varchar "NOT NULL"]
                         [:created_at :timestamp
                          "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"])
                        (sql/create-table-ddl
                         :payments
                         [:id :serial "PRIMARY KEY"]
                         [:paid_to :varchar "NOT NULL"]
                         [:amount :decimal "NOT NULL"]
                         [:created_at :timestamp
                          "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))
    (println " done")))
