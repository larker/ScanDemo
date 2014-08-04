package com.larkersos.demo.scan;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.larkersos.demo.BaseActivity;
import com.larkersos.demo.R;
import com.larkersos.demo.utils.ImageCacheUtil;
import com.larkersos.demo.utils.Utils;
import com.larkersos.demo.widget.CustomDialog;
import com.larkersos.demo.widget.WebViewDetail;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.BitmapLuminanceSource;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.DecodeFormatManager;
import com.zxing.decoding.InactivityTimer;
import com.zxing.view.ViewfinderView;

@SuppressLint("NewApi")
public class CaptureActivity extends BaseActivity  implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	// �Ƿ������ͷ
	//private boolean isShowCamera = false;
	
	private SurfaceView surfaceView;
	private ImageButton cancelScanButton;
	private Button moreChoiceButton;
	private PopupWindow popupWindow;
	// �Ի���
	private CustomDialog dialog1;
	private static final int DIALOG_WIDTH = 280;
	private static final int DIALOG_HEIGHT = 280;
	//����ͷ
	private RelativeLayout rl_camera;
	private FrameLayout fl_camera;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// UI����
		setContentView(R.layout.scan_camera);
		//ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		cancelScanButton = (ImageButton) this.findViewById(R.id.ibn_cancel_scan);
		moreChoiceButton = (Button) this.findViewById(R.id.btn_more_menu);
		rl_camera = (RelativeLayout) this.findViewById(R.id.rl_camera);
		fl_camera = (FrameLayout) this.findViewById(R.id.fl_camera);
		
		hasSurface = false;
		//quit the scan view
		cancelScanButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		moreChoiceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPopWindow();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		initScanState();
		Log.d("fragment", "capture onResume()");
		rl_camera.getBackground().setAlpha(0);
	}

	private void initScanState() {
		surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		inactivityTimer = new InactivityTimer(this);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		vibrate = true;
	}
	protected void showNetErrorWindow(){
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
				R.layout.scan_network_toast, null, true);
		popupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		//popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.showAtLocation(findViewById(R.id.fl_camera),Gravity.CENTER, 0, 0);
		popupWindow.update();
	}

	protected void showPopWindow() {
		// TODO Auto-generated method stub
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
				R.layout.scan_menu_pop, null, true);
		menuView.getBackground().setAlpha(150);
		Button btn_choose_pic = (Button) menuView.findViewById(R.id.btn_choose_file);
		Button btn_cancel_menu = (Button) menuView.findViewById(R.id.btn_qrcode_menu_cancel);
		btn_choose_pic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("fragment", "���ļ�");
				Intent intent = new Intent();  
                /* ����Pictures����Type�趨Ϊimage */  
                intent.setType("image/*");  
                /* ʹ��Intent.ACTION_GET_CONTENT���Action */  
                intent.setAction(Intent.ACTION_GET_CONTENT);   
                /* ȡ����Ƭ�󷵻ر����� */  
                startActivityForResult(intent, 1);  
            
			}
		});
		btn_cancel_menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
		popupWindow = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.showAtLocation(findViewById(R.id.fl_camera),Gravity.BOTTOM, 0, 0);
		popupWindow.update();
	}

	@Override
	protected void onPause() {
		Log.d("fragment", "capture onPause");
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		// ��ͣ������
        inactivityTimer.onPause();
		 // �ر�����ͷ
		CameraManager.get().closeDriver();
		 if (!hasSurface) {
	            SurfaceHolder surfaceHolder = surfaceView.getHolder();
	            surfaceHolder.removeCallback(this);
	            surfaceHolder.getSurface().release();
	        }
		 super.onPause();
	}
	@Override
	protected void onStop() {
		Log.d("fragment", "capture onStop()");
		if (popupWindow != null){
			popupWindow.dismiss();
		}if(dialog1 != null){
			dialog1.dismiss();
		}
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		Log.d("fragment", "capture onDestroy");
		// �ر�����ͷ
		CameraManager.get().closeDriver();
		//isShowCamera = false;
		
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	/**
	 * Handler scan result
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		boolean b = Utils.checkInternetConnect(getApplicationContext());
		if (!b) {
			playBeepSoundAndVibrate();
			final LinearLayout net_error_view = (LinearLayout) findViewById(R.id.scan_error_view);
			net_error_view.setVisibility(View.VISIBLE);
			net_error_view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					net_error_view.setVisibility(View.GONE);
					restartPreviewAfterDelay(1000);
				}
			});
			return;
		}
		inactivityTimer.onActivity();
		dialog1 = new CustomDialog(this, DIALOG_WIDTH, DIALOG_HEIGHT, R.layout.scan_dialog, R.style.Theme_dialog);
		playBeepSoundAndVibrate();
		if(result == null){
			showDialog(null);
			return;
		}
		// ɨ����
		showDialog(result.getText());
	}
	
	// dialog��ʾ
	private void showDialog(String msg) {
		Window dialogWindow = dialog1.getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
		
		if (msg == null || msg.length() == 0) {
			msg = "�޷�ʶ��";
		}
		final String message = msg;
		
		TextView tv_scan_message = (TextView) dialog1
				.findViewById(R.id.tv_scan_message);
		tv_scan_message.setText(msg);
		dialog1.show();
		
		// ����ɨ��
		TextView scan_result = (TextView) dialog1
				.findViewById(R.id.tv_button_scanfail);
		// ȡ����ת������ɨ��
		TextView tv_button_cancel = (TextView) dialog1
				.findViewById(R.id.tv_button_cancel);
		// ��ת
		TextView tv_button_confirm = (TextView) dialog1
				.findViewById(R.id.tv_button_confirm);
		LinearLayout ll_scan_result_button = (LinearLayout) dialog1
				.findViewById(R.id.ll_scan_diag_button_group);
		// �����ҳ�����ӣ���ת
		final String[] resultArray = message.split("#");
		if (resultArray.length>0 && resultArray[0].startsWith("http")){
			scan_result.setVisibility(View.GONE);
			ll_scan_result_button.setVisibility(View.VISIBLE);
		}else{
			scan_result.setVisibility(View.VISIBLE);
			ll_scan_result_button.setVisibility(View.GONE);
		}
		// ����ɨ��
		scan_result.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog1.dismiss();
				// ����ɨ��
				restartPreviewAfterDelay(1000);
			}
		});
		
		// ����ɨ��
		tv_button_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog1.dismiss();
				// ����ɨ��
				restartPreviewAfterDelay(1000);
			}
		});
		
		// ����ɨ��
		tv_button_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog1.dismiss();
				// �����ҳ�����ӣ���ת
				String url = message;
				if (resultArray.length>0 && resultArray[0].startsWith("http")){
					if(resultArray.length>1 && resultArray[1].trim().length()>0){
						url = resultArray[0];
					}
					//��ȡ��������
					Intent intent = new Intent(getApplicationContext(),WebViewDetail.class);
					intent.putExtra("PramArry", new String []{url});
					startActivity(intent);
				}else{
					// ����ɨ��
					restartPreviewAfterDelay(1000);
				}
			}
		});

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
//			if(!isShowCamera){
				CameraManager.get().openDriver(surfaceHolder);
				// ����ͷ��
//				isShowCamera = true;
//			}
		} catch (IOException ioe) {
			return;
		} catch (Exception e) {
			return;
		}
		
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
			// ����ͷ��
//			isShowCamera = true;
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			initCamera(holder);
			hasSurface = true;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}
	 /**
     * �ھ���һ���ӳٺ���������Խ�����һ��ɨ�衣
     * �ɹ�ɨ�����ɵ��ô˷�������׼�������´�ɨ��
     * 
     * @param delayMS
     */
    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        //resetStatusView();
    }

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
        	//��ȡuri��ת������ʵ·��
        	
            Uri uri = data.getData();
           if (uri.toString().startsWith("content://media")){
            String[] proj = { MediaStore.Images.Media.DATA };     
            Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);  
             int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
            actualimagecursor.moveToFirst();  
             String img_path = actualimagecursor.getString(actual_image_column_index);  
             //��ѡ��gifͼƬֱ�ӷ����޷�ʶ��
            if(img_path.endsWith("gif")){
            	Log.d("fragment", "uri===="+uri.toString()+"===="+img_path);
            	Result rawResult = null;
            	 handleDecode(rawResult, null);
            	 return;
            }
           }
           if(uri.toString().startsWith("file://")&&uri.toString().endsWith("gif")){
        	   Result rawResult = null;
          	 handleDecode(rawResult, null);
          	 return;
           }
            ContentResolver cr = this.getContentResolver();  
            Bitmap bitmap = null;
            bitmap = ImageCacheUtil.getResizedBitmap(null, null, this, uri, 200, false);  
            MultiFormatReader multiFormatReader = new MultiFormatReader();

    		// ����Ĳ���
    		Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(
    				2);
    		// ���Խ����ı�������
    		Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
    		if (decodeFormats == null || decodeFormats.isEmpty()) {
    			decodeFormats = new Vector<BarcodeFormat>();

    			// �������ÿ�ɨ������ͣ�������ѡ���˶�֧��
    			decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
    			decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
    			decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
    		}
    		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

    		// ���ü������ַ������ʽΪUTF8
    		// hints.put(DecodeHintType.CHARACTER_SET, "UTF8");

    		// ���ý������ò���
    		multiFormatReader.setHints(hints);

    		// ��ʼ��ͼ����Դ����
    		Result rawResult = null;
    		try {
    			rawResult = multiFormatReader
    					.decodeWithState(new BinaryBitmap(new HybridBinarizer(
    							new BitmapLuminanceSource(bitmap))));
    		} catch (NotFoundException e) {
    			e.printStackTrace();
    		}
    		 handleDecode(rawResult, null);
        }  
       
         
    }  
	
	

}
