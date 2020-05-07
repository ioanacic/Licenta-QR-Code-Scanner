package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeeQuestionsActivity extends Activity {
    private RecyclerView recyclerView;
    private QuestionsAdapter adapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    Spinner spinner, spinnerSubject;

    List<Question> questions = new ArrayList<>();
    List<Question> questionsBySubject = new ArrayList<>();

    ArrayAdapter<String> dataAdapterSpinner;

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


        populateRecyclerView();
        getData();
    }

    // add new param for the field for comparison
    public void populateRecyclerView() {
        adapter = new QuestionsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Question selectedQuestion = adapter.getMyQuestions().get(position);
                        String key = selectedQuestion.getKey();
                        Intent intent = new Intent(SeeQuestionsActivity.this, GenerateQRActivity.class);
                        intent.putExtra("KEY", key);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }

    public void getData() {
        mDatabase = FirebaseDatabase.getInstance().getReference("questions");
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
    }

    public void onOptionSelectedSubject() {
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
}
