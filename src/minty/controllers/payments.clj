(ns minty.controllers.payments
  (:require [compojure.core :refer [defroutes GET POST]]
            [clojure.string :as str]
            [ring.util.response :as ring]
            [minty.views.payments :as view]
            [minty.models.payment :as model]))

(defn index [] (view/index (model/all) (model/allBuckets)))

(defn create [payment]
  (model/create payment)
  (ring/redirect "/"))

(defn createBucket [name]
  (model/createBucket name)
  (ring/redirect "/"))

(defn deleteBucket [id]
  (model/deleteBucket id)
  (ring/redirect "/"))

(defroutes routes
  (GET  "/" [] (index))
  (POST "/" [paidto amount] (create {:amount amount :paid_to paidto}))
  (POST "/deleteBucket" [id] (deleteBucket id))
  (POST "/moveToBucket" [line-id bucket-id]
        (model/moveToBucket line-id bucket-id))
  (POST "/createBucket" [name] (createBucket name)))
