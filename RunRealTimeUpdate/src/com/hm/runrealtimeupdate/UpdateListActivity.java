package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;
import com.hm.runrealtimeupdate.logic.UpdateInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class UpdateListActivity extends Activity {
	
	public static final String STR_INTENT_RACEID = "raceid";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatelist);
		
		 // 大会情報取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra( STR_INTENT_RACEID );
        RaceInfo raceInfo = Logic.getRaceInfo( getContentResolver(), raceId);
        
        // TODO:大会IDが削除されている場合を考慮
        
        // 大会名表示
        TextView raceNameTextView = (TextView)findViewById(R.id.id_updatelist_txt_racename);
        raceNameTextView.setText( raceInfo.getRaceName());
        
        // 速報データ取得
        List<UpdateInfo> updateInfoList = Logic.getUpdateInfoList( getContentResolver(), raceId );
        
        // リストビュー設定
        UpdateDataAdapter adapter = new UpdateDataAdapter(this, updateInfoList);
        ListView updateListView = (ListView)findViewById(R.id.id_updatelist_listview_runner);
        updateListView.setAdapter(adapter);
        
        // 大会詳細ボタン
        Button raceDetailButton = (Button)findViewById(R.id.id_updatelist_btn_detail);
        raceDetailButton.setTag(raceId);
        raceDetailButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 大会情報取得
				String raceId = (String)v.getTag();
				
				// 大会詳細画面遷移
				Intent intent = new Intent(UpdateListActivity.this, RaceDetailActivity.class);
				intent.putExtra(RaceDetailActivity.STR_INTENT_RACEID, raceId);
				startActivity(intent);
				
			}
		});
        
        // 通過情報ボタン
        Button passListButton = (Button)findViewById(R.id.id_updatelist_btn_passlist);
        passListButton.setTag(raceId);
        passListButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String raceId = (String)v.getTag();
				
				// 通過情報画面遷移
				Intent intent = new Intent(UpdateListActivity.this, PassListActivity.class);
				intent.putExtra(PassListActivity.STR_INTENT_RACEID, raceId);
				startActivity(intent);
				
			}
		});
        return;
	}
	
	/**
	 * 速報データリストアダプタ
	 * @author Hayato Matsumuro
	 *
	 */
	private class UpdateDataAdapter extends ArrayAdapter<UpdateInfo>{
		
		LayoutInflater inflater;
		
		public UpdateDataAdapter(Context context, List<UpdateInfo> objects) {
			super(context, 0, objects);
			
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if( convertView == null ){
				convertView = this.inflater.inflate(R.layout.list_item_updatedata, parent, false);
			}
			
			TextView mainTextView = (TextView)convertView.findViewById(R.id.id_updatedata_txt_main);
			TextView subTextView = (TextView)convertView.findViewById(R.id.id_updatedata_txt_sub);
			
			
        	
			UpdateInfo updateInfo = getItem(position);
			String mainStr = updateInfo.getNumber() + " " + updateInfo.getSection() + " "+ updateInfo.getName() + " 選手 " + updateInfo.getPoint();
        	String subStr = updateInfo.getSplit();
			mainTextView.setText(mainStr);
			subTextView.setText(subStr);
			
			return convertView;
		}
		
		
	}
}
