(ns minty.models.migration
  (:require [clojure.java.jdbc :as sql]))

(def db (or (System/getenv "DATABASE_URL")
            (System/getenv "HEROKU_POSTGRESQL_AMBER_URL")
            "postgresql://localhost:5432/shouter"))

(defn migrated? []
  (sql/db-do-commands db
                      (sql/execute! (sql/drop-table-ddl "buckets"))
                      (sql/execute! (sql/drop-table-ddl "payments")))
  false
  #_(-> (sql/query db
                   [(str "select count(*) from information_schema.tables "
                         "where table_name='payments'")])
        first :count pos?))

(defn migrate []
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
