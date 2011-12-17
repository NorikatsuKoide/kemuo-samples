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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class CustomSuggestionsProvider extends ContentProvider {

	private static final String DB_FILENAME = "suggestions.db";
	private static final int DB_VERSION = 4;
	private static final String TABLE_NAME = CustomSuggestionsProvider.class.getSimpleName();
	
	private static class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context) {
			super(context, DB_FILENAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(
					"CREATE TABLE " + TABLE_NAME + "(" +
					CustomSuggestionsColumns._ID + " INTEGER PRIMARY KEY," +
					CustomSuggestionsColumns.TEXT_1 + " TEXT," +
					CustomSuggestionsColumns.TEXT_2 + " TEXT," +
					CustomSuggestionsColumns.INTENT_DATA + " TEXT" +
					");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}
	
	private static final int SUGGESTIONS = 0;
	private static final int SUGGESTION_ID = 1;
	private static final int SEARCH_SUGGESTION = 2;
	private static final UriMatcher _uriMatcher;
	static {
		_uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		_uriMatcher.addURI(
						CustomSuggestionsColumns.AUTHORITY,
						"suggestions", SUGGESTIONS);
		_uriMatcher.addURI(
						CustomSuggestionsColumns.AUTHORITY,
						"suggestions/#", SUGGESTION_ID);
		_uriMatcher.addURI(
						CustomSuggestionsColumns.AUTHORITY,
						"search_suggest_query", SEARCH_SUGGESTION);
	}
	
	private DBHelper _helper = null;
	
	@Override
	public boolean onCreate() {
		_helper = new DBHelper(getContext());
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = _helper.getWritableDatabase();
		long rowId = db.insert(TABLE_NAME, null, values);
		if(rowId <= 0)
			return null;
		
		Uri recUri = ContentUris.withAppendedId(CustomSuggestionsColumns.CONTENT_URI, rowId);
		getContext().getContentResolver().notifyChange(recUri, null);
		return uri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String where,
			String[] whereArgs, String sortOrder) {
		SQLiteDatabase db = _helper.getWritableDatabase();
		Cursor cursor = null;
		
		switch(_uriMatcher.match(uri)) {
		case SUGGESTIONS:
			cursor = db.query(TABLE_NAME, projection, where, whereArgs, null, null, sortOrder);
			break;
			
		case SUGGESTION_ID:
			String finalWhere =
						CustomSuggestionsColumns._ID + "=" +
						uri.getPathSegments().get(1);
			if(where != null) {
				finalWhere = finalWhere + " AND " + where;
			}
			
			cursor = db.query(TABLE_NAME, projection, finalWhere, whereArgs, null, null, sortOrder);
			break;
			
		case SEARCH_SUGGESTION:
			projection = new String[] {
				CustomSuggestionsColumns._ID,
				CustomSuggestionsColumns.TEXT_1,
				CustomSuggestionsColumns.TEXT_2,
				CustomSuggestionsColumns.INTENT_DATA,
			};
			whereArgs[0] = whereArgs[0] + "%";
			cursor = db.query(TABLE_NAME, projection, where, whereArgs, null, null, sortOrder);
			break;
			
		default:
			break;
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return cursor;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = _helper.getWritableDatabase();
		int count = 0;
		
		switch(_uriMatcher.match(uri)) {
		case SUGGESTIONS:
			count = db.delete(TABLE_NAME, where, whereArgs);
			break;
			
		case SUGGESTION_ID:
			String finalWhere =
						CustomSuggestionsColumns._ID + "=" +
						uri.getPathSegments().get(1);
			if(where != null) {
				finalWhere = finalWhere + " AND " + where;
			}
			
			count = db.delete(TABLE_NAME, finalWhere, whereArgs);
			break;
			
		default:
			break;
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = _helper.getWritableDatabase();
		int count = 0;
		
		switch(_uriMatcher.match(uri)) {
		case SUGGESTIONS:
			count = db.update(TABLE_NAME, values, where, whereArgs);
			break;
			
		case SUGGESTION_ID:
			String finalWhere =
						CustomSuggestionsColumns._ID + "=" +
						uri.getPathSegments().get(1);
			if(where != null) {
				finalWhere = finalWhere + " AND " + where;
			}
			
			count = db.update(TABLE_NAME, values, finalWhere, whereArgs);
			break;
			
		default:
			break;
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch(_uriMatcher.match(uri)) {
		case SUGGESTIONS:
			return CustomSuggestionsColumns.CONTENT_TYPE;
			
		case SUGGESTION_ID:
			return CustomSuggestionsColumns.CONTENT_ITEM_TYPE;
			
		default:
			return null;
		}
	}
}
