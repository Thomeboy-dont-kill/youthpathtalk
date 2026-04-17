package com.neu.youthpathtalk.post.biz.lua;

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
    @Bean("postLikeScript")
    public DefaultRedisScript<List> postLikeScript(){
        DefaultRedisScript<List> script=new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/post_like.lua"));
        script.setResultType(List.class);
        return script;
    }
    @Bean("initPostLikeBitmapScript")
    public DefaultRedisScript<Void> initPostLikeBitmapScript(){
        DefaultRedisScript<Void> script=new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/post_like_bitmap_batch_setBit.lua"));
        script.setResultType(Void.class);
        return script;
    }

    @Bean("releaseLockScript")
    public DefaultRedisScript<Long> releaseLockScript(){
        DefaultRedisScript<Long> script=new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/releaseLock.lua"));
        script.setResultType(Long.class);
        return script;
    }

    @Bean("incrementAndExpireScript")
    public DefaultRedisScript<Long> incrementAndExpireScript(){
        DefaultRedisScript<Long> script=new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/incrementAndExpire.lua"));
        script.setResultType(Long.class);
        return script;
    }

    @Bean("addRecentViewScript")
    public DefaultRedisScript<Void> addRecentViewScript(){
        DefaultRedisScript<Void> script=new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/post_view_record_ZSet_addRecentView.lua"));
        script.setResultType(Void.class);
        return script;
    }
}
