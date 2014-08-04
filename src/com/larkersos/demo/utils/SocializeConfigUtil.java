package com.larkersos.demo.utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;

import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * @功能描述 : 
 *		在此进行平台的配置， 比如需要支持平台，则添加相应的项即可
 *
 */
public class SocializeConfigUtil {
	// 新浪微博
	public static boolean SUPPORT_SINA = true;
	//腾讯微博
	public static boolean SUPPORT_TENC = true;
	//人人
	public static boolean SUPPORT_RENR = false;
	//豆瓣
	public static boolean SUPPORT_DOUBAN = false;
	//QQ空间
	public static boolean SUPPORT_QZONE = false;
	// 来往
	public static boolean SUPPORT_LAIWANG = false;
	// 易信
	public static boolean SUPPORT_YIXIN = false;
	
	// 微信，朋友圈单独处理
	// 微信
	public static boolean SUPPORT_WX = true;
	// 微信朋友圈
	public static boolean SUPPORT_WXCircle = true;

	// 其他
	public static boolean SUPPORT_FACEBOOK = false;
	public static boolean SUPPORT_TWITTER = false;
	public static boolean SUPPORT_GOOGLE = false;

	// 可支持，不支持的分享渠道
	public static final String  PLATFORMS_SUPPORT = "supportPlatforms";
	public static final String  PLATFORMS_DONT_SUPPORT = "noSupportPlatforms";
	
	// package
	public static final String DESCRIPTOR = "com.umeng.share";
	// 默认跳转URL为m版
	public static final String DEFAULT_URL = "http://m.chinanews.com";

	private static final Set<WeakReference<SocializeConfig>> wCigs = new HashSet<WeakReference<SocializeConfig>>();

	/**
	 * 设置支持分享平台。
	 * 
	 * @return
	 */
	public static UMSocialService setSupportPlatforms(UMSocialService mController,Context mActivity, String contentUrl){
		// 设置平台
		SocializeConfig config = mController.getConfig();
		// 不支持的渠道剔除
		List<SHARE_MEDIA> mediasList = getSupportPlatformsMap().get(PLATFORMS_DONT_SUPPORT);
		for(SHARE_MEDIA media:mediasList){
			config.removePlatform(media);
		}
		// 设置支持的平台
		config.setPlatforms(SocializeConfigUtil.getSupportPlatforms());
		mController.setGlobalConfig(config);

		//设置是否支持微信
		mController = addWXPlatform(mController,mActivity, contentUrl);
		
		return mController;
	}
	/**
	 * @param mController
	 * @param mActivity
	 * @param shareContent  分享内容
	 * @param shareImgUrl	分享图片url地址
	 * @return
	 */
	public static UMSocialService setSupportPlatforms(UMSocialService mController,Activity mActivity,String shareContent,String shareImgUrl){
		// 设置支持的平台
		mController = setSupportPlatforms(mController, mActivity,shareImgUrl);
		// 设置分享内容 20140117
		mController = setShareContent(mController, mActivity, shareContent, shareImgUrl,null,null,null);
		
		return mController;

	}
	
	/**
	 * 设置支持分享内容
	 * @param mController
	 * @param mActivity
	 * @param shareContent
	 * @param shareImgUrl
	 * @param shareUrl
	 * @param shareContentWeixin  微信的分享内容，可独立设置，默认为shareContent
	 * @return
	 */
	public static UMSocialService setShareContent(UMSocialService mController,Activity mActivity,String shareContent,String shareImgUrl,String shareUrl,String shareContentWeixin, String weixinTitle){
		// 没有跳转url则使用图片地址
		if(shareUrl == null || shareUrl.trim().length()==0){
			shareUrl = shareImgUrl;
		}

		// 分享标题
		String shareTitle = "中新网分享";
		
		// 分享内容
		mController.setShareContent(shareContent);
		// 判断设置是否分享图片 20140117
		UMImage mUMImgBitmap = null;
		if(shareImgUrl !=null && shareImgUrl.trim().length() > 0){
//			// 要发送的只能为本地文件， 不能为url？
			mUMImgBitmap = new UMImage(mActivity, shareImgUrl);
			if(mUMImgBitmap !=null){
//				mUMImgBitmap.setTitle("图片");
				// target url 必须填写
				mUMImgBitmap.setTargetUrl(shareUrl) ;
//				mController.setShareImage(mUMImgBitmap);
				mController.setShareMedia(mUMImgBitmap);
			}
		}
		
		// 设置分享内容
		try {
			// 设置分享到微信的内容, 图片类型
			if(shareContentWeixin == null || shareContentWeixin.trim().length()==0){
				shareContentWeixin = shareContent;
			}
			WeiXinShareContent weixinContent = new WeiXinShareContent(mUMImgBitmap);
			if(weixinTitle == null || weixinTitle.trim().length() == 0){
				weixinContent.setTitle(shareTitle);
			}else{
				weixinContent.setTitle(weixinTitle);
			}
			weixinContent.setShareContent(shareContentWeixin.replaceAll("#snsName#", "wechat_session"));
			mController.setShareMedia(weixinContent);
			// 设置朋友圈分享的内容
			CircleShareContent circleShareContent = new CircleShareContent(mUMImgBitmap);
			circleShareContent.setTitle(shareContentWeixin.replaceAll("#snsName#", "wechat_timeline"));
			circleShareContent.setShareContent(shareContentWeixin.replaceAll("#snsName#", "wechat_timeline"));
			mController.setShareMedia(circleShareContent);

			
			// 设置分享到腾讯微博的文字内容
			TencentWbShareContent tencentShareContent = new TencentWbShareContent(mUMImgBitmap);
			tencentShareContent.setShareContent(shareContent.replaceAll("#snsName#", "tencent"));
			mController.setShareMedia(tencentShareContent);
			
			// ** 其他平台的分享内容.除了上文中已单独设置了分享内容的微信、朋友圈、腾讯微博平台，
			// 设置分享到新浪微博的文字内容
			SinaShareContent sinaShareContent = new SinaShareContent(mUMImgBitmap);
			sinaShareContent.setShareContent(shareContent.replaceAll("#snsName#", "sina"));
			mController.setShareMedia(sinaShareContent);
//			
//			// 设置分享到qq的文字内容
//			QQShareContent qqShareContent = new QQShareContent(
//					shareContent.replaceAll("#snsName#", "qq"));
//			mController.setShareMedia(qqShareContent);
//			
//			// 设置分享到qzone的文字内容
//			QZoneShareContent qZoneShareContent = new QZoneShareContent(
//					shareContent.replaceAll("#snsName#", "qzone"));
//			mController.setShareMedia(qZoneShareContent);
//			
//			// 设置分享到qzone的文字内容
//			DoubanShareContent doubanShareContent = new DoubanShareContent(
//					shareContent.replaceAll("#snsName#", "douban"));
//			mController.setShareMedia(doubanShareContent);
//
//			// 设置分享到renren的文字内容
//			RenrenShareContent renrenShareContent = new RenrenShareContent(
//					shareContent.replaceAll("#snsName#", "renren"));
//			mController.setShareMedia(renrenShareContent);
			// 短信
			SmsShareContent smsShareContent =new SmsShareContent(mUMImgBitmap);
			smsShareContent.setShareContent(shareContent.replaceAll("#snsName#", "sms"));
			mController.setShareMedia(smsShareContent);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mController;
	}

	

	/**
	 * demo 中需要和侧边栏配置联动，所以使用代理方式获取Config 实例。
	 * 
	 * @return
	 */
	public final static SocializeConfig getSocialConfig(Context context) {
		SocializeConfig config = SocializeConfig.getSocializeConfig();
		WeakReference<SocializeConfig> ref = new WeakReference<SocializeConfig>(config);
		wCigs.add(ref);
		
		// 不支持的渠道剔除
		List<SHARE_MEDIA> mediasList = getSupportPlatformsMap().get(PLATFORMS_DONT_SUPPORT);
		for(SHARE_MEDIA media:mediasList){
			config.removePlatform(media);
		}
		
		// 可支持的第三方渠道
		mediasList = getSupportPlatformsMap().get(PLATFORMS_SUPPORT);
		config.setPlatforms(mediasList.toArray(new SHARE_MEDIA[mediasList.size()]));
		
		config.supportAppPlatform(context, SHARE_MEDIA.FACEBOOK, DESCRIPTOR,SUPPORT_FACEBOOK);
		config.supportAppPlatform(context, SHARE_MEDIA.TWITTER, DESCRIPTOR,SUPPORT_TWITTER);
		config.supportAppPlatform(context, SHARE_MEDIA.GOOGLEPLUS, DESCRIPTOR,SUPPORT_GOOGLE);
		
		return config;
	}
	
	public synchronized final static void nofifyConfigChange(Context context){
		 Set<WeakReference<SocializeConfig>> deltable = new HashSet<WeakReference<SocializeConfig>>();
		for(WeakReference<SocializeConfig> ref : wCigs){
			SocializeConfig cig = ref.get();
			if(cig != null){
				cig.setPlatforms(getSupportPlatforms());
				
				cig.supportAppPlatform(context, SHARE_MEDIA.FACEBOOK, DESCRIPTOR,SUPPORT_FACEBOOK);
				cig.supportAppPlatform(context, SHARE_MEDIA.TWITTER, DESCRIPTOR,SUPPORT_TWITTER);
				cig.supportAppPlatform(context, SHARE_MEDIA.GOOGLEPLUS,DESCRIPTOR, SUPPORT_GOOGLE);
			}else
				deltable.add(ref);
		}
		
		for(WeakReference<SocializeConfig> ref : deltable){
			if(wCigs.contains(ref))
				wCigs.remove(ref);
		}
		
		UMSocialService umSocialService = UMServiceFactory.getUMSocialService("no private config", RequestType.SOCIAL);
		SocializeConfig config = umSocialService.getConfig();
		config.setPlatforms(getSupportPlatforms());
		umSocialService.setGlobalConfig(config);
		
		
	}
	
	/**
	 * 	可支持的社交分享渠道
	 * @return
	 */
	public static final Map<String,List<SHARE_MEDIA>> getSupportPlatformsMap(){
		Map<String,List<SHARE_MEDIA>> platformsMap = new HashMap<String,List<SHARE_MEDIA>>();
		
		// 可支持，不支持的分享渠道
		List<SHARE_MEDIA> supportPlatforms = new ArrayList<SHARE_MEDIA>();
		List<SHARE_MEDIA> noSupportPlatforms = new ArrayList<SHARE_MEDIA>();
		// 各个第三方平台判断
		if (SUPPORT_QZONE) {
			supportPlatforms.add(SHARE_MEDIA.QZONE);
		} else {
			noSupportPlatforms.add(SHARE_MEDIA.QZONE);
		}
		if (SUPPORT_SINA) {
			supportPlatforms.add(SHARE_MEDIA.SINA);
		} else {
			noSupportPlatforms.add(SHARE_MEDIA.SINA);
		}
		if (SUPPORT_TENC) {
			supportPlatforms.add(SHARE_MEDIA.TENCENT);
		} else {
			noSupportPlatforms.add(SHARE_MEDIA.TENCENT);
		}
		if (SUPPORT_RENR) {
			supportPlatforms.add(SHARE_MEDIA.RENREN);
		} else {
			noSupportPlatforms.add(SHARE_MEDIA.RENREN);
		}
		if (SUPPORT_DOUBAN) {
			supportPlatforms.add(SHARE_MEDIA.DOUBAN);
		} else {
			noSupportPlatforms.add(SHARE_MEDIA.DOUBAN);
		}
		if (SUPPORT_LAIWANG) {
			supportPlatforms.add(SHARE_MEDIA.LAIWANG);
		} else {
			noSupportPlatforms.add(SHARE_MEDIA.LAIWANG);
		}
		if (SUPPORT_YIXIN) {
			supportPlatforms.add(SHARE_MEDIA.YIXIN);
		} else {
			noSupportPlatforms.add(SHARE_MEDIA.YIXIN);
		}
		
		platformsMap.put(PLATFORMS_SUPPORT,supportPlatforms);
		platformsMap.put(PLATFORMS_DONT_SUPPORT,noSupportPlatforms);
		
		return platformsMap;
		
	}
	
	/**
	 * 	可支持的社交分享渠道
	 * @return
	 */
	public static final SHARE_MEDIA[] getSupportPlatforms(){
		// 可支持的第三方渠道
		List<SHARE_MEDIA> lists = getSupportPlatformsMap().get(PLATFORMS_SUPPORT);
		return lists.toArray(new SHARE_MEDIA[lists.size()]);
	}
    
    /**
     * @功能描述 :  添加微信平台分享
     * @param mController
     * @param mActivity
     * @return
     */
    public static UMSocialService addWXPlatform(UMSocialService mController,Context mActivity , String contentUrl){
//    	//微信appkey(中新网客户端)
//    	#define APP_KEY_WECHAT      @"wxf83c277ff0152a1c"
		String appId = "wxf83c277ff0152a1c";
		// 微信图文分享必须设置一个url 
		//String contentUrl = "http://www.umeng.com/social";
		if(contentUrl == null || contentUrl.trim().length()==0){
			contentUrl = DEFAULT_URL;
		}
		if (SUPPORT_WX){
			// 添加微信平台
//			mController.getConfig().supportWXPlatform(mActivity, appId, contentUrl);
		}
		
		if (SUPPORT_WXCircle){
			// 支持微信朋友圈
//			mController.getConfig().supportWXCirclePlatform(mActivity, appId, contentUrl) ;
		}
		
		return mController;
    	
    }
    
    
    public static UMSocialService setShareContent2(UMSocialService mController,Context mActivity,String shareContent,String shareImgUrl,String shareUrl,String shareContentWeixin, String weixinTitle){
		// 没有跳转url则使用图片地址
		if(shareUrl == null || shareUrl.trim().length()==0){
			shareUrl = shareImgUrl;
		}

		// 分享标题
		String shareTitle = "分享";
		
		// 分享内容
		mController.setShareContent(shareContent);
		// 判断设置是否分享图片 
		UMImage mUMImgBitmap = null;
		if(shareImgUrl !=null && shareImgUrl.trim().length() > 0){
//			// 要发送的只能为本地文件， 不能为url？
			mUMImgBitmap = new UMImage(mActivity, shareImgUrl);
			if(mUMImgBitmap !=null){
//				mUMImgBitmap.setTitle("图片");
				// target url 必须填写
				mUMImgBitmap.setTargetUrl(shareUrl) ;
//				mController.setShareImage(mUMImgBitmap);
				mController.setShareMedia(mUMImgBitmap);
			}
		}
		
		// 设置分享内容
		try {
			// 设置分享到微信的内容, 图片类型
			if(shareContentWeixin == null || shareContentWeixin.trim().length()==0){
				shareContentWeixin = shareContent;
			}
			WeiXinShareContent weixinContent = new WeiXinShareContent();
			if(mUMImgBitmap != null){
				weixinContent.setShareImage(mUMImgBitmap);
			}
			if(weixinTitle == null || weixinTitle.trim().length() == 0){
				weixinContent.setTitle(shareTitle);
			}else{
				weixinContent.setTitle(weixinTitle);
			}
			
			weixinContent.setShareContent(shareContentWeixin.replaceAll("#snsName#", "wechat_session"));
			if(mUMImgBitmap == null){
				weixinContent.setShareContent(shareContent.replaceAll("#snsName#", "wechat_session"));
			}
			weixinContent.setTargetUrl(shareUrl);
			mController.setShareMedia(weixinContent);
			// 设置朋友圈分享的内容
			CircleShareContent circleShareContent = new CircleShareContent();
			if(mUMImgBitmap != null){
				circleShareContent.setShareImage(mUMImgBitmap);
			}
			circleShareContent.setTitle(weixinTitle);
			circleShareContent.setShareContent(shareContentWeixin.replaceAll("#snsName#", "wechat_timeline"));
			circleShareContent.setTargetUrl(shareUrl);
			mController.setShareMedia(circleShareContent);

			
			// 设置分享到腾讯微博的文字内容
			TencentWbShareContent tencentShareContent = new TencentWbShareContent(mUMImgBitmap);
			tencentShareContent.setShareContent(shareContent.replaceAll("#snsName#", "tencent")+" @来自中新网客户端");
			mController.setShareMedia(tencentShareContent);
			
			// ** 其他平台的分享内容.除了上文中已单独设置了分享内容的微信、朋友圈、腾讯微博平台，
			// 设置分享到新浪微博的文字内容
			SinaShareContent sinaShareContent = new SinaShareContent(mUMImgBitmap);
			sinaShareContent.setShareContent(shareContent.replaceAll("#snsName#", "sina"));
			mController.setShareMedia(sinaShareContent);
//			
//			// 设置分享到qq的文字内容
//			QQShareContent qqShareContent = new QQShareContent(
//					shareContent.replaceAll("#snsName#", "qq"));
//			mController.setShareMedia(qqShareContent);
//			
//			// 设置分享到qzone的文字内容
//			QZoneShareContent qZoneShareContent = new QZoneShareContent(
//					shareContent.replaceAll("#snsName#", "qzone"));
//			mController.setShareMedia(qZoneShareContent);
//			
//			// 设置分享到qzone的文字内容
//			DoubanShareContent doubanShareContent = new DoubanShareContent(
//					shareContent.replaceAll("#snsName#", "douban"));
//			mController.setShareMedia(doubanShareContent);
//
//			// 设置分享到renren的文字内容
//			RenrenShareContent renrenShareContent = new RenrenShareContent(
//					shareContent.replaceAll("#snsName#", "renren"));
//			mController.setShareMedia(renrenShareContent);
			// 短信
			SmsShareContent smsShareContent =new SmsShareContent(mUMImgBitmap);
			smsShareContent.setShareContent(shareContent.replaceAll("#snsName#", "sms"));
			mController.setShareMedia(smsShareContent);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mController;
	}

}
