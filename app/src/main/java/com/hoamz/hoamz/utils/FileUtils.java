package com.hoamz.hoamz.utils;

import static java.lang.Math.max;
import static java.lang.Math.ulp;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.hoamz.hoamz.data.model.Photo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    public static void writeToFile(Context context, String data,String title){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,title);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"text/plain");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/YourNote");
        Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"),contentValues);
        if(uri != null){
            try(OutputStream outputStream = context.getContentResolver().openOutputStream(uri)){
                if(outputStream != null){
                    outputStream.write(data.getBytes());
                    outputStream.close();
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
            }catch (IOException e){
                Toast.makeText(context, "Wrong", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(context, "Wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public static void saveTextToImage(Context context, String text, String title,Bitmap bitmapBackground,boolean isLight){
        text = "\n" +title + "\n\n" + text + "\n\n";
        Bitmap bitmap = ConvertTextToBitmap(text,bitmapBackground,isLight);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,title);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/png");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES);
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        if(uri != null){
            try(OutputStream outputStream = context.getContentResolver().openOutputStream(uri)){
                if(outputStream != null){
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                    outputStream.close();
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
            }catch (IOException e){
                Toast.makeText(context, "Wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static Bitmap ConvertTextToBitmap(String text,Bitmap bitmapBackground,boolean isLight){
        Paint paint = new Paint();
        if(isLight){
            paint.setColor(Color.BLACK);
        }
        else{
            paint.setColor(Color.WHITE);
        }
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.LEFT);//can trai
        String [] lines= text.split("\n");
        int maxWidth = 0;//lay chieu rong dai nhat
        for(String line : lines){
            maxWidth = (int) max(maxWidth,paint.measureText(line));
        }

        //khoi tao kich co anh
        int w = maxWidth + 120;//padding start60 end 60
        int h = lines.length * 70 + 200;//padding top 100 bottom 100
        h = Math.max(h,500);
        Bitmap bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Bitmap bitmap_ = Bitmap.createScaledBitmap(bitmapBackground,w,h,true);
        canvas.drawBitmap(bitmap_,0,0,null);
        float x = 60,y = 60;
        for(String line : lines){
            canvas.drawText(line,x,y,paint);
            y += 70;//moi dong cach nhau 70px
        }
        return bitmap;
    }

    //save image to gallery

    public static void saveImage(Context context,Photo photo){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,System.currentTimeMillis() + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES + "/EasyNotes");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        boolean ok = false;
        if(uri != null){
            try(OutputStream outputStream = context.getContentResolver().openOutputStream(uri)){
                if(outputStream != null) {
                    try (InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(photo.getUri()))) {
                        if (inputStream != null) {
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            if (bitmap != null) {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                ok = true;
                                Toast.makeText(context, "Save image successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }catch (IOException e){
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        }

        if(!ok){
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
