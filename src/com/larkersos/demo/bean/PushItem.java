package com.larkersos.demo.bean;

import java.io.Serializable;

/** ������Ϣ���� **/
public class PushItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
// ���ݸ�ʽ��
{
    "pushlist": [
        {
            "title": "����2",
            "content": "��������2",
            "zwtype": "zw",
            "zwid": "268999",
            "channel": "yl",
            "url": "http://ku.m.chinanews.com/forapp/zw/268999.xml",
            "createdate": "2014-02-11 09:25:26",
            "activetime": "60"
        },
        {
            "title": "����",
            "content": "��������212121",
            "zwtype": "zw",
            "zwid": "267999",
            "channel": "life",
            "url": "http://ku.m.chinanews.com/forapp/zw/267999.xml",
            "createdate": "2014-02-11 09:23:21",
            "activetime": "60"
        }
    ]
}
	 */
	private String title = "";
	private String content = "";
	private String zwtype = "";
	private String channel = "";
	private String url = "";
	private String createdate = "";
	private String activetime = "";
	// �Ƿ��Ѿ����͹���Y�Ĳ���ʾ
	private String hashPush = "N";
	public String getHashPush() {
		return hashPush;
	}

	public void setHashPush(String hashPush) {
		this.hashPush = hashPush;
	}

	private String zwid = "";
	public String getZwid() {
		return zwid;
	}

	public void setZwid(String zwid) {
		this.zwid = zwid;
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

	public String getZwtype() {
		return zwtype;
	}

	public void setZwtype(String zwtype) {
		this.zwtype = zwtype;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getActivetime() {
		return activetime;
	}

	public void setActivetime(String activetime) {
		this.activetime = activetime;
	}

}
