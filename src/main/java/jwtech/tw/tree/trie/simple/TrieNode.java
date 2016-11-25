package jwtech.tw.tree.trie.simple;

import java.util.Collection;

/**
 * Trie结点
 */
class TrieNode{
    char key;
    Collection<TrieNode> points=null;
    TrieNode(){
    }
    public TrieNode(char key, Collection<TrieNode> points) {
        this.key = key;
        this.points = points;
    }

    NodeKind kind=null;
}