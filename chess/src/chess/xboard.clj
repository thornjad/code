(ns chess.xboard)

(defn parse-xboard-command [command]
  (cond
    (= command "xboard") (constantly nil)
    (= command "new") new-game
    (= command "force") force-mode
    (= command "go") go-mode
    (= command "quit") quit-game
    :else (constantly nil)))

(defn send-xboard [response]
  (println response))

(defn parse-move [move-string]
  (let [from (str/slice move-string 0 2)
        to (str/slice move-string 2 4)]
    {:from from :to to}))

(defn make-move [board move]
  (let [from (get-piece board (:from move))
        to (get-piece board (:to move))
        new-board (assoc board (:to move) from)
        new-board (assoc new-board (:from move) nil)]
    new-board))

(defn search-best-move [board]
  (let [moves (generate-moves board)
        scored-moves (map #(vector % (evaluate-board (make-move board %))) moves)
        best-move (apply max-key second scored-moves)]
    (first best-move)))

(defn xboard-loop [board]
  (while true
    (let [command (read-line)]
      (if-let [handler (parse-xboard-command command)]
        (handler board)
        (let [move (parse-move command)
              new-board (make-move board move)]
          (send-xboard (format "move %s%s" (:from move) (:to move)))
          (search-best-move new-board))))))
