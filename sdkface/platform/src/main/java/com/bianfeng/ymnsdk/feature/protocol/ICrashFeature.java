package com.bianfeng.ymnsdk.feature.protocol;

/**
 * 崩溃上报
 * @author huchanghai
 */
public interface ICrashFeature {

	void setCrashUserIdentifier(String uid);

	void reportException(String eid, String data);

	void leaveBreadcrumb(String data);

}
