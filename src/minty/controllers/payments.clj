(ns minty.controllers.payments
  (:require [compojure.core :refer [defroutes GET POST]]
            [clojure.string :as str]
            [ring.util.response :as ring]
            [minty.views.payments :as view]
            [minty.models.payment :as model]
            [clojure.data.json :as json]))

(defn index [] (view/index (model/all) (model/allBuckets)))

(defroutes routes
  (GET  "/" [] (index))
  (GET "/getAllBuckets" [] (json/write-str (model/allBuckets)))
  (GET "/getAllPayments" [] (json/write-str (model/all)))
  (POST "/rule/create" [regex]
        (model/createRule regex))
  (POST "/payment/delete" [id]
        (model/deletePayment id))
  (POST "/payment/create" [amount paid_to]
        (model/createPayment amount paid_to))
  (POST "/bucket/create" [name] (model/createBucket name))
  (POST "/bucket/delete" [id]
        (println (str "Id was: " id))
        (model/deleteBucket id))
  (POST "/" [paidto amount] (create {:amount amount :paid_to paidto}))
  (POST "/deleteBucket" [id] (deleteBucket id))
  (POST "/moveToBucket" [line-id bucket-id]
        (model/moveToBucket line-id bucket-id))
  (POST "/createBucket" [name] (createBucket name)))
