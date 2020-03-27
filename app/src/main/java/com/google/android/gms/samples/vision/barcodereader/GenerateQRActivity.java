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

    ImageView qrImage;
    Button save;
    EditText renamePicture;
    String inputValue;

    String keyOfSelectedQuestion;

    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr);

        qrImage = (ImageView) findViewById(R.id.QR_Image);
        save = (Button) findViewById(R.id.save);
        renamePicture = (EditText) findViewById(R.id.pictureName);

        keyOfSelectedQuestion = getIntent().getStringExtra("KEY");

        inputValue = keyOfSelectedQuestion.trim();
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
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean save;
                String result;

                File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                File dir = new File(filepath.getAbsoluteFile() + "/QRCodes/");
                dir.mkdir();

                String pictureName;
                if (renamePicture.getText().toString().isEmpty()) {
                    pictureName = keyOfSelectedQuestion;
                } else {
                    pictureName = renamePicture.getText().toString();
                }
                File file = new File(dir, pictureName + ".jpg");

                try {
                    outputStream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                try {
                    save = QRGSaver.save(filepath.toString(), keyOfSelectedQuestion.trim(), bitmap, QRGContents.ImageType.IMAGE_JPEG);
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
