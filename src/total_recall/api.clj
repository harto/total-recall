(ns total-recall.api
  (:require [clojure.data.json :as json]
            [clj-http.client :as http]))

(def endpoint "http://totalrecall.99cluster.com")

(defn init-game!
  "Start a new game and return a map with :id, :width and :height"
  [name email]
  (-> (format "%s/games/" endpoint)
      (http/post {:form-params {:name name :email email} :force-redirects true})
      (get :body)
      (json/read-json)))

(defn request-card [game-id {:keys [x y]}]
  (-> (format "%s/games/%s/cards/%s,%s" endpoint game-id x y)
      (http/get)
      (get :body)))

(defn finish-game! [game-id {x1 :x y1 :y} {x2 :x y2 :y}]
  (-> (format "%s/games/%s/end" endpoint game-id)
      (http/post {:form-params {:x1 x1 :y1 y1 :x2 x2 :y2 y2}})
      (get :body)
      (json/read-json)))
