(ns re-frame-todo.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :visible-todos
 (fn [db]
   (let [showing (:showing db)
         filter-fn (case showing
                     :all identity
                     :active (complement :done)
                     :completed :done)]
     (filter (fn [[id todo]] (filter-fn todo)) (:todos db)))))
