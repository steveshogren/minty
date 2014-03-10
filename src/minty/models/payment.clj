(ns minty.models.payment
  (:require [clojure.java.jdbc :as sql]
            [minty.models.migration :as db]))


(defn all []
  [{:amount 59 :paid-to "Traitor Joes" :allocated "Food"}
   {:amount 499 :paid-to "Starbucks" :allocated "Fun"}
   {:amount 499 :paid-to "Uncle Samuel"}]
  #_(into [] (sql/query db/db ["select * from payments order by id desc"])))

(defn create [payment]
  (sql/insert! db/db :payments [:amount :paid-to] [(:amount payment) (:paid-to payment)]))
