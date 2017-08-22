(ns movie-app.response
  (:import [org.bson.types ObjectId])
  (:require   [movie-app.db :as db]
              [movie-app.utility :as util]
              [cheshire.core :as cheshire]
              [cheshire.generate :as generate :refer [add-encoder encode-str]]))


(generate/add-encoder org.bson.types.ObjectId generate/encode-str)

(defn json-200 [to-render]
  {:status 200
   :headers {"Content-Type" "text/json; charset=utf-8"
             "Cache-Control" "no-cache, no-store, must-revalidate"
             "Pragma" "no-cache"
             "Expires" "0"}
   :body (cheshire/generate-string
          to-render {:key-fn #(util/memoized->camelCase (name %))})})

(defn json-404 []
  {:status 404
   :headers {"Content-Type" "text/json; charset=utf-8"
             "Cache-Control" "no-cache, no-store, must-revalidate"
             "Pragma" "no-cache"
             "Expires" "0"}
   :body (cheshire/generate-string "Page not found")})

(defn result-nil? [result]
  (if (nil? result)
    (json-404)
    (json-200 result)))

(defn get-map-by-id [col id]
  (let [result
        (try
          (db/get-by-id col  id)
          (catch IllegalArgumentException e (str "Caught exception: " (.getMessage e))))]
    (result-nil? result)))

(defn get-col [col]
  (let [result (db/get-maps col)]
    (result-nil? result)))
