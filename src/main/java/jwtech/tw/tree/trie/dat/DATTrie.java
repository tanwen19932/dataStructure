package jwtech.tw.tree.trie.dat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TW
 * @date Administrator on 2016/11/22.
 */
public class DATTrie {
    static final char ENDSTATE='\0';
    int[] base;
    int[] check;
    Map<Character, Integer> charMap = new HashMap<>();

    public DATTrie() {
        base = new int[100];
        check = new int[100];
        base[0] = 1;
        check[0] = 0;

    }

    private void Extend_Array() {
        base = Arrays.copyOf(base, base.length * 2);
        check = Arrays.copyOf(check, check.length * 2);
    }

    public void insert(String word) {
        word += ENDSTATE;
        char last = 0;
        int state = base[1];

        for (char ch : word.toCharArray()) {
            int t = base[state+ch];
            if(check[t]==0){
                check[t]=state;
            }
        }
    }

    public int exist(String word) {
        int state = base[1];
        char last = 0;
        for (char ch : word.toCharArray()) {
           int t = base[state+ch];
           if(check[t]==state){
               if(t == ENDSTATE){
                   return state;
               }
               state = t;
               continue;
           }
           else {
               return -1;
           }
        }
        return -1;
    }


}