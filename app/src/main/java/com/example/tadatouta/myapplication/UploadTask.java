package com.example.tadatouta.myapplication;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.OutputStream;
import android.util.Log;

public class UploadTask extends AsyncTask<Params,Void,Void> {
    @Override
    protected Void doInBackground(Params...parm){
        Params params=parm[0];
        HttpURLConnection httpConn = null;
        Log.d("Server","connecting");
        try {
            // URL設定
            URL url = new URL(params.urlSt);

            Log.d("Url",params.urlSt);

            // HttpURLConnection
            httpConn = (HttpURLConnection) url.openConnection();

            // request POST
            httpConn.setRequestMethod("POST");

            // no Redirects
            httpConn.setInstanceFollowRedirects(false);

            // データを書き込む
            httpConn.setDoOutput(true);

            // 時間制限
            httpConn.setReadTimeout(100000);
            httpConn.setConnectTimeout(100000);

            // 接続
            httpConn.connect();
            Log.d("Server","Connected");
            // POSTデータ送信処理
            OutputStream outStream = null;

            ByteArrayOutputStream img = new ByteArrayOutputStream();
            params.bmp.compress(Bitmap.CompressFormat.PNG,100,img);

            try {
                outStream = new BufferedOutputStream(httpConn.getOutputStream());
                outStream.write(img.toByteArray());
                outStream.flush();
                Log.d("Server","Posted");
            } catch (IOException e) {
                // POST送信エラー
                e.printStackTrace();
            } finally {
                if (outStream != null) {
                    outStream.close();
                }
            }

            final int result=httpConn.getResponseCode();
            if(result==HttpURLConnection.HTTP_OK){
                Log.d("res",String.valueOf(result));
            }else {
                Log.d("res",String.valueOf(result));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
        return null;
    }
}
