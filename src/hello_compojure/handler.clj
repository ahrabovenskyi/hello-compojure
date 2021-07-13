(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def ^:dynamic dynamic-language "en")

(defn map-keys
  [m f]
  (zipmap (map f (keys m)) (vals m)))

(defn translate [s]
  ;(let [f (future (Thread/sleep 5000) (println "done") (println "language in future" dynamic-language) 100)] @f)
  (if (= dynamic-language "en")
    s
    (let [translation-as-string (slurp (io/resource (str "public/" dynamic-language "/translation.txt")))
          translation-map (map-keys (json/read-str translation-as-string) keyword)
          result ((keyword s) translation-map)]
      result)))

(defn save-language-header
  [handler]
  (fn [request]
    (let [headers (:headers request)
          headers-as-map (map-keys headers keyword)
          language-header (:language headers-as-map "en")]
      (binding [dynamic-language language-header] (handler request))))
  )

(defroutes app-routes
           (GET "/hello" []  (translate "Hello"))
           (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-routes save-language-header)
      (wrap-defaults site-defaults)))
