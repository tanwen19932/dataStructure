package jwtech.tw.translater;

import jwtech.tw.doubleLanDic.Dic;

/**
 * @author TW
 * @date Administrator on 2016/11/22.
 */
public abstract class Translater {
    Dic dicFrom;
    Dic dicTo;

    public abstract String trans(String text) throws Exception;
}
