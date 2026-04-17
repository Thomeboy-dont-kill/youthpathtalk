package com.neu.youthpathtalk.holder;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.neu.youthpathtalk.constant.GlobalConstans;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Julien
 * @time 2026/03/04 20:03
 * @description 登录用户上下文持有者
 */
public class LoginUserContextHolder {
    //TTL 能实现线程间值的“继承”和“传递”
    private static final ThreadLocal<Map<String,Object>> LOGIN_USER_CONTEXT_THREAD_LOCAL= TransmittableThreadLocal.withInitial(HashMap::new);
    //设置userId
    public static void setUserId(Object userId){
        LOGIN_USER_CONTEXT_THREAD_LOCAL.get().put(GlobalConstans.USER_ID,userId);
    }
    //获取userId
    public static Long getUserId(){
        Object userId=LOGIN_USER_CONTEXT_THREAD_LOCAL.get().get(GlobalConstans.USER_ID);
        if (Objects.isNull(userId)){
            return null;
        }
        return Long.parseLong(userId.toString());
    }
    //删除ThreadLocal
    public static void remove(){LOGIN_USER_CONTEXT_THREAD_LOCAL.remove();}
}
