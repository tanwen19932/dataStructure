package jwtech.tw.algo;

import static jwtech.tw.algo.Arith.*;

/**
 * @author TW
 * @date TW on 2016/11/25.
 */
public class MI {
    public static strictfp double MI(double[][] p) {
        double p__ = p[0][0] + p[0][1] + p[1][0] + p[1][1];
        double result = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                double a = div(p[i][j], p__);
                double b = 0;
                if (a != 0) {
                    b = div
                            (Math.log(div(p__ * p[i][j], (p[i][0] + p[i][1]) * (p[0][j] + p[1][j])))
                                    , Math.log(2));
                }
                result += mul(a, b);
            }
        }
        return result;
    }
}
