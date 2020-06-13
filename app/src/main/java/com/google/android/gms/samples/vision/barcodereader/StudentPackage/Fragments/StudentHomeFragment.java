package com.google.android.gms.samples.vision.barcodereader.StudentPackage.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.samples.vision.barcodereader.R;
import com.google.android.gms.samples.vision.barcodereader.Authentication.SignInActivity;
import com.google.android.gms.samples.vision.barcodereader.StudentPackage.StudentProfileActivity;
import com.google.firebase.auth.FirebaseAuth;

public class StudentHomeFragment extends Fragment implements View.OnClickListener {
    FirebaseAuth mAuth;

    ImageView img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.student_home_activity, container, false);
        rootView.findViewById(R.id.studentProfileButton).setOnClickListener(this);
        rootView.findViewById(R.id.studentSignOutButton).setOnClickListener(this);
        img = rootView.findViewById(R.id.logoImageView);
        img.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.studentProfileButton) {
            Intent intent = new Intent(getActivity(), StudentProfileActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.studentSignOutButton) {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            startActivity(intent);
        }
    }
}
