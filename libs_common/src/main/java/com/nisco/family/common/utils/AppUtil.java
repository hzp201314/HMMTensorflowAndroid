package com.nisco.family.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;

/**
 * 描述：应用工具类.
 */
public class AppUtil {

	public static List<String[]> mProcessList = null;

	/**
	 * 描述：打开并安装文件.
	 * 
	 * @param context
	 *            the context
	 * @param file
	 *            apk文件路径
	 */
	public static void installApk(Context context, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 描述：卸载程序.
	 * 
	 * @param context
	 *            the context
	 * @param packageName
	 *            包名
	 */
	public static void uninstallApk(Context context, String packageName) {
		Intent intent = new Intent(Intent.ACTION_DELETE);
		Uri packageURI = Uri.parse("package:" + packageName);
		intent.setData(packageURI);
		context.startActivity(intent);
	}

	/**
	 * 用来判断服务是否运行.
	 * 
	 * @param context
	 *            the context
	 * @param className
	 *            判断的服务名字 "com.xxx.xx..XXXService"
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> servicesList = activityManager
				.getRunningServices(Integer.MAX_VALUE);
		Iterator<RunningServiceInfo> l = servicesList.iterator();
		while (l.hasNext()) {
			RunningServiceInfo si = l.next();
			if (className.equals(si.service.getClassName())) {
				isRunning = true;
			}
		}
		return isRunning;
	}

	/**
	 * 停止服务.
	 * 
	 * @param context
	 *            the context
	 * @param className
	 *            the class name
	 * @return true, if successful
	 */
	public static boolean stopRunningService(Context context, String className) {
		Intent intent_service = null;
		boolean ret = false;
		try {
			intent_service = new Intent(context, Class.forName(className));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (intent_service != null) {
			ret = context.stopService(intent_service);
		}
		return ret;
	}

	/**
	 * 描述：判断网络是否有效.
	 * 
	 * @param context
	 *            the context
	 * @return true, if is network available
	 */
	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * Gps是否打开 需要<uses-permission
	 * android:name="android.permission.ACCESS_FINE_LOCATION" />权限
	 * 
	 * @param context
	 *            the context
	 * @return true, if is gps enabled
	 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * 判断当前网络是否是移动数据网络.
	 * 
	 * @param context
	 *            the context
	 * @return boolean
	 */
	public static boolean isMobile(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}

	/**
	 * 获取屏幕尺寸与密度.
	 * 
	 * @param context
	 *            the context
	 * @return mDisplayMetrics
	 */
	public static DisplayMetrics getDisplayMetrics(Context context) {
		Resources mResources;
		if (context == null) {
			mResources = Resources.getSystem();
		} else {
			mResources = context.getResources();
		}
		DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
		return mDisplayMetrics;
	}

	/**
	 * 打开键盘.
	 * 
	 * @param context
	 *            the context
	 */
	public static void showSoftInput(Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 关闭键盘事件.
	 * 
	 * @param context
	 *            the context
	 */
	public static void closeSoftInput(Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null
				&& ((Activity) context).getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(((Activity) context)
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 获取包信息.
	 * 
	 * @param context
	 *            the context
	 */
	public static PackageInfo getPackageInfo(Context context) {
		PackageInfo info = null;
		try {
			String packageName = context.getPackageName();
			info = context.getPackageManager().getPackageInfo(packageName,
					PackageManager.GET_ACTIVITIES);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * 
	 * 描述：获取可用内存.
	 * 
	 * @param context
	 * @return
	 */
	public static long getAvailMemory(Context context) {
		// 获取android当前可用内存大小
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		// 当前系统可用内存 ,将获得的内存大小规格化
		return memoryInfo.availMem;
	}

	/**
	 * 
	 * 描述：总内存.
	 * 
	 * @param context
	 * @return
	 */
	public static long getTotalMemory(Context context) {
		// 系统内存信息文件
		String file = "/proc/meminfo";
		String memInfo;
		String[] strs;
		long memory = 0;

		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
			// 读取meminfo第一行，系统内存大小
			memInfo = bufferedReader.readLine();
			strs = memInfo.split("\\s+");
			// 获得系统总内存，单位KB
			memory = Integer.valueOf(strs[1]).intValue() * 1024;
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Byte转位KB或MB
		return memory;
	}

	/**
	 * <sd是否已插入> <功能详细描述>
	 * 
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean hasSDExisted() {
		String code = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(code)) {
			return true;
		}
		return false;
	}

}
