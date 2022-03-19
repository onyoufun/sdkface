package com.linxcool.sdkface.feature.protocol;

import java.util.Map;

/**
 * 社交
 * @author huchanghai
 */
public interface ISocialFeature {

	void signIn();

	void signOut();

	void submitScore(String user, long score);

	void showLeaderboard(String data);

	void unlockAchievement(Map<String, String> data);

	void showAchievements();

}
