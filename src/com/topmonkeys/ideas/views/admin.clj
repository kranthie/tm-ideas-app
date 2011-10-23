(ns com.topmonkeys.ideas.views.admin
  (:use [noir.core :only (pre-route defpage defpartial render)]
        [hiccup.form-helpers :only (form-to text-field text-area password-field submit-button)]
        [hiccup.page-helpers :only (link-to)])
  (:require [com.topmonkeys.ideas.models.user :as users]
            [com.topmonkeys.ideas.models.idea :as ideas]
            [com.topmonkeys.ideas.views.common :as common]
            [clojure.string :as string]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.session :as session]
            [clj-time.core :as date]
            [clj-time.coerce :as date-util]))

(defonce heading-sep " &raquo; ")

(pre-route "/ideas/admin*" {}
           (when-not (users/is-admin?)
             (resp/redirect "/ideas/login")))

(defpartial error-text [errors]
            [:p (string/join "<br/>" errors)])

;; Login Page
(defpartial login-fields [{:keys [username] :as user}]
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
                (login-fields user)
                (submit-button {:class "submit"} "submit"))])))

(defpage [:post "/ideas/login"] {:as user}
         (if (users/login user)
           (resp/redirect "/ideas/admin/ideas/")
            (render "/ideas/login" user)))

(defpage "/ideas/logout" {}
  (users/logout)  
  (resp/redirect "/ideas/"))

; Admin - Ideas Page
(defpartial idea-partial [{:keys [id handle title description] :as idea}]
   [:div.ideas {:onclick (str "location.href='" "/ideas/admin/ideas/view/" id "'")}
    [:h4.align-left (link-to {:class "user-link"} (str "/ideas/admin/ideas/view/" id) title)]
    [:small.align-right (link-to (str "/ideas/admin/ideas/edit/" id) "Edit") " | " (link-to (str "/ideas/admin/ideas/remove/" id) "Remove") ]
    [:div.clear-align]])

(defpartial ideas-page [ideas-coll]
  (map idea-partial ideas-coll))

(defpage "/ideas/admin/ideas/" {}
  (common/admin-layout
    [:header
     [:h2.align-left (link-to (str "/ideas/admin/ideas/") "Admin") heading-sep
      (link-to (str "/ideas/admin/ideas/") "Ideas")]
     (link-to {:class "align-right"} (str "/ideas/admin/ideas/add") "Add idea")
     [:div.clear-align]]
    (ideas-page (ideas/get-all-ideas))))

;; View Idea
(defpartial idea-view-partial [{:keys [id title html-description created-at username] :as idea}]
  [:div
     [:header
      [:h3 (link-to (str "/ideas/admin/ideas/view/" id) title)]
      [:h6 "Added by " (link-to (str "/ideas/user/" username) username) " " (ideas/get-elapsed-time (date-util/from-long created-at))]]
     [:div.doc-section html-description]])

(defpartial idea-header-partial [idea]
  [:header
     [:h2.align-left (link-to (str "/ideas/admin/ideas/") "Admin") heading-sep
      (link-to (str "/ideas/admin/ideas/") "Ideas") heading-sep
      (link-to (str "/ideas/admin/ideas/view/" (:id idea)) (:id idea))]
     [:div.clear-align]])

(defpartial idea-view-page [idea]
  (common/admin-layout
    (idea-header-partial idea)
    (idea-view-partial idea)))

(defpage "/ideas/admin/ideas/view/:id" {:keys [id]}
  (let [idea (ideas/get-idea-by-id (read-string id))]
    (when idea
      (idea-view-page idea))))

;; Edit Idea
(defpartial idea-fields [{:keys [title description] :as idea}]
  (vali/on-error :title error-text)
  (text-field {:placeholder "Title" :id "edit-idea"} :title title)
  (vali/on-error :description error-text)
  (text-area {:placeholder "Description"} :description description))

(defpartial idea-edit-partial [{:keys [id] :as idea}]
  [:div.doc-section
   (form-to [:post (str "/ideas/admin/ideas/edit/" id)]
            (idea-fields idea)
            (submit-button {:class "submit"} "submit"))])

(defpartial idea-edit-page [idea]
  (common/admin-layout
    (idea-header-partial idea)
    (idea-edit-partial idea)))

(defpage "/ideas/admin/ideas/edit/:id" {:keys [id] :as idea}
  (if-let [idea (ideas/get-idea-by-id (read-string id))]
    (idea-edit-page idea)))

(defpage [:post "/ideas/admin/ideas/edit/:id"] {:keys [id] :as idea}
  (if (ideas/update-idea (assoc idea :id (read-string id) :handle (ideas/generate-handle (:title idea))))
    (resp/redirect "/ideas/admin/ideas/")
    (render "/ideas/admin/ideas/edit/:id" idea)))

;; Add Idea
(defpartial idea-add-partial [{:keys [id] :as idea}]
  [:div.doc-section
   (form-to [:post (str "/ideas/admin/ideas/add")]
            (idea-fields idea)
            (submit-button {:class "submit"} "submit"))])

(defpartial idea-add-page [idea]
  (common/admin-layout
    (idea-header-partial idea)
    (idea-add-partial idea)))

(defpage "/ideas/admin/ideas/add" {:keys [id] :as idea}
  (idea-add-page idea))

(defpage [:post "/ideas/admin/ideas/add"] {:keys [id] :as idea}
  (println idea)
  (if (ideas/add-idea (assoc idea :handle (ideas/generate-handle (:title idea)) :username (users/get-username)))
    (resp/redirect "/ideas/admin/ideas/")
    (render "/ideas/admin/ideas/add" idea)))


;; Remove Idea
(defpage "/ideas/admin/ideas/remove/:id" {:keys [id] :as idea}
  (ideas/remove-idea (read-string id))
  (resp/redirect "/ideas/admin/ideas/"))


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
     [:h2.align-left (link-to (str "/ideas/admin/ideas/") "Admin") heading-sep
      (link-to (str "/ideas/admin/users/") "Users")]
     (link-to {:class "align-right"} (str "/ideas/admin/users/add") "Add user")
     [:div.clear-align]]
    (users-page (users/get-all-users))))

;; View User
(defpartial user-view-partial [{:keys [username name email] :as user}]
  [:div
   [:table#user-details
    [:tr [:td {:width "100"} "Name"] [:td name]]
    [:tr [:td {:width "100"} "Email"] [:td email]]]])

(defpartial user-header-partial [user]
  [:header
     [:h2.align-left (link-to (str "/ideas/admin/ideas/") "Admin") heading-sep
      (link-to (str "/ideas/admin/users/") "Users") heading-sep
      (link-to (str "/ideas/admin/users/view/" (:username user)) (:username user))]
     [:div.clear-align]])

(defpartial user-view-page [user]
  (common/admin-layout
    (user-header-partial user)
    (user-view-partial user)))

(defpage "/ideas/admin/users/view/:username" {:keys [username]}
  (if-let [user (users/get-user-by-username username)]
    (do (println user)
    (user-view-page user))))

;; Edit User
(defpartial user-fields [{:keys [name email password] :as user}]
  (vali/on-error :name error-text)
            (text-field {:placeholder "Name"} :name name)
            (vali/on-error :email error-text)
            (text-field {:placeholder "Email"} :email email)
            (vali/on-error :password error-text)
            (password-field {:placeholder "Password"} :password password))

(defpartial user-edit-partial [{:keys [username] :as user}]
  [:div.doc-section
   (form-to [:post (str "/ideas/admin/users/edit/" username)]
            (user-fields user)
            (submit-button {:class "submit"} "submit"))])

(defpartial user-edit-page [user]
  (common/admin-layout
    (user-header-partial user)
    (user-edit-partial user)))

(defpage "/ideas/admin/users/edit/:username" {:keys [username]}
  (if-let [user (users/get-user-by-username username)]
    (user-edit-page user)))

(defpage [:post "/ideas/admin/users/edit/:username"] {:keys [username] :as user}
  (if (users/update-user user)
    (resp/redirect "/ideas/admin/users/")
    (render "/ideas/admin/users/edit/:username" user)))

;; Add User
(defpartial user-add-partial [{:keys [username] :as user}]
  [:div.doc-section
   (form-to [:post (str "/ideas/admin/users/add")]
            (text-field {:placeholder "Username"} :username username)
            (vali/on-error :username error-text)
            (user-fields user)
            (submit-button {:class "submit"} "submit"))])

(defpartial user-add-page [user]
  (common/admin-layout
    (user-header-partial user)
    (user-add-partial user)))

(defpage "/ideas/admin/users/add" {:keys [username] :as user}
  (user-add-page user))

(defpage [:post "/ideas/admin/users/add"] {:keys [username] :as user}
  (if (users/add-user user)
    (resp/redirect "/ideas/admin/users/")
    (render "/ideas/admin/users/add" user)))

;; Remove User
(defpage "/ideas/admin/users/remove/:username" {:keys [username] :as user}
  (users/remove-user user)
  (resp/redirect "/ideas/admin/users/"))
