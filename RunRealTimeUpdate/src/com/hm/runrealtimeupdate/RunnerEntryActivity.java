package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.LogicException;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.RunnerInfo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class RunnerEntryActivity extends Activity {
	
	public static final String STR_ACTIVITY_ID = "runnerEntryActivity";
	
	public static final String STR_INTENT_RACEID = "raceid";
	public static final String STR_INTENT_CURRENTTAB = "currenttab";

	/**
	 * 登録できる選手の数
	 */
	private static int INT_RUNNER_NUM_MAX = 30;
	
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
        
        // カレントタブ取得
        int currentTab = intent.getIntExtra( STR_INTENT_CURRENTTAB, RaceTabActivity.INT_INTENT_VAL_CURRENTTAB_DETAIL );
        
        // 選手数
        int runnerNum = Logic.getRunnerInfoList( getContentResolver(), raceId ).size();
        
        RelativeLayout contentsLayout = ( RelativeLayout )findViewById( R.id.id_activity_runnerentry_body_contents_layout );
        RelativeLayout messageLayout = ( RelativeLayout )findViewById( R.id.id_activity_runnerentry_body_message_layout );
        if( runnerNum >= INT_RUNNER_NUM_MAX ){
        	// 最大を上回っていたら、メッセージを表示
        	contentsLayout.setVisibility( View.GONE );
        	messageLayout.setVisibility( View.VISIBLE );
        }else{
        	contentsLayout.setVisibility( View.VISIBLE );
        	messageLayout.setVisibility( View.GONE );
        }
        
        // 戻るボタン
        Button backButton =(Button)findViewById( R.id.id_activity_runnerentry_header_back_button );
        
        BackButtonTag backButtonTag = new BackButtonTag();
        backButtonTag.setRaceId(raceId);
        backButtonTag.setCurrentTab(currentTab);
        backButton.setTag( backButtonTag );
        
        backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				BackButtonTag backButtonTag = ( BackButtonTag )v.getTag();
				
				// 大会画面遷移
				Intent intent = new Intent( RunnerEntryActivity.this, RaceTabActivity.class);
				intent.putExtra(RaceTabActivity.STR_INTENT_RACEID, backButtonTag.getRaceId());
				intent.putExtra(RaceTabActivity.STR_INTENT_CURRENTTAB, backButtonTag.getCurrentTab());
				startActivity(intent);
			}
		});
        
        // 決定ボタン
        Button decideButton = (Button)findViewById( R.id.id_activity_runnerentry_body_numberform_decide_button );
        decideButton.setTag(raceInfo);
        decideButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				String[] params = { null, null, null };
				
				params[0] = getString(R.string.str_txt_defaulturl);
				
				// 大会情報取得
				RaceInfo raceInfo = (RaceInfo)v.getTag();
				params[1] = raceInfo.getRaceId();
				
				// ゼッケンNo.取得
				// URL入力エディットボックスから入力値取得
				EditText noEdit = (EditText)findViewById( R.id.id_activity_runnerentry_body_contents_numberform_number_edittext );
				params[2] = noEdit.getText().toString();
				
				if( params[2] == null || params[2].equals("")){
					Toast.makeText(RunnerEntryActivity.this, "ゼッケン番号を入力してください。", Toast.LENGTH_SHORT).show();
					return;
				}
				// 選手情報取得タスク起動
				RunnerInfoLoaderTask task = new RunnerInfoLoaderTask(raceInfo);
				task.execute(params);
			}
		});
	}
	
	private class BackButtonTag {
		
		private String raceId;

		private int currentTab;
		
		public String getRaceId() {
			return raceId;
		}

		public void setRaceId(String raceId) {
			this.raceId = raceId;
		}

		public int getCurrentTab() {
			return currentTab;
		}

		public void setCurrentTab(int currentTab) {
			this.currentTab = currentTab;
		}
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
			
			RunnerEntryDialogInfo info = new RunnerEntryDialogInfo();
			info.setRaceInfo( m_RaceInfo );
			info.setRunnerInfo( runnerInfo );
			
			// 選手情報ダイアログ表示
			InfoDialog<RunnerEntryDialogInfo> runnerEntryDialogInfo = new InfoDialog<RunnerEntryDialogInfo>( info, new RunnerEntryButtonCallbackImpl() );
			runnerEntryDialogInfo.onDialog(
					RunnerEntryActivity.this,
					getString( R.string.str_dialog_title_runnerentry ),
					createDialogMessage( runnerInfo ),
					getString( R.string.str_dialog_msg_OK ),
					getString( R.string.str_dialog_msg_NG )
			);
		}
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
	
	private class RunnerEntryButtonCallbackImpl implements InfoDialog.ButtonCallback<RunnerEntryDialogInfo>{

		@Override
		public void onClickPositiveButton(DialogInterface dialog, int which, RunnerEntryDialogInfo info) {
			
			if( !Logic.checkEntryRunnerId( getContentResolver(), info.getRaceInfo(), info.getRunnerInfo() )){
				// データベース登録
				Logic.entryRunnerInfo( getContentResolver(), info.getRaceInfo(), info.getRunnerInfo() );
				
				// キーボードを隠す
				EditText numberEdit = (EditText)findViewById( R.id.id_activity_runnerentry_body_contents_numberform_number_edittext );
		        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(numberEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				
				Intent intent = new Intent( RunnerEntryActivity.this, RaceTabActivity.class);
				intent.putExtra( RaceTabActivity.STR_INTENT_RACEID, info.getRaceInfo().getRaceId() );
				intent.putExtra( RaceTabActivity.STR_INTENT_CURRENTTAB, RaceTabActivity.INT_INTENT_VAL_CURRENTTAB_RUNNER );
				startActivity(intent);

				Toast.makeText( RunnerEntryActivity.this, "登録しました", Toast.LENGTH_SHORT).show();
			}else{
				// 登録済みのゼッケン番号
				Toast.makeText( RunnerEntryActivity.this, "すでに登録済みです", Toast.LENGTH_SHORT).show();
			}	
		}

		@Override
		public void onClickNegativeButton(DialogInterface dialog, int which, RunnerEntryDialogInfo info) {
			
		}
		
	}
	
	/**
	 * 選手登録ダイアログ用の情報
	 * @author Hayato Matsumuro
	 *
	 */
	private class RunnerEntryDialogInfo{
		
		private RaceInfo raceInfo;
		
		private RunnerInfo runnerInfo;

		public RaceInfo getRaceInfo() {
			return raceInfo;
		}

		public void setRaceInfo(RaceInfo raceInfo) {
			this.raceInfo = raceInfo;
		}

		public RunnerInfo getRunnerInfo() {
			return runnerInfo;
		}

		public void setRunnerInfo(RunnerInfo runnerInfo) {
			this.runnerInfo = runnerInfo;
		}
	}
}
