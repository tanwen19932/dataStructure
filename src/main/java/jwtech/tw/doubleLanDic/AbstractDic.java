package jwtech.tw.doubleLanDic;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TW
 * @date Administrator on 2016/11/23.
 */
public abstract class AbstractDic
        implements Dic {
    private static Logger LOG = LoggerFactory.getLogger(AbstractDic.class);
    Map<String, Set<String>> dicMap = new ConcurrentHashMap<>();
    String dicFilePath = null;
    private AbstractDic() {
        try {
            addDicFile(dicFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected AbstractDic(String dicFilePath) {
        this.dicFilePath = dicFilePath;
        try {
            addDicFile(dicFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Set<String> getTransWordSet(String word) {
        return dicMap.get(word);
    }


    @Override
    public void addDicFile(String filePath)
            throws FileNotFoundException {
        if (filePath == null) {
            throw new FileNotFoundException(" 请设置字典 Path");
        }
        File file = new File(filePath);
        System.out.println("词典位置： " + file.getAbsolutePath());
        addDicFile(file);
    }

    @Override
    public void addDicFile(File file) {
        try {
            for (String line : Files.readLines(file, Charset.defaultCharset())) {
                try {
                    String ja = line.split("\\|")[0].trim();
                    String zhs = line.split("\\|")[1].trim();
                    Set<String> set = new TreeSet<>();
                    for (String zh : zhs.split("[\\p{Punct} ；：;:'\"]")) {
                        if (zh.trim().length() > 0)
                            set.add(zh.trim());
                    }
                    addPair(ja, set);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
            System.out.println("词典初始化完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPair(String word, Set<String> transWords) {
        if (dicMap.containsKey(word)) {
            for(String transWord: transWords){
                dicMap.get(word).add(transWord);
            }
        } else {
            dicMap.put(word, transWords);
        }
    }
    public void addPair(String word, String transWord) {
        if (dicMap.containsKey(word)) {
            dicMap.get(word).add(transWord);
        } else {
            Set<String> set = new TreeSet<>();
            set.add(transWord);
            dicMap.put(word, set);
        }
    }

    public String getDicFilePath() {
        return dicFilePath;
    }

    public void setDicFilePath(String dicFilePath) {
        this.dicFilePath = dicFilePath;
    }
}
