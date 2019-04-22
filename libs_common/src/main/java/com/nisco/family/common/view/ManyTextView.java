package com.nisco.family.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * created by hzp on 2018/12/20 08:39
 * 作者：codehan
 * 描述：文本不足一行时居右显示，超过一行时居左显示
 */
public class ManyTextView extends TextView {

    private int mLineY;
    private int mViewWidth;

    public ManyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas Canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
        mViewWidth = getMeasuredWidth();
//        mViewWidth = getWidth();
        String text = getText().toString();
        mLineY = 0;
        mLineY += getTextSize();//获取text大小
        Layout layout = getLayout();

        if (layout == null) {
            return;
        }

        Paint.FontMetrics fm = paint.getFontMetrics();
        int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
        textHeight = (int) (textHeight * layout.getSpacingMultiplier() + layout
                .getSpacingAdd());

        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);//第i行开始
            int lineEnd = layout.getLineEnd(i);//第i行结束
            //返回布局必须有多宽，以便显示每个段落一行的指定文本切片。
            float width = StaticLayout.getDesiredWidth(text, lineStart,
                    lineEnd, getPaint());
            String line = text.substring(lineStart, lineEnd);

            if(i < layout.getLineCount() - 1) {
                if (needScale(line)) {
                    drawScaledText(Canvas, lineStart, line, width);
                } else {
                    Canvas.drawText(line, 0, mLineY, paint);
                }
            } else {
                if(layout.getLineCount()==1){
                    Canvas.drawText(line, getMeasuredWidth()-width, mLineY, paint);
                }else{
                    Canvas.drawText(line, 0, mLineY, paint);
                }
            }
            mLineY += textHeight;
        }
    }

    private void drawScaledText(Canvas Canvas, int lineStart, String line,
                                float lineWidth) {
        float x = 0;
        if (isFirstLineOfParagraph(lineStart, line)) {
            String blanks = "  ";
            Canvas.drawText(blanks, x, mLineY, getPaint());
            float bw = StaticLayout.getDesiredWidth(blanks, getPaint());
            x += bw;

            line = line.substring(3);
        }

        int gapCount = line.length() - 1;
        int i = 0;
        if (line.length() > 2 && line.charAt(0) == 12288
                && line.charAt(1) == 12288) {
            String substring = line.substring(0, 2);
            float cw = StaticLayout.getDesiredWidth(substring, getPaint());
            Canvas.drawText(substring, x, mLineY, getPaint());
            x += cw;
            i += 2;
        }

        float d = (mViewWidth - lineWidth) / gapCount;
        for (; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            Canvas.drawText(c, x, mLineY, getPaint());
            x += cw + d;
        }
    }

    private boolean isFirstLineOfParagraph(int lineStart, String line) {
        return line.length() > 3 && line.charAt(0) == ' '
                && line.charAt(1) == ' ';
    }

    private boolean needScale(String line) {
        if (line == null || line.length() == 0) {
            return false;
        } else {
            return line.charAt(line.length() - 1) != '\n';
        }
    }

}

