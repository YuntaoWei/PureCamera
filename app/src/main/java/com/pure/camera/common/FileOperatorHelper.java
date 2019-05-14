package com.pure.camera.common;

import android.content.ContentResolver;
import android.net.Uri;

import com.pure.camera.CameraApp;
import com.pure.camera.data.MediaFile;
import com.pure.camera.filter.BaseFilter;
import com.pure.camera.filter.engine.NoFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOperatorHelper {

    private static FileOperatorHelper mInstance;
    private final static boolean DEBUG_YUV = false;

    private FileOperatorHelper() {}

    public static FileOperatorHelper getInstance() {

        if(null == mInstance) {
            synchronized (FileOperatorHelper.class) {
                if(null == mInstance) {
                    mInstance = new FileOperatorHelper();
                }
            }
        }

        return mInstance;
    }

    public boolean saveFile(MediaFile file) {
        byte[] data = file.getFileData();
        File localFile = new File(MediaFile.DEFAUT_STORAGE_LOCATION, file.getDisplayName());
        if(!localFile.exists()) {
            try {
                localFile.getParentFile().mkdirs();
                localFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try(FileOutputStream fops = new FileOutputStream(localFile)) {
            fops.write(data, 0, data.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        updateDataBase(file);
        return true;
    }

    public boolean saveFile(MediaFile file, BaseFilter filter) {
        byte[] data = file.getFileData();

        boolean fileProcessed = false;
        if(!DEBUG_YUV && filter != null && !(filter instanceof NoFilter)) {
            //需要做滤镜处理，将原始camera数据进行相应的处理
            LogPrinter.i("test", "orientation : " + file.getFileOrientation());
            fileProcessed = filter.getFilterImage(data, file.getFileWidth(), file.getFileHeight(),
                    file.getFileOrientation(), file.getFilePath());
        }

        if(fileProcessed)
            updateDataBase(file);
        return fileProcessed;
    }

    public Uri updateDataBase(MediaFile file) {
        ContentResolver cr = CameraApp.getGlobalContext().getContentResolver();
        return cr.insert(file.getUri(), file.toContentValues());
    }

}
