package com.xdluoyang.millionheroes;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class WindowSmall extends LinearLayout {

    public static int viewWidth;
    public static int viewHeight;
    public Context context;

    public WindowSmall(Context context) {
        super(context);
        this.context = context;

        LayoutInflater.from(context).inflate(R.layout.window_small, this);
        View view = findViewById(R.id.small_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
    }
}