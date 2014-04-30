package com.liris.datamatrixedcamera.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Jocelyn on 28/04/2014.
 */

public class TacheEnregistrementPhoto extends AsyncTask<byte[],Statut,Resultat> {

    private ProgressDialog dialog = null;
    private Activity activity;
    private ActiviteCamera.OnTacheEnregistrementDone callback;

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
            Log.i("STATUT","changement init");
            Bitmap bmp = BitmapFactory.decodeByteArray(data[0], 0, data[0].length);
            int x = bmp.getWidth() / 2 - 255;
            int y = bmp.getHeight() / 2 - 255;
            Log.i("SubBMP", "bmp h = " + bmp.getHeight() + " bmp w = " + bmp.getWidth() + " x = " + x + " y = " + y);

            Bitmap subBmp = bmp.createBitmap(bmp,x,y,512,512);
            bmp.recycle();
            int[] pixels = new int[subBmp.getWidth()*subBmp.getWidth()];
            subBmp.getPixels(pixels,0,subBmp.getWidth(),0,0,subBmp.getWidth(),subBmp.getHeight());
            publishProgress(Statut.GRAYSCALE);
            Log.i("STATUT","changement gs");
            int c = 0,l = 0;
            for(int i = 0;i<pixels.length;i++) {
                c=i%512;
                l=i/512;

                //Log.w("MAT IMAGE",i+"i " + c + "c " + l + "l");
                int red = Color.red(pixels[i]);
                int blue = Color.blue(pixels[i]);
                int green = Color.green(pixels[i]);
                float grayscale = (float) (0.21*red + 0.71*green + 0.07*blue);
                //pixels[i] = grayscale;

                ActiviteCamera.image.put(l, c, pixels[0]);
            }

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
                callback.afficherDialog();

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
