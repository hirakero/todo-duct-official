(ns todo.handler.users
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [todo.boundary.users :as users]))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{[_ email password] :ataraxy/result}]
    (let [id (users/create-user db email password)]
      [::response/created (str "/usres/" id)])))