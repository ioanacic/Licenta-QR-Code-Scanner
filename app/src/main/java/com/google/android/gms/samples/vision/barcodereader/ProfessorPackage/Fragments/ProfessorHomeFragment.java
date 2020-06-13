package com.google.android.gms.samples.vision.barcodereader.ProfessorPackage.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.samples.vision.barcodereader.ProfessorPackage.ProfessorProfileActivity;
import com.google.android.gms.samples.vision.barcodereader.R;
import com.google.android.gms.samples.vision.barcodereader.Authentication.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfessorHomeFragment extends Fragment implements View.OnClickListener {
    FirebaseAuth mAuth;

    ImageView img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.professor_home_activity, container, false);
        rootView.findViewById(R.id.professorProfileButton).setOnClickListener(this);
        rootView.findViewById(R.id.professorSignOutButton).setOnClickListener(this);
        rootView.findViewById(R.id.addQuestionButton).setOnClickListener(this);
        rootView.findViewById(R.id.addSubjectButton).setOnClickListener(this);
        img = rootView.findViewById(R.id.logoImageView);
        img.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.professorProfileButton) {
            Intent intent = new Intent(getActivity(), ProfessorProfileActivity.class);
            startActivity(intent);
            img.setVisibility(View.INVISIBLE);
        }
        if (v.getId() == R.id.professorSignOutButton) {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            startActivity(intent);
            img.setVisibility(View.INVISIBLE);
        }
        if (v.getId() == R.id.addQuestionButton) {
            AddQuestionFragment addQuestionFragment = new AddQuestionFragment();

            FragmentTransaction transactionAddQ = getChildFragmentManager().beginTransaction();
            transactionAddQ.replace(R.id.addContainer, addQuestionFragment).commit();
            img.setVisibility(View.INVISIBLE);
        }
        if (v.getId() == R.id.addSubjectButton) {
            AddSubjectFragment addSubjectFragment = new AddSubjectFragment();

            FragmentTransaction transactionAddSj = getChildFragmentManager().beginTransaction();
            transactionAddSj.replace(R.id.addContainer, addSubjectFragment).commit();
            img.setVisibility(View.INVISIBLE);
        }
    }
}
