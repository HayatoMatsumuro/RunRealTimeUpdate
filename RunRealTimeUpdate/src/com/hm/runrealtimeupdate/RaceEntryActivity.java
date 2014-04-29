package com.hm.runrealtimeupdate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RaceEntryActivity extends Activity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raceentry);
        
        // –ß‚éƒ{ƒ^ƒ“
        Button backBtn = (Button)findViewById(R.id.id_raceentry_btn_back);
        backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ‘å‰ï“o˜^‰æ–Ê‘JˆÚ
				Intent intent = new Intent(RaceEntryActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}
}
