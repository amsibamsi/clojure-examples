(ns thegame.main
  (:require [thegame.game :as game])
  (:gen-class))

(defn start
  []
  (game/start (game/default)))

(defn -main
  []
  (start)
  (System/exit 0))
