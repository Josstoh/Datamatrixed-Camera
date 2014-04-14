package com.liris.datamatrixedcamera.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.liris.datamatrixedcamera.app.R;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


public class ActiviteCamera extends Activity implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private Camera camera;
    private Activity activity;
    private ListView listView;
    private View dialog = null;
    private PowerManager.WakeLock wakeLock;
    private String TAG = "datamatrixedcamera";
    private SoundPool soundPool;
    private int idSonPhoto;
    private Boolean loaded = false;
    private RawCallback callback1;
    private RawCallback callback2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;
        this.callback1 = new RawCallback();
        this.callback2 = new RawCallback();
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
                camera.takePicture(callback1,
                        null,
                        callback2,
                        new Camera.PictureCallback() {

                            @Override
                            public void onPictureTaken(byte[] photo, Camera camera) {
                                //new SavePhotoTask().execute(photo);
                                Toast.makeText(activity, "PHOTO ENREGISTREE", 10).show();
                                Toast.makeText(activity, String.valueOf(photo.length), 10).show();
                                Log.e("Test", "Picture Taken 2 ");
                                camera.startPreview();
                            }
                        }
                );
            }
        });

        bMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openOptionsMenu();
            }
        });

        //LayoutInflater inflater = LayoutInflater.from(mPreview.context);
        //this.dialog = inflater.inflate(R.layout.dialog_dialog_choix_taille,null);
        //AlertDialog.Builder builder = new AlertDialog.Builder(mPreview.context);
        //builder.setTitle("Choisissez la taille").setView(dialog).show();

        //LayoutInflater controlInflater = LayoutInflater.from(context);
        //View viewControl = controlInflater.inflate(R.layout.controles, null);
        //ViewGroup.LayoutParams layoutParamsControl
        //        = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        //        ViewGroup.LayoutParams.MATCH_PARENT);
        //((Activity) context).addContentView(viewControl, layoutParamsControl);
    }

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

        /*
		// Creation alertDialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialog = inflater.inflate(R.layout.dialog_dialog_choix_taille,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choisissez la taille");
        builder.setView(findViewById(R.layout.dialog_dialog_choix_taille));
        builder.show();
        this.listView = (ListView) findViewById(R.id.listView);

        // Recuperation des tailles dispos
		*/
        Camera.Parameters parameters = camera.getParameters();
        /*
        HashMap<String,String> mapSize;
        ArrayList<HashMap<String,String>> listItem = new ArrayList();
        */
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
/*
        for (Camera.Size i : previewSizes)
        {
            mapSize = new HashMap();
            mapSize.put("hauteur",String.valueOf(i.height));
            mapSize.put("largeur",String.valueOf(i.width));
            listItem.add(mapSize);
        }

        List<Integer> l = parameters.getSupportedPreviewFormats();
        for(Integer i : l)
        {
            Log.i("Supported Format",i.toString());
        }
        try{
            listView.setAdapter(new SimpleAdapter(context, listItem, R.layout.affichage_size,
                    new String[] {"hauteur","largeur"},new int[] {R.id.hauteur,R.id.largeur} ));
        }
        catch (Exception e) {
            Boolean b = (listView != null);
            Log.w("ERREUR",e.getMessage()+""+b.toString());
        }

		*/

        Camera.Size previewSize = previewSizes.get(1);
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        camera.setParameters(parameters);
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
                //soundPool.play(idSonPhoto, volume, volume, 1, 0, 1f);
                Log.e("Test", "Played sound");
            }

        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.e("Test", "Picture Taken");
            Toast.makeText(activity, "test", 10).show();
            Boolean test = (data!=null);
            Integer s = ((data == null) ? 0 : data.length);
            Toast.makeText(activity, "PHOTO RAW", 10).show();
            Toast.makeText(activity, test.toString(), 10).show();
            Toast.makeText(activity, s.toString(), 10).show();

            Camera.Size size = camera.getParameters().getPictureSize();
            //int[]photo = convertYUV420_NV21toRGB8888(data,size.width,size.height);
            int[] photo = new int[size.height*size.width];
            applyGrayScale(photo,data,size.width,size.height);

            for(int l=50;l<100;l++){
                for(int c = 50; c<100; c++) {
                    int p = 640*l+c;
                    photo[p]=0x80ffffff; // xxRRVVBB

                }
            }
            Bitmap bmp = Bitmap.createBitmap(photo, size.width, size.height, Bitmap.Config.ARGB_8888);
            File fichierPhoto=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "fichierPhoto.png");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(fichierPhoto);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                Log.i("ActiviteCamera","Photo PNG enregistré");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try{
                    out.close();
                } catch(Throwable ignore) {}
            }
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
            for(int i = 0; i < size; i++) {
                p = data[i] & 0xFF;
                pixels[i] = 0xff000000 | p<<16 | p<<8 | p;
            }
        }

        public Mat getSquare (int [] pixels, int width, int height, int squareSize)
        {
            //initialisation des compteurs pour parcourir la nouvelle matrice
            int linePx = 0;
            int columnPx = 0;

            // localisation du premier pixel (en haut à gauche) du carré
            int heightFirstPixel = (height/2) - (squareSize/2);
            int widthFirstPixel = (width/2) - (squareSize/2);

            //localisation du dernier pixel (en bas à droite) du carré
            int heightLastPixel = (height/2) + (squareSize/2);
            int widthLastPixel = (width/2) + (squareSize/2);

            //initialisation d'une matrice Mat (OpenCV)
            Mat matPixel = new Mat();

            //parcours du tableau de pixel pour récupérer la valeur des pixels constituant le carré
            for(int l = heightFirstPixel; l<heightLastPixel;l++)
            {
                for (int c = widthFirstPixel; c < widthLastPixel; c++)
                {
                    int p = l * width + c;
                    double yPixel = pixels[p];
                    matPixel.put(linePx,columnPx,yPixel);
                    columnPx = columnPx + 1;

                }
                linePx = linePx + 1;
            }


            return matPixel;

        }

        public void displaySquare (Mat matPixel)
        {
            // convert to bitmap:
            Bitmap bm = Bitmap.createBitmap(matPixel.cols(), matPixel.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(matPixel, bm);

            // find the imageview and draw it!
            ImageView iv = (ImageView) findViewById(R.id.apercuCarre);
            iv.setImageBitmap(bm);
        }
    }
}

