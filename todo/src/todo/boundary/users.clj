(ns todo.boundary.users
  (:require duct.database.sql
           [clojure.java.jdbc :as jdbc]
           [buddy.hashers :as hashers]))

(defprotocol Users
  (create-user [db email password])
  (find-user-by-email [db email])
  (authemticate-user [db email password])
  )


(extend-protocol Users
  duct.database.sql.Boundary

  (create-user [{db :spec} email password]
    (let [pw-hash (hashers/derive password)
          results (jdbc/insert! db :users {:email email, :password pw-hash})]
      (-> results ffirst val)))
  
  (find-user-by-email [{db :spec} email]
    (first (jdbc/query db ["select * from users where email=?" email])))
  
  (authemticate-user [{db :spec :as all} email password]    
    (if-let [user (find-user-by-email all email)]
      (if (hashers/check password (:password user))
        (dissoc user :password))))
  )


