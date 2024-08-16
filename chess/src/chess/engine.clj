(ns chess.engine)

;; Type is the values as keywords for pieces, color is :black or :white.
;; TODO is this good enough? clj doesn't have an enum, so maybe
(defrecord Piece [type color])

;; pieces is a vector of vectors representing the layout of the chessboard. turn is :white or
;; :black. castling is a map of boolean values whether castling is allowed for each side. en-passant
;; is a vector of squares in which en-passant is possible, or nil if not. halfmove is the number of
;; half-moves since the last pawn move or capture. Fullmove is the number of moves played so far
;; TODO ultimately should be a bitboard
;; TODO castling, is that necessary? any other way to figure this out?
;; TODO castling must store kingside and queensite for both
;; TODO en-passant seems unnecessary, can't the engine figure this out for itself using the log. NO,
;; not if initialized from FEN
;; TODO what's the point of halfmove?
;; TODO what's the point of fullmove?
;; TODO also have a PEN log?
(defrecord Board [pieces turn castling en-passant halfmove fullmove])

;; pen is a vector of moves made so far.
;; TODO a function to generate PEN from this (probably just concat)
(defrecord Move-Log [moves])

;; TODO map to vector with color and type, return Piece?
(def piece-types {\p :wp \r :wr \n :wn \b :wb \q :wq \k :wk
                  \P :bp \R :br \N :bn \B :bb \Q :bq \K :bk})

(defn parse-piece [c]
  (piece-types c))

(defn parse-castling [s]
  (cond
    (= s "-") #{}
    (= s "K") #{:wk}
    (= s "Q") #{:wq}
    (= s "k") #{:bk}
    (= s "q") #{:bq}
    (= s "KQ") #{:wk :wq}
    (= s "kq") #{:bk :bq}
    :else (throw (ex-info "Invalid castling string" {:castling s}))))

(defn parse-en-passant [s]
  (if (= s "-")
    nil
    (let [file (first s)
          rank (Integer/parseInt (subs s 1))]
      {:file file :rank rank})))

;; parse-piece is a function which takes a character and returns the corresponding Piece record or
;; nil if the char is an empty square. parse-castling is a function which parses the castling field
;; and returns a map matching the Board castling field. parse-en-passant does the same for
;; en-passant.
(defn fen->board [fen]
  (let [[position turn castling en-passant halfmove fullmove] (clojure.string/split fen #" ")
        pieces (flatten (map #(if (number? (read-string %))
                                (repeat % \space)
                                (clojure.string/split % #"/")))
                        position)
        board (vec (partition 8 (map #(if-let [piece (parse-piece %)]
                                        piece
                                        (Piece. :empty :empty)))
                              (apply str pieces)))]
    (Board. board (keyword turn) (parse-castling castling) (parse-en-passant en-passant) (read-string halfmove) (read-string fullmove))))

(defn apply-move [board move]
  (let [from (:from move)
        to (:to move)
        piece (get-in board from)
        promoted-piece (:promoted-piece move)]
    (assoc-in
     (assoc-in board [(:rank from) (:file from)] nil)
     [(:rank to) (:file to)]
     (if promoted-piece
       (assoc piece :type promoted-piece)
       piece))))

;; TODO return Board
(defn pgn->board [pgn]
  (let [game (clj-pgn/parse-game pgn)
        starting-fen (:fen (clj-pgn/parse-game-metadata game))
        board (fen->board starting-fen)
        moves (clj-pgn/moves game)]
    (reduce apply-move board moves)))

(defn knight-moves [position]
  (let [rank (first position)
        file (second position)]
    (for [dr [-2 -1 1 2]
          df (if (or (= (Math/abs dr) 2) (= (Math/abs dr) 1))
               [-1 1]
               [])]
      (let [new-rank (char (+ (int rank) dr))
            new-file (char (+ (int file) df))]
        (str new-rank new-file)))))

;; Function to determine legal moves for a pawn.
(defn pawn-moves [position]
  (let [rank (first position)
        file (second position)
        fwd (char (+ (int rank) (* -1 (if (= \b rank) 0 1)))) ; Determine direction based on pawn's color
        moves (if (= rank \2)
                [(str (char (+ (int rank) 2)) file) (str fwd file)]
                [(str fwd file)])]
    (filter #(and (< (int \a) (int (second %))) (< (int (second %)) (int \i)))
            moves)))

;; Function to determine legal moves for a knight.
(defn knight-moves [position]
  (let [rank (first position)
        file (second position)]
    (filter #(and (< (int \a) (int (second %))) (< (int (second %)) (int \i))
                  (<= (int \1) (int (first %))) (<= (int (first %)) (int \8)))
            (for [dr [-2 -1 1 2]
                  df (if (or (= (Math/abs dr) 2) (= (Math/abs dr) 1))
                       [-1 1]
                       [])]
              (let [new-rank (char (+ (int rank) dr))
                    new-file (char (+ (int file) df))]
                (str new-rank new-file))))))

;; Function to determine legal moves for a bishop.
(defn bishop-moves [position board]
  (let [rank (first position)
        file (second position)]
    (->> (for [dr [-1 1]
               df [-1 1]]
           (take-while (fn [sq]
                         (and (< (int \a) (int (second sq))) (< (int (second sq)) (int \i))
                              (<= (int \1) (int (first sq))) (<= (int (first sq)) (int \8))))
                       (iterate (fn [[r f]]
                                  [(char (+ (int r) dr)) (char (+ (int f) df))])
                                [rank file])))
         (mapcat identity)
         (filter #(not= position %))
         (take-while (fn [sq]
                       (or (nil? (get board sq))
                           (= (get-in board [sq :color]) (get-in board [position :color])))))
         (map str))))

;; Function to determine legal moves for a rook.
(defn rook-moves [position board]
  (let [rank (first position)
        file (second position)]
    (->> (for [d [[1 0] [-1 0] [0 1] [0 -1]]]
           (take-while (fn [sq]
                         (and (< (int \a) (int (second sq))) (< (int (second sq)) (int \i))
                              (<= (int \1) (int (first sq))) (<= (int (first sq)) (int \8))))
                       (iterate (fn [[r f]]
                                  [(char (+ (int r) (first d))) (char (+ (int f) (second d)))])
                                [rank file])))
         (mapcat identity)
         (filter #(not= position %))
         (take-while (fn [sq]
                       (or (nil? (get board sq))
                           (= (get-in board [sq :color]) (get-in board [position :color])))))
         (map str))))

(defn king-moves [position board]
  (let [rank (first position)
        file (second position)]
    (->> (for [dr [-1 0 1]
               df [-1 0 1]]
           (let [new-rank (char (+ (int rank) dr))
                 new-file (char (+ (int file) df))]
             (str new-rank new-file)))
         (filter #(and (< (int \a) (int (second %))) (< (int (second %)) (int \i))
                       (<= (int \1) (int (first %))) (<= (int (first %)) (int \8))))
         (filter #(or (nil? (get board %))
                      (= (get-in board [% :color]) (get-in board [position :color]))
                      (not= (get-in board [% :piece]) "king")))
         (map str))))

;; Function to determine legal moves for a queen.
(defn queen-moves [position board]
  (concat (bishop-moves position board) (rook-moves position board)))

(defn get-piece [board square]
  "Returns the piece at the given square on the board."
  (get-in board square))

(defn piece-color [piece]
  "Returns the color of the given piece, or nil if the square is empty."
  (when piece
    (if (#{\a \b \c \d \e \f \g \h} piece)
      :white
      :black)))

(defn legal-destination-set [board color square]
  "Returns the set of legal destination squares for the piece at the given square, for the given color."
  (let [piece (get-piece board square)]
    (cond
      (nil? piece) #{} ; empty square
      (= color (piece-color piece))
      ((:moves (piece-type piece)) board square)
      :else #{}))) ; wrong color

(defn create-move [board from to]
  "Creates a new board representing the given move, by moving the piece at the 'from' square to the 'to' square."
  (let [piece (get-piece board from)]
    (assoc-in
     (assoc-in board to piece)
     from nil)))

(defn is-check? [board king-square color]
  (let [opponent-color (opposite-color color)]
    (some #(let [piece (get-piece board %)]
             (and (not (nil? piece))
                  (color-eq? opponent-color (piece-color piece))
                  (contains? (set (legal-destination-set piece % board))
                             king-square)))
          (color-pieces board color))))

(defn find-kings [board]
  (let [pieces (mapcat (partial map vector [:white :black])
                       (:pieces board))
        kings (filter #(= (get-piece board %) :king) pieces)]
    {:white (first kings) :black (second kings)}))

(defn move-puts-king-in-check? [board from to]
  "Returns true if the given move puts the current player's king in check."
  (let [color (piece-color (get-piece board from))
        board-after-move (create-move board from to)
        king-square (first (keys (filter #(= (piece-color (get-piece board-after-move %)) color) (find-kings board-after-move))))] ; find the square of the current player's king
    (is-check? board-after-move king-square color)))

(defn generate-moves [board square]
  (let [piece (get-piece board square)
        piece-color (piece-color piece)
        moves (legal-destination-set piece square board)
        move-fn (fn [to-square]
                  (let [move (create-move board square to-square)]
                    (if (move-puts-king-in-check? move piece-color board)
                      nil
                      move)))]
    (->> moves
         (mapcat move-fn)
         (remove nil?))))

;; TODO memoize
(defn evaluate [board]
  (let [piece-values {:king 0 :queen 9 :rook 5 :bishop 3 :knight 3 :pawn 1}
        value-for-piece (fn [piece]
                          (let [value (get piece-values (:type piece))]
                            (if (= (:color piece) (:turn board))
                              value
                              (- value))))
        score (reduce + (map value-for-piece (flatten (:pieces board))))]
    (if (= (:turn board) :white) score (- score))))

(defn best-move [board depth]
  (let [moves (generate-moves board)
        scored-moves (map #(let [next-board (make-move board %)]
                             {:move % :score (evaluate next-board)})
                          moves)
        sorted-moves (sort-by :score scored-moves)
        best-move (:move (last sorted-moves))]
    best-move))

;; TODO opening book
