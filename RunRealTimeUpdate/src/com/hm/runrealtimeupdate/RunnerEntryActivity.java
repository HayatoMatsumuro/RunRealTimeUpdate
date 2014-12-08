package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.LogicException;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.RunnerInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

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
        TextView messageTextView = ( TextView )findViewById(R.id.id_activity_runnerentry_body_message_norunner_textview);
        if( runnerNum >= INT_RUNNER_NUM_MAX ){
        	// 最大を上回っていたら、メッセージを表示
        	contentsLayout.setVisibility( View.GONE );
        	messageLayout.setVisibility( View.VISIBLE );
        	messageTextView.setText( getString( R.string.str_msg_runnerfull ) );
        }
        
        // 速報中なら、メッセージを表示する
        if( raceInfo.isRaceUpdate() ){
        	contentsLayout.setVisibility( View.GONE );
        	messageLayout.setVisibility( View.VISIBLE );
        	messageTextView.setText( getString( R.string.str_msg_runnerupdateexe ) );
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
        Button decideButton = (Button)findViewById( R.id.id_activity_runnerentry_body_contents_numberform_decide_button );
        decideButton.setTag(raceInfo);
        decideButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 大会情報取得
				RaceInfo raceInfo = (RaceInfo)v.getTag();
				
				// ゼッケンNo.取得
				// URL入力エディットボックスから入力値取得
				EditText noEdit = (EditText)findViewById( R.id.id_activity_runnerentry_body_contents_numberform_number_edittext );
				String number = noEdit.getText().toString();
				
				if( number == null || number.equals("")){
					Toast.makeText(RunnerEntryActivity.this, "ゼッケン番号を入力してください。", Toast.LENGTH_SHORT).show();
					return;
				}
				// 選手情報取得タスク起動
				RunnerInfoLoaderTask task = new RunnerInfoLoaderTask(raceInfo);
				
				RunnerInfoLoaderTask.TaskParam param = task.new TaskParam();
				param.setUrl( getString( R.string.str_txt_defaulturl ) );
				param.setRaceId( raceInfo.getRaceId() );
				param.setNumber( number );
				task.execute( param );
			}
		});
        
        // 検索ボタン
        Button searchButton = (Button)findViewById(R.id.id_activity_runnerentry_body_contens_nameform_search_button );
        searchButton.setTag(raceInfo);
        searchButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 大会情報取得
				RaceInfo raceInfo = (RaceInfo)v.getTag();
				
				// 姓取得
				EditText seiEdit = (EditText)findViewById( R.id.id_activity_runnerentry_body_contents_nameform_sei_edittext );
				
				// 名取得
				EditText meiEdit = (EditText)findViewById( R.id.id_activity_runnerentry_body_contents_nameform_mei_edittext );
				
				// 名前検索タスク起動
				RunnerInfoByNameLoaderTask task = new RunnerInfoByNameLoaderTask();
				RunnerInfoByNameLoaderTask.TaskParam param = task.new TaskParam();
				param.setUrl( getString( R.string.str_txt_defaulturl ) );
				param.setRaceId( raceInfo.getRaceId() );
				param.setSei( seiEdit.getText().toString() );
				param.setMei( meiEdit.getText().toString() );
				task.execute( param );
			}
		});
        
        // 選手リストビュー短押し
        ListView runnerInfoListView = ( ListView )findViewById( R.id.id_activity_runnerentry_body_contents_runnerlist_listview );
        runnerInfoListView.setTag( raceInfo );
        runnerInfoListView.setOnItemClickListener( new OnItemClickListener(){

			@Override
			public void onItemClick( AdapterView<?> parent, View v, int position, long id ){
				
				// 選択した大会情報を取得する
				ListView listView = (ListView)parent;
				RaceInfo raceInfo = ( RaceInfo )listView.getTag();
				
				RunnerInfo runnerInfo = (RunnerInfo)listView.getItemAtPosition(position);
				
				// 選手情報取得タスク起動
				RunnerInfoLoaderTask task = new RunnerInfoLoaderTask(raceInfo);
				RunnerInfoLoaderTask.TaskParam param = task.new TaskParam();
				
				param.setUrl( getString( R.string.str_txt_defaulturl ) );
				param.setRaceId( raceInfo.getRaceId() );
				param.setNumber( runnerInfo.getNumber() );
				task.execute( param );
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
	class RunnerInfoLoaderTask extends AsyncTask<RunnerInfoLoaderTask.TaskParam, Void, RunnerInfo>{
		
		/**
		 * 進捗ダイアログ
		 */
		private ProgressDialog m_ProgressDialog = null;
		
		/**
		 * 大会情報
		 */
		private RaceInfo m_RaceInfo;
		
		public RunnerInfoLoaderTask( RaceInfo raceInfo ){
			super();
			m_RaceInfo = raceInfo;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			// 進捗ダイアログ作成
			m_ProgressDialog = new ProgressDialog( RunnerEntryActivity.this );
			m_ProgressDialog.setTitle( getResources().getString( R.string.str_dialog_title_progress_runnerinfo ) );
			m_ProgressDialog.setMessage( getResources().getString( R.string.str_dialog_msg_get ) );
			m_ProgressDialog.setCancelable( true );
			m_ProgressDialog.setButton( DialogInterface.BUTTON_NEGATIVE, getResources().getString( R.string.str_dialog_msg_cancel ), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					cancel( true );
				}
				
			});
			
			m_ProgressDialog.show();
		}
		
		@Override
		protected RunnerInfo doInBackground(TaskParam... params) {
			
			RunnerInfo runnerInfo = null;
			
			try{
				String url = params[0].getUrl();
				String raceId = params[0].getRaceId();
				String number = params[0].getNumber();
				runnerInfo = Logic.getNetRunnerInfo( url, raceId, number );
			}catch (LogicException e) {
				e.printStackTrace();
			}
			return runnerInfo;
		}
		
		@Override
		protected void onPostExecute(RunnerInfo runnerInfo){
			
			// ダイアログ削除
			if( m_ProgressDialog != null ){
				m_ProgressDialog.dismiss();
			}
			
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
			
			return;
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			
			// ダイアログ削除
			if( m_ProgressDialog != null ){
				m_ProgressDialog.dismiss();
			}

			Toast.makeText(RunnerEntryActivity.this, "選手情報取得をキャンセルしました。", Toast.LENGTH_SHORT).show();
		}
		
		public class TaskParam{
			
			private String url;
			
			private String raceId;
			
			private String number;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public String getRaceId() {
				return raceId;
			}

			public void setRaceId(String raceId) {
				this.raceId = raceId;
			}

			public String getNumber() {
				return number;
			}

			public void setNumber(String number) {
				this.number = number;
			}
		}
	}
	
	class RunnerInfoByNameLoaderTask extends AsyncTask<RunnerInfoByNameLoaderTask.TaskParam, Void, List<RunnerInfo>>{

		/**
		 * 進捗ダイアログ
		 */
		ProgressDialog m_ProgressDialog = null;
		
		// 進捗ダイアログ作成
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			m_ProgressDialog = new ProgressDialog( RunnerEntryActivity.this );
			m_ProgressDialog.setTitle( getResources().getString( R.string.str_dialog_title_progress_namesearch ) );
			m_ProgressDialog.setMessage( getResources().getString( R.string.str_dialog_msg_get ) );
			m_ProgressDialog.setCancelable( true );
			m_ProgressDialog.setButton( DialogInterface.BUTTON_NEGATIVE, getResources().getString( R.string.str_dialog_msg_cancel ), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					cancel( true );
				}
				
			});
			
			m_ProgressDialog.show();
		}
		
		@Override
		protected List<RunnerInfo> doInBackground( TaskParam... params ) {
			
			List<RunnerInfo> runnerInfoList = null;
			
			// 名前から選手情報を検索する
			String url = params[0].getUrl();
			String raceId = params[0].getRaceId();
			String mei = params[0].getMei();
			String sei = params[0].getSei();
			runnerInfoList = Logic.searchRunnerInfoByName( url, raceId, sei, mei );
			return runnerInfoList;
		}
		
		@Override
		protected void onPostExecute( List<RunnerInfo> runnerInfoList ){
			
			// ダイアログ削除
			if( m_ProgressDialog != null ){
				m_ProgressDialog.dismiss();
			}
	        
			ListView runnerInfoListView = (ListView)findViewById(R.id.id_activity_runnerentry_body_contents_runnerlist_listview );
			TextView noSearchNameTextView = ( TextView )findViewById( R.id.id_activity_runnerentry_body_contents_nosearchname_textview );
			
			if( runnerInfoList != null ){
				// リストビュー表示
				RunnerListAdapter adapter = ( RunnerListAdapter )runnerInfoListView.getAdapter();
				
				if( adapter != null ){
					adapter.clear();
				}
				
				adapter = new RunnerListAdapter( RunnerEntryActivity.this, runnerInfoList );
			    runnerInfoListView.setAdapter( adapter );
			    
		        adapter.notifyDataSetChanged();
		        
		        runnerInfoListView.setVisibility( View.VISIBLE );
				noSearchNameTextView.setVisibility( View.GONE );
			}else{
				// 選手なしのメッセージ表示
				runnerInfoListView.setVisibility( View.GONE );
				noSearchNameTextView.setVisibility( View.VISIBLE );
			}
			
			// キーボードを隠す
	        InputMethodManager imm = ( InputMethodManager )getSystemService( Context.INPUT_METHOD_SERVICE );
	        
			EditText seiEdit = ( EditText )findViewById( R.id.id_activity_runnerentry_body_contents_nameform_sei_edittext );
	        imm.hideSoftInputFromWindow( seiEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
	        
	        EditText meiEdit = ( EditText )findViewById( R.id.id_activity_runnerentry_body_contents_nameform_mei_edittext );
	        imm.hideSoftInputFromWindow( meiEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
	        
	        return;
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			
			// ダイアログ削除
			if( m_ProgressDialog != null ){
				m_ProgressDialog.dismiss();
			}

			Toast.makeText(RunnerEntryActivity.this, "名前検索をキャンセルしました。", Toast.LENGTH_SHORT).show();
		}
		
		private class TaskParam{
			
			private String url;
			
			private String raceId;
			
			private String sei;
			
			private String mei;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public String getRaceId() {
				return raceId;
			}

			public void setRaceId(String raceId) {
				this.raceId = raceId;
			}

			public String getSei() {
				return sei;
			}

			public void setSei(String sei) {
				this.sei = sei;
			}

			public String getMei() {
				return mei;
			}

			public void setMei(String mei) {
				this.mei = mei;
			}
		}
	}
	/**
	 * ダイアログのメッセージ作成
	 * @param runnerInfo 選手情報
	 * @return
	 */
	private String createDialogMessage( RunnerInfo runnerInfo ){
		StringBuilder builder = new StringBuilder();
		
		builder.append(getString(R.string.str_txt_runnername));
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
	 * ランナーリストアダプタ
	 * @author Hayato Matsumuro
	 *
	 */
	private class RunnerListAdapter extends ArrayAdapter<RunnerInfo>{

		LayoutInflater inflater;
    	
		public RunnerListAdapter(Context context, List<RunnerInfo> objects) {
			super(context, 0, objects);
			
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			if( convertView == null ){
				convertView = this.inflater.inflate(R.layout.list_item_runnerinfo, parent, false);
			}
			
			RelativeLayout sectionLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_runnerinfo_section_layout );
			RelativeLayout runnerLayout = (RelativeLayout)convertView.findViewById( R.id.id_list_item_runnerinfo_runner_layout );
			
			TextView runnerNameTextView = (TextView)convertView.findViewById( R.id.id_list_item_runnerinfo_runner_name_textview );
			TextView runnerNumberTextView = (TextView)convertView.findViewById( R.id.id_list_item_runnerinfo_runner_number_textview );
			
			RunnerInfo item = getItem(position);
			
			sectionLayout.setVisibility( View.GONE );
			runnerLayout.setVisibility( View.VISIBLE );
			runnerNameTextView.setText( item.getName() );
			runnerNumberTextView.setText( item.getNumber() );
			
			return convertView;
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
