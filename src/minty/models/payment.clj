(ns minty.models.payment
  (:require [clojure.java.jdbc :as sql]
            [minty.models.migration :as db]))

(defn all []
  (into [] (sql/query db/db ["select * from payments order by id desc"])))

(defn create [payment]
  #_(throw (Exception. (str "Blah: " payment)))
  (sql/insert! db/db :payments [:amount :paid_to]
               [(Float/parseFloat (:amount payment)) (:paid_to payment)]))


