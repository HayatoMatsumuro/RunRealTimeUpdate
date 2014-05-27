package com.hm.runrealtimeupdate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ErrorActivity extends Activity {

	public static final String STR_INTENT_MESSAGE = "message";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error);
		
		// メッセージ取得
		Intent intent = getIntent();
		String message = intent.getStringExtra(STR_INTENT_MESSAGE);
		
		// メッセージ表示
		if( message != null){
			TextView msgTextView = (TextView)findViewById(R.id.id_error_txt_message);
			msgTextView.setText(message);
		}
		
		// メイン画面ボタン
		Button mainButton = (Button)findViewById(R.id.id_error_btn_backstart);
		mainButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(ErrorActivity.this, MainActivity.class);
				startActivity(intent);				
			}
		});
	}

}
