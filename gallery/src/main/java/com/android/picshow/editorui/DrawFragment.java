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
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.android.picshow.R;
import com.android.picshow.editor.BaseEditorManager;
import com.android.picshow.editor.filters.BaseEditor;
import com.android.picshow.editor.operate.OperateUtils;
import com.android.picshow.editor.scrawl.DrawAttribute;
import com.android.picshow.editor.scrawl.DrawingBoardView;
import com.android.picshow.editor.scrawl.PaintUtils;
import com.android.picshow.editor.scrawl.ScrawlTools;
import com.android.picshow.editor.utils.FileUtils;
import com.android.picshow.utils.LogPrinter;

/**
 * Created by yuntao.wei on 2018/5/16.
 * github:https://github.com/YuntaoWei
 * bLogPrinter:http://bLogPrinter.csdn.net/qq_17541215
 */

public class DrawFragment extends Fragment implements View.OnClickListener, BaseEditor, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = DrawFragment.class.getSimpleName();

    private DrawingBoardView drawView;

    ScrawlTools casualWaterUtil = null;
    String mPath;
    private EditActivity mActivity;

    private View mainView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (EditActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_draw, null);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;
        initView();
    }

    private void initView() {
        drawView = (DrawingBoardView) mainView.findViewById(R.id.drawView);
        LinearLayout drawLayout = (LinearLayout) drawView.findViewById(R.id.drawLayout);

        mActivity.onFragmentAttached();
    }

    private void initData() {
        Bundle b = getArguments();
        mPath = b.getString(BaseEditorManager.SRC_PIC_PATH, null);
        LogPrinter.i(TAG, "pic path : " + mPath);

        BaseEditorManager.decodeBitmapAsync(mPath, new BaseEditorManager.LoadListener() {

            @Override
            public void onLoadSuccess(Object o) {
                OperateUtils operateUtils = new OperateUtils(mActivity);
                //final Bitmap resizeBmp = operateUtils.compressionFiller((Bitmap)o, drawLayout);
                final Bitmap resizeBmp = (Bitmap)o;
                if(resizeBmp == null) {
                    LogPrinter.i(TAG, "some thing error occurs!");
                    return;
                }

                final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        resizeBmp.getWidth(), resizeBmp.getHeight());

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawView.setLayoutParams(layoutParams);

                        casualWaterUtil = new ScrawlTools(mActivity, drawView, resizeBmp);

                        onColorPicked(PaintUtils.COLOR_RED);
                    }
                });

            }

            @Override
            public void onLoadFailed() {

            }
        });
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.cancel) {
        } else if (i == R.id.save) {
        } else if (i == R.id.revocation) {
        } else if (i == R.id.cancel_revocation) {
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
        } else if (i == R.id.draw_curve) {
        } else if (i == R.id.draw_line) {
        } else if (i == R.id.draw_arrow) {
        } else if (i == R.id.draw_circle) {
        } else if (i == R.id.draw_rec) {
        } else if (i == R.id.color_picker) {
            onColorPickerClicked(true);

        } else {
        }
    }

    public void onColorPickerClicked(boolean show) {
        if(colorPanel != null) {
            colorPanel.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void onColorPicked(int colorIndex) {
        int paintColor = PaintUtils.getColor(mActivity, colorIndex);

        setPaint(paintColor, 1);

        btnColorPicker.setBackgroundResource(PaintUtils.getColorPickerBackground(colorIndex));
        onColorPickerClicked(false);
    }

    private void setPaint(int color, int size) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = size;
        Bitmap bm = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.marker, option);
        casualWaterUtil.creatDrawPainter(
                DrawAttribute.DrawStatus.PEN_WATER, bm, color);
    }

    private Button btnColorPicker;

    /** color picker view, it shows when color picker clicked.**/
    private View colorPanel;

    /**
     * init draw fragment bottom panel,it called by activity when fragment view created and bottom panel inflate success.
     * @param v see layout:draw_bottom_operation.xml
     */
    @Override
    public void initPanelView(View v) {
        initData();

        colorPanel = v.findViewById(R.id.color_panel);

        /* color pick button */
        v.findViewById(R.id.color_white).setOnClickListener(this);
        v.findViewById(R.id.color_gray).setOnClickListener(this);
        v.findViewById(R.id.color_black).setOnClickListener(this);
        v.findViewById(R.id.color_orange).setOnClickListener(this);
        v.findViewById(R.id.color_yellow).setOnClickListener(this);
        v.findViewById(R.id.color_green).setOnClickListener(this);
        v.findViewById(R.id.color_blue).setOnClickListener(this);
        v.findViewById(R.id.color_red).setOnClickListener(this);

        SeekBar paintSize = (SeekBar) v.findViewById(R.id.seek);
        paintSize.setProgress(50);
        paintSize.setMax(100);
        paintSize.setOnSeekBarChangeListener(this);

        v.findViewById(R.id.color_picker).setOnClickListener(this);


        /* the paint style */
        v.findViewById(R.id.draw_text).setOnClickListener(this);
        v.findViewById(R.id.draw_curve).setOnClickListener(this);
        v.findViewById(R.id.draw_line).setOnClickListener(this);
        v.findViewById(R.id.draw_arrow).setOnClickListener(this);
        v.findViewById(R.id.draw_circle).setOnClickListener(this);
        v.findViewById(R.id.draw_rec).setOnClickListener(this);

        v.findViewById(R.id.cancel).setOnClickListener(this);
        v.findViewById(R.id.revocation).setOnClickListener(this);
        v.findViewById(R.id.cancel_revocation).setOnClickListener(this);
        v.findViewById(R.id.save).setOnClickListener(this);

    }


    @Override
    public void onSave() {
        Bitmap bit = casualWaterUtil.getBitmap();

        FileUtils.writeImage(bit, mPath, 100);

        onCancel();
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int size = seekBar.getProgress() / 20;
        LogPrinter.i(TAG,"onStopTrackingTouch : " + size);
        if(size == 0)
            size = 1;
        setPaint(-1, size);
    }
}
