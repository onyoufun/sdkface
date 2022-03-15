package com.bianfeng.ymnsdk.feature.protocol;

import java.util.Map;

/**
 * 数据分析
 * @author huchanghai
 */
public interface IAnalyticsFeature {

	void startSession();

	void stopSession();

	void setSessionContinueMillis(int millis);

	void setCaptureUncaughtException(boolean opt);

	void logError(String eid, String data);

	void logEvent(String eid);

	void logEvent(String eid, Map<String, String> data);

	void logTimedEventBegin(String eid);

	void logTimedEventEnd(String eid);

}
