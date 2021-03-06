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

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;

public class ScannerFragment extends AbstractScannerFragment {

    public static final String TAG = ScannerFragment.class.getSimpleName();

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.setDecoderFormats(BarcodeFormat.CODE_39, BarcodeFormat.UPC_A);

    }

    @Override
    protected void onBarcodeConfirmed(@NonNull String barcode, @NonNull BarcodeFormat format) {

        Log.d(TAG, "Barcode Confirmed: [" + barcode + ", " + format.toString() + "]");

    }

}
