package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class StartWithLogoActivity extends Activity {

    ImageView quizImg;

    String typeOfUser;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_with_logo_activity);

        quizImg = findViewById(R.id.quizImg);

        new CountDownTimer(700, 100){
            public void onTick(long millisUntilFinished){
            }
            public  void onFinish(){
                mAuth = FirebaseAuth.getInstance();
                if (mAuth.getCurrentUser() != null) {
                    getTypeOfUser();
                    return;
                } else {
                    Intent intent = new Intent(StartWithLogoActivity.this, SignInActivity.class);
                    startActivity(intent);
                }
            }
        }.start();
    }

    public void getTypeOfUser() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                for (HashMap.Entry i : map.entrySet()) {
                    if (i.getKey().equals("typeOfUser")) {
                        typeOfUser = i.getValue().toString();
                    }
                }

                Intent intent;
                if (typeOfUser == null) {
                    intent = new Intent(StartWithLogoActivity.this, SignInActivity.class);
                } else if (typeOfUser.equals("S")) {       // is student
                    intent = new Intent(StartWithLogoActivity.this, StudentMenuActivity.class);
                } else {        // is professor
                    intent = new Intent(StartWithLogoActivity.this, ProfessorMenuActivity.class);
                }
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
