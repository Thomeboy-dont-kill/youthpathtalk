package com.neu.youthpathtalk.auth.lua;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/03/09 10:18
 * @description lua脚本,单例防止redis因为不同SHA1产生重复的新缓存
 */
@Component
public class LuaScriptConfig {
    @Bean("verifyCodeValidateScript")
    public DefaultRedisScript<Long> verifyCodeValidateScript(){
        DefaultRedisScript<Long> script=new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/verify_code_validate.lua"));
        script.setResultType(Long.class);
        return script;
    }
}
