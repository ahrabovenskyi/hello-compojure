(defproject hello-compojure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [org.clojure/data.json "2.3.1"]
                 [com.taoensso/tower "3.0.2"]
                 [taoensso.tower :as tower :refer (with-tscope)]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler hello-compojure.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})




