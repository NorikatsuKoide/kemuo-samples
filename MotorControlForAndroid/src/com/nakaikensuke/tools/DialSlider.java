package com.nakaikensuke.tools;

/**
 * ダイヤル式のスライダー部品
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DialSlider extends View {
	
	/**
	 * ダイヤル操作イベントハンドラ
	 */
	public interface OnDialListener {
		void onDial(View view, int deltaDegree);
	}

	private boolean _isDialing = false;
	private OnDialListener _handler = null;
	
	private int _prevDegree = 0;
	
	/**
	 * コンストラクタ
	 * @param context
	 */
	public DialSlider(Context context) {
		super(context);
	}

	/**
	 * XMLからの生成用コンストラクタ 
	 * @param context
	 * @param attrs
	 */
	public DialSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * ダイヤルイベントハンドラ登録
	 */
	public void setOnDialListener(OnDialListener handler) {
		_handler = handler;
	}

	/**
	 * 描画イベントハンドラ
	 * デフォルトの実装。かなりシンプルな表示になります。
	 */
	@Override
	protected synchronized void onDraw(Canvas canvas) {

		int width = getWidth();
		int height = getHeight();
		
		// 背景は黒
		canvas.drawColor(0x055505);
		
		// ダイヤルの土台はダイヤル中だけやや大きくする
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		canvas.drawCircle(width / 2, height / 2, _isDialing ? 70 : 50, paint);

		if(!_isDialing)
			return;

		// ダイヤル中は現在のダイヤル地点を表示する
		double radian = Math.toRadians(_prevDegree);
		paint.setColor(Color.CYAN);
		canvas.drawCircle(
				(float)(width	/ 2 + 100 * Math.cos(radian)),
				(float)(height	/ 2 - 100 * Math.sin(radian)),
				20.0f,
				paint);
	}

	/**
	 * タッチイベントハンドラ
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			_isDialing = true;
			_prevDegree = calculateDegree(event);
			break;
			
		case MotionEvent.ACTION_MOVE:
			onActionMove(event);
			break;
			
		case MotionEvent.ACTION_UP:
			_isDialing = false;
			break;
			
		default:
			return false;
		}
		
		invalidate();
		return true;
	}
	
	/**
	 * マウス移動イベントハンドラ
	 */
	private void onActionMove(MotionEvent event) {
		
		// 角度算出
		int deg = calculateDegree(event);
		
		// 角度の差が180度より大きいとは、0→π と 0→-π とが交わる境界をまたいだ時に発生する。
		// この場合、不正な角度差だとみなし、通知しない。
		
		if(Math.abs(_prevDegree - deg) < 180) {
			if(_handler != null)
				_handler.onDial(this, _prevDegree - deg);
		}
		
		_prevDegree = deg;
	}
	
	/**
	 * マウスイベントから角度を算出する
	 */
	private int calculateDegree(MotionEvent event) {
		
		// 座標を View 中心座標系に変換
		int height = DialSlider.this.getHeight();
		int width = DialSlider.this.getWidth();
		double yy = (double)height / 2.0 - event.getY();
		double xx = event.getX() - (double)width / 2.0;
		
		// 角度算出
		return calculateDegree(xx, yy);
	}
	
	/**
	 * 与えられた (X,Y) から角度を算出する。
	 * ただし、第3象限から第4象限に移動する場合、不正な値となるため、呼び出し元でチェックする必要がある。
	 */
	private int calculateDegree(double x, double y) {
		double a  = (x != 0.0 || y != 0.0) ? Math.atan2(y, x) : 0.0;
		return (int)(360 * a / (2 * Math.PI));
	}
	
	/**
	 * 現在の角度を取得する
	 */
	public int getDegree() {
		return _prevDegree;
	}
	
	/**
	 * 現在ダイヤル操作中か判定する
	 */
	public boolean isDialing() {
		return _isDialing;
	}
}
