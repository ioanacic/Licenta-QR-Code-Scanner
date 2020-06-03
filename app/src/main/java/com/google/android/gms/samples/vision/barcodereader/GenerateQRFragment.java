package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

import static android.content.Context.WINDOW_SERVICE;

public class GenerateQRFragment extends Fragment implements View.OnClickListener {
    String TAG = "GenerateQRCode";

    ImageView qrImage;
    Button save;
    TextInputEditText renamePicture;
    TextInputLayout renameLayout;

    String inputValue;
    boolean show = false;

    String keyOfSelectedQuestion, subjectOfSelectedQuestion, courseOfSelectedQuestion, type, statement;

    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    OutputStream outputStream;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.generate_qr, container, false);

        qrImage = (ImageView) rootView.findViewById(R.id.QR_Image);
        save = (Button) rootView.findViewById(R.id.save);
        renamePicture = rootView.findViewById(R.id.pictureName);

        rootView.findViewById(R.id.infoRenameButton).setOnClickListener(this);
        renameLayout = rootView.findViewById(R.id.renameLayout);
        renameLayout.setErrorTextAppearance(R.style.error_appearance);;

        inputValue = keyOfSelectedQuestion.trim();
        if (inputValue.length() > 0) {
            WindowManager manager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
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
                File dir;
                String addToPath1 = "/QRCodes/" + subjectOfSelectedQuestion + "/";
                dir = new File(filepath.getAbsoluteFile() + addToPath1);
                dir.mkdir();

                String addToPath2 = addToPath1 + courseOfSelectedQuestion + "/";
                dir = new File(filepath.getAbsoluteFile() + addToPath2);
                dir.mkdir();


                String pictureName;
                if (renamePicture.getText().toString().isEmpty()) {
                    pictureName = statement;
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
                    Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                    FirebaseDatabase.getInstance().getReference("questions").child(keyOfSelectedQuestion).child("isQrGenerated").setValue(true);

                    getActivity().getSupportFragmentManager().popBackStack();
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

        return rootView;
    }

    public void setup(String keyOfSelectedQuestion, String subjectOfSelectedQuestion, String courseOfSelectedQuestion, String type, String statement) {
        this.keyOfSelectedQuestion = keyOfSelectedQuestion;
        this.subjectOfSelectedQuestion = subjectOfSelectedQuestion;
        this.courseOfSelectedQuestion = courseOfSelectedQuestion;
        this.type = type;
        this.statement = statement;
    }

    public void setup(String keyOfSelectedQuestion, String type) {
        this.keyOfSelectedQuestion = keyOfSelectedQuestion;
        this.type = type;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.infoRenameButton) {
            if (!show) {
                renameLayout.setError("You will identify the code easier if you rename it now");
                renameLayout.setErrorEnabled(true);
                show = true;
            } else {
                renameLayout.setErrorEnabled(false);
                show = false;
            }
        }
    }
}
