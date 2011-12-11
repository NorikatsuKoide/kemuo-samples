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

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.yohpapa.research.searchsample.FileListGenerator.FileItem;

public class FragmentFileList extends ListFragment {
	@SuppressWarnings("unused")
	private static final String TAG = FragmentFileList.class.getSimpleName();
	
	private static final String ARG_PATH = "PATH";
	public static FragmentFileList newInstance(String path) {
		
		FragmentFileList fragment = new FragmentFileList();
		Bundle bundle = new Bundle();
		bundle.putString(ARG_PATH, path);
		fragment.setArguments(bundle);
		
		return fragment;
	}

	private static final String KEY_CURRENT_PATH = "KEY_CURRENT_PATH";
	
	private Handler _handler = new Handler();
	private String _currentPath = Environment.getExternalStorageDirectory().toString();
	
	private final Observer _observer = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			if((Integer)data != SearchSampleApp.NAME_TYPE_CHANGED)
				return;
			
			FileListAdapter adapter = (FileListAdapter)getListAdapter();
			if(adapter == null)
				return;
			
			adapter.update(PreferenceManager.getNameType(getActivity()));
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState == null) {
			Bundle arg = getArguments();
			if(arg == null) {
				return;
			}
			_currentPath = arg.getString(ARG_PATH);
		} else {
			_currentPath = savedInstanceState.getString(KEY_CURRENT_PATH);
		}
		
		SearchSampleActivity parent = (SearchSampleActivity)getActivity();
		parent.addObserver(_observer);
		
		FileListGenerator.start(_currentPath, null, _fileListCallback);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		FileListGenerator.cancel();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putString(KEY_CURRENT_PATH, _currentPath);
	}

	private final FileListGenerator.Callback _fileListCallback = new FileListGenerator.Callback() {
		@Override
		public void notifyFileList(final FileListGenerator.FileItem[] files) {
			_handler.post(new Runnable() {
				@Override
				public void run() {
					setListAdapter(
							new FileListAdapter(
									getActivity(),
									files,
									PreferenceManager.getNameType(getActivity())));
				}
			});
		}
	};
	
	@Override
	public void onListItemClick(ListView list, View view, int position, long id) {
		
		FileItem item = (FileItem)view.getTag(FileListAdapter.KEY_ITEM_CONTENT);
		if(item == null)
			return;
		
		if(!item.isDirectory()) {
			// TODO:
			// ここでmimeをとってIntentを投げる
			Toast.makeText(getActivity(), item.getLongName(), Toast.LENGTH_SHORT).show();
			return;
		}
		
		String path = _currentPath + File.separator + item.getLongName();
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(
				R.anim.fragment_slide_right_enter,
				R.anim.fragment_slide_left_exit,
				R.anim.fragment_slide_left_enter,
				R.anim.fragment_slide_right_exit);

		Fragment next = FragmentFileList.newInstance(path);
		ft.replace(R.id.fragment_filelist, next);
		ft.addToBackStack(null);
		ft.commit();
	}
	
	public String getCurrentPath() {
		return _currentPath;
	}
}
