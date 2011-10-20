(ns com.topmonkeys.ideas.models
  (:require [com.topmonkeys.ideas.models.user :as users]))

(defn initialize!
  "Initializes the models."
  []
  (users/initialize))