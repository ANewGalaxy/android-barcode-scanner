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

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.CameraPreview;
import com.journeyapps.barcodescanner.DecoderFactory;
import com.journeyapps.barcodescanner.Size;
import com.journeyapps.barcodescanner.camera.CameraParametersCallback;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import java.util.List;

/**
 * @author Tyler Sizse
 */
public class AndroidBarcodeScannerView extends FrameLayout implements BarcodeCallback {

    public static final String TAG = AndroidBarcodeScannerView.class.getSimpleName();

    private BarcodeFrameView barcodeFrameView;
    private BarcodeLaserView barcodeLaserView;
    private BarcodeMaskView barcodeMaskView;
    private TextView barcodeStatusView;
    private BarcodeView barcodeView;

    private BarcodeCallback delegate;

    public AndroidBarcodeScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.android_barcode_scanner_view, this);

        barcodeView = new BarcodeView(context);

        addView(barcodeView, 0);

        barcodeFrameView = (BarcodeFrameView) findAndValidateView(R.id.barcode_frame_view, "R.id.barcode_frame_view");

        barcodeLaserView = (BarcodeLaserView) findAndValidateView(R.id.barcode_laser_view, "R.id.barcode_laser_view");

        barcodeMaskView = (BarcodeMaskView) findAndValidateView(R.id.barcode_mask_view, "R.id.barcode_mask_view");

        barcodeStatusView = (TextView) findAndValidateView(R.id.barcode_status_view, "R.id.barcode_status_view");

        //barcodeView = (BarcodeView) findAndValidateView(R.id.barcode_surface_view, "R.id.barcode_surface_view");

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomScannerView);

        try {

            int w = a.getDimensionPixelSize(R.styleable.CustomScannerView_scanFrameWidth, 900);

            int h = a.getDimensionPixelSize(R.styleable.CustomScannerView_scanFrameHeight, 600);

            barcodeView.setFramingRectSize(new Size(w, h));

        } catch (RuntimeException exception) {

            Log.e(TAG, exception.getMessage());

            exception.printStackTrace();

        } finally { a.recycle(); }

        barcodeFrameView.setCameraPreview(barcodeView);

        barcodeMaskView.setCameraPreview(barcodeView);

        barcodeLaserView.setCameraPreview(barcodeView);

        barcodeFrameView.initialize(context, attrs);

        barcodeLaserView.initialize(context, attrs);

        barcodeMaskView.initialize(context, attrs);

    }

    private View findAndValidateView(int resID, String id) {

        View view = findViewById(resID);

        if (view == null)

            throw new IllegalArgumentException("Could not find " + view.getClass().getSimpleName() + "with the id: " + id);

        return view;

    }

    public void changeCameraParameters(CameraParametersCallback callback) {

        barcodeView.changeCameraParameters(callback);

    }

    public void setCameraSettings(CameraSettings cameraSettings) {

        barcodeView.setCameraSettings(cameraSettings);

    }

    public void setDecoderFactory(DecoderFactory decoderFactory) {

        barcodeView.setDecoderFactory(decoderFactory);

    }

    public CameraSettings getCameraSettings() {

        return barcodeView.getCameraSettings();

    }

    public DecoderFactory getDecoderFactory() {

        return barcodeView.getDecoderFactory();

    }

    public void setStatusText(String text) {

        barcodeStatusView.setText(text);

    }

    public void resume() {
        barcodeView.resume();
    }

    public void pause() {
        barcodeView.pause();
    }

    public void pauseAndWait() {
        barcodeView.pauseAndWait();
    }

    public void decodeSingle(BarcodeCallback callback) {

        delegate = callback;

        barcodeView.decodeSingle(this);

    }

    public void decodeContinuous(BarcodeCallback callback) {

        delegate = callback;

        barcodeView.decodeContinuous(this);

    }

    @Override
    public void barcodeResult(BarcodeResult result) {

        delegate.barcodeResult(result);

    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {

        for (ResultPoint resultPoint : resultPoints)

            barcodeLaserView.addResultPoint(resultPoint);

        delegate.possibleResultPoints(resultPoints);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_FOCUS:
            case KeyEvent.KEYCODE_CAMERA:
                // Handle these events so they don't launch the Camera app
                return true;

            // Use volume up/down to turn on light
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                barcodeView.setTorch(false);
                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:
                barcodeView.setTorch(true);
                return true;

            default:
                return super.onKeyDown(keyCode, event);

        }

    }

    protected static CameraPreview.StateListener createStateListener(@NonNull View view) {

        return new CameraPreview.StateListener() {

            @Override
            public void previewSized() { view.invalidate(); }

            @Override
            public void previewStarted() { }

            @Override
            public void previewStopped() { }

            @Override
            public void cameraError(Exception error) { }

            @Override
            public void cameraClosed() { }

        };

    }
}
