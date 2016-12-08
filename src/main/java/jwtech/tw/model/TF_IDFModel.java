package jwtech.tw.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TW
 * @date Administrator on 2016/11/23.
 */
public class TF_IDFModel extends AbstractModel {
    private static final String modelPath = "data/mi/ja_tfidf";
    Map<String, Double> wordTFIDFMap = new HashMap<>();
    private static Logger LOG = LoggerFactory.getLogger(TF_IDFModel.class);
    private static TF_IDFModel instance;

    private TF_IDFModel() {
        try {
            loadFromFile(modelPath);
        } catch (IOException e) {
            LOG.error("初始化模型失败 检测路径:{}",modelPath);
        }
    }

    public static synchronized void init() {
        if (instance == null) {
            instance = new TF_IDFModel();
            LOG.info("初始化TF_IDF模型完毕！");
        }
    }

    public static TF_IDFModel getInstance() {
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


}
