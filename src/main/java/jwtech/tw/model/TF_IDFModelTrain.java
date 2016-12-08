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

/**
 * @author TW
 * @date TW on 2016/12/8.
 */
public class TF_IDFModelTrain {
    private static Logger LOG = LoggerFactory.getLogger(TF_IDFModelTrain.class);

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
        System.out.println(TF_IDFModel.getInstance().getWordTF_IDF("ぁと"));
        //trainFromFile( "/Users/TW/ja_all/all","/Users/TW/ja_all/ja_tfidf" );

        //System.out.println(TF_IDF.class.getResource( "/0" ));
        //System.out.println(TF_IDF.class.getClassLoader().getResource( "conf/0" ));
    }
}
