package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class SecondActivity extends Activity implements View.OnClickListener {

    // use a compound button so either checkbox or switch widgets work.
//    private CompoundButton autoFocus;
//    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        statusMessage = (TextView) findViewById(R.id.status_message);
        barcodeValue = (TextView) findViewById(R.id.barcode_value);

//        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
//        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        findViewById(R.id.read_barcode).setOnClickListener(this);
        findViewById(R.id.generate_qr).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
//            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
//            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

//            startActivityForResult(intent, RC_BARCODE_CAPTURE);
            startActivity(intent);
        }
        if (v.getId() == R.id.generate_qr) {
            Intent intent = new Intent(this, GenerateQRActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.signOutButton) {
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}

