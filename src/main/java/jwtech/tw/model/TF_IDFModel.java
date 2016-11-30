package jwtech.tw.model;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author TW
 * @date Administrator on 2016/11/23.
 */
public class TF_IDFModel extends AbstractModel {
    private static final String modelPath = "data/mi/ja_tfidf";
    Map<String, Double> wordTFIDFMap = new TreeMap<>();
    private static Logger LOG = LoggerFactory.getLogger(TF_IDFModel.class);
    private static TF_IDFModel instance;

    private TF_IDFModel() {
        try {
            loadFromFile(modelPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void init() {
        if (instance == null) {
            instance = new TF_IDFModel();

        }
    }

    public static TF_IDFModel GetInstance() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    public double getWordTF_IDF(String word) {
        // TODO: 2016/11/25
        if (wordTFIDFMap.containsKey(word)) {
            return wordTFIDFMap.get(word);
        }
        return 0;
    }

    @Override
    public void handleLine(String line) {
        if (line.trim().length() > 1) {
            String[] pairs = line.split("\t");
            wordTFIDFMap.put(pairs[0], Double.parseDouble(pairs[1]));
        }
    }

    public static void trainFromFile(String filePath, String savePath)
            throws IOException {
        CountMap<String> wordDocNumMap = new CountMap<>();
        CountMap<String> wordTotalNumMap = new CountMap<>();
        long totalDocSize = 0;
        long totalWordSize = 0;
        BufferedReader br = Files.newReader(new File(filePath), Charset.forName("utf-8"));
        String line;
        int lineNum = 0;
        while ((line = br.readLine()) != null) {
            lineNum++;
            LOG.info("读取到：第" + lineNum);
            if (line.trim().length() == 0) {
                break;
            }
            String[] pairs = line.split("\t");
            for (String pair : pairs) {
                String key = pair.split(" ")[0];
                long value = Long.parseLong(pair.split(" ")[1]);
                totalWordSize += value;
                wordDocNumMap.add(key);
                wordTotalNumMap.add(key, value);
            }
            totalDocSize++;
        }
        br.close();
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#,##0.00000000");//保留8位小数且不用科学计数法
        for (Map.Entry<String, Long> entry : wordTotalNumMap.entrySet()) {
            String word = entry.getKey();
            long wordCount = entry.getValue();
            long wordDocCount = wordDocNumMap.get(entry.getKey());
            double tf = jwtech.tw.algo.TF_IDF.tf(wordCount, totalWordSize);
            double idf = jwtech.tw.algo.TF_IDF.idf(wordDocCount, totalDocSize);
            double tf_idf = jwtech.tw.algo.TF_IDF.tf_idf(tf, idf);
            sb.append(word).append("\t")
                    .append(df.format(tf_idf))
                    .append("\r\n");
        }
        Files.append(sb, new File(savePath), Charset.defaultCharset());
    }


    public static void main(String[] args)
            throws IOException {
        System.out.println(TF_IDFModel.GetInstance().getWordTF_IDF("ぁと"));
        //trainFromFile( "/Users/TW/ja_all/all","/Users/TW/ja_all/ja_tfidf" );

        //System.out.println(TF_IDF.class.getResource( "/0" ));
        //System.out.println(TF_IDF.class.getClassLoader().getResource( "conf/0" ));
    }
}
