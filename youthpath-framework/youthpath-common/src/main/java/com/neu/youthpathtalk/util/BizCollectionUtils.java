package com.neu.youthpathtalk.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Julien
 * @time 2026/05/29 15:33
 * @description 如果不需要考虑其他的比如脏数据清理，就可以用这个替代手动遍历
 */
public class BizCollectionUtils {
    public static <K, V> List<V> reorder(
            List<K> orderedKeys,
            Map<K, V> map
    ) {

        List<V> result = new ArrayList<>();

        for (K key : orderedKeys) {

            V value = map.get(key);

            if (Objects.nonNull(value)) {
                result.add(value);
            }
        }

        return result;
    }
}
