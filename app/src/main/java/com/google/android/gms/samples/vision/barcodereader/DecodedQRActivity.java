package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class DecodedQRActivity extends Activity {
    private TextView decodedMessage;

    private static final int RC_BARCODE_CAPTURE = 9001;
    public static final String BarcodeObject = "Barcode";

    public TextView questionText;
    public RadioButton answerAText, answerBText, answerCText, answerDText;
    public ProgressBar progressBar, progressBarHoriz;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decoded_qr);

        decodedMessage = (TextView) findViewById(R.id.decoded_qr_text);
        questionText = (TextView) findViewById(R.id.questionText);
        answerAText = (RadioButton) findViewById(R.id.answerAText);
        answerBText = (RadioButton) findViewById(R.id.answerBText);
        answerCText = (RadioButton) findViewById(R.id.answerCText);
        answerDText = (RadioButton) findViewById(R.id.answerDText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarHoriz = (ProgressBar) findViewById(R.id.progressBarHoriz);

        getData();
    }

    public void getData() {
        final String barcodeValue = getIntent().getStringExtra(BarcodeObject);
        decodedMessage.setText(barcodeValue);

        // loading
        progressBar.setVisibility(View.VISIBLE);
        mDatabase = FirebaseDatabase.getInstance().getReference("questions");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.getKey().equals(barcodeValue)) {
                        Question q = d.getValue(Question.class);
                        questionText.setText(q.question);
                        answerAText.setText(q.answerA);
                        answerBText.setText(q.answerB);
                        answerCText.setText(q.answerC);
                        answerDText.setText(q.answerD);
                    }
                }
                // stop loading
                progressBar.setVisibility(View.INVISIBLE);
                answerAText.setVisibility(View.VISIBLE);
                answerBText.setVisibility(View.VISIBLE);
                answerCText.setVisibility(View.VISIBLE);
                answerDText.setVisibility(View.VISIBLE);
                progressBarHoriz.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
