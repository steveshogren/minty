(ns minty.models.payment
  (:require [clojure.java.jdbc :as sql]))

(def spec (or (System/getenv "DATABASE_URL")
              (System/getenv "HEROKU_POSTGRESQL_AMBER_URL")
              "postgresql://localhost:5432/shouter"))

(defn all []
  [{:amount 59 :paid-to "Jim Jam"}
   {:amount 499 :paid-to "Test"}
   {:amount 499 :paid-to "Horse"}]
  #_(into [] (sql/query spec ["select * from payments order by id desc"])))

(defn create [payment]
  (sql/insert! spec :payments [:amount :paid-to] [(:amount payment) (:paid-to payment)]))
