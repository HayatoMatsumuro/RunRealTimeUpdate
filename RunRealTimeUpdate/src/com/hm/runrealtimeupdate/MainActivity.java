package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	/**
	 * 登録できる大会の最大数
	 */
	private static final int INT_RACEINFO_NUM_MAX = 5;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 大会情報
        List<RaceInfo> raceInfoList = Logic.getRaceInfoList(getContentResolver());
        
        RaceListAdapter adapter = new RaceListAdapter( this, raceInfoList);
        ListView raceInfoListView = (ListView)findViewById(R.id.id_main_listview_race);
        raceInfoListView.setAdapter(adapter);
        
        // リストのアイテム短押し
        raceInfoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				
				// 選択した大会情報を設定する
				List<RaceInfo> raceInfoList = Logic.getRaceInfoList(getContentResolver());
				RaceInfo item = raceInfoList.get(position);
				Logic.setSelectRaceInfo( item );
				
				Intent intent = new Intent( MainActivity.this, RaceDetailActivity.class);
				startActivity(intent);
			}
		});
        
        // リストのアイテム長押し
        raceInfoListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) {
				
				// 選択した大会情報を設定する
				List<RaceInfo> raceInfoList = Logic.getRaceInfoList(getContentResolver());
				RaceInfo item = raceInfoList.get(position);
				Logic.setSelectRaceInfo( item );
				
				// 削除ダイアログ表示
				raceInfoDeleteDialog(item);
				
				return true;
			}
		});
        
        // 大会登録ボタン
        Button entryBtn = (Button)findViewById(R.id.id_main_btn_entry);
        entryBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 大会登録画面遷移
				Intent intent = new Intent(MainActivity.this, RaceEntryActivity.class);
				startActivity(intent);
			}
		});
        
        // ボタン状態更新
		entryButtonEnabled();
    }

    private void raceInfoDeleteDialog( RaceInfo raceInfo ){
    	// ダイアログ表示
		AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		dialog.setTitle(getString(R.string.str_dialog_title_deleterace));
		dialog.setMessage(createDialogMessage(raceInfo));
		
		// 削除するボタン
		dialog.setPositiveButton(getString(R.string.str_dialog_msg_DEL), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				//TODO: 大会削除のロジック
				// 大会削除
				//DataBaseAccess.deleteRaceInfoByRaceId(getContentResolver(), m_DeleteRaceInfoItem.getRaceId());
				
				// 選手削除
				//DataBaseAccess.deleteRunnerInfoByRaceId(getContentResolver(), m_DeleteRaceInfoItem.getRaceId());

				// タイムリスト
				//DataBaseAccess.deleteTimeListByRaceId(getContentResolver(), m_DeleteRaceInfoItem.getRaceId());
				
				// 速報データ
				//DataBaseAccess.deleteUpdateDataByRaceId(getContentResolver(), m_DeleteRaceInfoItem.getRaceId());
				
				//　リストから大会情報削除
				//m_RaceInfoList.remove(m_DeleteRaceInfoItem);

				// 表示リストを更新
				//m_RaceInfoAdapter.notifyDataSetChanged();
				
				// ボタン状態更新
				entryButtonEnabled();
				
				// 速報中なら、速報停止
				//if( m_DeleteRaceInfoItem.getUpdateFlg().equals(DataBaseAccess.STR_DBA_RACE_UPDATEFLG_ON)){
					
					// 速報停止
					//Intent intent = new Intent(MainActivity.this, UpdateService.class);
					//stopService(intent);
				//}
				
				Toast.makeText(MainActivity.this, "削除しました", Toast.LENGTH_SHORT).show();
				
			}

		});
		
		//　やめるボタン
		dialog.setNegativeButton(getString(R.string.str_dialog_msg_NG), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// なにもしない
			}
		});
		
		dialog.show();
    }

    private String createDialogMessage( RaceInfo raceInfo ){
    	StringBuilder builder = new StringBuilder();
    	builder.append(getString(R.string.str_dialog_msg_name));
		builder.append("\n");
		builder.append(raceInfo.getRaceName());
		builder.append("\n");
		
		return builder.toString();
    }
    
    /**
     * 大会リストアダプタ
     * @author Hayato Matsumuro
     *
     */
    private class RaceListAdapter extends ArrayAdapter<RaceInfo>{

    	LayoutInflater inflater;
    	
		public RaceListAdapter(Context context, List<RaceInfo> objects) {
			super(context, 0, objects);
			
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			if( convertView == null ){
				convertView = this.inflater.inflate(R.layout.list_item_raceinfo, parent, false);
			}
			
			TextView raceNameTextView = (TextView)convertView.findViewById(R.id.id_raceinfo_txt_racename);
			TextView raceDateTextView = (TextView)convertView.findViewById(R.id.id_raceinfo_txt_racedate);
			TextView raceLocationTextView = (TextView)convertView.findViewById(R.id.id_raceinfo_txt_racelocation);
			TextView raceUpdateTextView = (TextView)convertView.findViewById(R.id.id_raceinfo_txt_update);
			
			RaceInfo item = getItem(position);
			
			raceNameTextView.setText(item.getRaceName());
			raceDateTextView.setText(item.getRaceDate());
			raceLocationTextView.setText(item.getRaceLocation());
			
			// 速報中の大会ならば、速報中と表示
			if(Logic.checkRaceIdUpdate(item.getRaceId())){
				raceUpdateTextView.setText(getString(R.string.str_txt_updateexe));
			}
			
			return convertView;
			
		}
    	
    }
    
    private void entryButtonEnabled(){
    	Button btn = (Button)findViewById(R.id.id_main_btn_entry);
    	if( Logic.getRaceInfoList(getContentResolver()).size() >= INT_RACEINFO_NUM_MAX){
    		btn.setEnabled(false);
    	}else{
    		btn.setEnabled(true);
    	}
    }
}
