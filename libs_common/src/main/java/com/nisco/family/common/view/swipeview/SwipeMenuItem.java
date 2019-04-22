package com.nisco.family.common.view.swipeview;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by cathy on 16/11/8.
 */

public class SwipeMenuItem {
    private int id; //id
    private Context mContext;   //上下文
    private String title;   //标题
    private Drawable icon;  //图标
    private Drawable background;    //背景
    private int titleColor; //标题颜色
    private int titleSize;  //标题文字大小
    private int width;  //宽度

    public SwipeMenuItem(Context context) {
        mContext = context;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public int getTitleSize() {
        return titleSize;
    }

    public void setTitleSize(int titleSize) {
        this.titleSize = titleSize;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitle(int resId) {
        setTitle(mContext.getString(resId));
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setIcon(int resId) {
        this.icon = mContext.getResources().getDrawable(resId);
    }

    public Drawable getBackground() {
        return background;
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    public void setBackground(int resId) {
        this.background = mContext.getResources().getDrawable(resId);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
