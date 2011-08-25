package com.nakaikensuke.motorcontrol;

import java.io.IOException;

import org.microbridge.server.AbstractServerListener;
import org.microbridge.server.Client;
import org.microbridge.server.Server;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.nakaikensuke.tools.DialSlider;

public class YohSampleActivity extends Activity {
	private final String TAG = "YOH-SAMPLE";

	private final int MAX_DIAL_LEVEL = 1800;
	private int _dialLevel = 0;

	private final int ARDUINO_PORT			= 4567;
	private final byte SND_SERVO_CTRL		= 0x00;
	private final byte SND_DCMOTOR_CTRL	= 0x05;
	private Server _server = null;
	
	private boolean _isDcMotorFront = true;
	
	/**
	 * Activity生成イベントハンドラ
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// レイアウト適用
		setContentView(R.layout.main);
		
		// くるくるスライダー初期化
		DialSlider slider = (DialSlider)findViewById(R.id.servo_slider);
		slider.setOnDialListener(_dialHandler);

		// DCモータ前進後退ラジオボタン初期化
		RadioGroup motorDir = (RadioGroup)findViewById(R.id.dc_motor_dirs);
		motorDir.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				_isDcMotorFront = (checkedId == R.id.dc_motor_front);
			}
		});
		
		// DCモータスピードスライダー初期化
		SeekBar dcSpeeder = (SeekBar)findViewById(R.id.dc_motor_progress);
		dcSpeeder.setOnSeekBarChangeListener(_dcMotorSeekHandler);
	}
	
	/**
	 * くるくるスライダー状態変更コールバックオブジェクト
	 */
	private final DialSlider.OnDialListener _dialHandler = new DialSlider.OnDialListener() {
		
		/**
		 * ダイアルイベントハンドラ
		 */
		public void onDial(View view, int deltaDegree) {
			
			// 最小値に達したらそれ以上下げない
			if(_dialLevel + deltaDegree < 0) {
				_dialLevel = 0;
			}
			// 最大値に達したらそれ以上進めない
			else if(_dialLevel + deltaDegree >= MAX_DIAL_LEVEL) {
				_dialLevel = MAX_DIAL_LEVEL;
			}
			else {
				_dialLevel += deltaDegree;
			}

			// Arduinoに送信するメッセージ作成
			byte[] message = new byte[2];
			message[0] = SND_SERVO_CTRL;
			message[1] = (byte)(_dialLevel / 10);
			
			// メッセージをArduinoに送信
			try {
				_server.send(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	// DCモータスピードスライダーイベントハンドラオブジェクト
	private final SeekBar.OnSeekBarChangeListener _dcMotorSeekHandler = new SeekBar.OnSeekBarChangeListener() {
		public void onStopTrackingTouch(SeekBar arg0) {}
		
		public void onStartTrackingTouch(SeekBar arg0) {}
		
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			sendDcMotorReq(_isDcMotorFront, progress);
		}
	};
	
	/**
	 * DCモータ制御コマンドを送信する
	 * @param isFront
	 * @param progress
	 */
	private void sendDcMotorReq(boolean isFront, int progress) {
		
		// Arduinoに送信するメッセージ作成
		byte[] message = new byte[3];
		message[0] = SND_DCMOTOR_CTRL;
		message[1] = (byte)(isFront ? 0x01 : 0x00);
		message[2] = (byte)progress;
		
		// メッセージをArduinoに送信
		try {
			_server.send(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() {
		Log.e(TAG, ">>>>>>>>>> Version 1.1 <<<<<<<<<<");
		
		// MicroBridgeサーバ初期化
		_server = new Server(ARDUINO_PORT);
		_server.addListener(_lightSensorCallback);
		try {
			_server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if(_server != null) {
			_server.stop();
		}
	}

	// Arduinoメッセージ受信コールバックオブジェクト
	private final AbstractServerListener _lightSensorCallback = new AbstractServerListener() {
		
		/**
		 * Arduinoメッセージ受信イベントハンドラ
		 */
		@Override
		public void onReceive(Client client, byte[] data) {
			Log.e(TAG, "Unknown message: " + data[0]);
		}
	};
}