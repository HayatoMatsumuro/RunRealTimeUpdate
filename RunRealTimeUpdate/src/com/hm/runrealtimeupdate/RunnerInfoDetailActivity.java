package com.hm.runrealtimeupdate;

import android.app.Activity;
import android.os.Bundle;

public class RunnerInfoDetailActivity extends Activity {

	public static final String STR_INTENT_RACEID = "raceid";
	
	public static final String STR_INTENT_NUMBER = "number";

	public static final String STR_ACTIVITY_ID = "RunnerInfoDetailActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_runnerinfodetail);
		
		//Intent intent = getIntent();
       // String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        
		// 戻るボタン
		/*Button backButton = ( Button )findViewById(R.id.id_activity_runnerinfodetail_contents_back_button);
		backButton.setTag( raceId );
		backButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String raceId = (String)v.getTag();
				
				// 大会詳細画面遷移
				(( RunnerActivityGroup )getParent()).showRunnerListActivity( raceId );
				
			}
		});*/
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		//Intent intent = getIntent();
       // String raceId = intent.getStringExtra(STR_INTENT_RACEID);
       //String number = intent.getStringExtra(STR_INTENT_NUMBER);
        
        //RunnerInfo runnerInfo = Logic.getRunnerInfo(getContentResolver(), raceId, number);
        /*
        // ゼッケン番号
     	TextView numberTextView = (TextView)findViewById(R.id.id_activity_runnerinfodetail_contents_number_textview);
     	numberTextView.setText("No. " + runnerInfo.getNumber());
     	
     	// 選手名
     	TextView nameTextView = (TextView)findViewById(R.id.id_activity_runnerinfodetail_contents_name_textview);
     	nameTextView.setText(runnerInfo.getName());
     		
     	// 部門
     	TextView sectionTextView = (TextView)findViewById(R.id.id_activity_runnerinfodetail_contents_section_textview);
     	sectionTextView.setText(runnerInfo.getSection());
     	
		
		// タイムリスト
		TableLayout tableLayout = (TableLayout)findViewById(R.id.id_activity_runnerinfodetail_contents_timelist_layout);
		
		for( RunnerInfo.TimeList timelist : runnerInfo.getTimeList() ){
			TableRow tableRow = new TableRow(this);
			
			// 地点
			TextView pointTextView = new TextView( this );
			pointTextView.setText(timelist.getPoint());
			
			// スプリット
			TextView splitTextView = new TextView( this );
			splitTextView.setText(timelist.getSplit());
			splitTextView.setGravity(Gravity.CENTER);
			
			// ラップ
			TextView lapTextView = new TextView( this );
    		lapTextView.setText(timelist.getLap());
    		lapTextView.setGravity(Gravity.CENTER);
    		
    		// カレントタイム
    		TextView currentTimeView = new TextView( this );
    		currentTimeView.setText(timelist.getCurrentTime());
    		currentTimeView.setGravity(Gravity.CENTER);
			
    		tableRow.addView(pointTextView);
    		tableRow.addView(splitTextView);
    		tableRow.addView(lapTextView);
    		tableRow.addView(currentTimeView);
    		
    		tableLayout.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT));
		}*/
	}
}
