(ns minty.controllers.payments
  (:require [compojure.core :refer [defroutes GET POST]]
            [clojure.string :as str]
            [ring.util.response :as ring]
            [minty.views.payments :as view]
            [minty.models.payment :as model]
            [clojure.data.json :as json]))

#_(defn index [] (view/index (model/getAllPayments) (model/allBuckets)))

(defroutes routes
  (GET  "/" [] (ring/resource-response "index.html" {:root "public"}))
  (GET "/getAllBuckets" [] (json/write-str (model/allBuckets)))
  (GET "/getAllPayments" [] (json/write-str (model/getAllPayments)))

  (GET "/payments" [] (json/write-str (model/grouped-payments)))

  (GET "/rule/getAll" [] (json/write-str (model/getAllRules)))

  (POST "/rule/create" [regex bucket_id]
        (println (str "Rule create regex: " regex " bucket: " bucket_id))
        (model/createRule regex bucket_id))

  (POST "/rule/delete" [id] (model/deleteRule id))

  (POST "/payment/delete" [id]
        (model/deletePayment id))
  (POST "/payment/create" [amount paid_to]
        (model/createPayment amount paid_to))

  (POST "/bucket/create" [name] (model/createBucket name))
  (POST "/bucket/delete" [id]
        (println (str "Id was: " id))
        (model/deleteBucket id))
  )
