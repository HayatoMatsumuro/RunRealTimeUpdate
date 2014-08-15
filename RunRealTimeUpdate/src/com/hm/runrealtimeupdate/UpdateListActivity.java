package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.UpdateInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UpdateListActivity extends Activity {
	
	public static final String STR_INTENT_RACEID = "raceid";
	
	private static final long LONG_RESENT_TIME = 300000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatelist);
		
		// 大会Id取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra( STR_INTENT_RACEID );
        
        // 大会情報が取得できないなら、エラー画面
        if( ( raceId == null ) || ( raceId.equals("")) ){
        	Intent intentErr = new Intent(UpdateListActivity.this, ErrorActivity.class);
        	intentErr.putExtra(ErrorActivity.STR_INTENT_MESSAGE, "大会情報取得に失敗しました。");
        	return;
        }
        
        return;
	}
	
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		// 大会Id取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra( STR_INTENT_RACEID );
        
        RelativeLayout contentsLayout = ( RelativeLayout )findViewById(R.id.id_activity_updatelist_body_contents_layout );
        RelativeLayout messageLayout = ( RelativeLayout )findViewById( R.id.id_activity_updatelist_body_message_layout );
        
        // 速報データ取得
        List<UpdateInfo> updateInfoList = Logic.getUpdateInfoList( getContentResolver(), raceId, LONG_RESENT_TIME );
        
        if( updateInfoList.isEmpty() ){
        	contentsLayout.setVisibility( View.GONE );
        	messageLayout.setVisibility( View.VISIBLE );
        } else {

        	contentsLayout.setVisibility( View.VISIBLE );
        	messageLayout.setVisibility( View.GONE );
        	
        	ListView updateListView = (ListView)findViewById(R.id.id_activity_updatelist_body_contents_update_listview);
        	UpdateDataAdapter adapter = ( UpdateDataAdapter )updateListView.getAdapter();
        	
        	if( adapter != null ){
				adapter.clear();
			}
        	
        	// リストビュー設定
            adapter = new UpdateDataAdapter(this, updateInfoList);
            
            updateListView.setAdapter(adapter);
        }
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
			
			TextView infoTextView = (TextView)convertView.findViewById( R.id.id_list_item_updatedata_updateinfo_info_textview);
			TextView splitTextView = (TextView)convertView.findViewById( R.id.id_list_item_updatedata_sub_timelist_split_textview );
			TextView currentTimeTextView = (TextView)convertView.findViewById( R.id.id_list_item_updatedata_sub_timelist_currenttime_textview );
			RelativeLayout newLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_updatedata_sub_new_layout );			
        	UpdateInfo updateInfo = getItem(position);
			
        	String infoStr = updateInfo.getName() + getString(R.string.str_txt_updaterunner) + updateInfo.getPoint() + " " + getString(R.string.str_txt_updatepass);
        	String splitStr = getString(R.string.str_txt_split) + " " + updateInfo.getSplit();
			String currentTimeStr = getString(R.string.str_txt_currenttime) + " " + updateInfo.getCurrentTime();
        	
        	infoTextView.setText(infoStr);
        	splitTextView.setText(splitStr);
        	currentTimeTextView.setText(currentTimeStr);
			
			// New 表示
			if( updateInfo.isRecentFlg()){
				newLayout.setVisibility(View.VISIBLE);
			}else{
				newLayout.setVisibility(View.GONE);
			}
			return convertView;
		}
	}
}
