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

    Spinner spinner;

    List<Question> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_questions_activity);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

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

        getData();
        populateRecyclerView(questions);
    }

    // add new param for the field for comparison
    public void populateRecyclerView(final List<Question> q) {
        Collections.sort(q, (o1, o2) -> o1.getCourse().compareTo(o2.getCourse()));
        adapter = new QuestionsAdapter(q);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Question selectedQuestion = q.get(position);
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
                    String key = d.getKey();
                    q.setKey(key);
                    questions.add(q);
                }
                adapter.notifyDataSetChanged();
                addItemOnSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addItemOnSpinner() {
        List<String> options = new ArrayList<String>();
        options.add("All courses");

        for (Question q : questions) {
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    public void onOptionSelected() {
        String selectedOption = spinner.getSelectedItem().toString().trim();
        List<Question> selectedQuestions = new ArrayList<>();

        for (Question q : questions) {
            if (q.getCourse().equals(selectedOption)) {
                selectedQuestions.add(q);
            }
        }

        if (spinner.getSelectedItem().toString().trim().equals("All courses")) {
            populateRecyclerView(questions);
        } else {
            populateRecyclerView(selectedQuestions);
        }
    }
}
