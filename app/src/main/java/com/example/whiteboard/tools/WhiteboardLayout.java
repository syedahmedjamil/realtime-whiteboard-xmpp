package com.example.whiteboard.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.whiteboard.R;

public class WhiteboardLayout extends ConstraintLayout implements GestureDetector.OnGestureListener {

    private GestureDetector gestureDetector;
    private OnTapListener onTapListener;

    public WhiteboardLayout(Context context) {
        super(context);
    }

    public WhiteboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WhiteboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BrushDrawingView getBrushDrawingView() {
        gestureDetector = new GestureDetector(getContext(), this);
        return (BrushDrawingView) findViewById(R.id.brush_drawing_view_fragment_drawing);
    }

    public void setOnTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
            return true;

        return super.onTouchEvent(event);

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Toast.makeText(getContext(), "Tapped the whiteboard", Toast.LENGTH_SHORT).show();
        if(onTapListener != null)
            onTapListener.onTap(e);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface OnTapListener{
        void onTap(MotionEvent event);
    }
}
