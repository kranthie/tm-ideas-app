(ns com.topmonkeys.ideas.models.idea
  (:use [somnium.congomongo :only (with-mongo fetch fetch-one
                                    insert! fetch-and-modify destroy!)]
        [clj-time.core :only (date-time now interval in-secs in-minutes in-hours in-days)]
        [clj-time.coerce :only (to-long)])
  (:require [com.topmonkeys.ideas.models.db :as db]
            [clojure.string :as string])
  (:import com.petebevin.markdown.MarkdownProcessor))
 
(defonce ideas-coll :ideas)
(defonce mdp (MarkdownProcessor.))

;; Utils
(defn generate-handle
  "Generates a handle based on the idea's title."
  [title]
  (-> title
    (string/lower-case)
    (string/replace #"[^a-zA-Z0-9\s]" "")
    (string/replace #" " "-")))

(defn get-elapsed-time
  "Returns human friendly time interval."
  [then]
  (let [i (interval then (now)) 
        minutes (in-minutes i)
        hours (in-hours i)
        days (in-days i)
        weeks (Math/round (/ days 7.0))
        months (Math/round (/ days 30.0))
        years (Math/round (/ days 365.0))]
    (cond
      (= minutes 0) "a few seconds ago"
      (= minutes 1) "a minute ago"
      (< minutes 60) (str minutes " minutes ago")
      (= hours 1) "an hour ago"
      (< hours 24) (str hours " hours ago")
      (= days 1) "a day ago"
      (< days 7) (str days " days ago")
      (= weeks 1) "a week ago"
      (< weeks 5) (str weeks " weeks ago")
      (= months 1) "a month ago"
      (< months 12) (str months " months ago")
      (= years 1) "a year ago"
      :else (str years " years ago")))) 

(defn md-to-html [md-text]
  (. mdp (markdown md-text)))


;; Create
(defn add-idea
  "Adds an idea."
  [{:keys [title description handle username]}]
  (with-mongo db/connection
    (insert! ideas-coll {:id (db/get-next-id ideas-coll) :title title 
                         :handle handle :username username :description description :html-description (md-to-html description) 
                         :comments [] :created-at (to-long (now))})))
 
(defn add-comment
  "Adds a comment to an idea."
  [{:keys [handle comment username]}]
  (with-mongo db/connection
    (println (str "Comment: " comment))
    (fetch-and-modify ideas-coll {:handle handle} {:$push {:comments {:id (db/get-next-id (str ideas-coll "_comments")) :comment comment :html-comment (md-to-html comment) :username username :created-at (to-long (now))}}} :return-new? true)))
 
;; Read
(defn get-all-ideas
  "Fetches all ideas from the database."
  []
  (with-mongo db/connection
    (fetch ideas-coll)))
 
(defn get-idea-by-handle
  "Fetches idea by handle."
  [handle]
  (with-mongo db/connection
    (fetch-one ideas-coll :where {:handle handle})))
 
(defn get-idea-by-id
  "Fetches idea by id."
  [id]
  (with-mongo db/connection
    (fetch-one ideas-coll :where {:id id})))
 
;; Update
(defn update-idea
  "Updates an idea."
  [{:keys [id title description handle] :as idea}]
  (println idea)
  (with-mongo db/connection
    (fetch-and-modify ideas-coll {:id id} {:$set {:title title :handle handle :description description :html-description (md-to-html description) :updated-at (to-long (now))}} :return-new? true)))
 
;; Delete
(defn remove-idea
  "Deletes an idea."
  [id]
  (with-mongo db/connection
    (destroy! ideas-coll {:id id})))
 
(defn- remove-all-ideas
  "Deletes all ideas."
  []
  (with-mongo db/connection
    (destroy! ideas-coll {})))
 

  
 
