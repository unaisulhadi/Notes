package com.meridian.mynotes.View;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.meridian.mynotes.R;

import java.util.HashMap;
import java.util.Map;

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

    /*
     * Caches typefaces based on their file path and name, so that they don't have to be created
     * every time when they are referenced.
     */
    private static Map<String, Typeface> mTypefaces;
    private Context context;

    public CustomTextView(final Context context) {
        this(context, null);
        setTypeFace(context,null);
    }

    public CustomTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
        setTypeFace(context,attrs);
    }

    public CustomTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        setTypeFace(context,attrs);
    }

    private void setTypeFace(Context context, AttributeSet attrs)
    {
        this.context=context;
        if (mTypefaces == null)
        {
            mTypefaces = new HashMap<String, Typeface>();
        }
        // prevent exception in Android Studio / ADT interface builder
        if (this.isInEditMode() || attrs==null)
        {
            return;
        }

        setIncludeFontPadding(false);
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        if (array != null) {
            final String typefaceAssetPath = array.getString(
                    R.styleable.CustomTextView_tvTypeface);

            if (typefaceAssetPath != null) {
                Typeface typeface = null;

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
