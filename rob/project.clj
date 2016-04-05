(defproject rob "0.1.0-SNAPSHOT"
  :description "a jabber bot"
  :url "http://github.com/amsibamsi/rob"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.igniterealtime.smack/smack-tcp "4.0.2"]
                 [umgebung/umgebung "1.0.2"]
                 [org.clojure/tools.logging "0.3.0"]
                 [clj-http "1.0.0"]]
  :main rob.core)
