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
                :checked done
                :on-change #(re-frame/dispatch [:toggle-done id])}]
       (if @editing
         [todo-input {:title title
                      :on-save #(do (re-frame/dispatch [:save id %])
                                    (reset! editing false))}]
         [:label {:style {:text-decoration (str (if done "line-through" "none"))}
                  :on-double-click #(reset! editing true)} title])
       [:button {:on-click #(re-frame/dispatch [:delete-todo id])} "Delete"]])))

(defn filter-control []
  (let [selected-filter (reagent/atom :all)
        build-filter-input (fn [[key label]]
                             [[:input {:type "radio"
                                       :name "filter"
                                       :value label
                                       :checked (= @selected-filter key)
                                       :on-change #(do (reset! selected-filter key)
                                                       (re-frame/dispatch [:set-filter key]))}]
                              [:label label]])]
    (fn []
      `[:div
        "Show: "
        ~@(mapcat build-filter-input [[:all "All"] [:active "Active"] [:completed "Completed"]])])))

(defn footer-control []
  (let [active-todo-count @(re-frame/subscribe [:active-todo-count])]
    [:div
     (str active-todo-count (if (= 1 active-todo-count) " item" " items") " left")
     [filter-control]
     [:button {:on-click #(re-frame/dispatch [:clear-completed])} "Clear completed"]]))

(defn todo-list []
  [:div
   (map (fn [todo] ^{:key (:id todo)} [todo-item todo]) @(re-frame/subscribe [:visible-todos]))])

(defn todo-app []
  [:div
   [todo-list]
   [todo-input {:id "new-todo"
                :on-save #(re-frame/dispatch [:add-todo %])}]
   [footer-control]])
