package com.shirael.project1;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class CropActivity extends Activity {

    ImageView compositeImageView;
    boolean crop;
    static Bitmap bitmap2;
    Bitmap resultingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            crop = extras.getBoolean("crop");
        }
        int widthOfscreen = 0;
        int heightOfScreen = 0;

        DisplayMetrics dm = new DisplayMetrics();
        try {
            getWindowManager().getDefaultDisplay().getMetrics(dm);
        } catch (Exception ex) {
        }
        widthOfscreen = dm.widthPixels;
        heightOfScreen = dm.heightPixels;

      compositeImageView = (ImageView) findViewById(R.id.our_imageview);

       // compositeImageView=(ImageView) findViewById(R.id.DrawImageView);

       //  Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),
      //  R.drawable.website);
        Bitmap resultingImage = Bitmap.createBitmap(widthOfscreen,
                heightOfScreen, bitmap2.getConfig());

        Canvas canvas = new Canvas(resultingImage);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

      //  for(int j=0;j<MainActivity.crop_num;j++)
       // {
            //Path path = new Path();
            for (int i = 0; i < MainActivity.paths[1].getPoints().size(); i++) {
                MainActivity.paths[1].getPath().lineTo(MainActivity.paths[1].getPoints().get(i).x, MainActivity.paths[1].getPoints().get(i).y);

            }



            canvas.drawPath(MainActivity.paths[1].getPath(), paint);
            //if (crop) {


            //  } else {
             //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            //}
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            canvas.drawBitmap(bitmap2, 0, 0, paint);


       // }
        //resultingImage= convertBitmap(resultingImage);
        compositeImageView.setImageBitmap(resultingImage);

       /* for (int i = 0; i < SomeView.points.size(); i++) {
            path.lineTo(SomeView.points.get(i).x, SomeView.points.get(i).y);
        }*/
     /*   canvas.drawPath(path, paint);
        if (crop) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        } else {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        }
        canvas.drawBitmap(bitmap2, 0, 0, paint);
     //resultingImage= convertBitmap(resultingImage);

      compositeImageView.setImageBitmap(resultingImage);

        //String s=SaveImage(resultingImage);
       // Log.d("sssssss",s);*/
    }


    public Bitmap convertBitmap(Bitmap bitmap) {
        Bitmap output =  Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint colorFilterMatrixPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colorFilterMatrixPaint.setColorFilter(new ColorMatrixColorFilter(new float[] {
                0, 0, 0, 255, 0,
                0, 0, 0, 255, 0,
                0, 0, 0, 255, 0,
                0, 0, 0, -255, 255
        }));
        canvas.drawBitmap(bitmap, 0, 0, colorFilterMatrixPaint);
        return output;
    }


    public Bitmap toBinary(Bitmap bmpOriginal) {
        int width, height, threshold;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        threshold = 127;
        Bitmap bmpBinary = Bitmap.createBitmap(bmpOriginal);

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get one pixel color
                int pixel = bmpOriginal.getPixel(x, y);
                int red = Color.red(pixel);

                //get binary value
                if(red < threshold){
                    bmpBinary.setPixel(x, y, 0xFF000000);
                } else{
                    bmpBinary.setPixel(x, y, 0xFFFFFFFF);
                }

            }
        }
        return bmpBinary;
    }








    public static String SaveImage(Bitmap finalBitmap)
    {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Shopic Snaps");

        if(!myDir.exists())
            myDir.mkdirs();

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image_"+ n+".jpg";
        File file = new File (myDir, fname);

        if (file.exists ())
            file.delete ();

        try
        {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return root + "/App Snaps/"+fname;
    }




}
