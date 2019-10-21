package com.mandy.util;

import java.util.UUID;

/**
 * Created by MandyOu on 2019/10/21
 */
public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
