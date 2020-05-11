package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.EditText;

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

public class StudentProfileActivity extends Activity implements View.OnClickListener {

    TextInputEditText lastNameField;
    TextInputEditText firstNameField;
    TextInputEditText groupField;
    TextInputEditText yearOfStudyField;

    Student currentStudent;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_profile_activity);

        lastNameField = (TextInputEditText) findViewById(R.id.changeLNameField);
        firstNameField = (TextInputEditText) findViewById(R.id.changeFNameField);
        groupField = (TextInputEditText) findViewById(R.id.changeGroupField);
        yearOfStudyField = (TextInputEditText) findViewById(R.id.changeYearField);

        findViewById(R.id.saveChangesStudent).setOnClickListener(this);
        findViewById(R.id.changePasswordStudent).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

        getStudentInfo();
    }

    public void getStudentInfo() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                String lName = "", fName = "", group = "", year = "";
                for (HashMap.Entry i : map.entrySet()) {
                    if (i.getKey().equals("lastName")) {
                        lName = i.getValue().toString();
                        lastNameField.setText(lName);
                    } else if (i.getKey().equals("firstName")) {
                        fName = i.getValue().toString();
                        firstNameField.setText(fName);
                    } else if (i.getKey().equals("group")) {
                        group = i.getValue().toString();
                        groupField.setText(group);
                    } else if (i.getKey().equals("yearOfStudy")) {
                        year = i.getValue().toString();
                        yearOfStudyField.setText(year);
                    }
                }
                currentStudent = new Student(lName, fName, group, year);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void save() {
        String lName = "", fName = "", group = "", year = "";
        if (!lastNameField.getText().equals(currentStudent.getLastName())) {
            lName = lastNameField.getText().toString().trim();
            mDatabase.child("lastName").setValue(lName);
        }
        if (!firstNameField.getText().equals(currentStudent.getFirstName())) {
            fName = firstNameField.getText().toString().trim();
            mDatabase.child("firstName").setValue(fName);
        }
        if (!groupField.getText().equals(currentStudent.getGroup())) {
            group = groupField.getText().toString().trim();
            mDatabase.child("group").setValue(group);
        }
        if (!yearOfStudyField.getText().equals(currentStudent.getYearOfStudy())) {
            year = yearOfStudyField.getText().toString().trim();
            mDatabase.child("yearOfStudy").setValue(year);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.saveChangesStudent) {
            save();
            Intent intent = new Intent(StudentProfileActivity.this, StudentAccountActivity.class);
            startActivity(intent);
        }
    }
}
