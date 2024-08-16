(ns chess.core)

(defn print-board [board]
  (let [board-rows (partition 8 (:board-state board))]
    (doseq [row board-rows]
      (doseq [square row]
        (print (str square " ")))
      (println))))

(defn play-chess []
  (let [board (init-board)
        player-color (read-string "Enter your color (w/b): ")
        engine-color (if (= player-color "w") "b" "w")
        game-over? false]

    (loop []
      (println (print-board board))
      (if game-over?
        (println (str (if (checkmate? board player-color)
                        "Checkmate. "
                        "Stalemate. ")
                      "Game over."))
        (let [current-color (if (= (:turn board) "w") player-color engine-color)]
          (if (= current-color player-color)
            (let [move-str (read-line "Enter your move: ")
                  move (str->move move-str board current-color)]
              (if move
                (if (apply-move board move)
                  (do
                    (println (str "You played: " move-str))
                    (if (checkmate? board engine-color)
                      (do
                        (println (print-board board))
                        (println "Checkmate. You win!")
                        (recur))
                      (do
                        (println (print-board board))
                        (recur))))
                  (do
                    (println "Invalid move. Try again.")
                    (recur)))
                (do
                  (println "Invalid move. Try again.")
                  (recur))))
            (let [engine-move (best-move board engine-color 3)]
              (if (apply-move board engine-move)
                (do
                  (println (str "Engine plays: " (move->str engine-move)))
                  (if (checkmate? board player-color)
                    (do
                      (println (print-board board))
                      (println "Checkmate. You lose!")
                      (recur :game-over? true))
                    (do
                      (println (print-board board))
                      (recur))))
                (do
                  (println "Engine made an illegal move.")
                  (recur))))))))))
