package uur.com.pinbook.LazyList;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.AsyncTask;
import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import uur.com.pinbook.Controller.BitmapConversion;
import uur.com.pinbook.R;

import static uur.com.pinbook.ConstantsModel.NumericConstant.*;
import static uur.com.pinbook.ConstantsModel.StringConstant.displayRounded;
import static uur.com.pinbook.ConstantsModel.StringConstant.groupsCacheDirectory;

public class ImageLoader {

    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    private Map<String, ImageView> imageViewsss = Collections.synchronizedMap(new WeakHashMap<String, ImageView>());

    //private Map<String, Map<ImageView, String>> imageViewsxx = Collections.synchronizedMap(new WeakHashMap< String, Map<ImageView, String>>());

    ExecutorService executorService;
    Handler handler = new Handler();//handler to display images in UI thread
    String fileChild;

    public ImageLoader(Context context, String fileChild) {
        this.fileChild = fileChild;
        fileCache = new FileCache(context, fileChild);
        executorService = Executors.newFixedThreadPool(5);
    }

    public void removeImageViewFromMap(String url){
        imageViewsss.remove(url);
    }

    final int stub_id = R.drawable.pin_throw68;

    public void DisplayImage( String url, ImageView imageView, String displayType) {

        imageViewsss.put(url, imageView);
        Bitmap bitmap = memoryCache.get(url);

        if (bitmap != null) {
            if(displayType == displayRounded)
                bitmap = BitmapConversion.getRoundedShape(bitmap, friendImageShown, friendImageShown, null);

            imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(url, imageView, displayType);

            if (url != null)
                new DownloadImageTask(imageView, displayType).execute(url);

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String displayType;

        public DownloadImageTask(ImageView bmImage, String displayType) {
            this.bmImage = bmImage;
            this.displayType = displayType;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            if(result != null) {
                if (displayType == displayRounded)
                    result = BitmapConversion.getRoundedShape(result, friendImageShown, friendImageShown, null);

                bmImage.setImageBitmap(result);
            }
        }
    }

    private void queuePhoto(String url, ImageView imageView, String displayType) {
        PhotoToLoad p = new PhotoToLoad(url, imageView, displayType);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);

        //from SD cache
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;

        //from web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            LazyUtils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public String displayType;

        public PhotoToLoad(String u, ImageView i, String displayType) {
            this.url = u;
            this.imageView = i;
            this.displayType = displayType;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad))
                    return;
                Bitmap bmp = getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if (imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {

        //String tag = imageViews.get(photoToLoad.imageView);

        String tag = photoToLoad.url;

        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                if(photoToLoad.displayType == displayRounded)
                    bitmap = BitmapConversion.getRoundedShape(bitmap, friendImageShown, friendImageShown, null);
                photoToLoad.imageView.setImageBitmap(bitmap);
            } else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}