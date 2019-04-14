package com.pure.camera.util;

import android.content.ContentResolver;
import android.net.Uri;

import com.pure.camera.CameraApp;
import com.pure.camera.data.MediaFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOperatorHelper {

    private static FileOperatorHelper mInstance;

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

        return updateDataBase(file) == null;
    }

    public Uri updateDataBase(MediaFile file) {
        ContentResolver cr = CameraApp.getGlobalContext().getContentResolver();
        return cr.insert(file.getUri(), file.toContentValues());
    }

}
