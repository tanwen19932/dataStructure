package jwtech.tw.model;

import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;

/**
 * @author TW
 * @date Administrator on 2016/11/24.
 */
public class MIModel {
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

    public static void tf_idfFromFile(String fileStr)
            throws IOException {
        BufferedReader br = Files.newReader(new File(fileStr), Charset.forName("utf-8"));
        String line;
        TrainModel MIModel = new TrainModel();
        int lineNum = 0;
        while ((line = br.readLine()) != null) {
            lineNum++;
            System.out.println("读取到：第" + lineNum);
            Document doc = new Document();
            if (line.trim().length() == 0) {
                break;
            }
            String[] pairs = line.split("\t");
            for (String pair : pairs) {
                String key = pair.split(" ")[0];
                long value = Long.parseLong(pair.split(" ")[1]);
                doc.add(key, value);
            }
            MIModel.add(doc);
        }
        br.close();
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#,##0.0000000");//保留两位小数且不用科学计数法
        long totalDocSize = MIModel.getDocsSize();
        long totalWordSize = MIModel.getTotalWordNum();

        for (int i = 0; i <; i++) {

        }
        for (Map.Entry<String, Long> entry : MIModel.getWordTotalNumMap().entrySet()) {
            String word = entry.getKey();
            long wordCount = entry.getValue();
            long wordDocCount = tf_idfModel.getWordDocNumMap().get(entry.getKey());
            double tf = jwtech.tw.algo.TF_IDF.tf(wordCount, tf_idfModel.getTotalWordNum());
            double idf = jwtech.tw.algo.TF_IDF.idf(wordDocCount, totalDocSize);
            double tf_idf = jwtech.tw.algo.TF_IDF.tf_idf(tf, idf);
            sb.append(word).append("\t")
                    .append(wordCount).append("\t")
                    .append(totalWordSize).append("\t")
                    .append(wordDocCount).append("\t")
                    .append(totalDocSize).append("\t")
                    .append("tf\t").append(df.format(tf)).append("\t")
                    .append("idf\t").append(df.format(idf)).append("\t")
                    .append("tf_idf\t").append(df.format(tf_idf))
                    .append("\r\n");
        }
        Files.append(sb, new File("I:/ja_all/all_in_2"), Charset.defaultCharset());
    }
}
