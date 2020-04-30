package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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

public class HistoryActivity extends Activity {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, historyAsProfessor, historyAsStudent;

    TextView yourScore;
    Spinner spinner;
    String score;
    String keyOfSelectedStudent;
    List<AnsweredQuestion> answeredQuestions = new ArrayList<>();
    List<Question> questionsAQ = new ArrayList<>();
    List<Question> professorQuestionsAQ = new ArrayList<>();        // if not empty, then the professor wanna access history
    Map<String, String> professorsSubjects = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_QS);
        yourScore = (TextView) findViewById(R.id.yourScore);

        spinner = (Spinner) findViewById(R.id.subjectsOptions);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onOptionSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mAuth = FirebaseAuth.getInstance();

        // it s a professor that s accessing student's history
        if (getIntent().getStringExtra("KEY") != null) {
            keyOfSelectedStudent = getIntent().getStringExtra("KEY");
        }


        getAnsweredQuestions();
    }

    public void populateRecyclerView(final List<Question> q) {
        Collections.sort(q, (o1, o2) -> o1.getCourse().compareTo(o2.getCourse()));
        adapter = new HistoryAdapter(q);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Question selectedQuestion = q.get(position);
                        String key = selectedQuestion.getKey();
                        Intent intent = new Intent(HistoryActivity.this, SeeAQActivity.class);
                        intent.putExtra("KEY", key);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }

    public void getAnsweredQuestions() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        if (keyOfSelectedStudent != null) {
            // the professor
            historyAsProfessor = mDatabase.child(keyOfSelectedStudent);
            seeHistoryAs(historyAsProfessor);
            spinner.setVisibility(View.INVISIBLE);
        } else {
            // the student
            historyAsStudent = mDatabase.child(mAuth.getCurrentUser().getUid());
            seeHistoryAs(historyAsStudent);
        }

    }

    public void seeHistoryAs(DatabaseReference dbR) {
        dbR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                for (HashMap.Entry i : map.entrySet()) {
                    if (i.getKey().equals("answers")) {
                        Map<String, Object> answersHM = (Map<String, Object>) i.getValue();
                        for (HashMap.Entry ii : answersHM.entrySet()) {
                            String key = (String) ii.getKey();
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
                            aQ.setqId(key);
                            answeredQuestions.add(aQ);
                        }
                    }
                    if (i.getKey().equals("score")) {
                        score = (String) i.getValue();
                    }
                }
                getInfoAboutAQ();
                yourScore.setText(score);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getInfoAboutAQ() {
        mDatabase = FirebaseDatabase.getInstance().getReference("questions");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    for (AnsweredQuestion aQ : answeredQuestions) {
                        if (aQ.getqId().equals(d.getKey())) {
                            Question q = d.getValue(Question.class);

                            String key = d.getKey();
                            q.setKey(key);

                            String isCorrect;
                            if (aQ.isCorrect()) {
                                isCorrect = "correct";
                            } else {
                                isCorrect = "wrong";
                            }
                            q.setCorrect(isCorrect);

                            String idProfessor = q.getIdProfessor();
                            q.setIdProfessor(idProfessor);

                            if (mAuth.getCurrentUser().getUid().equals(idProfessor)) {
                                professorQuestionsAQ.add(q);
                            }

                            questionsAQ.add(q);
                        }
                    }
                }
                getProfessorsData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getProfessorsData() {
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    // get the type of user
                    Map<String, User> userValue = (HashMap<String, User>) d.getValue();
                    String typeOfUser = "";
                    for (HashMap.Entry i : userValue.entrySet()) {
                        if (i.getKey().equals("typeOfUser")) {
                            typeOfUser = (String) i.getValue();
                        }
                    }

                    // we only get the professors
                    if (typeOfUser.equals("P")) {
                        for (Question q : questionsAQ) {
                            if (d.getKey().equals(q.getIdProfessor())) {
                                Map<String, Professor> professorValue = (HashMap<String, Professor>) d.getValue();
                                String key = d.getKey();
                                String lastName = "";
                                String firstName = "";
                                String subject = "";

                                for (HashMap.Entry i : professorValue.entrySet()) {
                                    if (i.getKey().equals("lastName")) {
                                        lastName = (String) i.getValue();
                                    }
                                    if (i.getKey().equals("firstName")) {
                                        firstName = (String) i.getValue();
                                    }
                                    if (i.getKey().equals("subject")) {
                                        subject = (String) i.getValue();
                                    }
                                }

                                professorsSubjects.put(key, subject + " - " + lastName + " " + firstName);
                            }
                        }
                    }
                }
                if (!professorQuestionsAQ.isEmpty()) {
                    populateRecyclerView(professorQuestionsAQ);
                } else {
                    populateRecyclerView(questionsAQ);
                }
                adapter.notifyDataSetChanged();
                addItemsOnSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addItemsOnSpinner() {
        List<String> options = new ArrayList<String>();
        options.add("All subjects");

        for (Map.Entry entry : professorsSubjects.entrySet()) {
            boolean contains = false;
            for (String o : options) {
                if (entry.getValue().equals(o)) {
                    contains = true;
                }
            }
            if (contains == false) {
                options.add(entry.getValue().toString());
            }
        }

        Collections.sort(options, (o1, o2) -> o1.compareTo(o2));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    public void onOptionSelected() {
        String selectedOption = spinner.getSelectedItem().toString().trim();
        List<Question> selectedQuestions = new ArrayList<>();

        // is the student wanna access history
        if (professorQuestionsAQ.isEmpty()) {
            if (selectedOption.equals("All subjects")) {
                populateRecyclerView(questionsAQ);
            } else {
                String idProfessor = "";
                // get professor id for option selected
                for (Map.Entry entry : professorsSubjects.entrySet()) {
                    if (entry.getValue().equals(selectedOption)) {
                        idProfessor = entry.getKey().toString();
                    }
                }

                for (Question q : questionsAQ) {
                    if (q.getIdProfessor().equals(idProfessor)) {
                        selectedQuestions.add(q);
                    }
                }


                populateRecyclerView(selectedQuestions);
            }
        }
    }
}
