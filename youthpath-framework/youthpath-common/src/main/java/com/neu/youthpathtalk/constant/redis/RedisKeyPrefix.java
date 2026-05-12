package com.neu.youthpathtalk.constant.redis;

/**
 * @author Julien
 * @time 2026/03/09 9:14
 * @description RedisKey前缀常量
 */
public final class RedisKeyPrefix {
    private RedisKeyPrefix(){};

    //分隔符
    public static final String SEPARATOR=":";

    //业务域
    public static final String USER="user";
    public static final String POST="post";
    public static final String SEARCH="search";

    //功能类型
    public static final String VERIFY="verify";
    public static final String PAGE="page";
    public static final String LIKE="like";
    public static final String VIEW="view";
    public static final String HOT="hot";
    public static final String LIMIT="limit";

    //标识
    public static final String LOCK = "lock";
    public static final String TEMP = "temp";
    public static final String SLIDE = "slide";
}
