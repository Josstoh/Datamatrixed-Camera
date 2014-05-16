package com.liris.datamatrixedcamera.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.liris.datamatrixedcamera.app.traitement.ActiviteTraitement;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class ActiviteCamera extends Activity implements SurfaceHolder.Callback {

    // Général
    static public Activity activity;
    static public Mat image;
    static  public Bitmap subBmp;

    // Constantes
    private String optionsTaillePhoto = "TAILLE_PHOTO";
    private String TAG = "datamatrixedcamera";
    private DisplayMetrics metrics = new DisplayMetrics();

    // Attributs
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private Camera camera;
    private int hauteurPhoto = -1,largeurPhoto = -1;
    private SoundPool soundPool;
    private int idSonPhoto;
    private Boolean loaded = false;
    private PhotoCallback callback;
    private int positionChoixTaillePhoto = -1;
    private int positionChoixTaillePreview = -1;
    private ImageView ivMire;
    private MenuItem focusAuto;
    private MenuItem focusMacro;

    // Attribut test pour openCV
    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Création de la matrice image
                   image = new Mat(512,512,CvType.CV_8UC1);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Attributions
        activity = this;
        this.callback = new PhotoCallback();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //Teste si OpenCV est installé
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mOpenCVCallBack))
        {
            Log.e("", "Cannot connect to OpenCV Manager");
            new AlertDialog.Builder(activity).setTitle("Oups !")
                    .setMessage("OpenCV Manager n'est pas installé sur votre appareil Android : cette application ne peut pas fonctionner ! :-( \n Vous allez être rediriger vers la fiche market de OpencCV Manager...")
                    .setPositiveButton("Ok !",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            final String appPackageName = "org.opencv.engine";
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    }).setNegativeButton("Sortez moi de là !!",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            })
                    .show();

        }

        // Pour mettre en plein écran
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);



        // Gestion surfaceView et surfaceHolder
        setContentView(R.layout.activite_camera);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();
        holder.addCallback(this);

        // Gestion mise en veille de l'ecran
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // Gestion Son
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
                loaded = true;
            }
        });
        idSonPhoto = soundPool.load(this, R.raw.mario_coin_sound, 1);

        // Controles
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        View vueControles = inflater.inflate(R.layout.controles,null);
        LayoutParams layoutParamsControl
                = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        this.addContentView(vueControles, layoutParamsControl);

        ImageButton bPhoto = (ImageButton) findViewById(R.id.bPhoto);
        ImageButton bMenu = (ImageButton) findViewById(R.id.bMenu);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(100, 100, Gravity.CENTER);
        ivMire = (ImageView) findViewById(R.id.ivMire);
        ivMire.setLayoutParams(layoutParams);
        Button bTraitement = (Button) findViewById(R.id.bTraitement);

        // Gestion des listeners
        bPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    camera.takePicture(callback,
                            null,
                            null,
                            callback);
                }
                catch(Exception e){
                    Log.e("Photo",e.getMessage());
                    finish();
                }

            }
        });

        bMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openOptionsMenu();
            }
        });

        bTraitement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ActiviteTraitement.class);
                startActivity(intent);
            }
        });


        // On restaure les paramètres enregistrés
        SharedPreferences settings = getSharedPreferences(optionsTaillePhoto, 0);
        largeurPhoto = settings.getInt("largeur",-1);
        hauteurPhoto = settings.getInt("hauteur", -1);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        focusMacro = menu.findItem(R.id.menu_focusMacro);
        focusAuto = menu.findItem(R.id.menu_focusAuto);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        final Camera.Parameters parameters = camera.getParameters();

        // Creation alertDialog et initialisations
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choisissez la taille");
        LayoutInflater inflater = LayoutInflater.from(activity);
        ArrayList<String> listTailles = new ArrayList<String>();


        switch(item.getItemId())
        {
            case R.id.menu_focusAuto:
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                camera.setParameters(parameters);
                focusAuto.setChecked(true);
                Log.i("FOCUS","Auto");
                break;

            case R.id.menu_focusMacro:
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                focusMacro.setChecked(true);
                Log.i("FOCUS","Macro");
                break;

            case R.id.menu_taillePhoto:
                List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
                for (Camera.Size i : pictureSizes)
                {
                   listTailles.add(i.height + " x " + i.width);
                }
                final String[] arrayTaillePhoto = new String[listTailles.size()];
                listTailles.toArray(arrayTaillePhoto);

                AlertDialog.Builder builder1 = builder.setSingleChoiceItems(arrayTaillePhoto,
                        positionChoixTaillePhoto,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {

                                positionChoixTaillePhoto = position;

                                String choix = arrayTaillePhoto[position];
                                int index = choix.indexOf("x");
                                int taille = choix.length();
                                hauteurPhoto = Integer.valueOf(choix.substring(0, index - 1));
                                largeurPhoto = Integer.valueOf(choix.substring(index + 2));

                                // Mise à jour des paramètres
                                Log.i("Item choisit", hauteurPhoto + " x " + largeurPhoto);
                                parameters.setPictureSize(largeurPhoto, hauteurPhoto);
                                camera.setParameters(parameters);


                                // adaptation de la mire
                                definirTailleMire();
                            }
                        }
                ).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNeutralButton("Enregistrer mon choix", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (largeurPhoto != -1 || hauteurPhoto != -1) {
                            SharedPreferences previewSizePref = getSharedPreferences(optionsTaillePhoto, MODE_PRIVATE);
                            SharedPreferences.Editor prefEditor = previewSizePref.edit();
                            prefEditor.putBoolean("existe",true);
                            prefEditor.putInt("largeur", largeurPhoto);
                            prefEditor.putInt("hauteur", hauteurPhoto);
                            prefEditor.commit();
                            Log.w("SETTINGS","J'enregistre " + largeurPhoto + " " + hauteurPhoto);
                        }

                    }
                });

                Dialog dialog = builder.create();
                dialog.show();
                return true;

            case R.id.menu_taillePreview:
                Camera.Size choixPreview = parameters.getPreviewSize();
                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
                int it = 0;
                for (Camera.Size i : previewSizes)
                {
                    listTailles.add(i.height + " x " + i.width);
                    if(i.equals(choixPreview))
                        positionChoixTaillePreview = it;
                    it++;
                }
                final String[] arrayTaillePreview = new String[listTailles.size()];
                listTailles.toArray(arrayTaillePreview);

                builder.setSingleChoiceItems(arrayTaillePreview,
                        positionChoixTaillePreview,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {

                                positionChoixTaillePreview = position;

                                String choix = arrayTaillePreview[position];
                                int index = choix.indexOf("x");
                                String hauteur = choix.substring(0, index - 1);
                                String largeur = choix.substring(index + 2);

                                // Mise à jour des paramètres
                                Log.i("Item choisit", hauteur + " x " + largeur);
                                parameters.setPreviewSize(Integer.valueOf(largeur), Integer.valueOf(hauteur));
                                camera.stopPreview();
                                camera.setParameters(parameters);
                                camera.startPreview();
                            }
                        }
                ).setPositiveButton("OK", null);
                builder.show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Met à jour l'affichage de la preview dans la bonne orientation
     * @param activity
     * @param cameraId
     * @param camera
     * @return
     */
    public static void setCameraDisplayOrientation(Activity activity,int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(holder);

            surfaceView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean b, Camera camera) {
                            Toast.makeText(activity, "FOCUS", 10).show();
                        }
                    });
                    return true;
                }
            });

            // Initialisations
            Camera.CameraInfo infos = new android.hardware.Camera.CameraInfo();
            Camera.Parameters parametres= camera.getParameters();

            // Désactiver le son par défaut
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, infos);
            if (Build.VERSION.SDK_INT > 17)
            {
                if(infos.canDisableShutterSound)
                    camera.enableShutterSound(false);
            }

            // Active les préférences
            if (largeurPhoto != -1)
            {
                parametres.setPictureSize(largeurPhoto,hauteurPhoto);
            }
            // Utiliser les tailles optimales et rotation de la photo finale quand il faut
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            int width,height;
            switch (rotation) {
                case Surface.ROTATION_0: degrees = 0; break;
                case Surface.ROTATION_90: degrees = 90; break;
                case Surface.ROTATION_180: degrees = 180; break;
                case Surface.ROTATION_270: degrees = 270; break;
            }
            if(degrees == 90 || degrees == 270)
            {
                width = metrics.widthPixels;
                height = metrics.heightPixels;
            }
            else
            {
                width = metrics.heightPixels;
                height = metrics.widthPixels;
            }

            Log.i("Taille Preview",width+"w "+height+"h");
            Camera.Size taillePreview = obtenirTaillePreviewOptimale(camera.getParameters().getSupportedPreviewSizes(),width,height);
            parametres.setPreviewSize(taillePreview.width,taillePreview.height);
            parametres.setJpegQuality(70);
            parametres.setRotation(Math.abs(degrees-90)%360);

            ArrayList<Camera.Area> areas = new ArrayList<Camera.Area>();
            areas.add(new Camera.Area(new Rect(-100,-100,100,100),600));
            if(parametres.getMaxNumFocusAreas()>0){
                parametres.setFocusAreas(areas);
                Log.i("FOCUS","FOCUS AREA utilisable"+ parametres.getMaxNumFocusAreas());
            }


            // MAJ des paramètres
            camera.setParameters(parametres);


            //Obtenir les tailles actuelles
            int it = 0;
            for (Camera.Size i : parametres.getSupportedPictureSizes())
            {
                if(i.equals(parametres.getPictureSize())){
                    positionChoixTaillePhoto = it;
                    break;
                }
                it++;
            }
            it = 0;
            for (Camera.Size i : parametres.getSupportedPreviewSizes())
            {
                if(i.equals(parametres.getPreviewSize())){
                    positionChoixTaillePreview = it;
                    break;
                }
                it++;
            }
            definirTailleMire();
            camera.startPreview();

        } catch (Exception e) {
            Log.e("Erreur",e.getMessage());
            camera.release();
            camera = null;
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        setCameraDisplayOrientation(this,Camera.CameraInfo.CAMERA_FACING_BACK,camera);
    }

    /**
     * Calcule la taille optimale pour la preview
     * @param tailles
     * @param w
     * @param h
     * @return
     */
    private Camera.Size obtenirTaillePreviewOptimale(List<Camera.Size> tailles, int w, int h) {

        final double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (tailles == null)
            return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Cherche une taille qui respecte le ratio donné
        for (Camera.Size size : tailles)
        {
            double ratio = (double) size.width / size.height;
            Log.i("Taille Preview", "Checking size " + size.width + "w " + size.height + "h"+" ratio =" + ratio + " minDiff before =" + minDiff);
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff)
            {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
            Log.i("Taille Preview", "minDiff after = " + minDiff + " optimalSize = " + optimalSize.width  + "w "+ optimalSize.height+"h");
        }
        // Si on trouve rien dessus, on ignore les entrées

        if (optimalSize == null)
        {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : tailles) {
                if (Math.abs(size.height - targetHeight) < minDiff)
                {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
      Log.i("Taille Preview", "Using size: " + optimalSize.width + "w " + optimalSize.height + "h");
        return optimalSize;
    }

    /**
    * Calcule la taille de la mire par rapport à la taille de la preview et de la photo pour correspondre à l'image finale
     */
    private void definirTailleMire()
    {
        if(camera != null)
        {
            Camera.Parameters parametres = camera.getParameters();
            Camera.Size taillePreview = parametres.getPreviewSize();
            Camera.Size taillePicture = parametres.getPictureSize();

            double largeur = (double) 512/taillePicture.width*taillePreview.width;
            double hauteur = (double) 512/taillePicture.height*taillePreview.height;
            Log.i("Taille Mire","taille photo : " + taillePicture.width + "w " + taillePicture.height + "h");
            Log.i("Taille Mire","taille preview : " + taillePreview.width + "w " + taillePreview.height + "h");
            Log.i("Taille Mire",largeur + "l " + hauteur +"h");
            int taille = (largeur>hauteur) ? (int)largeur : (int)hauteur;
            Log.i("Taille Mire","taille =" + taille);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(taille,taille, Gravity.CENTER);
            ivMire.setLayoutParams(params);
        }

    }
    // Classe appelé lors de la prise d'une photo
    class PhotoCallback implements Camera.ShutterCallback, Camera.PictureCallback {

        @Override
        public void onShutter() {
            // Getting the user sound settings
            AudioManager audioManager = (AudioManager) activity.getSystemService(activity.AUDIO_SERVICE);
            float actualVolume = (float) audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = actualVolume / maxVolume;
            // Is the sound loaded already?
            if (loaded) {
                soundPool.play(idSonPhoto, volume, volume, 1, 0, 1f);
                Log.e("Test", "Played sound");
            }

        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try{
                Toast.makeText(activity, "PHOTO", 10).show();
                new TacheEnregistrementPhoto(activity,new OnTacheEnregistrementDone()).execute(data);
                camera.startPreview();
            }
            catch(Exception e){
                Log.i("Photo","Erreur : " + e.getMessage());
                e.printStackTrace();
                recreate();
            }
        }
    }
    public class OnTacheEnregistrementDone
    {
        public void afficherDialog(byte[] data)
        {
            // Création du dialog pour traitement de l'image
            LayoutInflater inflater = LayoutInflater.from(activity);
            View vue = inflater.inflate(R.layout.dialog_traitement,null);
            ImageView apercu = (ImageView) vue.findViewById(R.id.apercu);
            apercu.setImageBitmap(subBmp);

            new AlertDialog.Builder(activity).setTitle("Voulez-vous continuer avec cette image ?")
                    .setView(vue)
                    .setPositiveButton("Oui",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(activity, ActiviteTraitement.class);
                            intent.putExtra("dialog",true);
                            startActivity(intent);
                        }
                    })
                    .setNeutralButton("Enregistrer sur l'image",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new TacheSauvegardePhoto().execute(subBmp);
                        }
                    })
                    .setNegativeButton("Non",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
    }
}
