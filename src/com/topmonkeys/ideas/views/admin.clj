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
    (resp/redirect "/ideas/admin/ideas/")
    (common/home-layout
      [:header
       [:h2 "Login"]]
      [:div.doc-section
       (form-to [:post "/ideas/login"]
                (user-fields user)
                (submit-button {:class "submit"} "submit"))])))

(defpage [:post "/ideas/login"] {:as user}
         (if (users/login user)
           (resp/redirect "/ideas/admin/ideas/")
            (render "/ideas/login" user)))

(defpage "/ideas/logout" {}
  (users/logout)  
  (resp/redirect "/ideas/"))

; Admin - Ideas Page
(defpage "/ideas/admin/ideas/" {}
  (common/admin-layout
    [:header
     [:h2 "Admin|Ideas"]]))

; Admin - Users Page
(defpartial user-partial [{:keys [username password] :as user}]
   [:div.users {:onclick (str "location.href='" "/ideas/admin/users/view/" username "'")}
    [:h4.align-left (link-to {:class "user-link"} (str "/ideas/admin/users/view/" username) username)]
    [:small.align-right (link-to (str "/ideas/admin/users/edit/" username) "Edit") " | " (link-to (str "/ideas/admin/users/remove/" username) "Remove") ]
    [:div.clear-align]])

(defpartial users-page [users-coll]
  (map user-partial users-coll))

(defpage "/ideas/admin/users/" {}
  (common/admin-layout
    [:header
     [:h2.align-left "Admin|Users"]
     [:a.align-right "Add user"]
     [:div.clear-align]]
    (users-page (users/get-all-users))))

(defpartial user-view-page [user]
  (common/admin-layout
    (users-page user)))

(defpage "/ideas/admin/users/view/:username" {:keys [username]}
  (if-let [user (users/get-user-by-username username)]
    (user-view-page [user])))

