package com.example.tadatouta.myapplication;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadJson extends AsyncTask<ParamsJson,Void,Void> {
    @Override
    protected Void doInBackground(ParamsJson...parm){
        ParamsJson params=parm[0];
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
            OutputStreamWriter outStreamJson = null;


            try {
                outStreamJson = new OutputStreamWriter(httpConn.getOutputStream());
                outStreamJson.write(params.json);
                outStreamJson.flush();
                Log.d("Server","Posted");
            } catch (IOException e) {
                // POST送信エラー
                e.printStackTrace();
            } finally {
                if (outStreamJson != null) {
                    outStreamJson.close();
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