package com.google.android.gms.samples.vision.barcodereader;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddQuestionFragment extends Fragment implements View.OnClickListener {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_question_activity, container, false);

        // Views
        questionField = rootView.findViewById(R.id.questionField);
        answerAField = rootView.findViewById(R.id.firstAnswerField);
        answerBField = rootView.findViewById(R.id.secondAnswerField);
        answerCField = rootView.findViewById(R.id.thirdAnswerField);
        answerDField = rootView.findViewById(R.id.fourthAnswerField);
        courseField = rootView.findViewById(R.id.courseField);
        spinner = rootView.findViewById(R.id.anwerOptions);
        spinnerSubject = rootView.findViewById(R.id.spinnerSubject);

        // Buttons
        rootView.findViewById(R.id.saveButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("questions");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("users");

        addItemOnSpinner();
        addItemOnSpinnerSubject();

        return rootView;
    }

    public void addItemOnSpinner() {
        options.add("Select correct answer");
        options.add("Answer A");
        options.add("Answer B");
        options.add("Answer C");
        options.add("Answer D");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    public void addItemOnSpinnerSubject() {
        optionsSubjects.add("Select a subject");
        getAllSubjects();

        // dont know if necessary
//        Collections.sort(options, (o1, o2) -> o1.compareTo(o2));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, optionsSubjects);
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

        if (subject.equals("Select a subject")) {
            TextView errorTextview = (TextView) spinnerSubject.getSelectedView();
            errorTextview.setError("");
            errorTextview.setTextColor(Color.RED);
            errorTextview.setText("Select a subject");

//            ((TextView)spinnerSubject.getSelectedView()).setError("Choose an option");
            return;
        }

        if (correctAnswer.equals("Select correct answer")) {
            TextView errorTextview = (TextView) spinner.getSelectedView();
            errorTextview.setError("");
            errorTextview.setTextColor(Color.RED);
            errorTextview.setText("Select correct answer");

//            ((TextView)spinner.getSelectedView()).setError("Choose an option");
            return;
        }

        if (question.isEmpty() || courseNr.isEmpty() || answerA.isEmpty() || answerB.isEmpty() ||
                answerC.isEmpty() || answerD.isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.noFieldEmpty), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.questionAddedSuccsessfully), Toast.LENGTH_LONG).show();
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
