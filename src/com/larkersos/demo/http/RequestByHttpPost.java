package com.larkersos.demo.http;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.larkersos.demo.bean.Comment;

/** httpPost请求 */
public class RequestByHttpPost {
	public static List<Comment> doPost(String requestUrl, List<NameValuePair> params) {
		System.out.println("requestUrl：" + requestUrl);
		List<Comment> list = new ArrayList<Comment>();
		InputStream inStream = null;// 保存返回数据
		HttpPost httpPost = new HttpPost(requestUrl);
		Comment comment = new Comment();
		try {
			HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);// 设置字符集
			httpPost.setEntity(entity);// 设置参数
			HttpClient httpClient = new DefaultHttpClient();// 创建HttpClient对象
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);// 设置连接超时
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);// 设置请求超时
			HttpResponse httpResponse = httpClient.execute(httpPost);// 发送请求
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				//取得数据记录
				HttpEntity resultEntity = httpResponse.getEntity();
				inStream = resultEntity.getContent();
//				result = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
			}else{
				comment.setCode("500");
			}
		} catch (Exception e) {
			comment.setCode("501");
			e.printStackTrace();
		}
		if(inStream != null){
//			list = ParseXmlService.parseAppCommentXml(inStream);
		}else{
			list.add(comment);
		}
		return list;
	}
	/**
	 * 提交请求
	 * @param requestUrl
	 * @param paramsMap
	 * @return
	 */
	public static String doPost(String requestUrl, HashMap<String,String> paramsMap) {
		// 判断提交方式
		if(paramsMap.containsKey("postMethod") && "get".equalsIgnoreCase(paramsMap.get("postMethod"))){
			// get
			return RequestByHttpGet.doGet2(requestUrl,paramsMap);
		}else{
			// post
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// 处理数据
			for(String key:paramsMap.keySet()){
				params.add(new BasicNameValuePair
						(key, String.valueOf(paramsMap.get(key))));  
			}
			return doPost2(requestUrl, params);
		}
	}
	public static String doPost2(String requestUrl, List<NameValuePair> params) {
		String result = "";
		HttpPost httpPost = new HttpPost(requestUrl);// 新建HttpPost对象
		try {
			HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);// 设置字符集
			httpPost.setEntity(entity);// 设置参数
			HttpClient httpClient = new DefaultHttpClient();// 创建HttpClient对象
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);// 设置连接超时
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);// 设置请求超时
			HttpResponse httpResponse = httpClient.execute(httpPost);// 发送请求
			HttpEntity entityRes = httpResponse.getEntity();
			if (httpResponse.getStatusLine().getStatusCode() == 200&&entityRes != null) {
					// 处理编码 20140325
					String charset = EntityUtils.getContentCharSet(entityRes) == null ? "UTF-8": EntityUtils.getContentCharSet(entityRes);
					result = EntityUtils.toString(httpResponse.getEntity(),charset);
					//  去掉返回结果中的"\r"字符，否则会在结果字符串后面显示一个小方格  
					result = result.replaceAll("\r", "").replace("\n", "");
			}else{
				result = "500";
				Log.d("doPost2:"+requestUrl + "=getStatusCode:", ""+httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "501";
		}
		Log.d("doPost2:"+requestUrl + "=resutl:", ""+result);

		return result;
	}

}
