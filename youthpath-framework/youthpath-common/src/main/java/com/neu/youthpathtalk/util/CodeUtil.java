package com.neu.youthpathtalk.util;

import java.security.SecureRandom;

public class CodeUtil {
    private CodeUtil(){}
    public static String generateCode(){
        return String.format("%06d",new SecureRandom().nextInt(1000000));
    }
}
