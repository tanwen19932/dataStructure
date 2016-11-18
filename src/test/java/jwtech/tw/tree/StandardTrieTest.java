package jwtech.tw.tree;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TW
 * @date Administrator on 2016/11/16.
 */
public class StandardTrieTest extends TestCase {
    public void testfullMatch()
            throws Exception {
        /**
         * 测试
         */
        List<String> a2 = new ArrayList<>(  );
        for (int i = 0; i < 10000; i++) {
            String a =String.format( "%04d" ,i);
            //System.out.println(a);
            if(compare(a,"6087")==2&&
                    compare(a,"5173")==2&&
                    compare(a,"1358")==2&&
                    compare(a,"3825")==2&&
                    compare(a,"2531")==2){
                a2.add( a );
            }
        }
        System.out.println(a2.size() + " " + a2);
        EnglishTrie trie = new EnglishTrie();
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

    private int compare(String a, String s) {
        int num=0;
        for (int i = 0; i < a.length(); i++) {
            if(s.contains( String.valueOf( a.charAt( i ) ) )){
                s= s.replace( String.valueOf( a.charAt( i ) ),"" );
                num++;
            }
        }
        return num;
    }

}