package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddQuestionActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "AddQuestionActivity";

    TextInputEditText questionField, courseField;
    TextInputEditText answerAField, answerBField, answerCField, answerDField;
    Spinner spinner, spinnerSubject;
    boolean isEmpty = false;

    List<String> options = new ArrayList<String>();
    List<String> optionsSubjects = new ArrayList<String>();

    private DatabaseReference mDatabase, mDatabaseUsers;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_question_activity);

        // Views
        questionField = findViewById(R.id.questionField);
        answerAField = findViewById(R.id.firstAnswerField);
        answerBField = findViewById(R.id.secondAnswerField);
        answerCField = findViewById(R.id.thirdAnswerField);
        answerDField = findViewById(R.id.fourthAnswerField);
        courseField = findViewById(R.id.courseField);
        spinner = findViewById(R.id.anwerOptions);
        spinnerSubject = findViewById(R.id.spinnerSubject);

        // Buttons
        findViewById(R.id.saveButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("questions");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");

        addItemOnSpinner();
        addItemOnSpinnerSubject();
    }

    public void addItemOnSpinner() {
        options.add("Correct Answer");
        options.add("Answer A");
        options.add("Answer B");
        options.add("Answer C");
        options.add("Answer D");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    public void addItemOnSpinnerSubject() {
        optionsSubjects.add("Subject");
        getAllSubjects();

        // dont know if necessary
//        Collections.sort(options, (o1, o2) -> o1.compareTo(o2));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, optionsSubjects);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerSubject.setAdapter(dataAdapter);
    }

    public void getAllSubjects() {
        mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                for (HashMap.Entry i : map.entrySet()) {
                    if (i.getKey().equals("subjects")) {
                        Map<String, Object> subjectsHM = (Map<String, Object>) i.getValue();
                        for (HashMap.Entry ii : subjectsHM.entrySet()) {
                            optionsSubjects.add(ii.getValue().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addQuestion() {
        final String question;
        final String courseNr;
        final String answerA;
        final String answerB;
        final String answerC;
        final String answerD;
        final String correctAnswer;
        final String idProfessor;
        final String subject;
        String course = "";

        question = questionField.getText().toString().trim();
        courseNr = courseField.getText().toString().trim();
        answerA = answerAField.getText().toString().trim();
        answerB = answerBField.getText().toString().trim();
        answerC = answerCField.getText().toString().trim();
        answerD = answerDField.getText().toString().trim();
        correctAnswer = spinner.getSelectedItem().toString().trim();
        idProfessor = mAuth.getCurrentUser().getUid();
        subject = spinnerSubject.getSelectedItem().toString().trim();

        if (question.isEmpty() || courseNr.isEmpty() || answerA.isEmpty() || answerB.isEmpty() || answerC.isEmpty() ||
                answerD.isEmpty() || correctAnswer.isEmpty() || subject.isEmpty()) {
            Toast.makeText(AddQuestionActivity.this, getString(R.string.noFieldEmpty), Toast.LENGTH_LONG).show();
            isEmpty = true;
            return;
        } else {
            course = "Course " + courseNr;
        }

        isEmpty = false;
        Question newQuestion = new Question(question, course, answerA, answerB, answerC, answerD, correctAnswer, idProfessor, subject);
        String uniqueId = UUID.randomUUID().toString();     // generate unique id for the entry

        mDatabase.child(uniqueId).setValue(newQuestion).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddQuestionActivity.this, getString(R.string.questionAddedSuccsessfully), Toast.LENGTH_LONG).show();
                    if (!isEmpty) {
                        questionField.getText().clear();
                        courseField.getText().clear();
                        answerAField.getText().clear();
                        answerBField.getText().clear();
                        answerCField.getText().clear();
                        answerDField.getText().clear();
                        spinner.setSelection(0);
                        spinnerSubject.setSelection(0);
                    }
                } else {
                    Log.w(TAG, "FAILED");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.saveButton) {
            addQuestion();
        }
    }
}
