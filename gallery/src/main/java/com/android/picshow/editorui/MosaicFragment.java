package com.android.picshow.editorui;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.android.picshow.R;
import com.android.picshow.editor.BaseEditorManager;
import com.android.picshow.editor.filters.BaseEditor;
import com.android.picshow.editor.mosaic.DrawMosaicView;
import com.android.picshow.editor.mosaic.MosaicUtil;
import com.android.picshow.editor.utils.FileUtils;
import com.android.picshow.utils.LogPrinter;

/**
 * Created by yuntao.wei on 2018/5/17.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class MosaicFragment extends Fragment implements View.OnClickListener, BaseEditor,
        SeekBar.OnSeekBarChangeListener {

    private static final String TAG = MosaicFragment.class.getSimpleName();

    private EditActivity mActivity;

    private DrawMosaicView mosaic;

    String mPath;
    private int mWidth, mHeight;

    Bitmap srcBitmap = null;


    private static final int DEFAULT_PROGRESS = 25;

    private View mainView;

    private int size = DEFAULT_PROGRESS;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (EditActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_mosaic, null);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;
        initView();
        initData();
    }


    private void initData() {
        Bundle b = getArguments();
        mPath = b.getString(BaseEditorManager.SRC_PIC_PATH, null);
        LogPrinter.i(TAG, "pic path : " + mPath);

        BaseEditorManager.decodeBitmapAsync(mPath, new BaseEditorManager.LoadListener() {
            @Override
            public void onLoadSuccess(Object o) {

                srcBitmap = (Bitmap)o;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWidth = srcBitmap.getWidth();
                        mHeight = srcBitmap.getHeight();
                        mosaic.setMosaicBackgroundResource(srcBitmap);
                        mosaic.setMosaicBrushWidth(10);

                        mActivity.onFragmentAttached();
                    }
                });
            }

            @Override
            public void onLoadFailed() {

            }
        });
    }

    private void initView() {
        mosaic = (DrawMosaicView) mainView.findViewById(R.id.mosaic);
    }

    private View topPanel;
    private SeekBar seek;
    private Button btnSmall, btnBig, btnBlur, btnEraser;

    @Override
    public void initPanelView(View v) {

        topPanel = v.findViewById(R.id.topPanel);
        seek = (SeekBar) v.findViewById(R.id.seek);
        seek.setMax(50);
        seek.setProgress(DEFAULT_PROGRESS);
        seek.setOnSeekBarChangeListener(this);
        mosaic.setMosaicBrushWidth(size);

        btnSmall = (Button) v.findViewById(R.id.mosaic_menu1);
        btnBig = (Button) v.findViewById(R.id.mosaic_menu2);
        btnBlur = (Button) v.findViewById(R.id.mosaic_menu3);
        btnEraser = (Button) v.findViewById(R.id.mosaic_menu4);
        btnSmall.setOnClickListener(this);
        btnBig.setOnClickListener(this);
        btnBlur.setOnClickListener(this);
        btnEraser.setOnClickListener(this);

        v.findViewById(R.id.cancel).setOnClickListener(this);
        v.findViewById(R.id.revocation).setOnClickListener(this);
        v.findViewById(R.id.cancel_revocation).setOnClickListener(this);
        v.findViewById(R.id.save).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.mosaic_menu1) {
            Bitmap bitmapMosaic = MosaicUtil.getMosaic(srcBitmap, BaseEditorManager.MOSAIC_SMALL_RADIUS);
            mosaic.setMosaicResource(bitmapMosaic);
            setSelectStatus();
            btnSmall.setBackgroundResource(R.drawable.mosaic_small_blue_n);

        } else if (i == R.id.mosaic_menu2) {
            Bitmap bitmapMosaic1 = MosaicUtil.getMosaic(srcBitmap, BaseEditorManager.MOSAIC_BIG_RADIUS);
            mosaic.setMosaicResource(bitmapMosaic1);
            setSelectStatus();
            btnBig.setBackgroundResource(R.drawable.mosaic_big_blue_n);

        } else if (i == R.id.mosaic_menu3) {
            Bitmap bitmapBlur = MosaicUtil.getBlur(srcBitmap, BaseEditorManager.MOSIC_BLUR_RADIUS);
            mosaic.setMosaicResource(bitmapBlur);
            setSelectStatus();
            btnBlur.setBackgroundResource(R.drawable.mosaic_blur_blue_n);

        } else if (i == R.id.mosaic_menu4) {
            mosaic.setMosaicType(MosaicUtil.MosaicType.ERASER);
            setSelectStatus();
            btnEraser.setBackgroundResource(R.drawable.eraser_blue_n);

        } else if (i == R.id.revocation) {
            revocation();

        } else if (i == R.id.cancel_revocation) {
            cancelRevocation();

        } else if (i == R.id.save) {
            mActivity.shouldReLoadPicture(mosaic.getMosaicBitmap());
            onCancel();

        } else if (i == R.id.cancel) {
            onCancel();

        } else {
        }
    }

    private void setSelectStatus() {
        if(topPanel.getVisibility() != View.VISIBLE) {
            topPanel.setVisibility(View.VISIBLE);
        }

        if(seek != null) {
            seek.setProgress(DEFAULT_PROGRESS);
        }

        btnSmall.setBackgroundResource(R.drawable.mosaic_small_white_n);
        btnBig.setBackgroundResource(R.drawable.mosaic_big_white_n);
        btnBlur.setBackgroundResource(R.drawable.mosaic_blur_white_n);
        btnEraser.setBackgroundResource(R.drawable.eraser_white_n);
    }

    private void revocation() {
        mosaic.revocation();
    }

    private void cancelRevocation() {
        mosaic.cancelRevocation();
    }

    @Override
    public void onSave() {
        Bitmap bit = mosaic.getMosaicBitmap();

        FileUtils.writeImage(bit, mPath, 100);

        onCancel();
    }

    @Override
    public void onCancel() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onCompareStart() {
        LogPrinter.i(TAG, "onCompareStart");
    }

    @Override
    public void onCompareEnd() {
        LogPrinter.i(TAG, "onCompareEnd");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        size = seekBar.getProgress();
        if(size == 0)
            size = 5;
        mosaic.setMosaicBrushWidth(size);
    }
}
