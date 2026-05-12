package com.neu.youthpathtalk.lua;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/23 18:00
 * @description lua脚本,单例防止redis因为不同SHA1产生重复的新缓存
 */
@Component
public class LuaScriptConfig {
    @Bean("incrementAndExpireScript")
    public DefaultRedisScript<Long> incrementAndExpireScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/incrementAndExpire.lua"));
        script.setResultType(Long.class);
        return script;
    }
}
