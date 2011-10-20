(ns com.topmonkeys.ideas.views.idea
  (:require [com.topmonkeys.ideas.views.common :as common]
            [com.topmonkeys.ideas.models.idea :as ideas]
            [com.topmonkeys.ideas.models.user :as users]
            [noir.response :as resp]
            [noir.validation :as vali]
            [clojure.string :as string]
            [clj-time.core :as date]
            [clj-time.coerce :as date-util])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
        hiccup.form-helpers))

(defpartial error-text [errors]
            [:p (string/join "<br/>" errors)])

(pre-route "/ideas/idea/edit*" {}
           (when-not (users/is-admin?)
             (resp/redirect "/ideas/login")))

; Home Page
(defpage "/" []
  (resp/redirect "/ideas/"))

(defpartial comment-partial [{:keys [comment username]}]
  ;[:div {:class "row clearfix"}
  ; [:div {:class "twelve columns omega"}
   [:div.comments 
    [:p comment]
    [:p username]])

(defpartial new-comment-partial []
  (vali/on-error :comment error-text)
  (text-area {:placeholder "comment"} :comment))

(defpartial idea-partial [show-comments {:keys [title username handle description created-at comments] :as idea}]
  (when idea
    [:div
     [:header
      [:h2 (link-to (str "/ideas/idea/view/" handle) title)]
      [:h6 "Added by " (link-to (str "/ideas/user/" username) username) " " (ideas/get-elapsed-time (date-util/from-long created-at))]]
     [:div.doc-section description]
     (if show-comments
       [:div.doc-section
        (when  (users/is-admin?)
           (form-to [:post (str "/ideas/idea/view/" handle "/add-comment")]
                    (new-comment-partial)
                    (submit-button {:class "submit"} "add comment")))
        [:hr.small]
        [:h4 (str (count comments) " Comments")] 
        (map comment-partial comments)]
       [:hr.small])]))

(defpage [:post "/ideas/idea/view/:handle/add-comment"] {:keys [handle comment]}
  (when (ideas/add-comment {:handle handle :comment comment :username (users/get-username)})
    (resp/redirect (str "/ideas/idea/view/" handle))))

(defpartial ideas-page [show-comments ideas-coll]
  (map (partial idea-partial show-comments) ideas-coll))

(defpage "/ideas/" []
  (common/home-layout
    (ideas-page false (ideas/get-all-ideas))))

; Idea View Page

(defpartial idea-view-page [idea]
  (common/home-layout
    (ideas-page true idea)))

(defpage "/ideas/idea/view/:handle" {:keys [handle]}
  (if-let [idea (ideas/get-idea-by-handle handle)]
    (idea-view-page [idea])))
