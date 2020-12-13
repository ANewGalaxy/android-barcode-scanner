package com.anewgalaxy.androidbarcodescanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class TestScannerFragment extends ScannerFragment implements View.OnClickListener {

    private TextView resultTextView;
    private Button confirmButton;
    private Button rescanButton;

    private String barcode, barcodeFormat;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_test_scanner, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resultTextView = view.findViewById(R.id.barcode_scanner_result_view);

        confirmButton = view.findViewById(R.id.barcode_scanner_confirm_button);

        rescanButton = view.findViewById(R.id.barcode_scanner_rescan_button);

        confirmButton.setOnClickListener(this);

        rescanButton.setOnClickListener(this);

        setFeedbackButtonsEnabled(false);

        resultTextView.setText(null);

    }

    @Override
    public void onResume() {
        super.onResume();

        // Empty the result view since there will be a new scan
        resultTextView.setText(null);

    }

    @Override
    public void onBarcodeScanned(@NonNull String barcode, @NonNull String barcodeFormat) {

        Log.d(TAG, "Barcode Scanned: [" + barcode + ", " + barcodeFormat + "]");

        resultTextView.setText(barcode);

        this.barcodeFormat = barcodeFormat;

        this.barcode = barcode;

        setFeedbackButtonsEnabled(true);

    }

    @Override
    public void onClick(@NonNull View view) {

        int id = view.getId();

        if (id == R.id.barcode_scanner_confirm_button) {

            // Handle scan confirmation here

            Log.d(TAG, "Scan Confirmed: [" + barcode + ", " + barcodeFormat + "]");

        } else if (id == R.id.barcode_scanner_rescan_button) {

            setFeedbackButtonsEnabled(false);

            resultTextView.setText(null);

            super.resumeScanning();

        }

    }

    private void setFeedbackButtonsEnabled(boolean enabled) {

        confirmButton.setEnabled(enabled);

        rescanButton.setEnabled(enabled);

    }



}
