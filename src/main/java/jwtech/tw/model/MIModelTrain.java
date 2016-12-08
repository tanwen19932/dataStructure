package jwtech.tw.model;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import jwtech.tw.algo.MI;
import jwtech.tw.tree.trie.dat.DoubleArrayTrie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author TW
 * @date TW on 2016/12/8.
 */
public class MIModelTrain {
    private static final String dicDir = "data/mi/ja_dic";
    private static final String docsDir = "data/mi/ja_docs";
    private static final String wordsDir = "data/mi/ja_words";
    private static final String miModelDir = "data/mi/ja_mi";

    private static Logger LOG = LoggerFactory.getLogger(MIModelTrain.class);

    //模型训练
    public static void step1(String filePath, String savePath) throws IOException {
        //存储字典
        darts.DoubleArrayTrie totalTrie = new darts.DoubleArrayTrie();
        Set<String> words = new TreeSet<>();
        BufferedReader br = Files.newReader(new File(filePath), Charset.forName("utf-8"));
        String line;
        int lineNum = 0;
        while ((line = br.readLine()) != null) {
            lineNum++;
            LOG.info("初始化所有word 读取到行数：" + lineNum);
            if (line.trim().length() == 0) {
                break;
            }
            String[] pairs = line.split("\t");
            for (String pair : pairs) {
                String key = pair.split(" ")[0];
                words.add(key);
            }
        }
        br.close();
        String[] wordsA = words.toArray(new String[words.size()]);
        totalTrie.build(Lists.newArrayList(wordsA));
        totalTrie.save(dicDir);
    }

    public static void step2(String filePath) throws IOException {
        darts.DoubleArrayTrie totalTrie = new darts.DoubleArrayTrie();
        totalTrie.open(dicDir);
        BufferedReader br = Files.newReader(new File(filePath), Charset.forName("utf-8"));
        List<String> docs = new ArrayList<>();
        Set<String> words = new TreeSet<>();

        String line;
        int lineNum = 0;
        while ((line = br.readLine()) != null) {
            lineNum++;
            LOG.info("初始化所有doc信息 读取到行数：" + lineNum);
            if (line.trim().length() == 0) {
                break;
            }
            Set<Integer> bitSet = new TreeSet<>();
            String[] pairs = line.split("\t");
            for (String pair : pairs) {
                String key = pair.split(" ")[0];
                bitSet.add(totalTrie.exactMatchSearch(key));
                words.add(key);
            }
            line = line.replaceAll("\\d", "");
            docs.add(line);
        }
        br.close();
        String[] wordsA = words.toArray(new String[words.size()]);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(wordsDir));
        oos.writeObject(wordsA);
        oos.close();
        oos = new ObjectOutputStream(new FileOutputStream(docsDir));
        oos.writeObject(docs);
        oos.close();

    }

    public static void step3(String savePath) throws IOException, ClassNotFoundException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        LOG.info(" 开始导入模型------ ");
        darts.DoubleArrayTrie totalTrie = new darts.DoubleArrayTrie();
        totalTrie.open(dicDir);
        LOG.info(" 词语Trie模型导入成功 耗时{}ms------ ", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(wordsDir));
        String[] wordsA = (String[]) ois2.readObject();
        ois2.close();
        LOG.info(" 词语数组[]模型导入成功 耗时{}ms------ ", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(docsDir));
        List<String> docsStr = (List<String>) ois.readObject();
        ois.close();
        LOG.info(" 文档模型导入成功 耗时{}ms------ ", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        List<Set<Integer>> docs = new ArrayList<>();
        for (String docStr : docsStr) {
            Set<Integer> doc = new TreeSet<>();
            for (String word : docStr.split("\\s")) {
                if (word.trim().length() > 0) {
                    doc.add(totalTrie.exactMatchSearch(word.trim()));
                }
            }
            doc.remove(new Integer(-1));
            docs.add(doc);
        }
        System.gc();
        LOG.info(" 文档模型处理成功 耗时{}ms------ ", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        class wordMICal implements Callable<Boolean> {
            private int i;
            private int n;

            private wordMICal(int i, int n) {
                this.i = i;
                this.n = n;
            }

            @Override
            public Boolean call() throws Exception {
                Stopwatch stopwatch = Stopwatch.createStarted();
                List<List<double[][]>> i_nWord = new ArrayList<>();
                //LOG.info("{}- {} 创建新的数组存放doc信息 耗时{}ms ———————— ",i ,i+n,stopwatch.elapsed(TimeUnit.MILLISECONDS));
                for (int m = i; m < i + n; m++) {
                    List<double[][]> doubles = new ArrayList<>(wordsA.length - i - 1);
                    for (int j = 0; j < wordsA.length - m - 1; j++) {
                        doubles.add(new double[2][2]);
                    }
                    i_nWord.add(doubles);
                }
                LOG.info("{}- {} 创建存放doc信息数组成功 耗时{}ms ———————— 准备遍历DOCS", i, i + n, stopwatch.elapsed(TimeUnit.MILLISECONDS));
                int[] i_10 = new int[n];
                int[] i_00 = new int[n];
                for (Set<Integer> doc : docs) {
                    for (int m = i; m < i + n; m++) {
                        if (doc.contains(m)) {
                            i_10[m - i]++;
                        } else {
                            i_00[m - i]++;
                        }
                    }
                    for (Integer wordId : doc) {
                        if (wordId <= i) continue;
                        for (int m = i; m < i + n; m++) {
                            if (doc.contains(m)) {
                                if (wordId > m) {
                                    i_nWord.get(m - i).get(wordId - m - 1)[1][1] += 1;
                                    i_nWord.get(m - i).get(wordId - m - 1)[1][0] -= 1;
                                }
                            } else {
                                if (wordId > m) {
                                    i_nWord.get(m - i).get(wordId - m - 1)[0][1] += 1;
                                    i_nWord.get(m - i).get(wordId - m - 1)[0][0] -= 1;
                                }
                            }
                        }
                    }
                }
                for (int j = 0; j < n; j++) {
                    for (double[][] aDouble : i_nWord.get(j)) {
                        aDouble[1][0] += i_10[j];
                    }
                }
                for (int j = 0; j < n; j++) {
                    for (double[][] aDouble : i_nWord.get(j)) {
                        aDouble[0][0] += i_00[j];
                    }
                }
                LOG.info("遍历DOCS完毕 [{}-{}] 花费时间： {}s ——————计算MI信息 ", i, i + n, stopwatch.elapsed(TimeUnit.SECONDS));
                stopwatch.reset().start();
                for (int m = 0; m < n; m++) {
                    for (int j = 0; j < i_nWord.get(m).size(); j++) {
                        StringBuilder sb = new StringBuilder();
                        double[][] values = i_nWord.get(m).get(j);
                        double mi = MI.MI(values);
                        if (mi == 0) {
                            continue;
                        }
                        sb.append(j + i + m + 1)
                                .append("\t").append(mi)
                                .append("\r\n");
                        Files.append(sb, new File(savePath + "/" + (i + m)), Charset.defaultCharset());
                        //LOG.info("处理到 {}->{}  word:{}+{} MI:{}  耗时：{}", i, j + i + 1, wordsA[i], wordsA[j + i + 1], mi, stopwatch.elapsed(TimeUnit.MILLISECONDS));
                    }
                }
                LOG.info("计算MI完毕 [{}-{}] MI花费时间： {}s ", i, i + n, stopwatch.elapsed(TimeUnit.SECONDS));
                return true;
            }
        }
        int runTimes = 0;
        int thread = 64;
        int startWord = 0;
        int n = 3;
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(thread));
        List<Future<Boolean>> futures = new ArrayList<>();
        LOG.info("开始导入 0-{} 词", thread * n);
        for (int i = 0; i < wordsA.length; i += n) {
            if (i < startWord) {
                futures.add(service.submit(() -> true));
            } else {
                futures.add(service.submit(new wordMICal(i, n)));
            }
            if (i < (runTimes * thread * n + n * thread)) {
            } else {
                LOG.info("等待处理：{} - {} ", (i - n * thread), i);
                for (Future<Boolean> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.error("任务出错！", e);
                    }
                }
                futures.clear();
                LOG.info("{} - {} 个词执行完毕 耗时{}s 准备删除docs中无用数据", stopwatch.elapsed(TimeUnit.SECONDS), (i - n * thread), i);
                for (Set<Integer> doc : docs) {
                    Iterator<Integer> iterator = doc.iterator();
                    while (iterator.hasNext()) {
                        Integer wordId = iterator.next();
                        if (wordId < i) iterator.remove();
                        else break;
                    }
                }
                runTimes++;
                LOG.info("开始导入第{}-{}个词 ！", i, i + n);
                stopwatch.reset().start();
            }
        }
    }
    //public static void step3_2(String savePath) throws IOException, ClassNotFoundException {
    //    darts.DoubleArrayTrie totalTrie = new darts.DoubleArrayTrie();
    //    totalTrie.open(dicDir);
    //    ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(wordsDir));
    //    String[] wordsA = (String[]) ois2.readObject();
    //    ois2.close();
    //    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(docsDir));
    //    List<String> docsStr = (List<String>) ois.readObject();
    //    ois.close();
    //    List<BitSet> docs = new ArrayList<>();
    //    for (String docStr : docsStr) {
    //        BitSet doc = new BitSet();
    //        for (String word : docStr.split("\\s")) {
    //            if (word.trim().length() > 0) {
    //                doc.set(totalTrie.exactMatchSearch(word.trim()));
    //            }
    //        }
    //        docs.add(doc);
    //    }
    //    System.gc();
    //    Stopwatch stopwatch = Stopwatch.createStarted();
    //    DecimalFormat df = new DecimalFormat("#,##0.00000000");//保留两位小数且不用科学计数法
    //    for (int i = 0; i < wordsA.length; i++) {
    //        List<double[][]> doubles = new ArrayList<>(wordsA.length - i);
    //        for (int j = 0; j < wordsA.length - i; j++) {
    //            doubles.add(new double[2][2]);
    //        }
    //        int rightNowDoc = 0;
    //        for (BitSet doc : docs) {
    //            rightNowDoc++;
    //            LOG.info("当前运行到第{}个词 第{}个文档",i,rightNowDoc);
    //
    //            if (doc.get(i)) {
    //                for (int j = 0; j < doubles.size(); j++) {
    //                    doubles.get(j)[1][0] +=1;
    //                }
    //                for (Integer wordId : doc.) {
    //                    if (wordId < i) doc.remove(new Integer(i));
    //                    else if(wordId >i){
    //                        doubles.get(wordId-i)[1][1] += 1;
    //                        doubles.get(wordId-i)[1][0] -= 1;
    //                    }
    //                }
    //
    //            } else {
    //                for (int j = 0; j < doubles.size(); j++) {
    //                    doubles.get(j)[0][0] +=1;
    //                }
    //                for (Integer wordId : doc) {
    //                    if (wordId < i) doc.remove(new Integer(i));
    //                    else if(wordId >i){
    //                        doubles.get(wordId-i)[0][1] += 1;
    //                        doubles.get(wordId-i)[0][0] -= 1;
    //                    }
    //                }
    //            }
    //        }
    //        LOG.info("遍历花费时间： {} ", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    //        for (int j = 0; j < doubles.size(); j++) {
    //            StringBuilder sb = new StringBuilder();
    //            double[][] values = doubles.get(j);
    //            stopwatch.reset().start();
    //            stopwatch.reset().start();
    //            double mi = MI.MI(values);
    //            sb.append(wordsA[i]).append("\t").append(wordsA[j + i + 1])
    //                    .append("\t").append(df.format(mi))
    //                    .append("\r\n");
    //            Files.append(sb, new File(savePath), Charset.defaultCharset());
    //            LOG.info("处理到 {}->{}  word:{}+{} MI:{}  耗时：{}", i, j, wordsA[i], wordsA[j + i + 1], mi, stopwatch.elapsed(TimeUnit.MILLISECONDS));
    //
    //        }
    //    }
    //}

    public static void step4() {
    }

    public static void trainFromFile3(String filePath, String savePath) throws IOException {
        darts.DoubleArrayTrie totalTrie = new darts.DoubleArrayTrie();
        totalTrie.open(dicDir);
        Set<String> words = new TreeSet<>();
        BufferedReader br = Files.newReader(new File(filePath), Charset.forName("utf-8"));
        List<Set<Integer>> docs = new ArrayList<>();

        String line;
        int lineNum = 0;
        while ((line = br.readLine()) != null) {
            lineNum++;
            LOG.info("初始化所有doc信息 读取到行数：" + lineNum);
            if (line.trim().length() == 0) {
                break;
            }
            String[] pairs = line.split("\t");
            Set<Integer> doc = new TreeSet<>();
            for (String pair : pairs) {
                String key = pair.split(" ")[0];
                doc.add(totalTrie.exactMatchSearch(key));
                words.add(key);
            }
            doc.remove(new Integer(-1));
            docs.add(doc);
        }
        br.close();
        String[] wordsA = words.toArray(new String[words.size()]);
        for (String w : wordsA) {
            System.out.println(totalTrie.exactMatchSearch(w));
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
                        if (doc.contains(totalTrie.exactMatchSearch(wordsA[i]))) {
                            x = 1;
                        }
                    } catch (Exception ignored) {
                    }
                    try {
                        if (doc.contains(totalTrie.exactMatchSearch(wordsA[j]))) {
                            y = 1;
                        }
                    } catch (Exception ignored) {
                    }
                    values[x][y] += 1;
                }
                LOG.info("遍历花费时间： {} ", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                stopwatch.reset().start();
                double mi = MI.MI(values);
                sb.append(wordsA[i]).append("\t").append(wordsA[j])
                        .append("\t").append(df.format(mi))
                        .append("\r\n");
                Files.append(sb, new File(savePath), Charset.defaultCharset());
                LOG.info("处理到 {}->{}  word:{}+{} MI:{}  耗时：{}", i, j, wordsA[i], wordsA[j], mi, stopwatch.elapsed(TimeUnit.MILLISECONDS));
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
                if (!datTrie.Exists(key)) {
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
        for (Set<String> doc : allDoc) {
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
            DoubleArrayTrie arrayTrie = new DoubleArrayTrie();
            String[] pairs = line.split("\t");
            for (String pair : pairs) {
                String key = pair.split(" ")[0];
                if (arrayTrie.Exists(key)) {
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
                    } catch (Exception e) {
                    }
                    try {
                        if (doc.Exists(wordsA[j])) y = 1;
                    } catch (Exception e) {
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
            throws IOException, ClassNotFoundException {
        //trainFromFile3("/Users/TW/ja_all/all", "/Users/TW/ja_all/ja_mi");
        //step2("/Users/TW/ja_all/all");
        //System.out.println( 0.0000000==0);
        step3("data/mi/ja_mi");
        //System.out.println(TF_IDF.class.getResource( "/0" ));
        //System.out.println(TF_IDF.class.getClassLoader().getResource( "conf/0" ));
    }

}
