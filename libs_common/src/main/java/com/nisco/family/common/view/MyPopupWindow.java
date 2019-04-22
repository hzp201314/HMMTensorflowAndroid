package com.nisco.family.common.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.nisco.family.common.R;
import com.nisco.family.common.adapter.CommonAdapter;
import com.nisco.family.common.adapter.ViewHolder;
import com.nisco.family.common.model.SelectItem;

import java.util.List;

/**
 * Created by tianzy on 2018/8/23.
 */
public class MyPopupWindow extends PopupWindow {

    private Activity mContext;
    private OnItemClickListener onItemClickListener;
    private View menuView;
    private List<SelectItem> mDatas;

    public MyPopupWindow(Activity mContext, List<SelectItem> mDatas, OnItemClickListener onItemClickListener) {
        super(mContext);
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.onItemClickListener = onItemClickListener;
        initData();
    }

    private void initData() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        menuView = inflater.inflate(R.layout.common_popuwindow_layout, null);
        ListView lv = (ListView) menuView.findViewById(R.id.lv);
        PopupwindowAdapter adapter = new PopupwindowAdapter(mContext, R.layout.common_populist_item);
        adapter.setmDatas(mDatas);
        lv.setAdapter(adapter);

//        ViewTreeObserver observer = view.getViewTreeObserver();
//        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                int width = view.getMeasuredWidth();
//                MyPopupWindow.this.setWidth(width);
//                MyPopupWindow.this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
//            }
//        });

        MyPopupWindow.this.setWidth(160);
        MyPopupWindow.this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        // 设置SelectPicPopupWindow的View
        this.setContentView(menuView);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setBackgroundDrawable(mContext.getResources().getDrawable(R.color.divider));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClickListener.onItemClick(position);
                dismiss();
            }
        });

        menuView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int height = menuView.findViewById(R.id.popu_layout).getTop();
                int y = (int) menuView.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class PopupwindowAdapter extends CommonAdapter<SelectItem> {

        public PopupwindowAdapter(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, SelectItem selectItem) {
            holder.setText(R.id.popu_tv, selectItem.getName());
        }
    }
}
