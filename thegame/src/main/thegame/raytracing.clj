(ns thegame.raytracing
  (:require [thegame.geometry :as geom]))

(def default-eye
  "default position of the eye."
  [0 0 0])

(def default-look
  "default direction of view."
  [0 0 1])

(def default-up
  "default upwards direction."
  [0 1 0])

(def default-fov
  "default field of view."
  (/ Math/PI 2))

(def default-near
  "default near distance for view plane."
  1.0)

(defn view
  "returns a new view represented by a hash map. eye: position of the eye. look: unit vector pointing into viewing direction. up: unit vector pointing upwards, perpendicular to look. fov: field of view in radians in both dimensions. near: distance to the viewing plane."
  [eye look up fov near]
  {:eye eye
   :look (geom/norm look)
   :up (geom/norm (geom/sub up
                            (geom/scale look
                                        (geom/dot up
                                                  look))))
   :fov fov
   :near near})

(defn default-view
  "return a default view. using default values for all view arguments."
  []
  (view default-eye
        default-look
        default-up
        default-fov
        default-near))

(defn view-plane
  "return the viewing plane for a perspective projection. if the width/height aspect ratio aratio is not 1 then the corresponding side will be shrinked in size and the field of view will be reduced. the default aspect ratio is 1 if not specified otherwise. returns a rectangle as hash with the 4 vectors :top-left, :top-right, :bottom-left and :bottom-right."
  ([view aratio]
   (let [e (:eye view)
         l (:look view)
         u (:up view)
         r (geom/cross l u)
         n (:near view)
         a (/ (:fov view) 2)
         c (geom/add e (geom/scale l n))
         s (* (Math/tan a) n)
         w (* (min aratio 1) s)
         h (* (min (/ 1 aratio) 1) s)]
     {:top-left (geom/add c (geom/scale u h) (geom/scale r (- w)))
      :top-right (geom/add c (geom/scale u h) (geom/scale r w))
      :bottom-left (geom/add c (geom/scale u (- h)) (geom/scale r (- w)))
      :bottom-right (geom/add c (geom/scale u (- h)) (geom/scale r w))}))
  ([view]
   (view-plane view
               1)))

(defn ray0
  "first ray of image. vector from the eye to the upper left corner of the viewing plane."
  [plane]
  (plane :top-left))

(defn ray-dx
  "vector pointing from one point on the viewing plane to the next one on the right."
  [plane width]
  (geom/scale (geom/sub (plane :top-right)
                        (plane :top-left))
              (/ 1
                 (dec width))))

(defn ray-dy
  "vector pointing from one point on the viewing plane to the next one down."
  [plane height]
  (geom/scale (geom/sub (plane :bottom-left)
                        (plane :top-left))
              (/ 1
                 (dec height))))

(defn sphere-distance
  "return distance from eye to the hit of the ray with the sphere hull. return nan if there is no hit. solve the quadratic equation (e + t*p - s)^2 = r^2 for t, where e=eye, p=a point on the ray, s=sphere center, r=sphere radius, t=unknown. if there is a solution for t then the ray hits."
  [eye ray sphere]
  (let [s (geom/sub (:origin sphere)
                    eye)
        r (:radius sphere)
        a (geom/dot ray
                    ray)
        b (- (* 2
                (geom/dot s
                          ray)))
        c (- (geom/dot s
                       s)
             (Math/pow r 2))
        rt (Math/sqrt (- (Math/pow b 2)
                         (* 4
                            a
                            c)))
        t1 (/ (+ (- b)
                 rt)
              (* 2
                 a))
        t2 (/ (- (- b)
                 rt)
              (* 2
                 a))
        t (if (or (< t1 0)
                  (< t2 0))
            (max t1 t2)
            (min t1 t2))]
    (if (< t 0)
      Double/NaN
      (geom/length (geom/scale ray
                               t)))))

(defn first-hit
  "return the first object that a ray hits. return nil if there is no hit."
  [eye ray objects]
  (loop [spheres objects
         closest nil
         distance nil]
    (if (empty? spheres)
      closest
      (let [current (first spheres)
            current-distance (sphere-distance eye
                                              ray
                                              current)]
        (if (Double/isNaN current-distance)
          (recur (rest spheres)
                 closest
                 distance)
          (if (nil? distance)
            (recur (rest spheres)
                   current
                   current-distance)
            (if (< current-distance
                   distance)
              (recur (rest spheres)
                     current
                     current-distance)
              (recur (rest spheres)
                     closest
                     distance))))))))

(defn shade
  "determine color of ray on a sphere."
  [ray sphere]
  (sphere :color))

(defn ray-color
  "return the color of a ray."
  [eye ray objects]
  (let [h (first-hit eye
                     ray
                     objects)]
    (if (nil? h)
      nil
      (shade ray h))))

(defn ray-range
  "return a range of rays by adding ray-delta n times to ray."
  [ray ray-delta n]
  (take n
        (iterate #(geom/add % ray-delta)
                 ray)))

(defn rays
  "get a list of rows of rays for view at resolution width/height."
  [view plane width height]
  (let [r0 (ray0 plane)
        rx (ray-dx plane width)
        ry (ray-dy plane height)]
    (map #(ray-range % rx width)
         (ray-range r0 ry height))))

(defn raytrace
  "raytrace the view with a resolution defined by width/height. return a list of rows with a color for each ray."
  [view world width height]
  (let [p (view-plane view
                      (/ width
                         height))
        r (rays view
                p
                width
                height)
        e (:eye view)]
    (map (fn [row]
           (map (fn [ray]
                  (ray-color e
                             ray
                             world))
                row))
         r)))
