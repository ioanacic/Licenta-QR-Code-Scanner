package com.google.android.gms.samples.vision.barcodereader.StudentPackage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.samples.vision.barcodereader.R;
import com.google.android.gms.samples.vision.barcodereader.Classes.Student;
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
    TextInputLayout groupLayout, yearLayout;

    Button updatebPass;

    Student currentStudent;

    boolean error = false;
    boolean showG = false;
    boolean showY = false;

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
        groupLayout = findViewById(R.id.groupLayout);
        yearLayout = findViewById(R.id.yearLayout);

        groupLayout.setErrorTextAppearance(R.style.error_appearance);
        yearLayout.setErrorTextAppearance(R.style.error_appearance);

        findViewById(R.id.saveChangesStudent).setOnClickListener(this);
        findViewById(R.id.changePasswordStudent).setOnClickListener(this);
        findViewById(R.id.infoChangeGroup).setOnClickListener(this);
        findViewById(R.id.infoChangeYear).setOnClickListener(this);
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
        int count = 0;

        if (!lastNameField.getText().toString().equals(currentStudent.getLastName())) {
            if (lastNameField.getText().toString().isEmpty()) {
                Toast.makeText(StudentProfileActivity.this, getString(R.string.noFieldEmpty), Toast.LENGTH_LONG).show();
                return;
            }
            lName = lastNameField.getText().toString().trim();
            mDatabase.child("lastName").setValue(lName);
            count++;
        }
        if (!firstNameField.getText().toString().equals(currentStudent.getFirstName())) {
            if (firstNameField.getText().toString().isEmpty()) {
                Toast.makeText(StudentProfileActivity.this, getString(R.string.noFieldEmpty), Toast.LENGTH_LONG).show();
                return;
            }
            fName = firstNameField.getText().toString().trim();
            mDatabase.child("firstName").setValue(fName);
            count++;
        }
        if (!groupField.getText().toString().equals(currentStudent.getGroup())) {
            if (groupField.getText().toString().isEmpty()) {
                Toast.makeText(StudentProfileActivity.this, getString(R.string.noFieldEmpty), Toast.LENGTH_LONG).show();
                return;
            }
            group = groupField.getText().toString().trim();
            mDatabase.child("group").setValue(group);
            count++;
        }
        if (!yearOfStudyField.getText().toString().equals(currentStudent.getYearOfStudy())) {
            if (yearOfStudyField.getText().toString().isEmpty()) {
                Toast.makeText(StudentProfileActivity.this, getString(R.string.noFieldEmpty), Toast.LENGTH_LONG).show();
                return;
            }
            year = yearOfStudyField.getText().toString().trim();
            mDatabase.child("yearOfStudy").setValue(year);
            count++;
        }

        if (count != 0) {
            Intent intent = new Intent(StudentProfileActivity.this, StudentMenuActivity.class);
            startActivity(intent);
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
        }
        if (view.getId() == R.id.changePasswordStudent) {
            oldPLayout.setVisibility(View.VISIBLE);
            newPLayout.setVisibility(View.VISIBLE);
            updatebPass.setVisibility(View.VISIBLE);
        }
        if (view.getId() == R.id.updatePasswordButton) {
            updatePassword();
        }
        if (view.getId() == R.id.infoChangeGroup) {
            if (!showG) {
                groupLayout.setError("Update it at the beginning of every school year");
                groupLayout.setErrorEnabled(true);
                showG = true;
            } else {
                showG = false;
                groupLayout.setErrorEnabled(false);
            }
        }
        if (view.getId() == R.id.infoChangeYear) {
            if (!showY) {
                yearLayout.setError("Update it at the beginning of every school year");
                yearLayout.setErrorEnabled(true);
                showY = true;
            } else {
                yearLayout.setErrorEnabled(false);
                showY = false;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, StudentMenuActivity.class);
        startActivity(intent);
    }
}
