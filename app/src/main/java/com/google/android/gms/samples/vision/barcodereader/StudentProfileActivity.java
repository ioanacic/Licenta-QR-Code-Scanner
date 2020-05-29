package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class StudentProfileActivity extends Activity implements View.OnClickListener {

    TextInputEditText lastNameField;
    TextInputEditText firstNameField;
    TextInputEditText groupField;
    TextInputEditText yearOfStudyField;
    TextInputEditText oldPassField, newPassField;

    TextInputLayout oldPLayout, newPLayout;

    Button updatebPass;

    Student currentStudent;

    boolean error = false;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_profile_activity);

        lastNameField = (TextInputEditText) findViewById(R.id.changeLNameField);
        firstNameField = (TextInputEditText) findViewById(R.id.changeFNameField);
        groupField = (TextInputEditText) findViewById(R.id.changeGroupField);
        yearOfStudyField = (TextInputEditText) findViewById(R.id.changeYearField);
        oldPassField = (TextInputEditText) findViewById(R.id.oldPassword);
        newPassField = (TextInputEditText) findViewById(R.id.newPassword);

        oldPLayout = findViewById(R.id.oldPLayout);
        newPLayout = findViewById(R.id.newPLayout);

        findViewById(R.id.saveChangesStudent).setOnClickListener(this);
        findViewById(R.id.changePasswordStudent).setOnClickListener(this);
        updatebPass = findViewById(R.id.updatePasswordButton);
        updatebPass.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
        firebaseUser = mAuth.getCurrentUser();

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
        if (!lastNameField.getText().equals(currentStudent.getLastName()) && !lastNameField.getText().toString().isEmpty()) {
            lName = lastNameField.getText().toString().trim();
            mDatabase.child("lastName").setValue(lName);
        }
        if (!firstNameField.getText().equals(currentStudent.getFirstName()) && !lastNameField.getText().toString().isEmpty()) {
            fName = firstNameField.getText().toString().trim();
            mDatabase.child("firstName").setValue(fName);
        }
        if (!groupField.getText().equals(currentStudent.getGroup()) && !lastNameField.getText().toString().isEmpty()) {
            group = groupField.getText().toString().trim();
            mDatabase.child("group").setValue(group);
        }
        if (!yearOfStudyField.getText().equals(currentStudent.getYearOfStudy()) && !lastNameField.getText().toString().isEmpty()) {
            year = yearOfStudyField.getText().toString().trim();
            mDatabase.child("yearOfStudy").setValue(year);
        }
    }

    public void updatePassword() {
        final String email = firebaseUser.getEmail();
        final String oldPass = oldPassField.getText().toString().trim();
        final String newPass = newPassField.getText().toString().trim();
        verifyPass(oldPass, newPass);
        if (error) {
            return;
        }
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPass);

        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(StudentProfileActivity.this, R.string.changePassFailed,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(StudentProfileActivity.this, R.string.changePassSucceded,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(StudentProfileActivity.this, R.string.auth_failed,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verifyPass(String oldp, String newp) {
        if (oldp.isEmpty()) {
            oldPLayout.setError("This field cannot be empty");
            error = true;
        } else {
            error = false;
        }
        if (newp.isEmpty()) {
            newPLayout.setError("This field cannot be empty");
            error = true;
        } else {
            error = false;
        }
        if (newp.length() < 6) {
            newPLayout.setError("Password must be at least 6 characters");
            error = true;
        } else {
            error = false;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.saveChangesStudent) {
            save();
            Intent intent = new Intent(StudentProfileActivity.this, StudentAccountActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.changePasswordStudent) {
            oldPLayout.setVisibility(View.VISIBLE);
            newPLayout.setVisibility(View.VISIBLE);
            updatebPass.setVisibility(View.VISIBLE);
        }
        if (view.getId() == R.id.updatePasswordButton) {
            updatePassword();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, StudentAccountActivity.class);
        startActivity(intent);
    }
}
