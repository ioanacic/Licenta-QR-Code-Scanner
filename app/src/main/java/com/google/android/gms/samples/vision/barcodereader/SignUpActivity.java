package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class SignUpActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "SignUp";

    private EditText emailField;
    private EditText passwordField;
    private EditText lastNameField;
    private EditText firstNameField;
    private EditText phoneField;
    private EditText groupField;
    private EditText yearOfStudyField;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        emailField = findViewById(R.id.fieldEmail);
        passwordField = findViewById(R.id.fieldPassword);
        lastNameField = findViewById(R.id.fieldLastName);
        firstNameField = findViewById(R.id.fieldFirstName);
        phoneField = findViewById(R.id.fieldPhone);
        groupField = findViewById(R.id.fieldGroup);
        yearOfStudyField = findViewById(R.id.fieldYearOfStudy);

        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    private void singUp() {
        final String lastName;
        final String firstName;
        final String phone;
        final String group;
        final String year;
        final String email;
        final String password;

        if (!validateForm()) {
            return;
        } else {
            lastName = lastNameField.getText().toString().trim();
            firstName = firstNameField.getText().toString().trim();
            phone = phoneField.getText().toString().trim();
            group = groupField.getText().toString().trim();
            year = yearOfStudyField.getText().toString().trim();
            email = emailField.getText().toString().trim();
            password = passwordField.getText().toString().trim();
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(lastName, firstName, phone, group, year, email, password);
                            mDatabase.child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, getString(R.string.createdSuccsessful), Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.w(TAG, "FAILED");
                                    }
                                }
                            });
                            Intent intent = new Intent(SignUpActivity.this, SecondActivity.class);
                            startActivity(intent);
                        } else {
                            try {
                                throw task.getException();
                            }
                            // password with less that 6 characters
                            catch (FirebaseAuthWeakPasswordException weakPassword) {
                                Log.w(TAG, "createUserWithEmail:weakPassword");
                                Toast.makeText(SignUpActivity.this, R.string.weakPassword,
                                        Toast.LENGTH_SHORT).show();
                            }
                            // wrong email format
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                Log.w(TAG, "createUserWithEmail:malformedEmail");
                                Toast.makeText(SignUpActivity.this, R.string.malformedEmail,
                                        Toast.LENGTH_SHORT).show();
                            }
                            // email already exists
                            catch (FirebaseAuthUserCollisionException existEmail) {
                                Log.w(TAG, "createUserWithEmail:existEmail");
                                Toast.makeText(SignUpActivity.this, R.string.existEmail,
                                        Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            }
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String lastName = lastNameField.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            lastNameField.setError("Required.");
            valid = false;
        } else {
            lastNameField.setError(null);
        }

        String firstName = firstNameField.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            firstNameField.setError("Required.");
            valid = false;
        } else {
            firstNameField.setError(null);
        }

        String phone = phoneField.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            phoneField.setError("Required.");
            valid = false;
        } else {
            phoneField.setError(null);
        }

        if (phone.length() < 10) {
            phoneField.setError("Phone number must have 10 characters.");
            valid = false;
        } else {
            phoneField.setError(null);
        }

        String group = groupField.getText().toString();
        if (TextUtils.isEmpty(group)) {
            groupField.setError("Required.");
            valid = false;
        } else {
            groupField.setError(null);
        }

        String year = yearOfStudyField.getText().toString();
        if (TextUtils.isEmpty(year)) {
            yearOfStudyField.setError("Required.");
            valid = false;
        } else {
            yearOfStudyField.setError(null);
        }

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            singUp();
        }
    }
}
