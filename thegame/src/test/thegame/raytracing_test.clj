(ns thegame.raytracing-test
  (:require [clojure.test :refer :all]
            [thegame.geometry :as g]
            [thegame.raytracing :as r]
            [thegame.util :as u])
  (:import [java.awt Color]))

(defn <>
  "test if a value is equal to another within a fixed error margin."
  [x y]
  (u/within? x
             y
             1e-6))

(defn <v>
  "test if a vector is equal to another within a fixed error margin."
  [v w]
  (g/within? v
             w
             1e-6))

(defn test-view
  "predefined view to use in tests."
  []
  (r/view [0.0 0.0 0.0]
          [0.0 0.0 1.0]
          [0.0 1.0 0.0]
          (/ Math/PI 2)
          1))

(deftest view
  []
  (testing "creating a new view"
    (let [v (test-view)]
      (is (= (:eye v) [0.0 0.0 0.0]) "eye")
      (is (= (:look v) [0.0 0.0 1.0]) "look")
      (is (= (:up v) [0.0 1.0 0.0]) "up")
      (is (= (:fov v) (/ Math/PI 2)) "fov")
      (is (= (:near v) 1) "near plane"))))

(deftest view-plane
  []
  (testing "getting the viewing plane"
    (let [v (test-view)
          p (r/view-plane v)]
      (is (<v> (:top-left p) [1.0 1.0 1.0]) "top left corner")
      (is (<v> (:top-right p) [-1.0 1.0 1.0]) "top right corner")
      (is (<v> (:bottom-left p) [1.0 -1.0 1.0]) "bottom left corner")
      (is (<v> (:bottom-right p) [-1.0 -1.0 1.0]) "bottom right corner"))))

(deftest first-ray
  []
  (testing "getting first ray"
    (let [v (test-view)
          p (r/view-plane v)
          r (r/ray0 p)]
      (is (u/within? (nth r 0) 1 0.1) "x")
      (is (u/within? (nth r 1) 1 0.1) "y")
      (is (u/within? (nth r 2) 1 0.1) "z"))))

(deftest ray-deltas
  []
  (testing "deltas between rays"
    (let [v (test-view)
          p (r/view-plane v)
          dx (r/ray-dx p 3)
          dy (r/ray-dy p 3)]
      (is (<v> dx [-1 0 0]) "horizontal")
      (is (<v> dy [0 -1 0]) "vertical"))))

(deftest sphere-distance
  []
  (testing "distance to a sphere hit"
    (let [e [0 0 0]
          s (g/sphere [0 0 2] 1)
          r [0 0 1]]
      (is (<> (r/sphere-distance e
                                 r
                                 s)
              1)))))

(deftest first-hit
  []
  (testing "first hit object of a ray"
    (let [e [0 0 0]
          s1 (g/sphere [0 0 3] 1)
          s2 (g/sphere [0 0 2] 1)
          r [0 0 1]]
      (is (= (r/first-hit e
                          r
                          [s1 s2])
             s2)))))

(deftest ray-range
  []
  (testing "range of rays"
    (is (= (r/ray-range [1 0 0] [0 1 0] 3)
           '([1 0 0] [1 1 0] [1 2 0])))))

(deftest rays
  []
  (testing "getting all rays"
    (let [v (test-view)
          p (r/view-plane v)
          r (r/rays v p 2 2)
          row1 (nth r 0)
          row2 (nth r 1)]
      (is (<v> (nth row1 0)
               [1 1 1]))
      (is (<v> (nth row1 1)
               [-1 1 1]))
      (is (<v> (nth row2 0)
               [1 -1 1]))
      (is (<v> (nth row2 1)
               [-1 -1 1])))))

(deftest raytrace
  []
  (testing "raytracing"
    (let [v (test-view)
          g Color/green
          w [(g/sphere [0 0 2]
                       1
                       g)]
          r (r/raytrace v w 3 3)]
      (is (= r
             (list (list nil nil nil)
                   (list nil g nil)
                   (list nil nil nil)))))))
