package jwtech.tw.model;

import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * @author TW
 * @date Administrator on 2016/11/23.
 */
public class TF_IDFModel {

    public static void tf_idfFromFile(String fileStr)
            throws IOException {
        CountMap<String> wordDocNumMap = new CountMap<>();
        CountMap<String> wordTotalNumMap = new CountMap<>();
        long totalDocSize = 0;
        long totalWordSize = 0;

        BufferedReader br = Files.newReader( new File( fileStr ), Charset.forName( "utf-8" ) );
        String line;
        int lineNum  = 0;
        while ((line = br.readLine()) != null) {
            lineNum++;
            System.out.println("读取到：第"+lineNum);
            Document doc = new Document();
            if (line.trim().length() == 0) {
                break;
            }
            String[] pairs = line.split( "\t" );
            for (String pair : pairs) {
                totalWordSize++;
                String key = pair.split( " " )[0];
                long value = Long.parseLong( pair.split( " " )[1] );
                wordDocNumMap.add(key);
                wordTotalNumMap.add(key,value);
                doc.add( key, value );
            }
            totalDocSize++;
        }
        br.close();
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat( "#,##0.0000000" );//保留两位小数且不用科学计数法

        for (Map.Entry<String, Long> entry : wordTotalNumMap.entrySet()) {
            String word = entry.getKey();
            long wordCount = entry.getValue();
            long wordDocCount = wordDocNumMap.get( entry.getKey() );
            double tf = jwtech.tw.algo.TF_IDF.tf( wordCount, totalWordSize );
            double idf = jwtech.tw.algo.TF_IDF.idf( wordDocCount , totalDocSize );
            double tf_idf = jwtech.tw.algo.TF_IDF.tf_idf( tf, idf );
            sb.append( word ).append( "\t" )
                    .append( wordCount ).append( "\t" )
                    .append( totalWordSize ).append( "\t" )
                    .append( wordDocCount ).append( "\t" )
                    .append( totalDocSize ).append( "\t" )
                    .append( "tf\t" ).append( df.format( tf ) ).append( "\t" )
                    .append( "idf\t" ).append( df.format( idf ) ).append( "\t" )
                    .append( "tf_idf\t" ).append( df.format( tf_idf ) )
                    .append( "\r\n" );
        }
        Files.append( sb, new File( "data/model/all_in_2" ), Charset.defaultCharset() );
    }
    public static void main(String[] args)
            throws IOException {
        tf_idfFromFile( "I:/ja_all/all" );

        //System.out.println(TF_IDF.class.getResource( "/0" ));
        //System.out.println(TF_IDF.class.getClassLoader().getResource( "conf/0" ));
    }

    public double getWordTF_IDF(String word) {
        // TODO: 2016/11/25
        return 0;
    }
}
