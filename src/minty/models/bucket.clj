(ns minty.models.bucket
  (:require [clojure.java.jdbc :as sql]
            [minty.models.migration :as db]))

(defn all []
  [{:name "Fun" :id 1}
   {:name "Utils" :id 2}
   {:name "Food" :id 3}]
  #_(into [] (sql/query db/db ["select * from buckets order by id desc"])))

(defn create [bucket]
  (sql/insert! db/db :buckets [:name] [(:name payment)]))

(defn delete [bucket]
  (sql/delete! db/db :buckets ["name = ?" (:name payment)]))
