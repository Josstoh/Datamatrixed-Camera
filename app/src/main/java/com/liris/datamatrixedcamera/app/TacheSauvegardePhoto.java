package com.liris.datamatrixedcamera.app;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.io.FileOutputStream;

import static org.opencv.highgui.Highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static org.opencv.highgui.Highgui.imread;

/**
 * Created by Jocelyn on 03/04/2014.
 */
class TacheSauvegardePhoto extends AsyncTask<Bitmap, String, String> {
    @Override
    protected String doInBackground(Bitmap... photo) {

        File fichierPhoto=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Photo1.jpg");

        if (fichierPhoto.exists()) {
            fichierPhoto.delete();
        }
        try {

            FileOutputStream out = new FileOutputStream(fichierPhoto.getPath());
            photo[0].compress(Bitmap.CompressFormat.JPEG, 90, out);
            MediaScannerConnection.scanFile(ActiviteCamera.activity,
                    new String[]{fichierPhoto.toString()}, null,new MediaScannerConnection.MediaScannerConnectionClient() {
                        @Override
                        public void onMediaScannerConnected() {}

                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
        return(null);
    }
}
