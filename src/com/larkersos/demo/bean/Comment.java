package com.larkersos.demo.bean;

import java.io.Serializable;

public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;
	//code:���ֳɹ�ʧ�ܻ����������쳣�ı�ʶ��""��Ϊ�ɹ���500�������������쳣��501�����������쳣
	private String code = "";
	//����id
	private String cmntid;
	//����id
	private String newsid;
	//�û���
	private String username;
	//��������
	private String content;
	//IP��ַ
	private String ip;
	//����ʱ��
	private String posttime;
	//��������
	private String commenttotal = "0";
	//���ű���
	private String newstitle;
	//״̬�룺1-�ɹ���2-ʧ��
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
