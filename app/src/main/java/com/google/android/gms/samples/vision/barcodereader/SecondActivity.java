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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecondActivity extends Activity implements View.OnClickListener {
    TextView statusMessage;
    TextView barcodeValue;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    List<AnsweredQuestion> answers = new ArrayList<>();
    List<Question> allQuestions = new ArrayList<>();
    String score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        statusMessage = (TextView) findViewById(R.id.status_message);
        barcodeValue = (TextView) findViewById(R.id.barcode_value);

        findViewById(R.id.read_barcode).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.addQuestionButton).setOnClickListener(this);
        findViewById(R.id.historyButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        getFirebaseData();
    }

    public void getFirebaseData() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                for (HashMap.Entry i : map.entrySet()) {
                    if (i.getKey().equals("answers")) {
                        Map<String, Object> answersHM = (Map<String, Object>) i.getValue();
                        for (HashMap.Entry ii : answersHM.entrySet()) {
                            Map<String, Map<String, Object>> oneAnswer = (Map<String, Map<String, Object>>) ii.getValue();
                            String answer = "";
                            boolean correct = false;
                            for (HashMap.Entry oneField : oneAnswer.entrySet()) {
                                if (oneField.getKey().equals("answer")) {
                                    answer = (String) oneField.getValue();
                                }
                                if (oneField.getKey().equals("correct")) {
                                    correct = (boolean) oneField.getValue();
                                }
                            }
                            AnsweredQuestion aQ = new AnsweredQuestion(answer, correct);
                            answers.add(aQ);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
            Intent intent = new Intent(this, AddQuestionActivity.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.historyButton) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        }
    }
}

