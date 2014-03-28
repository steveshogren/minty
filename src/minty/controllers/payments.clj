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
  (GET "/rules" [range] (json/write-str (model/getSummedRules range)))

  (POST "/rule/create" [regex bucket_id] (model/createRule regex bucket_id))
  (POST "/rule/delete" [id] (model/deleteRule id))

  (POST "/bucket/create" [name] (model/createBucket name))
  (POST "/bucket/delete" [id] (model/deleteBucket id))
  )
