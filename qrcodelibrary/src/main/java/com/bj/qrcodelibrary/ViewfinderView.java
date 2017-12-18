/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bj.qrcodelibrary;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.bj.qrcodelibrary.camera.CameraManager;
import com.bj.qrcodelibrary.util.LightSensor;
import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View implements ScaleGestureDetector.OnScaleGestureListener,
        View.OnTouchListener{

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 80L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;

    private CameraManager cameraManager;
    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int laserColor;
    private final int resultPointColor;
    private int scannerAlpha;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;
    private Rect tmpRect;
    private Bitmap tmpBitmap;
    private float cornerWidth;//方角宽度
    private float cornerLength;//方角长度
    private int mStrokeWidth;//内圈寬度
    private int corner_rect_length;//内圈颜色
    private int cornerColor;//角度颜色
    private String description;//描述文字
    private int textColor;
    private boolean textVisible;
    private float textMargin;//描述文本距离上面的距离
    private float text_size;//描述文本字体大小
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private boolean zoomMaxFlag = true;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        resultColor = resources.getColor(R.color.result_view);
        laserColor = resources.getColor(R.color.viewfinder_laser);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<>(5);
        lastPossibleResultPoints = null;
        tmpRect = new Rect();
        tmpBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.scan);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewfinderView);
        cornerWidth = a.getDimensionPixelSize(R.styleable.ViewfinderView_corner_width, dip2px(context, 4));
        cornerLength = a.getDimensionPixelSize(R.styleable.ViewfinderView_corner_length, dip2px(context, 20));
        mStrokeWidth = a.getDimensionPixelSize(R.styleable.ViewfinderView_corner_rect_length, dip2px(context, 1));
        cornerColor = a.getColor(R.styleable.ViewfinderView_corner_color, Color.parseColor("#62e203"));
        corner_rect_length = a.getColor(R.styleable.ViewfinderView_corner_rect_color, Color.parseColor("#66ffffff"));
        maskColor = a.getColor(R.styleable.ViewfinderView_mask_color, resources.getColor(R.color.viewfinder_mask));
        description = a.getString(R.styleable.ViewfinderView_text_description);
        if (TextUtils.isEmpty(description)) {
            description = "将二维码/条码放入框内,即可自动扫描";
        }
        textColor = a.getColor(R.styleable.ViewfinderView_text_color, Color.parseColor("#66ffffff"));
        textVisible = a.getBoolean(R.styleable.ViewfinderView_text_visible, true);
        textMargin = a.getDimensionPixelSize(R.styleable.ViewfinderView_text_margin, dip2px(context, 20));
        text_size = a.getDimensionPixelSize(R.styleable.ViewfinderView_text_size, sp2px(context, 14));
        a.recycle();
        setOnTouchListener(this);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (zoomMaxFlag) {
                    if (mCameraZoomListener != null) {
                        mCameraZoomListener.onZooming(true, false, true, 1);
                    }
                    zoomMaxFlag = false;
                } else {
                    if (mCameraZoomListener != null) {
                        mCameraZoomListener.onZooming(true, false, false, 1);
                    }
                    zoomMaxFlag = true;
                }
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
//                if (mCameraZoomListener != null) {
//                    //判断点击事件是否在
//                    mCameraZoomListener.onZooming(false, true, false, 1);
//                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
//                if (mCameraZoomListener != null) {
//                    //判断点击事件是否在
//                    mCameraZoomListener.onZooming(false, true, false, 1);
//                }
            }
        });
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }
        Rect frame = cameraManager.getFramingRect();
        Rect previewFrame = cameraManager.getFramingRectInPreview();
        if (frame == null || previewFrame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);
        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {

            // Draw a red "laser scanner" line through the middle to show decoding is active
            paint.setColor(laserColor);
//            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
//            int middle = frame.height() / 2 + frame.top;
//            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);//移除红线

            float scaleX = frame.width() / (float) previewFrame.width();
            float scaleY = frame.height() / (float) previewFrame.height();

            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;
            int frameLeft = frame.left;
            int frameTop = frame.top;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                synchronized (currentPossible) {
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                                frameTop + (int) (point.getY() * scaleY),
                                POINT_SIZE, paint);
                    }
                }
            }
            if (currentLast != null) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                synchronized (currentLast) {
                    float radius = POINT_SIZE / 2.0f;
                    for (ResultPoint point : currentLast) {
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                                frameTop + (int) (point.getY() * scaleY),
                                radius, paint);
                    }
                }
            }
            drawRectCorner(frame, canvas);
            drawBitmap(frame, canvas);
            drawBottomText(frame, canvas);


            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
//            postInvalidateDelayed(ANIMATION_DELAY,
//                    frame.left - POINT_SIZE,
//                    frame.top - POINT_SIZE,
//                    frame.right + POINT_SIZE,
//                    frame.bottom + POINT_SIZE);
        }
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }

    private ValueAnimator animator;
    private float x;
    private boolean flag = true;

    /*画四个角,如果通过绘制path的方式会卡*/
    private void drawRectCorner(Rect frame, Canvas canvas) {
        //画内部rect
        paint.setStrokeWidth(mStrokeWidth);
        paint.setColor(corner_rect_length);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(frame, paint);
        paint.reset();
        //画四角
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(cornerColor);
        //左上角
        canvas.drawRect(frame.left, frame.top, frame.left + cornerLength, frame.top + cornerWidth, paint);
        canvas.drawRect(frame.left, frame.top, frame.left + cornerWidth, frame.top + cornerLength, paint);
        //右上角
        canvas.drawRect(frame.right - cornerLength, frame.top, frame.right, frame.top + cornerWidth, paint);
        canvas.drawRect(frame.right - cornerWidth, frame.top, frame.right, frame.top + cornerLength, paint);
        //左下角
        canvas.drawRect(frame.left, frame.bottom - cornerLength, frame.left + cornerWidth, frame.bottom, paint);
        canvas.drawRect(frame.left, frame.bottom - cornerWidth, frame.left + cornerLength, frame.bottom, paint);
        //右下角
        canvas.drawRect(frame.right - cornerLength, frame.bottom - cornerWidth, frame.right, frame.bottom, paint);
        canvas.drawRect(frame.right - cornerWidth, frame.bottom - cornerLength, frame.right, frame.bottom, paint);
        paint.reset();
    }

    /*画扫描线*/
    private void drawBitmap(final Rect frame, Canvas canvas) {
        if (animator == null) {
            animator = ValueAnimator.ofFloat(frame.top, frame.bottom);
            animator.setDuration(3000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    x = (Float) animation.getAnimatedValue();
                    postInvalidate(
                            frame.left - POINT_SIZE,
                            frame.top - POINT_SIZE,
                            frame.right + POINT_SIZE,
                            frame.bottom + POINT_SIZE);
                }
            });
        }

        if (flag) {
            animator.start();
            flag = false;
        }
        tmpRect.set(frame.left, (int) x, frame.right, (int) x + 40);
        canvas.drawBitmap(tmpBitmap, null, tmpRect, paint);
    }

    private Rect tmpRect1 = new Rect();

    private void drawBottomText(Rect frame, Canvas canvas) {
        if (textVisible) {
            paint.setTextSize(text_size);
            paint.setColor(textColor);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.getTextBounds(description, 0, description.length(), tmpRect1);
            canvas.drawText(description, 0, description.length(), frame.centerX(),
                    frame.centerY() + frame.height() / 2 + textMargin + tmpRect1.height(), paint);
            paint.reset();
        }
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (mCameraZoomListener != null) {
            mCameraZoomListener.onZooming(false, false, false, detector.getScaleFactor());
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    public interface CameraZoomListener {
        /**
         * @param isDouble   是否双击
         * @param isMax      //是否移动到最大
         * @param scaleValue //缩小放大的值
         * @param isSingle   //是否单击
         */
        void onZooming(boolean isDouble, boolean isSingle, boolean isMax, float scaleValue);
    }

    private CameraZoomListener mCameraZoomListener;

    public void setCameraZoomListener(CameraZoomListener mCameraZoomListener) {
        this.mCameraZoomListener = mCameraZoomListener;
    }
}
