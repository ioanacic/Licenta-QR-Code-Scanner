package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddSubjectActivity extends Activity implements View.OnClickListener {

    EditText newSubjectField;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_subject_activity);

        newSubjectField = findViewById(R.id.newSubjectField);

        findViewById(R.id.saveSubjectButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
    }

    public void addSubject() {
        final String subject;
        subject = newSubjectField.getText().toString().trim();

        if (subject.isEmpty()) {
            Toast.makeText(AddSubjectActivity.this, getString(R.string.noFieldEmpty), Toast.LENGTH_LONG).show();
            return;
        }

        String uniqueId = UUID.randomUUID().toString();
        mDatabase.child("subjects").child(uniqueId).setValue(subject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddSubjectActivity.this, getString(R.string.subjectAddedSuccsessfully), Toast.LENGTH_LONG).show();
                    newSubjectField.getText().clear();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.saveSubjectButton) {
            addSubject();
        }
    }
}
