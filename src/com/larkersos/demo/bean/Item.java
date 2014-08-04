package com.larkersos.demo.bean;

import java.io.Serializable;

public class Item implements Serializable {
	private static final long serialVersionUID = 1L;
	//code:区分成功失败或网络连接异常的标识：""：为成功；500：服务器连接异常；501：本地网络异常
	private String code = "";
	//ID
	private String id = "";
	//类型
	private String type = "";
	//标题
	private String title = "";
	//内容
	private String content = "";
	//图片地址
	private String img;
	//缩略图
	private String smimg;
	//新闻url
	private String url = "";
	//wapUrl
	private String wapurl = "";
	//顶图的xml
	private String topic = "";
	//新闻列表的xml
	private String newslist = "";
	//是否有首图
	private String istopic = "";
	//评论数量
	private String bbscount = "";
	//发布时间
	private String pubtime = "";
	//图片1说明|图片2说明|图片3说明
	private String imgnote = "";
	//来源
	private String source = "";
	//评论ID
	private String bbsid = "";
	
	//相关新闻
	private String abnlist = "";
	//浏览量
	private String pv = "";
	//新闻详情描述
	private String desc = "";
	//video url
	private String spaddress = "";
	
	public String getSpaddress() {
		return spaddress;
	}
	public void setSpaddress(String spaddress) {
		this.spaddress = spaddress;
	}
	public String getPv() {
		return pv;
	}
	public void setPv(String pv) {
		this.pv = pv;
	}
	public String getAbnlist() {
		return abnlist;
	}
	public void setAbnlist(String abnlist) {
		this.abnlist = abnlist;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getSmimg() {
		return smimg;
	}
	public void setSmimg(String smimg) {
		this.smimg = smimg;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getNewslist() {
		return newslist;
	}
	public void setNewslist(String newslist) {
		this.newslist = newslist;
	}
	public String getIstopic() {
		return istopic;
	}
	public void setIstopic(String istopic) {
		this.istopic = istopic;
	}
	public String getBbscount() {
		return bbscount;
	}
	public void setBbscount(String bbscount) {
		this.bbscount = bbscount;
	}
	public String getPubtime() {
		return pubtime;
	}
	public void setPubtime(String pubtime) {
		this.pubtime = pubtime;
	}
	public String getImgnote() {
		return imgnote;
	}
	public void setImgnote(String imgnote) {
		this.imgnote = imgnote;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getBbsid() {
		return bbsid;
	}
	public void setBbsid(String bbsid) {
		this.bbsid = bbsid;
	}
	public String getWapurl() {
		return wapurl;
	}
	public void setWapurl(String wapurl) {
		this.wapurl = wapurl;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	@Override
	public String toString() {
		return "Item [id=" + id + ", title=" + title + ", content=" + content
				+ ", img=" + img + ", url=" + url + ", bbscount=" + bbscount
				+ ", pubtime=" + pubtime + "]";
	}
	
}
