(ns thegame.input
  (:require [thegame.physics :as phy]
            [thegame.time :as time]
            [thegame.util :as util]
            [taoensso.timbre :as tim])
  (:import (java.awt.event KeyListener
                           KeyEvent)))

(def key-up-delay
  "how many nanoseconds to wait before counting a key up status really as key up. this is to prevent flapping of key pressed/released status as observed on x11 with autorepeat on. x11 continuously resends key press/release events when a key is pressed and held down. querying the key status after the last released event but before the next pressed event would lead to a key status of up, although the key is still down. to compensate that we wait some time after a key released event and only count it as key up if no key pressed event was received after some time. usually a key pressed event happens very quickly after the corresponding key released event if both are part of the autorepeat."
  (* 10
     1000000))

(def as-quit
  "action symbol to quit."
  :quit)

(def as-forward
  "action symbol to move forward."
  :forward)

(def as-back
  "action symbol to move backward."
  :back)

(def as-right
  "action symbol to move right."
  :right)

(def as-left
  "action symbol to move left."
  :left)

(def kc-q
  "key code for letter q."
  KeyEvent/VK_Q)

(def kc-w
  "key code for letter f."
  KeyEvent/VK_W)

(def kc-s
  "key code for letter s."
  KeyEvent/VK_S)

(def kc-d
  "key code for letter d."
  KeyEvent/VK_D)

(def kc-a
  "key code for letter a."
  KeyEvent/VK_A)

(def keymap
  "map with the default key bindings."
  {kc-q as-quit
   kc-w as-forward
   kc-s as-back
   kc-d as-right
   kc-a as-left})

(def ks-down
  "key state down."
  :down)

(def ks-up
  "key state up."
  :up)

(defn key-event
  "return a new key event."
  [state time]
  {:state state
   :time time})

(defn action-for-key
  "return the action symbol for the specified key code."
  [input k]
  ((input :keymap) k))

(defn key-listener
  "return a new listener. keys-atom is an atom of a map where key states are registered."
  [keys-atom time0]
  (reify KeyListener
    (keyPressed [this event]
      (let [now (time/now time0)
            code (.getKeyCode event)
            text (KeyEvent/getKeyText code)]
        (swap!
          keys-atom
          assoc
          code
          (key-event ks-down
                     now))
        (tim/trace "key down event"
                   now
                   text)))
    (keyReleased [this event]
      (let [now (time/now time0)
            code (.getKeyCode event)
            text (KeyEvent/getKeyText code)]
        (swap!
          keys-atom
          dissoc
          code
          (key-event ks-up
                    now))
        (tim/trace "key up event"
                   now
                   text)))
    (keyTyped [this event])))

(defn key-event-down?
  "return true if the given key event indicates that the key was down at time t. a key is also counted as down if its state is up but changed less than the time defined in key-up-delay ago."
  [key-event t]
  (let [s (key-event :state)
        ts (key-event :time)]
    (or (= s
           ks-down)
        (and (= s
                ks-up)
             (> key-up-delay
                (- t 
                   ts))))))

(defn keys-down
  "return list of codes for all keys that are were down at time t."
  [input t]
  (keys (filter #(key-event-down? (second %) 
                                  t)
                @(input :keys-atom)))) 

(defn clear-events
  "clear all existing events."
  [input]
  (reset! (input :keys-atom)
          {})
  input)

(defn input
  "create new input."
  [keymap time0]
  (let [keys-atom (atom {})
        key-listener (key-listener keys-atom
                                   time0)]
    {:keymap keymap
     :keys-atom keys-atom
     :key-listener key-listener}))

(defn default
  "create a new default input."
  [time0]
  (input keymap
         time0))
