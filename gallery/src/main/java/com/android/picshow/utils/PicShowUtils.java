package com.android.picshow.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.android.picshow.R;
import com.android.picshow.editor.BaseEditorManager;
import com.android.picshow.model.Album;
import com.android.picshow.model.PhotoItem;
import com.android.picshow.model.SimpleMediaItem;
import com.pure.commonbase.PathHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuntao.wei on 2017/11/28.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class PicShowUtils {

    public static final int MAX_LOAD = 3;


    public static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Get the TimeLine page thumbnail size from xml configuration.
     *
     * @param ctx
     * @return
     */
    public static int getImageWidth(Context ctx) {
        int screenWidth = ctx.getResources().getDisplayMetrics().widthPixels;
        int colNumbers = ctx.getResources().getInteger(R.integer.col_num);
        int allSpaceing = (colNumbers + 1) * (ctx.getResources().getDimensionPixelSize(R.dimen.col_spaceing));
        return (screenWidth - allSpaceing) / colNumbers;
    }

    /**
     * Get the AlbumSet Page thumbnail size from the xml configuration.
     *
     * @param ctx
     * @return
     */
    public static int getAlbumImageWidth(Context ctx) {
        int screenWidth = ctx.getResources().getDisplayMetrics().widthPixels;
        int colNumbers = ctx.getResources().getInteger(R.integer.album_col_num);
        int allSpaceing = (int) ctx.getResources().getDimension(R.dimen.album_col_spaceing);
        return (screenWidth - allSpaceing) / colNumbers;
    }


    //PackageManager.PERMISSION_GRANTED
    private static final int PERMISSION_GRANTED = 0;
    //PackageManager.PERMISSION_DENIED
    private static final int PERMISSION_DENIED = -1;

    public static boolean checkPermissions(Context ctx) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        ArrayList<String> needRequest = new ArrayList<>();
        for (String permission : PERMISSIONS
        ) {
            if (ContextCompat.checkSelfPermission(ctx, permission) == PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Hide the toolbar
     *
     * @param toolbar
     * @param needTitle
     */
    private static void setNoTitle(Toolbar toolbar, boolean needTitle) {
        if (needTitle) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.INVISIBLE);
        }
    }

    public static void extendLayoutToFulScreen(Activity a) {
        a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * Set display full screen options.
     *
     * @param toolbar
     */
    public static void enterFullScreen(Toolbar toolbar) {
        setNoTitle(toolbar, false);
    }

    /**
     * Exit the full screen mode.
     *
     * @param toolbar
     */
    public static void exitFullScreen(Toolbar toolbar) {
        setNoTitle(toolbar, true);
    }

    /**
     * Use merge sort to sort the datas.
     *
     * @param original src data.
     * @param desc     use DESC or ASC.
     */
    public static void sortItem(Album[] original, boolean desc) {
        mergeSort(original, 1);
    }

    private static void mergeSort(Album[] a, int len) {
        int size = a.length;
        int mid = size / (len << 1);
        int c = size & ((len << 1) - 1);
        if (mid == 0)
            return;
        for (int i = 0; i < mid; ++i) {
            int s = i * 2 * len;
            merge(a, s, s + len, (len << 1) + s - 1);
        }
        if (c != 0)
            merge(a, size - c - 2 * len, size - c, size - 1);
        mergeSort(a, 2 * len);
    }

    private static void merge(Album[] a, int s, int m, int t) {
        Album[] tmp = new Album[t - s + 1];
        int i = s, j = m, k = 0;
        while (i < m && j <= t) {
            if (a[i].dateToken >= a[j].dateToken) {
                tmp[k] = a[i];
                k++;
                i++;
            } else {
                tmp[k] = a[j];
                j++;
                k++;
            }
        }
        while (i < m) {
            tmp[k] = a[i];
            i++;
            k++;
        }
        while (j <= t) {
            tmp[k] = a[j];
            j++;
            k++;
        }
        System.arraycopy(tmp, 0, a, s, tmp.length);
    }

    public static void shareItems(Context ctx, ArrayList<Uri> uri, boolean image) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Bundle b = new Bundle();
        b.putParcelableArrayList(Intent.EXTRA_STREAM, uri);
        intent.putExtras(b);
        ctx.startActivity(Intent.createChooser(intent, ctx.getString(R.string.share)));

    }

    public static void shareItem(Context ctx, SimpleMediaItem item) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType(item.isImage ? "image/jpeg" : "video/*");
        intent.putExtra(Intent.EXTRA_STREAM, item.itemUrl);
        ctx.startActivity(Intent.createChooser(intent, ctx.getString(R.string.share)));
    }

    public static void editItem(Context ctx, SimpleMediaItem item) {

        if (item.isImage) {
            /*Intent intent = new Intent(ctx, EditActivity.class);
            intent.putExtra(BaseEditorManager.SRC_PIC_PATH, MediaSetUtils.uriToPath(ctx, item.itemUrl));
            ctx.startActivity(intent);*/
            ARouter.getInstance().build(PathHelper.PATH_EDIT)
                    .withString(BaseEditorManager.SRC_PIC_PATH, MediaSetUtils.uriToPath(ctx, item.itemUrl))
                    .navigation();
        } else {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(item.itemUrl, "video/*");
            ctx.startActivity(Intent.createChooser(intent, ctx.getString(R.string.edit)));
        }
    }

    public static boolean deleteItem(Context ctx, SimpleMediaItem item) {
        Cursor c = ctx.getContentResolver().query(item.itemUrl,
                new String[]{}, null, null, null);
        String path = null;
        if (c != null) {
            while (c.moveToNext()) {
                path = c.getString(0);
            }
            c.close();
            c = null;
        }
        int row = ctx.getContentResolver().delete(item.itemUrl, null, null);
        if (row > 0 && path != null) {
            File f = new File(path);
            if (f.exists())
                return f.delete();
        }
        return false;
    }

    public static boolean renameItem(Context ctx, Uri uri, boolean image, String newName) {
        Cursor c = ctx.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA},
                null, null, null);
        String absPath = null;
        String newFilePath = null;
        if (c != null && c.moveToNext()) {
            absPath = c.getString(0);
            c.close();
            c = null;
        }

        boolean result = false;
        if (absPath != null) {
            File f = new File(absPath);
            if (f.exists()) {
                int index = absPath.lastIndexOf(".");
                String reg = absPath.substring(index);
                String newFileName = newName + reg;
                File p = f.getParentFile();
                File newFile = new File(p, newFileName);
                newFilePath = newFile.getAbsolutePath();
                result = f.renameTo(newFile);
            }
        }
        if (result) {
            ContentValues cv = new ContentValues();
            cv.put(MediaStore.Files.FileColumns.TITLE, newName);
            cv.put(MediaStore.Files.FileColumns.DATA, newFilePath == null ? "" : newFilePath);
            result = ctx.getContentResolver().update(uri, cv, null, null) > 0;
        }

        return result;
    }

    public static void setDialogSize(Dialog dialog) {
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 900;
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }

    public static void showDetailDialog(Context ctx, final List<String> details) {
        //detailDialog
        final LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ctx, R.style.draw_dialog);
        View dialogView = inflater.inflate(R.layout.detail_layout, null);
        ListView list = dialogView.findViewById(R.id.detail_list);
        list.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return details.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder vh = null;
                String[] info = null;
                if (position == getCount() - 1) {
                    String o = details.get(position);
                    int index = o.indexOf('/');
                    info = new String[2];
                    info[0] = o.substring(0, index);
                    info[1] = o.substring(index + 1);
                } else {
                    info = details.get(position).split("/");
                }

                if (convertView != null) {
                    vh = (ViewHolder) convertView.getTag();
                    vh.tvItemName.setText(info[0]);
                    vh.tvDetail.setText(info[1]);
                } else {
                    vh = new ViewHolder();
                    convertView = inflater.inflate(R.layout.detail_item, null);
                    vh.tvDetail = convertView.findViewById(R.id.detail);
                    vh.tvItemName = convertView.findViewById(R.id.item_name);
                    convertView.setTag(vh);
                    vh.tvItemName.setText(info[0]);
                    vh.tvDetail.setText(info[1]);
                }
                return convertView;
            }

            class ViewHolder {
                public TextView tvDetail;
                public TextView tvItemName;
            }
        });
        mBuilder.setView(dialogView);
        setDialogSize(mBuilder.show());
    }

    public static boolean isImage(String type) {
        int a = type == null ? PhotoItem.TYPE_IMAGE :
                (type.startsWith("video") ? PhotoItem.TYPE_VIDEO : PhotoItem.TYPE_IMAGE);
        return a == PhotoItem.TYPE_IMAGE;
    }

    public static List<String> getExifInfo(String path) {
        List<String> infos = new ArrayList<>();
        String detail = null;
        try {
            ExifInterface ef = new ExifInterface(path);
            detail = ef.getAttribute(ExifInterface.TAG_DATETIME);
            if (detail != null) {
                infos.add(ExifInterface.TAG_DATETIME + " : /" + detail);
            }
            detail = null;

            detail = ef.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            if (detail != null) {
                infos.add(ExifInterface.TAG_IMAGE_WIDTH + " : /" + detail);
            }
            detail = null;

            detail = ef.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            if (detail != null) {
                infos.add(ExifInterface.TAG_IMAGE_LENGTH + " : /" + detail);
            }
            detail = null;

            detail = ef.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (detail != null) {
                infos.add(ExifInterface.TAG_ORIENTATION + " : /" + detail);
            }
            detail = null;

            detail = ef.getAttribute(ExifInterface.TAG_MAKE);
            if (detail != null) {
                infos.add(ExifInterface.TAG_MAKE + " : /" + detail);
            }
            detail = null;

            detail = ef.getAttribute(ExifInterface.TAG_MODEL);
            if (detail != null) {
                infos.add(ExifInterface.TAG_MODEL + " : /" + detail);
            }
            detail = null;

            detail = ef.getAttribute(ExifInterface.TAG_FLASH);
            if (detail != null) {
                infos.add(ExifInterface.TAG_FLASH + " : /" + detail);
            }
            detail = null;

            detail = ef.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            if (detail != null) {
                infos.add(ExifInterface.TAG_FOCAL_LENGTH + " : /" + detail);
            }
            detail = null;

            detail = ef.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            if (detail != null) {
                infos.add(ExifInterface.TAG_WHITE_BALANCE + " : /" + detail);
            }
            detail = null;

            detail = ef.getAttribute(ExifInterface.TAG_APERTURE_VALUE);
            if (detail != null) {
                infos.add(ExifInterface.TAG_APERTURE_VALUE + " : /" + detail);
            }
            detail = null;

            detail = ef.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            if (detail != null) {
                infos.add(ExifInterface.TAG_EXPOSURE_TIME + " : /" + detail);
            }
            detail = null;

            detail = ef.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);
            if (detail != null) {
                infos.add(ExifInterface.TAG_ISO_SPEED_RATINGS + " : /" + detail);
            }
            detail = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return infos;
    }

    public static void showPopupDialog(Context ctx) {

    }


}
