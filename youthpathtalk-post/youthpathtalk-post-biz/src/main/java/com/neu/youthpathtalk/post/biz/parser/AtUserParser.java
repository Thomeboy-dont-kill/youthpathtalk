package com.neu.youthpathtalk.post.biz.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Julien
 * @time 2026/06/12 15:43
 * @description 用于从文本解析得到@提及的用户名
 */
/*
@Slf4j
@Component
public class AtUserParser {

    // 和用户名规则一致
    private static final Pattern AT_PATTERN =
            Pattern.compile("@([a-zA-Z0-9_\\-\\u4e00-\\u9fa5]{1,20})(?=[^a-zA-Z0-9_\\-\\u4e00-\\u9fa5]|$)");

    public Set<String> parse(String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptySet();
        }

        Set<String> result = new HashSet<>();

        Matcher matcher = AT_PATTERN.matcher(text);

        while (matcher.find()) {
            String member=matcher.group(1);
            log.info("提取的用户名：member={}",member);
            result.add(member);
        }

        return result;
    }
}
*/
