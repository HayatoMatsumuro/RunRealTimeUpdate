package com.hm.runrealtimeupdate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RaceEntryActivity extends Activity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raceentry);
        
        // �߂�{�^��
        Button backBtn = (Button)findViewById(R.id.id_raceentry_btn_back);
        backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ���o�^��ʑJ��
				Intent intent = new Intent(RaceEntryActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
        
        // URL���̓G�f�B�b�g�{�b�N�X
        EditText urlEdit = (EditText)findViewById(R.id.id_raceentry_edit_inputurl);
        urlEdit.setText(R.string.str_txt_defaulturl);
        urlEdit.setSelection(urlEdit.getText().length());
	}
}
