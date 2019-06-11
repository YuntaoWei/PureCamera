package com.pure.camera.common;

public class Assert {

    public static void assertNotNull(Object... object) {
        for (Object o : object
             ) {
            if(null == object)
                throw new RuntimeException("Object is null!");
        }
    }

    public static boolean objectNotNull(Object... object) {
        for (Object o : object
                ) {
            if(null == object)
                return false;
        }

        return object != null;
    }

}