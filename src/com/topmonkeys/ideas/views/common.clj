(ns com.topmonkeys.ideas.views.common
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

; Include files
(def includes {:jquery (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js")
               :fb-js (include-js "/js/fb.js")
               :base (include-css "/css/base.css")
               :layout (include-css "/css/layout.css")
               :skeleton (include-css "/css/skeleton.css")
               :docs (include-css "/css/docs.css")
               :custom (include-css "/css/custom.css")
               :favicon [:link {:rel "shortcut icon" :href "/img/favicon.ico"}]
               :apple-icon-57 [:link {:rel "apple-touch-icon" :sizes "57x57" :href "/img/apple-touch-icon.png"}]
               :apple-icon-72 [:link {:rel "apple-touch-icon" :sizes "72x72" :href "/img/apple-touch-icon-72x72.png"}]
               :apple-icon-114 [:link {:rel "apple-touch-icon" :sizes "114x114" :href "/img/apple-touch-icon-114x114.png"}]})

; Builds html links.
(defpartial build-link [{:keys [url class text]}]
  [:li
   (link-to {:class class} url text)])

;Builds html meta information.
(defpartial build-meta []
  [:meta {:charset "utf-8"}]
  [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
  [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1"}])
  
; Builds html header.
(defpartial build-header [title include-files]
  [:head
   [:meta { :property "fb:app_id" :content "306009122744195" }]
   [:script {:src "http://connect.facebook.net/en_US/all.js#xfbml=1"}]
   [:title title]
   (map #(get includes %) include-files)
   "<script type=\"text/javascript\">
		  var _gaq = _gaq || [];
		  _gaq.push(['_setAccount', 'UA-253140-2']);
		  _gaq.push(['_trackPageview']);
		
		  (function() {
		    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
		    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
		    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
		  })();
</script>"
   ])

; Builds html footer.
(defpartial build-footer []
  [:div.footer
   [:small.align-left "Powered by " 
    [:a.footer {:href "http://clojure.org/" :target "_blank"} "clojure"] " | "
    [:a.footer {:href "http://webnoir.org/" :target "_blank"} "noir"] " | "
    [:a.footer {:href "http://www.mongodb.org/" :target "_blank"} "mongodb"] " | "
    [:a.footer {:href "http://getskeleton.com/" :target "_blank"} "skeleton"]]
   [:small.align-right "Source code at " 
    [:a.footer {:href "https://github.com/kranthie/tm-ideas-app" :target "_blank"} "github"]]])

; Builds html body.
(defpartial build-body [links content]
  [:body
   [:div.container
    [:div {:class "three columns sidebar"}
     [:nav
      [:h3#logo (link-to "/ideas/" "Ideas!")]
      [:ul
       (map build-link links)]]]
    [:div {:class "twelve columns content offset-by-three"}
     content 
     (build-footer)]]])

; Builds the mail layout of the website.
(defpartial layout 
  [links & content]
  (html5
    (build-meta)
    (build-header "Ideas" [:jquery :base :layout :skeleton :docs :custom
                           :favicon :apple-icon-57 :apple-icon-72 :apple-icon-114])
    (build-body links content)))

;; Home Page Layout
(def home-links [{:url "/ideas/admin/ideas/" :text "Admin"}
                 {:url "/ideas/contact" :text "Contact Us"}])

(defpartial home-layout
  [& content]
  (layout home-links content))

;; Admin Page Layout
(def admin-links [{:url "/ideas/admin/ideas/" :text "Ideas"}
                  {:url "/ideas/admin/users/" :text "Users"}
                  {:url "/ideas/logout" :text "Logout"}])

(defpartial admin-layout
  [& content]
  (layout admin-links content))
