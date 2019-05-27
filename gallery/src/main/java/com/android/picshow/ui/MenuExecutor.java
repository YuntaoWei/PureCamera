package com.android.picshow.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;

import com.android.picshow.R;
import com.android.picshow.model.SimpleMediaItem;
import com.android.picshow.utils.PhotoPageUtils;
import com.android.picshow.utils.PicShowUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuntao.wei on 2017/12/29.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class MenuExecutor {

    public static final int MENU_ACTION_DELETE = 0x001;
    public static final int MENU_ACTION_SETAS = 0x002;
    public static final int MENU_ACTION_DETAIL = 0x003;
    public static final int MENU_ACTION_SHARE = 0x004;
    public static final int MENU_ACTION_EDIT = 0x005;
    public static final int MENU_ACTION_RENAME = 0x006;


    private Context mContext;


    public MenuExecutor(Context ctx) {
        mContext = ctx;
    }

    private void deleteItemsBySync(List<Uri> u) {
        
    }

    private class ExecuteTask extends AsyncTask<Integer, Void, Void> {

        private ExcuteListener l;
        private ArrayList<SimpleMediaItem> uris;

        public ExecuteTask(ArrayList<SimpleMediaItem> u, ExcuteListener l) {
            uris = u;
            this.l = l;
        }

        @Override
        protected void onPreExecute() {
            if(l != null)
                l.startExcute();
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            switch (integers[0]) {
                case MENU_ACTION_DELETE :
                    if(uris.size() == 1) {
                        PicShowUtils.deleteItem(mContext, uris.get(0));
                    } else {
                        ContentResolver cr = mContext.getContentResolver();
                        for (SimpleMediaItem u : uris
                                ) {
                            cr.delete(u.itemUrl, null, null);
                        }
                    }
                    break;

                case MENU_ACTION_SHARE :
                    if(uris.size() == 1) {
                        PicShowUtils.shareItem(mContext, uris.get(0));
                    } else {
                        PicShowUtils.shareItems(mContext, PhotoPageUtils.mediaItem2Uris(uris), true);
                    }
                    break;

                case MENU_ACTION_EDIT :
                    PicShowUtils.editItem(mContext, uris.get(0));
                    break;

                case MENU_ACTION_RENAME :
                    showConfirmDialog(uris.get(0));
                    break;

                default:
                    return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(l != null)
                l.excuteSuccess();
        }
    }

    public interface ExcuteListener {

        void startExcute();
        void excuteSuccess();
        void excuteFailed();

    }

    public void execute(int action, ArrayList<SimpleMediaItem> u, ExcuteListener l) {
        ExecuteTask deleteTask = new ExecuteTask(u, l);
        deleteTask.execute(action);
        switch (action) {
            case MENU_ACTION_DELETE :
                if(u.size() == 1) {
                    PicShowUtils.deleteItem(mContext, u.get(0));
                } else {
                    deleteTask.execute(action);
                }
                break;

            case MENU_ACTION_SHARE :
                if(u.size() == 1) {
                    PicShowUtils.shareItem(mContext, u.get(0));
                } else {

                }
                break;

            case MENU_ACTION_EDIT :
                PicShowUtils.editItem(mContext, u.get(0));
                break;

            case MENU_ACTION_RENAME :
                showConfirmDialog(u.get(0));
                break;

            default:
        }
    }

    private void showConfirmDialog(final SimpleMediaItem u) {
        ConfirmDialog confirmDialog = ConfirmDialog.getInstance(mContext);
        confirmDialog.setTitle(R.string.rename);
        confirmDialog.setView(R.layout.editable_dialog_layout, R.id.edit_able);
        confirmDialog.setPositiveListener(R.string.confirm, new ConfirmDialog.ConfirDialogClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which, String editable) {
                PicShowUtils.renameItem(mContext, u.itemUrl, u.isImage, editable);
            }

        });
        confirmDialog.setNegativeListener(R.string.cancel, null);
        confirmDialog.show();
    }

}
