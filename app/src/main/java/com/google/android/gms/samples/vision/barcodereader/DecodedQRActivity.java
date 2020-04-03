package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DecodedQRActivity extends Activity {
    private static final int RC_BARCODE_CAPTURE = 9001;
    public static final String BarcodeObject = "Barcode";

    TextView decodedMessage;
    TextView questionText;
    RadioButton answerAText, answerBText, answerCText, answerDText;
    RadioGroup answersGr;
    ProgressBar progressBar, progressBarHoriz;
    Button submitButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, mDatabaseUpdate;

    String questionId, correctAnswer, score;
    boolean isCorrect;
    Question q;

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
        answersGr = (RadioGroup) findViewById(R.id.answersGroup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarHoriz = (ProgressBar) findViewById(R.id.progressBarHoriz);
        submitButton = (Button) findViewById(R.id.submit);

        mAuth = FirebaseAuth.getInstance();

        questionId = getIntent().getStringExtra(BarcodeObject);
        getData();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if the student didn't choose an option
                if (answersGr.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(DecodedQRActivity.this, R.string.haveToSelectOption, Toast.LENGTH_SHORT).show();
                } else {
                    sendData();
                    updateScore();
                    Intent intent = new Intent(DecodedQRActivity.this, SecondActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void getData() {
        decodedMessage.setText(questionId);

        // loading
        progressBar.setVisibility(View.VISIBLE);
        mDatabase = FirebaseDatabase.getInstance().getReference("questions");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.getKey().equals(questionId)) {
                        q = d.getValue(Question.class);
                        questionText.setText(q.question);
                        answerAText.setText(q.answerA);
                        answerBText.setText(q.answerB);
                        answerCText.setText(q.answerC);
                        answerDText.setText(q.answerD);
                        correctAnswer = q.getCorrectAnswer();
                        correctAnswer = format(correctAnswer);
                    }
                }
                // stop loading
                progressBar.setVisibility(View.INVISIBLE);
                answerAText.setVisibility(View.VISIBLE);
                answerBText.setVisibility(View.VISIBLE);
                answerCText.setVisibility(View.VISIBLE);
                answerDText.setVisibility(View.VISIBLE);
                progressBarHoriz.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendData() {
        int id = answersGr.getCheckedRadioButtonId();
        View v = answersGr.findViewById(id);
        int checkedAnswerId = answersGr.indexOfChild(v);
        String answerChecked = null;

        isCorrect = false;

        switch (checkedAnswerId) {
            case 0:
                answerChecked = "answerA";
                break;
            case 1:
                answerChecked = "answerB";
                break;
            case 2:
                answerChecked = "answerC";
                break;
            case 3:
                answerChecked = "answerD";
                break;
        }

        // check if the answer is correct or not
        if (answerChecked.equals(correctAnswer)) {
            isCorrect = true;
        }

        AnsweredQuestion aQ = new AnsweredQuestion(answerChecked, isCorrect);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(mAuth.getCurrentUser().getUid()).child("answers").child(questionId).setValue(aQ);
    }

    public void updateScore() {
        mDatabaseUpdate = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseUpdate.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                for (HashMap.Entry i : map.entrySet()) {
                    if (i.getKey().equals("score")) {
                        score = (String) i.getValue();
                    }
                }
                if (isCorrect == true) {
                    double scoreD;
                    scoreD = Double.parseDouble(score);
                    scoreD = scoreD + 0.1;
                    score = (Double.toString(scoreD)).substring(0, 3);
                }

                mDatabaseUpdate.child(mAuth.getCurrentUser().getUid()).child("score").setValue(score);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        Map<String, Object> updateScore = new HashMap<>();
//        updateScore.put("score", score);
//        mDatabase.child(mAuth.getCurrentUser().getUid()).updateChildren(updateScore);

    }

    public String format(String s) {
        String sFormatted = null;
        switch (s) {
            case "Answer A":
                sFormatted = "answerA";
                break;
            case "Answer B":
                sFormatted = "answerB";
                break;
            case "Answer C":
                sFormatted = "answerC";
                break;
            case "Answer D":
                sFormatted = "answerD";
                break;
        }

        return sFormatted;
    }

}
