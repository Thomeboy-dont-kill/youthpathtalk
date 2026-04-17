package com.neu.youthpathtalk.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.youthpathtalk.constant.GlobalConstans;
import com.neu.youthpathtalk.gateway.enums.BizResponseErrorCode;
import com.neu.youthpathtalk.response.Response;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static cn.dev33.satoken.SaManager.log;

/**
 * @author Julien
 * @time 2026/03/11 14:03
 * @description 权限认证全局过滤器
 */
@Component
public class AuthGlobalFilter implements GlobalFilter {
    private static final List<String> WHITE_LIST=List.of(
            "/auth",
            "/code"
    );
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request=exchange.getRequest();
        String path=request.getURI().getPath();

        //白名单放行
        if (WHITE_LIST.stream().anyMatch(path::startsWith)){
            return chain.filter(exchange);
        }

        //获取UserId注入请求头(下游服务直接使用)
        Long userId=StpUtil.getLoginIdAsLong();

        //权限校验
        List<String> pathList = (List<String>)StpUtil.getSession().get("pathList");
        if (!pathList.stream().anyMatch(path::equals)){//暂时用equals
            return forbidden(exchange,Response.fail(BizResponseErrorCode.USER_NOT_ALLOWED));
        }

        //透传userId到下游服务
        ServerHttpRequest newRequest=request.mutate()
                .header(GlobalConstans.USER_ID,String.valueOf(userId))
                .build();
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    /**
     * 未授权访问响应
     */
    private Mono<Void> forbidden(ServerWebExchange exchange, Response response){
        return writeJsonResponse(exchange,
                HttpStatus.FORBIDDEN,
                response);
    }
    /**
     * 通用 JSON 响应
     */
    private Mono<Void> writeJsonResponse(ServerWebExchange exchange,
                                         HttpStatus status,
                                         Object body) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
        response.getHeaders().set("Pragma", "no-cache");
        response.getHeaders().set("Expires", "0");

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(body);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("响应写入失败", e);
            return response.setComplete();
        }
    }

}
