package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PassListActivity extends Activity {
	
	public static final String STR_ACTIVITY_ID = "passListActivity";
	
	public static final String STR_INTENT_RACEID = "raceid";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passlist);
        
        // 大会情報取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        RaceInfo raceInfo = Logic.getRaceInfo(getContentResolver(), raceId);
        
        // 大会情報が取得できないなら、エラー画面
        if( raceInfo == null ){
        	Intent intentErr = new Intent(PassListActivity.this, ErrorActivity.class);
        	intentErr.putExtra(ErrorActivity.STR_INTENT_MESSAGE, "大会情報取得に失敗しました。");
        	return;
        }
        
        // 大会名表示
        TextView raceNameTextView = (TextView)findViewById(R.id.id_passlist_txt_name);
        raceNameTextView.setText(raceInfo.getRaceName());
        
        // 部門リストを取得する
        List<String> sectionList = Logic.getSectionList(getContentResolver(), raceId);
        
        // リストアダプタを作成
        ListAdapter adapter = (ListAdapter) new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, sectionList );
        
        // リストビューに設定
        ListView listView = (ListView)findViewById(R.id.id_passlist_listview_sectionlist);
        listView.setAdapter(adapter);
        listView.setTag(raceId);
        
        // リストビュー短押し
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long arg3) {
				// 大会ID取得
				String raceId = (String)parent.getTag();
				
				// 部門取得
				ListView listView = (ListView)parent;
				String section = (String)listView.getItemAtPosition(position);
				
				Intent intent = new Intent(PassListActivity.this, PassListSectionActivity.class);
				intent.putExtra(PassListSectionActivity.STR_INTENT_RACEID, raceId);
				intent.putExtra(PassListSectionActivity.STR_INTENT_SECTION, section);
				startActivity(intent);
			}
        	
        });
        
        // 戻るボタン
        Button backButton = (Button)findViewById(R.id.id_passlist_btn_back);
        backButton.setTag(raceId);
        backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String raceId = (String)v.getTag();
				
				Intent intent = new Intent(PassListActivity.this, UpdateListActivity.class);
				intent.putExtra(UpdateListActivity.STR_INTENT_RACEID, raceId);
				startActivity(intent);
				
			}
		});
        
	}

}
