package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class AddQuestion extends Activity implements View.OnClickListener {
    private static final String TAG = "AddQuestion";

    EditText questionField;
    EditText answerAField, answerBField, answerCField, answerDField;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

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

        // Buttons
        findViewById(R.id.saveButton).setOnClickListener(this);
        findViewById(R.id.seeQButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("questions");
    }

    public void addQuestion() {
        final String question;
        final String answerA;
        final String answerB;
        final String answerC;
        final String answerD;

        question = questionField.getText().toString().trim();
        answerA = answerAField.getText().toString().trim();
        answerB = answerBField.getText().toString().trim();
        answerC = answerCField.getText().toString().trim();
        answerD = answerDField.getText().toString().trim();

        Question newQuestion = new Question(question, answerA, answerB, answerC, answerD);
        String uniqueId = UUID.randomUUID().toString();     // generate unique id for the entry

        mDatabase.child(uniqueId).setValue(newQuestion).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddQuestion.this, getString(R.string.questionAddedSuccsessfully), Toast.LENGTH_LONG).show();
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
            questionField.getText().clear();
            answerAField.getText().clear();
            answerBField.getText().clear();
            answerCField.getText().clear();
            answerDField.getText().clear();
        } else if (i == R.id.seeQButton) {
            Intent intent = new Intent(this, QuestionCodeActivity.class);
            startActivity(intent);
        }
    }
}
