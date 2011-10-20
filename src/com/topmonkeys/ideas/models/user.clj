(ns com.topmonkeys.ideas.models.user
  (:use [somnium.congomongo :only (with-mongo fetch fetch-one
                                    insert! fetch-and-modify destroy!)]
        [clj-time.core :only (now)]
        [clj-time.coerce :only (to-long)])
  (:require [com.topmonkeys.ideas.models.db :as db]
            [clojure.string :as string]
            [noir.session :as session]
            [noir.validation :as vali]))

(def users-coll :users)

;; Create
(defn add-user
  "Adds a user."
  [{:keys [username password]}]
  (with-mongo db/connection
    (insert! users-coll {:id (db/get-next-id users-coll) :username username :password password :created-at (to-long (now))})))

;; Read
(defn get-all-users
  "Fetches all users from the database."
  []
  (with-mongo db/connection
    (fetch users-coll)))
 
(defn get-user-by-username
  "Fetches user by username."
  [username]
  (with-mongo db/connection
    (fetch-one users-coll :where {:username username})))

(defn get-user-by-id
  "Fetches idea by id."
  [id]
  (with-mongo db/connection
    (fetch-one users-coll :where {:id id})))

;; Update
(defn update-user
  "Updates a user."
  [{:keys [username password]}]
  (with-mongo db/connection
    (fetch-and-modify users-coll {:username username} {:$set {:password password :updated-at (to-long (now))}} :return-new? true)))

;; Delete
(defn remove-user
  "Deletes a user."
  [{:keys [username]}]
  (with-mongo db/connection
    (destroy! users-coll {:username username})))
 
(defn- remove-all-users
  "Deletes all users."
  []
  (with-mongo db/connection
    (destroy! users-coll {})))

;; Login
(defn login 
  "Authenticates the user and adds information to the session."
  [{:keys [username password] :as user}]
  (if (= password (:password (get-user-by-username username)))
    (do
      (session/put! :admin true)
      (session/put! :username username))
    (vali/set-error :username "Invalid username or password")))

(defn logout
  "Clears the session."
  []
  (session/clear!))
  
(defn is-admin? 
  "Checks if the logged in user is an admin."
  []
  (session/get :admin))

(defn get-username
  "Returns the username from the session"
  []
  (session/get :username))

; INITIALIZE
(defn initialize
  "Initializes the users collection."
  []
  (let [user {:username "admin" :password "password"}]
    (when-not (get-user-by-username (:username user))
      (add-user user))))
