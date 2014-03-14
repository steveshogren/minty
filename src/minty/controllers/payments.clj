(ns minty.controllers.payments
  (:require [compojure.core :refer [defroutes GET POST]]
            [clojure.string :as str]
            [ring.util.response :as ring]
            [minty.views.payments :as view]
            [minty.models.payment :as model]))

(defn index []
  (view/index (model/all)))

(defn create [payment]
  (model/create payment)
  (ring/redirect "/"))

(defroutes routes
  (GET  "/" [] (index))
  (POST "/" [paidto amount] (create {:amount amount :paid_to paidto})))
