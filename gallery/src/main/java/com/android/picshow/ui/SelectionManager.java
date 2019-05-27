package com.android.picshow.ui;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.SparseArray;

import com.android.picshow.model.Path;
import com.android.picshow.model.PhotoItem;
import com.android.picshow.model.SimpleMediaItem;

import java.util.ArrayList;

/**
 * Created by yuntao.wei on 2017/12/29.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class SelectionManager {

    //private HashMap<String, Path> selectedPath;
    private SparseArray<Path> selectedPath;
    private SelectionListener selectionListener;
    private boolean selectionMode;

    public SelectionManager() {
        selectedPath = new SparseArray<>();
        //selectedPath = new HashMap<>();
    }

    public interface SelectionListener {

        void enterSelectionMode();
        void exitSelectionMode();
        void onSelectionChange(Path p, boolean select);

    }

    public int getSelectPostion() {
        //Set<String> keys = selectedPath.keySet();
        //return Integer.valueOf(keys.iterator().next());
        return selectedPath.keyAt(0);
    }

    public boolean togglePath(int position, Path p) {
        if(selectedPath.get(position) != null) {
            selectedPath.remove(position);
            if(selectionListener != null)
                selectionListener.onSelectionChange(p, false);
            if(selectedPath.size() == 0)
                exitSelectionMode();
            return false;
        } else {
            selectedPath.put(position, p);
            if(selectionListener != null)
                selectionListener.onSelectionChange(p, true);
            if(selectedPath.size() == 1)
                enterSelectionMode();
            return true;
        }
    }

    public boolean isSelected(int postion) {
        return selectedPath == null ? false : selectedPath.get(postion) != null;
    }

    private void enterSelectionMode() {
        selectionMode = true;
        if(selectionListener != null)
            selectionListener.enterSelectionMode();

    }

    private void exitSelectionMode() {
        selectionMode = false;
        if(selectionListener != null)
            selectionListener.exitSelectionMode();
    }

    public void clearSelection() {
        if(selectedPath != null) {
            selectedPath.clear();
        }
        exitSelectionMode();
    }



    public void setSelectionListener(SelectionListener sl) {
        selectionListener = sl;
    }

    public int getSelectCount() {
        if(selectedPath == null)
            return 0;
        return selectedPath.size();
    }

    public ArrayList<SimpleMediaItem> getSelectItems() {
        if(selectedPath.size() > 100)
            return null;

        return getUris();

    }

    private ArrayList<SimpleMediaItem> getUris() {
        ArrayList<SimpleMediaItem> uris = new ArrayList<>();
        int size = selectedPath.size(), i = 0;
        Path p = null;
        Uri u = null;
        for(;i < size; i++) {
            p = selectedPath.get(i);
            if(null == p)
                continue;

            if(p.mType == PhotoItem.TYPE_GIF || p.mType == PhotoItem.TYPE_IMAGE) {
                u = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, p.ID);
            } else {
                u = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, p.ID);
            }
            uris.add(new SimpleMediaItem(u, true, null));
        }
        /*for (Path p : selectedPath.values()
             ) {
            Uri u = null;
            if(p.mType == PhotoItem.TYPE_GIF || p.mType == PhotoItem.TYPE_IMAGE) {
                u = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, p.ID);
            } else {
                u = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, p.ID);
            }
            uris.add(new SimpleMediaItem(u, true, null));
        }*/

        return uris;
    }

}
