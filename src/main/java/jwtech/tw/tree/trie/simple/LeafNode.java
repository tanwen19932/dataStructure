package jwtech.tw.tree.trie.simple;

/**
 * Trie叶子结点
 */
class LeafNode extends TrieNode {

    LeafNode(char k) {
        super();
        super.key = k;
        super.kind = NodeKind.LN;
    }
}