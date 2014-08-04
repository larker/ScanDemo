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

/** httpGet请求 */
public class RequestByHttpGet {
	// 设置连接超时
	public static int CONNECTION_TIMEOUT = 30000;
	// 设置请求超时
	public static int SO_TIMEOUT = 30000;

    /** 检查软件是否有更新版本  */
    public static HashMap<String, String> isUpdate(HashMap<String, String> mHashMap,int versionCode,String versionUrl){ 
		if(mHashMap ==null){
			mHashMap = new HashMap<String, String>();
		}
		mHashMap.put("isUpdate", "N");
		InputStream inStream = null;// 保存返回数据
		HttpGet httpGet = new HttpGet(versionUrl);// 新建HttpGet对象
		HttpResponse httpResponse = null;
		try {
			// 创建HttpClient对象 广告请求超时3S
			HttpClient httpClient = getHttpClient(5000,3000);
			httpResponse = httpClient.execute(httpGet);// 发送请求
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				//取得数据记录
				HttpEntity entity = httpResponse.getEntity();
				inStream = entity.getContent();
				if(inStream != null && !"".equals(inStream.toString())){
				      // 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析  
			        try{  
//			            mHashMap = ParseXmlService.parseAppVersionXml(inStream);  
			    			// 版本判断  
			            if (null != mHashMap && mHashMap.containsKey("version")){  
			            		String serviceCodeStr = mHashMap.get("version");
			            		serviceCodeStr = serviceCodeStr.replaceAll("\\.", "");
			                int serviceCode = Integer.valueOf(serviceCodeStr);  
			                // 版本判断  
			                if (serviceCode > versionCode){  
			                		mHashMap.put("isUpdate", "Y");
			                }  
			            }
			            if (null != mHashMap && mHashMap.containsKey("minVersion")){  
		            		String serviceMinCodeStr = mHashMap.get("minVersion");
		            		serviceMinCodeStr = serviceMinCodeStr.replaceAll("\\.", "");
		                int serviceMinCode = Integer.valueOf(serviceMinCodeStr);  
		                // 版本判断  
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
	 * get方式请求
	 * @param requestUrl
	 * @param paramsMap
	 * @return
	 */
	public static String doGet2(String requestUrl,HashMap<String,String> paramsMap) {
		// 处理数据
		for(String key:paramsMap.keySet()){
			String paramValue = paramsMap.get(key);
			try{
				// 参数处理
				paramValue = URLEncoder.encode(paramsMap.get(key),"utf-8");
			}catch(Exception e){
				paramValue = paramValue.replaceAll(" ", "");
				e.printStackTrace();
			}
			// 拼接参数
			if(requestUrl.indexOf("?") >0){
				requestUrl = requestUrl + "&" + key + "="+ paramValue; 
			}else{
				requestUrl = requestUrl + "?" + key + "="+ paramValue; 
			}
		}
		return doGet(requestUrl);
	}
	/** get方式请求 */
	public static String doGet(String requestUrl) {
		String strResult = "";// 保存返回数据
		HttpGet httpGet = new HttpGet(requestUrl);// 新建HttpGet对象
		System.out.println("requestUrl：" + requestUrl);
		HttpResponse httpResponse = null;
		try {
			// 创建HttpClient对象
			HttpClient httpClient = getHttpClient();
			httpResponse = httpClient.execute(httpGet);// 发送请求
			HttpEntity entity = httpResponse.getEntity();
			if (httpResponse.getStatusLine().getStatusCode() == 200 &&entity != null ) {
				/* 读返回数据 */
					// 处理编码 
					String charset = EntityUtils.getContentCharSet(entity) == null ? "UTF-8": EntityUtils.getContentCharSet(entity);
					strResult = EntityUtils.toString(httpResponse.getEntity(),charset);
					//  去掉返回结果中的"\r"字符，否则会在结果字符串后面显示一个小方格  
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
	
	/** get方式请求获取频道列表 */
	public static List<Item> doGetTopicList(String requestUrl) {
		// 头图不使用缓存
		return doGetNewsList(requestUrl,false);
	}
	/** get方式请求获取频道列表 */
	public static List<Item> doGetChannelList(String requestUrl) {
		return doGetNewsList(requestUrl);
	}
	
	/** get方式请求获取正文信息 */
	public static List<Item> doGetNewsContent(String requestUrl) {
		return doGetNewsList(requestUrl);
	}
	/** get方式请求 */
	public static List<Item> doGetNewsList(String requestUrl) {
		// 默认使用缓存
		return doGetNewsList(requestUrl, true);
	}
	/** get方式获取请求头信息 **/
	public static String getRequestHeader(String requestUrl){
		String lastModified = "";
		HttpGet httpGet = new HttpGet(requestUrl);// 新建HttpGet对象
		HttpResponse httpResponse = null;
		try {
			// 创建HttpClient对象
			HttpClient httpClient = getHttpClient();
			httpResponse = httpClient.execute(httpGet);// 发送请求
			lastModified = httpResponse.getLastHeader("Last-Modified").getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("最后更新时间======================================="+lastModified);
		return lastModified;
	}
	/** get方式请求 */
	public static List<Item> doGetNewsList(String requestUrl,boolean useCache) {
		// 保存返回数据
		List<Item> list = new ArrayList<Item>();
		Item item = new Item();
		
		// 判断是否有缓存文件
		System.out.println("requestUrl：" + requestUrl);
		// http://ku.m.chinanews.com/forapp/cl/sh/newslist_1.xml
		String cacheFileName = requestUrl;
		File cacheFile = null;
		// 是否使用缓存
		if(useCache){
			cacheFile = FileCacheUtil.getCacheFile(cacheFileName);
		}
		if(cacheFile == null){
			HttpGet httpGet = new HttpGet(requestUrl);// 新建HttpGet对象
			HttpResponse httpResponse = null;
			try {
				// 创建HttpClient对象
				HttpClient httpClient = getHttpClient();
				httpResponse = httpClient.execute(httpGet);// 发送请求
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					httpResponse.getAllHeaders();
					//取得数据记录
					BufferedReader br = null;
					StringBuilder sb = new StringBuilder();
			        String readline = "";
			        try{
			            /**
			             * 若乱码，请改为new InputStreamReader(is, 编码).
			             */
			            br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
			            while ((readline = br.readLine()) != null) {
			                sb.append(readline);
			            }
			            br.close();
			            if(sb.length() > 0){
							// 写入文件
							FileCacheUtil.setFileCache(sb.toString(), cacheFileName);
							//获取文件
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
		//解析数据
		if(cacheFile != null){
//			list = ParseXmlService.parseAppNewsXml(cacheFile);
		}else{
			list.add(item);
		}
		return list;
	}
	
	/** get方式请求 */
	public static InputStream doGetInputStream(String requestUrl) {
		InputStream inStream = null;// 保存返回数据
		HttpGet httpGet = new HttpGet(requestUrl);// 新建HttpGet对象
System.out.println("requestUrl：" + requestUrl);
		HttpResponse httpResponse = null;
		try {
			// 创建HttpClient对象
			HttpClient httpClient = getHttpClient();
			httpResponse = httpClient.execute(httpGet);// 发送请求
			System.out.println("httpResponse.getStatusLine().getStatusCode()=="+httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				//取得数据记录
				HttpEntity entity = httpResponse.getEntity();
				inStream = entity.getContent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inStream;
	}
	
	/** get方式请求 :获取评论*/
	public static List<Comment> doGet3(String requestUrl) {
		List<Comment> list = new ArrayList<Comment>();
		InputStream inStream = null;// 保存返回数据
		HttpGet httpGet = new HttpGet(requestUrl);// 新建HttpGet对象
		System.out.println("requestUrl：" + requestUrl);
		HttpResponse httpResponse = null;
		Comment comment = new Comment();
		try {
			// 创建HttpClient对象
			HttpClient httpClient = getHttpClient();
			httpResponse = httpClient.execute(httpGet);// 发送请求
			System.out.println("httpResponse.getStatusLine().getStatusCode()=="+httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				//取得数据记录
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
	
	/** get方式请求 :获取推送消息*/
	public static List<PushItem> doGetPushList(String requestUrl) {
		List<PushItem> list = null;
		HttpGet httpGet = new HttpGet(requestUrl);// 新建HttpGet对象
		//System.out.println("requestUrl：" + requestUrl);
		HttpResponse httpResponse = null;
		try {
			// 创建HttpClient对象 请求超时3S
			HttpClient httpClient = getHttpClient(5000,3000);
			httpResponse = httpClient.execute(httpGet);// 发送请求
//			System.out.println("httpResponse.getStatusLine().getStatusCode()=="+httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				//取得数据记录后解析json
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
	
	/** get方式请求 :获HttpClient*/
	public static HttpClient  getHttpClient() {
		return getHttpClient(CONNECTION_TIMEOUT,SO_TIMEOUT);
	}
	public static HttpClient  getHttpClient(int connectionTimeout,int soTimeout) {
		HttpClient httpClient = new DefaultHttpClient();// 创建HttpClient对象
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);// 设置连接超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout);// 设置请求超时
		
		return httpClient;
	}

	/** get方式请求 下载视频 */
	public static String  doGetVideo(String requestUrl,boolean useCache) {
		String resultCode = ""; //结果
		String fileName = requestUrl.substring(requestUrl.lastIndexOf("/"));
		// 创建连接  
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
            // 获得存储的路径  
            mSavePath = FileUtil.getFilePath(FileUtil.CACHE_DIR_VIDEO);  
            URL url = new URL(requestUrl);
            // 创建连接  
            conn = (HttpURLConnection) url.openConnection();  
            conn.connect();  
            // 获取文件大小  
            length = conn.getContentLength();  
            if(length <= 0){
            		resultCode = "501";
            }else{
                // 创建输入流  
                is = conn.getInputStream();  
                File file = new File(mSavePath);  
                // 判断文件目录是否存在  
                if (!file.exists()){  
                    file.mkdirs();  
                }  
                File cacheFile = new File(mSavePath, requestUrl.substring(requestUrl.lastIndexOf("/")));  
                fos = new FileOutputStream(cacheFile);  
                // 缓存  
                byte buf[] = new byte[1024];  
                // 写入到文件中  
                do{  
                    numread = is.read(buf);  
                    if(numread > 0){
	                  
	                    // 写入文件  
	                    fos.write(buf, 0, numread);
                    }
                } while ( numread > 0);// 点击取消就停止下载.
            }
        } catch (Exception e){
        		length = -1;
            e.printStackTrace();  
        }  finally{
        		if(length <= 0){
        			// 下载失败 
        			resultCode = "500";
        		}else if (numread <= 0){  
                // 下载完成  
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
