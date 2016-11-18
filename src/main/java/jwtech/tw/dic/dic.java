package jwtech.tw.dic;

import java.io.File;

/**
 * @author TW
 * @date Administrator on 2016/11/17.
 */
public interface dic {
    void addWord();
    void delWord();
    void addWordFile(String file);
    void addWordFile(File file);
}
