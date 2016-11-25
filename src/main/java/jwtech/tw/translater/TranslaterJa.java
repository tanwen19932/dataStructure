package jwtech.tw.translater;

import jwtech.tw.doubleLanDic.JaZhDic;
import jwtech.tw.model.MIModel;
import jwtech.tw.model.Models;
import jwtech.tw.model.TF_IDFModel;
import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.Collection;
import java.util.Set;

/**
 * @author TW
 * @date Administrator on 2016/11/22.
 */
public class TranslaterJa extends Translater {
    TF_IDFModel tf_idfModel = Models.getTF_IDFModel();
    MIModel miModel = Models.getMIModel();


    public TranslaterJa() {
        dicFrom = JaZhDic.GetInstance();
    }

    @Override
    public String trans(String text) {
        Result result = ToAnalysis.parse(text);
        if (result.size() == 0) {
            return null;
        } else if (result.size() == 1) {
            return getSingleTransWord(result.toString());
        } else {
            return getMulitWordTrans(result.getTerms());
        }
    }


    public String getSingleTransWord(String word) {
        Set<String> wordSet = dicFrom.getTransWordSet(word);
        String result = null;
        double max = 0;
        for (String temp : wordSet) {
            if (tf_idfModel.getWordTF_IDF(temp) > max) {
                max = tf_idfModel.getWordTF_IDF(temp);
                result = temp;
            }
        }
        return result;
    }

    public String getMulitWordTrans(Collection words) {
        StringBuilder sb = new StringBuilder();
        String[] wordsArray = new String[words.size()];
        words.toArray(wordsArray);
        String word = wordsArray[0];
        String word2 = wordsArray[1];
        String[] pair = miModel.getHighMIPair(dicFrom.getTransWordSet(word), dicFrom.getTransWordSet(word2));
        String lastTransWord = pair[1];
        sb.append(pair[0]);
        sb.append(pair[1]);
        for (int i = 2; i < wordsArray.length; i++) {
            String[] pair2 = miModel.getHighMIPair(lastTransWord, dicFrom.getTransWordSet(word2));
            sb.append(pair2[1]);
            lastTransWord = pair[1];
        }
        return sb.toString();
    }
}
