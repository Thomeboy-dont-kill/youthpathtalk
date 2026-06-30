package com.neu.youthpathtalk.constants;

/**
 * @author Julien
 * @time 2026/05/10 14:43
 * @description
 */
public final class SearchConstants {

    private SearchConstants() {}

    /*
     * index
     */
    public static final String INDEX_POST = "post_index";

    /*
     * field
     */
    public static final String FIELD_ID = "id";

    public static final String FIELD_TITLE = "title";

    public static final String FIELD_TITLE_PINYIN =
            "title.pinyin";

    public static final String FIELD_TITLE_TRIGRAM =
            "title.trigram";

    public static final String FIELD_CONTENT = "content";

    public static final String FIELD_BOARDTYPE = "boardType";

    public static final String FIELD_CREATETIME = "createTime";

    public static final String FIELD_VIEWCOUNT = "viewCount";

    public static final String FIELD_TITLESUGGEST =
            "titleSuggest";

    public static final String FIELD_TITLESUGGEST_PINYIN =
            "titleSuggest.pinyin";

    /*
     * boost
     */
    public static final double TITLE_BOOST = 5.0;

    public static final double TITLE_PINYIN_BOOST = 1.0;

    public static final String FIELD_TITLE_WITH_BOOST =
            FIELD_TITLE + "^" + (int) TITLE_BOOST;

    public static final String FIELD_TITLE_PINYIN_WITH_BOOST =
            FIELD_TITLE_PINYIN + "^" + (int) TITLE_PINYIN_BOOST;

    /*
     * search
     */
    public static final String MINIMUM_SHOULD_MATCH = "60%";

    public static final String FUZZINESS_AUTO = "AUTO";

    public static final int MAX_CONTENT_LENGTH = 5000;

    /*
     * aggregation
     */
    public static final String BOARD_TYPE_AGG =
            "boardTypeAgg";

    public static final String CREATE_TIME_AGG =
            "createTimeAgg";

    /*
     * suggest
     */
    public static final String SUGGEST_CORRECTION_NAME =
            "correctionSuggest";

    public static final int SUGGEST_SIZE = 1;

    public static final double SUGGEST_MAX_ERRORS = 2.0;

    public static final double SUGGEST_CONFIDENCE = 0.0;
    public static final int SEARCH_HISTORY_MAX_COUNT = 20;

    public static final int SEARCH_HISTORY_MAX_LENGTH = 50;
}
