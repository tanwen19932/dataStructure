package jwtech.tw.translater;

import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;

/**
 * @author TW
 * @date Administrator on 2016/11/18.
 */
public class ZhText extends Text {

    ZhText(String text) {
        super("zh", text);
    }

    @Override
    public void genSeg() {
        Result result = ToAnalysis.parse(text);
        this.segText = result.toStringWithOutNature();
    }
}
