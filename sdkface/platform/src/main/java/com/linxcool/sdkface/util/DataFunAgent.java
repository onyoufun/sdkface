package com.linxcool.sdkface.util;

import android.content.Context;

import com.linxcool.sdkface.AppConfig;

import org.json.JSONArray;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 测试埋点代理类
 * @author huchanghai
 */
public class DataFunAgent {

	private static String sessionId;

	protected static void init(Context context) {
		try {
			Logger.e("DataFunAgent.init(), but it not set real sdk...");
			sessionId = UUID.randomUUID().toString();
		} catch (Exception e) {
			e.printStackTrace();
			sessionId = String.valueOf(System.currentTimeMillis());
		}
	}

	protected static void onEvent(String eventId, String ext, Map<String, Object> map) {
		Logger.e("DataFunAgent.onEvent(), but it not set real sdk...");
	}

	public static String getDeviceId(Context context) {
		try {
			String mid = DeviceIdUtil.getDevcieId(context);
			return URLEncoder.encode(mid, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String getVersion() {
		return "0.0.0";
	}
	
	protected static void testCallFunction(String functionName) {
		testCallFunction(functionName, null);
	}

	protected static void testCallFunction(String functionName, String[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sessionId", sessionId);
		map.put("functionName", functionName);
		map.put("args", arrayToMapItem(args));
		map.put("package_id", AppConfig.getConfigId());
		Logger.e("DataFunAgent.testCallFunction(), but it not set real sdk...");
	}

	private static String arrayToMapItem(String[] args) {
		if (args == null || args.length == 0) {
			return null;
		}
		JSONArray json = new JSONArray();
		for (int i = 0; i < args.length; i++) {
			json.put(args[i]);
		}
		return json.toString();
	}

}
