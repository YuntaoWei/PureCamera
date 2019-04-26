package com.pure.camera.filter;

import com.pure.camera.common.LogPrinter;
import com.pure.camera.filter.engine.GrayFilter;
import com.pure.camera.filter.engine.MosaicFilter;
import com.pure.camera.filter.engine.ReliefFilter;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class CameraFilterManager {

    private static final String TAG = "CameraFilterManager";
    public static final String FILTER_NAME_GRAY = GrayFilter.NAME;
    public static final String FILTER_NAME_RELIF = ReliefFilter.NAME;
    public static final String FILTER_NAME_MOSAIC = MosaicFilter.NAME;
    public static final String FILTER_NAME_ORIGINAL = "OriginalFilter";

    Map<String, BaseFilter> filters = new HashMap<>();

    private static CameraFilterManager INSTANCE;

    public static CameraFilterManager getInstance() {
        if(null == INSTANCE) {
            INSTANCE = new CameraFilterManager();
        }

        return INSTANCE;
    }

    private CameraFilterManager() {}

    public BaseFilter getFilter(String filterName) {
        BaseFilter filter = filters.get(filterName);
        if(null == filter) {
            filter = createFilter(filterName);
            filters.put(filterName, filter);
        }

        return filter;
    }

    private BaseFilter createFilter(String name) {
        String className = "com.pure.camera.filter.engine." + name;
        try {
            Class clazz = Class.forName(className);
            return (BaseFilter) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LogPrinter.e(TAG, "ClassNotFoundException : " + className);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LogPrinter.e(TAG, "IllegalAccessException : " + className);
        } catch (InstantiationException e) {
            e.printStackTrace();
            LogPrinter.e(TAG, "InstantiationException : " + className);
        }

        return null;
    }


}
