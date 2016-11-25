package jwtech.tw.model;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author TW
 * @date TW on 2016/11/11.
 */
public class GuavaFile {

    //	public static void seg(String dirStr ,String toDirStr) throws IOException {
    //		File dir = new File(dirStr);
    //		File[] files = dir.listFiles();
    //		for (File file : files) {
    //			for (String line : Files.readLines(file, Charset.forName("utf-8"))) {
    //				try {
    //					if (LanguageUtil.getInstance().detect(line).equals("ja")) {
    //						Map preJo = new HashMap();
    //						String param  = HtmlUtil.htmlRemoveTag(line).replaceAll("(・|【|】|�|[a-zA-Z0-9]|\\.|『|』|\\(|\\))", " ");
    //						preJo.put("text", URLEncoder.encode(param ,"UTF-8"));
    //						String jaLine = HttpUtil.doGet("http://localhost:8080/token", preJo);
    //						JSONObject jsonObject = new JSONObject(jaLine);
    //						String out = jsonObject.getString("tgtl")+ "\r\n";
    ////						System.out.println(out);
    //						Files.append(out, new File(toDirStr+file.getName()), Charset.forName("utf-8"));
    //					} else {
    //						break;
    //					}
    //				} catch (Exception e) {
    //					e.printStackTrace();
    //				}
    //			}
    //		}
    //	}
    static Set<Character.UnicodeBlock> japaneseUnicodeBlocks = new HashSet<Character.UnicodeBlock>() {{
        add( Character.UnicodeBlock.HIRAGANA );
        add( Character.UnicodeBlock.KATAKANA );
        add( Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS );
    }};

    public static void wf(String dirStr, String toDirStr)
            throws IOException {
        File dir = new File( dirStr );
        File[] files = dir.listFiles();
        int total = files.length;
        int now = 0;
        for (File file : files) {
            Map<String, Integer> wordMap = new TreeMap<>();
            for (String line : Files.readLines( file, Charset.forName( "utf-8" ) )) {
                String[] words = line.split( "\\s" );
                for (String word : words) {
                    word = word.replaceAll( "[,.，。、]", "" ).trim();
                    word = word.replaceAll( "\\p{Punct}", "" ).trim();
                    word = word.replaceAll( "\\pP", "" ).trim();
                    word = word.replaceAll( "\\p{P}", "" ).trim();
                    word = word.replaceAll( "[\\pP‘’“”]", "" ).trim();
                    if (word.replaceAll( "", "" ).trim().length() == 0) {
                        continue;
                    }
                    if (wordMap.containsKey( word )) {
                        Integer a = wordMap.get( word );
                        Integer aNew = new Integer( a.intValue() + 1 );
                        a = null;
                        wordMap.put( word, aNew );
                    } else {
                        wordMap.put( word, 1 );
                    }
                }
            }
            now++;
            for (Map.Entry entry : wordMap.entrySet()) {
                String out = entry.getKey() + " " + entry.getValue() + "\r\n";
                System.out.println( now + "/" + total + " file:" + file.getName() );
                Files.append( out, new File( toDirStr + file.getName() ), Charset.forName( "utf-8" ) );
            }
        }

    }

    public static void reduce(String dirStr, String toDirStr)
            throws IOException {
        File dir = new File( dirStr );
        File[] files = dir.listFiles();
        int total = files.length;
        int now = 0;
        for (File file : files) {
            StringBuilder sb = new StringBuilder();
            Label2:
            for (String line : Files.readLines( file, Charset.forName( "utf-8" ) )) {
                if (line.replaceAll( "\\s*", "" ).trim().length() == 0) {
                    continue;
                }
                String[] pair = line.split( " " );
                try {
                    String word = pair[0];
                    for (char c : word.toCharArray()) {
                        if (japaneseUnicodeBlocks.contains( Character.UnicodeBlock.of( c ) )) {
                        } else {
                            System.out.println( c + " is not a Japanese character" );
                            continue Label2;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                sb.append( line + "\t" );
            }
            if (sb.length() > 0) {
                sb.append( "\r\n" );
                Files.append( sb, new File( toDirStr + "all" ), Charset.forName( "utf-8" ) );
            }
            now++;
            //if(now == 2) break;
            System.out.println( now + "/" + total + " file:" + file.getName() );
        }
    }

    //public static void totalCount(String fileStr)
    //        throws IOException {
    //    BufferedReader br = Files.newReader( new File( fileStr ), Charset.forName( "utf-8" ) );
    //    String line;
    //    CountMap totalMap = new CountMap( true );
    //    long totalDocSize = 0;
    //    try {
    //        totalDocSize = 0;
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //    long totalWordSize = 0;
    //    while ((line = br.readLine()) != null) {
    //        CountMap lineMap = new CountMap();
    //        if (line.trim().length() == 0) {
    //            break;
    //        }
    //        String[] pairs = line.split( "\t" );
    //        for (String pair : pairs) {
    //            String key = pair.split( " " )[0];
    //            long value = Long.parseLong( pair.split( " " )[1] );
    //            totalWordSize += value;
    //            lineMap.add( key, value );
    //        }
    //        totalMap.add( lineMap );
    //        totalDocSize += 1;
    //        //if (totalDocSize > 10) break;
    //    }
    //    br.close();
    //    CountMap docMap = totalMap.getDocCountMap();
    //    StringBuilder sb = new StringBuilder();
    //    DecimalFormat df = new DecimalFormat( "#,##0.0000000" );//保留两位小数且不用科学计数法
    //    for (Map.Entry<String, Long> entry : totalMap.entrySet()) {
    //        String word = entry.getKey();
    //        long wordCount = entry.getValue();
    //        long wordDocCount = docMap.get( entry.getKey() );
    //        double tf = TF_IDF.tf( wordCount, totalWordSize );
    //        double idf = TF_IDF.idf( wordDocCount, totalDocSize );
    //        double tf_idf = TF_IDF.tf_idf( tf, idf );
    //        sb.append( word ).append( "\t" )
    //                .append( wordCount ).append( "\t" )
    //                .append( totalWordSize ).append( "\t" )
    //                .append( wordDocCount ).append( "\t" )
    //                .append( totalDocSize ).append( "\t" )
    //                .append( "tf\t" ).append( df.format( tf ) ).append( "\t" )
    //                .append( "idf\t" ).append( df.format( idf ) ).append( "\t" )
    //                .append( "tf_idf\t" ).append( df.format( tf_idf ) )
    //                .append( "\r\n" );
    //    }
    //    Files.append( sb, new File( "I:/ja_all/all_in_1" ), Charset.defaultCharset() );
    //}

    public static void main(String[] args)
            throws IOException {
        //		seg("I:\\ja", "I:/ja_segment/");
        //		wf("I:/ja_segment/", "I:/ja_WF/");
        //reduce( "I:/ja_WF/", "I:/ja_all/" );
        //totalCount( "I:/ja_all/all" );
    }
}
