package com.yohpapa.research.searchsample;

import android.content.SearchRecentSuggestionsProvider;

public class RecentSuggestionsProvider extends SearchRecentSuggestionsProvider {
	public static final String AUTHORITY = "com.yohpapa.research.searchsample.RecentSuggestionsProvider";
	public static final int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;
	
	public RecentSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}
