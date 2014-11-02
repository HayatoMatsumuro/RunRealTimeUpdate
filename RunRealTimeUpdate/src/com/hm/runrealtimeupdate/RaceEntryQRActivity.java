package com.hm.runrealtimeupdate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class RaceEntryQRActivity extends Activity implements AutoFocusCallback, PreviewCallback {

	private SurfaceView m_SurfaceView = null;
	private Camera m_Camera = null;
	private Point m_PreviewSize = null;
	private float m_PreviewWidthRatio = 0;
	private float m_PreviewHeightRatio = 0;

	private static final int MIN_PREVIEW_PIXELS = 470 * 320;
	private static final int MAX_PREVIEW_PIXELS = 1280 * 720;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_raceentryqr );
		
		m_SurfaceView = ( SurfaceView )findViewById( R.id.id_activity_raceentryqr_preview_surfaceview );
		SurfaceHolder holder = m_SurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback( new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				try {
					if (m_Camera != null) {
						m_Camera.setPreviewDisplay(holder);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (m_Camera != null) {
					m_Camera.stopPreview();
				}
				
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				if( m_Camera != null ){
					Camera.Parameters parameters = m_Camera.getParameters();
					parameters.setPreviewSize( m_PreviewSize.x, m_PreviewSize.y );
					m_Camera.setParameters( parameters );
					m_Camera.startPreview();
				}
				
			}
		});
		
		// 大会登録戻るボタン
		Button menuButton = ( Button )findViewById( R.id.id_activity_raceentryqr_menu_button );
		menuButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent( RaceEntryQRActivity.this, RaceEntryActivity.class );
				startActivity( intent );
			}
		});
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		m_Camera = Camera.open();
		
		// プレビューサイズ設定
		Camera.Parameters parameters = m_Camera.getParameters();
		List<Size> rawPreviewSizes = parameters.getSupportedPreviewSizes();
		List<Size> supportPreviewSizes = new ArrayList<Size>(rawPreviewSizes);
		Collections.sort( supportPreviewSizes, new Comparator<Size>(){

			@Override
			public int compare(Size lSize, Size rSize) {
				int lPixels = lSize.width * lSize.height;
				int rPixels = rSize.width * rSize.height;
				if( rPixels < lPixels ){
					return -1;
				}
				if( rPixels > lPixels ){
					return 1;
				}
				return 0;
			}
		});
		
		WindowManager manager = (WindowManager) getSystemService( Context.WINDOW_SERVICE );
		Display display = manager.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		float screenAspectRatio = (float) screenWidth / (float) screenHeight;
		Point bestSize = null;
		float diff = Float.POSITIVE_INFINITY;
		
		for (Size supportPreviewSize : supportPreviewSizes) {
			int supportWidth = supportPreviewSize.width;
			int supportHeight = supportPreviewSize.height;
			
			int pixels = supportWidth * supportHeight;
			if (pixels < MIN_PREVIEW_PIXELS || pixels > MAX_PREVIEW_PIXELS) {
				continue;
			}
			
			boolean isPortrait = supportWidth < supportHeight;
			int previewWidth = isPortrait ? supportHeight : supportWidth;
			int previewHeight = isPortrait ? supportWidth : supportHeight;
			if (previewWidth == screenWidth && previewHeight == screenHeight) {
				m_PreviewSize = new Point(supportWidth, supportHeight);
				m_PreviewWidthRatio = 1;
				m_PreviewHeightRatio = 1;
				return;
			}
			float aspectRatio = (float) previewWidth / (float) previewHeight;
			float newDiff = Math.abs(aspectRatio - screenAspectRatio);
			if (newDiff < diff) {
				bestSize = new Point(supportWidth, supportHeight);
				diff = newDiff;
			}
		}
		if (bestSize == null) {
			Size defaultSize = parameters.getPreviewSize();
			bestSize = new Point(defaultSize.width, defaultSize.height);
		}
		m_PreviewSize = bestSize;
		m_PreviewWidthRatio = (float) m_PreviewSize.x / (float) screenWidth;
		m_PreviewHeightRatio = (float) m_PreviewSize.y / (float) screenHeight;

	}
	
	 @Override
	 protected void onPause() {
		 super.onPause();
		 if (m_Camera != null) {
			m_Camera.release();
			m_Camera = null;
		 }
	 }

	@Override
	public void onPreviewFrame( byte[] data, Camera camera ) {
		Result rawResult = null;
		View target = ( View ) findViewById( R.id.id_activity_raceentryqr_center_view );
		int left = ( int ) ( target.getLeft() * m_PreviewWidthRatio );
		int top = ( int ) ( target.getTop() * m_PreviewHeightRatio );
		int width = ( int ) ( target.getWidth() * m_PreviewWidthRatio );
		int height = (int) (target.getHeight() * m_PreviewHeightRatio );
		
		PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
				data,
				m_PreviewSize.x,
				m_PreviewSize.y,
				left,
				top,
				width,
				height,
				false
		);
		
		if (source != null) {
			BinaryBitmap bitmap = new BinaryBitmap( new HybridBinarizer( source ) );
			MultiFormatReader multiFormatReader = new MultiFormatReader();
			
			try {
				rawResult = multiFormatReader.decode(bitmap);
				
				// URL表示ダイアログ表示
				InfoDialog<String> URLDispDialog = new InfoDialog<String>( rawResult.getText(), new URLDispCallbackImpl());
				URLDispDialog.onDialog(
						RaceEntryQRActivity.this,
						getString( R.string.str_dialog_title_urldisp ),
						createDialogMessage( rawResult.getText() ),
						getString( R.string.str_dialog_msg_OK ),
						getString( R.string.str_dialog_msg_NG )
				);
		
			} catch ( ReaderException re) {
				Toast.makeText(getApplicationContext(), "読み取りに失敗しました。", Toast.LENGTH_SHORT).show();
			}
		}
		
	}

	@Override
	public void onAutoFocus( boolean success, Camera camera ) {
		if (success) {
			m_Camera.setOneShotPreviewCallback(this);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (m_Camera != null) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				m_Camera.autoFocus(this);
			}
		}
		
		return super.onTouchEvent(event);
	}
	
	private String createDialogMessage( String url ){
		
		StringBuilder builder = new StringBuilder();
		builder.append("取得したURL : ");
		builder.append( url );
		
		return builder.toString();
	}
	
	/**
	 * URL表示ダイアログボタン押しのコールバック
	 */
	private class URLDispCallbackImpl implements InfoDialog.ButtonCallback<String>{

		@Override
		public void onClickPositiveButton(DialogInterface dialog, int which, String url ){
			
			// 取得したURLにアップデートサイトが含まれるか調べる
			if( url.startsWith( getString( R.string.str_txt_defaulturl ) ) ){
				// アップデートサイト取得
				String raceId = url.replaceAll( getString( R.string.str_txt_defaulturl ), "" );
				
				// 大会登録画面遷移
				Intent intent = new Intent( RaceEntryQRActivity.this, RaceEntryActivity.class );
				intent.putExtra( RaceEntryActivity.STR_INTENT_RACEID, raceId );
				startActivity( intent );
				
				Toast.makeText(getApplicationContext(), "QRコード解析に成功しました。", Toast.LENGTH_SHORT).show();
			}else{
				// アップデートサイトでない
				Toast.makeText(getApplicationContext(), "URL解析に失敗しました。", Toast.LENGTH_SHORT).show();
			}
			
		}

		@Override
		public void onClickNegativeButton(DialogInterface dialog, int which, String url ){
			
		}
		
	}
}
