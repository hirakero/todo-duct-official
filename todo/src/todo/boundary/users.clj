(ns todo.boundary.users
  (:require duct.database.sql
           [clojure.java.jdbc :as jdbc]
           [buddy.hashers :as hashers]))

(defprotocol Users
  (create-user [db email password]))

(extend-protocol Users
  duct.database.sql.Boundary
  (create-user [{db :spec} email password]
    (let [pw-hash (hashers/derive password)
          results (jdbc/insert! db :users {:email email, :password pw-hash})]
      (-> results ffirst val))))
