package com.nakaikensuke.calculator;

import java.text.DecimalFormat;

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
	
	// 現在表示すべき結果
	private String _current = "0";
	
	// 計算可能な最大桁数
	private static final int MAXLENGTH = 20;
	
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
}
