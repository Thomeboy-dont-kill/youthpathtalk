package com.neu.youthpathtalk.constants;

import java.util.concurrent.TimeUnit;

/**
 * @author Julien
 * @time 2026/05/10 21:30
 * @description
 */
public final class CanalConstants {
    private CanalConstants(){}

    public static final int DEFAULT_BATCH_SIZE = 100;
    public static final Long DEFAULT_GET_TIMEOUT = 5L;
    public static final TimeUnit DEFAULT_GET_TIMEOUT_UNIT = TimeUnit.SECONDS;

    public static final long AWAIT_TERMINATION_TIMEOUT=5L;
    public static final TimeUnit AWAIT_TERMINATION_TIMEOUT_UNIT=TimeUnit.SECONDS;
}
