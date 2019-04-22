package com.nisco.family.common.view;

/**
 * Created by wangyu on 2017/12/20.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * 重写ExpandableListView以解决ScrollView嵌套ExpandableListView
 * <br> 导致ExpandableListView显示不正常的问题
 */
public class CustomExpandableListView extends ExpandableListView {

    public CustomExpandableListView(Context context) {
        super(context);
    }

    public CustomExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomExpandableListView(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}