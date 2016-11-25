package jwtech.tw.doubleLanDic;

import junit.framework.TestCase;

import java.util.Set;

/**
 * @author TW
 * @date Administrator on 2016/11/23.
 */
public class JaDicTest
        extends TestCase {
    public void testGetTransWord()
            throws Exception {
        Set<String> words = JaZhDic.GetInstance().getTransWord( "ああ" );
        for(String word :words){
            System.out.println(word);
        }
    }

}