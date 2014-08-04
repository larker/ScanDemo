package com.larkersos.demo.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;

/** utils类 */
public class Utils {
	/********************* 设置缩略图大小开始 ********************/
	public static Bitmap getBitmapFromFile(File dst, int width, int height) {
		if (null != dst && dst.exists()) {
			BitmapFactory.Options opts = null;
			if (width > 0 && height > 0) {
				opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(dst.getPath(), opts);
				// 计算图片缩放比例
				final int minSideLength = Math.min(width, height);
				opts.inSampleSize = computeSampleSize(opts, minSideLength,width * height);
				opts.inJustDecodeBounds = false;
				opts.inInputShareable = true;
				opts.inPurgeable = true;
			}
			try {
				return BitmapFactory.decodeFile(dst.getPath(), opts);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	/********************** 设置缩略图大小结束 **********************/
	/** 正文字号转译 */
	public static String getImgLoadMode(String imgLoadMode){
		String showMode = "始终加载";
		if("1".equals(imgLoadMode)){
			showMode = "始终加载";
		}else if("2".equals(imgLoadMode)){
			showMode = "不加载";
		}else if("3".equals(imgLoadMode)){
			showMode = "仅wifi下加载";
		}else{
			showMode = "始终加载";
		}
		return showMode;
	}
	/** 正文字号转译 */
	public static String getFontSize(String fontSize){
		String font = "17";
		if("小".equals(fontSize)){
			font = "14";
		}else if("大".equals(fontSize)){
			font = "20";
		}else if("特大".equals(fontSize)){
			font = "23";
		}else{
			font = "17";
		}
		return font;
	}
	/** 获取文件类型 **/
	public static String getMimeType(String fileUrl) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String type = fileNameMap.getContentTypeFor(fileUrl);
		return type;
	}
	/** 返回码转译 */
	public static String checkCode(String code) {
		if ("500".equals(code))
			return "请求失败，请稍后再试或与管理员联系";
		else if ("501".equals(code))
			return "网络连接异常，请检查网络";
		else
			return code;
	}
	/** 电话号码校验
	 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188、147(数据卡)
	 * 联通：130、131、132、152、155、156、185、186、3G上网卡145
	 * 电信：133、153、180、189、（1349卫通）
	 **/
	public static boolean checkTelPhoneNum(String phoneNum){
		String regExp = "^[1][3,4,5,8]{1}[0-9]{9}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(phoneNum);
		return m.find();
	}
	/** 邮箱校验 **/
	public static boolean checkEmail(String email){
		String pattern1="[a-z0-9A-Z][\\w_]+@\\w+(\\.\\w+)";
//		String pattern1 = "^([a-z0-9A-Z]+[-|//.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?//.)+[a-zA-Z]{2,}$";
		Pattern pattern = Pattern.compile(pattern1); 
		Matcher m = pattern.matcher(email); 
		return m.find();
	}
	/**用户名校验**/
	public static boolean checkNickname(String nickname){
		String regExp = "^[a-zA-Z0-9\u4E00-\u9FA5]+$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(nickname);
		return m.find();
	}
	/** 通过网络地址获取图片 */
	public static Bitmap getImageBitMap(String imgUrl) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			myFileUrl = new URL(imgUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		// 判断地址合法 20131108
		if(myFileUrl != null){
			try {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
				conn.setDoInput(true);
				conn.connect();
				is = conn.getInputStream();
				String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+FileCacheUtil.PRIVATE_DIR
						+FileCacheUtil.CACHE_DIR_IMG+"/firstPic";
				File file = new File(filePath);
				OutputStream os = new FileOutputStream(file);
				int bytesRead = 0;
				byte[] buffer = new byte[8192];
				while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
				}
				os.close();
				bitmap = BitmapFactory.decodeFile(filePath);
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
				bitmap = null;
			}
		}
		return bitmap;
	}
	/** 处理图片圆角 **/
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		return getRoundedCornerBitmap(bitmap,12);
	}
	/**
	 * 处理图片圆角
	 * @param bitmap
	 * @param roundPx ：值越大，越圆
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    return output;
	 }

	/** 时间戳转日期 **/
	public static String getDate(String date) {
		String time = "";
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			time = sdf.format(new Date(Long.parseLong(date) * 1000));
		}catch(Exception e){
			time = "";
		}
		return time;
	}
	/** 校验中文 **/
	public static boolean checkChinese(String str){
		 Matcher matcher = Pattern.compile("[\u4E00-\u9FA5]").matcher(str);
		return matcher.find();
	}
	/**验证数字**/
	public static boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	 } 
	/** 获取当前时间 **/
	public static String getNowDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	/** 没有网络 */
	public static final int NETWORKTYPE_INVALID = 0;
	/** wap网络 */
	// public static final int NETWORKTYPE_WAP = 1;
	/** 2G网络 */
	public static final int NETWORKTYPE_2G = 2;
	/** 3G和3G以上网络，或统称为快速网络 */
	public static final int NETWORKTYPE_3G = 3;
	/** wifi网络 */
	public static final int NETWORKTYPE_WIFI = 4;

	/** 检查网络连接 */
	public static boolean checkInternetConnect(Context context) {
		boolean isConnect = false;
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
		if (connManager != null) {
			NetworkInfo[] info = connManager.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						isConnect = true;
					}
				}
			}
		}
		return isConnect;
	}

	public static boolean isFastMobileNetwork(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		switch (telephonyManager.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return false; // ~ 14-64 kbps
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return true; // ~ 400-1000 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return true; // ~ 600-1400 kbps
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return false; // ~ 100 kbps
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return true; // ~ 2-14 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return true; // ~ 700-1700 kbps
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return true; // ~ 1-23 Mbps
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return true; // ~ 400-7000 kbps
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return true; // ~ 1-2 Mbps
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return true; // ~ 5 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return true; // ~ 10-20 Mbps
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return false; // ~25 kbps
		case TelephonyManager.NETWORK_TYPE_LTE:
			return true; // ~ 10+ Mbps
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return false;
		default:
			return false;
		}
	}

	/**
	 * 获取网络状态，wifi,wap,2g,3g.
	 * 
	 * @param context
	 *            上下文
	 * @return int 网络状态 {@link #NETWORKTYPE_2G},{@link #NETWORKTYPE_3G},
	 *         {@link #NETWORKTYPE_INVALID},{@link #NETWORKTYPE_WAP}
	 *         <p>
	 *         {@link #NETWORKTYPE_WIFI}
	 */

	public static int getNetWorkType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		int mNetWorkType = 0;
		if (networkInfo != null && networkInfo.isConnected()) {
			String type = networkInfo.getTypeName();
			if (type.equalsIgnoreCase("WIFI")) {
				mNetWorkType = NETWORKTYPE_WIFI;
			} else if (type.equalsIgnoreCase("MOBILE")) {
				// String proxyHost = android.net.Proxy.getDefaultHost();
				// mNetWorkType = TextUtils.isEmpty(proxyHost) ?
				// (isFastMobileNetwork(context) ? NETWORKTYPE_3G :
				// NETWORKTYPE_2G) : NETWORKTYPE_WAP;
				mNetWorkType = isFastMobileNetwork(context) ? NETWORKTYPE_3G
						: NETWORKTYPE_2G;
			}
		} else {
			mNetWorkType = NETWORKTYPE_INVALID;
		}

		return mNetWorkType;
	}
	/** 判断wifi是否可用 **/
	public static boolean isWifiConnected(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	        if (mWiFiNetworkInfo != null) {  
	            return mWiFiNetworkInfo.isAvailable();  
	        }  
	    }  
	    return false;  
	}


	/** 获取公共参数 */
	public static HashMap<String,String> getCommonParam() {
		HashMap<String,String> paramMap = new HashMap<String,String>();
		return paramMap;
	}
//	public static HashMap<String,String> getCommonParam(MyApp myApp) {
//		HashMap<String,String> paramMap = new HashMap<String,String>();
//		paramMap.put("appName", myApp.APP_NAME);
//		paramMap.put("channel", myApp.APP_CHANNEL);
//		paramMap.put("version", String.valueOf(myApp.APP_VERSION_CODE));
//		paramMap.put("deviceId", myApp.APP_HWID);
//		paramMap.put("sysModel", android.os.Build.MODEL);
//		paramMap.put("sysVersion", android.os.Build.VERSION.RELEASE);
//		paramMap.put("language", Locale.getDefault().getLanguage());
//		paramMap.put("deviceScreen", myApp.screenWith + "*" + myApp.screenHeight);
//		// 网络状态
//		int mNetWorkType = getNetWorkType(myApp.getApplicationContext());
//		if(mNetWorkType != Utils.NETWORKTYPE_INVALID){
//			String mNetWorkTypeStr = "WIFI";
//			if(mNetWorkType == Utils.NETWORKTYPE_WIFI){
//				mNetWorkTypeStr = "WIFI";
//			}else if(mNetWorkType == Utils.NETWORKTYPE_2G){
//				mNetWorkTypeStr = "2G";
//			}else if(mNetWorkType == Utils.NETWORKTYPE_3G){
//				mNetWorkTypeStr = "3G";
//			}
//			myApp.APP_NET_TYPE = mNetWorkTypeStr;
//		}
//		paramMap.put("netType", myApp.APP_NET_TYPE);
//		
//		return paramMap;
//	}
	
    /****/
	public static String[] processImg(String content, int width, boolean flag) {
		if (content.contains("poster")) {
			String[] temp = content.split("poster=");
			String[] temp1 = temp[0].split("<video");
			String[] temp2 = temp[1].split("</video>");
			String[] temp3 = temp2[0].split("webkit-playsinline");
			// return null;
			// return
			// temp1[0]+"<p align=\"center\"><img src="+temp3[0]+"onclick=\"window.contact.openVideo()\"/></p>"+temp2[1];
			// return
			// temp1[0]+"<p align=\"center\"><img src=\"./video_icon.png\"onclick=\"window.contact.openVideo()\"/></p>"+temp2[1];
			if (!flag)
				return new String[] {
						temp1[0]
								+ "<div style=\"width:100%; align=center; position:relative; padding-left:5px padding-right:5px padding-bottom:10px\" onclick=\"window.contact.openVideo()\" ><img src="
								+ temp3[0]
								+ "/><div style=\"width:20%; height:20%; position:absolute; z-index:5;bottom:40%; right:40%;\"><img src=\"./video_icon.png\" /></div></div><br/>"
								+ temp2[1], temp3[0] };
			else
				return new String[] {
						temp1[0]
								+ "<table width=\""
								+ width
								+ "px\" border=\"0\" onclick=\"window.contact.openVideo()\"><tr><td height=\""
								+ width
								/ 2
								+ "px\" background="
								+ temp3[0]
								+ "><table  border=\"0\" align=\"center\"><tr height=\""
								+ width
								* 47
								/ 100
								+ "px\"><td  style=\"padding-left:"
								+ width
								/ 4
								+ "\"><img src=\"./video_icon.png\"></td></tr><tr><td ></td></tr><tr ></tr></table></td></tr></table>"
								+ temp2[1], temp3[0] };
			// <div id="bg" style="background: url('xxx.jpg')">文字or<img
			// src="xxx.jpg" /></div>
		} else {
			String[] temp = content.split("video");
			if (!flag)
				return new String[] {
						temp[0]
								+ "div style=\"width:100%; align=center; position:relative; padding-left:5px padding-right:5px padding-bottom:10px\" onclick=\"window.contact.openVideo()\" ><img  src=\"./default_pic.png\"/><div style=\"width:20%; height:20%; position:absolute; z-index:5;bottom:40%; right:40%;\"><img src=\"./video_icon.png\" /></div></div><br/"
								+ temp[2], "" };
			else
				return new String[] {
						temp[0]
								+ "table width=\""
								+ width
								+ "px\" border=\"0\" onclick=\"window.contact.openVideo()\"><tr><td height=\""
								+ width
								/ 2
								+ "px\" background=\"./default_pic.png\"/><table  border=\"0\" align=\"center\"><tr height=\""
								+ width
								* 47
								/ 100
								+ "px\"><td  style=\"padding-left:"
								+ width
								/ 4
								+ "\"><img src=\"./video_icon.png\"></td></tr><tr><td ></td></tr><tr ></tr></table></td></tr></table"
								+ temp[2], "" };

		}
	}
}
