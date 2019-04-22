package com.nisco.family.common.view;

/**
 * Created by cathy on 2016/12/20.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 解决scrollView嵌套ListView listView显示不全的问题
 */
public class MyListView extends ListView {
    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs,
                      int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    /**
     * 重写该方法、达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}