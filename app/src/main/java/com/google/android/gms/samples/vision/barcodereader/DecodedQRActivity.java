package com.google.android.gms.samples.vision.barcodereader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecodedQRActivity extends Activity {
    private static final int RC_BARCODE_CAPTURE = 9001;
    public static final String BarcodeObject = "Barcode";
    private static final String TAG = "DecoderQRActivity";

    TextView questionText;
    TextView infoMessage;
    RadioButton answerA, answerB, answerC, answerD;
    RadioGroup answersGr;
    ProgressBar progressBar, progressBarHoriz;
    Button submitButton;
    ObjectAnimator animator = new ObjectAnimator();
    List<String> radioButtonsList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, mDatabaseUpdate;

    String questionId, correctAnswer, score, selectedAnswer, selectedAnswerText;
    String correctAnswerText;
    boolean isCorrect;
    boolean qAnswered = false;
    boolean submitted = false;
    boolean paused = false;
    boolean history = false;
    Question q;
    Map<String, String> answersAndText = new HashMap<>();

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decoded_qr);

        questionText = (TextView) findViewById(R.id.questionText);
        infoMessage = (TextView) findViewById(R.id.infoMessage);
        answerA = (RadioButton) findViewById(R.id.answerA);
        answerB = (RadioButton) findViewById(R.id.answerB);
        answerC = (RadioButton) findViewById(R.id.answerC);
        answerD = (RadioButton) findViewById(R.id.answerD);
        answersGr = (RadioGroup) findViewById(R.id.answersGroup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarHoriz = (ProgressBar) findViewById(R.id.progressBarHoriz);
        submitButton = (Button) findViewById(R.id.submit);


        mAuth = FirebaseAuth.getInstance();

        // the id of the current question
        questionId = getIntent().getStringExtra(BarcodeObject);

        getData();
        checkAnsweredQuestions();
    }

    @Override
    protected void onPause() {          // it s also called when you press back or start button
        super.onPause();

        paused = true;

        if (!submitted) {                             // submit not pressed
            if (history || qAnswered) {              // history pressed or just see the activity
                return;
            }
            disableAndWriteDB();        // time expired or exited application
        }
    }

    public void checkAnsweredQuestions() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                for (HashMap.Entry i : map.entrySet()) {
                    if (i.getKey().equals("answers")) {
                        Map<String, Object> answersHM = (Map<String, Object>) i.getValue();
                        for (HashMap.Entry ii : answersHM.entrySet()) {
                            String key = (String) ii.getKey();
                            if (key.equals(questionId)) {
                                qAnswered = true;       // the question was already answered, the student cannot submit a new answer

                                Map<String, Map<String, Object>> oneAnswer = (Map<String, Map<String, Object>>) ii.getValue();
                                for (HashMap.Entry oneField : oneAnswer.entrySet()) {
                                    if (oneField.getKey().equals("answer")) {
                                        selectedAnswer = (String) oneField.getValue();          // from db
                                        for (Map.Entry entry : answersAndText.entrySet()) {
                                            if (selectedAnswer.equals(entry.getKey())) {
                                                selectedAnswerText = entry.getValue().toString();       // text of answer, have to find id
                                                break;
                                            }
                                        }
                                        setSelectedAnswer();
                                    }
                                }
                            }
                        }
                    }
                }
                activateHistoryButton();

                startAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getData() {
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

                        // create the list of answers to shuffle
                        radioButtonsList.add(q.getAnswerA());
                        radioButtonsList.add(q.getAnswerB());
                        radioButtonsList.add(q.getAnswerC());
                        radioButtonsList.add(q.getAnswerD());
                        Collections.shuffle(radioButtonsList);

                        // set answers shuffled
                        answerA.setText(radioButtonsList.get(0));
                        answerB.setText(radioButtonsList.get(1));
                        answerC.setText(radioButtonsList.get(2));
                        answerD.setText(radioButtonsList.get(3));

                        correctAnswer = q.getCorrectAnswer();
                        correctAnswer = format(correctAnswer);

                        // answers with correct id from db
                        answersAndText.put("answerA", q.getAnswerA());
                        answersAndText.put("answerB", q.getAnswerB());
                        answersAndText.put("answerC", q.getAnswerC());
                        answersAndText.put("answerD", q.getAnswerD());
                    }
                }
                // stop loading
                progressBar.setVisibility(View.INVISIBLE);
                answerA.setVisibility(View.VISIBLE);
                answerB.setVisibility(View.VISIBLE);
                answerC.setVisibility(View.VISIBLE);
                answerD.setVisibility(View.VISIBLE);
                progressBarHoriz.setVisibility(View.VISIBLE);

                activateSubmitButton();
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
        String answerText = null;

        isCorrect = false;

        // radio button chosen by student
        switch (checkedAnswerId) {
            case 0:
                answerText = radioButtonsList.get(0);
                break;
            case 1:
                answerText = radioButtonsList.get(1);
                break;
            case 2:
                answerText = radioButtonsList.get(2);
                break;
            case 3:
                answerText = radioButtonsList.get(3);
                break;
        }

        for (Map.Entry entry : answersAndText.entrySet()) {
            if (entry.getValue().equals(answerText)) {
                answerChecked = entry.getKey().toString();
            }
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

    }

    public String format(String s) {
        String sFormatted = null;
        switch (s) {
            case "Answer A":
                sFormatted = "answerA";
                correctAnswerText = q.getAnswerA();
                break;
            case "Answer B":
                sFormatted = "answerB";
                correctAnswerText = q.getAnswerB();
                break;
            case "Answer C":
                sFormatted = "answerC";
                correctAnswerText = q.getAnswerC();
                break;
            case "Answer D":
                sFormatted = "answerD";
                correctAnswerText = q.getAnswerD();
                break;
        }

        return sFormatted;
    }

    public void setSelectedAnswer() {
        if (!selectedAnswer.equals("-1")) {
            searchIdButton();
        }
        switch (selectedAnswer) {
            case "-1":
                answerA.setEnabled(false);
                answerB.setEnabled(false);
                answerC.setEnabled(false);
                answerD.setEnabled(false);
                break;
            case "answerA":

                answersGr.check(R.id.answerA);
                answerB.setEnabled(false);
                answerC.setEnabled(false);
                answerD.setEnabled(false);
                break;
            case "answerB":
                answersGr.check(R.id.answerB);
                answerA.setEnabled(false);
                answerC.setEnabled(false);
                answerD.setEnabled(false);
                break;
            case "answerC":
                answersGr.check(R.id.answerC);
                answerA.setEnabled(false);
                answerB.setEnabled(false);
                answerD.setEnabled(false);
                break;
            case "answerD":
                answersGr.check(R.id.answerD);
                answerA.setEnabled(false);
                answerB.setEnabled(false);
                answerC.setEnabled(false);
                break;
        }
    }

    public void searchIdButton() {
        for (String s : radioButtonsList) {
            if (selectedAnswerText.equals(s)) {
                int id = radioButtonsList.indexOf(s);
                switch(id) {
                    case 0:
                        selectedAnswer = "answerA";
                        break;
                    case 1:
                        selectedAnswer = "answerB";
                        break;
                    case 2:
                        selectedAnswer = "answerC";
                        break;
                    case 3:
                        selectedAnswer = "answerD";
                        break;
                }
                break;
            }
        }
    }

    public void activateSubmitButton() {
        if (!qAnswered) {
            submitButton.setVisibility(View.VISIBLE);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // if the student didn't choose an option
                    if (answersGr.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(DecodedQRActivity.this, R.string.haveToSelectOption, Toast.LENGTH_SHORT).show();
                    } else {
                        sendData();
//                        updateScore();
                        animator.removeAllListeners();
                        animator.cancel();

                        submitted = true;

                        Intent intent = new Intent(DecodedQRActivity.this, StudentAccountActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    public void activateHistoryButton() {
        if (qAnswered) {
            submitButton.setText("History");
            submitButton.setVisibility(View.VISIBLE);
            infoMessage.setVisibility(View.VISIBLE);
            progressBarHoriz.setVisibility(View.INVISIBLE);
            progressBarHoriz.setEnabled(false);

            animator.removeAllListeners();
            animator.cancel();

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    history = true;
                    Intent intent = new Intent(DecodedQRActivity.this, HistoryActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void startAnimation() {
        if (qAnswered) {
            return;
        }

        animator = ObjectAnimator.ofInt (progressBar, "progress", 0, 100);
        animator.setInterpolator(new LinearInterpolator());
        animator.setStartDelay(0);
        animator.setDuration(5000);         // default = 60000

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                progressBarHoriz.setProgress(100 - value);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (!submitted) {
                    if (paused) {
                        return;
                    }
                    disableAndWriteDB();
                    Toast.makeText(DecodedQRActivity.this, R.string.timeExpired, Toast.LENGTH_SHORT).show();
                }

            }
        });

        animator.start();
    }

    public void disableAndWriteDB() {
        submitButton.setEnabled(false);
        answerA.setEnabled(false);
        answerB.setEnabled(false);
        answerC.setEnabled(false);
        answerD.setEnabled(false);

        progressBarHoriz.setVisibility(View.INVISIBLE);
        progressBarHoriz.setEnabled(false);

        String answer = "-1";
        boolean isCorrect = false;
        AnsweredQuestion aQ = new AnsweredQuestion(answer, isCorrect);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(mAuth.getCurrentUser().getUid()).child("answers").child(questionId).setValue(aQ);
    }

    // all the back buttons must be overriden else wont work properly
//    @Override
//    public void onBackPressed() {
//        Intent setIntent = new Intent(DecodedQRActivity.this, SecondActivity.class);
//        startActivity(setIntent);
//    }
}
