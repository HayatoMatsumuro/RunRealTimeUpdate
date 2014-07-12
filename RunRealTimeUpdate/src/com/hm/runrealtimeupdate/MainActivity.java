package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
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
        
        // 大会情報リスト設定
        RaceListAdapter adapter = new RaceListAdapter( this, raceInfoList);
        ListView raceInfoListView = (ListView)findViewById(R.id.id_main_listview_race);
        raceInfoListView.setAdapter(adapter);
        
        // リストのアイテム短押し
        raceInfoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				
				// 選択した大会情報を取得する
				ListView listView = (ListView)parent;
				RaceInfo raceInfo = (RaceInfo)listView.getItemAtPosition(position);
				
				// 画面遷移
				Intent intent = new Intent( MainActivity.this, RaceDetailActivity.class);
				intent.putExtra(RaceDetailActivity.STR_INTENT_RACEID, raceInfo.getRaceId());
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
				
				// アダプタを取得する
				RaceListAdapter adapter = ( RaceListAdapter )listView.getAdapter();
				
				// 削除ダイアログ表示
				RaceDeleteDialog raceDeleteDialog
					= new RaceDeleteDialog(
							MainActivity.this,
							getContentResolver(),
							raceInfo,
							adapter,
							(Button)findViewById(R.id.id_main_btn_entry));
				raceDeleteDialog.onDialog();
				
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
        
        // 大会登録ボタンの表示状態設定
        int raceInfoNum = raceInfoListView.getAdapter().getCount();
        if( raceInfoNum >= INT_RACEINFO_NUM_MAX ){
        	entryBtn.setEnabled(false);
        }else{
        	entryBtn.setEnabled(true);
        }
    }

    /**
     *　大会削除ダイアログ
     * @author Hayato Matsumuro
     *
     */
    private class RaceDeleteDialog {
    	
    	/**
    	 * コンテキスト
    	 */
    	private Context m_Context;
    	
    	/**
    	 * コンテントリゾルバ
    	 */
    	private ContentResolver m_ContentResolver;
    	
    	/**
    	 * 大会情報
    	 */
    	private RaceInfo m_RaceInfo;
    	
    	/**
    	 * 大会情報アダプタ
    	 */
    	private RaceListAdapter m_Adapter;
    	
    	/**
    	 * 大会登録のボタンID
    	 */
    	private Button m_EntryButton;
    	
    	/**
    	 * コンストラクタ
    	 * @param context コンテキスト
    	 * @param contentResolver コンテントリゾルバ
    	 * @param raceInfo 大会情報
    	 * @param adapter 大会リストアダプタ
    	 * @param button 大会登録ボタン
    	 */
    	RaceDeleteDialog( Context context, ContentResolver contentResolver, RaceInfo raceInfo, RaceListAdapter adapter, Button button ){
    		
    		// 初期化
    		m_Context = context;
    		m_ContentResolver = contentResolver;
    		m_RaceInfo = raceInfo;
    		m_Adapter = adapter;
    		m_EntryButton = button;
    	}
    	
    	public void onDialog(){
    		
    		AlertDialog.Builder dialog = new AlertDialog.Builder(m_Context);
    		dialog.setTitle(getString(R.string.str_dialog_title_deleterace));
    		dialog.setMessage(createDialogMessage(m_RaceInfo));
    		
    		// 削除するボタン
    		dialog.setPositiveButton(getString(R.string.str_dialog_msg_DEL), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					
					// 速報中でないなら削除する
					if( !m_RaceInfo.isRaceUpdate()){
						
						// 大会削除
						Logic.deleteRaceInfo(m_ContentResolver, m_RaceInfo.getRaceId());
						
						// リストから大会削除
						if( m_Adapter != null ){

							m_Adapter.remove(m_RaceInfo);
							m_Adapter.notifyDataSetChanged();
						}
						
						// ボタンを表示にする
						if( m_EntryButton != null ){
							m_EntryButton.setEnabled(true);
						}
						
						Toast.makeText(m_Context, "削除しました", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(m_Context, "速報中のため、削除できません。", Toast.LENGTH_SHORT).show();
					}
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
			
			RaceInfo raceInfo = getItem(position);
			
			raceNameTextView.setText(raceInfo.getRaceName());
			raceDateTextView.setText(raceInfo.getRaceDate());
			raceLocationTextView.setText(raceInfo.getRaceLocation());
			
			// 速報中の大会ならば、速報中と表示
			if(raceInfo.isRaceUpdate()){
				raceUpdateTextView.setText(getString(R.string.str_txt_updateexe));
			}else{
				raceUpdateTextView.setText("");
			}
			
			return convertView;
			
		}
    	
    }
    
}
