package com.android.picshow.editorui.utils;

import android.app.Fragment;
import android.os.Bundle;

import com.android.picshow.editor.BaseEditorManager;
import com.android.picshow.editor.filters.BaseEditor;
import com.android.picshow.editorui.CropFragment;
import com.android.picshow.editorui.DrawBaseFragment;
import com.android.picshow.editorui.EnhanceFragment;
import com.android.picshow.editorui.FilterFragment;
import com.android.picshow.editorui.MosaicFragment;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by yuntao.wei on 2018/5/15.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class FragmentUtils {

    public static Map<String, BaseEditor> fragments = new WeakHashMap<>();
    private static final String KEY = "editor:";

    public static BaseEditor getFragment(int type, Bundle b) {
        BaseEditor f = fragments.get(getKey(type));

        if(f != null)
            return f;

        switch (type) {

            case BaseEditorManager.FILTER:
                f = new FilterFragment();
                break;

            case BaseEditorManager.CROP:
                f = new CropFragment();
                break;


            case BaseEditorManager.BEAUTY:

                break;

            case BaseEditorManager.ENHANCE:
                f = new EnhanceFragment();
                break;

            case BaseEditorManager.MOSAIC:
                f = new MosaicFragment();
                break;

            case BaseEditorManager.PAINT:
                f = new DrawBaseFragment();
                break;


        }

        if(f != null) {
            ((Fragment)f).setArguments(b);
        }
        fragments.put(getKey(type), f);

        return f;
    }

    public static String getKey(int type) {
        return KEY + type;
    }


}
