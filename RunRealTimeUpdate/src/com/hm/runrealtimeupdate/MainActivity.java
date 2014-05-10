package com.hm.runrealtimeupdate;

import java.util.ArrayList;
import java.util.List;

import com.hm.runrealtimeupdate.logic.DataBaseAccess;
import com.hm.runrealtimeupdate.logic.DataBaseRaceInfo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	/**
	 * 大会情報リスト
	 */
	private List<RaceInfoItem> m_RaceInfoList;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 大会情報
        List<DataBaseRaceInfo> dbRaceInfoList = DataBaseAccess.getAllRaceInfo(getContentResolver());
        m_RaceInfoList = new ArrayList<RaceInfoItem>();
        
        // アダプタ設定
        for( DataBaseRaceInfo dbRaceInfo:dbRaceInfoList){
        	RaceInfoItem item = new RaceInfoItem();
        	item.setRaceId(dbRaceInfo.getRaceId());
        	item.setRaceName(dbRaceInfo.getRaceName());
        	item.setRaceDate(dbRaceInfo.getRaceDate());
        	item.setRaceLocation(dbRaceInfo.getRaceLocation());
        	item.setUpdateFlg(dbRaceInfo.getUpdateFlg());
        	m_RaceInfoList.add(item);
        }
        RaceInfoAdapter raceInfoAdapter = new RaceInfoAdapter( this, m_RaceInfoList);
        ListView raceInfoListView = (ListView)findViewById(R.id.id_main_listview_race);
        raceInfoListView.setAdapter(raceInfoAdapter);
        
        // リストのアイテム短押し
        raceInfoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				RaceInfoItem item = m_RaceInfoList.get(position);
				Intent intent = new Intent( MainActivity.this, RaceDetailActivity.class);
				intent.putExtra(RaceDetailActivity.STR_INTENT_RACEID, item.getRaceDate());
				startActivity(intent);
			}
		});
        
        // 大会登録ボタン
        // TODO: 大会登録数が5以上の場合は、非表示
        
        Button entryBtn = (Button)findViewById(R.id.id_main_btn_entry);
        entryBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 大会登録画面遷移
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
    
    private class RaceInfoAdapter extends ArrayAdapter<RaceInfoItem>{

    	LayoutInflater inflater;
    	
		public RaceInfoAdapter(Context context, List<RaceInfoItem> objects) {
			super(context, 0, objects);
			
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			TextView raceNameTextView;
			TextView raceDateTextView;
			TextView raceLocationTextView;
			
			if( convertView == null ){
				convertView = this.inflater.inflate(R.layout.list_item_raceinfo, parent, false);
				
				raceNameTextView = (TextView)convertView.findViewById(R.id.id_raceinfo_txt_racename);
				raceDateTextView = (TextView)convertView.findViewById(R.id.id_raceinfo_txt_racedate);
				raceLocationTextView = (TextView)convertView.findViewById(R.id.id_raceinfo_txt_racelocation);
				
				RaceInfoItem item = getItem(position);
				
				raceNameTextView.setText(item.getRaceName());
				raceDateTextView.setText(item.getRaceDate());
				raceLocationTextView.setText(item.getRaceLocation());
				
			}
			
			return convertView;
			
		}
    	
    }
    private class RaceInfoItem extends DataBaseRaceInfo{
    }
}
