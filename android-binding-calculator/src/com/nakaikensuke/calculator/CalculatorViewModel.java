package com.nakaikensuke.calculator;

import java.text.DecimalFormat;
import java.util.Stack;

import gueei.binding.Command;
import gueei.binding.DependentObservable;
import gueei.binding.Observable;
import android.view.View;

public class CalculatorViewModel {
	
	public final Observable<Double> Display = new Observable<Double>(Double.class, 0d);
	public final DependentObservable<String> FormattedDisplay = new DependentObservable<String>(String.class, Display) {
		@Override
		public String calculateValue(Object... arg0) throws Exception {
			DecimalFormat format = new DecimalFormat();
			format.applyPattern("#.######");
			String output = format.format(Display.get());
			if(output.length() <= MAXLENGTH) {
				return output;
			}
			format.applyPattern("#.########E00");
			return format.format(Display.get());
		}
	};
	
	// 数字ボタン用コマンド
	public NumberCommand Number9 = new NumberCommand(9);
	public NumberCommand Number8 = new NumberCommand(8);
	public NumberCommand Number7 = new NumberCommand(7);
	public NumberCommand Number6 = new NumberCommand(6);
	public NumberCommand Number5 = new NumberCommand(5);
	public NumberCommand Number4 = new NumberCommand(4);
	public NumberCommand Number3 = new NumberCommand(3);
	public NumberCommand Number2 = new NumberCommand(2);
	public NumberCommand Number1 = new NumberCommand(1);
	public NumberCommand Number0 = new NumberCommand(0);
	
	// Backボタン用コマンド
	public Command Back = new Command() {
		@Override
		public void Invoke(View arg0, Object... arg1) {
			onBack();
		}
	};
	
	// ACボタン用コマンド
	public Command AllClear = new Command() {
		@Override
		public void Invoke(View arg0, Object... arg1) {
			onAllClear();
		}
	};
	
	// ドットボタン用コマンド
	public Command Dot = new Command() {
		@Override
		public void Invoke(View arg0, Object... arg1) {
			putDot();
		}
	};
	
	// 数字ボタンアクションに対応するコマンドクラス
	private class NumberCommand extends Command {		// CommandはInterfaceじゃない!
		private int _number = 0;
		
		public NumberCommand(int number) {
			_number = number;
		}
		
		@Override
		public void Invoke(View arg0, Object... arg1) {
			addNumber(_number);
		}
	}
	
	// 演算を表すクラス
	private abstract static class Operator {
		private final int _level;
		private final String _symbol;
		private final boolean _unaray;	// 単項演算子か否か
		
		public Operator(int level, String symbol, boolean unaray) {
			_level = level;
			_symbol = symbol;
			_unaray = unaray;
		}
		
		// 各種フィールドアクセスメソッド
		public int		 getLevel()  {return _level;}
		public String  getSymbol() {return _symbol;}
		public boolean isUnary()   {return _unaray;}
		
		public abstract double calculate(double operandA, double operandB);
	}
	
	// 演算ボタンアクションに対応するコマンドクラス
	private class OperatorCommand extends Command {
		private final Operator _operator;
		
		public OperatorCommand(Operator operator) {
			_operator = operator;
		}

		@Override
		public void Invoke(View arg0, Object... arg1) {
			operate(_operator);
		}
	}
	
	// 現在計算結果として表示している文字列
	private String _current = "0";
	
	// 計算可能な最大桁数
	private static final int MAXLENGTH = 20;
	
	// 最後の入力が演算か否か
	private boolean _lastCommandIsOperator = false;
	
	private Stack<Double> _operands = new Stack<Double>();
	private Stack<Operator> _operators = new Stack<CalculatorViewModel.Operator>();
	
	// 現在の入力を全てクリアします
	private void onAllClear() {
		_current = "0";
		_operands.clear();
		_operators.clear();
		Display.set(0d);
	}
	
	// 直前の入力をクリアします
	private void onBack() {
		if(_lastCommandIsOperator)
			return;
		
		String temp = _current;
		temp = temp.substring(0, temp.length() - 1);
		if(temp.length() == 0) {
			temp = "0";
		}
		_current = temp;
		
		Display.set(Double.parseDouble(_current));
	}
	
	// ドット (小数点) を追加する
	private void putDot() {
		String temp = _current;
		if(temp.equals("0")) {
			_current = "0.";
		} else {
			// すでにドットが入力されている場合は無視する
			if(temp.indexOf(temp) < 0) {
				_current = temp + ".";
			}
		}
		
		Display.set(Double.parseDouble(_current));
	}
	
	// 数字を追加します
	private void addNumber(int number) {
		
		String temp = _current;
		if(temp.length() >= MAXLENGTH)
			return;
		
		if(temp.equals("0") && temp.indexOf(".") < 0) {
			_current = Integer.toString(number);
		} else {
			_current = temp + number;
		}
		
		Display.set(Double.parseDouble(_current));
	}
	
	// 演算を実行します
	private void operate(Operator operator) {
		// TODO:
	}
}
