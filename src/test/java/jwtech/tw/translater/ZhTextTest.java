package jwtech.tw.translater;

import junit.framework.TestCase;
import jwtech.tw.tree.trie.dat.DoubleArrayTrie;

/**
 * @author TW
 * @date Administrator on 2016/11/18.
 */
public class ZhTextTest
        extends TestCase {
    public void testGetSeg()
            throws Exception {
        DoubleArrayTrie doubleArrayTrie = new DoubleArrayTrie();
        ZhText zhText = new ZhText("工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作");
        zhText.genSeg();
        for (String temp : zhText.segText.split(",")) {
            doubleArrayTrie.Insert(temp);
        }
        for (String temp : zhText.segText.split(",")) {
            System.out.println(temp + "是否存在 : " + doubleArrayTrie.Exists(temp));
        }
        System.out.println(zhText.segText);
    }
}