package jwtech.tw.main;

import com.google.common.io.Files;
import javaMI.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author TW
 * @date Administrator on 2016/11/22.
 */
public class SegGet {
    public static void main(String[] args)
            throws IOException {
        Map<String, Pair<Integer, Integer>> wordMap = new TreeMap<>();

        for (String line : Files.readLines(new File("0"), Charset.forName("utf-8"))) {
            String[] words = line.split("\\s");
            for (String word : words) {
                word = word.replaceAll("[,.，。、]", "").trim();
                word = word.replaceAll("\\p{Punct}", "").trim();
                word = word.replaceAll("\\pP", "").trim();
                word = word.replaceAll("\\p{P}", "").trim();
                word = word.replaceAll("[\\pP‘’“”]", "").trim();
                if (word.replaceAll("", "").trim().length() == 0) {
                    continue;
                }
                if (wordMap.containsKey(word)) {
                    Pair<Integer, Integer> pair = wordMap.get(word);
                    Integer a = pair.a;
                    Integer aNew = new Integer(a.intValue() + 1);
                    wordMap.put(word, new Pair<>(aNew, 1));
                } else {
                    wordMap.put(word, new Pair<>(1, 1));
                }
            }
        }
        for (Map.Entry entry : wordMap.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
}
