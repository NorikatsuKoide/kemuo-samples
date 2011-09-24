package com.nakaikensuke.listviewtutorial;

import gueei.binding.Binder;
import android.app.Activity;
import android.os.Bundle;

public class ListViewTutorial extends Activity {
	private static final String[] Asia = new String[] {
		"China", "Thailand", "Japan", "Korea",
	};
	
	private static final String[] NAmerica = new String[] {
		"U.S.A.", "Canada",
	};
	
	private static final String[] Europe = new String[] {
		"U.K.", "Italy", "France", "Spain", "Netherlands",
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ListViewTutorialViewModel model = new ListViewTutorialViewModel();
		model.AsiaList.setArray(Asia);
		model.NAmericaList.setArray(NAmerica);
		model.EuropeList.setArray(Europe);
		
		Binder.setAndBindContentView(this, R.layout.main, model);
	}
}