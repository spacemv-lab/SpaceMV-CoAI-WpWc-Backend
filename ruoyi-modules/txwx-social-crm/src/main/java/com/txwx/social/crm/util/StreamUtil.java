/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.util;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtil {

    public static boolean isListEqualIgnoreOrder(List<Long> list1, List<Long> list2) {
        if (list1 == list2) {
            return true;
        }
        if (list1 == null || list2 == null) {
            return false;
        }
        if (list1.size() != list2.size()) {
            return false;
        }

        // 使用 Stream API 分组计数
        Map<Long, Long> countMap1 = list1.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<Long, Long> countMap2 = list2.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return countMap1.equals(countMap2);
    }

    public static List<Long> unionDistinct(List<Long> list1, List<Long> list2) {
        if (CollectionUtils.isEmpty(list1) && CollectionUtils.isEmpty(list2)) {
            return new ArrayList<>();
        }

        if (CollectionUtils.isEmpty(list1)) {
            return new ArrayList<>(list2);
        }

        if (CollectionUtils.isEmpty(list2)) {
            return new ArrayList<>(list1);
        }

        return Stream.concat(list1.stream(), list2.stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
