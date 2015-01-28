package com.hm.runrealtimeupdate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RaceEntryCityActivity extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_raceentrycity );

		// 戻るボタン
		Button backButton = ( Button )findViewById( R.id.id_activity_raceentrycity_header_back_button );
		backButton.setOnClickListener
		(
			new OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					Intent intent = new Intent( RaceEntryCityActivity.this, RaceEntryActivity.class );
					startActivity( intent );
				}
			}
		);
	}
	
}
