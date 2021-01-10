package com.example.redditreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();
    private Object InputStream;

    public HttpHandler() {
    }

    public String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public Bitmap LoadImageFromWeb(String url, File file, int width)
    {
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            bitmap=BitmapFactory.decodeStream(is);
            //Log.e(TAG, "Bitmap: " + bitmap.getHeight());
            writeFile(bitmap,file,width);
            conn.disconnect();
            return bitmap;
        } catch (Throwable ex){
            ex.printStackTrace();
            return null;
        }
    }
        // our caching functions
// Find the dir to save cached images
    public File getCacheDirectory(Context context) {
        String sdState = android.os.Environment.getExternalStorageState();
        File cacheDir;

        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = android.os.Environment.getExternalStorageDirectory();

            // TODO : Change your diretcory here
            cacheDir = new File(sdDir, "Android/data/RedditReader/images");
        } else
            cacheDir = context.getCacheDir();

        if (!cacheDir.exists())
            try {
                cacheDir.mkdirs();
            }catch (SecurityException e){
                Log.e(TAG, "Security Exception");
                Toast.makeText(context, "Permissions filed, some functionality may not work. /n please, check permissions manually", Toast.LENGTH_LONG).show();
            }
        //Log.e(TAG, "Dir: " + cacheDir +" exists: " + cacheDir.exists());
        return cacheDir;
    }

    private void writeFile(Bitmap bmp, File f, int width) {
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(f);
            if(width > bmp.getWidth())bmp = scaleToFitWidth(bmp, width);
            bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception ex) {
            }
        }
    }
    private Bitmap scaleToFitWidth(Bitmap b, int width)
    {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }

}
