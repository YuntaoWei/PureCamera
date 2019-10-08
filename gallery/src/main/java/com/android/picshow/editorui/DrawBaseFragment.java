package com.android.picshow.editorui;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.picshow.R;
import com.android.picshow.editor.BaseEditorManager;
import com.android.picshow.editor.filters.BaseEditor;
import com.android.picshow.editor.graffti.GraffitiListener;
import com.android.picshow.editor.graffti.GraffitiParams;
import com.android.picshow.editor.graffti.GraffitiSelectableItem;
import com.android.picshow.editor.graffti.GraffitiText;
import com.android.picshow.editor.graffti.GraffitiView;
import com.android.picshow.editor.scrawl.PaintUtils;
import com.pure.commonbase.LogPrinter;

/**
 * Created by yuntao.wei on 2018/5/21.
 * github:https://github.com/YuntaoWei
 * blog:http://blog.csdn.net/qq_17541215
 */

public class DrawBaseFragment extends Fragment implements View.OnClickListener, BaseEditor,
        SeekBar.OnSeekBarChangeListener {

    public static final String KEY_PARAMS = "key_graffiti_params";
    private static final String TAG = DrawBaseFragment.class.getSimpleName();

    private View mainView;
    private GraffitiView graffitiView;
    private GraffitiParams mGraffitiParams;

    private String picturePath;
    private Bitmap srcPicture;

    private EditActivity mActivity;
    private static final int PAINT_SEEK_BAR_MAX = 50;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (EditActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.draw_base_layout, null);
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;
        initData();
    }


    private void initGraffitiView() {
        graffitiView = new GraffitiView(mActivity.getApplicationContext(), srcPicture,
                mGraffitiParams.mEraserPath, mGraffitiParams.mEraserImageIsResizeable, new GraffitiListener() {

            @Override
            public void onSaved(Bitmap bitmap, Bitmap bitmapEraser) {


                if (bitmapEraser != null) {
                    bitmapEraser.recycle();
                    bitmapEraser = null;
                }

                mActivity.shouldReLoadPicture(bitmap);
                onCancel();

            }

            @Override
            public void onError(int i, String msg) {

            }

            @Override
            public void onReady() {
                setPaintSize(PAINT_SEEK_BAR_MAX / 2);
            }

            @Override
            public void onSelectedItem(GraffitiSelectableItem selectableItem, boolean selected) {

            }

            @Override
            public void onCreateSelectableItem(GraffitiView.Pen pen, float x, float y) {
                if (pen == GraffitiView.Pen.TEXT) {
                    createGraffitiText(null, x, y);
                }
            }
        });

        LogPrinter.i(TAG, "initGraffitiView : " + mainView);
        LinearLayout layout = (LinearLayout) mainView;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layout.addView(graffitiView, lp);

        graffitiView.setIsDrawableOutside(mGraffitiParams.mIsDrawableOutside);
    }

    private void initData() {
        Bundle b = getArguments();
        picturePath = b.getString(BaseEditorManager.SRC_PIC_PATH, null);
        mGraffitiParams = b.getParcelable(KEY_PARAMS);
        BaseEditorManager.decodeBitmapAsync(picturePath,
                new BaseEditorManager.LoadListener() {
                    @Override
                    public void onLoadSuccess(Object o) {

                        srcPicture = (Bitmap) o;
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.onFragmentAttached();
                            }
                        });
                    }

                    @Override
                    public void onLoadFailed() {

                    }
                });
    }

    private void setPaintSize(float size) {
        graffitiView.setPaintSize(size);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.cancel) {
            onCancel();


        } else if (i == R.id.save) {
            if (graffitiView.isModified()) {
                graffitiView.save();
            }


        } else if (i == R.id.revocation) {
            graffitiView.undo();

        } else if (i == R.id.cancel_revocation) {
            graffitiView.revertUndo();

        } else if (i == R.id.color_white) {
            onColorPicked(PaintUtils.COLOR_WHITE);

        } else if (i == R.id.color_gray) {
            onColorPicked(PaintUtils.COLOR_GRAY);

        } else if (i == R.id.color_black) {
            onColorPicked(PaintUtils.COLOR_BLACK);

        } else if (i == R.id.color_orange) {
            onColorPicked(PaintUtils.COLOR_ORANGE);

        } else if (i == R.id.color_yellow) {
            onColorPicked(PaintUtils.COLOR_YELLOW);

        } else if (i == R.id.color_green) {
            onColorPicked(PaintUtils.COLOR_GREEN);

        } else if (i == R.id.color_blue) {
            onColorPicked(PaintUtils.COLOR_BLUE);

        } else if (i == R.id.color_red) {
            onColorPicked(PaintUtils.COLOR_RED);

        } else if (i == R.id.draw_text) {
            graffitiView.setPen(GraffitiView.Pen.TEXT);

        } else if (i == R.id.draw_curve) {
            graffitiView.setShape(GraffitiView.Shape.HAND_WRITE);
            graffitiView.setPen(GraffitiView.Pen.HAND);

        } else if (i == R.id.draw_line) {
            graffitiView.setShape(GraffitiView.Shape.LINE);

        } else if (i == R.id.draw_arrow) {
            graffitiView.setShape(GraffitiView.Shape.ARROW);

        } else if (i == R.id.draw_circle) {
            graffitiView.setShape(GraffitiView.Shape.HOLLOW_CIRCLE);

        } else if (i == R.id.draw_rec) {
            graffitiView.setShape(GraffitiView.Shape.HOLLOW_RECT);

        } else if (i == R.id.color_picker) {
            onColorPickerClicked(true);

        } else {
        }
    }

    public void onColorPickerClicked(boolean show) {
        if (colorPanel != null) {
            colorPanel.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void onColorPicked(int colorIndex) {
        int paintColor = PaintUtils.getColor(mActivity, colorIndex);

        btnColorPicker.setBackgroundResource(PaintUtils.getColorPickerBackground(colorIndex));
        onColorPickerClicked(false);

        graffitiView.setColor(paintColor);
    }

    private void createGraffitiText(final GraffitiText graffitiText, final float x, final float y) {
        Activity activity = mActivity;
        if (mActivity.isFinishing()) {
            return;
        }

        boolean fullScreen = (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
        Dialog dialog = null;
        if (fullScreen) {
            dialog = new Dialog(activity,
                    android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        } else {
            dialog = new Dialog(activity,
                    android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.show();
        final Dialog finalDialog1 = dialog;
        activity.getWindow().getDecorView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                finalDialog1.dismiss();
            }
        });

        ViewGroup container = (ViewGroup) View.inflate(mActivity.getApplicationContext(), R.layout.graffiti_create_text, null);
        final Dialog finalDialog = dialog;
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });
        dialog.setContentView(container);

        final EditText textView = (EditText) container.findViewById(R.id.graffiti_selectable_edit);
        final View cancelBtn = container.findViewById(R.id.graffiti_text_cancel_btn);
        final TextView enterBtn = (TextView) container.findViewById(R.id.graffiti_text_enter_btn);

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = (textView.getText() + "").trim();
                if (TextUtils.isEmpty(text)) {
                    enterBtn.setEnabled(false);
                    enterBtn.setTextColor(0xffb3b3b3);
                } else {
                    enterBtn.setEnabled(true);
                    enterBtn.setTextColor(0xff232323);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textView.setText(graffitiText == null ? "" : graffitiText.getText());

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBtn.setSelected(true);
                finalDialog.dismiss();
            }
        });

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (cancelBtn.isSelected()) {
                    return;
                }
                String text = (textView.getText() + "").trim();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                if (graffitiText == null) {
                    graffitiView.addSelectableItem(new GraffitiText(graffitiView.getPen(), text, graffitiView.getPaintSize(), graffitiView.getColor().copy(),
                            0, graffitiView.getGraffitiRotateDegree(), x, y, graffitiView.getOriginalPivotX(), graffitiView.getOriginalPivotY()));
                } else {
                    graffitiText.setText(text);
                }
                graffitiView.invalidate();
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int currentProgress = seekBar.getProgress();
        if (graffitiView.isSelectedItem()) {
            graffitiView.setSelectedItemSize(currentProgress * 4);
        } else {
            setPaintSize(currentProgress);
        }
        setPaintSize(currentProgress);
    }


    /**
     * color pick button
     **/
    private Button btnColorWhite, btnColorGray, btnColorBlack, btnColorOrange,
            btnColorYellow, btnColorGreen, btnColorBlue, btnColorRed;

    private SeekBar paintSize;

    private Button btnColorPicker;

    private Button btnCancel, btnRevocation, btnCancelRevocation, btnSave;

    /**
     * the paint style
     **/
    private Button btnText, btnCurve, btnLine, btnArrow, btnCircle, btnRectangle;

    /**
     * color picker view, it shows when color picker clicked.
     **/
    private View colorPanel;

    @Override
    public void initPanelView(View v) {
        initGraffitiView();
        initColorPickerButton(v);
        initCenterPanel(v);
        initGraphicPanel(v);
        initBottomPanel(v);
    }

    private void initColorPickerButton(View parentView) {
        colorPanel = parentView.findViewById(R.id.color_panel);

        btnColorWhite = (Button) parentView.findViewById(R.id.color_white);
        btnColorGray = (Button) parentView.findViewById(R.id.color_gray);
        btnColorBlack = (Button) parentView.findViewById(R.id.color_black);
        btnColorOrange = (Button) parentView.findViewById(R.id.color_orange);
        btnColorYellow = (Button) parentView.findViewById(R.id.color_yellow);
        btnColorGreen = (Button) parentView.findViewById(R.id.color_green);
        btnColorBlue = (Button) parentView.findViewById(R.id.color_blue);
        btnColorRed = (Button) parentView.findViewById(R.id.color_red);

        btnColorWhite.setOnClickListener(this);
        btnColorGray.setOnClickListener(this);
        btnColorBlack.setOnClickListener(this);
        btnColorOrange.setOnClickListener(this);
        btnColorYellow.setOnClickListener(this);
        btnColorGreen.setOnClickListener(this);
        btnColorBlue.setOnClickListener(this);
        btnColorRed.setOnClickListener(this);
    }

    private void initCenterPanel(View parentView) {
        paintSize = (SeekBar) parentView.findViewById(R.id.seek);
        paintSize.setProgress(PAINT_SEEK_BAR_MAX / 2);
        paintSize.setMax(PAINT_SEEK_BAR_MAX);
        paintSize.setOnSeekBarChangeListener(this);

        btnColorPicker = (Button) parentView.findViewById(R.id.color_picker);
        btnColorPicker.setOnClickListener(this);
    }

    private void initGraphicPanel(View parentView) {
        btnText = (Button) parentView.findViewById(R.id.draw_text);
        btnCurve = (Button) parentView.findViewById(R.id.draw_curve);
        btnLine = (Button) parentView.findViewById(R.id.draw_line);
        btnArrow = (Button) parentView.findViewById(R.id.draw_arrow);
        btnCircle = (Button) parentView.findViewById(R.id.draw_circle);
        btnRectangle = (Button) parentView.findViewById(R.id.draw_rec);

        btnText.setOnClickListener(this);
        btnCurve.setOnClickListener(this);
        btnLine.setOnClickListener(this);
        btnArrow.setOnClickListener(this);
        btnCircle.setOnClickListener(this);
        btnRectangle.setOnClickListener(this);
    }

    private void initBottomPanel(View parentView) {
        btnCancel = (Button) parentView.findViewById(R.id.cancel);
        btnRevocation = (Button) parentView.findViewById(R.id.revocation);
        btnCancelRevocation = (Button) parentView.findViewById(R.id.cancel_revocation);
        btnSave = (Button) parentView.findViewById(R.id.save);

        btnCancel.setOnClickListener(this);
        btnRevocation.setOnClickListener(this);
        btnCancelRevocation.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onSave() {

    }

    @Override
    public void onCancel() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onCompareStart() {

    }

    @Override
    public void onCompareEnd() {

    }

    private void release() {
        mActivity = null;
        if (srcPicture != null) {
            srcPicture.recycle();
            srcPicture = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }
}
