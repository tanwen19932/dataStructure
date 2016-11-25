package jwtech.tw.algo;

/**
 * @author TW
 * @date Administrator on 2016/11/23.
 */
public class TF_IDF{
    /**
     * @param word  单词出现次数
     * @param total 总共单词数
     *
     * @return tf值
     */
    public static strictfp double tf(long word, long total) {
        return Arith.div( word, total );
    }

    /**
     * @param wordDoc  关键词文档个数
     * @param totalDoc 总文档个数
     *
     * @return idf值
     */
    public static strictfp double idf(long wordDoc, long totalDoc) {
        return Math.log( Arith.div( totalDoc, wordDoc ) );
    }

    /**
     * @param word 单词出现次数
     * @param total 总共单词数
     * @param wordDoc 关键词文档个数
     * @param totalDoc 总文档个数
     *
     * @return tf_idf值
     */
    public static strictfp double tf_idf(long word, long total, long wordDoc, long totalDoc) {
        return Arith.mul( tf( word, total ), idf( wordDoc, totalDoc ) );
    }

    public static strictfp double tf_idf( double tf, double idf) {
        return Arith.mul( tf, idf );
    }
    public static void main(String[] args){
      System.out.println(idf( 1,10 ));
    }
}
