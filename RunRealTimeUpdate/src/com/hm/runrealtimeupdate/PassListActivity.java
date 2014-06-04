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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passlist);
        
        // 大会名表示
        RaceInfo raceInfo = Logic.getSelectRaceInfo();
        TextView raceNameTextView = (TextView)findViewById(R.id.id_passlist_txt_name);
        raceNameTextView.setText(raceInfo.getRaceName());
        
        // 部門リストを取得する
        List<String> sectionList = Logic.getSectionList();
        
        // リストアダプタを作成
        ListAdapter adapter = (ListAdapter) new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, sectionList );
        
        // リストビューに設定
        ListView listView = (ListView)findViewById(R.id.id_passlist_listview_sectionlist);
        listView.setAdapter(adapter);
        
        // リストビュー短押し
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				
				List<String> sectionList = Logic.getSectionList();
				String section = sectionList.get(position);
				
				Logic.setSelectSection(section);
				
				Intent intent = new Intent(PassListActivity.this, PassListSectionActivity.class);
				// TODO: ここは削除する
				//intent.putExtra(PassListSectionActivity.STR_INTENT_RACEID, m_RaceId);
				//intent.putExtra(PassListSectionActivity.STR_INTENT_SECTION, section);
				startActivity(intent);
			}
        	
        });
        
        // 戻るボタン
        Button backButton = (Button)findViewById(R.id.id_passlist_btn_back);
        backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PassListActivity.this, UpdateListActivity.class);
				startActivity(intent);
				
			}
		});
        
	}

}
