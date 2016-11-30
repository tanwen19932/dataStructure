package jwtech.tw.doubleLanDic;

/**
 * @author TW
 * @date Administrator on 2016/11/23.
 */
public class JaZhDic
        extends AbstractDic {
    private static AbstractDic instance;
    public static synchronized void init() {
        if (instance == null) {
            instance = new JaZhDic();
        }
    }
    public static Dic getInstance() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    private JaZhDic() {
        super("data/dic/ja_zh.txt" );
    }
}
