package jwtech.tw.model;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author TW
 * @date Administrator on 2016/11/23.
 */
public class CountMap<T>
        extends TreeMap<T, Long> {
    public CountMap() {
    }

    public void add(CountMap<T> otherMap) {
        for (Map.Entry<T, Long> entry : otherMap.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
    }

    public void add(T key) {
        add(key, 1);
    }

    public void add(T key, long value) {
        if (this.containsKey(key)) {
            long now = this.get(key);
            long then = now + value;
            this.put(key, then);
        } else {
            this.put(key, value);
        }
    }

}
