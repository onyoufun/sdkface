package com.bianfeng.ymnsdk.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;

public class ResourceUtil {

	private static final String FILE_NAME_CFG = "ymncfgs";

	public static void savePreferences(Context context, String key, String value) {
		SharedPreferences preferences = context.getSharedPreferences(FILE_NAME_CFG, Context.MODE_PRIVATE);
		preferences.edit().putString(key, value).commit();
	}

	public static String readPreferences(Context context, String key) {
		SharedPreferences preferences = context.getSharedPreferences(FILE_NAME_CFG, Context.MODE_PRIVATE);
		return preferences.getString(key, null);
	}
	
	public static void removePreferences(Context context, String key) {
		SharedPreferences preferences = context.getSharedPreferences(FILE_NAME_CFG, Context.MODE_PRIVATE);
		preferences.edit().remove(key).commit();
	}
	
	/**
	 * 检查SD卡是否存在
	 * 
	 * @return 存在返回true，否则返回false
	 */
	public static boolean isSdcardReady() {
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获得SD路径
	 * 
	 * @return sdCard path end with separator
	 */
	public static String getSdcardPath() {
		return Environment.getExternalStorageDirectory().toString() + File.separator;
	}

	/**
	 * 检查SD卡中是否存在该文件
	 * @param filePath 不包含SD卡目录的文件路径
	 * @return
	 */
	public static boolean isSdcardFileExist(String filePath) {
		File file = new File(getSdcardPath() + filePath);
		return file.exists();
	}

	/***
	 * 判断asset中是否存在指定文件
	 * @param context
	 * @param filePath
	 * @return
	 */
	public static boolean assetFileExist(Context context, String filePath) {
		AssetManager am = context.getAssets();
		try {
			String[] names = am.list("");
			for (int i = 0; i < names.length; i++) {
				if (names[i].equals(filePath.trim())) {
					return true;
				} else {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/***
	 * 读取InputStream转byte[]
	 * @param in
	 * @return byte数组
	 */
	public static byte[] InputStreamToByte(InputStream in) {

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			byte[] buff = new byte[1024];
			while (in.read(buff) != -1) {
				out.write(buff);
			}
			in.close();

			byte[] bytes = out.toByteArray();
			out.flush();
			out.close();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取文件路径对应的文件夹名
	 * @param filePath
	 * @return
	 */
	public static String getFolder(String filePath) {
		String[] a = filePath.split(File.separator);
		if (a.length <= 0)
			return null;
		int tL = filePath.length();
		int nL = a[a.length - 1].length();
		return filePath.substring(0, tL - nL);
	}

	/**
	 * 将assets中的文件释放到指定目录
	 * @param context
	 * @param fromPath assets下文件位置
	 * @param toPath 目标位置
	 * @return
	 */
	public static boolean retrieveFileFromAssets(Context context, String fromPath, String toPath) {
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = context.getAssets().open(fromPath);

			File file = new File(toPath);
			file.createNewFile();
			fos = new FileOutputStream(file);

			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fos)
					fos.close();
			} catch (IOException e) {
			}
			try {
				if (null != is)
					is.close();
			} catch (IOException e) {
			}
		}
		return false;
	}
	
	 /**
     * 获取手机该应用的私有路径
     * @param context
     * @return cache directory end with separator
     */
    public static String getAppDataDir(Context context) {
        File cacheDir = context.getCacheDir();
        if (cacheDir != null) return cacheDir.getParent() + File.separator;
        else return "/data/data/" + context.getPackageName() + File.separator;
    }
	
	/**
	 * 构建文件路径
	 * @param fullPath 完整路径
	 */
	public static void mkFileDirs(String fullPath) {
		File file = new File(fullPath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
    /**
     * 删除目标路径的文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) return file.delete();
        return false;
    }
    
    public static String getFileNameByUrl(String url) {
         return url.substring(url.lastIndexOf("/") + 1, url.length());
    }
}
