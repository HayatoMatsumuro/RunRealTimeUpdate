package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.LogicException;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.RunnerInfo;

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
import android.widget.Toast;

public class RunnerEntryActivity extends Activity {
	
	public static final String STR_ACTIVITY_ID = "runnerEntryActivity";
	
	public static final String STR_INTENT_RACEID = "raceid";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runnerentry);
        
        // 大会情報取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        RaceInfo raceInfo = Logic.getRaceInfo(getContentResolver(), raceId);
        
        // 大会情報が取得できないなら、エラー画面
        if( raceInfo == null ){
        	Intent intentErr = new Intent(RunnerEntryActivity.this, ErrorActivity.class);
        	intentErr.putExtra(ErrorActivity.STR_INTENT_MESSAGE, "大会情報取得に失敗しました。");
        	return;
        }
        
        // 戻るボタン
        Button backButton =(Button)findViewById(R.id.id_runnerentry_btn_back);
        backButton.setTag(raceId);
        backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String raceId = (String)v.getTag();
				
				// 大会詳細画面遷移
				//Intent intent = new Intent(RunnerEntryActivity.this, RaceDetailActivity.class);
				//intent.putExtra(RaceDetailActivity.STR_INTENT_RACEID, raceId);
				//startActivity(intent);
				(( RunnerActivityGroup )getParent()).showRunnerListActivity(raceId);
			}
		});
        
        // 決定ボタン
        Button decideButton = (Button)findViewById(R.id.id_runnerentry_btn_decide);
        decideButton.setTag(raceInfo);
        decideButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				String[] params = { null, null, null };
				
				params[0] = getString(R.string.str_txt_defaulturl);
				
				// 大会情報取得
				RaceInfo raceInfo = (RaceInfo)v.getTag();
				params[1] = raceInfo.getRaceId();
				
				// ゼッケンNo取得
				// URL入力エディットボックスから入力値取得
				EditText noEdit = (EditText)findViewById(R.id.id_runnerentry_edit_no);
				params[2] = noEdit.getText().toString();
				
				// 選手情報取得タスク起動
				RunnerInfoLoaderTask task = new RunnerInfoLoaderTask(raceInfo);
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
		
		private RaceInfo m_RaceInfo;
		
		public RunnerInfoLoaderTask(RaceInfo raceInfo){
			super();
			m_RaceInfo = raceInfo;
		}
		
		@Override
		/**
		 * params[0]:アップデートサイトURL
		 * params[1]:大会ID
		 * params[2]:ゼッケン番号
		 */
		protected RunnerInfo doInBackground(String... params) {
			
			RunnerInfo runnerInfo = null;
			
			try{
				runnerInfo = Logic.getNetRunnerInfo( params[0], params[1], params[2] );
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
			
			// 大会情報ダイアログ表示
			RunnerEntryDialog dialog
				= new RunnerEntryDialog(
						RunnerEntryActivity.this,
						getParent(),
						getContentResolver(),
						m_RaceInfo,
						runnerInfo);
			dialog.onDialog();
		}
	}
	
	/**
	 * 選手情報登録ダイアログ
	 * @author Hayato Matsumuro
	 *
	 */
	private class RunnerEntryDialog{
		
		/**
    	 * コンテキスト
    	 */
    	private Context m_Context;
    	
    	/**
    	 * ダイアログコンテキスト
    	 */
    	private Context m_DialogContext;
    	
    	/**
    	 * コンテントリゾルバ
    	 */
    	private ContentResolver m_ContentResolver;
    	
    	/**
    	 * 大会情報
    	 */
    	private RaceInfo m_RaceInfo;
    	
    	/**
    	 * 選手情報
    	 */
    	private RunnerInfo m_RunnerInfo;
    	
    	/**
    	 * コンストラクタ
    	 * @param context
    	 * @param parentContext
    	 * @param contentResolver
    	 * @param raceInfo
    	 * @param runnerInfo
    	 */
    	RunnerEntryDialog( Context context, Context dialogContext, ContentResolver contentResolver, RaceInfo raceInfo, RunnerInfo runnerInfo){
    		m_Context = context;
    		m_DialogContext = dialogContext;
    		m_ContentResolver = contentResolver;
    		m_RaceInfo = raceInfo;
    		m_RunnerInfo = runnerInfo;
    	}
    	
    	public void onDialog(){
    		AlertDialog.Builder dialog = new AlertDialog.Builder(m_DialogContext);
			dialog.setTitle(getString(R.string.str_dialog_title_runnerentry));
			dialog.setMessage(createDialogMessage(m_RunnerInfo));
			
			dialog.setPositiveButton(getString(R.string.str_dialog_msg_OK), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					if( !Logic.checkEntryRunnerId( m_ContentResolver, m_RaceInfo, m_RunnerInfo )){
						// データベース登録
						Logic.entryRunnerInfo( m_ContentResolver, m_RaceInfo, m_RunnerInfo);
						
						(( RunnerActivityGroup )getParent()).showRunnerListActivity(m_RaceInfo.getRaceId());

						Toast.makeText( m_Context, "登録しました", Toast.LENGTH_SHORT).show();
					}else{
						// 登録済みのゼッケン番号
						Toast.makeText( m_Context, "すでに登録済みです", Toast.LENGTH_SHORT).show();
					}	
				}
			});
			
			dialog.setNegativeButton(getString(R.string.str_dialog_msg_NG), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
				
			});
			dialog.show();
    	}
    	
    	/**
    	 * ダイアログのメッセージ作成
    	 * @param runnerInfo 選手情報
    	 * @return
    	 */
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
