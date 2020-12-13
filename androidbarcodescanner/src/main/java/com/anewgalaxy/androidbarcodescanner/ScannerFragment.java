package com.anewgalaxy.androidbarcodescanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;

public class ScannerFragment extends Fragment implements BarcodeCallback, View.OnClickListener {

    public static final String TAG = ScannerFragment.class.getSimpleName();

    private final ActivityResultLauncher<String> REQUEST_CAMERA_PERMISSION_LAUNCHER = registerForActivityResult(
            new RequestPermission(), this::onCameraPermissionResult);

    private AndroidBarcodeScannerView barcodeScannerView;
    private boolean scannerPaused = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_scanner, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barcodeScannerView = view.findViewById(R.id.barcode_scanner_view);

        if (barcodeScannerView == null) {

            Log.e(TAG, "Could not find barcodeScannerView");

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // If the camera permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            /*// Update the status text to inform the guest that camera permission is required
            barcodeScannerView.setStatusText();

            // Clear the result text view
            resultTextView.setText(null);*/

            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

                // TODO Create a dialog window describing why we need the permission for this feature

                // Display a reason of why we need the permission
                Toast.makeText(requireContext(), "Camera permission is needed in order to scan.",
                        Toast.LENGTH_LONG).show();

            } else

                // Request the camera permission
                REQUEST_CAMERA_PERMISSION_LAUNCHER.launch(Manifest.permission.CAMERA);

        } else

            resumeScanning();

    }

    @Override
    public void onPause() {
        super.onPause();

        if (!scannerPaused) {

            // Since we have paused the fragment, pause and wait for the camera to close
            barcodeScannerView.pauseAndWait();

            scannerPaused = true;

        }

    }

    @Override
    public void onDestroy() {

        // Make sure we have the view in-case the view isn't initialized before destruction
        if (barcodeScannerView != null && !scannerPaused) {

            // Since we are destroying the fragment, pause and wait for the camera to close
            barcodeScannerView.pauseAndWait();

            scannerPaused = true;

        }

        super.onDestroy();

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void barcodeResult(BarcodeResult result) {

        barcodeScannerView.pause();

        scannerPaused = true;

    }

    private void onCameraPermissionResult(boolean isGranted) {

        if (!isGranted) {

            // Display a reason of why we need the permission
            Toast.makeText(requireContext(), "Camera permission is needed in order to scan.",
                    Toast.LENGTH_LONG).show();

        } else

            // Camera permission is granted, so resume scanning
            resumeScanning();

    }

    public void resumeScanning() {

        if (barcodeScannerView != null && scannerPaused) {

            barcodeScannerView.resume();

            scannerPaused = false;

            Handler handler = new Handler(Looper.myLooper());
            handler.postDelayed(() -> {

                // As long as the scanner hasn't been paused, start the decoder
                if (!scannerPaused)

                    // Tells the decoder to stop after a single scan
                    barcodeScannerView.decodeSingle(this);

            }, 1500L);

        }

    }

}
