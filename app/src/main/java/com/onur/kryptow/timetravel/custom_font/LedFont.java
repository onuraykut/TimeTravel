package com.onur.kryptow.timetravel.custom_font;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by one on 3/12/15.
 */
@SuppressLint("AppCompatCustomView")
public class LedFont extends TextView {

    public LedFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LedFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LedFont(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/LED.Font.ttf");
            setTypeface(tf);
        }
    }

}