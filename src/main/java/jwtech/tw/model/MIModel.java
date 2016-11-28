package jwtech.tw.model;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import jwtech.tw.algo.MI;
import jwtech.tw.tree.trie.dat.DoubleArrayTrie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * @author TW
 * @date Administrator on 2016/11/24.
 */
public class MIModel {
    private static Logger LOG = LoggerFactory.getLogger(MIModel.class);

    public String[] getHighMIPair(Set<String> words1, Set<String> words2) {
        double max = 0;
        String[] result = new String[2];
        for (String word1 : words1) {
            for (String word2 : words2) {
                if (getMI(word1, word2) > max) {
                    max = getMI(word1, word2);
                    result[0] = word1;
                    result[1] = word2;
                }
            }
        }
        return result;
    }

    public String[] getHighMIPair(String word1, Set<String> words2) {
        double max = 0;
        String[] result = new String[2];
        for (String word2 : words2) {
            if (getMI(word1, word2) > max) {
                max = getMI(word1, word2);
                result[0] = word1;
                result[1] = word2;
            }
        }
        return result;
    }

    public double getMI(String word1, String word2) {
        //TODO qiu mi
        double result = 0;
        return 0;
    }
    public static void trainFromFile3(String filePath, String savePath) throws IOException{
        darts.DoubleArrayTrie totalTrie= new darts.DoubleArrayTrie();
        Set<String> words = new TreeSet<>();
        BufferedReader br = Files.newReader(new File(filePath), Charset.forName("utf-8"));
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
        String[] wordsA =  words.toArray(new String[words.size()]);
        totalTrie.build((Lists.newArrayList(wordsA)));
        List<Set<Integer>> docs = new ArrayList<>();
        br = Files.newReader(new File(filePath), Charset.forName("utf-8"));
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

        DecimalFormat df = new DecimalFormat("#,##0.00000000");//保留两位小数且不用科学计数法
        for (int i = 0; i < wordsA.length; i++) {
            for (int j = i + 1; j < wordsA.length; j++) {
                StringBuilder sb = new StringBuilder();
                double[][] values = new double[2][2];
                stopwatch.reset().start();
                for (Set<Integer> doc : docs) {
                    int x = 0;
                    int y = 0;
                    try {
                        if (doc.contains(totalTrie.exactMatchSearch(wordsA[i]))) x = 1;
                    }catch (Exception e){
                    }
                    try {
                        if (doc.contains(totalTrie.exactMatchSearch(wordsA[i]))) y = 1;
                    }catch (Exception e){
                    }
                    values[x][y] += 1;
                }
                LOG.info("遍历花费时间： {} ", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                stopwatch.reset().start();
                sb.append(wordsA[i]).append("\t").append(wordsA[j])
                        .append("\t").append(df.format(MI.MI(values)))
                        .append("\r\n");
                Files.append(sb, new File(savePath), Charset.defaultCharset());
                LOG.info("处理到 {}-> {}  word:{}-- {}  耗时：{}", i, j, wordsA[i], wordsA[j], stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }
        }
    }

    public static void trainFromFile2(String filePath, String savePath) throws IOException {
        List<Set<String>> allDoc = new ArrayList<>();
        DoubleArrayTrie datTrie = new DoubleArrayTrie();
        int totalWordSize = 0;
        int totalDocSize = 0;
        BufferedReader br = Files.newReader(new File(filePath), Charset.forName("utf-8"));
        String line;
        int lineNum = 0;
        while ((line = br.readLine()) != null) {
            lineNum++;
            LOG.info("读取到：第" + lineNum);
            if (lineNum == 1000) break;
            if (line.trim().length() == 0) {
                break;
            }
            Set<String> docSet = new TreeSet<>();
            String[] pairs = line.split("\t");
            for (String pair : pairs) {
                String key = pair.split(" ")[0];
                if(!datTrie.Exists(key)){
                    totalWordSize++;
                    try {
                        datTrie.Insert(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                docSet.add(key);
            }
            pairs = null;
            allDoc.add(docSet);
        }
        totalDocSize = allDoc.size();
        int[][] total = new int[totalWordSize][totalWordSize];
        br.close();
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#,##0.00000000");//保留两位小数且不用科学计数法
        Stopwatch stopwatch = Stopwatch.createStarted();
        for(Set<String> doc : allDoc){
            String[] wordsA = doc.toArray(new String[doc.size()]);
            for (int i = 0; i < wordsA.length; i++) {
                for (int j = i + 1; j < wordsA.length; j++) {
                    datTrie.GetAllChildWord(1);
                }
            }

        }

        //for (int i = 0; i < wordsA.length; i++) {
        //    for (int j = i + 1; j < wordsA.length; j++) {
        //        double[][] values = new double[2][2];
        //        stopwatch.reset().start();
        //        for (String doc : allDoc) {
        //            int x = 0;
        //            int y = 0;
        //            if (doc.contains(wordsA[i])) x = 1;
        //            if (doc.contains(wordsA[j])) y = 1;
        //            values[x][y] += 1;
        //        }
        //        LOG.info("遍历花费时间： {} ", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        //        stopwatch.reset().start();
        //        sb.append(wordsA[i]).append("\t").append(wordsA[j])
        //                .append("\t").append(df.format(MI.MI(values)))
        //                .append("\r\n");
        //        LOG.info("处理到 {}-> {}  word:{}-- {}  耗时：{}", i, j, wordsA[i], wordsA[j], stopwatch.elapsed(TimeUnit.MILLISECONDS));
        //    }
        //}
        //Files.append(sb, new File(savePath), Charset.defaultCharset());
    }

    public static void trainFromFile(String filePath, String savePath)
            throws IOException {
        List<DoubleArrayTrie> allDoc = new ArrayList<>();
        Set<String> words = new TreeSet<>();
        int totalDocSize = 0;
        BufferedReader br = Files.newReader(new File(filePath), Charset.forName("utf-8"));
        String line;
        int lineNum = 0;
        while ((line = br.readLine()) != null) {
            lineNum++;
            LOG.info("读取到：第" + lineNum);
            if (lineNum > 1000) break;
            if (line.trim().length() == 0) {
                break;
            }
            //Set<String> docSet = new TreeSet<>();
            DoubleArrayTrie arrayTrie= new DoubleArrayTrie();
            String[] pairs = line.split("\t");
            for (String pair : pairs) {
                String key = pair.split(" ")[0];
                if(arrayTrie.Exists(key)){
                    try {
                        arrayTrie.Insert(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                words.add(key);
                //docSet.add(key);
            }
            pairs = null;
            allDoc.add(arrayTrie);
        }
        totalDocSize = allDoc.size();
        String[] wordsA = words.toArray(new String[words.size()]);
        //int[][] total = new int[words.size()][words.size()];
        br.close();
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#,##0.00000000");//保留两位小数且不用科学计数法
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < wordsA.length; i++) {
            for (int j = i + 1; j < wordsA.length; j++) {
                double[][] values = new double[2][2];
                stopwatch.reset().start();
                for (DoubleArrayTrie doc : allDoc) {
                    int x = 0;
                    int y = 0;
                    try {
                        if (doc.Exists(wordsA[i])) x = 1;
                    }catch (Exception e){
                    }
                    try {
                        if (doc.Exists(wordsA[j])) y = 1;
                    }catch (Exception e){
                    }
                    values[x][y] += 1;
                }
                LOG.info("遍历花费时间： {} ", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                stopwatch.reset().start();
                sb.append(wordsA[i]).append("\t").append(wordsA[j])
                        .append("\t").append(df.format(MI.MI(values)))
                        .append("\r\n");
                LOG.info("处理到 {}-> {}  word:{}-- {}  耗时：{}", i, j, wordsA[i], wordsA[j], stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }
        }
        Files.append(sb, new File(savePath), Charset.defaultCharset());
    }

    public static void main(String[] args)
            throws IOException {
        trainFromFile3("/Users/TW/ja_all/all", "/Users/TW/ja_all/ja_mi");
        //System.out.println(TF_IDF.class.getResource( "/0" ));
        //System.out.println(TF_IDF.class.getClassLoader().getResource( "conf/0" ));
    }

}
