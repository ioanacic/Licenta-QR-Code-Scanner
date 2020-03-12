package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import static android.content.ContentValues.TAG;

public class DecodedQRActivity extends Activity {
    private TextView decodedMessage;

    private static final int RC_BARCODE_CAPTURE = 9001;
    public static final String BarcodeObject = "Barcode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decoded_qr);

        decodedMessage = (TextView)findViewById(R.id.decoded_qr_text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String barcodeValue = getIntent().getStringExtra(BarcodeObject);
        decodedMessage.setText(barcodeValue);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == RC_BARCODE_CAPTURE) {
//            if (resultCode == CommonStatusCodes.SUCCESS) {
//                if (data != null) {
//                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
//                    decodedMessage.setText(barcode.displayValue);
//                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
//                } else {
//                    Log.d(TAG, "No barcode captured, intent data is null");
//                }
//            } else {
//                Log.d(TAG, "Failure");
//            }
//        }
//        else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
}
