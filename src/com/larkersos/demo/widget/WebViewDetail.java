package com.larkersos.demo.widget;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larkersos.demo.BaseActivity;
import com.larkersos.demo.R;
import com.larkersos.demo.http.RequestByHttpGet;
import com.larkersos.demo.scan.CaptureActivity;
import com.larkersos.demo.utils.JsonUtil;
import com.larkersos.demo.utils.Utils;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("JavascriptInterface")
public class WebViewDetail extends BaseActivity {
	// ** 加载进度 */
	private ProgressDialog progressDialog;
	private WebView webView;
	private WebSettings webSet;
	private RelativeLayout rl_layout;
	private TextView tv_title;
	private ImageButton ib_back;
	private ImageButton ib_close;
	private Button btn_action;

	private String eventUrl; // webView 要打开web页面的url
	private byte[] postData;
	private String eventTitle; // 活动详情页面的标题
	private String eventButtonText; // 活动详情页面的按钮名称
	private String shareMessageWeixin; 	// 活动详情页面用到的预置分享文本,微信渠道独立设置
	private String shareImgUrl;   	//分享图片的地址
	private String shareUrl;   		//分享跳转的地址（如果为空则为图片地址）
	private String eventType; // 活动类型
	private String[] eventInfo;
	private boolean cacheFlag = false;

	// 数据存储
	private static SharedPreferences sp = null;
	// 请求参数map
	HashMap<String, String> paramsMap;

	// 跳转标志flag
	private boolean reLoadFlag = false;
	private int viewType = 1; 			// 显示活动详情网页的页面类型（页面跳转加载页面判断用）1为有标题栏 2为无标题栏；
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_detail);
		ctx = this;
		sp = ctx.getSharedPreferences("cns_app", 0);
		// paramsMap = Utils.getCommonParam(myApp); //获取公共参数
		eventInfo = getIntent().getExtras().getStringArray("PramArry"); // 获取活动参数
		eventUrl = eventInfo[0]; // 获取详情的url
		// 关闭按钮
		ib_close = (ImageButton) findViewById(R.id.ib_webview_close);
		rl_layout = (RelativeLayout) findViewById(R.id.rl_center_eventdetail_titlebar);
		ib_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				
				// 关闭切换动画
				overridePendingTransition(R.anim.tran_pre_in,
						R.anim.tran_pre_out);
			}
		});
		ib_close.setVisibility(View.INVISIBLE);

		// 取得参数MAP  不做eventUrl处理 
		processParam(eventUrl);
		// System.out.println(eventUrl+"url2======");
		
		// 活动页面标题
		if (paramsMap==null){
			paramsMap = new HashMap<String, String>(); 
		}
		if (paramsMap.containsKey("title")) {
			eventTitle = paramsMap.get("title");
			if (paramsMap.containsKey("type")) {
				eventType = paramsMap.get("type");
			}
			if (paramsMap.containsKey("name")) {
				eventButtonText = paramsMap.get("name");
			}
			initView(eventTitle, eventButtonText);
		} else {
			initView();
		}

		webView = (WebView) findViewById(R.id.wv_center_webcontent);

		// 判断是否有网络
		boolean b = Utils.checkInternetConnect(getApplicationContext());
		if (!b) {
			// 没有网络优先使用缓存：
			webView.getSettings().setCacheMode(
					WebSettings.LOAD_CACHE_ELSE_NETWORK);
		} else {
			// 判断是否有网络，有的话，使用LOAD_NO_CACHE
			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		}
		if(getIntent().getExtras().getString("post") == null){
			webView.loadUrl(processParam(eventUrl));
		}else{
			postData = getIntent().getExtras().getByteArray("postData");
			webView.postUrl(processParam(eventUrl), postData);
		}
		
		Log.d("fullcircle", "oncreate");
		initSet();
	}

	/**
	 * 初始化无按钮布局
	 */
	private void initView() {
		ib_close.setVisibility(View.VISIBLE);
		rl_layout.setVisibility(View.GONE);
	}

	/**
	 * 初始化页面布局
	 * 
	 * @param title
	 * @param url
	 * @param buttonText
	 */
	private void initView(String title, String buttonText) {
		ib_close.setVisibility(View.INVISIBLE);
		btn_action = (Button) findViewById(R.id.bt_center_eventdetail_button);
		btn_action.setText(eventButtonText);
		btn_action.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean b = Utils.checkInternetConnect(getApplicationContext());
				if (!b) {
					Toast.makeText(getApplicationContext(), "亲！网络不给力呦！",
							Toast.LENGTH_SHORT).show();
				} else {
					// Log.d("fullcircle", eventType);
					if ("share".equals(eventType)) {
						// 弹出分享页面
						onShareClick(v);
					} else if ("invite".equals(eventType)) {
						webView.loadUrl("javascript:SubAppEvents()");
					} else if ("button".equals(eventType)) {
						// TODO Auto-generated method stub
						webView.loadUrl("javascript:SubAppEvents()");
					}
				}
			}
		});

		if (eventType == null || eventType.trim().length() == 0
				|| eventButtonText == null
				|| eventButtonText.trim().length() == 0) {
			btn_action.setVisibility(View.INVISIBLE);
		}
		tv_title = (TextView) findViewById(R.id.tv_center_eventdetail_title);
		tv_title.setText(title);
		ib_back = (ImageButton) findViewById(R.id.ib_center_eventdetail_back);
		ib_back.setOnClickListener(new MyOnclickListener());
	}

	// 初始化设置
	private void initSet() {
		webSet = webView.getSettings();
		webSet.setSupportZoom(true);// 是否支持缩放
		// webSet.setDefaultZoom(ZoomDensity.MEDIUM);//设置默认的缩放级别
		webSet.setJavaScriptEnabled(true);// 是否支持javaScript
		webSet.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);// 设置布局样式
		webSet.setTextSize(TextSize.SMALLER); // 设置文字的大小
		// webSet.setBuiltInZoomControls(true);//打开自带的缩放按钮
		webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
//				System.out.println("onTouch==========");
				return false;
			}
		});// TestBug

		webView.setWebViewClient(webViewClient);

		webView.setWebChromeClient(webChromeClient);
		//
		webView.addJavascriptInterface(new Object() {
			public void callFromJS(String msg) {
				showToast(msg);
			}
		}, "Test");

	}

	private WebViewClient webViewClient = new WebViewClient() {
		String paramUrl;
		String paramDES;
		int paramType = -1;
		String paramNeedLogin;

		// HashMap<String, String> paramsMap;

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// Log.d("fullcircle", "shouldOverrideUrlLoading===="+url);
			// System.out.println("url::" + url);
			// ①获取公共参数 添加到参数map中
			// paramsMap = Utils.getCommonParam(myApp);
			// ②解析url, 更新参数map, 并更新 url
			boolean b = Utils.checkInternetConnect(getApplicationContext());
			if (!b) {
				Toast.makeText(getApplicationContext(), "亲！网络不给力呦！",
						Toast.LENGTH_SHORT).show();
				return true;
			} else {
				url = processParam(url);

				// 事件参数处理
				if (paramsMap.containsKey("eventType")) {
					// 扫一扫
					if("captureSolecode".equals(paramsMap.get("eventType"))){
						Intent intent = new Intent(ctx, CaptureActivity.class);
										startActivity(intent);
										//EventDetail.this.overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
					}else if("share".equals(paramsMap.get("eventType"))){
						// 弹出分享页面
						onShareClick(view);
					}
				}
				if (paramsMap.containsKey("title")) {
					eventTitle = paramsMap.get("title");
					if (eventTitle.length() > 20) {
						try {
							eventTitle = URLDecoder.decode(eventTitle, "utf-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (paramsMap.containsKey("type")) {
						eventType = paramsMap.get("type");
					}
					if (paramsMap.containsKey("name")) {
						eventButtonText = paramsMap.get("name");
						if (eventButtonText.length() > 8) {
							try {
								eventButtonText = URLDecoder.decode(
										eventButtonText, "utf-8");
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					viewType = 1; // 有标题栏
					initView(eventTitle, eventButtonText);
					rl_layout.setVisibility(View.VISIBLE);
				}

				// 默认方式webview
				paramType = 1;
				if (paramsMap.containsKey("type")) {
					try {
						paramType = Integer.parseInt(paramsMap.get("type"));
					} catch (Exception e) {
					}
				}
				// 直接调接口
				if (paramType == 0) {
					paramUrl = paramsMap.get("url");
					sendPost(paramUrl, paramsMap);
				} else if (paramType == 1) {
					// webview
					view.loadUrl(url);
				} else if (paramType == 2) {
					// TODO 2.调用跳转，使用浏览器
					Uri uri = Uri.parse(url);
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(uri);
					startActivity(intent);
				} else {
					// webview
					view.loadUrl(url);
				}
				return true;
			}
		}

		private String sendPost(final String paramUrl2,
				Map<String, String> paramsMap2) {
			boolean b = Utils.checkInternetConnect(getApplicationContext());
			if (!b) {
				Toast.makeText(getApplicationContext(),
						getString(R.string.msg_error_newwork), Toast.LENGTH_SHORT)
						.show();

			} else {
				showProgressDialog(getString(R.string.msg_loading_data));
				handler.post(new Runnable() {
					@Override
					public void run() {
						String result = RequestByHttpGet.doGet2(paramUrl2,
								paramsMap);
						Message msg = new Message();
						msg.obj = result;
						handler.sendMessage(msg);
					}
				});
			}
			return null;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				progressDialog.dismiss();
				if (msg.obj != null) {
					try {
						JSONObject json = new JSONObject(msg.obj.toString());
						String message = JsonUtil.getString(json, "message");
						if (message != null && message.trim().length() > 0) {
							Toast.makeText(getApplicationContext(), message,
									Toast.LENGTH_SHORT).show();
						}
						// 正常返回刷新页面
						String resultcode = JsonUtil.getString(json,"resultcode");
						if("0".equals(resultcode) ){
							// 判断处理返回信息
							// 有用需要更新时，刷新当前页面
//							if(myApp.updatedLoginInfo){
//								// 判断是否有postData 暂时未使用
//								if(postData == null){
//									webView.loadUrl(processParam(eventUrl));
//								}else{
//									webView.postUrl(processParam(eventUrl), postData);
//								}
//								
//							}
						}
					} catch (Exception e) {
						// 数据请求失败
						Toast.makeText(getApplicationContext(),
								getString(R.string.msg_error_service),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.msg_error_newwork), Toast.LENGTH_SHORT)
							.show();
				}
			};
		};

//		@Override
//		public void onPageStarted(WebView view, String url, Bitmap favicon) {
//			if (progDlg == null) {
//				progDlg = CustomProgressDialog.createDialog(ctx,
//						myApp.screenWith);
//				progDlg.setMessage("正在加载，请稍候...");
//			}
//			if (progDlg != null)
//				progDlg.show();
//		}

//		@Override
//		public void onPageFinished(WebView view, String url) {
//			if (progDlg != null)
//				progDlg.dismiss();
//		}

		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			view.setVisibility(View.INVISIBLE);
			Toast.makeText(getApplicationContext(), "亲！网络不给力呦！",
					Toast.LENGTH_SHORT).show();
		};
	};

	private WebChromeClient webChromeClient = new WebChromeClient() {

//		@Override
//		public void onProgressChanged(WebView view, int newProgress) {
//			if (progDlg != null)
//				progDlg.setMessage("已经加载 :" + newProgress + " % ,请稍候...");
//		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			AlertDialog.Builder dlg = new AlertDialog.Builder(ctx);
			dlg.setMessage(message);
			dlg.setTitle("提示");
			dlg.setCancelable(false);
			dlg.setPositiveButton(android.R.string.ok,
					new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					});
			dlg.create();
			dlg.show();
			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			AlertDialog.Builder dlg = new AlertDialog.Builder(ctx);
			dlg.setMessage(message);
			dlg.setTitle("提示");
			dlg.setCancelable(true);
			dlg.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					});
			dlg.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.cancel();
						}
					});
			dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					result.cancel();
				}
			});
			dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
				// DO NOTHING
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						result.cancel();
						return false;
					} else
						return true;
				}
			});
			dlg.create();
			dlg.show();
			return true;
		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, JsPromptResult result) {
			final JsPromptResult res = result;
			AlertDialog.Builder dlg = new AlertDialog.Builder(ctx);
			dlg.setMessage(message);
			final EditText input = new EditText(ctx);
			if (defaultValue != null) {
				// input.setText(defaultValue);
			}
			dlg.setView(input);
			dlg.setCancelable(false);
			dlg.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String usertext = input.getText().toString();
							res.confirm(usertext);
						}
					});
			dlg.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							res.cancel();
						}
					});
			dlg.create();
			dlg.show();
			return true;
		}
	};

	private void showToast(String string) {
		Toast.makeText(this, string, 1).show();

	}
	private class MyOnclickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		}
	}

	@Override
	public void onShareClick(View v) {
		// 设置分享内容
		shareMessage = "";
		shareUrl = "";
		shareTitle = "";
		shareImgUrl = "";
		// 分享
		super.onShareClick(v);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		/** 设置为竖屏 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {// 竖屏：PORTRAIT；横屏：LANDSCAPE
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		// 判断是否有网络
		boolean b = Utils.checkInternetConnect(getApplicationContext());
		if ( !b) {
			// 没有网络优先使用缓存：
			webView.getSettings().setCacheMode(
					WebSettings.LOAD_CACHE_ELSE_NETWORK);
		} else {
			// 判断是否有网络，有的话，使用LOAD_NO_CACHE
			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		}
		// 友盟统计分析
		MobclickAgent.onResume(this);
		if(cacheFlag){
			cacheFlag = false;
			webView.postUrl(processParam(eventUrl), postData);
		}
		if (reLoadFlag) {
			Log.d("fragment", "reload");
			reLoadFlag = false;
			webView.loadUrl(processParam(eventUrl));
		}
	}

	/** 加载进度展示 */
	public void showProgressDialog(String msg) {
		if (progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(msg);
			progressDialog.show();
		}
	}

}
