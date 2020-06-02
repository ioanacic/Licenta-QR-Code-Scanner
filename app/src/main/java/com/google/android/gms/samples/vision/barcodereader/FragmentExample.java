package com.google.android.gms.samples.vision.barcodereader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

public class FragmentExample extends Fragment implements View.OnClickListener {
    FirebaseAuth mAuth;

    String extra1, extra2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.professor_account_activity, container, false);
        rootView.findViewById(R.id.addQuestionButton).setOnClickListener(this);
        rootView.findViewById(R.id.seeQuestionsButton).setOnClickListener(this);
        rootView.findViewById(R.id.seeStudentsButton).setOnClickListener(this);
        rootView.findViewById(R.id.signOutButton).setOnClickListener(this);
        rootView.findViewById(R.id.addSubjectButton).setOnClickListener(this);
        rootView.findViewById(R.id.professorProfileButton).setOnClickListener(this);
        rootView.findViewById(R.id.seeTestsButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        return rootView;
    }

    // set pt put extra
    public void setup(String extra1, String extra2) {
        this.extra1 = extra1;
        this.extra2 = extra2;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addQuestionButton) {
            // new fragment
            SeeQuestionsFragment myFr = new SeeQuestionsFragment();

            ((ProfessorMenuActivity) getActivity()).replaceWithFragment(myFr);

//            Intent intent = new Intent(this, AddQuestionActivity.class);
//            startActivity(intent);
        }
//        if (v.getId() == R.id.seeQuestionsButton) {
//            Intent intent = new Intent(this, SeeQuestionsActivity.class);
//            startActivity(intent);
//        }
//        if (v.getId() == R.id.seeStudentsButton) {
//            Intent intent = new Intent(this, SeeStudentsActivity.class);
//            startActivity(intent);
//        }
//        if (v.getId() == R.id.signOutButton) {
//            mAuth.signOut();
//            Intent intent = new Intent(this, SignInActivity.class);
//            startActivity(intent);
//        }
//        if (v.getId() == R.id.addSubjectButton) {
//            Intent intent = new Intent(this, AddSubjectActivity.class);
//            startActivity(intent);
//        }
//        if (v.getId() == R.id.professorProfileButton) {
//            Intent intent = new Intent(this, ProfessorProfileActivity.class);
//            startActivity(intent);
//        }
//        if (v.getId() == R.id.seeTestsButton) {
//            Intent intent = new Intent(this, SeeTestsActivity.class);
//            startActivity(intent);
//        }
    }
}
