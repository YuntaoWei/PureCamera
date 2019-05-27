package com.android.picshow.editorui;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.picshow.R;
import com.android.picshow.editor.BaseEditorManager;
import com.android.picshow.editor.crop.CropImageType;
import com.android.picshow.editor.crop.CropImageView;
import com.android.picshow.editor.crop.HorizontalDial;
import com.android.picshow.editor.filters.BaseEditor;
import com.android.picshow.editor.utils.FileUtils;
import com.android.picshow.utils.LogPrinter;

/**
 * Created by yuntao.wei on 2018/5/16.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class CropFragment extends Fragment implements BaseEditor, View.OnClickListener {

    private static final String TAG = CropFragment.class.getSimpleName();

    private Toolbar mToolbar;

    private CropImageView cropImage;

    private String mPath = null;

    private ImageButton cancleBtn, okBtn;

    private EditActivity mActivity;

    private View mainView;

    private Bitmap originalPic;

    private View topPanel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.crop_image, null);

        return v;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;
        initView();
        initData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (EditActivity) context;
    }

    private void initView() {
        cropImage = (CropImageView) mainView.findViewById(R.id.cropmageView);
        cropImage.setMode(BaseEditorManager.ROTATE_MODE);
    }

    private void initData() {
        Bundle b = getArguments();
        mPath = b.getString(BaseEditorManager.SRC_PIC_PATH, null);

        BaseEditorManager.decodeBitmapAsync(mPath,
                new BaseEditorManager.LoadListener() {
                    @Override
                    public void onLoadSuccess(Object o) {

                        originalPic = (Bitmap) o;
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.onFragmentAttached();
                                initCropView();
                            }
                        });

                    }

                    @Override
                    public void onLoadFailed() {

                    }
                });
    }

    private void initCropView() {

        Bitmap hh = BitmapFactory.decodeResource(mActivity.getResources(),
                R.drawable.crop_button);

        cropImage.setCropOverlayCornerBitmap(hh);

        cropImage.setOriginalBm(originalPic);
        cropImage.setImageBitmap(originalPic);

        cropImage.setGuidelines(CropImageType.CROPIMAGE_GRID_ON_TOUCH);

        cropImage.setFixedAspectRatio(false);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.none) {
            cropImage.setFixedAspectRatio(false);
            setCropMenuStatus(1);


        } else if (i == R.id.aspect) {
            cropImage.setFixedAspectRatio(true);
            cropImage.setAspectRatio(-1, -1);
            setCropMenuStatus(2);


        } else if (i == R.id.one_per_one) {
            cropImage.setFixedAspectRatio(true);
            cropImage.setAspectRatio(10, 10);
            setCropMenuStatus(3);


        } else if (i == R.id.four_per_three) {
            cropImage.setFixedAspectRatio(true);
            cropImage.setAspectRatio(40, 30);
            setCropMenuStatus(4);


        } else if (i == R.id.three_per_four) {
            cropImage.setFixedAspectRatio(true);
            cropImage.setAspectRatio(30, 40);
            setCropMenuStatus(5);


        } else if (i == R.id.five_per_seven) {
            cropImage.setFixedAspectRatio(true);
            cropImage.setAspectRatio(50, 70);
            setCropMenuStatus(6);


        } else if (i == R.id.seven_per_five) {
            cropImage.setFixedAspectRatio(true);
            cropImage.setAspectRatio(70, 50);
            setCropMenuStatus(7);


        } else if (i == R.id.crop_menu) {
            cropImage.setMode(BaseEditorManager.CROP_MODE);
            setEditModeStatus(true);


        } else if (i == R.id.rotate_menu) {
            cropImage.setMode(BaseEditorManager.ROTATE_MODE);
            cropImage.rotateImage(90);
            setEditModeStatus(false);


        } else if (i == R.id.cancel) {
            onCancel();

        } else if (i == R.id.save) {
            if (cropImage.isModify())
                mActivity.shouldReLoadPicture(cropImage.getResultImage());
            onCancel();

        } else if (i == R.id.revocation) {
            revocation();

        } else if (i == R.id.cancel_revocation) {
            cancelRevocation();

        } else {
        }
    }

    private void setEditModeStatus(boolean cropMode) {

        if(cropMode) {
            btnCrop.setBackgroundResource(R.drawable.crop_blue_n);

            if(topPanel != null) {
                if(topPanel.getVisibility() != View.VISIBLE) {
                    topPanel.setVisibility(View.VISIBLE);
                }
            }

        } else {

            btnCrop.setBackgroundResource(R.drawable.crop_white_n);

            if(topPanel != null) {
                if(topPanel.getVisibility() == View.VISIBLE) {
                    topPanel.setVisibility(View.INVISIBLE);
                }
            }

        }
    }

    private void setCropMenuStatus(int index) {

        cropMenu1.setTextColor(0xFFD5D5D8);
        cropMenu2.setTextColor(0xFFD5D5D8);
        cropMenu3.setTextColor(0xFFD5D5D8);
        cropMenu4.setTextColor(0xFFD5D5D8);
        cropMenu5.setTextColor(0xFFD5D5D8);
        cropMenu6.setTextColor(0xFFD5D5D8);
        cropMenu7.setTextColor(0xFFD5D5D8);

        switch (index) {

            case 1:
                cropMenu1.setTextColor(0xFF007AFF);
                return;

            case 2:
                cropMenu2.setTextColor(0xFF007AFF);
                return;

            case 3:
                cropMenu3.setTextColor(0xFF007AFF);
                return;

            case 4:
                cropMenu4.setTextColor(0xFF007AFF);
                return;

            case 5:
                cropMenu5.setTextColor(0xFF007AFF);
                return;

            case 6:
                cropMenu6.setTextColor(0xFF007AFF);
                return;

            case 7:
                cropMenu7.setTextColor(0xFF007AFF);
                return;
        }

    }


    private TextView cropMenu1;
    private TextView cropMenu2;
    private TextView cropMenu3;
    private TextView cropMenu4;
    private TextView cropMenu5;
    private TextView cropMenu6;
    private TextView cropMenu7;
    private TextView btnCrop;

    @Override
    public void initPanelView(View v) {

        initCropMenu(v);

        initCenterOperationPanel(v);

        initBottomOperationPanel(v);
    }

    private void initCropMenu(View v) {
        cropMenu1 = (TextView) v.findViewById(R.id.none);
        cropMenu2 = (TextView) v.findViewById(R.id.aspect);
        cropMenu3 = (TextView) v.findViewById(R.id.one_per_one);
        cropMenu4 = (TextView) v.findViewById(R.id.four_per_three);
        cropMenu5 = (TextView) v.findViewById(R.id.three_per_four);
        cropMenu6 = (TextView) v.findViewById(R.id.five_per_seven);
        cropMenu7 = (TextView) v.findViewById(R.id.seven_per_five);

        cropMenu1.setOnClickListener(this);
        cropMenu2.setOnClickListener(this);
        cropMenu3.setOnClickListener(this);
        cropMenu4.setOnClickListener(this);
        cropMenu5.setOnClickListener(this);
        cropMenu6.setOnClickListener(this);
        cropMenu7.setOnClickListener(this);
        cropMenu7.setOnClickListener(this);
    }

    private void initCenterOperationPanel(View v) {
        btnCrop = (TextView) v.findViewById(R.id.crop_menu);
        TextView btnRotate = (TextView) v.findViewById(R.id.rotate_menu);
        HorizontalDial seekBar = (HorizontalDial) v.findViewById(R.id.seek);

        btnCrop.setOnClickListener(this);
        btnRotate.setOnClickListener(this);
        seekBar.setOnSeekChangeListener(new HorizontalDial.onSeekChangeListener() {

            @Override
            public void onSeekStart(HorizontalDial v) {

            }

            @Override
            public void onProgressChange(float progress) {
                LogPrinter.i(TAG, "onProgressChange : " + progress);
                if(progress > 45)
                    progress = 45;
                else if(progress < -45)
                    progress = -45;
                cropImage.setMode(BaseEditorManager.ROTATE_MODE);
                cropImage.rotateImage(progress);

            }

            @Override
            public void onSeekEnd(HorizontalDial v) {

            }

        });
    }

    private void initBottomOperationPanel(View v) {
        Button btnCancel = (Button) v.findViewById(R.id.cancel);
        Button btnSave = (Button) v.findViewById(R.id.save);
        Button btnRecovation = (Button) v.findViewById(R.id.revocation);
        Button btnCacekRevocation = (Button) v.findViewById(R.id.cancel_revocation);

        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnRecovation.setOnClickListener(this);
        btnCacekRevocation.setOnClickListener(this);

        topPanel = v.findViewById(R.id.topPanel);
    }

    private void revocation() {
        cropImage.revocation();
    }

    private void cancelRevocation() {
        cropImage.cancelRevocation();
    }

    @Override
    public void onSave() {
        Bitmap bit = cropImage.getCroppedImage();
        FileUtils.writeImage(bit, mPath, 100);
        onCancel();
    }

    @Override
    public void onCancel() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onCompareStart() {
        cropImage.showOriginalPic();
    }

    @Override
    public void onCompareEnd() {
        cropImage.showEditedPic();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(cropImage != null) {
            cropImage.recycle();
            cropImage = null;
        }
    }
}
