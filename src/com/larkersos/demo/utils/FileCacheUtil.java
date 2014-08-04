package com.larkersos.demo.utils;

import java.io.File;

import android.content.Context;
import android.util.Log;

/** utils类 */

public class FileCacheUtil extends FileUtil{
	private static final String TAG = FileCacheUtil.class.getName();

	public static final int CONFIG_CACHE_TIMEOUT = 1000*60*60*24; // 1 day

	/**
	 * 获取缓存所在的路径
	 */
	public static String getCachePath() {
		return FileUtil.getFilePath(FileUtil.HAS_SD_CARD,FileUtil.CACHE_DIR);
	}
	/**
	 * 获取文件缓存所在的路径
	 */
	public static String getCacheFilePath() {
		return FileUtil.getFilePath(FileUtil.HAS_SD_CARD,FileUtil.CACHE_DIR_FILES);
	}
	/**
	 * 获取图片缓存所在的路径
	 */
	public static String getCacheImagePath() {
		return FileUtil.getFilePath(FileUtil.HAS_SD_CARD,FileUtil.CACHE_DIR_IMG);
	}
	
	/**
	 * 根据文件名获取文件内容
	 * isSDCard控制是否使用sd
	 */
	public static String getFileCache(String fileName,Context context) {
		//SD存在，写入SD中
		return getFileCache(fileName, context, FileUtil.HAS_SD_CARD);
	}
	public static String getFileCache(String fileName,Context context,boolean isSDCard) {
		if (fileName == null) {
			return null;
		}

		// 读取内容
		String result = null;
		File file = getCacheFile(fileName,isSDCard);
		if (file.exists() && file.isFile()) {
			try {
				result = FileUtil.readFile(fileName, isSDCard, context);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	/**
	 * 保持文件内容
	 * isSDCard控制是否使用sd
	 */
	public static void setFileCache(String message, String fileName) {
		setFileCache(message, fileName, FileUtil.HAS_SD_CARD);
	}
	public static void setFileCache(String message, String fileName,boolean isSDCard) {
		// 处理文件名获取文件
		fileName = dealCacheFileName(fileName);
		//SD存在，写入SD中
		File file = getCacheFile(fileName,isSDCard);
		try {
			// 是否新建文件
			if(file == null){
				file = FileUtil.createFile(fileName, FileUtil.CACHE_DIR_FILES,isSDCard);
			}
			
			// 创建缓存数据到磁盘，就是创建文件
			FileUtil.writeFile(fileName, message, isSDCard);
		} catch (Exception e) {
			Log.d(TAG, "write " + file.getAbsolutePath() + " data failed!");
			e.printStackTrace();
		}
	}
	
	/**
	 * 处理缓存文件名
	 */
	public static String dealCacheFileName(String fileName) {
		String cacheFileName = fileName;
		// http://ku.m.chinanews.com/forapp/cl/sh/newslist_1.xml
		if(cacheFileName.indexOf("chinanews.com") > 0){
			cacheFileName = cacheFileName.substring(fileName.indexOf("chinanews.com") + 13).replaceAll("/", "_");
		}
		return cacheFileName;
	}
	
	/**
	 * 根据文件名获取所有的缓存文件
	 */
	public static File getCacheFile() {
		return  new File(getCacheFilePath());
	}
	
	/**
	 * 根据文件名获取文件是否存在
	 */
	public static boolean hasCacheFile(File file,String fileName) {
		// 处理文件名获取文件
		fileName = dealCacheFileName(fileName);
		return hasFile(file, fileName);
	}
	
	/**
	 * 根据文件名获取文件
	 * isSDCard控制是否使用sd
	 * expiredTime控制缓存时间 默认为1天  -1为不过期
	 */
	public static File getCacheFile(String fileName) {
		return getCacheFile(fileName, FileUtil.HAS_SD_CARD);
	}
	public static File getCacheFile(String fileName,boolean isSDCard) {
		return getCacheFile(fileName, isSDCard, CONFIG_CACHE_TIMEOUT);
	}
	public static File getCacheFile(String fileName,long expiredTime) {
		return getCacheFile(fileName, FileUtil.HAS_SD_CARD, expiredTime);
	}
	public static File getCacheFile(String fileName,boolean isSDCard,long expiredTime) {
		File file = null;
		
		// 处理文件名获取文件
		fileName = dealCacheFileName(fileName);
		file = new File(getCacheFilePath(),fileName);
		// 文件是否存在
		if (file != null && file.exists() && file.isFile() && file.length() > 0) {
			long currentExpiredTime = System.currentTimeMillis() - file.lastModified();
			Log.d(TAG, file.getAbsolutePath() + " expiredTime:" + expiredTime
					/ 60000 + "min");
			if(currentExpiredTime > expiredTime && (expiredTime !=-1)) {
				file.delete();
				file = null;
			}
		}else{
			file = null;
		}

		return file;
	}
	
}
