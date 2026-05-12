package com.neu.youthpathtalk.interceptor;

import com.neu.youthpathtalk.holder.LoginUserContextHolder;
import com.neu.youthpathtalk.rateLimiter.SlidingWindowRateLimiter;
import com.neu.youthpathtalk.rateLimiter.UserRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author Julien
 * @time 2026/05/12 15:22
 * @description
 */
@Component
@RequiredArgsConstructor
public class SearchInterceptor implements HandlerInterceptor {
    private final UserRateLimiter rateLimiter;
    private final SlidingWindowRateLimiter slidingWindowRateLimiter;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String ip=getRealIp(request);
        Long userId= LoginUserContextHolder.getUserId();
        //可以只留一个
        slidingWindowRateLimiter.checkLimit(userId, ip);
        rateLimiter.checkLimit(userId,ip);
        return true;
    }

    private String getRealIp(HttpServletRequest request) {

        String xff = request.getHeader("X-Forwarded-For");

        if (xff != null
                && !xff.isBlank()
                && !"unknown".equalsIgnoreCase(xff)) {

            return xff.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");

        if (realIp != null
                && !realIp.isBlank()
                && !"unknown".equalsIgnoreCase(realIp)) {

            return realIp;
        }

        return request.getRemoteAddr();
    }
}
