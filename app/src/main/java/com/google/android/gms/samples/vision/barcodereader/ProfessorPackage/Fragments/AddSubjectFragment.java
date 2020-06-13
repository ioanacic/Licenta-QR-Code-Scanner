package com.google.android.gms.samples.vision.barcodereader.ProfessorPackage.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.samples.vision.barcodereader.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class AddSubjectFragment extends Fragment implements View.OnClickListener {
    TextInputEditText newSubjectField;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_subject_activity, container, false);

        newSubjectField = rootView.findViewById(R.id.newSubjectField);
        rootView.findViewById(R.id.saveSubjectButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

        return rootView;
    }

    public void addSubject() {
        final String subject;
        subject = newSubjectField.getText().toString().trim();

        if (subject.isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.noFieldEmpty), Toast.LENGTH_LONG).show();
            return;
        }

        String uniqueId = UUID.randomUUID().toString();
        mDatabase.child("subjects").child(uniqueId).setValue(subject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.subjectAddedSuccsessfully), Toast.LENGTH_LONG).show();
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
