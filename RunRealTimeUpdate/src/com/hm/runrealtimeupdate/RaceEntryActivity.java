package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.LogicException;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class RaceEntryActivity extends Activity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raceentry);

        // ヘッダー
        RelativeLayout headerLayout = (RelativeLayout)findViewById(R.id.id_raceentry_relative_header);
        headerLayout.setBackgroundColor(getResources().getColor(R.color.maincolor));
        
        // ボーダー
        // TODO:
        //RelativeLayout borderLayout = (RelativeLayout)findViewById(R.id.id_raceentry_relative_border);
        //borderLayout.setBackgroundColor(getResources().getColor(R.color.subcolor));
        
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
        
        // 決定ボタン
        Button decideBtn = (Button)findViewById(R.id.id_raceentry_btn_decide);
        decideBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String[] params = { null, null };
				
				params[0] = getString(R.string.str_txt_defaulturl);
				
				// URL入力エディットボックスから入力値取得
				EditText urlEdit = (EditText)findViewById(R.id.id_raceentry_edit_inputurl);
				params[1] = formatRaceId(urlEdit.getText().toString());
				
				RaceInfoLoaderTask task = new RaceInfoLoaderTask();
				task.execute(params);
			}
		});
	}
	
	/**
	 * 入力された大会IDのフォーマットをする
	 * @param String inputRaceId
	 * @return
	 */
	private String formatRaceId( String inputRaceId ){
		
		String raceId = inputRaceId;
		
		// 最後が/だった場合は取り除く
		String lastStr = inputRaceId.substring(inputRaceId.length()-1, inputRaceId.length());
		if( lastStr.equals("/")){
			raceId = inputRaceId.substring(0,inputRaceId.length()-1);
		}
		
		return raceId;
	}
	
	/**
	 * 大会情報取得タスク
	 * @author Hayato Matsumuro
	 *
	 */
	class RaceInfoLoaderTask extends AsyncTask<String, Void, RaceInfo>{

		/**
		 * 
		 * @param String params[0] アップデートサイトURL、param[1] 大会ID
		 * @return
		 */
		@Override
		protected RaceInfo doInBackground(String... params) {
			
			RaceInfo raceInfo = null;
			try {
				raceInfo = Logic.getNetRaceInfo( params[0], params[1] );
				
			} catch (LogicException e) {
				e.printStackTrace();
			}
			return raceInfo;
		}
		@Override
		protected void onPostExecute( RaceInfo raceInfo)
		{
			if( raceInfo == null ){
				Toast.makeText(RaceEntryActivity.this, "大会情報取得に失敗しました。", Toast.LENGTH_SHORT).show();
				return;
			}else{
				RaceEntryDialog raceEntryDialog = new RaceEntryDialog( RaceEntryActivity.this, getContentResolver(), raceInfo);
				raceEntryDialog.onDialog();
			}
			
		}
	}
	
	/**
	 * 大会登録ダイアログ
	 * @author Hayato Matsumuro
	 *
	 */
	private class RaceEntryDialog{
		
		/**
    	 * コンテキスト
    	 */
    	private Context m_Context;
    	
    	/**
    	 * コンテントリゾルバ
    	 */
    	private ContentResolver m_ContentResolver;
    	
    	/**
    	 * 大会情報
    	 */
    	private RaceInfo m_RaceInfo;
    	
    	/**
    	 * コンストラクタ
    	 * @param context
    	 * @param contentResolver
    	 * @param raceInfo
    	 */
    	public RaceEntryDialog( Context context, ContentResolver contentResolver, RaceInfo raceInfo ){
    		// 初期化
    		m_Context = context;
    		m_ContentResolver = contentResolver;
    		m_RaceInfo = raceInfo;
    	}
    	
    	public void onDialog(){
    		AlertDialog.Builder dialog = new AlertDialog.Builder(RaceEntryActivity.this);
			dialog.setTitle(getString(R.string.str_dialog_title_race));
			dialog.setMessage(createDialogMessage(m_RaceInfo));
			
			dialog.setPositiveButton(getString(R.string.str_dialog_msg_OK), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int width)
				{
					
					if( Logic.checkEntryRaceId( m_ContentResolver, m_RaceInfo.getRaceId() ) ){
						// すでに大会が登録済み
						Toast.makeText( m_Context, "この大会はすでに登録済みです。", Toast.LENGTH_SHORT).show();
					}else{
						// データベース登録
						Logic.entryRaceInfo( m_ContentResolver, m_RaceInfo );
						
						Toast.makeText( m_Context, "登録しました", Toast.LENGTH_SHORT ).show();
						
						Intent intent = new Intent( m_Context, MainActivity.class );
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
	}
}
