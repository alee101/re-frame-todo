(ns re-frame-todo.db)

(def default-db
  {:todos (sorted-map 1 {:id 1 :title "abc" :done false} 2 {:id 2 :title "xyz" :done false})})
