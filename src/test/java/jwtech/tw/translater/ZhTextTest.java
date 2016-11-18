package jwtech.tw.translater;

import junit.framework.TestCase;

/**
 * @author TW
 * @date Administrator on 2016/11/18.
 */
public class ZhTextTest
        extends TestCase {
    public void testGetSeg()
            throws Exception {
        ZhText zhText = new ZhText( "工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作" );
        zhText.genSeg();
        System.out.println(zhText.segText);
    }
}