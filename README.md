# binary-search-tree

The aim of this program is to generate all binary-search-trees in range [1,n]. Given n = 3, seven trees are generated. They are listed below.

[1 nil [2 nil [3 nil nil]]]
[1 nil [3 [2 nil nil] nil]]
[2 nil [3 [1 nil nil] nil]]
[2 [1 nil nil] [3 nil nil]]
[2 [1 nil [3 nil nil]] nil]
[3 [1 nil [2 nil nil]] nil]
[3 [2 [1 nil nil] nil] nil]

The code can be found in the file, bst/src/bst/core.clj.
