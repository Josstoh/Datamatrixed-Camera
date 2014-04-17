package com.liris.datamatrixedcamera.app;

import android.app.AlertDialog;
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
class TacheSauvegardePhoto extends AsyncTask<byte[], String, String> {
    @Override
    protected String doInBackground(byte[]... photo) {
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, ActiviteCamera.activity, mOpenCVCallBack))
        {
            Log.e("TEST", "Cannot connect to OpenCV Manager");
        }
        File fichierPhoto=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Photo1.jpg");

        if (fichierPhoto.exists()) {
            fichierPhoto.delete();
        }
        MediaScannerConnection.scanFile(ActiviteCamera.activity,
                new String[]{fichierPhoto.toString()}, null,new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {

                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
        try {
            FileOutputStream fos=new FileOutputStream(fichierPhoto.getPath());
            fos.write(photo[0]);
            fos.close();
            MediaScannerConnection.scanFile(ActiviteCamera.activity,
                    new String[]{fichierPhoto.toString()}, null,new MediaScannerConnection.MediaScannerConnectionClient() {
                        @Override
                        public void onMediaScannerConnected() {

                        }

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
        Mat image = Highgui.imread(fichierPhoto.getAbsolutePath(),CV_LOAD_IMAGE_GRAYSCALE);
        Boolean b = image.empty();
        Log.i("Mat Resultat", b.toString());


        return(null);
    }
    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(ActiviteCamera.activity) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Mat Image = Highgui.imread("/image.jpg");
                    if (Image == null) {

                        Log.i("MAT","Fatal error: can't open /image.jpg!");
                    }
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
}
