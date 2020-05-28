package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SeeQuestionsActivity extends Activity implements SeeQuestionListener {
    private RecyclerView recyclerView;
    private QuestionsAdapter adapter;
    private DatabaseReference mDatabase, mDatabaseTest;
    private FirebaseAuth mAuth;

    Spinner spinner, spinnerSubject;
    Button createTestButton;

    List<Question> questions = new ArrayList<>();
    List<Question> questionsBySubject = new ArrayList<>();
    List<Question> questionsByCourse = new ArrayList<>();
    List<Question> questionsForTest = new ArrayList<>();

    ArrayAdapter<String> dataAdapterSpinner;

    boolean isPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_questions_activity);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mAuth = FirebaseAuth.getInstance();

        spinner = (Spinner) findViewById(R.id.coursesOptions);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onOptionSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerSubject = (Spinner) findViewById(R.id.subjectsOptions);
        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                questionsBySubject.clear();
                onOptionSelectedSubject();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createTestButton = findViewById(R.id.createTest);
        createTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTest();
                isPressed = true;
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference("questions");
        mDatabaseTest = FirebaseDatabase.getInstance().getReference("tests");
        populateRecyclerView();
        getData();
    }

    // add new param for the field for comparison
    public void populateRecyclerView() {
        adapter = new QuestionsAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Question selectedQuestion = adapter.getMyQuestions().get(position);
                        String key = selectedQuestion.getKey();
                        String subject = spinnerSubject.getSelectedItem().toString().trim();
                        String course = selectedQuestion.getCourse();

                        Intent intent = new Intent(SeeQuestionsActivity.this, GenerateQRActivity.class);
                        intent.putExtra("KEY", key);
                        intent.putExtra("COURSE", course);
                        intent.putExtra("SUBJECT", subject);
                        intent.putExtra("TYPE", "question");

                        if (spinnerSubject.getSelectedItem().toString().trim().equals("All subjects")) {
                            Toast.makeText(getApplicationContext(), R.string.selectASubject, Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Question selectedQuestion = adapter.getMyQuestions().get(position);
                        String key = selectedQuestion.getKey();
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked

                                        mDatabase.child(key).removeValue();
                                        Intent intent = new Intent(SeeQuestionsActivity.this, SeeQuestionsActivity.class);
                                        startActivity(intent);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage("Are you sure you want to permanently delete the question?").
                                setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                        adapter.notifyDataSetChanged();
                    }
                })
        );
    }

    public void getData() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Question q = d.getValue(Question.class);
                    // takes the questions created by the logged in professor
                    // TODO - remove the not null condition (it s just for now, when i have Qs without profId)
                    if (q.getIdProfessor() != null && q.getIdProfessor().equals(mAuth.getCurrentUser().getUid())) {
                        String key = d.getKey();
                        q.setKey(key);
                        questions.add(q);
                    }
                }
                adapter.notifyDataSetChanged();
//                addItemOnSpinner();
                addItemOnSpinnerSubject();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getDataBySubject() {
        for (Question q : questions) {
            if (q.getSubject().equals(spinnerSubject.getSelectedItem().toString().trim())) {
                questionsBySubject.add(q);
            }
        }
        addItemOnSpinner();
    }

    public void addItemOnSpinner() {
        List<String> options = new ArrayList<String>();
        options.add("All courses");

        if (!questionsBySubject.isEmpty()) {
            for (Question q : questionsBySubject) {
                boolean contains = false;
                for (String o : options) {
                    if (q.getCourse().equals(o)) {
                        contains = true;
                    }
                }
                if (contains == false) {
                    options.add(q.getCourse());
                }
            }
        }

        Collections.sort(options, (o1, o2) -> o1.compareTo(o2));

        dataAdapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        dataAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapterSpinner);
    }

    public void addItemOnSpinnerSubject() {
        List<String> options = new ArrayList<String>();
        options.add("All subjects");

        for (Question q : questions) {
            boolean contains = false;
            for (String o : options) {
                if (q.getSubject().equals(o)) {
                    contains = true;
                }
            }
            if (contains == false) {
                options.add(q.getSubject());
            }
        }

        Collections.sort(options, (o1, o2) -> o1.compareTo(o2));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerSubject.setAdapter(dataAdapter);
    }

    public void onOptionSelected() {
        String selectedOption = spinner.getSelectedItem().toString().trim();
        List<Question> selectedQuestions = new ArrayList<>();

        for (Question q : questionsBySubject) {
            if (q.getCourse().equals(selectedOption)) {
                selectedQuestions.add(q);
            }
        }

        if (spinner.getSelectedItem().toString().trim().equals("All courses")) {
            if (questionsBySubject.isEmpty()) {
                return;
            } else {
                adapter.updateQ(questionsBySubject);
            }
        } else {
            adapter.updateQ(selectedQuestions);
        }

        if (!questionsByCourse.isEmpty()) {
            questionsByCourse.clear();
        }
        questionsByCourse = selectedQuestions;
    }

    public void onOptionSelectedSubject() {
        // there were selected questions for test, but changed the subject, so RESET
        if (!questionsForTest.isEmpty()) {
            for (Question q : questionsForTest) {
                q.setSelected(false);
                mDatabase.child(q.getKey()).child("selected").setValue(false);
            }

            questionsForTest.clear();
        }

        String selectedOption = spinnerSubject.getSelectedItem().toString().trim();
        List<Question> selectedQuestions = new ArrayList<>();

        for (Question q : questions) {
            if (q.getSubject().equals(selectedOption)) {
                selectedQuestions.add(q);
            }
        }

        if (spinnerSubject.getSelectedItem().toString().trim().equals("All subjects")) {
            adapter.updateQ(questions);
        } else {
            adapter.updateQ(selectedQuestions);

        }

        getDataBySubject();
    }

    @Override
    public void onAddQButtonClicked(Question question) {
        // TODO update somehow that you cannoe DESELECT a question added to another test
        // with if condition = the button shows SELECTED, if questionsForTest.isEmpty = the questions are added in another test, CANNOT ADD AGAIN
        // without if condition = the button is selected, with DESELECT AND SELECT AGAIN can add the question to another test

        if (spinnerSubject.getSelectedItem().toString().trim().equals("All subjects")) {
//            updateTestQs(questions, question);
//            adapter.updateQ(questions);
            Toast.makeText(getApplicationContext(), R.string.selectASubject, Toast.LENGTH_SHORT).show();
        } else if (spinner.getSelectedItem().toString().trim().equals("All courses")) {
            updateTestQs(questionsBySubject, question);
            adapter.updateQ(questionsBySubject);
        } else {
            updateTestQs(questionsByCourse, question);
            adapter.updateQ(questionsByCourse);
        }
    }

    public void updateTestQs(List<Question> questions, Question question) {
        // if the professor added a test, the questionsForTest is empty now, but isPressed = true, so RESET
        if (questionsForTest.size() == 0) {
            isPressed = false;
        }

        // TODO user can deselect a question randomly, even if it s in a test and he doesnt want to use it in another
        for (Question q : questions) {
            if (q.equals(question)) {
                if (!q.isSelected()) {
                    // q.isSelected = false = nu a fost apasat
                    q.setSelected(true);
                    mDatabase.child(q.getKey()).child("selected").setValue(true);

                    questionsForTest.add(question);
                } else {
                    // q.isSelected = true = a fost apasat, ii anulez efectul
                    q.setSelected(false);
                    mDatabase.child(q.getKey()).child("selected").setValue(false);

                    for (Question qT : questionsForTest) {
                        if (qT.equals(q)) {
                            questionsForTest.removeAll(Arrays.asList(qT));
                            break;
                        }
                    }
                }
            }
        }

    }

    public void createTest() {
        String uniqueId = UUID.randomUUID().toString();
        String title = "";

        mDatabaseTest.child(uniqueId).child("professorKey").setValue(mAuth.getCurrentUser().getUid());
        for (Question q : questionsForTest) {
            mDatabaseTest.child(uniqueId).child(q.getKey()).setValue("");
        }
        mDatabaseTest.child(uniqueId).child("numberOfQuestions").setValue(questionsForTest.size());

        if (!spinnerSubject.getSelectedItem().toString().trim().equals("All subjects")) {
            title = spinnerSubject.getSelectedItem().toString().trim();
            if (!spinner.getSelectedItem().toString().trim().equals("All courses")) {
                title = title + " - " + spinner.getSelectedItem().toString().trim();
            }
            mDatabaseTest.child(uniqueId).child("title").setValue(title);
        }

        Toast.makeText(getApplicationContext(), R.string.testSaved, Toast.LENGTH_SHORT).show();

        questionsForTest.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // remove the selection for questions if the button is not pressed
        if (!isPressed) {
            for (Question q : questionsForTest) {
                q.setSelected(false);
                mDatabase.child(q.getKey()).child("selected").setValue(false);
            }
        }
    }

}
