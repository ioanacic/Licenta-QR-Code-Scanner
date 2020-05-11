package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.View;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfessorProfileActivity extends Activity implements View.OnClickListener {

    TextInputEditText lastNameField;
    TextInputEditText firstNameField;
    Spinner spinner;

    Professor currentProfessor;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.professor_profile_activity);

        lastNameField = (TextInputEditText) findViewById(R.id.changeLName);
        firstNameField = (TextInputEditText) findViewById(R.id.changeFName);
        spinner = (Spinner) findViewById(R.id.subjects);

        findViewById(R.id.saveChangesProfessor).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

        getProfessorInfo();
    }

    public void getProfessorInfo() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                String lName = "", fName = "";
                List<String> subjects = new ArrayList<>();
                for (HashMap.Entry i : map.entrySet()) {
                    if (i.getKey().equals("lastName")) {
                        lName = i.getValue().toString();
                        lastNameField.setText(lName);
                    } else if (i.getKey().equals("firstName")) {
                        fName = i.getValue().toString();
                        firstNameField.setText(fName);
                    } else if (i.getKey().equals("subjects")) {
                        Map<String, String> subjectsHM = (Map<String, String>) i.getValue();
                        for (Map.Entry entry : subjectsHM.entrySet()) {
                            subjects.add(entry.getValue().toString());
                        }
                    }
                }
                currentProfessor = new Professor(lName, fName, subjects);
                addItemOnSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addItemOnSpinner() {
        List<String> options = new ArrayList<String>();
        options.add("All subjects");
        List<String> subjects = currentProfessor.getSubjects();

        for (String s : subjects) {
            boolean contains = false;
            for (String o : options) {
                if (s.equals(o)) {
                    contains = true;
                }
            }
            if (contains == false) {
                options.add(s);
            }
        }

        Collections.sort(options, (o1, o2) -> o1.compareTo(o2));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    public void save() {
        String lName = "", fName = "";
        if (!lastNameField.getText().equals(currentProfessor.getLastName())) {
            lName = lastNameField.getText().toString().trim();
            mDatabase.child("lastName").setValue(lName);
        }
        if (!firstNameField.getText().equals(currentProfessor.getFirstName())) {
            fName = firstNameField.getText().toString().trim();
            mDatabase.child("firstName").setValue(fName);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.saveChangesProfessor) {
            save();
            Intent intent = new Intent(ProfessorProfileActivity.this, ProfessorAccountActivity.class);
            startActivity(intent);
        }
    }
}
