package com.nisco.family.common.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.nisco.family.common.R;
import com.nisco.family.common.adapter.CommonAdapter;
import com.nisco.family.common.adapter.ViewHolder;
import com.nisco.family.common.model.SelectItem;
import com.nisco.family.common.utils.TextUtil;

import java.util.List;


/**
 * Created by tianzy on 2018/11/19.
 */

public class MyCommonDialog extends Dialog {

    private TextView titleTv;//消息标题文本
    private TextView mQuery_tv;
    private EditText mEditText;
    private String titleStr;
    private ListView listView;
    private List<SelectItem> mDatas;
    private ListAdapter adapter;
    private Context mContext;

    private OnItemclickListener onItemclickListener;

    public void setOnItemclickListener(OnItemclickListener onItemclickListener) {
        this.onItemclickListener = onItemclickListener;
    }

    public MyCommonDialog(Context context, List<SelectItem> list, String title) {
        super(context, R.style.custom_dialog_style);
        this.titleStr = title;
        this.mDatas = list;
        this.mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_custom_dialog_layout);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(true);
        initView();
        initData();
        initEvent();
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != onItemclickListener) {
                    onItemclickListener.onItemClick(view, position);
                }
            }
        });

        mQuery_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = mEditText.getText().toString().trim();
                onItemclickListener.onQuery(info, adapter);

            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        titleTv = (TextView) findViewById(R.id.title);
        mQuery_tv = (TextView) findViewById(R.id.query_tv);
        listView = (ListView) findViewById(R.id.dialog_list);
        mEditText = (EditText) findViewById(R.id.input_info_et);
        TextUtil.toUpperCase(mEditText);
        adapter = new ListAdapter(mContext, R.layout.common_dialog_item_layout);
        adapter.setmDatas(mDatas);
        listView.setAdapter(adapter);
    }

    /**
     * 设置listview item的点击监听事件
     */
    public interface OnItemclickListener {
        public void onItemClick(View view, int position);

        public void onQuery(String keyWord, ListAdapter dialogAdapter);
    }

    public class ListAdapter extends CommonAdapter<SelectItem> {

        public ListAdapter(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, SelectItem item) {
            holder.setText( R.id.code_tv, item.getType());
            holder.setText( R.id.name_tv, item.getName());
        }
    }
}
