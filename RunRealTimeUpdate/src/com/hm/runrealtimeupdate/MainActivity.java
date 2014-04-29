package com.hm.runrealtimeupdate;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // ‘å‰ï“o˜^ƒ{ƒ^ƒ“
        // TODO: ‘å‰ï“o˜^”‚ª5ˆÈã‚Ìê‡‚ÍA”ñ•\¦
        
        Button entryBtn = (Button)findViewById(R.id.id_main_btn_entry);
        entryBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ‘å‰ï“o˜^‰æ–Ê‘JˆÚ
				Intent intent = new Intent(MainActivity.this, RaceEntryActivity.class);
				startActivity(intent);
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
