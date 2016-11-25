package jwtech.tw.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author TW
 * @date Administrator on 2016/11/24.
 */
public class Document {
    private AtomicLong totalWords = new AtomicLong( 0 );
    private CountMap<String> wordMap = new CountMap<>();

    public Document(){
    }


    public Document(CountMap<String> wordMap) {
        this.wordMap.add( wordMap );
        for (long count : wordMap.values()) {
            totalWords.addAndGet( count );
        }
    }
    public void add(String word) {
        add( word, 1 );
    }

    public void add(String word, long value) {
        wordMap.add(word,value);
        totalWords.addAndGet( value );
    }
    public AtomicLong getTotalWords() {
        return totalWords;
    }

    public CountMap<String> getWordMap() {
        return wordMap;
    }
}
