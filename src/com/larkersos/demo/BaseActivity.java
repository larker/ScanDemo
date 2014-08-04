package com.larkersos.demo;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.larkersos.demo.catalog.CatalogActivity;
import com.larkersos.demo.scan.CaptureActivity;
import com.larkersos.demo.utils.SocializeConfigUtil;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;


@SuppressLint("NewApi")
public class BaseActivity extends Activity {

	// 上下文
	public Context ctx;
	
	protected String shareTitle=""; 	// 预置分享标题
	protected String shareUrl=""; 			// 分享跳转地址
	protected String shareMessage; 	// 预置分享文本
	protected String shareImgUrl="";	// 预置分享图片

    /** 
     * 友盟Social SDK实例，整个SDK的Controller 
     */
	protected static UMSocialService mController = UMServiceFactory.getUMSocialService(SocializeConfigUtil.DESCRIPTOR, RequestType.SOCIAL);
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//解决网络不通
		// 判断系统版本 20131107
		int sysVersionSdk = android.os.Build.VERSION.SDK_INT;
		if(sysVersionSdk > 10){
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().build());
		}
		ctx = this;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		MobclickAgent.onResume(this);
		/** 设置为竖屏    */   
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){//竖屏：PORTRAIT；横屏：LANDSCAPE
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 响应Menu选择事件
		if (item.getItemId() == R.id.action_home) {
			// 首页
			Intent it = new Intent(ctx, MainActivity.class);
			startActivity(it);
		}else if (item.getItemId() == R.id.action_scan) {
			// 扫描页面
			Intent it = new Intent(ctx, CaptureActivity.class);
			startActivity(it);
		}else if (item.getItemId() == R.id.action_catalog) {
			// 分类页面
			Intent it = new Intent(ctx, CatalogActivity.class);
			startActivity(it);
		} else if (item.getItemId() == R.id.action_settings) {
			Intent intent=null;
            //判断手机系统的版本  即API大于10 就是3.0或以上版本 
            if(android.os.Build.VERSION.SDK_INT>10){
                intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            }else{
                intent = new Intent();
                ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
                intent.setComponent(component);
                intent.setAction("android.intent.action.VIEW");
            }
            startActivity(intent);
		}
		return true;
	}
	
	
	/**
	 * 根据接口文档规定 处理参数
	 * 
	 * @param paramsStr
	 */
	public String processParam(String url) {
		String resultUrl = url;
		// ①解析url
		HashMap<String, String> urlMap = new HashMap<String, String>();
		if (url.indexOf("?") > 0) {
			String[] temp = url.split("[?]");
			if (temp.length > 1) {
				resultUrl = temp[0];
				String[] params = temp[1].split("&");
				for (String param : params) {
					String[] kvpair = param.split("=");
					if (kvpair.length > 1) {
						urlMap.put(kvpair[0], kvpair[1]);
					}
				}
			}
		}
		// ② 更新 urlMap
//		paramsMap = Utils.getCommonParam();
//		for (String key : paramsMap.keySet()) {
//			if(paramsMap.get(key)!=null && paramsMap.get(key).trim().length()>0)
//			urlMap.put(key, paramsMap.get(key));
//		}

		// ③ 重新组拼url
		StringBuilder sb = new StringBuilder(resultUrl);
		if (urlMap.size() > 0) {
			sb = sb.append("?");

			// 不需要加密 直接请求
			for (String key : urlMap.keySet()) {
				sb = sb.append(key + "=" + urlMap.get(key) + "&");
			}
			try {
				resultUrl = sb.deleteCharAt(sb.length() - 1).toString();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
//		paramsMap = urlMap;
//		//  分享参数特别处理
//		if (paramsMap.containsKey("shareMessage")) {
//			eventMessage = paramsMap.get("shareMessage");
//		}
//		if (paramsMap.containsKey("shareMessageWeixin")) {
//			shareMessageWeixin = paramsMap.get("shareMessageWeixin");
//		}
//		if (paramsMap.containsKey("shareImgUrl")){
//			shareImgUrl = paramsMap.get("shareImgUrl");
//		}
//		if (paramsMap.containsKey("shareUrl")){
//			shareUrl = paramsMap.get("shareUrl");
//		}
		
		return resultUrl;
	}
	
	/** 
     * 分享按钮的 
     */ 
	public void onShareClick(View v) {
		
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);
		//UMSocialService mController,Activity mActivity,String shareContent,String shareImgUrl,String shareUrl,String shareContentWeixin, String weixinTitle){

		SocializeConfigUtil.setShareContent2(mController, ctx, shareMessage, shareImgUrl, shareUrl, shareMessage,shareTitle);
		// 设置支持的平台
		mController = SocializeConfigUtil.setSupportPlatforms(mController, ctx, shareUrl);
        // 打开平台选择面板，参数2为打开分享面板时是否强制登录,false为不强制登录
        mController.openShare((Activity) ctx, false);
	}

}
