(ns thegame.time)

(defn mtime
  "returns value of jvm internal clock in milliseconds. gives stable time within a running jvm that is independent from the system time, but the starting value is arbitrary."
  []
  (long (/ (System/nanoTime)
           1000000)))

(defn now
  "returns current time in ms relative to time0. time0 is usually the value of mtime taken at some earlier point."
  [time0]
  (- (mtime)
     time0))
