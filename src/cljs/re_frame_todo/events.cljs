(ns re-frame-todo.events
  (:require [re-frame.core :as re-frame]
            [re-frame-todo.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 :add-todo
 (fn [db [_ new-todo-title]]
   (let [next-id (-> db :todos keys last inc)
         new-todo {:id next-id :title new-todo-title :done false}]
     (assoc-in db [:todos next-id] new-todo))))

(re-frame/reg-event-db
 :delete-todo
 (fn [db [_ id]]
   (update-in db [:todos] dissoc id)))

(re-frame/reg-event-db
 :save
 (fn [db [_ id title]]
   (assoc-in db [:todos id :title] title)))

(re-frame/reg-event-db
 :toggle-done
 (fn [db [_ id]]
   (update-in db [:todos id :done] not)))

(re-frame/reg-event-db
 :set-filter
 (fn [db [_ filter-key]]
   (assoc db :showing filter-key)))

(re-frame/reg-event-db
 :clear-completed
 (fn [db _]
   (let [todos (:todos db)]
     (->> (vals todos)
          (filter :done)
          (map :id)
          (reduce dissoc todos)
          (assoc db :todos)))))
