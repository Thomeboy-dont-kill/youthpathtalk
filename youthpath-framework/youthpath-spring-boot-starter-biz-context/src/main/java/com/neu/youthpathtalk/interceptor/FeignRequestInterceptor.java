package com.neu.youthpathtalk.interceptor;

import com.neu.youthpathtalk.constant.GlobalConstans;
import com.neu.youthpathtalk.holder.LoginUserContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author Julien
 * @time 2026/03/04 21:25
 * @description Feign请求拦截器：在RPC之前从上下文中取出userId设置到请求头，传递给下游服务
 */
@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor{
    @Override
    public void apply(RequestTemplate requestTemplate) {
        Long userId= LoginUserContextHolder.getUserId();
        if (!Objects.isNull(userId)){
            requestTemplate.header(GlobalConstans.USER_ID,userId.toString());
            log.info("## FeignRequestInterceptor,userId: {}被设置到请求头中",userId);
        }
    }
}
