package com.nakaikensuke.calculator;

import java.text.DecimalFormat;
import java.util.Stack;

import gueei.binding.Command;
import gueei.binding.DependentObservable;
import gueei.binding.Observable;
import android.view.View;

public class CalculatorViewModel {
	
	// 計算結果表示用プロパティ
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
	
	// ＋ボタン用コマンド
	public OperatorCommand Plus = new OperatorCommand(new Operator(1, "+", false) {
		@Override
		public double calculate(double operandA, double operandB) {
			return operandA + operandB;
		}
	});
	
	// ーボタン用コマンド
	public OperatorCommand Minus = new OperatorCommand(new Operator(1, "-", false) {
		@Override
		public double calculate(double operandA, double operandB) {
			return operandA - operandB;
		}
	});
	
	// ×ボタン用コマンド
	public OperatorCommand Multiply = new OperatorCommand(new Operator(2, "x", false) {
		@Override
		public double calculate(double operandA, double operandB) {
			return operandA * operandB;
		}
	});
	
	// ÷ボタン用コマンド
	public OperatorCommand Divide = new OperatorCommand(new Operator(2, "/", false) {
		@Override
		public double calculate(double operandA, double operandB) {
			return operandA / operandB;
		}
	});
	
	// ＝ボタン用コマンド
	public OperatorCommand Equal = new OperatorCommand(new Operator(0, "=", true) {
		@Override
		public double calculate(double operandA, double operandB) {
			return operandA;
		}
	});
	
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
		@SuppressWarnings("unused")
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
	
	// 現在有効なオペランドの文字列
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
		
		_lastCommandIsOperator = false;
		
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
		
		if(!_lastCommandIsOperator) {
			
			// 直前の入力が演算でない場合は、直前の入力 (左辺) をスタックに積む
			_operands.push(Double.parseDouble(_current));
			_lastCommandIsOperator = true;
			
		} else {
			
			// 直前の入力が演算の場合は、直前の入力 (演算) をキャンセルする
			if(!_operators.isEmpty()) {
				_operators.pop();
			}
		}
		
		if(operator.isUnary() && operator.getLevel() > 0) {

			// ＝以外の単項演算子の場合 (現時点では存在しない)
			// 直前のオペランドに対して演算を適用する
			// TODO: オペランドがない場合はど〜なる？
			double result = operator.calculate(_operands.pop(), 0d);
			Display.set(result);
			_current = String.valueOf(result);
			
		} else {
			
			// ＝かもしくは二項演算子の場合
			// もちろん×や÷の方が＋やーよりも優先度が高い
			// 次に演算する時に優先順位を判定するために優先順位が低い演算を残しておく。
			//
			// 1 + 2 - 3 × 4 ＝
			// でトレースすると分かりやすい。
			
			Operator lastOperator = null;
			while(!_operators.empty()) {
				lastOperator = _operators.peek();
				if(lastOperator.getLevel() >= operator.getLevel()) {
					double operandB = _operands.pop();
					double operandA = _operands.pop();
					double result = lastOperator.calculate(operandA, operandB);
					Display.set(result);
					_operands.push(result);
					_operators.pop();
				} else {
					break;
				}
			}
			
			if(operator.getLevel() > 0) {
				_operators.push(operator);
			}
			
			_current = "0";
		}
	}
}
