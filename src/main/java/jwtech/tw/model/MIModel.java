package jwtech.tw.model;

import com.google.common.base.Preconditions;
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
 * @date Administrator on 2016/11/24.
 */
public class MIModel extends AbstractModel {
    private static Logger LOG = LoggerFactory.getLogger(MIModel.class);
    private static final String dicDir = "data/mi/ja_dic";
    private static final String docsDir = "data/mi/ja_docs";
    private static final String wordsDir = "data/mi/ja_words";
    private static final String miModelDir = "data/mi/ja_mi";
    private static Map<String, Double> wordMIMap = new HashMap<>();
    private static MIModel instance;

    private MIModel() {
        try {
            loadFromFile(miModelDir);
        } catch (IOException e) {
            LOG.error("初始化模型失败 检测路径:{}",miModelDir);
        }
    }

    public static synchronized void init() {
        if (instance == null) {
            instance = new MIModel();
            LOG.info("初始化MI模型完毕！");
        }
    }

    public static MIModel getInstance() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    public String[] getHighMIPair(Set<String> words1, Set<String> words2) throws Exception {
        Preconditions.checkArgument(words1 != null && words2 != null && words1.size() > 0 && words2.size() > 0, "传入的单词至少有一个为空");
        double max = -1;
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

    public String[] getHighMIPair(String word1, Set<String> words2) throws Exception {
        Preconditions.checkArgument(word1 != null && words2 != null && words2.size() > 0, "传入的单词至少有一个为空");
        double max = -1;
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

    public double getMI(String word1, String word2) throws Exception {
        Preconditions.checkArgument(word1 != null && word2 != null, "传入的单词至少有一个为null");
        String combine = word1.compareTo(word2) < 0 ? word1 + word2 : word2 + word1;
        if (wordMIMap.containsKey(combine)) {
            return wordMIMap.get(combine);
        }
        return 0;
    }

    @Override
    public void handleLine(String line) {
        String[] params = line.split("\t");
        wordMIMap.put(params[0] + params[1], Double.valueOf(params[2]));
    }


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
        darts.DoubleArrayTrie totalTrie = new darts.DoubleArrayTrie();
        totalTrie.open(dicDir);
        ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(wordsDir));
        String[] wordsA = (String[]) ois2.readObject();
        ois2.close();
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(docsDir));
        List<String> docsStr = (List<String>) ois.readObject();
        ois.close();
        List<Set<Integer>> docs = new ArrayList<>();
        for (String docStr : docsStr) {
            Set<Integer> doc = new HashSet<>();
            for (String word : docStr.split("\\s")) {
                if (word.trim().length() > 0) {
                    doc.add(totalTrie.exactMatchSearch(word.trim()));
                }
            }
            doc.remove(new Integer(-1));
            docs.add(doc);
        }
        System.gc();
        for (String word : wordsA) {
            System.out.println(totalTrie.exactMatchSearch(word));
        }
        int thread = 64;
        class wordMICal implements Callable<Boolean> {
            private int i;

            private wordMICal(int i) {
                this.i = i;
            }

            @Override
            public Boolean call() throws Exception {
                Stopwatch stopwatch = Stopwatch.createStarted();
                DecimalFormat df = new DecimalFormat("#,##0.00000000");//保留两位小数且不用科学计数法
                List<double[][]> doubles = new ArrayList<>(wordsA.length - i - 1);
                for (int j = 0; j < wordsA.length - i - 1; j++) {
                    doubles.add(new double[2][2]);
                }
                int i_10 = 0;
                int i_00 = 0;
                for (Set<Integer> doc : docs) {
                    //LOG.debug("当前运行到第{}个词 第{}个文档", i, rightNowDoc);
                    if (doc.contains(i)) {
                        i_10++;
                        for (Integer wordId : doc) {
                            //if (wordId < i) iterator.remove();
                            if (wordId > i) {
                                doubles.get(wordId - i - 1)[1][1] += 1;
                                doubles.get(wordId - i - 1)[1][0] -= 1;
                            }
                        }

                    } else {
                        i_00++;
                        for (Integer wordId : doc) {
                            //if (wordId < i) iterator.remove();
                            if (wordId > i) {
                                doubles.get(wordId - i - 1)[0][1] += 1;
                                doubles.get(wordId - i - 1)[0][0] -= 1;
                            }
                        }
                    }
                }
                for (double[][] aDouble : doubles) {
                    aDouble[1][0] += i_10;
                }
                for (double[][] aDouble : doubles) {
                    aDouble[0][0] += i_00;
                }
                //LOG.info("遍历 [{}] 花费时间： {} ",i, stopwatch.elapsed(TimeUnit.MILLISECONDS));
                stopwatch.reset().start();
                for (int j = 0; j < doubles.size(); j++) {
                    StringBuilder sb = new StringBuilder();
                    double[][] values = doubles.get(j);
                    double mi = MI.MI(values);
                    if (mi <= 0.00000000) {
                        continue;
                    }
                    sb.append(wordsA[i]).append("\t").append(wordsA[j + i + 1])
                            .append("\t").append(df.format(mi))
                            .append("\r\n");
                    Files.append(sb, new File(savePath + "/" + wordsA[i]), Charset.defaultCharset());
                    //LOG.info("处理到 {}->{}  word:{}+{} MI:{}  耗时：{}", i, j + i + 1, wordsA[i], wordsA[j + i + 1], mi, stopwatch.elapsed(TimeUnit.MILLISECONDS));
                }
                //LOG.info("计算 [{}] MI花费时间： {} ",i, stopwatch.elapsed(TimeUnit.MILLISECONDS));
                return true;
            }
        }
        int runTimes = 0;
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(thread));
        List<Future<Boolean>> futures = new ArrayList<>();
        Stopwatch stopwatch = Stopwatch.createStarted();
        LOG.info("开始导入 0-{} 词", thread);
        for (int i = 0; i < wordsA.length; i++) {
            futures.add(service.submit(new wordMICal(i)));
            if (i < runTimes * thread + thread) {
            } else {
                for (Future<Boolean> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.error("任务出错！", e);
                    }

                }
                LOG.info("耗时{} {}-{}个词执行完毕 准备删除docs中无用数据", stopwatch.elapsed(TimeUnit.SECONDS), i - thread, i);
                for (Set<Integer> doc : docs) {
                    Iterator<Integer> iterator = doc.iterator();
                    while (iterator.hasNext()) {
                        Integer wordId = iterator.next();
                        if (wordId < i) iterator.remove();
                    }
                }
                runTimes++;
                LOG.info("开始导入第{}-{}个词 ！", i, i + thread);
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

    public static void step4() {}

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
        //System.out.println( 0.0000000<=0.0000000);
        step3("data/mi/ja_mi");
        //System.out.println(TF_IDF.class.getResource( "/0" ));
        //System.out.println(TF_IDF.class.getClassLoader().getResource( "conf/0" ));
    }
}
