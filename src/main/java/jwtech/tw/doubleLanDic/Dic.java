package jwtech.tw.doubleLanDic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

/**
 * @author TW
 * @date Administrator on 2016/11/17.
 */
public interface Dic {
    Set<String> getTransWordSet(String word);

    void addDicFile(String file)
            throws FileNotFoundException;

    void addDicFile(File file);
}
