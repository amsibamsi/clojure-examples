(ns thegame.util-test
  (:require [clojure.test :refer :all]
            [thegame.util :as u]))

(deftest within
  []
  (is (u/within? 4 4 0) "equal")
  (is (u/within? 1.5 2 1) "a bit larger")
  (is (u/within? 6 7 1.2) "a bit smaller")
  (is (u/within? 3 5 2) "minimum")
  (is (u/within? 1.1 1 0.1) "maximum")
  (is (not (u/within? 5 4 0.9)) "too large")
  (is (not (u/within? 0.9 1 0.05)) "too small"))
