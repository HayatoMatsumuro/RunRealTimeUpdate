package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.LogicException;
import com.hm.runrealtimeupdate.logic.RaceInfo;

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
	
	public static final String STR_INTENT_RACEID = "raceid";
	
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
	
	/**
	 * 大会情報取得タスク
	 * @author Hayato Matsumuro
	 *
	 */
	class RaceInfoLoaderTask extends AsyncTask<String, Void, RaceInfo>{

		/**
		 * 
		 * @param String params[0] 大会のURL
		 * @return
		 */
		@Override
		protected RaceInfo doInBackground(String... params) {
			
			RaceInfo raceInfo = null;
			try {
				raceInfo = Logic.getNetRaceInfo(getRaceIdByUrl(params[0]));
				
			} catch (LogicException e) {
				e.printStackTrace();
			}
			return raceInfo;
		}
		@Override
		protected void onPostExecute( RaceInfo result)
		{
			if( result == null ){
				Toast.makeText(RaceEntryActivity.this, "大会情報取得に失敗しました。", Toast.LENGTH_SHORT).show();
				return;
			}
			
			// 選択中の大会情報設定
			Logic.setSelectRaceInfo(result);
			
			AlertDialog.Builder dialog = new AlertDialog.Builder(RaceEntryActivity.this);
			dialog.setTitle(getString(R.string.str_dialog_title_race));
			dialog.setMessage(createDialogMessage(result));
			
			dialog.setPositiveButton(getString(R.string.str_dialog_msg_OK), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int width)
				{
					RaceInfo raceInfo = Logic.getSelectRaceInfo();
					
					if( Logic.checkEntryRaceId(getContentResolver(), raceInfo.getRaceId())){
						// すでに大会が登録済み
						Toast.makeText(RaceEntryActivity.this, "この大会はすでに登録済みです。", Toast.LENGTH_SHORT).show();
					}else{
						// データベース登録
						Logic.entryRaceInfo( getContentResolver(), raceInfo);
						
						Toast.makeText(RaceEntryActivity.this, "登録しました", Toast.LENGTH_SHORT).show();
						
						Intent intent = new Intent( RaceEntryActivity.this, MainActivity.class );
						startActivity(intent);
					}
					
				}
				
				
			});
			
			dialog.setNegativeButton(getString(R.string.str_dialog_msg_NG), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// なにもしない					
				}
				
			});
			
			dialog.show();
		}
		
		/**
		 * 登録ダイアログのメッセージを作成する
		 * @param raceInfo 大会情報
		 * @return メッセージ
		 */
		private String createDialogMessage( RaceInfo raceInfo ){
			
			StringBuilder builder = new StringBuilder();
			builder.append(getString(R.string.str_dialog_msg_name));
			builder.append("\n");
			builder.append(raceInfo.getRaceName());
			builder.append("\n");
			builder.append(getString(R.string.str_dialog_msg_date));
			builder.append("\n");
			builder.append(raceInfo.getRaceDate());
			builder.append("\n");
			builder.append(getString(R.string.str_dialog_msg_location));
			builder.append("\n");
			builder.append(raceInfo.getRaceLocation());
			
			return builder.toString();
		}
		
		/**
		 * URLからRaceIdを取得する
		 * @param url URL
		 * @return
		 */
		private String getRaceIdByUrl( String url ){
			
			// URLからデフォルトのURLを削除
			int defUrlLen = getString(R.string.str_txt_defaulturl).length();
			String raceId = url.substring(defUrlLen, url.length());
			
			// 最後が/だった場合は取り除く
			String lastStr = raceId.substring(raceId.length()-1, raceId.length());
			if( lastStr.equals("/")){
				raceId = raceId.substring(0,raceId.length()-1);
			}
			
			return raceId;
		}
	}
}
