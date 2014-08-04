package com.larkersos.demo.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.larkersos.demo.bean.Comment;
import com.larkersos.demo.bean.Item;
import com.larkersos.demo.bean.PushItem;
import com.larkersos.demo.utils.FileCacheUtil;
import com.larkersos.demo.utils.FileUtil;

/** httpGet���� */
public class RequestByHttpGet {
	// �������ӳ�ʱ
	public static int CONNECTION_TIMEOUT = 30000;
	// ��������ʱ
	public static int SO_TIMEOUT = 30000;

    /** �������Ƿ��и��°汾  */
    public static HashMap<String, String> isUpdate(HashMap<String, String> mHashMap,int versionCode,String versionUrl){ 
		if(mHashMap ==null){
			mHashMap = new HashMap<String, String>();
		}
		mHashMap.put("isUpdate", "N");
		InputStream inStream = null;// ���淵������
		HttpGet httpGet = new HttpGet(versionUrl);// �½�HttpGet����
		HttpResponse httpResponse = null;
		try {
			// ����HttpClient���� �������ʱ3S
			HttpClient httpClient = getHttpClient(5000,3000);
			httpResponse = httpClient.execute(httpGet);// ��������
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				//ȡ�����ݼ�¼
				HttpEntity entity = httpResponse.getEntity();
				inStream = entity.getContent();
				if(inStream != null && !"".equals(inStream.toString())){
				      // ����XML�ļ��� ����XML�ļ��Ƚ�С�����ʹ��DOM��ʽ���н���  
			        try{  
//			            mHashMap = ParseXmlService.parseAppVersionXml(inStream);  
			    			// �汾�ж�  
			            if (null != mHashMap && mHashMap.containsKey("version")){  
			            		String serviceCodeStr = mHashMap.get("version");
			            		serviceCodeStr = serviceCodeStr.replaceAll("\\.", "");
			                int serviceCode = Integer.valueOf(serviceCodeStr);  
			                // �汾�ж�  
			                if (serviceCode > versionCode){  
			                		mHashMap.put("isUpdate", "Y");
			                }  
			            }
			            if (null != mHashMap && mHashMap.containsKey("minVersion")){  
		            		String serviceMinCodeStr = mHashMap.get("minVersion");
		            		serviceMinCodeStr = serviceMinCodeStr.replaceAll("\\.", "");
		                int serviceMinCode = Integer.valueOf(serviceMinCodeStr);  
		                // �汾�ж�  
		                if (serviceMinCode > versionCode){  
		                		mHashMap.put("forceUpdate", "Y");
		                }  
		            }  
			        } catch (Exception e){  
			            e.printStackTrace();  
			        } 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				inStream.close();
				inStream = null;
			} catch (Exception e) {}
		}
		return mHashMap;
    }
	
	/**
	 * get��ʽ����
	 * @param requestUrl
	 * @param paramsMap
	 * @return
	 */
	public static String doGet2(String requestUrl,HashMap<String,String> paramsMap) {
		// ��������
		for(String key:paramsMap.keySet()){
			String paramValue = paramsMap.get(key);
			try{
				// ��������
				paramValue = URLEncoder.encode(paramsMap.get(key),"utf-8");
			}catch(Exception e){
				paramValue = paramValue.replaceAll(" ", "");
				e.printStackTrace();
			}
			// ƴ�Ӳ���
			if(requestUrl.indexOf("?") >0){
				requestUrl = requestUrl + "&" + key + "="+ paramValue; 
			}else{
				requestUrl = requestUrl + "?" + key + "="+ paramValue; 
			}
		}
		return doGet(requestUrl);
	}
	/** get��ʽ���� */
	public static String doGet(String requestUrl) {
		String strResult = "";// ���淵������
		HttpGet httpGet = new HttpGet(requestUrl);// �½�HttpGet����
		System.out.println("requestUrl��" + requestUrl);
		HttpResponse httpResponse = null;
		try {
			// ����HttpClient����
			HttpClient httpClient = getHttpClient();
			httpResponse = httpClient.execute(httpGet);// ��������
			HttpEntity entity = httpResponse.getEntity();
			if (httpResponse.getStatusLine().getStatusCode() == 200 &&entity != null ) {
				/* ���������� */
					// ������� 
					String charset = EntityUtils.getContentCharSet(entity) == null ? "UTF-8": EntityUtils.getContentCharSet(entity);
					strResult = EntityUtils.toString(httpResponse.getEntity(),charset);
					//  ȥ�����ؽ���е�"\r"�ַ���������ڽ���ַ���������ʾһ��С����  
					strResult = strResult.replaceAll("\r", "").replace("\n", "");
				
			}else{
				strResult = "500";
			}
		} catch (Exception e) {
			strResult = "501";
			e.printStackTrace();
		}
		return strResult;
	}
	
	/** get��ʽ�����ȡƵ���б� */
	public static List<Item> doGetTopicList(String requestUrl) {
		// ͷͼ��ʹ�û���
		return doGetNewsList(requestUrl,false);
	}
	/** get��ʽ�����ȡƵ���б� */
	public static List<Item> doGetChannelList(String requestUrl) {
		return doGetNewsList(requestUrl);
	}
	
	/** get��ʽ�����ȡ������Ϣ */
	public static List<Item> doGetNewsContent(String requestUrl) {
		return doGetNewsList(requestUrl);
	}
	/** get��ʽ���� */
	public static List<Item> doGetNewsList(String requestUrl) {
		// Ĭ��ʹ�û���
		return doGetNewsList(requestUrl, true);
	}
	/** get��ʽ��ȡ����ͷ��Ϣ **/
	public static String getRequestHeader(String requestUrl){
		String lastModified = "";
		HttpGet httpGet = new HttpGet(requestUrl);// �½�HttpGet����
		HttpResponse httpResponse = null;
		try {
			// ����HttpClient����
			HttpClient httpClient = getHttpClient();
			httpResponse = httpClient.execute(httpGet);// ��������
			lastModified = httpResponse.getLastHeader("Last-Modified").getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("������ʱ��======================================="+lastModified);
		return lastModified;
	}
	/** get��ʽ���� */
	public static List<Item> doGetNewsList(String requestUrl,boolean useCache) {
		// ���淵������
		List<Item> list = new ArrayList<Item>();
		Item item = new Item();
		
		// �ж��Ƿ��л����ļ�
		System.out.println("requestUrl��" + requestUrl);
		// http://ku.m.chinanews.com/forapp/cl/sh/newslist_1.xml
		String cacheFileName = requestUrl;
		File cacheFile = null;
		// �Ƿ�ʹ�û���
		if(useCache){
			cacheFile = FileCacheUtil.getCacheFile(cacheFileName);
		}
		if(cacheFile == null){
			HttpGet httpGet = new HttpGet(requestUrl);// �½�HttpGet����
			HttpResponse httpResponse = null;
			try {
				// ����HttpClient����
				HttpClient httpClient = getHttpClient();
				httpResponse = httpClient.execute(httpGet);// ��������
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					httpResponse.getAllHeaders();
					//ȡ�����ݼ�¼
					BufferedReader br = null;
					StringBuilder sb = new StringBuilder();
			        String readline = "";
			        try{
			            /**
			             * �����룬���Ϊnew InputStreamReader(is, ����).
			             */
			            br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
			            while ((readline = br.readLine()) != null) {
			                sb.append(readline);
			            }
			            br.close();
			            if(sb.length() > 0){
							// д���ļ�
							FileCacheUtil.setFileCache(sb.toString(), cacheFileName);
							//��ȡ�ļ�
							cacheFile = FileCacheUtil.getCacheFile(cacheFileName);
			            }
			        } catch (Exception ie){
			            System.out.println("converts failed.");
			        }finally{
			        		try{
			        			 br.close();
			        		}catch(Exception e){}
			        }

				}else{
					item.setCode("500");
				}
			} catch (Exception e) {
				item.setCode("501");
				e.printStackTrace();
			}
		}
		//��������
		if(cacheFile != null){
//			list = ParseXmlService.parseAppNewsXml(cacheFile);
		}else{
			list.add(item);
		}
		return list;
	}
	
	/** get��ʽ���� */
	public static InputStream doGetInputStream(String requestUrl) {
		InputStream inStream = null;// ���淵������
		HttpGet httpGet = new HttpGet(requestUrl);// �½�HttpGet����
System.out.println("requestUrl��" + requestUrl);
		HttpResponse httpResponse = null;
		try {
			// ����HttpClient����
			HttpClient httpClient = getHttpClient();
			httpResponse = httpClient.execute(httpGet);// ��������
			System.out.println("httpResponse.getStatusLine().getStatusCode()=="+httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				//ȡ�����ݼ�¼
				HttpEntity entity = httpResponse.getEntity();
				inStream = entity.getContent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inStream;
	}
	
	/** get��ʽ���� :��ȡ����*/
	public static List<Comment> doGet3(String requestUrl) {
		List<Comment> list = new ArrayList<Comment>();
		InputStream inStream = null;// ���淵������
		HttpGet httpGet = new HttpGet(requestUrl);// �½�HttpGet����
		System.out.println("requestUrl��" + requestUrl);
		HttpResponse httpResponse = null;
		Comment comment = new Comment();
		try {
			// ����HttpClient����
			HttpClient httpClient = getHttpClient();
			httpResponse = httpClient.execute(httpGet);// ��������
			System.out.println("httpResponse.getStatusLine().getStatusCode()=="+httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				//ȡ�����ݼ�¼
				HttpEntity entity = httpResponse.getEntity();
				inStream = entity.getContent();
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
	
	/** get��ʽ���� :��ȡ������Ϣ*/
	public static List<PushItem> doGetPushList(String requestUrl) {
		List<PushItem> list = null;
		HttpGet httpGet = new HttpGet(requestUrl);// �½�HttpGet����
		//System.out.println("requestUrl��" + requestUrl);
		HttpResponse httpResponse = null;
		try {
			// ����HttpClient���� ����ʱ3S
			HttpClient httpClient = getHttpClient(5000,3000);
			httpResponse = httpClient.execute(httpGet);// ��������
//			System.out.println("httpResponse.getStatusLine().getStatusCode()=="+httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				//ȡ�����ݼ�¼�����json
				String result = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
				if(result.indexOf("-->")>0){
					result = result.substring(result.indexOf("-->")+3);
				}
//				list = ParseJSONService.ParsePushJsonResult(result);
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
		}
		return list;
	}
	
	/** get��ʽ���� :��HttpClient*/
	public static HttpClient  getHttpClient() {
		return getHttpClient(CONNECTION_TIMEOUT,SO_TIMEOUT);
	}
	public static HttpClient  getHttpClient(int connectionTimeout,int soTimeout) {
		HttpClient httpClient = new DefaultHttpClient();// ����HttpClient����
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);// �������ӳ�ʱ
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout);// ��������ʱ
		
		return httpClient;
	}

	/** get��ʽ���� ������Ƶ */
	public static String  doGetVideo(String requestUrl,boolean useCache) {
		String resultCode = ""; //���
		String fileName = requestUrl.substring(requestUrl.lastIndexOf("/"));
		// ��������  
		String mSavePath = "";
        HttpURLConnection conn = null;
        FileOutputStream fos =null;
        InputStream is = null;
        int length = 0;
        int numread = 0;
        if(useCache){
        	File file = new File("/storage/sdcard0"+FileUtil.PRIVATE_DIR+FileUtil.CACHE_DIR_VIDEO, fileName);
        	if(file.exists())
        		return "0";
        }
        try{  
            // ��ô洢��·��  
            mSavePath = FileUtil.getFilePath(FileUtil.CACHE_DIR_VIDEO);  
            URL url = new URL(requestUrl);
            // ��������  
            conn = (HttpURLConnection) url.openConnection();  
            conn.connect();  
            // ��ȡ�ļ���С  
            length = conn.getContentLength();  
            if(length <= 0){
            		resultCode = "501";
            }else{
                // ����������  
                is = conn.getInputStream();  
                File file = new File(mSavePath);  
                // �ж��ļ�Ŀ¼�Ƿ����  
                if (!file.exists()){  
                    file.mkdirs();  
                }  
                File cacheFile = new File(mSavePath, requestUrl.substring(requestUrl.lastIndexOf("/")));  
                fos = new FileOutputStream(cacheFile);  
                // ����  
                byte buf[] = new byte[1024];  
                // д�뵽�ļ���  
                do{  
                    numread = is.read(buf);  
                    if(numread > 0){
	                  
	                    // д���ļ�  
	                    fos.write(buf, 0, numread);
                    }
                } while ( numread > 0);// ���ȡ����ֹͣ����.
            }
        } catch (Exception e){
        		length = -1;
            e.printStackTrace();  
        }  finally{
        		if(length <= 0){
        			// ����ʧ�� 
        			resultCode = "500";
        		}else if (numread <= 0){  
                // �������  
        			resultCode= "0";
            }  
        		try{
        			if(is != null){
        				is.close(); 
        			}
        			if(fos != null){
        				fos.close();
        			}
            } catch (Exception e){
                e.printStackTrace();  
            }
        }
        return resultCode;
	}
}
