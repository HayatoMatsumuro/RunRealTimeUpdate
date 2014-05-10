package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.DataBaseRaceInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class RaceDetailActivity extends Activity {
	
	public static String STR_INTENT_RACEID = "raceid";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_racedetail);
        
        // 大会情報取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        DataBaseRaceInfo dbRaceInfo = DataBaseAccess.getRaceInfoByRaceId(getContentResolver(), raceId);

        // 大会名表示
        TextView raceNameTextView = (TextView)findViewById(R.id.id_racedetail_txt_racename);
        raceNameTextView.setText(dbRaceInfo.getRaceName());
        
        // 大会日
        TextView raceDateTextView = (TextView)findViewById(R.id.id_racedetail_txt_racedate);
        raceDateTextView.setText(dbRaceInfo.getRaceDate());
        
        // 開催地
        TextView raceLocationTextView = (TextView)findViewById(R.id.id_racedetail_txt_racelocation);
        raceLocationTextView.setText(dbRaceInfo.getRaceLocation());
        
	}

}
