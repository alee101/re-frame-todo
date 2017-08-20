(ns re-frame-todo.db)

(def default-db
  {:todos (sorted-map)
   :showing :all})
