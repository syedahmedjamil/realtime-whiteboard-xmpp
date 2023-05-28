package com.example.whiteboard.ui.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.whiteboard.R;

public class FreeHandDrawingView extends View {


    private OnMoveListener onMoveListener;
    private OnStartListner onStartListner;
    private OnUpListener onUpListener;
    private Paint mPaint;
    private Path mPath;
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private int drawColor;
    private int backgroundColor;
    private float mX;
    private float mY;
    int moveCount = 0;
    int networkMoveCount = 0;
    private static final float TOUCH_TOLERANCE = 5;
    private Rect mFrame;

    public FreeHandDrawingView(Context context) {
        super(context);

    }

    public FreeHandDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);


        drawColor = R.color.colorPrimary;

        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(drawColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12f);
    }

    public FreeHandDrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        int inset = 40;
        mFrame = new Rect (inset, inset, w - inset, h - inset);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap,0,0,null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN :
            {
                touchStart(x,y);
                break;
            }
            case MotionEvent.ACTION_MOVE :
            {
                touchMove(x,y);
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP :
            {
                touchUp();
                break;
            }
            default:
        }
        return  true;
    }

    private void touchUp() {
        Toast.makeText(getContext(), "Up", Toast.LENGTH_SHORT).show();
        Log.d("DRAW", "Touch Up: " + moveCount);
        mPath.reset();
        onUpListener.onUp();
        moveCount=0;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
        {
            moveCount++;
            Log.d("DRAW", "Touch Move: " + moveCount);

            mPath.quadTo(mX , mY , (x+mX)/2 , (y+mY)/2);
            mCanvas.drawPath(mPath,mPaint);
            onMoveListener.onMove(mX,mY,x,y);

            mX = x;
            mY = y;
        }
    }

    private void touchStart(float x, float y) {
        Toast.makeText(getContext(), "Start", Toast.LENGTH_SHORT).show();
        Log.d("DRAW", "Touch Start: " + moveCount);
        mPath.moveTo(x,y);
        onStartListner.onStart(x,y);
        mX = x;
        mY = y;
    }

    public void networkMove(float from_x, float from_y, float to_x, float to_y)
    {
        Log.d("DRAW", "Network Move: " + ++networkMoveCount);
        mPath.quadTo(from_x , from_y , (to_x+from_x)/2 , (to_y+from_y)/2);
        mCanvas.drawPath(mPath,mPaint);
        invalidate();
    }

    public void networkStart(float x , float y)
    {
        Log.d("DRAW", "Network Start: ");
        mPath.moveTo(x,y);
    }

    public void  networkUp()
    {
        networkMoveCount = 0;
        Log.d("DRAW", "Network Up: ");
        mPath.reset();
    }

    public interface OnMoveListener{
        public void onMove(float from_x, float from_y, float to_x, float to_y );
    }

    public interface OnStartListner
    {
        public void onStart(float x ,float y);
    }

    public interface OnUpListener
    {
        public void onUp();
    }

    public void setOnMoveListener(OnMoveListener listener)
    {
        onMoveListener = listener;
    }

    public void setOnStartListner(OnStartListner onStartListner) {
        this.onStartListner = onStartListner;
    }

    public void setOnUpListener(OnUpListener onUpListener) {
        this.onUpListener = onUpListener;
    }
}