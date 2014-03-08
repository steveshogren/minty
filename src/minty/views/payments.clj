(ns minty.views.payments
  (:require [minty.views.layout :as layout]
            [hiccup.core :refer [h]]
            [hiccup.form :as form]))

(defn payments-form []
  [:div {:id "shout-form" :class "sixteen columns alpha omega"}
   (form/form-to [:post "/"]
                 (form/label "shout" "What do you want to SHOUT?")
                 (form/text-area "shout")
                 (form/submit-button "SHOUT!"))])

(defn display-payments [payments]
  [:div {:class "shouts sixteen columns alpha omega"}
   [:h2 "TEST"]
   [:ul
    (map
     (fn [payment] [:li {:class "shout"} (h (str (:amount payment) " -- " (:paid-to payment)))])
     payments)]])

(defn index [payments]
  (layout/common "Payments"
                 #_(payments-form)
                 [:div {:class "clear"}]
                 (display-payments payments)))
