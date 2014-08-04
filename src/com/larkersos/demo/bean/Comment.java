package com.larkersos.demo.bean;

import java.io.Serializable;

public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;
	//code:区分成功失败或网络连接异常的标识：""：为成功；500：服务器连接异常；501：本地网络异常
	private String code = "";
	//评论id
	private String cmntid;
	//新闻id
	private String newsid;
	//用户名
	private String username;
	//评论内容
	private String content;
	//IP地址
	private String ip;
	//评论时间
	private String posttime;
	//评论总数
	private String commenttotal = "0";
	//新闻标题
	private String newstitle;
	//状态码：1-成功；2-失败
	private String status;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCmntid() {
		return cmntid;
	}
	public void setCmntid(String cmntid) {
		this.cmntid = cmntid;
	}
	public String getNewsid() {
		return newsid;
	}
	public void setNewsid(String newsid) {
		this.newsid = newsid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPosttime() {
		return posttime;
	}
	public void setPosttime(String posttime) {
		this.posttime = posttime;
	}
	public String getCommenttotal() {
		return commenttotal;
	}
	public void setCommenttotal(String commenttotal) {
		this.commenttotal = commenttotal;
	}
	public String getNewstitle() {
		return newstitle;
	}
	public void setNewstitle(String newstitle) {
		this.newstitle = newstitle;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
