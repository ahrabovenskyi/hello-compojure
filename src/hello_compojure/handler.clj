(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

; here we store the language separately for every request
(def ^:dynamic dynamic-language "en")

(def translations-map (json/read-str (slurp (io/resource (str "public/translations.json"))) :key-fn keyword))
(def supported-languages (conj (set (map name (keys translations-map))) "en"))

; since language is stored in 'dynamic-language' var
; we don't need to pass the language as argument to the translation function
(defn translate [string-to-translate]
  (if (= dynamic-language "en")
    string-to-translate
    (let [language-key (keyword dynamic-language)
          string-to-translate-key (keyword string-to-translate)
          translations-map-for-the-language (language-key translations-map)
          result (string-to-translate-key translations-map-for-the-language)]
      result)))

; new middleware where we retrieve the language from the header adn store it to 'dynamic-language' var
(defn save-language-header
  [handler]
  (fn [request]
    (let [headers (:headers request)
          headers-as-map (zipmap (map keyword (keys headers)) (vals headers))
          language-header (:language headers-as-map)
          ;if language is not supported, english is used
          language-to-use (or (some #(and (= language-header %) %) supported-languages) "en")]
      ; The whole magic happens here.
      ; Because of (handler request) is wrapped by binding,
      ; every request has its own dynamic-language value which can not be overridden by another request.
      (binding [dynamic-language language-to-use] (handler request)))))

(defroutes app-routes
           ; and this how we perform the translation.
           ; we just pass the string to 'translate' function as argument
           (GET "/hello" [] (translate "Hello"))
           (route/not-found "Not Found"))

(def app
  (-> app-routes
      ; in this middleware, we store language to dynamic-language variable that can be accessed from any place of code
      (wrap-routes save-language-header)
      (wrap-defaults site-defaults)))

; this is test comment
