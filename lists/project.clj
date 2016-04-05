(defproject lists "0.0.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [prismatic/dommy "0.1.1"]]
  :plugins [[lein-cljsbuild "0.3.0"]]
  :cljsbuild {
              :builds [{
                        :source-paths ["src"]
                        :compiler {
                                   :output-to "js/lists.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})
