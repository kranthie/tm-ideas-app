(ns com.topmonkeys.ideas.views.admin
  (:use [noir.core :only (pre-route defpage defpartial render)]
        [hiccup.form-helpers :only (form-to text-field password-field submit-button)]
        [hiccup.page-helpers :only (link-to)])
  (:require [com.topmonkeys.ideas.models.user :as users]
            [com.topmonkeys.ideas.views.common :as common]
            [clojure.string :as string]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.session :as session]))

(pre-route "/ideas/admin*" {}
           (when-not (users/is-admin?)
             (resp/redirect "/ideas/login")))

(defpartial error-text [errors]
            [:p (string/join "<br/>" errors)])

;; Login Page
(defpartial user-fields [{:keys [username] :as user}]
            (vali/on-error :username error-text)
            (text-field {:placeholder "Username"} :username username)
            (password-field {:placeholder "Password"} :password))

(defpage "/ideas/login" {:as user}
  (if (users/is-admin?)
    (resp/redirect "/ideas/admin")
    (common/home-layout
      [:header
       [:h2 "Login"]]
      [:div.doc-section
       (form-to [:post "/ideas/login"]
                (user-fields user)
                (submit-button {:class "submit"} "submit"))])))

(defpage [:post "/ideas/login"] {:as user}
         (if (users/login user)
           (resp/redirect "/ideas/admin")
            (render "/ideas/login" user)))

(defpage "/ideas/logout" {}
  (users/logout)  
  (resp/redirect "/ideas/"))

; Admin Page
(defpage "/ideas/admin" {}
         (common/admin-layout
           [:header
              [:h2 "Admin"]]))
