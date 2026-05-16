package com.neu.youthpathtalk.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/05/13 12:53
 * @description
 */
@Getter
@RequiredArgsConstructor
public enum SearchSyncOperation {
    INSERT(),
    UPDATE(),
    DELETE(),
    ;
}
