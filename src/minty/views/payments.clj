(ns minty.views.payments
  (:require [minty.views.layout :as layout]
            [hiccup.core :refer [h]]
            [hiccup.form :as form]))

(defn payments-form []
  [:div {:id "shout-form" :class "sixteen columns alpha omega"}
   [:span "Create new Payment"]
   (form/form-to [:post "/"]
                 (form/label "paid_to" "Paid to:")
                 (form/text-field "paid_to")
                 (form/label "amount" "Amount:")
                 (form/text-field "amount")
                 (form/submit-button "Create"))])

(defn display-payments [payments]
  [:div {:class "shouts sixteen columns alpha omega"}
   [:h2 "TEST"]
   [:ul
    (map
     (fn [payment] [:li {:class "shout"} (h (str (:amount payment) " -- " (:paid_to payment)))])
     payments)]])

(defn index [payments]
  (layout/common "Payments"
                 (payments-form)
                 [:div {:class "clear"}]
                 (display-payments payments)))
