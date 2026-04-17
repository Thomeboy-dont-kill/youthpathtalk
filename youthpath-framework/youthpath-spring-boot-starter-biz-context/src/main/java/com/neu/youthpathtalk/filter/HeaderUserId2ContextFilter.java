package com.neu.youthpathtalk.filter;

import com.neu.youthpathtalk.constant.GlobalConstans;
import com.neu.youthpathtalk.holder.LoginUserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;

/**
 * @author Julien
 * @time 2026/03/04 19:53
 * @description 提取请求头中的用户ID保存到上下文中，以便后续使用
 */
@Slf4j
public class HeaderUserId2ContextFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //从请求头中获取userId
        String userId=request.getHeader(GlobalConstans.USER_ID);
        log.info("## HeaderUserId2ContextFilter,userId: {}",userId);
        //判断请求头中是否存在userId
        if (StringUtils.isBlank(userId)){
            filterChain.doFilter(request,response);
            return;
        }
        LoginUserContextHolder.setUserId(userId);
        try{
            filterChain.doFilter(request,response);
        }
        finally {
            LoginUserContextHolder.remove();
        }
    }
}
