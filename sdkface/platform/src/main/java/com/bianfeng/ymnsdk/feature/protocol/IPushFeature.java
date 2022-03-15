package com.bianfeng.ymnsdk.feature.protocol;

import java.util.List;

/**
 * 推送
 * @author huchanghai
 */
public interface IPushFeature {

	void startPush();

	void closePush();

	void setAlias(String alias);

	void delAlias(String alias);

	void setTags(List<String> tags);

	void delTags(List<String> tags);

}
