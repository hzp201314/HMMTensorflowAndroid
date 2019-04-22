package com.nisco.family.common.base;
/**
 * 其他fragment的父类，复写一些基本方法
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nisco.family.common.constant.CommonConstants;
import com.nisco.family.common.R;
import com.nisco.family.common.model.User;
import com.nisco.family.common.utils.LogUtils;
import com.nisco.family.common.utils.SharedPreferenceUtil;

public abstract class BaseFragment extends Fragment implements View.OnClickListener {

	public Context mContext;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		Log.d(getFragmentName(), " onHiddenChanged" + hidden);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		LogUtils.i(getFragmentName(), " onAttach()");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		LogUtils.i(getFragmentName(), " onCreate()");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		LogUtils.i(getFragmentName(), " onCreateView()");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		LogUtils.i(getFragmentName(), " onViewCreated()");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LogUtils.i(getFragmentName(), " onActivityCreated()");
	}

	@Override
	public void onStart() {
		super.onStart();
		LogUtils.i(getFragmentName(), " onStart()");
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtils.i(getFragmentName(), " onResume()");
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtils.i(getFragmentName(), " onPause()");
	}

	@Override
	public void onStop() {
		super.onStop();
		LogUtils.i(getFragmentName(), " onStop()");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		LogUtils.i(getFragmentName(), " onDestroyView()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtils.i(getFragmentName(), " onDestroy()");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		LogUtils.i(getFragmentName(), " onDetach()");
	}

	/**
	 * fragment name
	 */
	public abstract String getFragmentName();

	/**
	 * 切换界面
	 * @param packageContext
	 * @param cls
	 * @param bundle
	 */
	public void pageJumpResultActivity(Context packageContext, Class<?> cls,
                                       Bundle bundle) {
		Intent intent = new Intent(packageContext, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
        ((Activity) packageContext).overridePendingTransition(
                R.anim.push_left_in, R.anim.push_left_out);

	}

	@Override
	public void onClick(View v) {

	}

	/**
	 * 判断是否是第一次登录
	 * @return
	 */
	public boolean isFirstLogin(){
		User user = (User) SharedPreferenceUtil.get(CommonConstants.USERINFO_FILE_NAME, CommonConstants.USERINFO_KEY_NAME);
		if (null != user && !user.isFirstIn()){
			return false;
		}
		return true;
	}
}
