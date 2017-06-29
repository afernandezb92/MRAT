package com.poc.team;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class Images {
    private ArrayList<byte[]> arrayImages;

    public Images() {
        arrayImages = new ArrayList<byte[]>();
    }

    private void put(Bitmap image){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapBytes = stream.toByteArray();
        arrayImages.add(bitmapBytes);
    }

    public void readImages(String[] imagesPath){
        arrayImages = new ArrayList<byte[]>(imagesPath.length);
        for (int i = 0; i < imagesPath.length; i++){
            try{
                File file = new File(imagesPath[i]);
                BitmapFactory.Options bm = new BitmapFactory.Options();
                Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath(), bm);
                System.out.println(b);
                put(b);
            } catch (Exception e){
                System.out.println("Error " + e);
            }
        }
    }

    public ArrayList<byte[]> getArrayImages() {
        return arrayImages;
    }
}
