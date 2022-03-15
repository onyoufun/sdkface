package com.bianfeng.ymnsdk.feature.protocol;

import org.json.JSONObject;

/**
 * 广告展示
 * @author huchanghai
 */
public interface IAdsFeature {

	void showAds(JSONObject json);

	void hideAds(JSONObject json);

	void preloadAds(JSONObject json);

	boolean isAdTypeSupported(int arg);

	float queryPoints();

	void spendPoints(int arg);
}
