package jwtech.tw.model;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author TW
 * @date TW on 2016/11/25.
 */
public abstract class AbstractModel {
    private static Logger LOG = LoggerFactory.getLogger(AbstractModel.class);

    public void loadFromFile(String filePath) throws IOException {
        BufferedReader br = Files.newReader(new File(filePath), Charset.forName("utf-8"));
        String line;
        int lineNum = 0;
        while ((line = br.readLine()) != null) {
            LOG.info("读取到：第"+lineNum);
            if(line.trim().length()==0) continue;
            handleLine(line);
        }
        br.close();
    }
    abstract public void handleLine(String line);
}
