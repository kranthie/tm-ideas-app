(defproject ideas "0.1.0-SNAPSHOT"
  :description "A web application to maintain ideas!"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [noir "1.2.0"]
                 [congomongo "0.1.7"]
                 [clj-time "0.3.1"]]
  :dev-dependencies [[lein-eclipse "1.0.0"]]
  :main com.topmonkeys.ideas.server)

