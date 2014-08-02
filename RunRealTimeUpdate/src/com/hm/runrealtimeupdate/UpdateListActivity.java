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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class UpdateListActivity extends Activity {
	
	public static final String STR_INTENT_RACEID = "raceid";
	
	private static final long LONG_RESENT_TIME = 300000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatelist);
		
		 // 大会情報取得
        Intent intent = getIntent();
        String raceId = intent.getStringExtra( STR_INTENT_RACEID );
        RaceInfo raceInfo = Logic.getRaceInfo( getContentResolver(), raceId);
        
        // 大会情報が取得できないなら、エラー画面
        if( raceInfo == null ){
        	Intent intentErr = new Intent(UpdateListActivity.this, ErrorActivity.class);
        	intentErr.putExtra(ErrorActivity.STR_INTENT_MESSAGE, "大会情報取得に失敗しました。");
        	return;
        }
        
        // 速報データ取得
        List<UpdateInfo> updateInfoList = Logic.getUpdateInfoList( getContentResolver(), raceId, LONG_RESENT_TIME );
        
        // リストビュー設定
        UpdateDataAdapter adapter = new UpdateDataAdapter(this, updateInfoList);
        ListView updateListView = (ListView)findViewById(R.id.id_activity_updatelist_runner_listview);
        updateListView.setAdapter(adapter);
        
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
			
			TextView runnerTextView = (TextView)convertView.findViewById( R.id.id_item_updatedata_runner_textview );
			TextView passTextView = (TextView)convertView.findViewById( R.id.id_item_updatedata_pass_textview );
			TextView splitTextView = (TextView)convertView.findViewById( R.id.id_item_updatedata_split_textview );
			TextView currentTimeTextView = (TextView)convertView.findViewById( R.id.id_item_updatedata_currenttime_textview );
			TextView updateNewTextView = (TextView)convertView.findViewById( R.id.id_item_updatedata_updatenuew_textview );			
        	UpdateInfo updateInfo = getItem(position);
			
        	String runnerStr = updateInfo.getName() + " " + getString(R.string.str_txt_updaterunner);
        	String passStr = updateInfo.getPoint() + " " + getString(R.string.str_txt_updatepass);
        	String splitStr = getString(R.string.str_txt_split) + " " + updateInfo.getSplit();
			String currentTimeStr = getString(R.string.str_txt_currenttime) + " " + updateInfo.getCurrentTime();
        	
        	runnerTextView.setText(runnerStr);
        	passTextView.setText(passStr);
        	splitTextView.setText(splitStr);
        	currentTimeTextView.setText(currentTimeStr);
			
			// New 表示
			if( updateInfo.isRecentFlg()){
				updateNewTextView.setVisibility(View.VISIBLE);
			}else{
				updateNewTextView.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
	}
}
