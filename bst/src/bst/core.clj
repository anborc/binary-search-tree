(ns bst.core
  (:gen-class)
  (:require [clojure.java.io :as io])
  )

;;https://stackoverflow.com/questions/26266367/how-to-get-tails-of-sequence-clojure
;;(defn tails [a-seq]
;;  (reductions (fn [s _] (rest s)) a-seq a-seq))

;;https://codereview.stackexchange.com/questions/135737/generate-all-permutations-in-clojure
;;(defn inits [a-seq]
;;  (reverse (map reverse (tails (reverse a-seq)))))
;;(defn rotations [a-seq]
;;  (rest (map concat (tails a-seq) (inits a-seq))))

(defn rotations [a-seq]
  (let [a-vec (vec a-seq)]
    (for [i (range (count a-vec))]
      (concat (subvec a-vec i) (subvec a-vec 0 i)))))

(defn permutations [a-set]
  (if (empty? a-set)
    (list ())
    (mapcat
      (fn [[x & xs]] (map #(cons x %) (permutations xs)))
      (rotations a-set))))
;;(map vec (permutations (range 1 5)))

;;insert a new node (x) into a binary search tree (bst)
(defn new-x [x bst]
  (let [results (ref '()) bst1 (ref bst)]
    (if (nil? (nth @bst1 1))
      (when (< x (nth @bst1 0)) (dosync (alter bst1 assoc 1 [x nil nil])) (dosync (ref-set results (cons @bst1 @results))) (dosync (ref-set bst1 bst)))
      (when (vector? (nth @bst1 1))
        (let [sub-result (new-x x (nth @bst1 1))]
          (when (not (= sub-result '()))
            (doseq [s sub-result] (dosync (ref-set results (cons [(nth @bst1 0) s (nth @bst1 2)] @results))))))))
    (if (nil? (nth @bst1 2))
      (when (> x (nth @bst1 0)) (dosync (ref-set bst1 (assoc @bst1 2 [x nil nil]))) (dosync (ref-set results (cons @bst1 @results))) (dosync (ref-set bst1 bst)))
      (when (vector? (nth @bst1 2))
        (let [sub-result (new-x x (nth @bst1 2))]
          (when (not (= sub-result '()))
            (doseq [s sub-result] (dosync (ref-set results (cons [(nth @bst1 0) (nth @bst1 1) s] @results))))))))
    @results))
;;(println (new-x 5 [4 [1 nil [2 nil [3 nil nil]]] nil]))
;;=> ([4 [1 nil [2 nil [3 nil nil]]] [5 nil nil]] [4 [1 nil [2 nil [3 nil [5 nil nil]]]] nil])

;;generate binary search tree(s) from a sequence
(defn bst [seq]
  (let [results (ref (list [(nth seq 0) nil nil]))]
    (doseq [x (rest seq)]
      (let [temp (ref '())]
        (doseq [result @results]
          (dosync (alter temp concat (new-x x result))))
        (dosync (ref-set results @temp))))
    @results))
;;(println (bst [4 1 2 3 5]))
;;=> ([4 [1 nil [2 nil [3 nil nil]]] [5 nil nil]] [4 [1 nil [2 nil [3 nil [5 nil nil]]]] nil])

;;generate all binary search trees from (range 1 (+ n 1)) and count them 
(defn bst-count [n]
  (let [seqs (permutations (range 1 (+ n 1)))
         bsts (ref '())]
    (println seqs)
    (doseq [seq seqs]
      (let [sets (bst seq)]
        (doseq [set sets]
          (when (not (some #{set} @bsts))
            (dosync (ref-set bsts (cons set @bsts))) (println set)))))
    (count @bsts)))
;;(println (bst-count 3))
;;=> 7
;;sequences:
;;((1 2 3) (1 3 2) (2 3 1) (2 1 3) (3 1 2) (3 2 1))
;;binary search trees:
;;[1 nil [2 nil [3 nil nil]]]
;;[1 nil [3 [2 nil nil] nil]]
;;[2 nil [3 [1 nil nil] nil]]
;;[2 [1 nil nil] [3 nil nil]]
;;[2 [1 nil [3 nil nil]] nil]
;;[3 [1 nil [2 nil nil]] nil]
;;[3 [2 [1 nil nil] nil] nil]

;;generate all binary search trees from (range 1 (+ n 1)) and write them in "bst/resources/bst.txt"
;;TODO: remove duplicates of the results saved in the file 
(defn bst-save [n]
  (let [file (io/resource "bst.txt")]
    (with-open [w (clojure.java.io/writer file)]
      (let [seqs (permutations (range 1 (+ n 1)))]
        ;;(println seqs)
        (doseq [seq seqs]
          (let [sets (bst seq)]
            (doseq [set sets]
              (println set)
              (.write w (with-out-str (pr set))) (.newLine w))))))))
;;(bst-save 7)

(defn -main []
  (bst-count 3))
