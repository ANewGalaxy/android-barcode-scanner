package com.anewgalaxy.scannerexample;

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
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anewgalaxy.androidbarcodescanner.AndroidBarcodeScannerView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ScannerFragment --
 * Abstract Fragment class that handles the scanning of bar-codes.
 * @author Tyler Sizse
 */
public abstract class AbstractScannerFragment extends Fragment implements BarcodeCallback {

    public static final String TAG = AbstractScannerFragment.class.getSimpleName();

    // Used to ask for camera permission. Calls onCameraPermissionResult method with the result
    private final ActivityResultLauncher<String> REQUEST_CAMERA_PERMISSION_LAUNCHER =
            registerForActivityResult(new RequestPermission(), this::onCameraPermissionResult);

    // Delay the decoder on resume for 1.5 Seconds in milliseconds
    private static final long DECODER_DELAY = 1500L;

    private AndroidBarcodeScannerView barcodeScannerView;
    private BeepManager beepManager;
    private TextView resultTextView;
    private Button confirmButton;
    private Button rescanButton;

    private boolean scannerPaused = true;

    private BarcodeFormat barcodeFormat;
    private String barcodeText;


    ////////////// Abstract Methods Start //////////////

    protected abstract void onBarcodeConfirmed(@NonNull String barcode, @NonNull BarcodeFormat format);


    ////////////// Lifecycle Methods Start //////////////

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanner, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get respective views from layout
        barcodeScannerView = view.findViewById(R.id.scanner_android_barcode_scanner_view);

        resultTextView = view.findViewById(R.id.scanner_result_view);

        confirmButton = view.findViewById(R.id.scanner_confirm_button);

        rescanButton = view.findViewById(R.id.scanner_rescan_button);


        // Set the onClick listeners to call the respective method onClick
        confirmButton.setOnClickListener(v -> onBarcodeConfirmed(barcodeText, barcodeFormat));

        rescanButton.setOnClickListener(v -> resumeScanning());

        // Disable the feedback buttons until we scan a barcode
        setFeedbackButtonsEnabled(false);


        // Create new BeepManager object to handle beeps and vibration
        beepManager = new BeepManager(requireActivity());

        beepManager.setVibrateEnabled(true);

        beepManager.setBeepEnabled(true);

    }

    @Override
    public void onResume() {
        super.onResume();

        // If the camera permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Update the status text to inform the guest that camera permission is required
            barcodeScannerView.setStatusText(getString(R.string.scanner_camera_permission_required));

            // Clear the result text view
            resultTextView.setText(null);

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


    ////////////// Other Event Methods Start  //////////////

    @Override
    public final void barcodeResult(BarcodeResult result) {

        // Gets the barcode from the result
        String resultText = result.getText();

        // Make sure we actually have a barcode scanned
        if (resultText != null) {

            // Pause the scanner
            barcodeScannerView.pause();

            scannerPaused = true;

            // Play a sound and vibrate when a scan has been processed
            beepManager.playBeepSoundAndVibrate();

            // Tell the user to confirm that the barcode is correct
            barcodeScannerView.setStatusText(getString(R.string.scanner_confirm_msg));

            // Display the barcode back to the user
            resultTextView.setText(resultText);

            // Store the barcode format
            barcodeFormat = result.getBarcodeFormat();

            // Store the barcode
            barcodeText = resultText;

            // Enable the feedback buttons after we have stored the bar-code and stopped scanner
            setFeedbackButtonsEnabled(true);

        } else

            // Scan for another bar-code
            barcodeScannerView.decodeSingle(AbstractScannerFragment.this);

    }

    @Override // Made this method final so it can't be overridden
    public final void possibleResultPoints(List<ResultPoint> resultPoints) { }


    ////////////// Custom Methods Start  //////////////

    /**
     * onCameraPermissionResult -- Takes 1 parameter.
     * This method gets called by the REQUEST_CAMERA_PERMISSION_LAUNCHER, after
     * asking for camera permission. Determines what happens when the permission gets granted or
     * denied.
     * @param isGranted - true if permission was granted false otherwise
     */
    private void onCameraPermissionResult(boolean isGranted) {

        if (!isGranted) {

            // TODO Create dialog that informs the user why permission is needed

            // Display a reason of why we need the permission
            Toast.makeText(requireContext(), "Camera permission is needed in order to scan.",
                    Toast.LENGTH_LONG).show();

        } else

            // Camera permission is granted, so resume scanning
            resumeScanning();

    }

    /**
     * resumeScanning -- Takes 0 parameters.
     * Resumes the scanner if it is not paused, resets resultTextView text,
     * resets the barcodeResult to be null so we can scan a new bar-code, and starts the decoder
     * after a delay of SCAN_DELAY.
     */
    private void resumeScanning() {

        if (scannerPaused) {

            // Update the status text to explain how to use the scanner
            barcodeScannerView.setStatusText(getString(R.string.zxing_msg_default_status));

            // Update the display text so the user knows we are waiting for them to scan a barcode
            resultTextView.setText(getString(R.string.scanner_waiting_for_scan));

            // Disable the feedback buttons until we scan another barcode
            setFeedbackButtonsEnabled(false);

            // Reset our barcodeText and format
            barcodeText = null;

            barcodeFormat = null;

            // Resume the scanner but not the decoder
            barcodeScannerView.resume();

            scannerPaused = false;

            // Create a handler that resumes the decoder after a delay
            // Gives the user time to move their camera before scanning
            new Handler(Looper.myLooper()).postDelayed(() -> {

                // As long as the scanner hasn't been paused, start the decoder
                if (!scannerPaused)

                    // Tells the decoder to stop after a single scan
                    barcodeScannerView.decodeSingle(AbstractScannerFragment.this);

            }, DECODER_DELAY);

        }

    }

    /**
     * setFeedbackButtonsEnabled -- Takes 1 parameter.
     * Toggles whether both rescanButton and confirmScan button are enabled or
     * disabled, based on the value of the parameter.
     *
     * @param enabled true to enable or false to disable
     */
    private void setFeedbackButtonsEnabled(boolean enabled) {

        confirmButton.setEnabled(enabled);

        rescanButton.setEnabled(enabled);

    }

    /**
     * setDecoderFormats --
     * Sets what formats the decoder should decode.
     *
     * @param barcodeFormats The barcode formats to decode. If no formats are provided then the
     *                       scanner will default to scanning all bar-code formats.
     * @throws NullPointerException If the array of formats contains a null value
     */
    protected final void setDecoderFormats(@NonNull BarcodeFormat...barcodeFormats) {

        if (barcodeFormats.length > 0) {

            List<BarcodeFormat> formatList = new ArrayList<>(barcodeFormats.length);

            Collections.addAll(formatList, barcodeFormats);

            if (formatList.contains(null))

                throw new NullPointerException("Cannot set decode format to a null BarcodeFormat");

            // Apply all the decoder formats
            barcodeScannerView.setDecoderFactory(new DefaultDecoderFactory(formatList));

        } else

            barcodeScannerView.setDecoderFactory(new DefaultDecoderFactory());

    }

}
