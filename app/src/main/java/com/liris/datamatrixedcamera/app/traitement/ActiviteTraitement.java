package com.liris.datamatrixedcamera.app.traitement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.liris.datamatrixedcamera.app.ActiviteCamera;
import com.liris.datamatrixedcamera.app.R;
import com.liris.datamatrixedcamera.app.lecture.LectureEtCorrection;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.util.Vector;

//import com.googlecode.javacv.cpp.opencv_core.CvScalar;
//import com.googlecode.javacv.cpp.opencv_core.IplImage;import android.widget.Toast;

public class ActiviteTraitement extends Activity {

    Uri url_images;
    Activity activity;
    private static final int SELECT_PICTURE = 1;
    private static final int ZONE_PICTURE = 2;
    private static final int Gray_Scale = 3;
    private static final int Binary = 4;
    private static final int Extraction = 5;
    private Bitmap apercu;
    private String selectedImagePath = null;
    private Mat submatrice;
    private Mat grayscaleMatrix;
    private Mat autoCorr;
    private Mat autocseuil;
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
        this.activity = this;
        setContentView(R.layout.activite_traitement);

        img = (ImageView) findViewById(R.id.ImageView01);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Création du dialog pour traitement de l'image
                LayoutInflater inflater = LayoutInflater.from(activity);
                View vue = inflater.inflate(R.layout.dialog_traitement_zoom,null);
                ImageView imApercu = (ImageView) vue.findViewById(R.id.apercu);
                imApercu.setImageBitmap(apercu);

                new AlertDialog.Builder(activity).setTitle("Zoom")
                        .setView(vue)
                        .setPositiveButton("Ok",null)
                        .show();
            }
        });
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mOpenCVCallBack)) {
            Log.e("TEST", "Cannot connect to OpenCV Manager");
        }

        ((Button) findViewById(R.id.bOneShot))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        doBinary();
                        doMaxExtraction();
                        doProfil();
                    }
                });


        // dialog == true si on ne passe pas par un fichier mais directement depuis la Mat image
        boolean dialog = getIntent().getBooleanExtra("dialog", false);
        if(!dialog)
        {
            ((Button) findViewById(R.id.bImporterImage))
                    .setOnClickListener(new OnClickListener() {
                        public void onClick(View arg0) {
                            // On récupère l'image
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                        }
                    });

            ((Button) findViewById(R.id.bZoomGrayscale))
                    .setOnClickListener(new OnClickListener() {
                        public void onClick(View arg0) {
                            // On la transforme en grayscale et on zoom
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
        }
        else
        {
            try {
                grayscaleMatrix=new Mat();
                grayscaleMatrix=ActiviteCamera.image;

                img.setImageBitmap(ActiviteCamera.subBmp);
            }
            catch(Exception e)
            {
                Log.e("TRAITEMENT","Erreur : " + e.getMessage());
                e.printStackTrace();
            }
        }


        ((Button) findViewById(R.id.bBinary))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {

                        // Binary
                        autoCorr= action.autoCorrelation( grayscaleMatrix);
                        autoCorr.convertTo(autoCorr, CvType.CV_8UC1);
                        Bitmap img_bitmp = Bitmap.createBitmap(autoCorr.cols(), autoCorr.rows(),Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(autoCorr, img_bitmp);
                        img.setImageBitmap(img_bitmp);
                        System.out.println("End");
                    }
                });

        ((Button) findViewById(R.id.bExtraction))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        // Extraction
                        // Mat x=autoCorr.submat(new org.opencv.core.Rect(150,150,200,200));
                        Mat x=autoCorr.submat(new org.opencv.core.Rect(0,0,512,512));
                        //Mat x=autoCorr.submat(new org.opencv.core.Rect(192,192,128,128));
                        //Mat x=autoCorr.submat(new org.opencv.core.Rect(192,192,320,320));
                        autocseuil= action.maxExtraction_( autoCorr,512,grayscaleMatrix);

                        //Mat ess_var=autocseuil.submat(new org.opencv.core.Rect(192,192,128,128));
                        autocseuil.convertTo(autocseuil, CvType.CV_8UC1);
                        Bitmap img_bitmp = Bitmap.createBitmap(autocseuil.cols(), autocseuil.rows(),Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(autocseuil, img_bitmp);
                        img.setImageBitmap(img_bitmp);
                        System.out.println("End");
                    }
                });

        ((Button) findViewById(R.id.bProfil))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        // Profil
                        Mat profil= action. profils( autocseuil);
                        //Mat ess_var=autocseuil.submat(new org.opencv.core.Rect(192,192,128,128));
                        profil.convertTo(profil, CvType.CV_8UC1);
                        apercu = Bitmap.createBitmap(profil.cols(), profil.rows(),Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(profil, apercu);
                        img.setImageBitmap(apercu);
                        LectureEtCorrection lec = new LectureEtCorrection();
                        int[][] datamatrix = new int [16][16];
                        int seuil =15;
                        do{
                            Log.i("Lecture",seuil+"");
                            datamatrix = calculDatamatrix(seuil);
                            for(int i = 0; i<16;i++) {
                                for (int j = 0; j < 16; j++) {
                                    System.out.print(datamatrix[i][j]);
                                }
                                System.out.print("\n");
                            }
                            seuil-=1;
                        }
                        while(lec.lecture(datamatrix) && seuil>0);

                    }

                });

    }

    private int[][] calculDatamatrix(int seuil)
    {
        double[]coordsV = Action.coordsVerticales;
        double[]coordsH = Action.coordsHorizontales;
        double[][]ndGFond = new double[16][16];
        double[][] ndGCentre = new double[16][16];
        int[][]datamatrix = new int[16][16];

        for(int i = 0; i<16;i++)
        {
            for(int j = 0; j<16; j++)
            {
                // Calcul du niveau de gris du fond de chaque case
                double nvFond=256;
                int it = (int)coordsV[j]+1;
                int it2 = (int) coordsH[i]+1;
                while (it<=coordsV[j+1]-1)
                {
                    nvFond = ( nvFond < autocseuil.get( (int)coordsH[i]+1,it )[0] ) ? nvFond : autocseuil.get( (int)coordsH[i]+1,it )[0];
                    nvFond = ( nvFond < autocseuil.get( (int)coordsH[i+1]-1,it)[0] ) ? nvFond : autocseuil.get( (int)coordsH[i+1]-1,it)[0];

                    it++;
                }
                while (it2<=coordsH[i+1]-1)
                {
                    nvFond = (nvFond + autocseuil.get(it2,(int)coordsV[j]+1)[0] + autocseuil.get((int)coordsV[j+1]-1,it2)[0]);
                    nvFond = ( nvFond < autocseuil.get(it2,(int)coordsV[j]+1)[0] ) ? nvFond : autocseuil.get(it2,(int)coordsV[j]+1)[0];
                    nvFond = ( nvFond < autocseuil.get( (int)coordsV[j+1]-1,it2 )[0] ) ? nvFond : autocseuil.get((int)coordsV[j+1]-1,it2)[0];
                    it2++;
                }
                Log.i("FOND", nvFond+"");

                // Calcul du niveau de gris du centre de chaque case
                double moyCentre = 0;
                int centreH = (int) (coordsH[i+1] - coordsH[i])/2;
                int centreV = (int) (coordsV[j+1] - coordsV[j])/2;
                //Log.i("CENTRE","centreH("+coordsH[i+1] +"-"+ coordsH[i]+")="+centreH+" centreV("+coordsV[j+1] +"-"+ coordsV[j]+")="+centreV);
                for(int l = (int)coordsH[i]+centreH -3;l<(int)coordsH[i]+centreH+3;l++)
                {
                    for(int c = (int)coordsV[j]+centreV -3;c<(int)coordsV[j]+centreV+3;c++)
                    {
                        moyCentre += autocseuil.get(l,c)[0];
                    }
                }
                moyCentre/=36;
                Log.i("CENTRE["+i+","+j+"]",moyCentre+"");

                ndGFond[i][j] = nvFond;
                ndGCentre[i][j] = moyCentre;
                double difference = moyCentre - nvFond;
                double ratio = moyCentre/nvFond;
                Log.i("DIFFERENCE["+i+","+j+"]","-------->"+difference+"<-------------");
                //Log.i("RATIO["+i+","+j+"]","-------->"+ratio+"<-------------");
                if(difference < seuil)
                {
                    datamatrix[i][j] = 0;
                }
                else {
                    datamatrix[i][j] = 1;
                }
            }
        }
        Log.i("DATAMATRIX","ndGFond");
        for(int i = 0; i<16;i++) {
            for (int j = 0; j < 16; j++) {
                System.out.print(ndGFond[i][j]);
            }
            System.out.print("\n");
        }
        Log.i("DATAMATRIX","ndGCentre");
        for(int i = 0; i<16;i++) {
            for (int j = 0; j < 16; j++) {
                System.out.print(ndGCentre[i][j]);
            }
            System.out.print("\n");
        }
        return datamatrix;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Action run = new Action();

        if (requestCode == SELECT_PICTURE) {
            if(data != null)
            {
                Uri selectedImageUri = data.getData();
                Log.i("Choix de l'image",selectedImageUri.toString());
                //selectedImagePath = getRealPathFromURI(this,selectedImageUri);
                selectedImagePath = RechercheFichier.getPath(this,selectedImageUri);
                Log.i("Choix de l'image","Image Path : " + selectedImagePath);
                img.destroyDrawingCache();
                img.setImageURI(selectedImageUri);
            }
        }
    }

    private void doBinary()
    {

        // Binary
        autoCorr= action.autoCorrelation( grayscaleMatrix);
        autoCorr.convertTo(autoCorr, CvType.CV_8UC1);
        Bitmap img_bitmp = Bitmap.createBitmap(autoCorr.cols(), autoCorr.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(autoCorr, img_bitmp);
        img.setImageBitmap(img_bitmp);
        System.out.println("End");
    }
    private void doMaxExtraction()
    {
        // Extraction
        Mat x=autoCorr.submat(new org.opencv.core.Rect(0,0,512,512));
        autocseuil= action.maxExtraction_( autoCorr,512,grayscaleMatrix);
        autocseuil.convertTo(autocseuil, CvType.CV_8UC1);
        Bitmap img_bitmp = Bitmap.createBitmap(autocseuil.cols(), autocseuil.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(autocseuil, img_bitmp);
        img.setImageBitmap(img_bitmp);
        System.out.println("End");
    }

    private void doProfil()
    {
        // Profil
        Mat profil= action. profils( autocseuil);
        profil.convertTo(profil, CvType.CV_8UC1);
        apercu = Bitmap.createBitmap(profil.cols(), profil.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(profil, apercu);
        img.setImageBitmap(apercu);
        System.out.println("End");


        int[][] datamatrix = calculDatamatrix(50);
        for(int i = 0; i<14;i++) {
            for (int j = 0; j < 14; j++) {
                System.out.print(datamatrix[i][j]);
            }
            System.out.print("\n");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
