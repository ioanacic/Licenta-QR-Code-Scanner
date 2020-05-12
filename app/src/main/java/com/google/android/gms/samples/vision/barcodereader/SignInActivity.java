/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";

    TextInputEditText mEmailField;
    TextInputEditText mPasswordField;

    String typeOfUser;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        // Views
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.createStudentAccountButton).setOnClickListener(this);
        findViewById(R.id.createProfessorAccountButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    private void signIn() {
        final String email = mEmailField.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();

        Log.d(TAG, "signIn:" + email);
        // checking if the email and password fields are not empty
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getTypeOfUser();
                        } else {
                            try {
                                throw task.getException();
                            }
                            // password with less that 6 characters
                            catch (FirebaseAuthWeakPasswordException weakPassword) {
                                Log.w(TAG, "createUserWithEmail:weakPassword");
                                Toast.makeText(SignInActivity.this, R.string.weakPassword,
                                        Toast.LENGTH_SHORT).show();
                            }
                            // wrong email format
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                Log.w(TAG, "createUserWithEmail:malformedEmail");
                                Toast.makeText(SignInActivity.this, malformedEmail.getLocalizedMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            // email already exists
                            catch (FirebaseAuthUserCollisionException existEmail) {
                                Log.w(TAG, "createUserWithEmail:existEmail");
                                Toast.makeText(SignInActivity.this, R.string.existEmail,
                                        Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e) {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, R.string.incorrectCredentials,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
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
                if (typeOfUser.equals("S")) {       // is student
                    intent = new Intent(SignInActivity.this, StudentAccountActivity.class);
                } else {        // is professor
                    intent = new Intent(SignInActivity.this, ProfessorAccountActivity.class);
                }
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createStudentAccountButton) {
            Intent intent = new Intent(this, SignUpStudentActivity.class);
            intent.putExtra("TYPE OF USER", "S");
            startActivity(intent);
        } else if (i == R.id.createProfessorAccountButton) {
            Intent intent = new Intent(this, SignUpProfessorActivity.class);
            intent.putExtra("TYPE OF USER", "P");
            startActivity(intent);
        } else if (i == R.id.emailSignInButton) {
            signIn();
        }
    }
}
