package com.larkersos.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.os.Environment;

/** FileUtil类 */
public class FileUtil {
//	private static final String TAG = FileUtil.class.getName();
	
	/** 检查是否存在SD卡，如果存在对SD卡操作，如果不存在，对data/data操作 **/
	public static final boolean HAS_SD_CARD = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	
	/** 缓存所在文件夹 **/
	public static final String AP_PRIVATE_DIR = "./data/data/com.cns.mc.activity/files/";//数据最目录
	public static final String PRIVATE_DIR = "/chinanews/";//数据最目录
	//系统数据文件夹
	public static final String SYSTEM_DATA_DIR = "system";
	// 所有缓存文件夹
	public static final String CACHE_DIR = "cache";
	//图片缓存文件夹
	public static final String CACHE_DIR_IMG = "cache/images";
	// 默认缓存文件夹
	public static final String CACHE_DIR_FILES = "cache/files";
	// 下载文件apk缓存文件夹
	public static final String CACHE_DIR_DOWNLOAD = "cache/download";
	//图片下载
	public static final String DOWN_LOAD = "download";
	//视频缓存文件夹
		public static final String CACHE_DIR_VIDEO = "cache/video";
	public static String getFilePath(boolean isSDCard) {
		// 默认路径文件夹cacheFile
		return getFilePath(isSDCard, CACHE_DIR_FILES);
	}
	public static String getFilePath(String dirFile) {
		// 默认路径文件夹cacheFile
		return getFilePath(HAS_SD_CARD, dirFile);
	}
	public static String getFilePath(boolean isSDCard ,String dirFile) {
		StringBuffer path = new StringBuffer();
		if(isSDCard){
			//SD存在，写入SD中
			path = path.append(Environment.getExternalStorageDirectory());
		}else{
			//sd不存在，写入data/data/下
			path = path.append(AP_PRIVATE_DIR);
		}
		path = path.append(PRIVATE_DIR);
		
		// 是否指定文件夹
		if(dirFile !=null && dirFile.trim().length() > 0){
			path = path.append(dirFile).append("/");
		}
		if(isSDCard){
			createSDFileDir(path.toString());
		}else{
			createDataDir(path.toString());
		}
		return path.toString();
	}

	public static String getCacheDecodeString(String url) {
		// 1. 处理特殊字符
		// 2. 去除后缀名带来的文件浏览器的视图凌乱(特别是图片更需要如此类似处理，否则有的手机打开图库，全是我们的缓存图片)
		if (url != null) {
			return url.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
		}
		return null;
	}
	
	/** 
	 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	 * 写入文件
	 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	 **/
	// 根据是否使用SD卡写在对应目录下面的文件
	public static void writeFile(String fileName, String message,boolean isSDCard) {
//		if(isSDCard){
//			writeFileSdcard(fileName, message);
//		}else{
//			writeFileData(fileName, message);
//		}
		try {
			// FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);
			FileOutputStream fout = new FileOutputStream(getFilePath(isSDCard,CACHE_DIR_FILES) + fileName);

			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	// 写在SD卡目录下面的文件
	public static void writeFileSdcard(String fileName, String message) {

		try {
			FileOutputStream fout = new FileOutputStream(getFilePath(true,CACHE_DIR_FILES) + fileName);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 写文件在./data/data/package/files/下面
	public static void writeFileData(String fileName, String message,Context context) {
		try {
			// 直接写入文件
			FileOutputStream fout = context.openFileOutput(getFilePath(false,CACHE_DIR_FILES) + fileName,
					Context.MODE_PRIVATE);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/** 
	 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	 * 读取文件
	 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	 **/
	// 从assets 文件夹中获取文件并读取数据
	/*
	 * public static String getFromAssets(Context context,String fileName) {
	 * String result = ""; try { InputStreamReader inputReader = new
	 * InputStreamReader(context.getResources().getAssets().open(fileName));
	 * BufferedReader bufReader = new BufferedReader(inputReader); String line =
	 * ""; while ((line = bufReader.readLine()) != null){ result += line; } }
	 * catch (Exception e) { e.printStackTrace(); } try { result = new
	 * String(result.getBytes("GBK"), "UTF-8"); } catch
	 * (UnsupportedEncodingException e) { e.printStackTrace(); } return result;
	 * }
	 */
	public static String getFromAssets(Context context, String fileName) {
		return getFromAssets(context, fileName,"UTF-8");
	}
	public static String getFromAssets(Context context, String fileName,String charSet) {
		String result = "";
		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			result = EncodingUtils.getString(buffer, charSet);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	// 读取目录下面的文件
	public static String readFile(String fileName,boolean isSDCard, Context context) {
		String res = "";
		FileInputStream fin = null;
		fin =  readFileStream(fileName, isSDCard, context, fin);
		
		try {
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				fin.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return res;
	}
	
	// 读取目录下面的文件流
	public static FileInputStream readFileStream(String fileName, Context context,FileInputStream fin) {
		return readFileStream(fileName, HAS_SD_CARD, context, fin);
	}
	public static FileInputStream readFileStream(String fileName,boolean isSDCard, Context context,FileInputStream fin) {
		if(isSDCard){
			fin =  readFileSdcardStream(fileName,fin);
		}else{
			fin =  readFileDataStream(fileName, context,fin,isSDCard);
		}
		return fin;
	}
	

	// 读在/mnt/sdcard/目录下面的文件
	public static FileInputStream readFileSdcardStream(String fileName,FileInputStream fin) {
		try {
			fin = new FileInputStream(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fin;
	}

	// 读文件在./data/data/package/下面
	public static FileInputStream readFileDataStream(String fileName, Context context,FileInputStream fin,boolean isSDCard) {
		try {
			if(context == null){
				fin = new FileInputStream(getFilePath(isSDCard) + fileName);
			}else{
				fin = context.openFileInput(fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fin;
	}
	
	/** 
	 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	 * 创建目录文件
	 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	 **/
	 public static File createFile(String fileName){
		 return createFile(fileName,"", HAS_SD_CARD);
	 }
	 public static File createFile(String fileName,boolean isSDCard){
		 return createFile(fileName,"", isSDCard);
	 }
	
	 public static File createFile(String fileName,String fileDir,boolean isSDCard){
		 File file = null;
		 // 新建文件夹
		 String path = getFilePath(isSDCard,fileDir);
		 creatFileDir(fileDir, isSDCard);
		// 新建文件
		 file = new File(path,fileName);
		 if(!file.exists()){
			 try {
				 file.createNewFile();
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		 }

		 return file;
	 }
	
	 /** 
	  * 新建dirPath 目录
	  * 
	  *  **/
	 public static File creatFileDir(String fileDir,boolean isSDCard){
		 File file = null;
		 // 新建文件夹
		 String path = getFilePath(isSDCard,fileDir);
		 if(isSDCard){
			 file = createSDFileDir(path);
		 }else{
			 file = createDataDir(path);
		 }
		 return file;
	 }
	
	 /** data/data下创建目录 
	  * dirPath 目录
	  * 
	  * **/
	 public static File createDataDir(String dirPath){
		 File dirFile = new File(dirPath);
		 if(!dirFile.exists()){
			if(dirFile.mkdirs()){
				 //赋予路径读写权限
				 String str = "chmod " + dirPath + "777" + " && busybox chmod " + dirPath + "777";
				 try {
					 Runtime.getRuntime().exec(str);
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			}
		 }
		 return dirFile;
	 }
	 /** 新建SDCard文件夹
	  * dirPath 目录
	  *  **/
	 public static File createSDFileDir(String dirPath){
		 File dirFile = new File(dirPath);
		 if(!dirFile.exists()){
			dirFile.mkdirs();
		 }
		 return dirFile;
	 }
	 
	 /** 输入流转字符串 **/
	 public static String Stream2String(InputStream is) {
         BufferedReader reader = new BufferedReader(new InputStreamReader(is), 16*1024); //强制缓存大小为16KB，一般Java类默认为8KB
         StringBuilder sb = new StringBuilder();
         String line = null;
         try {
             while ((line = reader.readLine()) != null) {  //处理换行符
                 sb.append(line + "\n");  
             }
         } catch (IOException e) {
             e.printStackTrace();
         } finally {
             try {
                 is.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
         return sb.toString();
     }
	 
	 /** 
	  * 删除文件，包含子文件,文件夹删除
	  *  
	  * **/
	 public static boolean deleteAllFile(File file){
		boolean result = true;
		if (file.exists()) {
			try{
				File[] files = file.listFiles();
				for (int i = 0;result && i < files.length; i++) {
					if (files[i].isDirectory()) {
						// 循环删除文件夹下的
						result = deleteAllFile(files[i]);
					}else{
						files[i].delete();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				result = false;
			}
		}
		
		return result;
	 }
	 
	 /** 
	  * 获得大小
	  *  **/
	 public static long getFileSize(File file){
		long fileSize = 0;
		// 判断空
		if(file != null){
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isDirectory()) {
					fileSize += files[i].length();
				}else{
					// 循环下级目录文件
					fileSize += getFileSize(files[i]);
				}
			}
		}
		return fileSize;
	 }
	 /** 
	  * 获得文件个数
	  *  **/
	 public static long getFileMum(File file){
		int fileNum = 0;
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()) {
				fileNum ++;
			}else{
				// 循环下级目录文件
				fileNum += getFileMum(files[i]);
			}
		}
		return fileNum;
	 }
	 /** 
	  * 获得文件存在
	  *  **/
	 public static boolean hasFile(File file,String fileName){
		 boolean hasFile= false;
		 if(fileName != null && fileName.trim().length() > 0){
			File[] files = file.listFiles();
			for (int i = 0; !hasFile && i < files.length; i++) {
				if (!files[i].isDirectory()) {
					if (files[i].getName().equals(fileName)) {
						hasFile= true;
						break;
					}
				}else{
					// 循环下级目录文件
					hasFile = hasFile(files[i],fileName);
				}
			}
		 }
		return hasFile;
	 }

}
