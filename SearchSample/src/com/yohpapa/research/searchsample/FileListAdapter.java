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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends ArrayAdapter<FileListGenerator.FileItem> {
	
	public static final int KEY_ITEM_CONTENT = R.string.tag_item_content;
	
	private final LayoutInflater _inflater;
	private boolean _isShort = true;
	
	public FileListAdapter(
			Context context, FileListGenerator.FileItem[] objects, boolean isShort) {
		super(context, R.layout.listitem_file, objects);
		
		_inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_isShort = isShort;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// コンテンツを設定するViewがない場合は最初に初期化しておく
		View view = convertView;
		if(view == null) {
			view = _inflater.inflate(R.layout.listitem_file, null);
		}

		// リスト位置が持っているデータよりも多い場合は何もしない (出来ない)
		if(position >= getCount())
			return view;
		
		// リスト位置に相当するエントリ情報を抽出する
		final FileListGenerator.FileItem item = getItem(position);
		if(item == null)
			return view;

		// クリック時に参照するためタグに対応するエントリ情報を設定しておく
		view.setTag(KEY_ITEM_CONTENT, item);
		
		// エントリ名を設定する
		TextView nameEntry = (TextView)view.findViewById(R.id.entry_name);
		if(nameEntry == null)
			return view;

		String name;
		if(_isShort) {
			name = item.getShortName();
		} else {
			name = item.getLongName();
		}
		
		nameEntry.setText(name);
		
		// エントリアイコンを設定する
		ImageView iconEntry = (ImageView)view.findViewById(R.id.entry_icon);
		if(iconEntry == null)
			return view;
		
		if(item.isDirectory()) {
			iconEntry.setImageResource(R.drawable.folder_icon);
		} else {
			iconEntry.setImageResource(R.drawable.file_icon);
		}
		
		return view;
	}
	
	public void update(boolean isShort) {
		_isShort = isShort;
		notifyDataSetChanged();
	}
}
