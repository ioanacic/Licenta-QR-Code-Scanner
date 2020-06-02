package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class ProfessorAccountActivity extends Activity implements View.OnClickListener {

    FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.professor_account_activity);

        findViewById(R.id.addQuestionButton).setOnClickListener(this);
        findViewById(R.id.seeQuestionsButton).setOnClickListener(this);
        findViewById(R.id.seeStudentsButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.addSubjectButton).setOnClickListener(this);
        findViewById(R.id.professorProfileButton).setOnClickListener(this);
        findViewById(R.id.seeTestsButton).setOnClickListener(this);
        findViewById(R.id.navigBar).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addQuestionButton) {
            Intent intent = new Intent(this, AddQuestionActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.seeQuestionsButton) {
            Intent intent = new Intent(this, SeeQuestionsActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.seeStudentsButton) {
            Intent intent = new Intent(this, SeeStudentsActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.signOutButton) {
            mAuth.signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.addSubjectButton) {
            Intent intent = new Intent(this, AddSubjectActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.professorProfileButton) {
            Intent intent = new Intent(this, ProfessorProfileActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.seeTestsButton) {
            Intent intent = new Intent(this, SeeTestsActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.navigBar) {
            Intent intent = new Intent(this, ProfessorMenuActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
