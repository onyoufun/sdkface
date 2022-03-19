package com.linxcool.sdkface.util;

import java.util.Observer;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class ViewHelper {

	public static void setLogoView(final Activity activity, final View logoView, long delayMillis, final Observer callback){
		try{
			final ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
			final View contentView = parent.getChildAt(0);
			parent.removeView(contentView);
			
			parent.addView(logoView, 0, new LayoutParams(-1, -1));
			
			Handler handler = new Handler(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					parent.removeView(logoView);
					if(contentView != null) parent.addView(contentView, 0, new LayoutParams(-1, -1));
					callback.update(null, null);
					return false;
				}
			});
			handler.sendEmptyMessageDelayed(0, delayMillis);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
