package com.example.whiteboard.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import java.util.Stack;

public class BrushDrawingView2 extends View {

    static final float DEFAULT_BRUSH_SIZE = 5.0f;
    static final float DEFAULT_ERASER_SIZE = 50.0f;
    static final int DEFAULT_OPACITY = 255;

    private float mBrushSize = DEFAULT_BRUSH_SIZE;
    private float mBrushEraserSize = DEFAULT_ERASER_SIZE;
    private int mOpacity = DEFAULT_OPACITY;

    private final Stack<LinePath> mDrawnPaths = new Stack<>();
    private final Stack<LinePath> networkDrawnPaths = new Stack<>();
    private Canvas mDrawCanvas;
    private boolean mBrushDrawMode;
    private boolean allowDraw = false;

    private Path mPath;
    private Path networkPath;
    private Paint mDrawPaint;
    private Paint networkDrawPaint;
    private Xfermode clearXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private Xfermode overXfermode =  new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);

    private Bitmap bitmap;

    private float mTouchX, mTouchY;
    private static final float TOUCH_TOLERANCE = 4;

    private BrushViewChangeListener mBrushViewChangeListener;
    private OnDrawingListener drawingListener;

    public BrushDrawingView2(Context context) {
        this(context, null);
    }

    public BrushDrawingView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrushDrawingView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupBrushDrawing();
    }

    private void setupBrushDrawing() {
        //Caution: This line is to disable hardware acceleration to make toolbar_eraser feature work properly
        setLayerType(LAYER_TYPE_HARDWARE, null);
        setupPathAndPaint();
    }

    private void setupPathAndPaint() {

        setLayerType(LAYER_TYPE_HARDWARE,null);

        mPath = new Path();
        networkPath = new Path();
        mDrawPaint = new Paint();
        networkDrawPaint = new Paint();

        mDrawPaint.setColor(Color.BLACK);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setDither(true);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawPaint.setStrokeWidth(mBrushSize);
        mDrawPaint.setAlpha(mOpacity);
        mDrawPaint.setXfermode(overXfermode);

        networkDrawPaint.setColor(Color.BLACK);
        networkDrawPaint.setAntiAlias(true);
        networkDrawPaint.setDither(true);
        networkDrawPaint.setStyle(Paint.Style.STROKE);
        networkDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        networkDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        networkDrawPaint.setStrokeWidth(mBrushSize);
        networkDrawPaint.setAlpha(mOpacity);
        networkDrawPaint.setXfermode(overXfermode);

    }

    void brushEraser() {
        mDrawPaint.setStrokeWidth(mBrushEraserSize);
        mDrawPaint.setXfermode(clearXfermode);
    }

    void reset()
    {
        mDrawPaint.setStrokeWidth(mBrushSize);
        mDrawPaint.setXfermode(overXfermode);
    }

    void setBrushSize(float size) {
        mBrushSize = size;
        mDrawPaint.setStrokeWidth(size);
    }

    void setNetworkBrushSize(float size) {
        networkDrawPaint.setStrokeWidth(size);
    }

    void setBrushColor(@ColorInt int color) {
        mDrawPaint.setColor(color);
    }

    void setNetworkBrushColor(@ColorInt int color) {
        networkDrawPaint.setColor(color);

    }

    void setBrushEraserSize(float brushEraserSize) {
        this.mBrushEraserSize = brushEraserSize;
    }

    void setBrushEraserColor(@ColorInt int color) {
        mDrawPaint.setColor(color);
    }

    public void setAllowDraw(boolean value) {
        allowDraw = value;
    }

    float getEraserSize() {
        return mBrushEraserSize;
    }

    float getBrushSize() {
        return mBrushSize;
    }

    int getBrushColor() {
        return mDrawPaint.getColor();
    }
    String getXfermode() {
        Xfermode currentXfermode = mDrawPaint.getXfermode();
        if ( currentXfermode == clearXfermode)
            return "CLEAR";
        else if ( currentXfermode == overXfermode)
            return  "OVER";
        return "NULL";
    }

    void clearAll() {
        mDrawnPaths.clear();
        if (mDrawCanvas != null) {
            mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        invalidate();
    }

    void setBrushViewChangeListener(BrushViewChangeListener brushViewChangeListener) {
        mBrushViewChangeListener = brushViewChangeListener;
    }

    public void setOnDrawingListner(OnDrawingListener onDrawingListner) {
        drawingListener = onDrawingListner;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Bitmap canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (LinePath linePath : mDrawnPaths) {
            canvas.drawPath(linePath.getDrawPath(), linePath.getDrawPaint());
        }

//        for (LinePath linePath : networkDrawnPaths)
//        {
//            canvas.drawPath(linePath.getDrawPath(),linePath.getDrawPaint());
//        }

        canvas.drawPath(networkPath,networkDrawPaint);
        canvas.drawPath(mPath, mDrawPaint);


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (allowDraw) {
            float touchX = event.getX();
            float touchY = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStart(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchMove(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    touchUp();
                    break;
            }
            invalidate();
            return true;
        } else {
            return false;
        }
    }

    private void touchStart(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mTouchX = x;
        mTouchY = y;
        drawingListener.sendTouchStart(x, y, getBrushSize(), getBrushColor(),getXfermode());
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mTouchX);
        float dy = Math.abs(y - mTouchY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mTouchX, mTouchY, (x + mTouchX) / 2, (y + mTouchY) / 2);
            mTouchX = x;
            mTouchY = y;
            drawingListener.sendTouchMove(mTouchX, mTouchY, x, y);
        }
    }

    private void touchUp() {
        mPath.lineTo(mTouchX, mTouchY);
        mDrawnPaths.push(new LinePath(mPath, mDrawPaint));
        mPath = new Path();
        drawingListener.sendTouchUp(mTouchX, mTouchY);
    }

    public void networkMove(float from_x, float from_y, float to_x, float to_y) {
        Log.d("DRAW", "Network Move: ");
        networkPath.quadTo(from_x, from_y, (to_x + from_x) / 2, (to_y + from_y) / 2);
        invalidate();
    }

    public void networkStart(float x, float y, float brushSize, int color, String Xfermode) {
        Log.d("DRAW", "Network Start: ");
        networkDrawPaint.setColor(color);

        if(Xfermode.equals("CLEAR")) {
            networkDrawPaint.setStrokeWidth(mBrushEraserSize);
            networkDrawPaint.setXfermode(clearXfermode);
        }
        else {
            networkDrawPaint.setStrokeWidth(brushSize);
            networkDrawPaint.setXfermode(overXfermode);
        }

        networkPath.reset();
        networkPath.moveTo(x, y);
        invalidate();
    }

    public void networkUp(float x, float y) {
        networkPath.lineTo(x, y);
        mDrawnPaths.push(new LinePath(networkPath, networkDrawPaint));
        networkPath = new Path();
        invalidate();
    }


    public interface OnDrawingListener {
        void sendTouchStart(float x, float y, float brushSize, int color, String Xfermode);

        void sendTouchMove(float from_x, float from_y, float to_x, float to_y);

        void sendTouchUp(float x, float y);

    }
}