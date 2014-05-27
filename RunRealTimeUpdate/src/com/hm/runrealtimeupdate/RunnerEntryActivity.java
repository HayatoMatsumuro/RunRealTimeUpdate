package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.parser.ParserException;
import com.hm.runrealtimeupdate.logic.parser.RunnerInfo;
import com.hm.runrealtimeupdate.logic.parser.RunnerInfoParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RunnerEntryActivity extends Activity {

	public static String STR_INTENT_RACEID = "raceid";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runnerentry);
        
        // 大会ID取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        
        // 戻るボタン
        Button backButton =(Button)findViewById(R.id.id_runnerentry_btn_back);
        backButton.setTag(raceId);
        backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 大会詳細画面遷移
				Intent intent = new Intent(RunnerEntryActivity.this, RaceDetailActivity.class);
				intent.putExtra(RaceDetailActivity.STR_INTENT_RACEID, (String)v.getTag());
				startActivity(intent);
			}
		});
        
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
				EditText noEdit = (EditText)findViewById(R.id.id_runnerentry_edit_no);
				params[2] = noEdit.getText().toString();
				
				RunnerInfoLoaderTask task = new RunnerInfoLoaderTask();
				task.execute(params);
			}
		});
	}
	
	class RunnerInfoLoaderTask extends AsyncTask<String, Void, RunnerInfo>{
		
		/**
		 * 大会ID
		 */
		private String m_RaceId;
		
		/**
		 * 選手情報
		 */
		private RunnerInfo m_RunnerInfo;

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
				
				m_RaceId = params[1];
			}catch (ParserException e) {
				e.printStackTrace();
			}
			return runnerInfo;
		}
		
		@Override
		protected void onPostExecute(RunnerInfo info){
			
			if(info == null){
				Toast.makeText(RunnerEntryActivity.this, "選手情報取得に失敗しました。", Toast.LENGTH_SHORT).show();
				return;
			}
			
			m_RunnerInfo = info;
			AlertDialog.Builder dialog = new AlertDialog.Builder(RunnerEntryActivity.this);
			dialog.setTitle(getString(R.string.str_dialog_title_runnerentry));
			dialog.setMessage(createDialogMessage(info));
			
			dialog.setPositiveButton(getString(R.string.str_dialog_msg_OK), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO:二重登録の確認
					
					// データベース登録
					DataBaseAccess.entryRunner(getContentResolver(), m_RaceId, m_RunnerInfo.getNumber(), m_RunnerInfo.getName(), m_RunnerInfo.getSection());
					
					// 入力をクリアする
					EditText noEdit = (EditText)findViewById(R.id.id_runnerentry_edit_no);
					noEdit.setText("");
					
					Toast.makeText(RunnerEntryActivity.this, "登録しました", Toast.LENGTH_SHORT).show();
					
					Intent intent = new Intent(RunnerEntryActivity.this, RaceDetailActivity.class);
					intent.putExtra(RaceDetailActivity.STR_INTENT_RACEID, m_RaceId);
					startActivity(intent);
				}
			});
			
			dialog.setNegativeButton(getString(R.string.str_dialog_msg_NG), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
				
			});
			dialog.show();
		}
		
		private String createDialogMessage( RunnerInfo raceInfo ){
			StringBuilder builder = new StringBuilder();
			
			builder.append(getString(R.string.str_txt_racename));
			builder.append(":");
			builder.append(raceInfo.getName());
			builder.append("\n");
			builder.append(getString(R.string.str_txt_no));
			builder.append(":");
			builder.append(raceInfo.getNumber());
			builder.append("\n");
			builder.append(getString(R.string.str_txt_section));
			builder.append(":");
			builder.append(raceInfo.getSection());
			builder.append("\n");
			return builder.toString();
		}
	}

}
