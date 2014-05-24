package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.List;

import com.hm.runrealtimeupdate.logic.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.DataBaseRaceInfo;
import com.hm.runrealtimeupdate.logic.DataBaseRunnerInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PassListActivity extends Activity {

	public static String STR_INTENT_RACEID = "raceid";
	
	private String m_RaceId;
	
	private List<String> m_SectionList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_passlist);
        
        // 大会ID取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra(STR_INTENT_RACEID);
        
        // 大会名表示
        DataBaseRaceInfo dbRaceInfo = DataBaseAccess.getRaceInfoByRaceId(getContentResolver(), raceId);
        TextView raceNameTextView = (TextView)findViewById(R.id.id_passlist_txt_name);
        raceNameTextView.setText(dbRaceInfo.getRaceName());
        
        // 大会IDから選手情報を取得
        List<DataBaseRunnerInfo> dbRunnerInfoList = DataBaseAccess.getRunnerInfoByRaceId(getContentResolver(), raceId);
        
        // 部門リストを作成する
        m_SectionList = new ArrayList<String>();
        for( DataBaseRunnerInfo info : dbRunnerInfoList){
        	
        	String section = info.getSection();
        	
        	if( m_SectionList.indexOf(section) == -1 ){
        		m_SectionList.add(section);
        	}
        }
        String[] sectionArray = (String[])m_SectionList.toArray(new String[0]);
        
        // リストアダプタを作成
        ListAdapter adapter = (ListAdapter) new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, sectionArray );
        
        // リストビューに設定
        ListView listView = (ListView)findViewById(R.id.id_passlist_listview_sectionlist);
        listView.setAdapter(adapter);
        
        // リストビュー短押し
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				
				String section = m_SectionList.get(position);
				
				Intent intent = new Intent(PassListActivity.this, PassListSectionActivity.class);
				intent.putExtra(PassListSectionActivity.STR_INTENT_RACEID, m_RaceId);
				intent.putExtra(PassListSectionActivity.STR_INTENT_SECTION, section);
				startActivity(intent);
			}
        	
        });
        
	}

}
