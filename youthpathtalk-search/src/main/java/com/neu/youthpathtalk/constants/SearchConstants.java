package com.neu.youthpathtalk.constants;

/**
 * @author Julien
 * @time 2026/05/10 14:43
 * @description
 */
public final class SearchConstants {
    private SearchConstants() {}

    public static final String FIELD_TITLE = "title";

    public static final String FIELD_TITLE_PINYIN = "title_pinyin";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_BOARDTYPE = "boardType";
    public static final String FIELD_USERID = "userId";
    public static final double TITLE_BOOST = 4.0;
    public static final double TITLE_PINYIN_BOOST = 2.0;
    public static final String FIELD_TITLE_WITH_BOOST = FIELD_TITLE + "^" + (int) TITLE_BOOST;
    public static final String FIELD_TITLE_PINYIN_WITH_BOOST = FIELD_TITLE_PINYIN + "^" + (int) TITLE_PINYIN_BOOST;
    public static final String MINIMUM_SHOULD_MATCH = "60%";
    public static final String FUZZINESS_AUTO = "AUTO";
    public static final int MAX_CONTENT_LENGTH = 5000;
}
