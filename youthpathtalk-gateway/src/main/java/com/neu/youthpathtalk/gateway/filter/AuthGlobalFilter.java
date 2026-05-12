package com.neu.youthpathtalk.gateway.filter;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.youthpathtalk.constant.GlobalConstans;
import com.neu.youthpathtalk.gateway.constants.AuthConstants;
import com.neu.youthpathtalk.gateway.enums.BizResponseErrorCode;
import com.neu.youthpathtalk.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Julien
 * @time 2026/03/11 14:03
 * @description 权限认证全局过滤器
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter {
//    private static final List<String> WHITE_LIST=List.of(
//            "/auth",
//            "/code"
//    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request=exchange.getRequest();
        String path=request.getURI().getPath();

        //OPTIONS请求直接放行（CORS预检）
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        //白名单放行
//        if (WHITE_LIST.stream().anyMatch(path::startsWith)){
//            return chain.filter(exchange);
//        }

        try {
            //从Cookie获取token
            String token = null;
            HttpCookie cookie = request.getCookies().getFirst(AuthConstants.TOKEN_NAME);
            if (cookie != null) {
                token = cookie.getValue();
            }

            log.debug("网关认证 - path: {}, token: {}", path, token != null ? "存在" : "不存在");

            if (token == null || token.isEmpty()) {
                return chain.filter(exchange);
            }

            //通过token获取userId（不依赖ThreadLocal上下文）
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId == null) {
                return chain.filter(exchange);
            }

            Long userId = Long.parseLong(loginId.toString());
            log.debug("网关认证成功 - userId: {}, path: {}", userId, path);

            //透传userId到下游服务
            ServerHttpRequest newRequest=request.mutate()
                    .header(GlobalConstans.USER_ID,String.valueOf(userId))
                    .build();
            return chain.filter(exchange.mutate().request(newRequest).build());
        } catch (Exception e) {
            log.error("网关认证异常 - path: {}, error: {}", path, e.getMessage(), e);
            return chain.filter(exchange);
        }
    }

//    private Mono<Void> unauthorized(ServerWebExchange exchange) {
//        return writeJsonResponse(exchange, HttpStatus.UNAUTHORIZED,
//                Response.fail(BizResponseErrorCode.USER_NOT_LOGIN));
//    }

    /**
     * 通用 JSON 响应
     */
//    private Mono<Void> writeJsonResponse(ServerWebExchange exchange,
//                                         HttpStatus status,
//                                         Object body) {
//        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(status);
//        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//        response.getHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
//        response.getHeaders().set("Pragma", "no-cache");
//        response.getHeaders().set("Expires", "0");
//
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            String json = mapper.writeValueAsString(body);
//            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
//            DataBuffer buffer = response.bufferFactory().wrap(bytes);
//            return response.writeWith(Mono.just(buffer));
//        } catch (Exception e) {
//            log.error("响应写入失败", e);
//            return response.setComplete();
//        }
//    }

}
