package com.nisco.family.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class MyGridView extends GridView
{

    private boolean haveScrollbar = true;

    public MyGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MyGridView(Context context)
    {
        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    // 解决scrollView和gridView嵌套内容只显示一行的冲突
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }

}
