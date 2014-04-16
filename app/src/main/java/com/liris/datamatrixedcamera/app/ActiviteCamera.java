package com.liris.datamatrixedcamera.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActiviteCamera extends Activity implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private Camera camera;
    static public Activity activity;
    private ListView listView = null;
    private View vueDialog = null;
    private PowerManager.WakeLock wakeLock;
    private String TAG = "datamatrixedcamera";
    private SoundPool soundPool;
    private int idSonPhoto;
    private Boolean loaded = false;
    private RawCallback callback;
    private int positionChoixTaillePhoto = -1;
    private int positionChoixTaillePreview = -1;
    private Camera.Size choixTaillePhoto = null;
    private Camera.Size choixTaillePreview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;
        this.callback = new RawCallback();

        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mOpenCVCallBack))
        {
            Log.e("TEST", "Cannot connect to OpenCV Manager");
        }
        // Pour mettre en plein écran
        if (Build.VERSION.SDK_INT < 16 || true) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);



        // Gestion surfaceView et surfaceHolder
        setContentView(R.layout.activite_camera);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();
        holder.addCallback(this);

        // Gestion mise en veille de l'ecran
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
        wakeLock.acquire();


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

        // Gestion des listeners
        bPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(callback,
                        null,
                        null,
                        callback);
            }
        });

        bMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openOptionsMenu();
            }
        });

    }
    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Mat Image = Highgui.imread("/image.jpg");
                    if (Image == null) {
                        AlertDialog ad = new AlertDialog.Builder(activity).create();
                        ad.setMessage("Fatal error: can't open /image.jpg!");
                    }
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    protected void onPause() {

        super.onPause();
        if (wakeLock != null) {
            Log.v(TAG, "Releasing wakelock");
            try {
                wakeLock.release();
            } catch (Throwable th) {
                // ignoring this exception, probably wakeLock was already released
            }
        } else {
            // should never happen during normal workflow
            Log.e(TAG, "Wakelock reference is null");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        final Camera.Parameters parameters = camera.getParameters();

        // Creation alertDialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choisissez la taille");
        LayoutInflater inflater = LayoutInflater.from(activity);
        ArrayList<String> listTailles = new ArrayList<String>();
        switch(item.getItemId())
        {
            case R.id.menu_taillePhoto:
                List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
                for (Camera.Size i : pictureSizes)
                {
                   listTailles.add(i.height + " x " + i.width);
                }
                final String[] arrayTaillePhoto = new String[listTailles.size()];
                listTailles.toArray(arrayTaillePhoto);

                builder.setSingleChoiceItems(arrayTaillePhoto,
                        positionChoixTaillePhoto,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {

                                positionChoixTaillePhoto = position;

                                String choix = arrayTaillePhoto[position];
                                int index = choix.indexOf("x");
                                int taille = choix.length();
                                String hauteur = choix.substring(0,index-1);
                                String largeur = choix.substring(index+2);

                                // Mise à jour des paramètres
                                Log.i("Item choisit", hauteur + " x " + largeur);
                                parameters.setPictureSize(Integer.valueOf(largeur),Integer.valueOf(hauteur));
                                camera.stopPreview();
                                camera.setParameters(parameters);
                                camera.startPreview();
                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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
                                int taille = choix.length();
                                String hauteur = choix.substring(0,index-1);
                                String largeur = choix.substring(index+2);

                                // Mise à jour des paramètres
                                Log.i("Item choisit", hauteur + " x " + largeur);
                                parameters.setPreviewSize(Integer.valueOf(largeur),Integer.valueOf(hauteur));
                                camera.stopPreview();
                                camera.setParameters(parameters);
                                camera.startPreview();
                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("positionChoixTaillePhoto",positionChoixTaillePhoto);
        outState.putInt("positionChoixTaillePreview",positionChoixTaillePreview);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //this.positionChoixTaillePreview = savedInstanceState.getInt("positionChoixTaillePreview");
        //this.positionChoixTaillePhoto = savedInstanceState.getInt("positionChoixTaillePhoto");
    }

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
        camera = Camera.open();
        try {
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
            // Désactiver le son par défaut
            Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
            if(info.canDisableShutterSound)
                camera.enableShutterSound(false);

            //Obtenir les tailles actuelles
            Camera.Parameters parametres= camera.getParameters();
            parametres.setJpegQuality(100);
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
            camera.setParameters(parametres);

        } catch (Exception e) {
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
        camera.stopPreview();
        setCameraDisplayOrientation(this,Camera.CameraInfo.CAMERA_FACING_BACK,camera);
        camera.startPreview();
    }

    class RawCallback implements Camera.ShutterCallback, Camera.PictureCallback {

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
            Boolean test = (data!=null);
            Integer s = ((data == null) ? 0 : data.length);
            Toast.makeText(activity, "PHOTO RAW", 10).show();
            Toast.makeText(activity, test.toString(), 10).show();
            Toast.makeText(activity, s.toString(), 10).show();

            Camera.Size size = camera.getParameters().getPictureSize();
            new TacheSauvegardePhoto().execute(data);
            camera.startPreview();
        }

        /**
         * Convertit YUV420 NV21 to RGB8888
         *
         * @param data byte array on YUV420 NV21 format.
         * @param width pixels width
         * @param height pixels height
         * @return a RGB8888 pixels int array. Where each int is a pixels ARGB.
         */

        public int[] convertYUV420_NV21toRGB8888(byte [] data, int width, int height) {
            int size = width*height;
            int offset = size;
            int[] pixels = new int[size];
            int u, v, y1, y2, y3, y4;

            // i percorre os Y and the final pixels
            // k percorre os pixles U e V
            for(int i=0, k=0; i < size; i+=2, k+=2) {
                y1 = data[i  ]&0xff;
                y2 = data[i+1]&0xff;
                y3 = data[width+i  ]&0xff;
                y4 = data[width+i+1]&0xff;

                u = data[offset+k  ]&0xff;
                v = data[offset+k+1]&0xff;
                u = u-128;
                v = v-128;

                pixels[i  ] = convertYUVtoRGB(y1, u, v);
                pixels[i+1] = convertYUVtoRGB(y2, u, v);
                pixels[width+i  ] = convertYUVtoRGB(y3, u, v);
                pixels[width+i+1] = convertYUVtoRGB(y4, u, v);

                if (i!=0 && (i+2)%width==0)
                    i+=width;
            }

            return pixels;
        }

        private int convertYUVtoRGB(int y, int u, int v) {
            int r,g,b;

            r = y + (int)1.402f*v;
            g = y - (int)(0.344f*u +0.714f*v);
            b = y + (int)1.772f*u;
            r = r>255? 255 : r<0 ? 0 : r;
            g = g>255? 255 : g<0 ? 0 : g;
            b = b>255? 255 : b<0 ? 0 : b;
            return 0xff000000 | (b<<16) | (g<<8) | r;
        }

        public void applyGrayScale(int [] pixels, byte [] data, int width, int height) {
            int p;
            int size = width*height;
            Log.i("Taille bytes",String.valueOf(data.length));
            Log.i("Taille donnée",String.valueOf(width)+" x " + String.valueOf(height));
            for(int i = 0; i < size; i++) {
                p = data[i] & 0xFF;
                pixels[i] = 0xff000000 | p<<16 | p<<8 | p;
            }
        }
    }
}
