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
class BarcodeFrameView extends View {

    public static final String TAG = BarcodeFrameView.class.getSimpleName();

    private final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int frameCornerSize;
    private int frameOffset;
    private int frameStyle;

    private CameraPreview cameraPreview;

    public BarcodeFrameView(@NonNull Context context) {
        super(context);

        initialize(context, null);

    }

    public BarcodeFrameView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initialize(context, attrs);

    }

    public void initialize(@NonNull Context context, AttributeSet attrs) {

        if (attrs != null) {

            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.CustomScannerView, 0, 0);

            try {

                PAINT.setStrokeWidth(a.getInteger(R.styleable.CustomScannerView_scanFrameThickness, 10));

                PAINT.setColor(a.getColor(R.styleable.CustomScannerView_scanFrameColor, 0xFFFFFF));

                PAINT.setAlpha((int) (0xFF * a.getFloat(R.styleable.CustomScannerView_scanFrameAlpha, 1f)));

                frameCornerSize = a.getDimensionPixelSize(R.styleable.CustomScannerView_scanFrameCornerSize, 48);

                frameOffset = a.getDimensionPixelSize(R.styleable.CustomScannerView_scanFrameOffset, 10);

                frameStyle = a.getInteger(R.styleable.CustomScannerView_scanFrameStyle, 2);

            } catch (RuntimeException exception) {

                Log.e(TAG, exception.getMessage());

                exception.printStackTrace();

            } finally { a.recycle(); }

        } else {

            PAINT.setStrokeWidth(10);

            PAINT.setColor(0xFFFFFF);

            PAINT.setAlpha(0xFF);

            frameCornerSize = 48;

            frameStyle = 2;

        }

    }

    public void setCameraPreview(CameraPreview cameraPreview) {

        cameraPreview.addStateListener(AndroidBarcodeScannerView.createStateListener(this));

        this.cameraPreview = cameraPreview;

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (frameStyle != 0) {

            if (cameraPreview != null) {

                Rect framingRect = cameraPreview.getFramingRect();

                if (framingRect != null) {

                    if (frameStyle == 2) {

                        int offset = (int) PAINT.getStrokeWidth() / 2;

                        int x1 = framingRect.left - frameOffset;
                        int y1 = framingRect.top - frameOffset;
                        int x2 = framingRect.right + frameOffset;
                        int y2 = framingRect.bottom + frameOffset;

                        int x1_offset = x1 - offset;
                        int y1_offset = y1 - offset;
                        int x2_offset = x2 + offset;
                        int y2_offset = y2 + offset;

                        // Top-Left Corner
                        canvas.drawLine(x1_offset, y1, x1_offset + frameCornerSize, y1, PAINT);
                        canvas.drawLine(x1, y1_offset, x1, y1_offset + frameCornerSize, PAINT);

                        // Top-Right Corner
                        canvas.drawLine(x2_offset - frameCornerSize, y1, x2_offset, y1, PAINT);
                        canvas.drawLine(x2, y1_offset, x2, y1_offset + frameCornerSize, PAINT);

                        // Bottom-Right Corner
                        canvas.drawLine(x2_offset, y2, x2_offset - frameCornerSize, y2, PAINT);
                        canvas.drawLine(x2, y2_offset - frameCornerSize, x2, y2_offset, PAINT);

                        // Bottom-Left Corner
                        canvas.drawLine(x1_offset, y2, x1_offset + frameCornerSize, y2, PAINT);
                        canvas.drawLine(x1, y2_offset - frameCornerSize, x1, y2_offset, PAINT);

                    } else

                        // Draw the frame as a rectangle
                        canvas.drawRect(framingRect, PAINT);

                }

            }

        }

    }

}
