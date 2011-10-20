(ns com.topmonkeys.ideas.views.contact
  (:require [com.topmonkeys.ideas.views.common :as common])
  (:use noir.core))

; Contact Page
(defpage "/ideas/contact" []
  (common/home-layout
    [:div
     [:header
      [:h2 "Contact Us"]]
      [:p "Aenean commodo accumsan diam, porttitor pharetra mi adipiscing quis. Phasellus mauris ante, elementum et lobortis id, consectetur ut quam. Cras eu leo a dolor dictum adipiscing. Nulla purus massa, laoreet sed pulvinar et, euismod sit amet lacus. In at accumsan dolor. Nullam ultrices nulla et urna mollis sed euismod elit egestas. Ut nec blandit nunc. Aliquam porta, diam eget lobortis tristique, lacus nulla consectetur mi, sed ultrices urna dolor et velit. Mauris ullamcorper sodales nibh id lobortis. Quisque tincidunt sagittis lectus sit amet luctus. Fusce malesuada fringilla massa, non interdum tellus venenatis a."]]))
