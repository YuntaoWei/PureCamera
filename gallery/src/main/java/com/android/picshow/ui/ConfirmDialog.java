package com.android.picshow.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.android.picshow.R;

/**
 * Created by yuntao.wei on 2018/1/3.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class ConfirmDialog implements OnClickListener {

    private Context mContext;
    private boolean mEditable;
    private OnClickListener onClickListener;
    private AlertDialog.Builder internalDialogBuilder;
    private EditText editText;
    private LayoutInflater layoutInflater;
    private ConfirDialogClickListener confirmNegativeListener;
    private ConfirDialogClickListener confirmPositiveListener;

    public interface ConfirDialogClickListener {

        public void onClick(DialogInterface dialogInterface, int which, String editable);

    }

    public static ConfirmDialog getInstance(Context ctx) {
        return new ConfirmDialog(ctx);
    }


    private ConfirmDialog(Context ctx) {
        mContext = ctx;
        internalDialogBuilder = new AlertDialog.Builder(mContext, R.style.ConfirmDialogStyle);
        layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public AlertDialog.Builder setTitle(String title) {
        return internalDialogBuilder.setTitle(title);
    }

    public AlertDialog.Builder setTitle(int resID) {
        return internalDialogBuilder.setTitle(resID);
    }

    public AlertDialog.Builder setMessage(String msg) {
        return internalDialogBuilder.setMessage(msg);
    }

    public AlertDialog.Builder setMessage(int msg) {
        return internalDialogBuilder.setMessage(msg);
    }

    public AlertDialog.Builder setView(View v) {
        return internalDialogBuilder.setView(v);
    }

    public AlertDialog.Builder setView(View v, int editId) {
        mEditable = true;
        editText = v.findViewById(editId);
        return internalDialogBuilder.setView(v);
    }

    public AlertDialog.Builder setView(int resLayout) {
        return internalDialogBuilder.setView(resLayout);
    }

    public AlertDialog.Builder setView(int resLayout, int editId) {
        mEditable = true;
        View v = layoutInflater.inflate(resLayout, null);
        editText = v.findViewById(editId);
        return internalDialogBuilder.setView(v);
    }

    public AlertDialog.Builder setNegativeListener(int text, ConfirDialogClickListener l) {
        confirmNegativeListener = l;
        return internalDialogBuilder.setNegativeButton(text, this);
    }

    public AlertDialog.Builder setNegativeListener(String text, ConfirDialogClickListener l) {
        confirmNegativeListener = l;
        return internalDialogBuilder.setNegativeButton(text, this);
    }

    public AlertDialog.Builder setPositiveListener(int text, ConfirDialogClickListener l) {
        confirmPositiveListener = l;
        return internalDialogBuilder.setPositiveButton(text, this);
    }

    public AlertDialog.Builder setPositiveListener(String text, ConfirDialogClickListener l) {
        confirmPositiveListener = l;
        return internalDialogBuilder.setPositiveButton(text, this);
    }

    public AlertDialog show() {
        return internalDialogBuilder.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String editContent = null;
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE :
                if(confirmNegativeListener == null)
                    return;
                if(mEditable)
                    editContent = editText.getText().toString();
                confirmNegativeListener.onClick(dialog, which, editContent);
                break;

            case DialogInterface.BUTTON_POSITIVE :
                if(confirmPositiveListener == null)
                    return;
                if(mEditable)
                    editContent = editText.getText().toString();
                confirmPositiveListener.onClick(dialog, which, editContent);
                break;

            default :

                break;
        }
    }



}
