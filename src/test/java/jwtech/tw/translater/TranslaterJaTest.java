package jwtech.tw.translater;

import junit.framework.TestCase;

/**
 * @author TW
 * @date TW on 2016/11/30.
 */
public class TranslaterJaTest extends TestCase {
    public void testGetSingleTransWord() throws Exception {
        TranslaterJa translaterJa = new TranslaterJa();
        System.out.println( translaterJa.trans("肥沃") );
    }

    public void testGetMulitWordTrans() throws Exception {
        TranslaterJa translaterJa = new TranslaterJa();
        System.out.println(translaterJa.trans("肥沃的土地"));
    }

}