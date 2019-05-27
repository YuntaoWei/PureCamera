package com.android.picshow.editor.crop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.picshow.R;
import com.android.picshow.editor.BaseEditorManager;


public class CropImageView extends FrameLayout implements CropOperationListener {

    private static final String TAG = CropImageView.class.getSimpleName();

    private static final Rect EMPTY_RECT = new Rect();

    public static final int DEFAULT_GUIDELINES = 1;
    public static final boolean DEFAULT_FIXED_ASPECT_RATIO = false;
    public static final int DEFAULT_ASPECT_RATIO_X = 1;
    public static final int DEFAULT_ASPECT_RATIO_Y = 1;

    private static final int DEFAULT_IMAGE_RESOURCE = 0;

    private static final String DEGREES_ROTATED = "DEGREES_ROTATED";

    private ImageView mImageView;
    private CropOverlayView mCropOverlayView;

    private Bitmap originalBm;
    private Bitmap mBitmap;
    private int mDegreesRotated = 0;

    private int mLayoutWidth;
    private int mLayoutHeight;

    private int mGuidelines = DEFAULT_GUIDELINES;
    private boolean mFixAspectRatio = DEFAULT_FIXED_ASPECT_RATIO;
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_X;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_Y;
    private int mImageResource = DEFAULT_IMAGE_RESOURCE;

    private int picWidth, picHeight;

    private int DEFAULT_MODE = BaseEditorManager.ROTATE_MODE;
    private int currentMode = DEFAULT_MODE;
    private int rotateX, rotateY;

    public CropImageView(Context context) {
        super(context);
        init(context);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CropImageView, 0, 0);

        try {
            mGuidelines = ta.getInteger(R.styleable.CropImageView_guidelines, DEFAULT_GUIDELINES);
            mFixAspectRatio = ta.getBoolean(R.styleable.CropImageView_fixAspectRatio,
                    DEFAULT_FIXED_ASPECT_RATIO);
            mAspectRatioX = ta.getInteger(R.styleable.CropImageView_aspectRatioX, DEFAULT_ASPECT_RATIO_X);
            mAspectRatioY = ta.getInteger(R.styleable.CropImageView_aspectRatioY, DEFAULT_ASPECT_RATIO_Y);
            mImageResource = ta.getResourceId(R.styleable.CropImageView_imageResource, DEFAULT_IMAGE_RESOURCE);
        } finally {
            ta.recycle();
        }

        init(context);
    }

    @Override
    public Parcelable onSaveInstanceState() {

        final Bundle bundle = new Bundle();

        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt(DEGREES_ROTATED, mDegreesRotated);


        return bundle;

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {

            final Bundle bundle = (Bundle) state;

            if (mBitmap != null) {
                mDegreesRotated = bundle.getInt(DEGREES_ROTATED);
                int tempDegrees = mDegreesRotated;
                Log.i(TAG, " onRestoreInstanceState");
                rotateImage(mDegreesRotated);
                mDegreesRotated = tempDegrees;
            }

            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));

        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (mBitmap != null) {
            final Rect bitmapRect = ImageViewUtil.getBitmapRectCenterInside(mBitmap, this);
            mCropOverlayView.setBitmapRect(bitmapRect);
        } else {
            mCropOverlayView.setBitmapRect(EMPTY_RECT);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (mBitmap != null) {

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            if (heightSize == 0)
                heightSize = mBitmap.getHeight();

            int desiredWidth;
            int desiredHeight;

            double viewToBitmapWidthRatio = Double.POSITIVE_INFINITY;
            double viewToBitmapHeightRatio = Double.POSITIVE_INFINITY;

            if (widthSize < mBitmap.getWidth()) {
                viewToBitmapWidthRatio = (double) widthSize / (double) mBitmap.getWidth();
            }
            if (heightSize < mBitmap.getHeight()) {
                viewToBitmapHeightRatio = (double) heightSize / (double) mBitmap.getHeight();
            }

            if (viewToBitmapWidthRatio != Double.POSITIVE_INFINITY || viewToBitmapHeightRatio != Double.POSITIVE_INFINITY) {
                if (viewToBitmapWidthRatio <= viewToBitmapHeightRatio) {
                    desiredWidth = widthSize;
                    desiredHeight = (int) (mBitmap.getHeight() * viewToBitmapWidthRatio);
                } else {
                    desiredHeight = heightSize;
                    desiredWidth = (int) (mBitmap.getWidth() * viewToBitmapHeightRatio);
                }
            } else {
                desiredWidth = mBitmap.getWidth();
                desiredHeight = mBitmap.getHeight();
            }

            int width = getOnMeasureSpec(widthMode, widthSize, desiredWidth);
            int height = getOnMeasureSpec(heightMode, heightSize, desiredHeight);

            mLayoutWidth = width;
            mLayoutHeight = height;

            final Rect bitmapRect = ImageViewUtil.getBitmapRectCenterInside(mBitmap.getWidth(),
                    mBitmap.getHeight(),
                    mLayoutWidth,
                    mLayoutHeight);
            mCropOverlayView.setBitmapRect(bitmapRect);

            mCropOverlayView.setBitmapSize(mBitmap.getWidth(), mBitmap.getHeight());

            setMeasuredDimension(mLayoutWidth, mLayoutHeight);

        } else {

            mCropOverlayView.setBitmapRect(EMPTY_RECT);
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);

        if (mLayoutWidth > 0 && mLayoutHeight > 0) {
            final ViewGroup.LayoutParams origparams = this.getLayoutParams();
            origparams.width = mLayoutWidth;
            origparams.height = mLayoutHeight;
            setLayoutParams(origparams);
        }
    }

    public int getImageResource() {
        return mImageResource;
    }

    public void showOriginalPic() {
        mImageView.setImageBitmap(originalBm);
    }

    public void showEditedPic() {
        mImageView.setImageBitmap(mBitmap);
    }

    public void setOriginalBm(Bitmap bm) {
        originalBm = bm.copy(bm.getConfig(), false);
        rotateX = originalBm.getWidth() / 2;
        rotateY = originalBm.getHeight() / 2;
    }

    public void setImageBitmap(Bitmap bitmap) {

        mBitmap = bitmap;

        picWidth = mBitmap.getWidth();
        picHeight = mBitmap.getHeight();

        mImageView.setImageBitmap(mBitmap);

        if (mCropOverlayView != null) {
            mCropOverlayView.resetCropOverlayView();
        }
    }

    public void setImageBitmap(Bitmap bitmap, ExifInterface exif) {

        if (bitmap == null) {
            return;
        }

        if (exif == null) {
            setImageBitmap(bitmap);
            return;
        }

        final Matrix matrix = new Matrix();
        final int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        int rotate = -1;

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }

        if (rotate == -1) {
            setImageBitmap(bitmap);
        } else {
            matrix.postRotate(rotate);
            final Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    matrix,
                    true);
            setImageBitmap(rotatedBitmap);
            bitmap.recycle();
        }
    }

    public void setImageResource(int resId) {
        if (resId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
            setImageBitmap(bitmap);
        }
    }

    private Bitmap cropIMG;

    public void revocation() {
        if (originalBm != null)
            setImageBitmap(originalBm);
    }

    public void cancelRevocation() {
        if (cropIMG != null)
            setImageBitmap(cropIMG);
    }

    public boolean isModify() {
        return mBitmap == cropIMG;
    }

    public Bitmap getResultImage() {

        return cropIMG.copy(cropIMG.getConfig(), true);
    }

    public Bitmap getCroppedImage() {

        final Rect displayedImageRect = ImageViewUtil.getBitmapRectCenterInside(mBitmap, mImageView);

        final float actualImageWidth = mBitmap.getWidth();
        final float displayedImageWidth = displayedImageRect.width();
        final float scaleFactorWidth = actualImageWidth / displayedImageWidth;

        final float actualImageHeight = mBitmap.getHeight();
        final float displayedImageHeight = displayedImageRect.height();
        final float scaleFactorHeight = actualImageHeight / displayedImageHeight;

        final float cropWindowX = Edge.LEFT.getCoordinate() - displayedImageRect.left;
        final float cropWindowY = Edge.TOP.getCoordinate() - displayedImageRect.top;
        final float cropWindowWidth = Edge.getWidth();
        final float cropWindowHeight = Edge.getHeight();

        final float actualCropX = cropWindowX * scaleFactorWidth;
        final float actualCropY = cropWindowY * scaleFactorHeight;
        final float actualCropWidth = cropWindowWidth * scaleFactorWidth;
        final float actualCropHeight = cropWindowHeight * scaleFactorHeight;

        final Bitmap croppedBitmap = Bitmap.createBitmap(mBitmap,
                (int) actualCropX,
                (int) actualCropY,
                (int) actualCropWidth,
                (int) actualCropHeight);

        return croppedBitmap;
    }

    public RectF getActualCropRect() {

        final Rect displayedImageRect = ImageViewUtil.getBitmapRectCenterInside(mBitmap, mImageView);

        final float actualImageWidth = mBitmap.getWidth();
        final float displayedImageWidth = displayedImageRect.width();
        final float scaleFactorWidth = actualImageWidth / displayedImageWidth;

        final float actualImageHeight = mBitmap.getHeight();
        final float displayedImageHeight = displayedImageRect.height();
        final float scaleFactorHeight = actualImageHeight / displayedImageHeight;

        final float displayedCropLeft = Edge.LEFT.getCoordinate() - displayedImageRect.left;
        final float displayedCropTop = Edge.TOP.getCoordinate() - displayedImageRect.top;
        final float displayedCropWidth = Edge.getWidth();
        final float displayedCropHeight = Edge.getHeight();

        float actualCropLeft = displayedCropLeft * scaleFactorWidth;
        float actualCropTop = displayedCropTop * scaleFactorHeight;
        float actualCropRight = actualCropLeft + displayedCropWidth * scaleFactorWidth;
        float actualCropBottom = actualCropTop + displayedCropHeight * scaleFactorHeight;

        actualCropLeft = Math.max(0f, actualCropLeft);
        actualCropTop = Math.max(0f, actualCropTop);
        actualCropRight = Math.min(mBitmap.getWidth(), actualCropRight);
        actualCropBottom = Math.min(mBitmap.getHeight(), actualCropBottom);

        final RectF actualCropRect = new RectF(actualCropLeft,
                actualCropTop,
                actualCropRight,
                actualCropBottom);

        return actualCropRect;
    }

    public void setFixedAspectRatio(boolean fixAspectRatio) {
        mCropOverlayView.setFixedAspectRatio(fixAspectRatio);
    }

    public void setGuidelines(int guidelines) {
        mCropOverlayView.setGuidelines(guidelines);
    }

    public void setAspectRatio(int aspectRatioX, int aspectRatioY) {
        mAspectRatioX = aspectRatioX == -1 ? picWidth : aspectRatioX;
        mAspectRatioY = aspectRatioY == -1 ? picHeight : aspectRatioY;
        Log.i(TAG, "setAspectRatio : " + mAspectRatioX + " x " + mAspectRatioY);

        mCropOverlayView.setAspectRatioX(mAspectRatioX);
        mCropOverlayView.setAspectRatioY(mAspectRatioY);
    }

    private float preAngle;

    public void rotateImage(float degrees) {

        if (mBitmap != null) {
            mBitmap = null;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        mBitmap = Bitmap.createBitmap(originalBm, 0, 0, originalBm.getWidth(), originalBm.getHeight(), matrix, true);

        cropIMG = mBitmap.copy(mBitmap.getConfig(), true);
        setImageBitmap(mBitmap);

        mDegreesRotated += degrees;
        mDegreesRotated = mDegreesRotated % 360;
    }


    public void rotateImage(int degrees, int offset) {

        /*if(mBitmap != null) {
            mBitmap = null;
        }*/

        float rotate = 0;
        if (preAngle > 0 && degrees > 0) {
            rotate = degrees - preAngle;
            preAngle = degrees;
        } else if (preAngle > 0 && degrees < 0) {
            rotate = preAngle + degrees;
            preAngle = rotate;
        } else if (preAngle < 0 && degrees < 0) {
            rotate = degrees - preAngle;
            preAngle = degrees;
        } else if (preAngle < 0 && degrees > 0) {
            rotate = degrees + preAngle;
            preAngle = rotate;
        } else if (preAngle == 0) {
            rotate = degrees;
            preAngle = rotate;
        }

        if (rotate > 45) {
            rotate = 45;
            preAngle = rotate;
        } else if (rotate < -45) {
            rotate = -45;
            preAngle = rotate;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

        setImageBitmap(mBitmap);

        mDegreesRotated += degrees;
        mDegreesRotated = mDegreesRotated % 360;
    }


    public void reverseImage(CropImageType.REVERSE_TYPE type) {
        Matrix matrix = new Matrix();

        if (type == CropImageType.REVERSE_TYPE.UP_DOWN) {
            matrix.postScale(1, -1);
        } else if (type == CropImageType.REVERSE_TYPE.LEFT_RIGHT) {
            matrix.postScale(-1, 1);
        }

        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        setImageBitmap(mBitmap);
    }

    public void setCropOverlayCornerBitmap(Bitmap bit) {
        mCropOverlayView.setCropOverlayCornerBitmap(bit);
    }

    public void setMode(int mode) {

        currentMode = mode;

        if (mode == DEFAULT_MODE) {
            mCropOverlayView.setVisibility(View.GONE);
        } else {
            mCropOverlayView.setVisibility(View.VISIBLE);
        }

    }


    private void init(Context context) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.crop_image_view, this, true);

        mImageView = (ImageView) v.findViewById(R.id.ImageView_image);

        setImageResource(mImageResource);
        mCropOverlayView = (CropOverlayView) v.findViewById(R.id.CropOverlayView);
        mCropOverlayView.setOnCropOperationListener(this);
        mCropOverlayView.setInitialAttributeValues(mGuidelines, mFixAspectRatio, mAspectRatioX, mAspectRatioY);
    }

    private static int getOnMeasureSpec(int measureSpecMode, int measureSpecSize, int desiredSize) {

        int spec;
        if (measureSpecMode == MeasureSpec.EXACTLY) {
            spec = measureSpecSize;
        } else if (measureSpecMode == MeasureSpec.AT_MOST) {
            spec = Math.min(desiredSize, measureSpecSize);
        } else {
            spec = desiredSize;
        }

        return spec;
    }


    @Override
    public void onCrop() {

        cropIMG = getCroppedImage();
        setImageBitmap(cropIMG);

    }

    public void recycle() {
        recycleBitmap(originalBm);
        recycleBitmap(mBitmap);
        recycleBitmap(cropIMG);
    }

    private void recycleBitmap(Bitmap bm) {

        if (bm != null) {
            bm.recycle();
            bm = null;
        }

    }

}
