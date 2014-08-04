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

	// ������
	public Context ctx;
	
	protected String shareTitle=""; 	// Ԥ�÷������
	protected String shareUrl=""; 			// ������ת��ַ
	protected String shareMessage; 	// Ԥ�÷����ı�
	protected String shareImgUrl="";	// Ԥ�÷���ͼƬ

    /** 
     * ����Social SDKʵ��������SDK��Controller 
     */
	protected static UMSocialService mController = UMServiceFactory.getUMSocialService(SocializeConfigUtil.DESCRIPTOR, RequestType.SOCIAL);
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//������粻ͨ
		// �ж�ϵͳ�汾 20131107
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
		/** ����Ϊ����    */   
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){//������PORTRAIT��������LANDSCAPE
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
		// ��ӦMenuѡ���¼�
		if (item.getItemId() == R.id.action_home) {
			// ��ҳ
			Intent it = new Intent(ctx, MainActivity.class);
			startActivity(it);
		}else if (item.getItemId() == R.id.action_scan) {
			// ɨ��ҳ��
			Intent it = new Intent(ctx, CaptureActivity.class);
			startActivity(it);
		}else if (item.getItemId() == R.id.action_catalog) {
			// ����ҳ��
			Intent it = new Intent(ctx, CatalogActivity.class);
			startActivity(it);
		} else if (item.getItemId() == R.id.action_settings) {
			Intent intent=null;
            //�ж��ֻ�ϵͳ�İ汾  ��API����10 ����3.0�����ϰ汾 
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
	 * ���ݽӿ��ĵ��涨 �������
	 * 
	 * @param paramsStr
	 */
	public String processParam(String url) {
		String resultUrl = url;
		// �ٽ���url
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
		// �� ���� urlMap
//		paramsMap = Utils.getCommonParam();
//		for (String key : paramsMap.keySet()) {
//			if(paramsMap.get(key)!=null && paramsMap.get(key).trim().length()>0)
//			urlMap.put(key, paramsMap.get(key));
//		}

		// �� ������ƴurl
		StringBuilder sb = new StringBuilder(resultUrl);
		if (urlMap.size() > 0) {
			sb = sb.append("?");

			// ����Ҫ���� ֱ������
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
//		//  ��������ر���
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
     * ����ť�� 
     */ 
	public void onShareClick(View v) {
		
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);
		//UMSocialService mController,Activity mActivity,String shareContent,String shareImgUrl,String shareUrl,String shareContentWeixin, String weixinTitle){

		SocializeConfigUtil.setShareContent2(mController, ctx, shareMessage, shareImgUrl, shareUrl, shareMessage,shareTitle);
		// ����֧�ֵ�ƽ̨
		mController = SocializeConfigUtil.setSupportPlatforms(mController, ctx, shareUrl);
        // ��ƽ̨ѡ����壬����2Ϊ�򿪷������ʱ�Ƿ�ǿ�Ƶ�¼,falseΪ��ǿ�Ƶ�¼
        mController.openShare((Activity) ctx, false);
	}

}
