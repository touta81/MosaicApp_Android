package com.example.tadatouta.myapplication;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DownloadTask extends AsyncTask<String,Void,String> {
    private Listener listener;
    @Override
    protected String doInBackground(String...urlSt){
        String produceMosaicArt="";
        final StringBuilder result = new StringBuilder();

        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlSt[0]);

            // HttpURLConnection インスタンス生成
            urlConnection = (HttpURLConnection) url.openConnection();

            // タイムアウト設定
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(20000);

            // リクエストメソッド
            urlConnection.setRequestMethod("GET");

            // リダイレクトを自動で許可しない設定
            urlConnection.setInstanceFollowRedirects(false);

            // ヘッダーの設定(複数設定可能)
            urlConnection.setRequestProperty("Accept-Language", "jp");

            // 接続
            urlConnection.connect();

            int resp = urlConnection.getResponseCode();

            switch (resp){
                case HttpURLConnection.HTTP_OK:
                    InputStream is = null;
                    BufferedReader br;
                    String line;

                    try{
                        is = urlConnection.getInputStream();
                        br=new BufferedReader(new InputStreamReader(is));
                        while ((line=br.readLine())!=null){
                               result.append(line);
                        }
                        produceMosaicArt=result.toString();
                        is.close();
                    } catch(IOException e){
                        e.printStackTrace();
                    } finally{
                        if(is != null){
                            is.close();
                        }
                    }
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return produceMosaicArt;
    }
    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(String pMosaic);
    }
}
