package com.bianfeng.ymnsdk;

@Deprecated
public interface YmnUserCode {

	int ACTION_RET_INIT_SUCCESS = 100;
	int ACTION_RET_INIT_FAIL = 101;
	
	int ACTION_RET_LOGIN_SUCCESS = 102;
	int ACTION_RET_LOGIN_TIMEOUT = 103;
	int ACTION_RET_LOGIN_NO_NEED = 104;
	int ACTION_RET_LOGIN_FAIL = 105;
	int ACTION_RET_LOGIN_CANCEL = 106;
	
	int ACTION_RET_LOGOUT_SUCCESS = 107;
	int ACTION_RET_LOGOUT_FAIL = 108;
	
	int ACTION_RET_PLATFORM_ENTER = 109;
	int ACTION_RET_PLATFORM_BACK = 110;
	
	int ACTION_RET_PAUSE_PAGE = 111;
	
	int ACTION_RET_EXIT_PAGE = 112;
	
	int ACTION_RET_ANTIADDICTIONQUERY = 113;
	
	int ACTION_RET_REALNAMEREGISTER = 114;
	
	int ACTION_RET_ACCOUNTSWITCH_SUCCESS = 115;
	int ACTION_RET_ACCOUNTSWITCH_FAIL = 116;

	int ACTION_RET_PLATFORM_INFO = 117;
}
