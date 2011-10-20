(ns com.topmonkeys.ideas.models.db
  (:use [somnium.congomongo :only (make-connection with-mongo with-mongo fetch-and-modify)]))

; Database Vars
(def ^{:private true} db-name "ideas-db_test")
(def ^{:private true} db-details {:host "127.0.0.1" :port 27017})

; A mongodb connection.
(defonce connection 
  (make-connection db-name db-details))

; A keyword for a collection to store user friendly ids for mongodb collections.
(defonce counters-coll 
  :counters)

(defn get-next-id
  "Fetches the next ID for the provided colection."
  [coll-name]
  (with-mongo connection
    (:next (fetch-and-modify counters-coll {:_id coll-name} {:$inc {:next 1}} :return-new? true :upsert? true))))
