(ns re-frame-todo.views
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]))

(defn todo-input [{:keys [title on-save]}]
  (let [val (reagent/atom title)
        save #(do (on-save @val)
                  (reset! val ""))]
    (fn [input-props]
      [:input {:type "text"
               :placeholder (when (empty? @val) "What needs to be done?")
               :value @val
               :on-change #(reset! val (-> % .-target .-value))
               :on-key-down #(when (= (.-which %) 13) (save))}])))

(defn todo-item []
  (let [editing (reagent/atom false)]
    (fn [{:keys [id title done]}]
      [:div
       [:input {:type "checkbox"
                :on-change #(re-frame/dispatch [:toggle-done id])}]
       (if @editing
         [todo-input {:title title
                      :on-save #(do
                                  (re-frame/dispatch [:save id %])
                                  (reset! editing false))}]
         [:label {:style {:text-decoration (str (if done "line-through" "none"))}
                  :on-click #(reset! editing true)} title])])))

(defn todo-list []
  (let [todos @(re-frame/subscribe [:todos])]
    [:div
     (map (fn [[id todo]] ^{:key id} [todo-item todo]) todos)
     [todo-input {:id "new-todo"
                  :on-save #(re-frame/dispatch [:add-todo %])}]]))

(defn todo-app []
  [todo-list])
