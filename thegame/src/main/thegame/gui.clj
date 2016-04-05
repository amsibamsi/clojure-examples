(ns thegame.gui
  (:require [thegame.raytracing :as raytr]
            [thegame.input :as in])
  (:import (java.awt.image BufferedImage)
           (java.awt Color Graphics)
           (javax.swing JFrame)))

(def default-width
  "default frame width."
  500)

(def default-height
  "default frame height."
  500)

(def default-ppr
  "default pixels per ray."
  50)

(def default-background
  "default background color."
  Color/black)

(defn frame
  "create a new gui frame."
  [width height]
  (doto (new JFrame)
    (.setTitle "the game")
    (.setSize width height)))

(defn gui
  "create a new gui with a frame, pixels per ray integer and a background color. a key listener is also added to the frame."
  [frame ppr background]
  {:frame frame
   :ppr ppr
   :background background})

(defn default
  "create the default gui."
  []
  (gui (frame default-height
              default-width)
       default-ppr
       default-background))

(defn register-input
  "register the input for events from the gui."
  [gui input]
  (.addKeyListener (gui :frame)
                   (input :key-listener)))

(defn show-frame
  "show the frame of a gui."
  [gui]
  (doto (gui :frame)
    (.show)
    (.createBufferStrategy 2)))

(defn width
  "return frame width."
  [frame]
  (.getWidth frame))

(defn height
  "return frame height."
  [frame]
  (.getHeight frame))

(defn buffer-strategy
  "get buffer strategy from frame."
  [frame]
  (.getBufferStrategy frame))

(defn buffer-restored?
  "was the graphics buffer restored?"
  [bs]
  (.contentsRestored bs))

(defn buffer-lost?
  "was the graphics buffer lost?"
  [bs]
  (.contentsLost bs))

(defn show
  "display the buffer."
  [bs]
  (.show bs))

(defn graphics
  "get graphics from buffer strategy."
  [bs]
  (.getDrawGraphics bs))

(defn graphics-dispose
  [g]
  "dispose graphics."
  (.dispose g))

(defn set-color
  "set the color to draw with."
  [^Graphics graphics ^Color color]
  (.setColor graphics color))

(defn draw-color
  "draw a color using graphics."
  [^Color color ^Graphics graphics x y ppr ^Color background]
  (if (nil? color)
    (set-color graphics background)
    (set-color graphics color))
  (.fillRect graphics
             (* x ppr)
             (* y ppr)
             ppr
             ppr))

(defn draw-row
  "draw a row of raytracing colors with graphics scaling one color onto ppr pixels in width and length."
  [row y ^Graphics graphics ppr ^Color background]
  (dotimes [x (count row)]
    (draw-color (nth row x)
              graphics
              x
              y
              ppr
              background)))

(defn draw-colors
  "draw the raytracing colors with graphics scaling one color onto ppr pixels in width and height."
  [colors ^Graphics graphics ppr ^Color background]
  (dotimes [y (count colors)]
    (draw-row (nth colors y)
              y
              graphics
              ppr
              background)))

(defn draw
  "draw the frame given colors as a set of rows, each on a set of colors, and the ppr value."
  [^JFrame frame colors ppr ^Color background]
  (let [bs (buffer-strategy frame)]
    (loop []
      (loop []
        (let [g (graphics bs)]
          (draw-colors colors g ppr background)
          (graphics-dispose g)
          (if (buffer-restored? bs)
            (recur))))
      (show bs)
      (if (buffer-lost? bs)
        (recur)))))

(defn render
  "render a view on a world as image and display it in the gui."
  [game]
  (let [f (:frame (game :gui))
        p (:ppr (game :gui))
        b (:background (game :gui))
        w (Math/ceil (/ (width f) p))
        h (Math/ceil (/ (height f) p))
        c (raytr/raytrace (game :view)
                      (game :world)
                      w
                      h)]
    (draw f
          c
          p
          b)
    game))

(defn closed?
  "gui frame was closed?"
  [gui]
  (not (.isShowing (gui :frame))))

(defn close
  "close the gui frame."
  [gui]
  (doto (gui :frame)
    (.setVisible false)  
    (.dispose)))

(defn quit
  "quit the gui."
  [gui]
  (close gui))
