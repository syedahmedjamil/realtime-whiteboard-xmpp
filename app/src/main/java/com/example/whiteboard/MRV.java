package com.example.whiteboard;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MRV extends RecyclerView {
    public MRV(@NonNull Context context) {
        super(context);
    }

    public MRV(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MRV(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int height = (int)(Resources.getSystem().getDisplayMetrics().heightPixels * 0.5f);
        heightSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }
}
