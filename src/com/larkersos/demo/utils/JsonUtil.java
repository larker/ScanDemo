package com.larkersos.demo.utils;

import org.json.JSONException;
import org.json.JSONObject;


/** utils�� */
public class JsonUtil {
	
	// json����
	public static JSONObject js = null; 
	
	/**
	 *  ��ʼ������
	 *  */
	public static void initJSONObject(JSONObject obj){
		js = obj;
	}
	/** 
	 * ��json����ȡ����
	 * 
	 *  */
	public static JSONObject getObject(String name){
		return getObject(js,name);
	}
	/** 
	 * ��json����ȡ���� 
	 * 
	 * */
	public static JSONObject getObject(JSONObject obj,String name){
		JSONObject value = null;
		if(obj != null && obj.has(name)){
			try {
				value = obj.getJSONObject(name);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
	
	/** 
	 * ��json����ȡ����
	 * 
	 *  */
	public static String getString(String name){
		return getString(js,name);
	}
	/** 
	 * ��json����ȡ���� 
	 * 
	 * */
	public static String getString(JSONObject obj,String name){
		String value = "";
		if(obj != null && obj.has(name)){
			try {
				value = obj.getString(name);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
}
