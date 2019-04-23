package com.pure.camera.common;

public class Assert {

    public static void assertNotNull(Object object) {
        if(null == object)
            throw new RuntimeException("Object is null!");
    }

}
