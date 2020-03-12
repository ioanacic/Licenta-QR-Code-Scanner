package com.google.android.gms.samples.vision.barcodereader;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class GenerateQRActivity extends Activity {
    String TAG = "GenerateQRCode";
    EditText edtValue;
    ImageView qrImage;
    Button start, save;
    String inputValue;
    //    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    String savePath = Environment.DIRECTORY_DCIM;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr);

        qrImage = (ImageView) findViewById(R.id.QR_Image);
        edtValue = (EditText) findViewById(R.id.edt_value);
        start = (Button) findViewById(R.id.start);
        save = (Button) findViewById(R.id.save);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputValue = edtValue.getText().toString().trim();
                if (inputValue.length() > 0) {
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width : height;
                    smallerDimension = smallerDimension * 3 / 4;

                    qrgEncoder = new QRGEncoder(
                            inputValue, null,
                            QRGContents.Type.TEXT,
                            smallerDimension);
                    try {
                        bitmap = qrgEncoder.encodeAsBitmap();
                        qrImage.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        Log.v(TAG, e.toString());
                    }
                } else {
                    edtValue.setError("Required");
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean save;
//                String result;
//                try {
//                    save = QRGSaver.save(savePath, edtValue.getText().toString().trim(), bitmap, QRGContents.ImageType.IMAGE_JPEG);
//                    result = save ? "Image Saved" : "Image Not Saved";
//                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                boolean save;
                String result;
////
//////                BitmapDrawable drawable = (BitmapDrawable) qrImage.getDrawable();
//////                Bitmap bitmap2 = drawable.getBitmap();
////
////
////                // Here, thisActivity is the current activity
////                if (ContextCompat.checkSelfPermission(GenerateQRActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
////                        != PackageManager.PERMISSION_GRANTED) {
////
////                    // Permission is not granted
////                    // Should we show an explanation?
////                    if (ActivityCompat.shouldShowRequestPermissionRationale(GenerateQRActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
////                        // Show an explanation to the user *asynchronously* -- don't block
////                        // this thread waiting for the user's response! After the user
////                        // sees the explanation, try again to request the permission.
////                    } else {
////                        // No explanation needed; request the permission
////                        ActivityCompat.requestPermissions(GenerateQRActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
////
////                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
////                        // app-defined int constant. The callback method gets the
////                        // result of the request.
////                    }
////                } else {
////                    // Permission has already been granted
////                }
////

//                File filepath = Environment.getExternalStorageDirectory();
                File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                File dir = new File(filepath.getAbsoluteFile()+"/QRCodes/");
                dir.mkdir();
                File file = new File(dir, System.currentTimeMillis()+".jpg");
                try {
                    outputStream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                try {
                    save = QRGSaver.save(filepath.toString(), edtValue.getText().toString().trim(), bitmap, QRGContents.ImageType.IMAGE_JPEG);
                    result = save ? "Image Saved" : "Image Not Saved";
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
