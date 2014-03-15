(ns minty.views.payments
  (:require [minty.views.layout :as layout]
            [hiccup.core :refer [h]]
            [hiccup.form :as form]))

(defn payments-form []
  [:div 
   [:span "Create new Payment"]
   (form/form-to [:post "/"]
                 (form/label "paid-to" "Paid to:")
                 (form/text-field "paidto")
                 (form/label "amount" "Amount:")
                 (form/text-field "amount")
                 (form/submit-button "Create"))])

(defn buckets-form []
  [:div 
   [:span "Create new Bucket"]
   (form/form-to [:post "/createBucket"]
                 (form/label "name" "Name")
                 (form/text-field "name")
                 (form/submit-button "Create"))])

(defn display-payments [payments]
  [:div 
   [:h2 "Payments"]
   [:ul
    (map
     (fn [payment] [:li (h (str (:amount payment)
                                " -- Paid: "
                                (:paid_to payment)
                                " -- id: "
                                (:id payment)
                                " -- name: "
                                (:name payment)
                                " -- bid: "
                                (:bucket_id payment)))])
     payments)]])

(defn display-buckets [buckets]
  [:div 
   [:h2 "Buckets"]
   [:ul
    (map
     (fn [bucket]
       [:li (h (str "name: "(:name bucket)
                    " -- id: " (:id bucket)
                    " -- amount: " (:amount bucket)
                    ))])
     buckets)]])

(defn index [payments buckets]
  (layout/common "Payments"
                 (payments-form)
                 [:div {:class "clear"}]
                 (display-payments payments)
                 (buckets-form)
                 (display-buckets buckets)))
