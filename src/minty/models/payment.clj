(ns minty.models.payment
  (:require [clojure.java.jdbc :as sql]
            [minty.models.db :as db]))

(defn all []
  (into [] (sql/query db/db
                      ["select p.id, p.paid_to, p.amount, b.id as bucket_id, b.name from payments as p left join buckets as b on p.bucket_id = b.id"])))


(defn create [payment]
  (sql/insert! db/db :payments [:amount :paid_to]
               [(Float/parseFloat (:amount payment)) (:paid_to payment)]))

(defn getBucket [id]
  (sql/query db/db ["select * from buckets where id = ?" id]))

(defn allBuckets []
  (into [] (sql/query db/db
                      ["select b.id, b.name, sum(p.amount) as amount from buckets as b left join payments as p on b.id = p.bucket_id group by b.id"])))


(defn createBucket [name]
  (sql/insert! db/db :buckets [:name] [name]))

(defn deleteBucket [id]
  (sql/delete! db/db :buckets ["id = ?" id]))

(defn moveToBucket [line-id bucket-id]
  (sql/update! db/db :payments {:bucket_id bucket-id} ["id = ?" line-id]))

(comment
  (moveToBucket 1 1)
  (moveToBucket 1 2)
  )


