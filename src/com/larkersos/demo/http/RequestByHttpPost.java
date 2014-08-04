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

/** httpPost���� */
public class RequestByHttpPost {
	public static List<Comment> doPost(String requestUrl, List<NameValuePair> params) {
		System.out.println("requestUrl��" + requestUrl);
		List<Comment> list = new ArrayList<Comment>();
		InputStream inStream = null;// ���淵������
		HttpPost httpPost = new HttpPost(requestUrl);
		Comment comment = new Comment();
		try {
			HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);// �����ַ���
			httpPost.setEntity(entity);// ���ò���
			HttpClient httpClient = new DefaultHttpClient();// ����HttpClient����
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);// �������ӳ�ʱ
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);// ��������ʱ
			HttpResponse httpResponse = httpClient.execute(httpPost);// ��������
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				//ȡ�����ݼ�¼
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
	 * �ύ����
	 * @param requestUrl
	 * @param paramsMap
	 * @return
	 */
	public static String doPost(String requestUrl, HashMap<String,String> paramsMap) {
		// �ж��ύ��ʽ
		if(paramsMap.containsKey("postMethod") && "get".equalsIgnoreCase(paramsMap.get("postMethod"))){
			// get
			return RequestByHttpGet.doGet2(requestUrl,paramsMap);
		}else{
			// post
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// ��������
			for(String key:paramsMap.keySet()){
				params.add(new BasicNameValuePair
						(key, String.valueOf(paramsMap.get(key))));  
			}
			return doPost2(requestUrl, params);
		}
	}
	public static String doPost2(String requestUrl, List<NameValuePair> params) {
		String result = "";
		HttpPost httpPost = new HttpPost(requestUrl);// �½�HttpPost����
		try {
			HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);// �����ַ���
			httpPost.setEntity(entity);// ���ò���
			HttpClient httpClient = new DefaultHttpClient();// ����HttpClient����
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);// �������ӳ�ʱ
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);// ��������ʱ
			HttpResponse httpResponse = httpClient.execute(httpPost);// ��������
			HttpEntity entityRes = httpResponse.getEntity();
			if (httpResponse.getStatusLine().getStatusCode() == 200&&entityRes != null) {
					// ������� 20140325
					String charset = EntityUtils.getContentCharSet(entityRes) == null ? "UTF-8": EntityUtils.getContentCharSet(entityRes);
					result = EntityUtils.toString(httpResponse.getEntity(),charset);
					//  ȥ�����ؽ���е�"\r"�ַ���������ڽ���ַ���������ʾһ��С����  
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
