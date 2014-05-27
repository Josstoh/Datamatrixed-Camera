package com.liris.datamatrixedcamera.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
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

    private int posX;
    private int posY;
    private int taille;
    private boolean portrait;
    private Camera.Size taillePhoto;
    private Camera.Size taillePreview;

    TacheEnregistrementPhoto(Activity activity,ActiviteCamera.OnTacheEnregistrementDone callback,
                             boolean mode,boolean portrait,int x,int y, Camera.Size photo, Camera.Size preview)
    {
        this.activity = activity;
        this.callback = callback;
        this.taille = mode ? 1024 : 512;
        this.posX = x;
        this.posY = y;
        this.portrait = portrait;
        this.taillePhoto = photo;
        this.taillePreview = preview;
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
    }

    @Override
    protected Resultat doInBackground(byte[]... data) {
        try{
            publishProgress(Statut.INITIALISATION);
            this.data = data[0];
            Log.i("STATUT","changement init");
            Bitmap bmp = BitmapFactory.decodeByteArray(data[0], 0, data[0].length);
            int x,y;
            if(ActiviteCamera.bMode.isChecked()) {
                if (posX == -1) {
                    x = bmp.getWidth() / 2 - (taille / 2 - 1);
                    y = bmp.getHeight() / 2 - (taille / 2 - 1);
                } else {
                    if (portrait) {
                        if (posY == -2) {
                            x = 0;
                            y = 0;
                        } else {
                            if (posY == -3) {
                                x = 0;
                                y = taillePhoto.width - taillePhoto.height;
                            } else {
                                x = 0;
                                y = ((taillePhoto.width * posY)/taillePreview.width)-taillePhoto.height/2;
                            }
                        }
                    } else {
                        if (posX == -2) {
                            x = 0;
                            y = 0;
                        } else {
                            if (posX == -3) {
                                x = taillePhoto.width - taillePhoto.height;
                                y = 0;
                            } else {
                                x = ((taillePhoto.width * posX)/taillePreview.width)-taillePhoto.height/2;
                                y = 0;
                            }
                        }
                    }
                }
            }
            else
            {
                x = bmp.getWidth() / 2 - (taille/2-1);
                y = bmp.getHeight() / 2 - (taille/2-1);
            }

            Log.i("SubBMP", "bmp h = " + bmp.getHeight() + " bmp w = " + bmp.getWidth() + " x = " + x + " y = " + y);

            Bitmap subBmp = bmp.createBitmap(bmp,x,y,taille,taille);
            bmp.recycle();
            publishProgress(Statut.GRAYSCALE);
            Log.i("STATUT","changement gs");
            int c = 0,l = 0;

            Utils.bitmapToMat(subBmp, ActiviteCamera.image);

            Imgproc.cvtColor(ActiviteCamera.image, ActiviteCamera.image, Imgproc.COLOR_RGB2GRAY);
            Utils.matToBitmap(ActiviteCamera.image,subBmp);
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
