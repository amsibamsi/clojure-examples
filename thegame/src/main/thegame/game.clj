(ns thegame.game
  (:require [thegame.world :as world]
            [thegame.gui :as gui]
            [thegame.input :as in]
            [thegame.raytracing :as raytr]
            [thegame.time :as time]
            [thegame.util :as util]
            [thegame.physics :as phy]
            [taoensso.timbre :as tim]))
(def sps
  "steps per second."
  30)

(defn process-key
  "process an key from the input."
  [game k]
  (let [a (in/action-for-key (game :input)

                             k)]
    ; would use defs from ns input, but must use compile time symbols
    (case a
      :quit (assoc game
                   :quit
                   true)
      :forward (phy/move-forward game
                                 0.05)
      :back (phy/move-backward game
                               0.05)
      :right (phy/move-right game
                             0.05)
      :left (phy/move-left game
                           0.05)
      game)))

(defn process-input
  "process input and change the game. returns a new game."
  [game]
  (let [ks (in/keys-down (game :input)
                        (game :time))]
    (reduce process-key
            game
            ks)))

(defn wait-time
  "return time to wait for next step in ms."
  [last-step now]
  (max 0
       (- (* (/ 1
                sps)
             1000000000)
          (- now
             last-step))))

(defn step-wait
  "wait for the next step."
  [game]
  (let [then (game :time)
        now (time/now (game :time0))
        wait (wait-time then
                        now)]
    (if (> wait 0)
      (Thread/sleep (/ wait 1000000)))))

(defn set-time
  "set the time in the game."
  [game]
  (assoc game
         :time
         (time/now (game :time0))))

(defn step
  "take a game, simulate it and return a new game with the next state."
  [game]
  (step-wait game)
  (-> game
      (set-time)
      (process-input)
      (gui/render)))

(defn quit?
  "should the game quit?"
  [game]
  (or (game :quit)
      (gui/closed? (game :gui))))

(defn quit
  "quit the game."
  [game]
  (tim/info "quitting")
  (gui/quit (game :gui))
  game)

(defn clear-input
  "clear the input of a game."
  [game]
  (assoc game
         :input
         (in/clear-events
           (game :input))))

(defn init
  "init the game."
  [game]
  (gui/register-input (game :gui)
                      (game :input))
  game)

(defn prepare
  "prepare a game for running."
  [game]
  (gui/show-frame (game :gui))
  (-> game
      (assoc :quit
             false)
      (clear-input)))

(defn gameloop
  "run the game loop. does not return until the game is quit."
  [game]
  (let [g (step game)]
    (if (quit? g)
      (quit g)
      (recur g))))

(defn run
  "run the game."
  [game]
  (-> game
      (prepare)
      (gameloop)))

(defn start
  "start a new game."
  [game]
  (-> game
      (init)
      (run)))

(defn create
  "create a new game."
  [gui view world]
  (let [t0 (time/mtime)]
    {:gui gui
     :input (in/default t0)
     :view view
     :world world
     :time0 t0
     :time 0
     :quit false}))

(defn default
  "create a new default game."
  []
  (create (gui/default)
          (raytr/default-view)
          (world/default)))
