package jwtech.tw.translater;

/**
 * @author TW
 * @date Administrator on 2016/11/22.
 */
public class Translate {
    public static String tansFrom2(String lanCodeFrom, String LanCodeTo, String text) {
        try {
            return TranslaterFactory.getTranslater(lanCodeFrom, LanCodeTo).trans(text);
        } catch (Exception e) {
            return text;
        }
    }
}
