package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListView raceInfoListView = (ListView)findViewById(R.id.id_activity_main_body_contents_racelist_listview);
        
        // リストのアイテム短押し
        raceInfoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				
				// 選択した大会情報を取得する
				ListView listView = (ListView)parent;
				RaceInfo raceInfo = (RaceInfo)listView.getItemAtPosition(position);
				
				// 画面遷移
				Intent intent = new Intent( MainActivity.this, RaceTabActivity.class);
				intent.putExtra(RaceTabActivity.STR_INTENT_RACEID, raceInfo.getRaceId());
				intent.putExtra(RaceTabActivity.STR_INTENT_CURRENTTAB, RaceTabActivity.INT_INTENT_VAL_CURRENTTAB_DETAIL);
				startActivity(intent);
			}
		});
        
        // リストのアイテム長押し
        raceInfoListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				
				// 選択した大会情報を取得する
				ListView listView = (ListView)parent;
				RaceInfo raceInfo = (RaceInfo)listView.getItemAtPosition(position);
				
				// 削除ダイアログ表示
				InfoDialog<RaceInfo> raceDeleteInfoDialog = new InfoDialog<RaceInfo>( raceInfo, new RaceDeleteButtonCallbackImpl() );
				raceDeleteInfoDialog.onDialog(
						MainActivity.this,
						getString(R.string.str_dialog_title_deleterace),
						createDialogMessage( raceInfo ),
						getString(R.string.str_dialog_msg_DEL),
						getString(R.string.str_dialog_msg_NG)
				);
				
				return true;
			}
		});
        
        // 大会登録ボタン
        Button entryBtn = (Button)findViewById(R.id.id_activity_main_header_entry_button);
        entryBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 大会登録画面遷移
				Intent intent = new Intent( MainActivity.this, RaceEntryActivity.class );
				startActivity(intent);
			}
		});
        
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		 // 大会情報
        List<RaceInfo> raceInfoList = Logic.getRaceInfoList(getContentResolver());
        
        // 速報サービス停止状態ならば、データベースの速報状態を停止する
        RaceInfo chkRaceInfo = null;
        for( RaceInfo raceInfo : raceInfoList ){
        	if( raceInfo.isRaceUpdate() ){
        		chkRaceInfo = raceInfo;
        		break;
        	}
        }
        
        if( chkRaceInfo != null ){
        	
        	if( !isSetUpdateAlarm() ){
        		Log.d("main", "Alarm Stop, database updateexe");
        		Logic.setUpdateOffRaceId( getContentResolver(), chkRaceInfo.getRaceId() );
        		chkRaceInfo.setRaceUpdate( false );
        	}
        }
        // 大会情報リスト設定
        RaceListAdapter adapter = new RaceListAdapter( this, raceInfoList );
        ListView raceInfoListView = (ListView)findViewById(R.id.id_activity_main_body_contents_racelist_listview);
        raceInfoListView.setAdapter(adapter);
        
        // 表示状態設定
        setViewBody( raceInfoListView.getCount() );
	}

	/**
     * ボディの表示設定
     */
    private void setViewBody( int raceInfoNum ){
    	
    	// 大会リストまたは大会未登録メッセージの設定
    	RelativeLayout contentsLayout = ( RelativeLayout )findViewById( R.id.id_activity_main_body_contents_layout );
    	RelativeLayout messageLayout = ( RelativeLayout )findViewById( R.id.id_activity_main_body_message_layout );
    	
    	if( raceInfoNum == 0 ){
    		contentsLayout.setVisibility(View.GONE);
    		messageLayout.setVisibility(View.VISIBLE);
        }else{
        	contentsLayout.setVisibility(View.VISIBLE);
        	messageLayout.setVisibility(View.GONE);
        }
    }
    
    /**
	 * ダイアログメッセージ作成
	 * @param raceInfo 大会情報
	 * @return
	 */
	private String createDialogMessage( RaceInfo raceInfo ){
    	StringBuilder builder = new StringBuilder();
    	builder.append(getString(R.string.str_dialog_msg_name));
		builder.append("\n");
		builder.append(raceInfo.getRaceName());
		builder.append("\n");
		
		return builder.toString();
    }
    
	/**
	 * 大会削除ダイアログのボタン押しコールバック
	 * @author Hayato Matsumuro
	 *
	 */
    private class RaceDeleteButtonCallbackImpl implements InfoDialog.ButtonCallback<RaceInfo>{

		@Override
		public void onClickPositiveButton(DialogInterface dialog, int which, RaceInfo info) {
			// ポジティブボタン押し
			// 速報中でないなら削除する
			if( !info.isRaceUpdate()){
				
				// 大会削除
				Logic.deleteRaceInfo( getContentResolver(), info.getRaceId());
				
				// リストから大会削除
				ListView raceInfoListView = (ListView)findViewById(R.id.id_activity_main_body_contents_racelist_listview);
				RaceListAdapter adapter = ( RaceListAdapter )raceInfoListView.getAdapter();
				adapter.remove( info );
				adapter.notifyDataSetChanged();
				
				// 表示状態設定
		        setViewBody( raceInfoListView.getCount() );
				
				Toast.makeText( MainActivity.this, "削除しました", Toast.LENGTH_SHORT).show();
			
			}else{
				
				Toast.makeText( MainActivity.this, "速報中のため、削除できません。", Toast.LENGTH_SHORT).show();
			}
			
		}

		@Override
		public void onClickNegativeButton(DialogInterface dialog, int which, RaceInfo info) {
		}
    	
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
			
			RelativeLayout raceUpdateLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_raceinfo_update_layout );
			
			TextView raceNameTextView = (TextView)convertView.findViewById(R.id.id_list_item_raceinfo_race_name_textview);
			
			RaceInfo raceInfo = getItem(position);
			
			raceNameTextView.setText(raceInfo.getRaceName());
			
			// 速報中の大会ならば、速報中と表示
			if(raceInfo.isRaceUpdate()){
				raceUpdateLayout.setVisibility(View.VISIBLE);
			}else{
				raceUpdateLayout.setVisibility(View.GONE);
			}
			
			return convertView;
			
		}
    	
    }
    
    /**
     * 更新アラームが設定されているか確認する
     * @return true:起動中/false:起動中でない
     */
    private boolean isSetUpdateAlarm(){
    	
    	Intent intent = new Intent( MainActivity.this, UpdateService.class );
    	
    	PendingIntent pendingIntent = PendingIntent.getService(
    						MainActivity.this,
    						UpdateService.INT_REQUESTCODE_START,
    						intent,
    						PendingIntent.FLAG_NO_CREATE );
    	
    	if( pendingIntent == null ) {
    		return false;
    	}else {
    	    return true;
    	}
    }
}
