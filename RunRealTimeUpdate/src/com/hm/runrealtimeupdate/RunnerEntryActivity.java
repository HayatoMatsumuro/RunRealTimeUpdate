package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.LogicException;
import com.hm.runrealtimeupdate.logic.RunnerInfo;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runnerentry);
        
        // 戻るボタン
        Button backButton =(Button)findViewById(R.id.id_runnerentry_btn_back);
        backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 大会詳細画面遷移
				Intent intent = new Intent(RunnerEntryActivity.this, RaceDetailActivity.class);
				startActivity(intent);
			}
		});
        
        // 決定ボタン
        Button decideButton = (Button)findViewById(R.id.id_runnerentry_btn_decide);
        decideButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String[] params = { null };
				
				// ゼッケンNo取得
				// URL入力エディットボックスから入力値取得
				EditText noEdit = (EditText)findViewById(R.id.id_runnerentry_edit_no);
				params[0] = noEdit.getText().toString();
				
				RunnerInfoLoaderTask task = new RunnerInfoLoaderTask();
				task.execute(params);
			}
		});
	}
	
	/**
	 * 選手情報取得タスク
	 * @author Hayato Matsumuro
	 *
	 */
	class RunnerInfoLoaderTask extends AsyncTask<String, Void, RunnerInfo>{
		

		@Override
		/**
		 * param[0]:ゼッケン番号
		 */
		protected RunnerInfo doInBackground(String... params) {
			
			RunnerInfo runnerInfo = null;
			
			try{
				runnerInfo = Logic.getNetRunnerInfo(params[0]);
			}catch (LogicException e) {
				e.printStackTrace();
			}
			return runnerInfo;
		}
		
		@Override
		protected void onPostExecute(RunnerInfo runnerInfo){
			
			if(runnerInfo == null){
				Toast.makeText(RunnerEntryActivity.this, "選手情報取得に失敗しました。", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Logic.setSelectRunnerInfo(runnerInfo);
			AlertDialog.Builder dialog = new AlertDialog.Builder(RunnerEntryActivity.this);
			dialog.setTitle(getString(R.string.str_dialog_title_runnerentry));
			dialog.setMessage(createDialogMessage(runnerInfo));
			
			dialog.setPositiveButton(getString(R.string.str_dialog_msg_OK), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//RunnerInfo runnerInfo = Logic.getSelectRunnerInfo();
					//if( Logic.checkEntryRunnerId(getContentResolver(), runnerInfo.getNumber())){
						// 登録済みのゼッケン番号
					//	Toast.makeText(RunnerEntryActivity.this, "すでに登録済みです", Toast.LENGTH_SHORT).show();
						
					//} else {
						
						// データベース登録
						//Logic.entryRunnerInfo(getContentResolver(), runnerInfo);
						
						//Toast.makeText(RunnerEntryActivity.this, "登録しました", Toast.LENGTH_SHORT).show();
						
						//Intent intent = new Intent(RunnerEntryActivity.this, RaceDetailActivity.class);
						//startActivity(intent);
						
					//}
				}
			});
			
			dialog.setNegativeButton(getString(R.string.str_dialog_msg_NG), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
				
			});
			dialog.show();
		}
		
		private String createDialogMessage( RunnerInfo runnerInfo ){
			StringBuilder builder = new StringBuilder();
			
			builder.append(getString(R.string.str_txt_racename));
			builder.append(":");
			builder.append(runnerInfo.getName());
			builder.append("\n");
			builder.append(getString(R.string.str_txt_no));
			builder.append(":");
			builder.append(runnerInfo.getNumber());
			builder.append("\n");
			builder.append(getString(R.string.str_txt_section));
			builder.append(":");
			builder.append(runnerInfo.getSection());
			builder.append("\n");
			return builder.toString();
		}
	}

}
