package jxxsoft.com.cn.paohuzi.util;

import jxxsoft.com.cn.paohuzi.app.MainApplication;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
	private static final String TAG = NetworkUtil.class.getSimpleName();
	/**
	 * 获得应用程序上下文
	 */
	private final static Application mApp = MainApplication.getInstance();

	/**
	 * 判断是否有网络连接
	 * @return
	 */
	public static boolean isNetworkConnected() {
		//获得连接管理器
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mApp
				.getSystemService(Application.CONNECTIVITY_SERVICE);
		//获得激活的网络对象
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			//返回网络连通
			return mNetworkInfo.isAvailable();
		}
		return false;
	}

	/**
	 * 判断WIFI网络是否可用
	 * @return
	 */
	public static boolean isWifiConnected() {
		//获得连接管理器
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mApp
				.getSystemService(Application.CONNECTIVITY_SERVICE);
		//获得wifi网络对象
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWiFiNetworkInfo != null) {
			//返回wifi是否可用
			return mWiFiNetworkInfo.isAvailable();
		}
		return false;
	}

	/**
	 * 判断MOBILE网络是否可用
	 * 
	 * @return
	 */
	public static boolean isMobileConnected() {
		//获得连接管理器
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mApp
				.getSystemService(Application.CONNECTIVITY_SERVICE);
		//获得手机连接对象
		NetworkInfo mMobileNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mMobileNetworkInfo != null) {
			//返回手机网络是否可用
			return mMobileNetworkInfo.isAvailable();
		}
		return false;
	}

	/**
	 * 获取当前网络连接的类型信息 one of TYPE_MOBILE, TYPE_WIFI, TYPE_WIMAX, TYPE_ETHERNET,
	 * TYPE_BLUETOOTH, or other types defined by ConnectivityManager
	 * 
	 * @return
	 */
	public static int getConnectedType() {
		//获得连接管理器
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mApp
				.getSystemService(Application.CONNECTIVITY_SERVICE);
		//获得可用的网络对象
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
			//返回网络连接的类型
			return mNetworkInfo.getType();
		}
		return -1;
	}
}