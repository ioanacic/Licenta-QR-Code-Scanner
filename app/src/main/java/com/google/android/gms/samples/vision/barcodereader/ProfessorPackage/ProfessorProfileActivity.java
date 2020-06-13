package com.google.android.gms.samples.vision.barcodereader.ProfessorPackage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.samples.vision.barcodereader.Classes.Professor;
import com.google.android.gms.samples.vision.barcodereader.R;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfessorProfileActivity extends Activity implements View.OnClickListener {

    TextInputEditText lastNameField;
    TextInputEditText firstNameField;
    TextInputEditText oldPassField, newPassField;
    TextInputLayout oldPLayout, newPLayout;

    Spinner spinner;
    Button updatebPass;

    Professor currentProfessor;

    boolean error = false;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.professor_profile_activity);

        lastNameField = (TextInputEditText) findViewById(R.id.changeLName);
        firstNameField = (TextInputEditText) findViewById(R.id.changeFName);
        oldPassField = (TextInputEditText) findViewById(R.id.oldPasswordProf);
        newPassField = (TextInputEditText) findViewById(R.id.newPasswordProf);

        oldPLayout = findViewById(R.id.oldPLayoutProf);
        newPLayout = findViewById(R.id.newPLayoutProf);

        spinner = (Spinner) findViewById(R.id.subjects);

        findViewById(R.id.saveChangesProfessor).setOnClickListener(this);
        findViewById(R.id.changePasswordProfessor).setOnClickListener(this);
        updatebPass = findViewById(R.id.updatePasswordProf);
        updatebPass.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
        firebaseUser = mAuth.getCurrentUser();

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
        int count = 0;

        if (!lastNameField.getText().toString().equals(currentProfessor.getLastName())) {
            if (lastNameField.getText().toString().isEmpty()) {
                Toast.makeText(ProfessorProfileActivity.this, getString(R.string.noFieldEmpty), Toast.LENGTH_LONG).show();
                return;
            }
            lName = lastNameField.getText().toString().trim();
            mDatabase.child("lastName").setValue(lName);
            count++;
        }
        if (!firstNameField.getText().toString().equals(currentProfessor.getFirstName())) {
            if (firstNameField.getText() == null) {
                Toast.makeText(ProfessorProfileActivity.this, getString(R.string.noFieldEmpty), Toast.LENGTH_LONG).show();
                return;
            }
            fName = firstNameField.getText().toString().trim();
            mDatabase.child("firstName").setValue(fName);
            count++;
        }

        if (count != 0) {
            Intent intent = new Intent(ProfessorProfileActivity.this, ProfessorMenuActivity.class);
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
                                Toast.makeText(ProfessorProfileActivity.this, R.string.changePassFailed,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfessorProfileActivity.this, R.string.changePassSucceded,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ProfessorProfileActivity.this, R.string.auth_failed,
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
        if (view.getId() == R.id.saveChangesProfessor) {
            save();
        }
        if (view.getId() == R.id.changePasswordProfessor) {
            oldPLayout.setVisibility(View.VISIBLE);
            newPLayout.setVisibility(View.VISIBLE);
            updatebPass.setVisibility(View.VISIBLE);
        }
        if (view.getId() == R.id.updatePasswordProf) {
            updatePassword();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, ProfessorMenuActivity.class);
        startActivity(intent);
    }
}
