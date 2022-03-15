package com.bianfeng.ymnsdk.entity;

import android.text.TextUtils;

public class UrlConfig {

	private int pid;
	private String gid;
	private int level;
	private ServerList serverList;

	public boolean isEnable() {
		if (TextUtils.isEmpty(getNormalHost()))
			return false;
		if (TextUtils.isEmpty(getBackupHost()))
			return false;
		return true;
	}

	public int getPid() {
		return pid;
	}

	public String getGid() {
		return gid;
	}

	public int getLevel() {
		return level;
	}

	public Normal getNormal() {
		if (serverList != null) {
			return serverList.getNormal();
		}
		return null;
	}

	public String getNormalHost() {
		Normal normal = getNormal();
		if (normal == null) {
			return null;
		}
		return normal.getServer();
	}

	public int getMaxNormalFails() {
		Normal normal = getNormal();
		if (normal == null) {
			return 0;
		}
		return normal.getMax_fails();
	}

	public Backup getBackup() {
		if (serverList != null) {
			return serverList.getBackup();
		}
		return null;
	}

	public String getBackupHost() {
		Backup backup = getBackup();
		if (backup == null) {
			return null;
		}
		return backup.getServer();
	}

	public int getMaxBackupFails() {
		Backup backup = getBackup();
		if (backup == null) {
			return 0;
		}
		return backup.getMax_fails();
	}

	public int getMaxBackupAvailableCount() {
		Backup backup = getBackup();
		if (backup == null) {
			return 0;
		}
		return backup.getRequest_times();
	}

	public class ServerList {
		private Normal normal;
		private Backup backup;

		public Normal getNormal() {
			return this.normal;
		}

		public Backup getBackup() {
			return this.backup;
		}
	}

	public class Normal {
		private String server;
		private int max_fails;

		public String getServer() {
			return this.server;
		}

		public int getMax_fails() {
			return this.max_fails;
		}

	}

	public class Backup {
		private String server;
		private int max_fails;
		private int request_times;

		public String getServer() {
			return this.server;
		}

		public int getMax_fails() {
			return this.max_fails;
		}

		public int getRequest_times() {
			return this.request_times;
		}
	}
}
