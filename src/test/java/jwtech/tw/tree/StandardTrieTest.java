package jwtech.tw.tree;

import junit.framework.TestCase;

/**
 * @author TW
 * @date Administrator on 2016/11/16.
 */
public class StandardTrieTest extends TestCase {
    public void fullMatch()
            throws Exception {
        /**
         * 测试
         */
        StandardTrie trie = new StandardTrie();
        trie.insert( "bear" );
        trie.insert( "bell" );
        trie.insert( "bid" );
        trie.insert( "bull" );
        trie.insert( "buy" );
        trie.insert( "sell" );
        trie.insert( "stock" );
        trie.insert( "stop" );
        //trie.preRootTraverse( trie.getRoot() );
        trie.fullMatch( "stoops" );
    }

}