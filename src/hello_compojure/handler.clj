(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

;store language here
(def language (ThreadLocal.))

(defn map-keys
  [m f]
  (zipmap (map f (keys m)) (vals m)))

(defn translate [s]
  (if (= (.get language) "en")
    s
    (let [language (.get language)
          translation-as-string (slurp (io/resource (str "public/" language "/translation.txt")))
          translation-map (map-keys (json/read-str translation-as-string) keyword)
          result ((keyword s) translation-map)]
      result)
    )
  )


(defn save-language-header
  [handler]
  (fn [request]
    (let [headers (:headers request)
          headers-as-map (map-keys headers keyword)
          language-header (:language headers-as-map "en")]
      (.set language language-header)
      (handler request)
      ))
  )

(defroutes app-routes
           (GET "/hello" []  (translate "Hello"))
           (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-routes save-language-header)
      (wrap-defaults site-defaults)
      ))
