(ns thegame.world
  (:require [thegame.geometry :as geom]))

(defn default
  "create a default world."
  []
  [(geom/sphere [0 0 3]
                1)])

