package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class SeeAQActivity extends Activity {
    TextView questionText;
    RadioButton answerA, answerB, answerC, answerD;
    RadioGroup answersGr;

    String keyOfSelectedQuestion;

    private DatabaseReference mDatabase, mDatabaseUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_aq_activity);

        questionText = (TextView) findViewById(R.id.questionText);
        answerA = (RadioButton) findViewById(R.id.answerA);
        answerB = (RadioButton) findViewById(R.id.answerB);
        answerC = (RadioButton) findViewById(R.id.answerC);
        answerD = (RadioButton) findViewById(R.id.answerD);
        answersGr = (RadioGroup) findViewById(R.id.answersGroup);

        // the id of the current question
        keyOfSelectedQuestion = getIntent().getStringExtra("KEY");
    }
}
