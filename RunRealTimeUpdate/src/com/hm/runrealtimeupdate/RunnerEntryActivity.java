package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.parser.ParserException;
import com.hm.runrealtimeupdate.logic.parser.RunnerInfo;
import com.hm.runrealtimeupdate.logic.parser.RunnerInfoParser;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RunnerEntryActivity extends Activity {

	public static String STR_INTENT_RACEID = "raceid";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runnerentry);
        
        // 大会ID取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        
        // 決定ボタン
        Button decideButton = (Button)findViewById(R.id.id_runnerentry_btn_decide);
        decideButton.setTag(raceId);
        decideButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String[] params = { null, null, null };
				
				// URL 設定
				params[0] = getString(R.string.str_txt_defaulturl);
				
				// 大会ID設定
				params[1] = (String)v.getTag();
				
				// ゼッケンNo取得
				// URL入力エディットボックスから入力値取得
				EditText urlEdit = (EditText)findViewById(R.id.id_runnerentry_edit_no);
				params[2] = urlEdit.getText().toString();
				
				RunnerInfoLoaderTask task = new RunnerInfoLoaderTask();
				task.execute(params);
			}
		});
	}
	
	class RunnerInfoLoaderTask extends AsyncTask<String, Void, RunnerInfo>{

		@Override
		/**
		 * param[0]:url
		 * param[1]:raceID
		 * param[2]:no
		 */
		protected RunnerInfo doInBackground(String... params) {
			
			RunnerInfo runnerInfo = new RunnerInfo();
			
			try{
				runnerInfo = RunnerInfoParser.getRunnerInfo(params[0], params[1], params[2]);
			}catch (ParserException e) {
				e.printStackTrace();
			}
			return runnerInfo;
		}
		
	}

}
