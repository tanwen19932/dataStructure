package jwtech.tw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author TW
 * @date Administrator on 2016/11/24.
 */
public class TrainModel {
    private AtomicLong totalWordNum = new AtomicLong(0);
    private CountMap<String> wordDocNumMap = new CountMap<>();
    private CountMap<String> wordTotalNumMap = new CountMap<>();
    private List<Document> docs = new ArrayList<>();
    private static TrainModel instance;

    public static synchronized void init() {
        if (instance == null) {
            instance = new TrainModel();
        }
    }

    public static TrainModel GetInstance() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    public synchronized void add(Document doc) {
        docs.add(doc);
        for (Map.Entry<String, Long> word : doc.getWordMap().entrySet()) {
            wordDocNumMap.add(word.getKey(), 1);
            wordTotalNumMap.add(word.getKey(), word.getValue());
        }
        totalWordNum.addAndGet(doc.getTotalWords().longValue());
    }

    public synchronized void remove(Document doc) {
        docs.remove(doc);
        for (Map.Entry<String, Long> word : doc.getWordMap().entrySet()) {
            wordDocNumMap.add(word.getKey(), -1);
            wordTotalNumMap.add(word.getKey(), -word.getValue());
        }
        totalWordNum.addAndGet(-doc.getTotalWords().longValue());
    }

    public long getTotalWordNum() {
        return totalWordNum.longValue();
    }

    public CountMap<String> getWordDocNumMap() {
        return wordDocNumMap;
    }

    public List<Document> getDocs() {
        return docs;
    }

    public long getDocsSize() {
        return docs.size();
    }

    public CountMap<String> getWordTotalNumMap() {
        return wordTotalNumMap;
    }
}
