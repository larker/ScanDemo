package com.larkersos.demo.utils;

import org.json.JSONException;
import org.json.JSONObject;


/** utils类 */
public class JsonUtil {
	
	// json对象
	public static JSONObject js = null; 
	
	/**
	 *  初始化对象
	 *  */
	public static void initJSONObject(JSONObject obj){
		js = obj;
	}
	/** 
	 * 从json对象取数据
	 * 
	 *  */
	public static JSONObject getObject(String name){
		return getObject(js,name);
	}
	/** 
	 * 从json对象取数据 
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
	 * 从json对象取数据
	 * 
	 *  */
	public static String getString(String name){
		return getString(js,name);
	}
	/** 
	 * 从json对象取数据 
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
