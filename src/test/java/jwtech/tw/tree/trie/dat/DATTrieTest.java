package jwtech.tw.tree.trie.dat;

import junit.framework.TestCase;

/**
 * @author TW
 * @date TW on 2016/11/26.
 */
public class DATTrieTest extends TestCase {
    public void testExist() throws Exception {
        DATTrie trie = new DATTrie();
        trie.insert("我的");
        trie.exist("我的");
    }

}