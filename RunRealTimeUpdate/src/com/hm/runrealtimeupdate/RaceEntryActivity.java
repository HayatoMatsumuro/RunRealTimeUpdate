package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.parser.ParserException;
import com.hm.runrealtimeupdate.logic.parser.RaceInfo;
import com.hm.runrealtimeupdate.logic.parser.RunnersUpdateParser;

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

public class RaceEntryActivity extends Activity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raceentry);
        
        // 戻るボタン
        Button backBtn = (Button)findViewById(R.id.id_raceentry_btn_back);
        backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 大会登録画面遷移
				Intent intent = new Intent(RaceEntryActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
        
        // URL入力エディットボックス
        EditText urlEdit = (EditText)findViewById(R.id.id_raceentry_edit_inputurl);
        urlEdit.setText(R.string.str_txt_defaulturl);
        urlEdit.setSelection(urlEdit.getText().length());
        
        // 決定ボタン
        Button decideBtn = (Button)findViewById(R.id.id_raceentry_btn_decide);
        decideBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String[] params = { null, null };
				
				// URL入力エディットボックスから入力値取得
				EditText urlEdit = (EditText)findViewById(R.id.id_raceentry_edit_inputurl);
				params[0] = urlEdit.getText().toString();
				
				RaceInfoLoaderTask task = new RaceInfoLoaderTask();
				task.execute(params);
			}
		});
	}
	
	class RaceInfoLoaderTask extends AsyncTask<String, Void, RaceInfo>{

		@Override
		protected RaceInfo doInBackground(String... params) {
			
			RaceInfo raceInfo = null;
			try {
				raceInfo = RunnersUpdateParser.getRaceInfo(params[0]);
				
			} catch (ParserException e) {
				e.printStackTrace();
			}
			return raceInfo;
		}
		@Override
		protected void onPostExecute(RaceInfo result)
		{
			if( result == null ){
				Toast.makeText(RaceEntryActivity.this, "大会情報取得に失敗しました。", Toast.LENGTH_SHORT).show();
			}
			
			AlertDialog.Builder dialog = new AlertDialog.Builder(RaceEntryActivity.this);
			dialog.setTitle(getString(R.string.str_dialog_title_race));
			dialog.setMessage(createDialogMessage(result));
			
			dialog.setPositiveButton(getString(R.string.str_dialog_msg_OK), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int width)
				{
					// TODO 自動生成されたメソッド・スタブ
					
				}
			});
			
			dialog.setNegativeButton(getString(R.string.str_dialog_msg_NG), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 自動生成されたメソッド・スタブ
					
				}
				
			});
			
			dialog.show();
		}
		
		private String createDialogMessage( RaceInfo raceInfo ){
			
			StringBuilder builder = new StringBuilder();
			builder.append(getString(R.string.str_dialog_msg_name));
			builder.append("\n");
			builder.append(raceInfo.getName());
			builder.append("\n");
			builder.append(getString(R.string.str_dialog_msg_date));
			builder.append("\n");
			builder.append(raceInfo.getDate());
			builder.append("\n");
			builder.append(getString(R.string.str_dialog_msg_location));
			builder.append("\n");
			builder.append(raceInfo.getLocation());
			
			return builder.toString();
		}
	}
}
