package com.gdglima.multimedia.appmultimedia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.gdglima.multimedia.appmultimedia.camera.AlbumStorageDirFactory;
import com.gdglima.multimedia.appmultimedia.camera.BaseAlbumDirFactory;
import com.gdglima.multimedia.appmultimedia.camera.FroyoAlbumDirFactory;
import com.gdglima.multimedia.appmultimedia.utils.ImageUtils;
import com.gdglima.multimedia.appmultimedia.utils.LogUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class HomeActivity extends ActionBarActivity {

    private static final int  maxSize = 2048;
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final String TAG = "HomeActivity";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mCurrentPhotoPath;

    @InjectView(R.id.iviCamera)
    View iviCamera;

    @InjectView(R.id.iviPhoto)
    ImageView iviPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        events();
    }

    private void initUI() {
        ButterKnife.inject(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }
    private void events()
    {
        iviCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 takePhoto();
            }
        });
    }

    public void takePhoto()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;
        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }


        startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO_B);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case ACTION_TAKE_PHOTO_B:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    handleBigCameraPhoto();
                }else
                {
                    if(mCurrentPhotoPath!=null)
                    {
                        File fileRemove= FileUtils.getFile(mCurrentPhotoPath);
                        if(fileRemove.exists())
                        {
                            try
                            {
                                FileUtils.deleteQuietly(fileRemove);
                            }catch (Exception e)
                            {

                            }

                        }
                    }
                }
                break;
            }

            default:
                break;
        }
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int targetW = metrics.widthPixels;
        int targetH = metrics.heightPixels;

        LogUtils.LOGI(TAG,"targetW "+targetW+ " targetH "+targetH);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0))
        {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }
        LogUtils.LOGI(TAG,"scaleFactor "+scaleFactor);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable=true;
        bmOptions.inInputShareable=true;
        bmOptions.inSampleSize = scaleFactor;


        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Bitmap bmp2 = ImageUtils.rotateImage(bitmap, mCurrentPhotoPath);

        iviPhoto.setImageBitmap(bmp2);
        iviPhoto.invalidate();

    }
    //--------------------------------------------------------

    private String getAlbumName()
    {
        return getString(R.string.path_photos);
    }

    private File getAlbumDir()
    {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        LogUtils.LOGI(TAG, "error a crear directorio");
                        return null;
                    }
                }
            }

        } else {
            Log.v(TAG, "error en permisos de escriturar/lectura");
        }

        return storageDir;
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException
    {
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return false;
    }
}
