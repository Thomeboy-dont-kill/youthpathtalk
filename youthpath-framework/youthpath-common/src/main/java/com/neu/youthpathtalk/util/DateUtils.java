package com.neu.youthpathtalk.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Julien
 * @time 2026/05/11 17:41
 * @description
 */
public class DateUtils {

    private static final List<DateTimeFormatter>
            FORMATTERS = List.of(

            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss"
            ),

            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss.S"
            ),

            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    );

    public static Long parseToMillis(
            String value
    ) {

        for (DateTimeFormatter formatter
                : FORMATTERS) {

            try {

                LocalDateTime time =
                        LocalDateTime.parse(
                                value,
                                formatter
                        );

                return time.atZone(
                        ZoneId.systemDefault()
                ).toInstant().toEpochMilli();

            } catch (Exception ignored) {

            }
        }

        throw new IllegalArgumentException(
                "无法解析时间: " + value
        );
    }
}
