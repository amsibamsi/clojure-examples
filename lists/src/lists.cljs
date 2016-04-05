(ns lists.core
  (:use-macros [dommy.macros :only [sel sel1]])
  (:require [dommy.core :as dom]))

(def item-counter (atom 0))

(defn new-item-id []
  (let [c (swap! item-counter inc)]
    (str "item" c)))

(def navigation-bar
  [:div {:class "navbar navbar-fixed-top"}
   [:div {:class "navbar-inner"}
    [:ul {:class "nav"}
     [:li
      [:button {:class "btn" :type "button" :onclick "lists.core.add()"}
       [:i {:class "icon-plus"}]]]]]])

(def list-container
  [:div {:class "container"}
   [:table {:class "table table-striped"}
    [:tbody {:id "list"}]]])

(defn item [id]
  [:tr {:id id}
   [:td
    [:button {:class "btn" :type "button" :onclick (str "lists.core.del('" id "')")}
     [:i {:class "icon-minus"}]]]
   [:td id]])

(defn init []
  (-> (sel1 :body)
      (dom/append! navigation-bar)
      (dom/append! list-container)))

(defn add []
  (dom/append!
    (sel1 :#list)
    (item (new-item-id))))

(defn del [id]
  (dom/remove! (sel1 (keyword (str "#" id)))))
