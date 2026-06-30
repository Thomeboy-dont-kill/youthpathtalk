package com.neu.youthpathtalk.user.biz.lua;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/06/02 11:47
 * @description
 */
@Component
public class LuaScriptConfig {
    @Bean("releaseLockScript")
    public DefaultRedisScript<Long> releaseLockScript(){
        DefaultRedisScript<Long> script=new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/releaseLock.lua"));
        script.setResultType(Long.class);
        return script;
    }
}
