package com.linxcool.sdkface.feature;

import android.content.Context;
import android.text.TextUtils;

import com.linxcool.sdkface.AppConfig;
import com.linxcool.sdkface.util.Logger;
import com.linxcool.sdkface.util.ResourceUtil;
import com.linxcool.sdkface.entity.UrlConfig;
import com.linxcool.sdkface.entity.UrlLocalState;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

public class YmnURLManager {

	private static final String TAG = "SdkfaceURLManager";

	private static final String KEY_URL_HOST_PUBLIC = "url_host_public";
	private static final String KEY_URL_HOST_TEST = "url_host_test";

	public static final String URL_HOST_PUBLIC_V1 = "http://not-set";
	public static final String URL_HOST_PUBLIC_V2 = "http://not-set";
	public static final String URL_HOST_TEST = "http://not-set";

	public static final String URL_HOTFIX_PUBLIC = "http://not-set";
	public static final String URL_HOTFIX_TEST = "http://not-set";

	/**
	 * <meta-data name='YMN_HOST_VER' value='V2'/>
	 */
	public static final String URL_KEY_PUBLIC_V2 = "V2";

	private static Gson gson = new Gson();

	private static String runtimeUrlHost;
	private static Map<String, UrlConfig> remoteConfigs;
	public static UrlLocalState localState;

	public static String getHost(Context context) {
		if (!runtimeUrlHost.contains("http")) {
			if (runtimeUrlHost.contains(URL_HOST_PUBLIC_V1) || runtimeUrlHost.contains(URL_HOST_PUBLIC_V2)) {
				runtimeUrlHost = "https://" + runtimeUrlHost;
			} else {
				runtimeUrlHost = "http://" + runtimeUrlHost;
			}
		}
		return runtimeUrlHost;
	}

	public static String getHotFix() {
		String hotfixUrl;
		if (AppConfig.isDebug()) {
			hotfixUrl = URL_HOTFIX_TEST;
		} else {
			hotfixUrl = URL_HOTFIX_PUBLIC;
		}
		if (!hotfixUrl.contains("http")) {
			hotfixUrl = "http://" + hotfixUrl;
		}
		return hotfixUrl;
	}

	public static void init(Context context) {
		try {
			loadRemoteConfigs(context);
			loadLocalState(context);
			loadRuntimeUrlHost(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从私有文件读取地址配置列表
	 * @param context
	 */
	private static void loadRemoteConfigs(Context context) {
		remoteConfigs = YmnPreferences.loadUrlRemoteState(context);
	}

	public static void loadLocalState(Context context) {
		localState = YmnPreferences.loadUrlLocalState(context);
	}

	private static void loadRuntimeUrlHost(Context context) throws Exception {
		// 从SD卡读取请求地址
		runtimeUrlHost = urlOnSdCard4Public(context);
		// 从私有文件（远程）读取请求地址
		if (TextUtils.isEmpty(runtimeUrlHost)) {
			runtimeUrlHost = urlOnLocalState4Public(context);
			Logger.d(TAG, "从私有文件（远程）读取请求地址 " + runtimeUrlHost);
		}
		// 从APK（UsdkProperties）读取请求地址
		if (TextUtils.isEmpty(runtimeUrlHost)) {
			if (AppConfig.isDebug()) {
				runtimeUrlHost = urlOnApk4Test();
			} else {
				runtimeUrlHost = urlOnApk4Public();
			}
		}
		Logger.d(TAG, "runtime host is " + runtimeUrlHost);
	}

	/**
	 * 保存远程地址配置并刷新本地配置
	 * @param context
	 * @param config
	 */
	public static void saveRemoteConfig(Context context, UrlConfig config) {
		try {
			YmnPreferences.saveUrlRemoteConfig(context, config);
			loadRuntimeUrlHost(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveLoacalState(Context context) {
		YmnPreferences.saveUrlLoacalState(context, localState);
	}

	/**
	 * 从SD卡读取请求地址
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static String urlOnSdCard4Public(Context context) throws Exception {
		if (!ResourceUtil.isSdcardReady()) {
			return null;
		}

		File file = new File(ResourceUtil.getSdcardPath() + ".bftj/sdk/ymnDebug");
		if (!file.exists()) {
			return null;
		}

		Properties properties = new Properties();
		properties.load(new FileInputStream(file));
		return properties.getProperty("url_host_ymnsdk");
	}

	/**
	 * 从APK配置文件读取请求地址
	 * @return
	 */
	private static String urlOnApk4Public() {
		String url = YmnProperties.getValue(KEY_URL_HOST_PUBLIC);
		Logger.d(TAG, "YmnProperties.getValue(KEY_URL_HOST_PUBLIC) :" + url);
		if (TextUtils.isEmpty(url)) {
			url = keyToUrl(AppConfig.getHostUrl());
			Logger.d(TAG, "keyToUrl(AppConfig.getHostUrl():" + url);
		}
		return url;
	}

	private static String keyToUrl(String key) {
		if (key.startsWith("http")) {
			return key;
		}
		if (URL_KEY_PUBLIC_V2.equals(key)) {
			return URL_HOST_PUBLIC_V2;
		}
		return URL_HOST_PUBLIC_V1;
	}

	private static String urlOnApk4Test() {
		String url = YmnProperties.getValue(KEY_URL_HOST_TEST);
		if (TextUtils.isEmpty(url)) {
			url = URL_HOST_TEST;
		}
		return url;
	}

	/**
	 * 从私有目录读取地址配置
	 * @param context
	 * @return
	 */
	private static String urlOnLocalState4Public(Context context) {
		if (localState != null) {
			return localState.getCurrentHost();
		}
		return null;
	}

	public static void notifyRequestSuccess(Context context) {
		if (localState != null) {
			localState.resetNormalContinuedFails();
			localState.resetBackupContinuedFails();
			
			if(localState.isBackupHost()){
				localState.reduceBackupRemainTime();
				checkLocalState(context);
			}
			
			saveLoacalState(context);
		}
	}

	public static void notifyRequestFailure(Context context) {
		if (localState != null) {
			if (localState.isNormalHost()) {
				localState.increaseNormalContinuedFails();
			} else if (localState.isBackupHost()) {
				localState.increaseBackupContinuedFails();
			}
			checkLocalState(context);
			saveLoacalState(context);
		}
	}

	private static void checkLocalState(Context context) {
		Logger.d(TAG, "localState is " + gson.toJson(localState));

		if (localState.isNormalHost()) {
			// 达到连续错误上限，切为备用地址
			if (localState.isNormalContinuedFailsLimited()) {
				localState.resetNormalContinuedFails();
				localState.resetBackupContinuedFails();
				localState.resetBackupRemainTime();
				localState.setCurrentHostToBackup();
				Logger.w(TAG, "set host to backup " + localState.getCurrentHost());
			}
		} else if (localState.isBackupHost()) {
			// 达到使用上限，切为常用地址
			if (localState.isBackupRemainTimeUseup()) {
				localState.resetBackupRemainTime();
				localState.resetNormalContinuedFails();
				localState.setCurrentHostToNormal();
				Logger.w(TAG, "set host to normal " + localState.getCurrentHost());
			}
			// 达到连续错误上限，清理配置，切为初始地址
			else if (localState.isBackupContinuedFailsLimited()) {
				cleanLocalState(context);
				Logger.w(TAG, "cleaned local state");
			}
		}

		try {
			if (localState != null) {
				runtimeUrlHost = localState.getCurrentHost();
			} else {
				loadRuntimeUrlHost(context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void cleanLocalState(Context context) {
		try {
			YmnPreferences.clearAllUrlConfigs(context);
			localState = null;
			remoteConfigs.clear();
			loadRuntimeUrlHost(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
