package com.shirael.project1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;

import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

/**
 @author : Sumit Ranjan <sumit.nitt@gmail.com>
 @description : Main Activity
 **/



public class MainActivity extends Activity implements OnTouchListener {
    private Uri mImageCaptureUri;
    private ImageView drawImageView;
    private Bitmap sourceBitmap;
    private Path clipPath;

    private static final int PICK_FROM_FILE = 1;
    private static final int CAMERA_CAPTURE = 2;

  //  Polygon.Builder polygon;
  Region r;

    Polygon p;
    Polygon polygon;
    Bitmap bmp;
    Bitmap alteredBitmap;
    Canvas canvas;
    Paint paint;
    Matrix matrix;
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;
    public static int num_of_path=0;
    public static int crop_num=7;
    public static Paths []paths=new Paths[crop_num];
    boolean flgPathDraw = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        if (!checkPermission()) {
            openActivity();
        } else {
            if (checkPermission()) {
                requestPermissionAndContinue();
            } else {
                openActivity();
            }
        }





        paths[0]=new Paths();
        paths[1]=new Paths();
        paths[2]=new Paths();
        paths[3]=new Paths();
        paths[4]=new Paths();
        paths[5]=new Paths();


        final String [] items			= new String [] {"Take from camera", "Select from gallery"};
        ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder		= new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) { //pick from camera
                if (item == 0) {
                    try {
                        //use standard intent to capture an image
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //we will handle the returned data in onActivityResult
                        startActivityForResult(captureIntent, CAMERA_CAPTURE);
                    } catch(ActivityNotFoundException e){
                        e.printStackTrace();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                } else { //pick from file
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );

        final AlertDialog dialog = builder.create();

        Button button 	= (Button) findViewById(R.id.btn_crop);
        Button saveButton 	= (Button) findViewById(R.id.btn_save);
       Button discardButton 	= (Button) findViewById(R.id.btn_discard);

        drawImageView = (ImageView) findViewById(R.id.DrawImageView);
        drawImageView.setOnTouchListener(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                try {
                    String path = Environment.getExternalStorageDirectory().toString();
                    OutputStream fOut = null;
                    File file = new File(path, String.valueOf(Math.round(Math.random()*100000))+".jpg");
                    Log.d("vvvvvv",file.getName());
                    fOut = new FileOutputStream(file);
                   // cropImageByPath();
                    alteredBitmap= Binary(alteredBitmap);

                    alteredBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    fOut.flush();
                    fOut.close();

                    MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
                    AlertDialog.Builder builder		= new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Saved");
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    //Now deleting the temporary file created on camera capture
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists()) {
                        f.delete();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

       discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if (mImageCaptureUri != null) {
                    //Now deleting the temporary file created on camera capture
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists()) {
                        f.delete();
                    }

                    getContentResolver().delete(mImageCaptureUri, null, null );
                    mImageCaptureUri = null;
                    //resetting the image view
                    ImageView iv = (ImageView) findViewById(R.id.DrawImageView);
                    iv.setImageDrawable(null);*/
              Log.d("discard","discard button");
                Log.d("num of path", String.valueOf(num_of_path));

                if (num_of_path>0)
                    num_of_path=num_of_path-1;
                Log.d("num of path", String.valueOf(num_of_path));

                paths[num_of_path].getPoints().clear();
                Log.d("points size", String.valueOf(paths[num_of_path].getPoints().size()));


                paths[num_of_path].setPath(new Path());
                    // flgPathDraw = true;
               // drawImageView.invalidate();
                onDraw(canvas);

                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);




            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {

            case PICK_FROM_FILE:
                mImageCaptureUri = data.getData();
                doCrop();
                break;

            case CAMERA_CAPTURE:
                mImageCaptureUri = data.getData();
                doCrop();
                break;
        }
    }




    public void fun()
    {
       // polygon = Polygon.Builder();




        for (int i = 0; i < paths[0].getPoints().size(); i++) {
            polygon = Polygon.Builder().
        addVertex(new com.shirael.project1.Point(paths[0].getPoints().get(i).x, MainActivity.paths[0].getPoints().get(i).y)).build();
        }



    }





    public Bitmap Binary(Bitmap bmpOriginal)
    {
        int width, height, threshold;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        threshold = 127;
        Bitmap bmpBinary = Bitmap.createBitmap(bmpOriginal);
//fun();

        for (int x = 0; x < bmpOriginal.getWidth(); x++) {
            for (int y = 0; y < bmpOriginal.getHeight(); y++) {
                com.shirael.project1.Point point=new com.shirael.project1.Point(x,y);
                if(r.contains((int)point.x,(int) point.y)) {
                    Log.d(TAG, "Touch IN");
                    bmpBinary.setPixel(x, y, Color.rgb(255, 255, 255));
                }
                else {
                    Log.d(TAG, "Touch OUT");
                    bmpBinary.setPixel(x, y, Color.rgb(0, 0, 0));
                }

               /* if(polygon.contains(point))
                {
                    bmpBinary.setPixel(x, y, Color.rgb(255, 255, 255));
                }
                else
                    bmpBinary.setPixel(x, y, Color.rgb(0, 0, 0));*/

            }
        }

                return bmpBinary;
    }




   /* public Bitmap toBinary(Bitmap bmpOriginal) {
        int width, height, threshold;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        threshold = 127;
        Bitmap bmpBinary = Bitmap.createBitmap(bmpOriginal);



        for (int x = 0; x < bmpOriginal.getWidth(); x++) {
            for (int y = 0; y < bmpOriginal.getHeight(); y++) {


                for(int i=0;i<MainActivity.paths[0].getPoints().size();i++)
                {
                    if(MainActivity.paths[1].getPoints().get(i).x)
                }


                myBitmap.setPixel(x, y, Color.rgb(255, 255, 255));
            }
        }*/



      /*  for(int x = 0; x < width; ++x) {
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



   /* public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX();
                downy = event.getY();
                clipPath = new Path();
                clipPath.moveTo(downx, downy);
                break;
            case MotionEvent.ACTION_MOVE:
                upx = event.getX();
                upy = event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                clipPath.lineTo(upx, upy);
                drawImageView.invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                clipPath.lineTo(upx, upy);
                drawImageView.invalidate();
                //cropImageByPath();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }*/



    public void onDraw(Canvas canvas) {

      //  if (b != null)
         //   canvas.drawBitmap(b, 0, 0, null);

        canvas.drawBitmap(bmp, matrix, paint);

        // Path path = new Path();
        for (int j = 0; j <= num_of_path; j++) {
            boolean first = true;

            for (int i = 0; i < paths[j].getPoints().size(); i += 2) {
                Point point = paths[j].getPoints().get(i);
                if (first) {
                    first = false;
                    paths[j].getPath().moveTo(point.x, point.y);
                } else if (i < paths[j].getPoints().size() - 1) {
                    Point next = paths[j].getPoints().get(i + 1);
                    paths[j].getPath().quadTo(point.x, point.y, next.x, next.y);
                } else {
                    paths[j].setMlastpoint(paths[j].getPoints().get(i));
                    paths[j].getPath().lineTo(point.x, point.y);
                }
            }
            canvas.drawPath(paths[j].getPath(), paint);
        }
    }





    private static final int PERMISSION_REQUEST_CODE = 200;
    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("permission_necessary");
                alertBuilder.setMessage("storage_permission_is_encessary_to_wrote_event");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            openActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    openActivity();
                } else {
                    finish();
                }

            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openActivity() {
        //add your further process after giving permission or to download images from remote server.
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



    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // if(event.getAction() != MotionEvent.ACTION_DOWN)
        // return super.onTouchEvent(event);

        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        if (flgPathDraw) {

            if (paths[num_of_path].isBfirstpoint()) {

                if (comparepoint( paths[num_of_path].getMfirstpoint(), point)) {
                    /// points.add(point);
                    paths[num_of_path].getPoints().add( paths[num_of_path].getMfirstpoint());
                    flgPathDraw = false;
                    onDraw(canvas);
                  //  drawImageView.invalidate();
                    // / showcropdialog();
                    // / cropflag=true;
                } else {
                    paths[num_of_path].getPoints().add(point);
                    onDraw(canvas);
                  //  drawImageView.invalidate();
                }
            } else {
                paths[num_of_path].getPoints().add(point);
                onDraw(canvas);
                //drawImageView.invalidate();
            }

            if (!( paths[num_of_path].isBfirstpoint())) {

                paths[num_of_path].setMfirstpoint(point);
                paths[num_of_path].setBfirstpoint(true);
                onDraw(canvas);
                //drawImageView.invalidate();

            }
        }

        view.invalidate();
        Log.e("Hi  ==>", "Size: " + point.x + " " + point.y);
        /*if (event.getAction() == MotionEvent.ACTION_UP&&c<2) {
            c++;



        }*/

        if (event.getAction() == MotionEvent.ACTION_UP) {
            // Log.d("Action up*******~~~~~~~>>>>", "called");
            paths[num_of_path].setMlastpoint(point);
            paths[0].getPath().close();
            RectF rectF = new RectF();
            paths[0].getPath().computeBounds(rectF, true);
            r = new Region();
            r.setPath( paths[0].getPath(), new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));



            if (flgPathDraw) {
                if (paths[num_of_path].getPoints().size() > 12) {
                    if (!comparepoint(paths[num_of_path].getMfirstpoint(), paths[num_of_path].getMlastpoint())) {
                        if(num_of_path==crop_num-2)
                        {
                            flgPathDraw = false;
                          // showcropdialog();

                        }
                        //cropImageByPath();
                        paths[num_of_path].getPoints().add(paths[num_of_path].getMfirstpoint());
                        num_of_path++;
                        onDraw(canvas);
                       // drawImageView.invalidate();
                        //cropflag=true;

                    }
                }
            }
        }





        return true;
    }





    private void showcropdialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        // bfirstpoint = false;

                       intent = new Intent(getApplicationContext(),CropActivity.class);
                        intent.putExtra("crop", true);
                        startActivity(intent);
                        break;

                   /* case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked

                        intent = new Intent(getApplicationContext(),CropActivity.class);
                        intent.putExtra("crop", false);
                        startActivity(intent);

                        paths[num_of_path].setBfirstpoint(false);
                        // resetView();

                        break;*/
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you Want to save Crop or Non-crop image?")
                .setPositiveButton("Crop", dialogClickListener).show()
                //.setNegativeButton("Non-crop", dialogClickListener).show()
                .setCancelable(false);
    }





    private boolean comparepoint(Point first, Point current) {
        int left_range_x = (int) (current.x - 3);
        int left_range_y = (int) (current.y - 3);

        int right_range_x = (int) (current.x + 3);
        int right_range_y = (int) (current.y + 3);

        if ((left_range_x < first.x && first.x < right_range_x)
                && (left_range_y < first.y && first.y < right_range_y)) {
            if (paths[num_of_path].getPoints().size() < 10) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }




    private void cropImageByPath() {
        //closing the path now.
        paths[0].getPath().close();
        //setting the fill type to inverse, so that the outer part of the selected path gets filled.
        paths[0].getPath().setFillType(FillType.INVERSE_WINDING);
        Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xferPaint.setColor(Color.BLACK);
        canvas.drawPath(clipPath, xferPaint);
        xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(alteredBitmap, 0, 0, xferPaint);
    }

    private void doCrop() {
        try {
            sourceBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);

            CropActivity.bitmap2=sourceBitmap;

            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = true;
            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(
                    mImageCaptureUri), null, bmpFactoryOptions);
            bmpFactoryOptions.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(
                    mImageCaptureUri), null, bmpFactoryOptions);
            alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp
                    .getHeight(), bmp.getConfig());
            canvas = new Canvas(alteredBitmap);
          //  paint = new Paint();
          //  paint.setColor(Color.GREEN);
           // paint.setStrokeWidth(5);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            paint.setColor(Color.GREEN);
            paint.setPathEffect(new DashPathEffect(new float[] { 10, 20 }, 0));
            matrix = new Matrix();
          canvas.drawBitmap(bmp, matrix, paint);
            //loading the image bitmap in image view
            drawImageView.setImageBitmap(alteredBitmap);
            //setting the touch listener
            drawImageView.setOnTouchListener(this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
