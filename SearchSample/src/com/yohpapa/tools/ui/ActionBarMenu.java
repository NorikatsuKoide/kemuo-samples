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
package com.yohpapa.tools.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

public class ActionBarMenu implements Menu {

	private final Resources _resources;
	private List<ActionBarMenuItem> _menuItems;
	
	public ActionBarMenu(Context context) {
		_resources = context.getResources();
		_menuItems = new ArrayList<ActionBarMenuItem>();
	}
	
	public Resources getResources() {
		return _resources;
	}

	@Override
	public MenuItem add(CharSequence title) {
		return addMenuItem(0, 0, 0, title.toString());
	}

	@Override
	public MenuItem add(int titleRes) {
		return addMenuItem(0, 0, 0, _resources.getString(titleRes));
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
		return addMenuItem(groupId, itemId, order, title.toString());
	}

	@Override
	public MenuItem add(int groupId, int itemId, int order, int titleRes) {
		return addMenuItem(groupId, itemId, order, _resources.getString(titleRes));
	}
	
	private MenuItem addMenuItem(int groupId, int itemId, int order, String title) {
		final ActionBarMenuItem item = new ActionBarMenuItem(this, itemId, order, title);
		_menuItems.add(findInsertIndex(_menuItems, order), item);
		return item;
	}
	
	private static int findInsertIndex(List<? extends MenuItem> items, int order) {
		for(int i = items.size() - 1; i >= 0; i--) {
			MenuItem item = items.get(i);
			if (item.getOrder() <= order)
				return i + 1;
		}
		return 0;
	}

	@Override
	public void clear() {
		_menuItems.clear();
	}

	@Override
	public MenuItem findItem(int id) {
		for (MenuItem item : _menuItems) {
			if (item.getItemId() == id)
				return item;
		}
		return null;
	}

	@Override
	public MenuItem getItem(int index) {
		if(index >= _menuItems.size())
			return null;
		
		return _menuItems.get(index);
	}

	@Override
	public void removeItem(int id) {
		removeItemAtInt(findItemIndex(id));
	}

	private int findItemIndex(int id) {
		final int size = size();
		for(int i = 0; i < size; i++) {
			MenuItem item = _menuItems.get(i);
			if (item.getItemId() == id)
				return i;
		}
		return -1;
    }
	
	private void removeItemAtInt(int index) {
		if((index < 0) || (index >= _menuItems.size()))
			return;

		_menuItems.remove(index);
    }
	
	@Override
	public int size() {
		return _menuItems.size();
	}

	@Override
	public int addIntentOptions(int groupId, int itemId, int order,
			ComponentName caller, Intent[] specifics, Intent intent, int flags,
			MenuItem[] outSpecificItems) {
		throw new UnsupportedOperationException("addIntentOptions is not supported");
	}

	@Override
	public SubMenu addSubMenu(CharSequence title) {
		throw new UnsupportedOperationException("addSubMenu is not supported");
	}

	@Override
	public SubMenu addSubMenu(int titleRes) {
		throw new UnsupportedOperationException("addSubMenu is not supported");
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
		throw new UnsupportedOperationException("addSubMenu is not supported");
	}

	@Override
	public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
		throw new UnsupportedOperationException("addSubMenu is not supported");
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException("close is not supported");
	}

	@Override
	public boolean hasVisibleItems() {
		throw new UnsupportedOperationException("hasVisibleItems is not supported");
	}

	@Override
	public boolean isShortcutKey(int keyCode, KeyEvent event) {
		throw new UnsupportedOperationException("isShortcutKey is not supported");
	}

	@Override
	public boolean performIdentifierAction(int id, int flags) {
		throw new UnsupportedOperationException("performIdentifierAction is not supported");
	}

	@Override
	public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
		throw new UnsupportedOperationException("performShortcut is not supported");
	}

	@Override
	public void removeGroup(int groupId) {
		throw new UnsupportedOperationException("removeGroup is not supported");
	}

	@Override
	public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
		throw new UnsupportedOperationException("setGroupCheckable is not supported");
	}

	@Override
	public void setGroupEnabled(int group, boolean enabled) {
		throw new UnsupportedOperationException("setGroupEnabled is not supported");
	}

	@Override
	public void setGroupVisible(int group, boolean visible) {
		throw new UnsupportedOperationException("setGroupVisible is not supported");
	}

	@Override
	public void setQwertyMode(boolean isQwerty) {
		throw new UnsupportedOperationException("setQwertyMode is not supported");
	}
}
