package darts;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author TW
 * @date TW on 2016/11/28.
 */
public class DoubleArrayTrieTest extends TestCase {
    public void testBuild() throws Exception {
        DoubleArrayTrie totalTrie = new DoubleArrayTrie();
        Set<String> words = new TreeSet<>();
        BufferedReader br = Files.newReader(new File("/Users/TW/ja_all/all"), Charset.forName("utf-8"));
        String line;
        int lineNum = 0;
        while ((line = br.readLine()) != null) {
            lineNum++;
            //if (lineNum == 10000) break;
            if (line.trim().length() == 0) {
                break;
            }
            //Set<String> docSet = new TreeSet<>();
            //Set<String> docWords = new TreeSet<>();
            String[] pairs = line.split("\t");
            for (String pair : pairs) {
                String key = pair.split(" ")[0];
                //docWords.add(key);
                words.add(key);
                //docSet.add(key);
            }

            //DoubleArrayTrie trie = new DoubleArrayTrie();
            //trie.build(Lists.newArrayList( docWords.toArray(new String[docWords.size()])));
            //docs.add(trie);
        }
        br.close();
        String[] wordsA = words.toArray(new String[words.size()]);
        totalTrie.build((Lists.newArrayList(wordsA)));
        List<Set<Integer>> docs = new ArrayList<>();
        br = Files.newReader(new File("/Users/TW/ja_all/all"), Charset.forName("utf-8"));
        while ((line = br.readLine()) != null) {
            lineNum++;
            if (line.trim().length() == 0) {
                break;
            }
            String[] pairs = line.split("\t");
            Set<Integer> doc = new TreeSet<>();
            for (String pair : pairs) {
                String key = pair.split(" ")[0];
                doc.add(totalTrie.exactMatchSearch(key));
            }
            doc.remove(new Integer(-1));
            docs.add(doc);
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < wordsA.length; i++) {
            for (int j = i + 1; j < wordsA.length; j++) {
                double[][] values = new double[2][2];
                stopwatch.reset().start();
                for (Set<Integer> doc : docs) {
                    int x = 0;
                    int y = 0;
                    try {
                        if (doc.contains(totalTrie.exactMatchSearch(wordsA[i]))) x = 1;
                    } catch (Exception e) {
                    }
                    try {
                        if (doc.contains(totalTrie.exactMatchSearch(wordsA[i]))) y = 1;
                    } catch (Exception e) {
                    }
                    values[x][y] += 1;
                }
                //LOG.info("遍历花费时间： {} ", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                stopwatch.reset().start();
                //sb.append(wordsA[i]).append("\t").append(wordsA[j])
                //        .append("\t").append(df.format(MI.MI(values)))
                //        .append("\r\n");
                //LOG.info("处理到 {}-> {}  word:{}-- {}  耗时：{}", i, j, wordsA[i], wordsA[j], stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }
        }
        //Files.append(sb, new File(savePath), Charset.defaultCharset());
    }


}