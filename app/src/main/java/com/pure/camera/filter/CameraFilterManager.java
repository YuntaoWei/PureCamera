package com.pure.camera.filter;

import android.text.TextUtils;

import com.pure.camera.common.LogPrinter;
import com.pure.camera.filter.engine.GrayFilter;
import com.pure.camera.filter.engine.MosaicFilter;
import com.pure.camera.filter.engine.NoFilter;
import com.pure.camera.filter.engine.PositiveColorFilter;
import com.pure.camera.filter.engine.ReliefFilter;
import com.pure.camera.filter.engine.WBFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraFilterManager {

    private static final String TAG = "CameraFilterManager";
    public static final String FILTER_NAME_GRAY = GrayFilter.NAME;
    public static final String FILTER_NAME_RELIF = ReliefFilter.NAME;
    public static final String FILTER_NAME_MOSAIC = MosaicFilter.NAME;
    public static final String FILTER_NAME_ORIGINAL = NoFilter.NAME;
    public static final String FILTER_NAME_WB = WBFilter.NAME;
    public static final String FILTER_NAME_POSITIVE = PositiveColorFilter.NAME;

    public static final String[] ALL_FILTER_NAME = {
            FILTER_NAME_ORIGINAL,
            FILTER_NAME_GRAY,
            FILTER_NAME_RELIF,
            FILTER_NAME_MOSAIC,
            FILTER_NAME_WB,
            FILTER_NAME_POSITIVE
    };

    Map<String, BaseFilter> filters = new HashMap<>();

    private static CameraFilterManager INSTANCE;

    public static CameraFilterManager getInstance() {
        if(null == INSTANCE) {
            INSTANCE = new CameraFilterManager();
        }

        return INSTANCE;
    }

    private CameraFilterManager() {}

    public List<BaseFilter> getAllFilter() {
        List<BaseFilter> filters = new ArrayList<>(ALL_FILTER_NAME.length);
        for (String filter : ALL_FILTER_NAME
             ) {
            filters.add(getFilter(filter));
        }

        return filters;
    }

    public BaseFilter getFilterByIndex(int index) {
        if(filters.size() < index)
            return null;

        return filters.get(ALL_FILTER_NAME[index]);
    }

    public BaseFilter getFilter(String filterName) {
        if(TextUtils.isEmpty(filterName)) {
            return null;
        }
        BaseFilter filter = filters.get(filterName);
        if(null == filter) {
            filter = createFilter(filterName);
            filters.put(filterName, filter);
        }

        return filter;
    }

    public int getFilterIndex(String name) {
        for(int i = 0; i < ALL_FILTER_NAME.length; i++) {
            if(ALL_FILTER_NAME[i].equals(name))
                return i;
        }

        return -1;
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
