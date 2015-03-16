(ns mafia-votecount.models
  (:use [korma.db]
        [korma.core]))

(def db (h2 {:db "./resources/database"}))

(defdb korma-db db)

(declare game host player vote)

(defentity game
  (table :game)
  (has-many player)
  (has-many host)
  (has-many vote))

(defentity host
  (table :host)
  (belongs-to game))

(defentity player
  (table :player)
  (belongs-to game))

(defentity vote
  (table :vote)
  (belongs-to game))

(defn has-game [id]
  (not-empty
    (select game
            (fields :id)
            (where {:id id}))))

(defn add-game [id name url]
  (insert game
          (values {:id id
                   :name name
                   :url (str url)
                   :start_date nil})))

(defn get-games []
  (select game))

(defn get-game-by-id [id]
  (let [games (select game (where {:id id}))]
    (if (seq? games)
      (first games)
      nil)))

(defn get-game-url [id]
  (:url (first
         (select game
                 (fields :url)
                 (where {:id id})))))

(defn add-players [game-id names]
  (let [rows (map (fn [name] {:game game-id :name name}) names)]
   (insert player
        (values rows))))

(defn get-players [game-id]
  (select player
          (fields :id :name)
          (where {:game game-id})))

(defn- host-make-row [game-id name]
  {:game game-id :name name})

(defn add-hosts [game-id names]
  (insert host
          (values
           (map (partial host-make-row game-id) names))))

(defn get-hosts [game-id]
  (into #{}
        (map :name
             (select host
                     (fields :name)
                     (where {:game game-id})))))

(defn add-votes [votes]
  (insert vote
          (values votes)))

(defn get-votes [id]
  (select vote
          (fields :id :index :day :voter :target)
          (where {:game id})))
