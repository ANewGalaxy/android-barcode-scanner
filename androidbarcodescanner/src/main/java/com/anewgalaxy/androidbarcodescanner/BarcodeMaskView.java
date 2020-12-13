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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.journeyapps.barcodescanner.CameraPreview;

/**
 * @author Tyler Sizse
 */
class BarcodeMaskView extends View {

    public static final String TAG = BarcodeMaskView.class.getSimpleName();

    private final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);

    private CameraPreview cameraPreview;

    public BarcodeMaskView(@NonNull Context context) {
        super(context);

        initialize(context, null);

    }

    public BarcodeMaskView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initialize(context, attrs);

    }

    public void initialize(@NonNull Context context, AttributeSet attrs) {

        if (attrs != null) {

            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.AndroidBarcodeScannerView, 0, 0);

            try {

                PAINT.setColor(a.getColor(R.styleable.AndroidBarcodeScannerView_scanMaskColor, 0x000000));

                PAINT.setAlpha((int) (0xFF * a.getFloat(R.styleable.AndroidBarcodeScannerView_scanMaskAlpha, 0.375f)));

            } catch (RuntimeException exception) {

                Log.e(TAG, exception.getMessage());

                exception.printStackTrace();

            } finally { a.recycle(); }

        } else {

            PAINT.setColor(0x000000);

            PAINT.setAlpha(0x60);

        }

    }

    public void setCameraPreview(CameraPreview cameraPreview) {

        cameraPreview.addStateListener(AndroidBarcodeScannerView.createStateListener(this));

        this.cameraPreview = cameraPreview;

    }

    @Override
    public void onDraw(Canvas canvas) {

        if (cameraPreview != null) {

            Rect framingRect = cameraPreview.getFramingRect();

            if (framingRect != null) {

                final int VIEW_W = getWidth();
                final int VIEW_H = getHeight();

                final int FRAME_X1 = framingRect.left;
                final int FRAME_X2 = framingRect.right;
                final int FRAME_Y1 = framingRect.top;
                final int FRAME_Y2 = framingRect.bottom;

                // Draw the scanner mask (Area outside of frame)
                canvas.drawRect(0, 0, VIEW_W, FRAME_Y1, PAINT);
                canvas.drawRect(0, FRAME_Y2 + 1, VIEW_W, VIEW_H, PAINT);
                canvas.drawRect(0, FRAME_Y1, FRAME_X1, FRAME_Y2 + 1, PAINT);
                canvas.drawRect(FRAME_X2 + 1, FRAME_Y1, VIEW_W, FRAME_Y2 + 1, PAINT);

            }

        }

    }

}
