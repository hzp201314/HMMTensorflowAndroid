package com.nisco.family.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author gumingxing
 * 
 * @param <T>
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
	protected Context mContext;
	protected List<T> mDatas;

	protected LayoutInflater mInflater;
	private int layoutId;

	public CommonAdapter(Context context, int layoutId) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mDatas = new ArrayList<T>();
		this.layoutId = layoutId;
	}

	public List<T> getmDatas() {
		return mDatas;
	}

	public void setmDatas(List<T> mDatas) {
		this.mDatas = mDatas;
	}

	public void addlist(List<T> mDatas) {
		this.mDatas.addAll(mDatas);
	}

	public void clear() {
		this.mDatas.clear();
	}

	@Override
	public int getCount() {
		return mDatas.size() != 0 ? mDatas.size() : 0;
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.get(mContext, convertView, parent,
				layoutId, position);
		convert(holder, getItem(position));
		return holder.getConvertView();
	}

	public abstract void convert(ViewHolder holder, T t);

}
