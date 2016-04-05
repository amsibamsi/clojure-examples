(ns thegame.util)

(defn within?
  "test if a value is within the range of another value +/- a margin."
  [x y m]
  (and
    (>= x (- y m))
    (<= x (+ y m))))
