package com.liris.datamatrixedcamera.app;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Jocelyn on 03/04/2014.
 */
class SavePhotoTask extends AsyncTask<byte[], String, String> {
    @Override
    protected String doInBackground(byte[]... photo) {
        File fichierPhoto=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "fichierPhoto.jpg");

        if (fichierPhoto.exists()) {
            fichierPhoto.delete();
        }


        for(int l=50;l<100;l++){
            for(int c = 50; c<100; c++) {
                int p = 640*l*3+c*3;

            }
        }
        try {
            FileOutputStream fos=new FileOutputStream(fichierPhoto.getPath());

            fos.write(photo[0]);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

        return(null);
    }
}
