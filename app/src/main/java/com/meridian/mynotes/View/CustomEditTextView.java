package com.meridian.mynotes.View;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.meridian.mynotes.R;

import java.util.HashMap;
import java.util.Map;

public class CustomEditTextView extends android.support.v7.widget.AppCompatEditText {

    private static Map<String, Typeface> mTypefaces;
    private Context context;

    public CustomEditTextView(Context context) {
        super(context);
        setTypeFace(context,null);
    }

    public CustomEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeFace(context,attrs);
    }

    public CustomEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeFace(context,attrs);
    }

    private void setTypeFace(Context context, AttributeSet attrs) {
        this.context=context;
        if (mTypefaces == null) {
            mTypefaces = new HashMap<String, Typeface>();
        }
        // prevent exception in Android Studio / ADT interface builder
        if (this.isInEditMode() || attrs==null) {
            return;
        }

        setIncludeFontPadding(false);
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomEditTextView);
        if (array != null)
        {
            final String typefaceAssetPath = array.getString(R.styleable.CustomEditTextView_etTypeface);

            if (typefaceAssetPath != null) {
                Typeface typeface ;

                if (mTypefaces.containsKey(typefaceAssetPath)) {
                    typeface = mTypefaces.get(typefaceAssetPath);
                } else {
                    AssetManager assets = context.getAssets();
                    typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
                    mTypefaces.put(typefaceAssetPath, typeface);
                }
                setTypeface(typeface);
            }
            else {
                setDefaultTypeFace();
            }

            array.recycle();
        }
        else {
            setDefaultTypeFace();
        }
    }

    private void setDefaultTypeFace() {
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_regular));
        setTypeface(custom_font);
    }
}

