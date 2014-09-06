(ns minty.web
  (:require [compojure.core :refer [defroutes]]
            [ring.adapter.jetty :as ring]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [minty.controllers.payments :as payments]
            [minty.views.layout :as layout]
            [minty.models.migration :as schema]
            [schema.core :as s]
            [schema.macros :as sm]
            [ring.middleware.json :as middleware])
  (:gen-class))

(def Data
  "A schema for a nested data type"
  {:a {:b s/Str
       :c s/Int}
   :d [{:e s/Keyword
        :f [s/Num]}]})

(s/validate
  Data
  {:a {:b "abc"
       :c 123}
   :d [{:e :bc
        :f [12.2 13 100]}
       {:e :bc
        :f [-1]}]})

(sm/defn add :- Int [x :- Int] (+ 1 x))

(defroutes routes
  #'payments/routes
  (route/resources "/")
  (route/not-found (layout/four-oh-four)))

(def application 
  (-> (handler/site #'routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)
      (middleware/wrap-json-params)))

(defn start [port]
  (ring/run-jetty #'application {:port port

                               :join? false}))

(defn -main []
  (schema/migrate)
  (let [port (Integer. (or (System/getenv "PORT") "8080"))]
    (start port)))
