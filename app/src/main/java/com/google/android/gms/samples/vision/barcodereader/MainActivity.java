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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        // Views
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.goToCamera).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
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
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                            startActivity(intent);
                        } else {
                            try {
                                throw task.getException();
                            }
                            // password with less that 6 characters
                            catch (FirebaseAuthWeakPasswordException weakPassword) {
                                Log.w(TAG, "createUserWithEmail:weakPassword");
                                Toast.makeText(MainActivity.this, R.string.weakPassword,
                                        Toast.LENGTH_SHORT).show();
                            }
                            // wrong email format
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                Log.w(TAG, "createUserWithEmail:malformedEmail");
                                Toast.makeText(MainActivity.this, R.string.malformedEmail,
                                        Toast.LENGTH_SHORT).show();
                            }
                            // email already exists
                            catch (FirebaseAuthUserCollisionException existEmail) {
                                Log.w(TAG, "createUserWithEmail:existEmail");
                                Toast.makeText(MainActivity.this, R.string.existEmail,
                                        Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            }
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
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

    // must get rid of it
    private void updateUI(FirebaseUser user) {
        if (user != null) {

//            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
//            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
//            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);

        } else {

//            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
//            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
//            findViewById(R.id.signedInButtons).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
//            singUp(mEmailField.getText().toString(), mPasswordField.getText().toString());
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        } else if (i == R.id.emailSignInButton) {
            signIn();
        } else if (i == R.id.signOutButton) {
            signOut();
        } else if (i == R.id.goToCamera) {
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        }
    }
}
