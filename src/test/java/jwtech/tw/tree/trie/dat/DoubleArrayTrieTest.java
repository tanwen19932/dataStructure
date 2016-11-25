package jwtech.tw.tree.trie.dat;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author TW
 * @date TW on 2016/11/25.
 */
public class DoubleArrayTrieTest extends TestCase {
    public void testFindAllWords() throws Exception {
        ArrayList<String> words = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("E:/兔子的试验学习中心[课内]/ACM大赛/ACM第四届校赛/E命令提示/words3.dic")));
        String s;
        int num = 0;
        while((s=reader.readLine()) != null)
        {
            words.add(s);
            num ++;
        }
        DoubleArrayTrie dat = new DoubleArrayTrie();

        for(String word: words)
        {
            dat.Insert(word);
        }

        System.out.println(dat.Base.length);
        System.out.println(dat.Tail.length);

        Scanner sc = new Scanner(System.in);
        while(sc.hasNext())
        {
            String word = sc.next();
            System.out.println(dat.Exists(word));
            System.out.println(dat.FindAllWords(word));
        }

    }

}