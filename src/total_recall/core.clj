;; A functional solver for the [99designs Total Recall challenge][1].
;;
;;  [1]: http://totalrecall.99cluster.com/

(ns total-recall.core
  (:require [clojure.string :as str]
            [total-recall.api :as api]))

(defn final-pair [unmatched unseen]
  (cond
   (and (= 1 (count unmatched) (count unseen))) [(first unmatched) (first unseen)]
   (and (empty? unmatched) (= 2 (count unseen))) unseen
   :else nil))

(defn solve [game]
  (loop [unmatched {} ; card -> position
         unseen (for [x (range (:width game))
                      y (range (:height game))]
                  {:x x :y y})]
    (if-let [final-pair (final-pair (vals unmatched) unseen)]
      (apply api/finish-game! (:id game) final-pair)
      (let [first-position (first unseen)
            first-card (api/request-card (:id game) first-position)]
        (if-let [matching-position (get unmatched first-card)]
          (do
            (api/request-card (:id game) matching-position)
            (recur (dissoc unmatched first-card)
                   (next unseen)))
          (let [second-position (fnext unseen)
                second-card (api/request-card (:id game) second-position)]
            (if (= first-card second-card)
              (recur unmatched
                     (nnext unseen))
              (if (get unmatched second-card)
                (recur (assoc unmatched first-card first-position)
                       (next unseen)) ; second-position becomes first-position next iteration
                (recur (assoc unmatched
                         first-card first-position
                         second-card second-position)
                       (nnext unseen))))))))))

(defn play [name email]
  (solve (api/init-game! name email)))

;; CLI entry point

(defn -main [& [name email]]
  (println (play (or name "Stuart Campbell")
                 (or email "stuart.campbell@99designs.com"))))
