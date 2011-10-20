(ns com.topmonkeys.ideas.server
  (:require [noir.server :as server]
            [com.topmonkeys.ideas.models :as models]))

(server/load-views "src/com/topmonkeys/ideas/views/")

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "3000"))]
    (models/initialize!)
    (server/start port {:mode mode
                        :ns 'com.topmonkeys.ideas})))
