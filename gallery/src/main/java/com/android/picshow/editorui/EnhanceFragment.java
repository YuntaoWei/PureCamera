package com.android.picshow.editorui;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.picshow.R;
import com.android.picshow.editor.BaseEditorManager;
import com.android.picshow.editor.enhance.PhotoEnhance;
import com.android.picshow.editor.filters.BaseEditor;
import com.android.picshow.editor.utils.FileUtils;
import com.android.picshow.utils.LogPrinter;


/**
 * Created by yuntao.wei on 2018/5/17.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class EnhanceFragment extends Fragment implements View.OnClickListener, BaseEditor,
        SeekBar.OnSeekBarChangeListener {

    private static final String TAG = EnhanceFragment.class.getSimpleName();

    private ImageView pictureShow;

    private SeekBar levelSeekBar;

    private String imgPath;
    private Bitmap bitmapSrc;

    private PhotoEnhance photoEnhance;
    private EditActivity mActivity;
    private View mainView;

    private int currentType = -1;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (EditActivity)context;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_enhance, null);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mainView = view;
        initView();
        initData();
    }

    private void initData() {
        Bundle b = getArguments();
        imgPath = b.getString(BaseEditorManager.SRC_PIC_PATH, null);
        LogPrinter.i(TAG, "pic path : " + imgPath);

        BaseEditorManager.decodeBitmapAsync(imgPath, new BaseEditorManager.LoadListener() {
            @Override
            public void onLoadSuccess(Object o) {

                bitmapSrc = (Bitmap)o;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pictureShow.setImageBitmap(bitmapSrc);
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

        pictureShow = (ImageView) mainView.findViewById(R.id.enhancePicture);

    }

    private Bitmap bit = null;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        if(photoEnhance == null)
            return;
        setEnhanceLevel(seekBar.getProgress());
        bit = photoEnhance.handleImage(currentType);
        pictureShow.setImageBitmap(bit);

    }

    private void setEnhanceLevel(int level) {

        switch (currentType) {

            case PhotoEnhance.Enhance_Brightness:
                photoEnhance.setBrightness(level);
                break;

            case PhotoEnhance.Enhance_Contrast:
                photoEnhance.setContrast(level);
                break;

            case PhotoEnhance.Enhance_Saturation:
                photoEnhance.setSaturation(level);
                break;

            case PhotoEnhance.Enhance_ColorTemperature:

                break;

            default:

                break;

        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.enhance_brightness) {
            currentType = PhotoEnhance.Enhance_Brightness;
            showTopPanel(PhotoEnhance.Enhance_Brightness);

        } else if (i == R.id.enhance_contrast) {
            currentType = PhotoEnhance.Enhance_Contrast;
            showTopPanel(PhotoEnhance.Enhance_Contrast);

        } else if (i == R.id.enhance_color_temperature) {
            currentType = PhotoEnhance.Enhance_ColorTemperature;
            showTopPanel(PhotoEnhance.Enhance_ColorTemperature);

        } else if (i == R.id.enhance_saturation) {
            currentType = PhotoEnhance.Enhance_Saturation;
            showTopPanel(PhotoEnhance.Enhance_Saturation);

        } else if (i == R.id.cancel) {
            onCancel();

        } else if (i == R.id.revocation) {
            revocation();

        } else if (i == R.id.cancel_revocation) {
            cancelRevocation();

        } else if (i == R.id.save) {
            mActivity.shouldReLoadPicture(bit);
            onCancel();

        } else {
        }

    }

    private void resetSelectStatus() {
        Drawable brightnessDefaultDrawable = getResources().getDrawable(R.drawable.light_white_n);
        brightnessDefaultDrawable.setBounds(0, 0,
                brightnessDefaultDrawable.getMinimumWidth(), brightnessDefaultDrawable.getMinimumHeight());
        tvBrightness.setTextColor(0xFFD5D5D8);
        tvBrightness.setCompoundDrawables(null, brightnessDefaultDrawable, null, null);

        Drawable contrastDefaultDrawable = getResources().getDrawable(R.drawable.contrast_white_n);
        contrastDefaultDrawable.setBounds(0, 0,
                contrastDefaultDrawable.getMinimumWidth(), contrastDefaultDrawable.getMinimumHeight());
        tvContrast.setTextColor(0xFFD5D5D8);
        tvContrast.setCompoundDrawables(null, contrastDefaultDrawable, null, null);

        Drawable colorDefaultDrawable = getResources().getDrawable(R.drawable.colortemperature_white_n);
        colorDefaultDrawable.setBounds(0, 0,
                colorDefaultDrawable.getMinimumWidth(), colorDefaultDrawable.getMinimumHeight());
        tvColorTemperature.setTextColor(0xFFD5D5D8);
        tvColorTemperature.setCompoundDrawables(null, colorDefaultDrawable, null, null);

        Drawable saturationDefaultDrawable = getResources().getDrawable(R.drawable.satura_white_n);
        saturationDefaultDrawable.setBounds(0, 0,
                saturationDefaultDrawable.getMinimumWidth(), saturationDefaultDrawable.getMinimumHeight());
        tvSaturation.setTextColor(0xFFD5D5D8);
        tvSaturation.setCompoundDrawables(null, saturationDefaultDrawable, null, null);

    }

    public void setSelectStatus(TextView tv, int type) {

        Drawable topDrawable = null;
        switch (type) {
            case PhotoEnhance.Enhance_Brightness:
                topDrawable = getResources().getDrawable(R.drawable.light_blue_n);
                break;

            case PhotoEnhance.Enhance_Contrast:
                topDrawable = getResources().getDrawable(R.drawable.contrast_blue_n);
                break;

            case PhotoEnhance.Enhance_Saturation:
                topDrawable = getResources().getDrawable(R.drawable.satura_blue_n);
                break;

            case PhotoEnhance.Enhance_ColorTemperature:
                topDrawable = getResources().getDrawable(R.drawable.colortemperature_blue_n);
                break;
        }

        if(topDrawable != null) {
            topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
            tv.setTextColor(0xFF007AFF);
            tv.setCompoundDrawables(null, topDrawable, null, null);
        }

    }

    private void showTopPanel(int type) {

        resetSelectStatus();
        if(topPanel != null) {
            topPanel.setVisibility(View.VISIBLE);
        }

        int level = 128;
        float oldEnhance;
        switch (type) {

            case PhotoEnhance.Enhance_Brightness:

                setSelectStatus(tvBrightness, PhotoEnhance.Enhance_Brightness);

                oldEnhance = photoEnhance.getBrightness();
                if(oldEnhance == 0)
                    break;
                level = (int) oldEnhance * 128;
                levelSeekBar.setProgress(level);
                break;

            case PhotoEnhance.Enhance_Contrast:

                setSelectStatus(tvContrast, PhotoEnhance.Enhance_Contrast);

                oldEnhance = photoEnhance.getContrast();
                if(oldEnhance == 1.0f)
                    break;
                level = (int)(oldEnhance * 128 - 64) * 2;
                break;

            case PhotoEnhance.Enhance_Saturation:

                setSelectStatus(tvSaturation, PhotoEnhance.Enhance_Saturation);

                oldEnhance = photoEnhance.getSaturation();
                if(oldEnhance == 1.0f)
                    break;
                level = (int) oldEnhance * 128;
                break;

            case PhotoEnhance.Enhance_ColorTemperature:

                setSelectStatus(tvColorTemperature, PhotoEnhance.Enhance_ColorTemperature);

                break;

            default:

                break;

        }

        levelSeekBar.setProgress(level);

    }

    private void recycle() {
        if (bitmapSrc != null) {
            bitmapSrc.recycle();
            bitmapSrc = null;
        }

        if (bit != null) {
            bit.recycle();
            bit = null;
        }
    }


    private View topPanel;
    private TextView tvBrightness, tvContrast, tvColorTemperature, tvSaturation;
    private Button btnCancel, btnRevocation, btnCancelRevocation, btnSave;

    @Override
    public void initPanelView(View v) {
        levelSeekBar = (SeekBar) v.findViewById(R.id.seek);
        levelSeekBar.setMax(255);
        levelSeekBar.setProgress(128);
        levelSeekBar.setOnSeekBarChangeListener(this);

        topPanel = v.findViewById(R.id.topPanel);
        tvBrightness = (TextView) v.findViewById(R.id.enhance_brightness);
        tvContrast = (TextView) v.findViewById(R.id.enhance_contrast);
        tvColorTemperature = (TextView) v.findViewById(R.id.enhance_color_temperature);
        tvSaturation = (TextView) v.findViewById(R.id.enhance_saturation);
        btnCancel = (Button) v.findViewById(R.id.cancel);
        btnRevocation = (Button) v.findViewById(R.id.revocation);
        btnCancelRevocation = (Button) v.findViewById(R.id.cancel_revocation);
        btnSave = (Button) v.findViewById(R.id.save);

        tvBrightness.setOnClickListener(this);
        tvContrast.setOnClickListener(this);
        tvColorTemperature.setOnClickListener(this);
        tvSaturation.setOnClickListener(this);

        btnCancel.setOnClickListener(this);
        btnRevocation.setOnClickListener(this);
        btnCancelRevocation.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        photoEnhance = new PhotoEnhance(bitmapSrc);
    }

    private void revocation() {
        pictureShow.setImageBitmap(bitmapSrc);
        photoEnhance.reset();
    }

    private void cancelRevocation() {
        pictureShow.setImageBitmap(bit);
        photoEnhance.restore();
    }

    @Override
    public void onSave() {
        FileUtils.writeImage(bit, imgPath, 100);
        onCancel();
    }

    @Override
    public void onCancel() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onCompareStart() {
        pictureShow.setImageBitmap(bitmapSrc);
    }

    @Override
    public void onCompareEnd() {
        pictureShow.setImageBitmap(bit);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycle();
    }
}
