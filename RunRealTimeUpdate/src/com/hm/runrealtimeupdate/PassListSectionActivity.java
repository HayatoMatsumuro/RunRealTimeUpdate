package com.hm.runrealtimeupdate;

import com.hm.runrealtimeupdate.logic.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.DataBaseRaceInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PassListSectionActivity extends Activity {

	public static final String STR_INTENT_RACEID = "raceid";
	
	public static final String STR_INTENT_SECTION = "section";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activite_passlistsection);
		
		Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        String section = intent.getStringExtra(STR_INTENT_SECTION);
        
        DataBaseRaceInfo dbRaceInfo = DataBaseAccess.getRaceInfoByRaceId(getContentResolver(), raceId);
        
        // 大会名表示
        TextView raceNameTextView = (TextView)findViewById(R.id.id_passlistsection_txt_racename);
        raceNameTextView.setText(dbRaceInfo.getRaceName());
        
        // 部門名表示
        TextView sectionTextView = (TextView)findViewById(R.id.id_passlistsection_txt_section);
        sectionTextView.setText(section);
	}

}
