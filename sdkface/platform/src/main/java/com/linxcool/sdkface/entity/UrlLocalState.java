package com.linxcool.sdkface.entity;

public class UrlLocalState {

	static final int STATE_NORMAL = 0;
	static final int STATE_BACKUP = 1;
	
	private UrlConfig urlConfig;
	private String currentHost;
	private int hostState;
	
	private int normalContinuedFails;
	private int backupContinuedFails;
	private int backupRemainTime;

	public UrlLocalState(UrlConfig urlConfig) {
		this.urlConfig = urlConfig;
		resetNormalContinuedFails();
		resetBackupContinuedFails();
		resetBackupRemainTime();
		setCurrentHostToNormal();
	}

	public void updateConfig(UrlConfig urlConfig) {
		if (isNormalHost()) {
			setCurrentHost(urlConfig.getNormalHost());
		} else if (isBackupHost()) {
			setCurrentHost(urlConfig.getBackupHost());
		}
		this.urlConfig = urlConfig;
	}

	public boolean isNormalHost() {
		return hostState == STATE_NORMAL;
	}

	public boolean isBackupHost() {
		return hostState == STATE_BACKUP;
	}

	public String getCurrentHost() {
		return currentHost;
	}

	private void setCurrentHost(String currentHost) {
		this.currentHost = currentHost;
	}

	public void setCurrentHostToNormal() {
		this.hostState = STATE_NORMAL;
		setCurrentHost(urlConfig.getNormalHost());
	}

	public void setCurrentHostToBackup() {
		this.hostState = STATE_BACKUP;
		setCurrentHost(urlConfig.getBackupHost());
	}

	public void increaseNormalContinuedFails() {
		this.normalContinuedFails++;
	}

	public void resetNormalContinuedFails() {
		this.normalContinuedFails = 0;
	}

	public boolean isNormalContinuedFailsLimited() {
		return normalContinuedFails >= urlConfig.getMaxNormalFails();
	}

	public void increaseBackupContinuedFails() {
		this.backupContinuedFails++;
	}

	public void resetBackupContinuedFails() {
		this.backupContinuedFails = 0;
	}

	public boolean isBackupContinuedFailsLimited() {
		return backupContinuedFails >= urlConfig.getMaxBackupFails();
	}

	public void reduceBackupRemainTime() {
		this.backupRemainTime--;
	}

	public void resetBackupRemainTime() {
		this.backupRemainTime = urlConfig.getMaxBackupAvailableCount();
	}

	public boolean isBackupRemainTimeUseup() {
		return backupRemainTime <= 0;
	}

}
