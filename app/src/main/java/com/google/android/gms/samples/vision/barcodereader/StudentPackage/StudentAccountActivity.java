package com.google.android.gms.samples.vision.barcodereader.StudentPackage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.samples.vision.barcodereader.Classes.AnsweredQuestion;
import com.google.android.gms.samples.vision.barcodereader.Barcode.BarcodeCaptureActivity;
import com.google.android.gms.samples.vision.barcodereader.OldActivities.HistoryActivity;
import com.google.android.gms.samples.vision.barcodereader.R;
import com.google.android.gms.samples.vision.barcodereader.Authentication.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class StudentAccountActivity extends Activity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    List<AnsweredQuestion> answers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_account_activity);

        findViewById(R.id.read_barcode).setOnClickListener(this);
        findViewById(R.id.historyButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.studentProfileButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.signOutButton) {
            mAuth.signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.historyButton) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.studentProfileButton) {
            Intent intent = new Intent(this, StudentProfileActivity.class);
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
