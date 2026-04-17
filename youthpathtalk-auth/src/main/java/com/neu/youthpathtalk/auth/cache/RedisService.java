package com.neu.youthpathtalk.auth.cache;

import com.neu.youthpathtalk.constant.redis.UserRedisKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author Julien
 * @time 2026/03/09 8:44
 * @description 提供redis的服务
 */
@Component
public class RedisService {
    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<Long> verifyCodeValidateScript;

    public RedisService(
            StringRedisTemplate stringRedisTemplate,
            @Qualifier("verifyCodeValidateScript")
            DefaultRedisScript<Long> verifyCodeValidateScript) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.verifyCodeValidateScript = verifyCodeValidateScript;
    }

    public boolean validateVerifyCode(String verifyCodeKey,String inputCode){
        Long result=stringRedisTemplate.execute(verifyCodeValidateScript, Collections.singletonList(verifyCodeKey),inputCode.trim());
        return result==1;
    }
    public Long getVerifyCodeTTL(String verifyCodeKey){
        return stringRedisTemplate.getExpire(verifyCodeKey,UserRedisKey.VERIFY_CODE_TTL_UNIT);
    }
    public void storeVerifyCode(String verifyCodeKey,String verifyCode){
        stringRedisTemplate.opsForValue().set(verifyCodeKey,verifyCode,UserRedisKey.VERIFY_CODE_TTL,UserRedisKey.VERIFY_CODE_TTL_UNIT);
    }
    public boolean hasKey(String key){
        Boolean result=stringRedisTemplate.hasKey(key);
        return Boolean.TRUE.equals(result);
    }
}
