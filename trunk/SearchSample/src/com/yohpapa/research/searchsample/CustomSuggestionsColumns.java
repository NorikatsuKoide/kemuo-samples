/**
Copyright (c) 2011, KENSUKE NAKAI
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list
  of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this
  list of conditions and the following disclaimer in the documentation and/or
  other materials provided with the distribution.
* Neither the name of the nakaikensuke.com nor the names of its contributors may
  be used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
OF SUCH DAMAGE.
*/
package com.yohpapa.research.searchsample;

import android.app.SearchManager;
import android.net.Uri;
import android.provider.BaseColumns;

public interface CustomSuggestionsColumns extends BaseColumns {
	
	public static final String AUTHORITY = "com.yohpapa.research.searchsample.CustomSuggestions";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/suggestions");
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.yohpapa.suggestion";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.yohpapa.suggestion";

	// Required columns
	public static final String TEXT_1 = SearchManager.SUGGEST_COLUMN_TEXT_1;
	
	// Optional columns
	public static final String TEXT_2 = SearchManager.SUGGEST_COLUMN_TEXT_2;
	public static final String ICON_1 = SearchManager.SUGGEST_COLUMN_ICON_1;
	public static final String ICON_2 = SearchManager.SUGGEST_COLUMN_ICON_2;
	public static final String INTENT_ACTION = SearchManager.SUGGEST_COLUMN_INTENT_ACTION;
	public static final String INTENT_DATA = SearchManager.SUGGEST_COLUMN_INTENT_DATA;
	public static final String INTENT_DATA_ID = SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID;
	public static final String INTENT_EXTRA_DATA = SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA;
	public static final String QUERY = SearchManager.SUGGEST_COLUMN_QUERY;
	public static final String SHORTCUT_ID = SearchManager.SUGGEST_COLUMN_SHORTCUT_ID;
	public static final String SPINNER_WHILE_REFRESHING = SearchManager.SUGGEST_COLUMN_SPINNER_WHILE_REFRESHING;
}
