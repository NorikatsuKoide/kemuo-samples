package com.nakaikensuke.devquiz2011;

import com.google.android.apps.gddquiz.IQuizService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DevQuiz2011Activity extends Activity {

	private TextView		_answer	= null;
	private Button		_getAnswer	= null;
	private IQuizService	_service	= null;
	
	/**
	 * Activity生成イベントハンドラ
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		_answer	= (TextView)findViewById(R.id.quiz_answer);
		_getAnswer	= (Button)findViewById(R.id.get_answer);
		_getAnswer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				if(_service == null) {
					Toast.makeText(
							DevQuiz2011Activity.this,
							"Servie not available",
							Toast.LENGTH_LONG).show();
					return;
				}
				
				String answer = "getCode() failed";
				try {
					answer = _service.getCode();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				_answer.setText(answer);
				Log.d("DevQuiz", answer);
			}
		});
	}

	/**
	 * Activity復帰イベントハンドラ
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		Intent intent = new Intent(IQuizService.class.getName());
		bindService(intent, _connection, BIND_AUTO_CREATE);
	}
	
	// サービス接続イベントハンドラオブジェクト
	private final ServiceConnection _connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			_service = IQuizService.Stub.asInterface(binder);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			_service = null;
		}
	};

	/**
	 * Activity一時停止イベントハンドラ
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		unbindService(_connection);
	}
}