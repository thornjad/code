(ns chess.engine-test
  (:require [clojure.test :refer :all]
            [chess.engine :refer :all]))

(deftest test-knight-moves
  (is (= (knight-moves 0 0) #{[1 2] [2 1]})
      "knight-moves should return correct moves from (0,0)")
  (is (= (knight-moves 4 4) #{[5 6] [6 5] [2 6] [6 2] [3 5] [5 3] [2 2] [3 3]})
      "knight-moves should return correct moves from (4,4)"))

(deftest test-generate-moves
  (let [board {:pieces [[{:type :king :color :white} {:type :rook :color :white}]
                        [{:type :pawn :color :white} {:type :pawn :color :black}]
                        [] [] [] []
                        [{:type :pawn :color :black} {:type :pawn :color :white}]
                        [{:type :rook :color :black} {:type :king :color :black}]]
               :turn :black}]
    (is (= (generate-moves board) #{[[0 1] [0 0]] [[1 1] [1 0]] [[2 1] [2 0]] [[3 1] [3 0]]
                                    [[5 1] [5 0]] [[6 1] [6 0]]})
        "generate-moves should return correct moves for black's turn")))

(deftest test-evaluate
  (let [board {:pieces [[{:type :king :color :white} {:type :rook :color :white}]
                        [{:type :pawn :color :white} {:type :pawn :color :black}]
                        [] [] [] []
                        [{:type :pawn :color :black} {:type :pawn :color :white}]
                        [{:type :rook :color :black} {:type :king :color :black}]]
               :turn :white}]
    (is (= (evaluate board) 2)
        "evaluate should return correct score for the board")))

(deftest test-best-move
  (let [board {:pieces [[{:type :king :color :white} {} {} {} {} {} {} {}]
                        [] [] [] [] [] [] []
                        [] [] [] [] [] [] []
                        [] [] [] [] [] [] []
                        [{:type :pawn :color :black} {} {} {} {} {} {} {}]
                        [{:type :king :color :black} {} {} {} {} {} {} {}]]
               :turn :white}]
    (is (= (best-move board 1) [[0 1] [0 2]])
        "best-move should return correct move for the board")))
