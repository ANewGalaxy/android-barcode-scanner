package com.anewgalaxy.androidbarcodescanner;

/**
 * Copyright (C) 2020 Tyler Sizse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (C) 2012-2018 ZXing authors, Journey Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest;
import android.content.Context;
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
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public abstract class ScannerFragment extends Fragment {

    public static final String TAG = ScannerFragment.class.getSimpleName();

    private final ActivityResultLauncher<String> REQUEST_CAMERA_PERMISSION_LAUNCHER = registerForActivityResult(
            new RequestPermission(), this::onCameraPermissionResult);

    private AndroidBarcodeScannerView barcodeScannerView;
    private boolean scannerPaused = true;
    private long scanDelay = 1500L;

    private String statusPermission;
    private String statusScanning;
    private String statusSuccess;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        statusPermission = getString(R.string.scan_status_permission);

        statusScanning = getString(R.string.scan_status_scanning);

        statusSuccess = getString(R.string.scan_status_success);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_scanner, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barcodeScannerView = view.findViewById(R.id.barcode_scanner_view);

        if (barcodeScannerView == null)

            Log.e(TAG, "Error finding view with the following id: barcode_scanner_view");

    }

    @Override
    public void onResume() {
        super.onResume();

        // If the camera permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Update the status text to inform the guest that camera permission is required
            barcodeScannerView.setStatusText(statusPermission);

            /*// Clear the result text view
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

    public abstract void onBarcodeScanned(@NonNull String barcode, @NonNull String barcodeFormat);

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

            barcodeScannerView.setStatusText(statusScanning);

            barcodeScannerView.resume();

            scannerPaused = false;

            if (scanDelay > 0) {

                Handler handler = new Handler(Looper.myLooper());
                handler.postDelayed(() -> {

                    // As long as the scanner hasn't been paused, start the decoder
                    if (!scannerPaused)

                        // Tells the decoder to stop after a single scan
                        this.decodeSingle();

                }, scanDelay);

            } else

                this.decodeSingle();

        }

    }

    private void decodeSingle() {

        barcodeScannerView.decodeSingle(result -> {

            if (result != null && result.getText() != null) {

                barcodeScannerView.setStatusText(statusSuccess);

                barcodeScannerView.pause();

                scannerPaused = true;

                this.onBarcodeScanned(result.getText(), result.getText());

            } else

                this.decodeSingle();

        });


    }

    public void setScanDelay(long delay) {

        scanDelay = Math.max(0L, delay);

    }

    public void setCustomStatus(String statusPermission, String statusScanning, String statusSuccess) {

        if (statusPermission != null)

            this.statusPermission = statusPermission;

        if (statusScanning != null)

            this.statusScanning = statusScanning;

        if (statusSuccess != null)

            this.statusSuccess = statusSuccess;

    }

}
