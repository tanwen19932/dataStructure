package jwtech.tw.translater;

import com.google.common.base.Preconditions;
import jwtech.tw.doubleLanDic.ZhJaDic;
import jwtech.tw.model.MIModel;
import jwtech.tw.model.Models;
import jwtech.tw.model.TF_IDFModel;
import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author TW
 * @date Administrator on 2016/11/22.
 */
public class TranslaterJa extends Translater {
    TF_IDFModel tf_idfModel = Models.getTF_IDFModel();
    MIModel miModel = Models.getMIModel();
    private static Logger LOG = LoggerFactory.getLogger(TranslaterJa.class);

    public TranslaterJa() {
        dicFrom = ZhJaDic.getInstance();
    }

    @Override
    public String trans(String text) {
        Result result = ToAnalysis.parse(text);
        if (result.size() == 0) {
            return null;
        } else if (result.size() == 1) {
            return getSingleTransWord(result.toStringWithOutNature(""));
        } else {
            try {
                return getMulitWordTrans(result.getTerms().stream().map(temp -> temp.getName()).collect(Collectors.toList()));
            } catch (Exception e) {
                return getSingleTransWord(result.toStringWithOutNature(""));
            }
        }
    }

    public String getSingleTransWord(String word) {

        Set<String> wordSet = dicFrom.getTransWordSet(word);
        String result = null;
        try {
            Preconditions.checkNotNull(wordSet);
        } catch (Exception e) {
            LOG.error("{} 未找到合适词 用原词作为翻译 ", word);
            return word;
        }
        double max = -1;
        for (String temp : wordSet) {
            double tempTF_IDF = tf_idfModel.getWordTF_IDF(temp);
            if (tempTF_IDF > max) {
                max = tempTF_IDF;
                result = temp;
            }
        }
        return result;
    }


    public String getMulitWordTrans(Collection words) {
        StringBuilder sb = new StringBuilder();
        String[] wordsArray = (String[]) words.toArray(new String[words.size()]);
        String word = wordsArray[0];
        String word2 = wordsArray[1];
        String[] pair;
        try {
            pair = miModel.getHighMIPair(dicFrom.getTransWordSet(word), dicFrom.getTransWordSet(word2));
        } catch (Exception e) {
            LOG.error("中文组：{} {} 获得翻译词串错误逐个单词翻译", word, word2);
            pair = new String[2];
            pair[0] = getSingleTransWord(word);
            pair[1] = getSingleTransWord(word2);
        }
        String lastTransWord = pair[1];
        sb.append(pair[0]);
        sb.append(pair[1]);
        for (int i = 2; i < wordsArray.length; i++) {
            String[] pair2;
            try {
                pair2 = miModel.getHighMIPair(lastTransWord, dicFrom.getTransWordSet(wordsArray[i]));
            } catch (Exception e) {
                LOG.error("{} 与中文：{} 未找到合适MI信息 按照单个词翻译 ", lastTransWord, wordsArray[i]);
                pair2 = new String[2];
                pair2[1] = getSingleTransWord(word2);
            }
            sb.append(pair2[1]);
            lastTransWord = pair[1];
        }
        return sb.toString();
    }
}
