package jwtech.tw.algo;

import junit.framework.TestCase;

/**
 * @author TW
 * @date TW on 2016/11/26.
 */
public class MITest extends TestCase {
    public void testMI() throws Exception {
    double[][] n = new double[2][2];
    n[0][0]=774106;
    n[0][1]=141;
    n[1][0]=27652;
    n[1][1]=49;
    System.out.println(MI.MI(n));
    }

}