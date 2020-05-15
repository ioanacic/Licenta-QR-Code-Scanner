package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecodeQRActivity extends Activity {
    public static final String BarcodeObject = "Barcode";

    ImageView backgroundImage;

    String decodedQR, typeOfQuiz, ended;
    boolean exists = false;
    Test test;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decode_qr);

        backgroundImage = findViewById(R.id.aboutToStartQuiz);

        decodedQR = getIntent().getStringExtra(BarcodeObject);
        typeOfQuiz = getIntent().getStringExtra("TYPE OF QUIZ");

        ended = getIntent().getStringExtra("QUIZ ENDED");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        if (decodedQR != null && typeOfQuiz != null) {
            if (typeOfQuiz.equals("question")) {
                verifyQuestion();
            } else if (typeOfQuiz.equals("test")) {
                getTest();
            }
        }

        if (ended != null) {
            backgroundImage.setImageResource(R.drawable.ended);
            startTimerGoBack();
        }
    }

    public void verifyQuestion() {
        mDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                for (HashMap.Entry i : map.entrySet()) {
                    if (i.getKey().equals("answers")) {
                        Map<String, Object> answersHM = (Map<String, Object>) i.getValue();
                        for (HashMap.Entry ii : answersHM.entrySet()) {
                            String key = (String) ii.getKey();
                            if (key.equals(decodedQR)) {
                                exists = true;       // the question was already answered, the student cannot submit a new answer
                            }
                        }
                    }
                }
                if (exists) {
                    backgroundImage.setImageResource(R.drawable.taken);
                    startTimerGoBack();
                } else {
                    backgroundImage.setImageResource(R.drawable.start);
                    startTimerGoQuiz();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getTest() {
        FirebaseDatabase.getInstance().getReference("tests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();

                for (Map.Entry i : map.entrySet()) {
                    if (i.getKey().equals(decodedQR)) {

                        Map<String, Object> oneTest = (HashMap<String, Object>) i.getValue();

                        int numberOfQuestions = 0;
                        List<String> qs = new ArrayList<>();

                        for (HashMap.Entry ii : oneTest.entrySet()) {
                            if (ii.getKey().equals("numberOfQuestions")) {
                                numberOfQuestions = Integer.valueOf(String.valueOf(ii.getValue()));
                            } else if (ii.getKey().equals("title")) {
                                break;
                            } else if (ii.getKey().equals("professorKey")) {
                                break;
                            } else {
                                qs.add((String) ii.getKey());
                            }
                        }

                        test = new Test(numberOfQuestions);
                        test.setQuestionsId(qs);
                    }
                }
                verifyTest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void verifyTest() {
        mDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                int howManyAnswered = 0;

                for (HashMap.Entry i : map.entrySet()) {
                    if (i.getKey().equals("answers")) {
                        Map<String, Object> answersHM = (Map<String, Object>) i.getValue();
                        for (HashMap.Entry ii : answersHM.entrySet()) {
                            for (String key : test.getQuestionsId()) {
                                if (ii.getKey().equals(key)) {
                                    howManyAnswered++;
                                }
                            }
                        }
                    }
                }

                if (howManyAnswered == test.getNumberOfQuestions()) {
                    exists = true;
                }

                if (exists) {
                    backgroundImage.setImageResource(R.drawable.taken);
                    startTimerGoBack();
                } else {
                    backgroundImage.setImageResource(R.drawable.start);
                    startTimerGoQuiz();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void startTimerGoQuiz() {
        new CountDownTimer(1000, 100) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Intent intent = new Intent(DecodeQRActivity.this, QuizActivity.class);
                intent.putExtra(BarcodeObject, decodedQR);
                startActivity(intent);
            }
        }.start();
    }

    public void startTimerGoBack() {
        new CountDownTimer(2500, 100) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Intent intent = new Intent(DecodeQRActivity.this, StudentAccountActivity.class);
                intent.putExtra(BarcodeObject, decodedQR);
                startActivity(intent);
            }
        }.start();
    }
}
