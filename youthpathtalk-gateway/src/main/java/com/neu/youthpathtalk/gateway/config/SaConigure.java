package com.neu.youthpathtalk.gateway.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.stp.StpUtil;
import com.neu.youthpathtalk.enums.CommonResponseErrorCode;
import com.neu.youthpathtalk.gateway.enums.BizResponseErrorCode;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.util.JsonUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cn.dev33.satoken.exception.NotLoginException;
/**
 * @author Julien
 * @time 2026/03/20 16:22
 * @description 基础登录校验
 */
@Configuration
public class SaConigure {
    @Bean
    public SaReactorFilter saReactorFilter(){
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude("/auth/**","/code/**")
                .setAuth(obj-> StpUtil.checkLogin())
                .setError(e -> {
                    // 获取响应对象
                    SaHolder.getResponse()
                            .setHeader("Content-Type", "application/json;charset=UTF-8");

                    if (e instanceof NotLoginException) {
                        // 未登录异常
                        SaHolder.getResponse().setStatus(401);
                        return JsonUtils.toJsonString(
                                Response.fail(BizResponseErrorCode.USER_NOT_LOGIN)
                        );
                    } else {
                        // 其他异常
                        SaHolder.getResponse().setStatus(500);
                        return JsonUtils.toJsonString(
                                Response.fail(CommonResponseErrorCode.SYSTEM_ERROR)
                        );
                    }
                });
    }
}
