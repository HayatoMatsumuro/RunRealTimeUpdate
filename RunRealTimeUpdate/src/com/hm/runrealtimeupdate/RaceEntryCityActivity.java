package com.hm.runrealtimeupdate;

import java.util.List;

import com.hm.runrealtimeupdate.logic.Logic;
import com.hm.runrealtimeupdate.logic.LogicException;
import com.hm.runrealtimeupdate.logic.RaceInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RaceEntryCityActivity extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_raceentrycity );

		// 戻るボタン
		Button backButton = ( Button )findViewById( R.id.id_activity_raceentrycity_header_back_button );
		backButton.setOnClickListener
		(
			new OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					Intent intent = new Intent( RaceEntryCityActivity.this, RaceEntryActivity.class );
					startActivity( intent );
				}
			}
		);

		// ファイル読み込み
		List<CommonLib.CityProperties> propertiesList = CommonLib.getCityProperties( this );

		// 大会情報リスト設定
		CityRaceListAdapter adapter = new CityRaceListAdapter( this, propertiesList );
		ListView cityRaceListView = ( ListView )findViewById( R.id.id_activity_raceentrycity_body_contents_city_listview );
		cityRaceListView.setAdapter( adapter );

		cityRaceListView.setOnItemClickListener
		(
			new OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> parent, View v, int position, long id )
				{
					// 選択した大会情報を取得する
					ListView listView = ( ListView )parent;
					CommonLib.CityProperties properties = ( CommonLib.CityProperties )listView.getItemAtPosition( position );

					// 大会情報取得タスクの起動
					RaceInfoLoaderTask task = new RaceInfoLoaderTask( properties.raceName, properties.raceId );
					RaceInfoLoaderTask.TaskParam param = task.new TaskParam();
					param.url = properties.parserInfo.url;
					param.parserUpdate = properties.parserInfo.parserClassName;
					task.execute( param );
				}
				
			}
		);
		return;
	}

	private class CityRaceListAdapter extends ArrayAdapter<CommonLib.CityProperties>
	{
		/**
		 * レイアウトインフレータ
		 */
		LayoutInflater m_Inflater;

		public CityRaceListAdapter( Context context, List<CommonLib.CityProperties> cityPropertiesList )
		{
			super( context, 0, cityPropertiesList );

			m_Inflater = ( LayoutInflater )context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

			return;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent )
		{
			if( convertView == null )
			{
				convertView = m_Inflater.inflate( R.layout.list_item_raceinfo, parent, false );
			}

			// 大会情報取得
			CommonLib.CityProperties cityProperties = getItem( position );

			// 大会名表示
			TextView raceNameTextView = ( TextView )convertView.findViewById( R.id.id_list_item_raceinfo_race_name_textview );
			raceNameTextView.setText( cityProperties.raceName );

			// 速報中の表示( 非表示 )
			RelativeLayout raceUpdateLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_raceinfo_update_layout );
			RelativeLayout raceReserveLayout = ( RelativeLayout )convertView.findViewById( R.id.id_list_item_raceinfo_reserve_layout );
			raceUpdateLayout.setVisibility( View.GONE );
			raceReserveLayout.setVisibility( View.GONE );

			return convertView;
		}
	}

	/**
	 * 大会情報取得タスク
	 * @author Hayato Matsumuro
	 *
	 */
	class RaceInfoLoaderTask extends AsyncTask<RaceInfoLoaderTask.TaskParam, Void, RaceInfo>
	{
		/**
		 * 進捗ダイアログ
		 */
		private ProgressDialog m_ProgressDialog = null;

		/**
		 * 大会名
		 */
		private String m_RaceName;

		/**
		 * 大会ID
		 */
		private String m_RaceId;

		/**
		 * コンストラクタ
		 * @param raceName 大会名
		 * @param raceId 大会ID
		 */
		public RaceInfoLoaderTask( String raceName, String raceId )
		{
			m_RaceName = raceName;
			m_RaceId = raceId;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			// 進捗ダイアログ作成
			m_ProgressDialog = new ProgressDialog( RaceEntryCityActivity.this );
			m_ProgressDialog.setTitle( getResources().getString( R.string.str_dialog_title_progress_raceinfo ) );
			m_ProgressDialog.setMessage( getResources().getString( R.string.str_dialog_msg_get ) );
			m_ProgressDialog.setCancelable( true );
			m_ProgressDialog.setButton
			(
				DialogInterface.BUTTON_NEGATIVE,
				getResources().getString( R.string.str_dialog_msg_cancel ),
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick( DialogInterface dialog, int which )
					{
						cancel( true );

						return;
					}
				}
			);

			m_ProgressDialog.show();

			return;
		}

		@Override
		protected RaceInfo doInBackground( TaskParam... params )
		{
			RaceInfo raceInfo = null;

			try
			{
				// 大会情報取得
				String url = params[0].url;
				String parserUpdate = params[0].parserUpdate;
				raceInfo = Logic.getNetRaceInfo( url, null, parserUpdate );
			}
			catch ( LogicException e )
			{
				e.printStackTrace();
			}

			return raceInfo;
		}

		@Override
		protected void onPostExecute( RaceInfo raceInfo )
		{
			if( m_ProgressDialog != null )
			{
				// ダイアログ削除
				m_ProgressDialog.dismiss();
			}

			if( raceInfo == null )
			{
				// 大会情報取得失敗
				Toast.makeText( RaceEntryCityActivity.this, "大会情報取得に失敗しました。", Toast.LENGTH_SHORT ).show();
				return;
			}
			else
			{
				raceInfo.name = m_RaceName;
				raceInfo.id = m_RaceId;
				InfoDialog<RaceInfo> raceEntryInfoDialog = new InfoDialog<RaceInfo>( raceInfo, new RaceEntryCityButtonCallbackImpl() );
				raceEntryInfoDialog.onDialog(
					RaceEntryCityActivity.this,
					getString( R.string.str_dialog_title_race ),
					createDialogMessage( raceInfo ),
					getString( R.string.str_dialog_msg_OK ),
					getString( R.string.str_dialog_msg_NG )
				);
			}

			return;
		}

		@Override
		protected void onCancelled()
		{
			super.onCancelled();

			// ダイアログ削除
			if( m_ProgressDialog != null )
			{
				m_ProgressDialog.dismiss();
			}

			Toast.makeText( RaceEntryCityActivity.this, "大会情報取得をキャンセルしました。", Toast.LENGTH_SHORT ).show();

			return;
		}

		/**
		 * タスクパラメータ
		 * @author Hayato Matsumuro
		 *
		 */
		private class TaskParam
		{
			/**
			 * アップデートサイトURL
			 */
			public String url;

			/**
			 * パーサーのクラス名
			 */
			public String parserUpdate;
		}
	}

	/**
	 * 登録ダイアログのメッセージを作成する
	 * @param raceInfo 大会情報
	 * @return メッセージ
	 */
	private String createDialogMessage( RaceInfo raceInfo )
	{
		StringBuilder builder = new StringBuilder();
		builder.append( getString( R.string.str_dialog_msg_name ) );
		builder.append( "\n" );
		builder.append( raceInfo.name );
		builder.append( "\n");

		return builder.toString();
	}

	/**
	 * 大会登録ダイアログのボタン押しのコールバック
	 * @author Hayato Matsumuro
	 *
	 */
	private class RaceEntryCityButtonCallbackImpl implements InfoDialog.ButtonCallback<RaceInfo>
	{
		@Override
		public void onClickPositiveButton( DialogInterface dialog, int which, RaceInfo info )
		{
			if( Logic.checkEntryRaceId( getContentResolver(), info.id ) )
			{
				// すでに大会が登録済み
				Toast.makeText( RaceEntryCityActivity.this, "この大会はすでに登録済みです。", Toast.LENGTH_SHORT ).show();
			}
			else
			{
				// データベース登録
				Logic.entryRaceInfo( getContentResolver(), info );

				Toast.makeText( RaceEntryCityActivity.this, "登録しました", Toast.LENGTH_SHORT ).show();

				// メイン画面に遷移
				Intent intent = new Intent( RaceEntryCityActivity.this, MainActivity.class );
				startActivity( intent );
			}

			return;
		}

		@Override
		public void onClickNegativeButton( DialogInterface dialog, int which, RaceInfo info )
		{
			return;
		}
	}
}
