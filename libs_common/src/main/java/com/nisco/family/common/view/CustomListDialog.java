package com.nisco.family.common.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.nisco.family.common.R;
import com.nisco.family.common.adapter.CommonAdapter;
import com.nisco.family.common.adapter.ViewHolder;
import com.nisco.family.common.model.SelectItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianzy on 2018/12/18.
 * 注：退出activity 销毁dialog
 */

public class CustomListDialog extends Dialog {

    private TextView titleTv;//消息标题文本
    private String titleStr;//从外界设置的title文本
    private ListView listView;
    private List<SelectItem> mDatas = new ArrayList<>();
    private ListAdapter adapter;
    private Context mContext;
    public static CustomListDialog customListDialog;

    private OnItemclickListener onItemclickListener;//取消按钮被点击了的监听器

    public void setOnItemclickListener(OnItemclickListener onItemclickListener) {
        this.onItemclickListener = onItemclickListener;
    }

    public CustomListDialog(Context context) {
        super(context, R.style.custom_dialog_style);
        this.mContext = context;
    }

    public static CustomListDialog getInstance(Context context){
        if (null == customListDialog){
            customListDialog = new CustomListDialog(context);
        }
        return customListDialog;
    }

    public void setTitleStr(String titleStr){
        this.titleStr = titleStr;
        if (!TextUtils.isEmpty(titleStr) && null != titleTv){
            titleTv.setText(titleStr);
        }
    }

    public void addDatas(List<SelectItem> list){
        this.mDatas.clear();
        this.mDatas.addAll(list);
    }

    public static void clearDialog(){
        if (null != customListDialog){
            customListDialog.cancel();
            customListDialog = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.common_listdialog_layout);
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
                if (null != onItemclickListener){
                    onItemclickListener.onItemClick(view, position);
                }
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
        titleTv = (TextView) findViewById( R.id.title);
        listView = (ListView) findViewById( R.id.dialog_list);
        adapter = new ListAdapter(mContext, R.layout.common_dialog_item_layout);
        adapter.setmDatas(mDatas);
        listView.setAdapter(adapter);
    }

    class ListAdapter extends CommonAdapter<SelectItem> {

        public ListAdapter(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, SelectItem item) {
            holder.setText( R.id.code_tv, item.getType());
            holder.setText( R.id.name_tv, item.getName());
        }
    }

    /**
     * 设置listview item的点击监听事件
     */
    public interface OnItemclickListener {
        public void onItemClick(View view, int position);
    }
}
