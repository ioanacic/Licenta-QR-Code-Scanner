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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeeStudentsActivity extends Activity {
    private RecyclerView recyclerView;
    private StudentsAdapter adapter;
    private DatabaseReference mDatabase;

    Spinner spinner;

    List<User> students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_students_activity);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_students);

        spinner = (Spinner) findViewById(R.id.groupsOptions);
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
        populateRecyclerView(students);

    }

    public void populateRecyclerView(final List<User> s) {
        // sort alfabetically
        Collections.sort(s, (o1, o2) -> o1.getLastName().compareTo(o2.getLastName()));
        adapter = new StudentsAdapter(s);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        User selectedStudent = students.get(position);
//                        String key = selectedStudent.getKey();
//                        Intent intent = new Intent(SeeStudentsActivity.this, GenerateQRActivity.class);
//                        intent.putExtra("KEY", key);
//                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }

    public void getData() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String key = d.getKey();
                    Map<String, User> userValue = (HashMap<String, User>) d.getValue();
                    String lastName = "";
                    String firstName = "";
                    String group = "";
                    String year = "";
                    String score = "";

                    for (HashMap.Entry i : userValue.entrySet()) {
                        if (i.getKey().equals("lastName")) {
                            lastName = (String) i.getValue();
                        }
                        if (i.getKey().equals("firstName")) {
                            firstName = (String) i.getValue();
                        }
                        if (i.getKey().equals("group")) {
                            group = (String) i.getValue();
                        }
                        if (i.getKey().equals("yearOfStudy")) {
                            year = (String) i.getValue();
                        }
                        if (i.getKey().equals("score")) {
                            score = (String) i.getValue();
                        }
                    }
                    User u = new User(lastName, firstName, group, year, score);
                    u.setKey(key);
                    students.add(u);
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
        options.add("All groups");

        for (User u : students) {
            boolean contains = false;
            for (String o : options) {
                if (u.getGroup().equals(o)) {
                    contains = true;
                }
            }
            if (contains == false) {
                options.add(u.getGroup());
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    public void onOptionSelected() {
        String selectedOption = spinner.getSelectedItem().toString().trim();
        List<User> selectedStudents = new ArrayList<>();

        for (User u : students) {
            if (u.getGroup().equals(selectedOption)) {
                selectedStudents.add(u);
            }
        }

        if (spinner.getSelectedItem().toString().trim().equals("All groups")) {
            populateRecyclerView(students);
        } else {
            populateRecyclerView(selectedStudents);
        }
    }
}
