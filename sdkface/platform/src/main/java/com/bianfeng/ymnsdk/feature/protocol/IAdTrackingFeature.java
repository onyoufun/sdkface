package com.bianfeng.ymnsdk.feature.protocol;

import java.util.Map;

/**
 * 广告跟踪
 * @author huchanghai
 */
public interface IAdTrackingFeature {

	void onRegister(String data);

	void trackEvent(String data);

	void trackEvent(String arg, Map<String, String> data);

}
