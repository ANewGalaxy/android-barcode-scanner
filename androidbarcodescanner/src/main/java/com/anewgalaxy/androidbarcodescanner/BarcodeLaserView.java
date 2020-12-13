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

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.CameraPreview;
import com.journeyapps.barcodescanner.Size;

import java.util.Stack;

/**
 * @author Tyler Sizse
 */
class BarcodeLaserView extends View {

    public static final String TAG = BarcodeLaserView.class.getSimpleName();

    private static final int[] LASER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ALPHA_ANIMATION_DELAY = 60L;
    private static final int MAX_RESULT_POINTS = 10;
    private static final int POINT_OPACITY = 0xA0;
    private static int alphaIndex = 0;

    private final Paint PAINT_RP = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Stack<ResultPoint> currResultPoints = new Stack<>();
    private Stack<ResultPoint> prevResultPoints = new Stack<>();

    private int pointRadius = 12;

    private CameraPreview cameraPreview;

    public BarcodeLaserView(@NonNull Context context) {
        super(context);

        initialize(context, null);

    }

    public BarcodeLaserView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initialize(context, attrs);

    }

    public void initialize(@NonNull Context context, AttributeSet attrs) {

        if (attrs != null) {

            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.CustomScannerView, 0, 0);

            try {
                
                PAINT_RP.setColor(a.getColor(R.styleable.CustomScannerView_scanResultPointsColor, 0xFFFFFF));

                PAINT.setColor(a.getColor(R.styleable.CustomScannerView_scanLaserColor, 0xFF0000));

                PAINT.setStrokeWidth(a.getInteger(R.styleable.CustomScannerView_scanLaserThickness, 4));

                pointRadius = a.getInteger(R.styleable.CustomScannerView_scanResultPointsRadius, 12);

            } catch (RuntimeException exception) {

                Log.e(TAG, exception.getMessage());

                exception.printStackTrace();

            } finally { a.recycle(); }

        } else {

            PAINT.setColor(0xFFFFFF);

            PAINT.setAlpha(0x60);

        }

        PAINT_RP.setStyle(Paint.Style.FILL);

        PAINT.setStyle(Paint.Style.STROKE);

    }

    public void setCameraPreview(CameraPreview cameraPreview) {

        cameraPreview.addStateListener(AndroidBarcodeScannerView.createStateListener(this));

        this.cameraPreview = cameraPreview;

    }

    protected void addResultPoint(ResultPoint resultPoint) {

        if (currResultPoints.size() < MAX_RESULT_POINTS)

            currResultPoints.add(resultPoint);

    }

    private boolean layoutSet;

    @Override
    public void onDraw(Canvas canvas) {

        if (cameraPreview != null) {

            Rect fRect = cameraPreview.getFramingRect();

            if (fRect != null) {

                PAINT.setAlpha(LASER_ALPHA[alphaIndex]);

                alphaIndex = (alphaIndex + 1) % LASER_ALPHA.length;

                // Update out layout to be the same as the framing rect
                layout(fRect.left - pointRadius, fRect.top, fRect.right + pointRadius, fRect.bottom);

                // DRAW LASER

                final int FRAME_CENTER_Y = (getHeight() / 2);

                canvas.drawLine(pointRadius, FRAME_CENTER_Y, getWidth() - pointRadius, FRAME_CENTER_Y, PAINT);

            }

            // DRAW RESULT POINTS

            Size pSize = cameraPreview.getPreviewSize();

            if (pSize != null) {

                final float SCALE_X = getWidth() / (float) pSize.width;

                final float SCALE_Y = getHeight() / (float) pSize.height;

                // Draw the previous result points
                if (!prevResultPoints.isEmpty()) {

                    PAINT_RP.setAlpha(POINT_OPACITY / 2);

                    final int PREV_POINT_RADIUS = (pointRadius / 2);

                    for (ResultPoint point; !prevResultPoints.isEmpty(); ) {

                        point = prevResultPoints.pop();

                        canvas.drawCircle((int) (point.getX() * SCALE_X),
                                (int) (point.getY() * SCALE_Y), PREV_POINT_RADIUS, PAINT_RP);

                    }

                }

                // Draw the previous result points
                if (!currResultPoints.isEmpty()) {

                    PAINT_RP.setAlpha(POINT_OPACITY);

                    for (final ResultPoint POINT : currResultPoints)

                        canvas.drawCircle((int) (POINT.getX() * SCALE_X),
                                (int) (POINT.getY() * SCALE_Y), pointRadius, PAINT_RP);

                    // Swap the list of current result points with the empty list of previous result points
                    final Stack<ResultPoint> temp = currResultPoints;

                    currResultPoints = prevResultPoints;

                    prevResultPoints = temp;

                }

            }

            // Request another draw at the animation interval
            this.postInvalidateDelayed(ALPHA_ANIMATION_DELAY);

        }

    }

}
