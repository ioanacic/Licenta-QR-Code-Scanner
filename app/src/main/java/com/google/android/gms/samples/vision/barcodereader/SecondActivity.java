package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends Activity implements View.OnClickListener {

    private TextView statusMessage;
    private TextView barcodeValue;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseQuestions, mDatabaseUsers, mDatabase;

    public List<AnsweredQuestion> answers = new ArrayList<>();
    public List<Question> allQuestions = new ArrayList<>();
    public String score;
    public double goodScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        statusMessage = (TextView) findViewById(R.id.status_message);
        barcodeValue = (TextView) findViewById(R.id.barcode_value);

        findViewById(R.id.read_barcode).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.addQuestionButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        getFirebaseData();
//        getAnswers();
//        getQuestions();
//        getScore();

    }

//    // the questions the user answered to
//    public void getAnswers() {
//        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
////        mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).child("answers").addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                for (DataSnapshot d : dataSnapshot.getChildren()) {
////                    AnsweredQuestion aQ = d.getValue(AnsweredQuestion.class);
////                    String qId = d.getKey();
////                    aQ.setqId(qId);
////                    answers.add(aQ);
////                }
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////
////            }
////        });
//
//        mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot d : dataSnapshot.getChildren()) {
//                    User u = d.getValue(User.class);
//                    score = u.getScore();
//
////                    for(DataSnapshot dd : d.child("answers").getChildren()) {
////                        AnsweredQuestion aQ = dd.getValue(AnsweredQuestion.class);
////                        String qId = dd.getKey();
////                        aQ.setqId(qId);
////                        answers.add(aQ);
////                    }
//                }
//                double testScore = calculateScore();
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    // all the questions
//    public void getQuestions() {
//        mDatabaseQuestions = FirebaseDatabase.getInstance().getReference("questions");
//        mDatabaseQuestions.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot d : dataSnapshot.getChildren()) {
//                    Question q = d.getValue(Question.class);
//                    String key = d.getKey();
//                    q.setKey(key);
//                    allQuestions.add(q);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    public void getScore() {
//        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
//        mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot d : dataSnapshot.getChildren()) {
////                    User u = d.getValue(User.class);
////                    score = u.getScore();
//                }
//                double testScore = calculateScore();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void getFirebaseData() {
        mDatabase = mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                score = user.score;
//                    answers = user.getAnswers();

                goodScore = calculateScore();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public double calculateScore() {
        double scoreD;
        if (score == null) {
            scoreD = 0.0;
        } else {
            scoreD = Double.parseDouble(score);
        }
        for (AnsweredQuestion aQ : answers) {
            if (aQ.isCorrect) {
                scoreD = scoreD + 0.1;
            }
        }
        return scoreD;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.signOutButton) {
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.addQuestionButton) {
            Intent intent = new Intent(this, AddQuestion.class);
            startActivity(intent);
        }
    }
}

