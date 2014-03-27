(ns minty.controllers.payments
  (:require [compojure.core :refer [defroutes GET POST]]
            [clojure.string :as str]
            [ring.util.response :as ring]
            [minty.views.payments :as view]
            [minty.models.payment :as model]
            [clojure.data.json :as json]))

(defroutes routes
  (GET  "/" [] (ring/resource-response "index.html" {:root "public"}))
  (GET "/totals" [range] (json/write-str (model/totals range)))
  (GET "/buckets" [range] (json/write-str (model/allBuckets range)))

  (GET "/payments" [range] (json/write-str (model/grouped-payments range)))

  (GET "/rules" [range]
       #_(println (str (model/getSummedRules)))
       (json/write-str (model/getSummedRules range)))

  (POST "/rule/create" [regex bucket_id]
        (println (str "Rule create regex: " regex " bucket: " bucket_id))
        (model/createRule regex bucket_id))

  (POST "/rule/delete" [id] (model/deleteRule id))

  (POST "/payment/delete" [id]
        (model/deletePayment id))
  (POST "/payment/create" [amount paid_to date]
        (model/createPayment amount paid_to date))

  (POST "/bucket/create" [name] (model/createBucket name))
  (POST "/bucket/delete" [id]
        (println (str "Id was: " id))
        (model/deleteBucket id))
  )
