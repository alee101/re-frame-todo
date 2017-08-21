(ns re-frame-todo.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :showing
 (fn [db]
   (:showing db)))

(re-frame/reg-sub
 :todos
 (fn [db]
   (:todos db)))

(re-frame/reg-sub
 :visible-todos
 :<- [:showing]
 :<- [:todos]
 (fn [[showing todos] _]
   (let [filter-fn (case showing
                     :all identity
                     :active (complement :done)
                     :completed :done)]
     (filter filter-fn (vals todos)))))

(re-frame/reg-sub
 :active-todo-count
 :<- [:todos]
 (fn [todos _]
   (->> (vals todos)
        (filter (complement :done))
        count)))
