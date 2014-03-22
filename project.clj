(defproject minty "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://minty.herokuapp.com"
  :license {:name "FIXME: choose"
            :url "http://example.com/FIXME"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.1"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [ring/ring-devel "1.1.0"]
                 [org.clojure/java.jdbc "0.3.2"]
                 [postgresql "9.1-901.jdbc4"]     
                 [org.clojure/data.csv "0.1.2"]
                 [ring-basic-authentication "1.0.1"]
                 [environ "0.2.1"]
                 [com.cemerick/drawbridge "0.0.6"]
                 [org.clojure/data.json "0.2.4"]
                 [ring/ring-json "0.2.0"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.8.10"]
            [environ/environ.lein "0.2.1"]]
  :ring {:handler minty.web/application
         :nrepl {:start? true :port 4005}}
  :hooks [environ.leiningen.hooks]
  ;;:profiles {:production {:env {:production true}}}
  )
