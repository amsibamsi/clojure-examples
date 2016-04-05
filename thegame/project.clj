(defproject thegame "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.taoensso/timbre "4.0.1"]
                 [cljfmt "0.1.10"]]
  :source-paths ["src/main"]
  :test-paths ["src/test"]
  :main thegame.main
  :profiles {:uberjar {:aot :all}}
  :repl-options {:init-ns thegame.repl})
