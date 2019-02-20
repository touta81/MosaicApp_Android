package com.example.tadatouta.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.IDNA;
import android.os.Environment;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Feature {
    public String featuresJson(ArrayList<Bitmap> bmps){
        int size=50;
        int div=1;
        int pixels[]=new int[size*size];
        float hsv[]=new float[3];
        float hs[][]=new float[div][div];
        float ss[][]=new float[div][div];
        float vs[][]=new float[div][div];
        int num=0;


        List<Features> list=new ArrayList<>();
        for (Bitmap bmp:bmps){
            bmp.getPixels(pixels,0,size,0,0,size,size);
            for (int x=0;x<size;x++){
                for (int y=0;y<size;y++){
                    int pixel=pixels[x+y*size];
                    int r=Color.red(pixel);
                    int g=Color.green(pixel);
                    int b=Color.blue(pixel);
                    Color.RGBToHSV(r,g,b,hsv);
                    for (int i=0;i<div;i++){
                        for (int j=0;j<div;j++){
                            if((x>=size/i && x<size/(i+1)) && (y>=size/j && y<size/(j+1))){
                                hs[i][j]+=hsv[0];
                                ss[i][j]+=hsv[1];
                                vs[i][j]+=hsv[2];
                            }
                        }
                    }
                }
            }
            for (int i=0;i<div;i++) {
                for (int j = 0; j < div; j++) {
                    hs[i][j] /= (size / div) * (size / div);
                    ss[i][j] /= (size / div) * (size / div);
                    vs[i][j] /= (size / div) * (size / div);
                }
            }

            ArrayList<ArrayList<float[]>> row=new ArrayList<>();
            for (int i=0;i<div;i++) {
                ArrayList<float[]> col=new ArrayList<>();
                for (int j = 0; j < div; j++) {
                    float array[]=new float[3];
                    array[0]=hs[i][j];
                    array[1]=ss[i][j];
                    array[2]=vs[i][j];
                    col.add(array);
                }
                row.add(col);
            }
            Features features=new Features();
            features.setNum(num);
            features.setRow(row);
            list.add(features);
            num++;
        }
        Gson gson =new Gson();
        JSON json1=new JSON();
        json1.setBlock_size(size);
        json1.setFeautures(list);
        String json=gson.toJson(json1);
        return json;
    }
}
class JSON{
    public int block_size;
    public List<Features> feautures;

    public String toString(){
        return "JSON{"+
                "block_size='"+block_size+'\''+
                ",features="+feautures+
                '}';
    }

    public void setBlock_size(int block_size) {
        this.block_size = block_size;
    }

    public void setFeautures(List<Features> feautures) {
        this.feautures = feautures;
    }
}
class Features{
    public int num;
    public ArrayList<ArrayList<float[]>> row=new ArrayList();

    public  String toString(){
        return "Features{"+
                num+"='"+row+'}';
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setRow(ArrayList<ArrayList<float[]>> row) {
        this.row = row;
    }
}
