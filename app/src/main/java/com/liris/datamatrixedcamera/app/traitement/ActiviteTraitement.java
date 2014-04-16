package com.liris.datamatrixedcamera.app.traitement;

import java.util.Vector;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

//import com.googlecode.javacv.cpp.opencv_core.CvScalar;
//import com.googlecode.javacv.cpp.opencv_core.IplImage;import android.widget.Toast;

import com.liris.datamatrixedcamera.app.R;

public class ActiviteTraitement extends Activity {

    Uri url_images;
    private static final int SELECT_PICTURE = 1;
    private static final int ZONE_PICTURE = 2;
    private static final int Gray_Scale = 3;
    private static final int Binary = 4;
    private static final int Extraction = 5;
    private String selectedImagePath = null;
    private Mat submatrice;
    private Mat grayscaleMatrix;
    private Mat autoCorr;
    private ImageView img;
    private Action action = new Action();
    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Mat Image = Highgui.imread("/image.jpg");
                    if (Image == null) {
                        AlertDialog ad = new AlertDialog.Builder(ActiviteTraitement.this).create();
                        ad.setMessage("Fatal error: can't open /image.jpg!");
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_traitement);

        img = (ImageView) findViewById(R.id.ImageView01);
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mOpenCVCallBack)) {
            Log.e("TEST", "Cannot connect to OpenCV Manager");
        }
        ((Button) findViewById(R.id.Button01))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                    }
                });

        ((Button) findViewById(R.id.Button02))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {

                        try {
                            String path = selectedImagePath;

                            System.out.println("selected image path is " + selectedImagePath);

                            Mat m = Highgui.imread(path);
                            if (m == null) {
                                Log.i("Start", "--------Image Cannot be Loaded--------");
                            } else {
                                Log.i("Start", "--------Image Loaded Successfully--------");
                                System.out.println("je suis dans le code 2 ");

                                Log.i("Paramenres on matrix", "height " + m.height() + " width " + m.width() + " total = " + m.total() + " channels " + m.channels());
                                int NLs2, NCs2;
                                int NC = m.width();
                                int NL = m.height();
                                System.out.println("line_size " + NL);
                                System.out.println("colone_size " + NC);
                                NLs2 = NL / 2;
                                NCs2 = NC / 2;
                                int rowStart = NLs2 - 255;
                                int rowEnd = NLs2 + 256;
                                int colStart = NCs2 - 255;
                                int colEnd = NCs2 + 256;
                                submatrice = m.submat(rowStart, rowEnd, colStart, colEnd);
                                // Bitmap img_b;
                                System.out.println("probleme");
                                grayscaleMatrix = action.grayScale(submatrice);
                                Bitmap img_bitmp = Bitmap.createBitmap(submatrice.cols(), submatrice.rows(), Bitmap.Config.ARGB_8888);
                                Utils.matToBitmap(grayscaleMatrix, img_bitmp);
                                img.setImageBitmap(img_bitmp);
                            }




                        } catch (Exception e) {
                            System.err.print("Error in the code");
                            Log.i("Error in imreadddd", "Error in imreadxxxx");
                        }

                    }
                });

        ((Button) findViewById(R.id.Button04))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {

                        Intent intent1 = new Intent();
                        intent1.setType("image/*");
                        intent1.setAction(Intent.ACTION_MAIN);
                        startActivityForResult(intent1, Binary);

                    }
                });

        ((Button) findViewById(R.id.Button05))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {

                        Intent intent1 = new Intent();
                        intent1.setType("image/*");
                        intent1.setAction(Intent.ACTION_MAIN);
                        startActivityForResult(intent1, Extraction);
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Action run = new Action();

        if (requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            Log.i("Choix de l'image",selectedImageUri.toString());
            selectedImagePath = getRealPathFromURI(this,selectedImageUri);
            Log.i("Choix de l'image","Image Path : " + selectedImagePath);
            img.destroyDrawingCache();
            img.setImageURI(selectedImageUri);

        }


        if (requestCode == ZONE_PICTURE) {

            try {
                String path = selectedImagePath;
                System.out.println("selected image path is " + selectedImagePath);
                Mat m = Highgui.imread(path);
                if (m == null) {
                    Log.i("Start", "--------Image Cannot be Loaded--------");
                } else {
                    Log.i("Start", "--------Image Loaded Successfully--------");
                    System.out.println("je suis dans le code 2 ");

                    Log.i("Paramenres on matrix", "height " + m.height() + " width " + m.width() + " total = " + m.total() + " channels " + m.channels());
                    int NLs2, NCs2;
                    int NC = m.width();
                    int NL = m.height();
                    System.out.println("line_size " + NL);
                    System.out.println("colone_size " + NC);
                    NLs2 = NL / 2;
                    NCs2 = NC / 2;
                    int rowStart = NLs2 - 255;
                    int rowEnd = NLs2 + 256;
                    int colStart = NCs2 - 255;
                    int colEnd = NCs2 + 256;
                    submatrice = m.submat(rowStart, rowEnd, colStart, colEnd);
                    // Bitmap img_b;
                    System.out.println("probleme");
                    grayscaleMatrix = run.grayScale(submatrice);
                    Bitmap img_bitmp = Bitmap.createBitmap(submatrice.cols(), submatrice.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(grayscaleMatrix, img_bitmp);
                    img.setImageBitmap(img_bitmp);
                }
            } catch (Exception e) {
                System.err.print("Error in the code");
                Log.i("Erreur Grayscale", "Erreur lors de la conversion en grayscale : " + e.getMessage());
            }

        }







        if (requestCode == Gray_Scale) {
            grayscaleMatrix = run.grayScale(submatrice);
            Bitmap img_bitmp = Bitmap.createBitmap(submatrice.cols(), submatrice.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(grayscaleMatrix, img_bitmp);
            img.setImageBitmap(img_bitmp);
        }

        if (requestCode == Binary) {
            autoCorr = run.autoCorrelation(grayscaleMatrix);
            autoCorr.convertTo(autoCorr, CvType.CV_8UC1);
            Bitmap img_bitmp = Bitmap.createBitmap(autoCorr.cols(), autoCorr.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(autoCorr, img_bitmp);
            img.setImageBitmap(img_bitmp);
            System.out.println("End");
        }


        if (requestCode == Extraction) {
            // Mat x=autoCorr.submat(new org.opencv.core.Rect(150,150,200,200));
            // Mat x=autoCorr.submat(new org.opencv.core.Rect(0,0,512,512));
            //Mat x=autoCorr.submat(new org.opencv.core.Rect(192,192,128,128));
            //Mat x=autoCorr.submat(new org.opencv.core.Rect(192,192,320,320));
            // Mat autocseuil= run.maxExtraction_( autoCorr,512,grayscaleMatrix);

            //Mat ess_var=autocseuil.submat(new org.opencv.core.Rect(192,192,128,128));
            //autocseuil.convertTo(autocseuil, CvType.CV_8UC1);
            //Bitmap img_bitmp = Bitmap.createBitmap(autocseuil.cols(), autocseuil.rows(),Bitmap.Config.ARGB_8888);
            //Utils.matToBitmap(autocseuil, img_bitmp);
            //img.setImageBitmap(img_bitmp);
            System.out.println("End");
        }

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            Log.i("Choix de l'image", ((Boolean) cursor.isNull(0)).toString());
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            Log.i("Choix de l'image", "column_index = " + String.valueOf(column_index));
            cursor.moveToFirst();
            return cursor.getString(column_index);

        }
        catch(Exception e) {
            Log.i("Choix de l'image","Erreur lors de la conversion : " + e.getMessage());
            return null;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }

        }
    }

    public Bary remplir(Mat input, int l, int c) {

        int N, n, sommeL, sommeC;
        N = n = sommeL = sommeC = 0;
        Vector<Bary> blob = new Vector<Bary>();
        blob.add(new Bary(l, c, 0));
        int ncourant = 0;
        while (ncourant <= n) {
            l = blob.get(ncourant).l;
            c = blob.get(ncourant).c;

            if (input.get(l, c)[0] > 128) {// point non encore traitï¿½
                N++;
                input.put(l, c, 100);
                sommeL = sommeL + l;
                sommeC = sommeC + c;
            }

            // 4-connex

            if (input.get(l - 1, c)[0] > 128) {
                blob.add(new Bary(l - 1, c, 0));
                n++;

            }

            if (input.get(l, c - 1)[0] > 128) {
                blob.add(new Bary(l, c - 1, 0));
                n++;
            }
            if (input.get(l, c + 1)[0] > 128) {
                blob.add(new Bary(l, c + 1, 0));
                n++;
            }

            if (input.get(l + 1, c)[0] > 128) {
                blob.add(new Bary(l + 1, c, 0));
                n++;
            }

            //8-connex
            if (input.get(l - 1, c - 1)[0] > 128) {
                blob.add(new Bary(l - 1, c - 1, 0));
                n++;
            }

            if (input.get(l - 1, c + 1)[0] > 128) {
                blob.add(new Bary(l - 1, c + 1, 0));
                n++;
            }
            if (input.get(l + 1, c - 1)[0] > 128) {
                blob.add(new Bary(l + 1, c - 1, 0));
                n++;
            }

            if (input.get(l + 1, c + 1)[0] > 128) {
                blob.add(new Bary(l + 1, c + 1, 0));
                n++;
            }

            ncourant++;
        }

        return new Bary(Math.round(sommeL / N), Math.round(sommeC / N), 0);


    }
//Extraction des parametres de la grille
}
