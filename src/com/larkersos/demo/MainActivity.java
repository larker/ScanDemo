package com.larkersos.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.larkersos.demo.catalog.CatalogActivity;
import com.larkersos.demo.scan.CaptureActivity;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// �����ѯ��ť
		Button button = (Button) findViewById(R.id.btn_search);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Do something in response to button click
				// ����ҳ��
				Intent it = new Intent(ctx, CatalogActivity.class);
				startActivity(it);
			}
		});
	}

	/** Called when the user touches the button */
	// �������ɨ��ҳ��
	public void clickScan(View view) {
		// Do something in response to button click
		// ɨһɨҳ��
		Intent it = new Intent(ctx, CaptureActivity.class);
		startActivity(it);
	}
}
