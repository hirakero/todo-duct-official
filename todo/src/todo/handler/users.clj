(ns todo.handler.users
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [buddy.sign.jwt :as jwt]
            [todo.boundary.users :as users]))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{[_ email password] :ataraxy/result}]
    (let [id (users/create-user db email password)]
      [::response/created (str "/users/" id)])))

(defmethod ig/init-key ::signin [_ {:keys [db jwt-secret]}]
  (fn [{[_ email password] :ataraxy/result}]
    (letfn [(with-token [user]
                (->> (jwt/sign (select-keys user [:email]) jwt-secret)
                     (assoc user :token)) user)]
        (if-let [user (users/authemticate-user db email password)]
          [::response/ok {:user (with-token user)}]
          [::response/forbidden "Not authorized"]))))
