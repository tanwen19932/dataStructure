package jwtech.tw.model;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
            LOG.error("初始化模型失败 检测路径:{}", miModelDir);
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

}

