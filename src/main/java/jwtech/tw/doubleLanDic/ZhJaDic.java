package jwtech.tw.doubleLanDic;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author TW
 * @date Administrator on 2016/11/23.
 */
public class ZhJaDic
        extends AbstractDic {
    private static AbstractDic instance;

    public static synchronized void init() {
        if (instance == null) {
            instance = new ZhJaDic();
        }
    }

    public static Dic GetInstance() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    private ZhJaDic() {
        super("data/dic/ja_zh.txt");
    }

    @Override
    public void addDicFile(File file) {
        try {
            for (String line : Files.readLines(file, Charset.defaultCharset())) {
                try {
                    String ja = line.split("\\|")[0].trim();
                    String zhs = line.split("\\|")[1].trim();
                    for (String zh : zhs.split("\\p{Punct}")) {
                        if (zh.trim().length() > 0)
                        addPair(zh, ja);
                    }
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
}
