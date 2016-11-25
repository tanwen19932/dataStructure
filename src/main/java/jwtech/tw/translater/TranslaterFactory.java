package jwtech.tw.translater;

/**
 * @author TW
 * @date Administrator on 2016/11/22.
 */
public class TranslaterFactory {
    public static Translater getTranslater(String lanCodeFrom, String lanCodeTo) {
        if (lanCodeTo.equals( "ja" )) {
            return new TranslaterJa();
        }
        return null;
    }

}
