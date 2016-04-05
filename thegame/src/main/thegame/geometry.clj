(ns thegame.geometry
  (:require [thegame.util :as util])
  (:import [java.awt Color]))

(defn within?
  "test if a vector is equal to another +/- an error margin for each component."
  [v1 v2 m]
  (reduce #(and %1 %2)
          (map #(util/within? (nth v1 %)
                  (nth v2 %)
                  m)
               [0 1 2])))

(defn inv
  "invert a vector."
  [v]
  (vec (map #(- %) v)))

(defn add
  "add two or more vectors."
  ([v1 v2]
   (vec (map + v1 v2)))
  ([v1 v2 & vs]
   (vec (apply map + (conj vs v1 v2)))))

(defn sub
  "subtract a vector from another."
  [v1 v2]
  (vec (map - v1 v2)))

(defn length
  "get the length of a vector."
  [v]
  (Math/sqrt (reduce +
                     (map #(Math/pow % 2)
                          v))))

(defn scale
  "scale a vector."
  [v s]
  (vec (map #(* s %)
            v)))

(defn norm
  "normalize a vector to length 1 keeping its direction."
  [v]
  (scale v (/ 1 (length v))))

(defn dot
  "dot product of two vectors."
  [v1 v2]
  (reduce + (map * v1 v2)))

(defn cross
  "cross product of two vectors."
  [v1 v2]
  [(- (* (get v1 1)
         (get v2 2))
      (* (get v1 2)
         (get v2 1)))
   (- (* (get v1 2)
         (get v2 0))
      (* (get v1 0)
         (get v2 2)))
   (- (* (get v1 0)
         (get v2 1))
      (* (get v1 1)
         (get v2 0)))])

(defn sphere
  "returns a new sphere as hash containing the origin vector, the radius and a color. default color is red."
  ( [origin radius color]
   {:origin origin
    :radius radius
    :color color})
  ([origin radius]
   (sphere origin
           radius
           Color/red)))
