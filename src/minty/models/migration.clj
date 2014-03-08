(ns minty.models.migration
  (:require [clojure.java.jdbc :as sql]
            [minty.models.payment :as payment]))

(defn migrated? []
  (-> (sql/query payment/spec
                 [(str "select count(*) from information_schema.tables "
                       "where table_name='payments'")])
      first :count pos?))

(defn migrate []
  (when (not (migrated?))
    (print "Creating database structure...") (flush)
    (sql/db-do-commands payment/spec
                        (sql/create-table-ddl
                         :payments
                         [:id :serial "PRIMARY KEY"]
                         [:paid_to :varchar "NOT NULL"]
                         [:amount :decimal "NOT NULL"]
                         [:created_at :timestamp
                          "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))
    (println " done")))
