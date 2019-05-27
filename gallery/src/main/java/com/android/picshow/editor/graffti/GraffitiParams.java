package com.android.picshow.editor.graffti;


import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;


public class GraffitiParams implements Parcelable {


    public String mImagePath;

    public String mSavePath;

    public boolean mSavePathIsDir;

    public String mEraserPath;

    public boolean mEraserImageIsResizeable = true;

    public boolean mIsDrawableOutside;

    public long mChangePanelVisibilityDelay = 800; //ms

    public float mAmplifierScale = 2.5f;

    public boolean mIsFullScreen = false;

    public float mPaintSize = -1;

    public static final Creator<GraffitiParams> CREATOR = new Creator<GraffitiParams>() {
        @Override
        public GraffitiParams createFromParcel(Parcel in) {
            GraffitiParams params = new GraffitiParams();
            params.mImagePath = in.readString();
            params.mSavePath = in.readString();
            params.mSavePathIsDir = in.readInt() == 1;
            params.mEraserPath = in.readString();
            params.mEraserImageIsResizeable = in.readInt() == 1;
            params.mIsDrawableOutside = in.readInt() == 1;
            params.mChangePanelVisibilityDelay = in.readLong();
            params.mAmplifierScale = in.readFloat();
            params.mIsFullScreen = in.readInt() == 1;
            params.mPaintSize = in.readFloat();

            return params;
        }

        @Override
        public GraffitiParams[] newArray(int size) {
            return new GraffitiParams[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mImagePath);
        dest.writeString(mSavePath);
        dest.writeInt(mSavePathIsDir ? 1 : 0);
        dest.writeString(mEraserPath);
        dest.writeInt(mEraserImageIsResizeable ? 1 : 0);
        dest.writeInt(mIsDrawableOutside ? 1 : 0);
        dest.writeLong(mChangePanelVisibilityDelay);
        dest.writeFloat(mAmplifierScale);
        dest.writeInt(mIsFullScreen ? 1 : 0);
        dest.writeFloat(mPaintSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private static DialogInterceptor sDialogInterceptor;

    public static void setDialogInterceptor(DialogInterceptor interceptor) {
        sDialogInterceptor = interceptor;
    }

    public static DialogInterceptor getDialogInterceptor() {
        return sDialogInterceptor;
    }

    public enum DialogType {
        SAVE, CLEAR_ALL, COLOR_PICKER;
    }

    public interface DialogInterceptor {

        boolean onShow(Activity activity, GraffitiView graffitiView, DialogType dialogType);

    }
}
