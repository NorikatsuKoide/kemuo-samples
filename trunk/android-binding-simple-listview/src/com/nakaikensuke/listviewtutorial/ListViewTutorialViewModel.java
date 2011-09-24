package com.nakaikensuke.listviewtutorial;

import gueei.binding.collections.ArrayListObservable;

public class ListViewTutorialViewModel {
	public ArrayListObservable<String> AsiaList = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> NAmericaList = new ArrayListObservable<String>(String.class);
	public ArrayListObservable<String> EuropeList = new ArrayListObservable<String>(String.class);
}
