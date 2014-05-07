package com.liris.datamatrixedcamera.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Jocelyn on 28/04/2014.
 */

public class TacheEnregistrementPhoto extends AsyncTask<byte[],Statut,Resultat> {

    private ProgressDialog dialog = null;
    private Activity activity;
    private ActiviteCamera.OnTacheEnregistrementDone callback;
    private byte[] data;

    TacheEnregistrementPhoto(Activity activity,ActiviteCamera.OnTacheEnregistrementDone callback)
    {
        this.activity = activity;
        this.callback = callback;

    }

    @Override
    protected void onPreExecute() {
        this.dialog = ProgressDialog.show(activity,"Enregistrement de la photo",
                "Enregistrement en cours...",
                true,true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        dialog.dismiss();
                        cancel(true);
                    }
                });
        Log.i("STATUT","création dialog et aff");

    }

    @Override
    protected Resultat doInBackground(byte[]... data) {
        try{
            publishProgress(Statut.INITIALISATION);
            this.data = data[0];
            Log.i("STATUT","changement init");
            Bitmap bmp = BitmapFactory.decodeByteArray(data[0], 0, data[0].length);
            int x = bmp.getWidth() / 2 - 255;
            int y = bmp.getHeight() / 2 - 255;
            Log.i("SubBMP", "bmp h = " + bmp.getHeight() + " bmp w = " + bmp.getWidth() + " x = " + x + " y = " + y);

            Bitmap subBmp = bmp.createBitmap(bmp,x,y,512,512);
            bmp.recycle();
            publishProgress(Statut.GRAYSCALE);
            Log.i("STATUT","changement gs");
            int c = 0,l = 0;

            Utils.bitmapToMat(subBmp, ActiviteCamera.image);

            Imgproc.cvtColor(ActiviteCamera.image, ActiviteCamera.image, Imgproc.COLOR_RGB2GRAY);

            Log.i("MAT IMAGE", ActiviteCamera.image.get(511,511)[0]+" ");
            ActiviteCamera.subBmp = subBmp;
            publishProgress(Statut.TERMINE);
            Log.i("STATUT","changement terminé");
            return Resultat.OK;
        }
        catch(Exception e){
            Log.i("Photo","Erreur : " + e.getMessage());
            e.printStackTrace();
            return Resultat.ERREUR;
        }

    }

    @Override
    protected void onPostExecute(Resultat resultat) {
        super.onPostExecute(resultat);
        dialog.dismiss();
        switch (resultat)
        {
            case OK:
                dialog.setMessage("Terminé !");
                dialog.show();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                callback.afficherDialog(this.data);

                break;
            case ERREUR:
                dialog.setMessage("ERREUR");
                dialog.show();
                break;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }



    @Override
    protected void onProgressUpdate(Statut... values) {
        super.onProgressUpdate(values);
        switch (values[0])
        {
            case INITIALISATION:
                dialog.setMessage("Initialisation des variables...");
                break;
            case GRAYSCALE:
                dialog.setMessage("Transformation en noir&blanc");
                break;
            case TERMINE:
                dialog.setMessage("Opération terminé !");
                break;
        }
    }
}
