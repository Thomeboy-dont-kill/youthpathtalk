package com.neu.youthpathtalk.post.biz.enums;

import com.neu.youthpathtalk.exception.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/22 22:54
 * @description post服务业务异常状态码
 */
@Getter
@RequiredArgsConstructor
public enum BizResponseErrorCode implements BaseException {
    AUTH_NOT_LOGIN("POST-20001","用户未登录"),
    AUTH_NOT_PERMISSION("POST-20002","无权限"),

    POST_NOT_OWNER("POST-20003","用户没有持有者权限"),
    POST_NOT_EXISTS_OR_DELETED("POST-20004","帖子不存在或已删除"),
    POST_NOT_EXISTS_OR_ABNORMAL("POST-20005","帖子不存在或状态异常"),

    POST_LIKE_CACHE_EXPIRED("POST-20006","点赞失败，请稍后重试"),
    POST_FAVORITE_CACHE_EXPIRED("POST-20007","收藏失败，请稍后重试"),

    COMMENT_NOT_EXISTS("POST-20008","评论不存在"),
    COMMENT_NOT_OWNER("POST-20009","用户没有持有者权限"),
    COMMENT_EDIT_EXPIRED("POST-20010","评论编辑功能失效"),
    COMMENT_LIKE_CACHE_EXPIRED("POST-20011","点赞失败，请稍后重试"),
    ;

    private final String errorCode;
    private final String errorMessage;
}
