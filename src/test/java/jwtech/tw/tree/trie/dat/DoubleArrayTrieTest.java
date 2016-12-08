package jwtech.tw.tree.trie.dat;

import com.google.common.io.Files;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author TW
 * @date TW on 2016/11/25.
 */
public class DoubleArrayTrieTest extends TestCase {
    public void testExists() throws Exception {
        DoubleArrayTrie words = new DoubleArrayTrie();
        DoubleArrayTrie arrayTrie = new DoubleArrayTrie();
        BufferedReader br = Files.newReader(new File("/Users/TW/ja_all/all"), Charset.forName("utf-8"));
        String line;
        int lineNum = 0;

        while ((line = br.readLine()) != null) {
            lineNum++;
            if (lineNum == 1000) break;
            if (line.trim().length() == 0) {
                break;
            }
            //Set<String> docSet = new TreeSet<>();
            String[] pairs = line.split("\t");
            for (String pair : pairs) {
                String key = pair.split(" ")[0];
                try {
                    arrayTrie.Insert(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                words.Insert(key);
                //docSet.add(key);
            }
        }
        for (String word : arrayTrie.GetAllChildWord(0)) {
            System.out.println(words.Exists(word));
        }
    }

    public void testFindAllWords() throws Exception {
        ArrayList<String> words = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("E:/兔子的试验学习中心[课内]/ACM大赛/ACM第四届校赛/E命令提示/words3.dic")));
        String s;
        int num = 0;
        while ((s = reader.readLine()) != null) {
            words.add(s);
            num++;
        }
        DoubleArrayTrie dat = new DoubleArrayTrie();

        for (String word : words) {
            dat.Insert(word);
        }

        System.out.println(dat.Base.length);
        System.out.println(dat.Tail.length);

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String word = sc.next();
            System.out.println(dat.Exists(word));
            System.out.println(dat.FindAllWords(word));
        }

    }

}