package com.example.teachingbox3;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by 孟东风 on {DATE}.
 */
//修改字体颜色和大小
public class QNumberPicker extends NumberPicker {
    public QNumberPicker(Context context){
        super(context);
    }
    public QNumberPicker(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    public QNumberPicker(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
    }
    @Override
    public void addView(View child){
        super.addView(child);
        updateView(child);
    }
    //参数params
    @Override
    public void addView(View child,android.view.ViewGroup.LayoutParams params){
        super.addView(child,params);
        updateView(child);
    }

    @Override
    public void addView(View child,int index,android.view.ViewGroup.LayoutParams params){
        super.addView(child,index,params);
    }
    public void updateView(View view){
        if( view instanceof EditText){
            EditText editText = (EditText) view;
            editText.setTextColor(ContextCompat.getColor(getContext(), R.color.white)); //修改字的颜色
            editText.setTextSize(10f);//修改字的大小
        }
    }
}