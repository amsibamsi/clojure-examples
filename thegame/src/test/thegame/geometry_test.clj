(ns thegame.geometry-test
  (:require [clojure.test :refer :all]
            [thegame.geometry :as g]
            [thegame.util :as u])
  (:import [java.awt Color]))

(defn <>
  "test if a value is equal to another within a fixed error margin."
  [x y]
  (u/within? x
             y
             1e-6))

(deftest within?
  []
  (testing "within? comparison of vectors"
    (is (g/within? [1 1 1]
                   [1 1.5 0.5]
                   0.5)
        "within")
    (is (not (g/within? [1 1 1]
                        [1.2 1 1]
                        0.1))
        "not within")))

(deftest inv
  []
  (testing "inverting a vector"
    (is (= (g/inv [1 2 3])
           [-1 -2 -3]))))

(deftest add
  []
  (testing "adding vectors"
    (is (= (g/add [1 2 4]
                  [1 2 4])
           [2 4 8])
        "add 2 vectors")
    (is (= (g/add [1 2 3]
                  [1 2 3]
                  [1 2 3])
           [3 6 9])
        "add 3 vectors")))

(deftest sub
  []
  (testing "subtracting two vectors"
    (is (= (g/sub [4 5 6]
                  [3 2 1])
           [1 3 5]))))

(deftest length
  []
  (testing "length of a vector"
    (is (<> (g/length [0 3 4])
            5.0))))

(deftest scale
  []
  (testing "scaling a vector."
    (is (= (g/scale [1 2 3]
                    2)
           [2 4 6]))))

(deftest norm
  []
  (testing "normalizing a vector")
  (let [v (g/norm [0 3 4])]
    (is (= (nth v
                0)
           0.0)
        "x")
    (is (<> (nth v
                 1)
            (/ 3.0
               5.0))
        "y")
    (is (<> (nth v
                 2)
            (/ 4.0
               5.0))
        "z")))

(deftest dot
  []
  (testing "dot product of two vectors"
    (is (= (g/dot [1 2 3]
                  [4 4 4])
           (* 6 4)))))

(deftest cross
  []
  (testing "cross product of two vectors")
  (is (= (g/cross [1 0 0]
                  [0 1 0])
         [0 0 1])))

(deftest new-sphere
  []
  (testing "creating a new sphere"
    (let [o [1 1 1]
          r 2
          c Color/blue
          s (g/sphere o
                      r
                      c)]
      (is (= (:origin s)
             o)
          "origin")
      (is (= (:radius s)
             r)
          "radius")
      (is (= (:color s)
             c)
          "color"))))
