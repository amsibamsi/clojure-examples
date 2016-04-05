(ns thegame.physics
  (:require [thegame.geometry :as geom]))

(defn move-speed
  "distance per second."
  []
  1.0)

(defn rotate-speed
  "rotation speed in radians per mouse pixel movement."
  []
  0.01)

(defn move-forward
  "move the view forward by time t."
  [game t]
  (assoc game
         :view
         (assoc (game :view)
                :eye
                (geom/add (:eye (game :view))
                          (geom/scale (:look (game :view))
                                      (* (move-speed) t))))))

(defn move-backward
  "move the view backward by time t."
  [game t]
  (assoc game
         :view
         (assoc (game :view)
                :eye
                (geom/add (:eye (game :view))
                          (geom/scale (:look (game :view))
                                      (* (- (move-speed)) t))))))

(defn move-right
  "move the view to the right by time t."
  [game t]
  (assoc game
         :view
         (assoc (game :view)
                :eye
                (geom/add (:eye (game :view))
                          (geom/scale (geom/cross (:look (game :view))
                                                  (:up (game :view)))
                                      (* (move-speed) t))))))

(defn move-left
  "move the view to the left by time t."
  [game t]
  (assoc game
         :view
         (assoc (game :view)
                :eye
                (geom/add (:eye (game :view))
                          (geom/scale (geom/cross (:up (game :view))
                                                  (:look (game :view)))
                                      (* (move-speed) t))))))
