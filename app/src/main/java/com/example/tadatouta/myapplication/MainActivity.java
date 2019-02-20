package com.example.tadatouta.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.content.ContentValues;
import android.content.ContentResolver;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.system.ErrnoException;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    private Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
    private static final int RESULT_PICK_IMAGEFILE=1000;
    private final int REQUEST_PERMISSION=1000;
    private Feature features;
    private UploadTask uploadTask;
    private UploadJson uploadJson;
    private ArrayList<Bitmap> bmps = new ArrayList<>();
    private DownloadTask downloadTask;
    private CreateMosaic createMosaic;
    private String urlSt="10.0.2.2:80";
    final String LOG_TAG="Error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int startX = 0;
        int startY = 0;
        int size = 50;

        File root = FileMk();

        File[] files = root.listFiles();

        //ディレクトリ内の画像をリサイズして取得
        for (File file : files) {
            Bitmap bmp;
            bmp = BitmapFactory.decodeFile(file.getPath());
            bmp = Bitmap.createBitmap(bmp, startX, startY, size, size, null, true);
            bmps.add(bmp);
        }

        //画像ライブラリを開く
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,RESULT_PICK_IMAGEFILE);
    }

    //画像ライブラリからソース画像が開かれた時の処理
    public void onActivityResult(int requestCode,int resultCode,final Intent resultData){
        if(requestCode==RESULT_PICK_IMAGEFILE && resultCode==Activity.RESULT_OK){
            if(resultData.getData()!=null){

                //jsonを作る
                String json = "";
                features = new Feature();
                json = features.featuresJson(bmps);

                //jsonで素材画像群の特徴量を送る
                uploadJson = new UploadJson();
                uploadJson.execute(new ParamsJson[]{new ParamsJson("http://" + urlSt + "/post/", json)});

                //ソース画像を送る
                try {
                    Log.d("post","try post");
                    Uri uri=resultData.getData();
                    Bitmap bmp=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    uploadTask = new UploadTask();
                    uploadTask.execute(new Params[]{new Params("http://" + urlSt + "/source/", bmp)});

                }catch (IOException e){
                    e.printStackTrace();
                    Log.w(LOG_TAG,"" + ((ErrnoException) e.getCause()).errno);
                }finally {
                    downloadTask=new DownloadTask();
                    downloadTask.setListener(createListener());
                    downloadTask.execute("http://"+urlSt+"/out/");
                }
            }
        }
   }


    //ディレクトリの作成
    private File FileMk(){
        //ディレクトリのパス
        String path = Environment.getExternalStorageDirectory().getPath() + "/Mosaic";
        File root = new File(path);
        boolean mkfile=false;

        //ディレクトリが存在しなかったら作成
        if (!root.exists()) {
            try {
                mkfile=root.mkdir();
            }catch (Exception e){
                e.printStackTrace();
                Log.w("Ell","" + ((ErrnoException) e.getCause()).errno);
            }
            //結果をトースト
            if(mkfile){
                Toast toast =
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT);
                toast.show();
            }else {
                Toast toast=Toast.makeText(this,"failed",Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        return root;
    }

    // permissionの確認
    public void checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED){
            FileMk();
        }
        // 拒否していた場合
        else{
            requestLocationPermission();
        }
    }

    // 許可を求める
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);

        } else {
            Toast toast =
                    Toast.makeText(this, "アプリ実行に許可が必要です", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    REQUEST_PERMISSION);

        }
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FileMk();
            } else {
                // それでも拒否された時の対応
                Toast toast =
                        Toast.makeText(this, "何もできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        downloadTask.setListener(null);
        super.onDestroy();
    }

    private DownloadTask.Listener createListener() {
        return new DownloadTask.Listener() {
            @Override
            public void onSuccess(String pMosaic) {
                Bitmap mosaicArt;
                mosaicArt=createMosaic.Mosaic(pMosaic);
                ImageView imageView = findViewById(R.id.image_view);
                imageView.setImageBitmap(mosaicArt);
            }
        };
    }

}
