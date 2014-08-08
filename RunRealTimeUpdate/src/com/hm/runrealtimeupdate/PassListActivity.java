package com.hm.runrealtimeupdate;

//TODO:削除予定
import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
        
        // 部門リストを取得する
        //List<String> sectionList = Logic.getSectionList(getContentResolver(), raceId);
        
        // リストアダプタを作成
        //SectionListAdapter adapter = new SectionListAdapter( this, sectionList );
        
        // リストビューに設定
        ListView listView = (ListView)findViewById(R.id.id_activity_passlist_sectionlist_listview);
        //listView.setAdapter(adapter);
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
				
				// 通過情報部門画面に遷移
				(( PassActivityGroup )getParent()).showPassListSectionActivity(raceId, section);
			}
        });
	}
	
	/**
	 * 部門リストアダプタ
	 * @author Hayato Matsumuro
	 *
	 */
	/*
	private class SectionListAdapter extends ArrayAdapter<String>{

		LayoutInflater inflater;
		
		public SectionListAdapter(Context context, List<String> objects) {
			super(context, 0, objects);
			
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if( convertView == null ){
				convertView = this.inflater.inflate( R.layout.list_item_section, parent, false);
			}
			
			TextView sectionTextView = ( TextView )convertView.findViewById(R.id.id_item_section_section_textview);
			
			String section = getItem( position );
			
			sectionTextView.setText( section );
			
			return convertView;
		}
		
		
	}
	*/
}
