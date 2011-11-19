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
package com.yohpapa.research.actionbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

public class ActionBarMenuItem implements MenuItem {

	private final ActionBarMenu _parent;
	private final int _id;
	private final int _order;
	private String _title;
	private String _titleCondensed;
	private Drawable _iconDrawable;
	private boolean _enabled;
	
	public ActionBarMenuItem(ActionBarMenu parent, int id, int order, String title) {
		_parent = parent;
		_id = id;
		_iconDrawable = null;
		_order = order;
		_title = title;
		_titleCondensed = title;
		_enabled = true;
	}
	
	@Override
	public Drawable getIcon() {
		return _iconDrawable;
	}

	@Override
	public MenuItem setIcon(Drawable icon) {
		_iconDrawable = icon;
		return this;
	}

	@Override
	public MenuItem setIcon(int iconRes) {
		_iconDrawable = _parent.getResources().getDrawable(iconRes);
		return this;
	}

	@Override
	public int getItemId() {
		return _id;
	}

	@Override
	public int getOrder() {
		return _order;
	}

	@Override
	public CharSequence getTitle() {
		return _title;
	}

	@Override
	public MenuItem setTitle(CharSequence title) {
		if(title != null) {
			_title = title.toString();
		}
		return this;
	}

	@Override
	public MenuItem setTitle(int title) {
		setTitle(_parent.getResources().getString(title));
		return this;
	}

	@Override
	public CharSequence getTitleCondensed() {
		if(_titleCondensed == null)
			return _title;
		
		return _titleCondensed;
	}

	@Override
	public MenuItem setTitleCondensed(CharSequence title) {
		if(title != null) {
			_titleCondensed = title.toString();
		}
		return this;
	}

	@Override
	public boolean isEnabled() {
		return _enabled;
	}

	@Override
	public MenuItem setEnabled(boolean enabled) {
		_enabled = enabled;
		return this;
	}

	@Override public boolean collapseActionView() {return false;}
	@Override public boolean expandActionView() {return false;}
	@Override public ActionProvider getActionProvider() {return null;}
	@Override public View getActionView() {return null;}
	@Override public char getAlphabeticShortcut() {return 0;}
	@Override public int getGroupId() {return 0;}
	@Override public Intent getIntent() {return null;}
	@Override public ContextMenuInfo getMenuInfo() {return null;}
	@Override public char getNumericShortcut() {return 0;}
	@Override public SubMenu getSubMenu() {return null;}
	@Override public boolean hasSubMenu() {return false;}
	@Override public boolean isActionViewExpanded() {return false;}
	@Override public boolean isCheckable() {return false;}
	@Override public boolean isChecked() {return false;}
	@Override public boolean isVisible() {return true;}
	@Override public MenuItem setActionProvider(ActionProvider actionProvider) {return this;}
	@Override public MenuItem setActionView(View view) {return this;}
	@Override public MenuItem setActionView(int resId) {return this;}
	@Override public MenuItem setAlphabeticShortcut(char alphaChar) {return this;}
	@Override public MenuItem setCheckable(boolean checkable) {return this;}
	@Override public MenuItem setChecked(boolean checked) {return this;}
	@Override public MenuItem setIntent(Intent intent) {return this;}
	@Override public MenuItem setNumericShortcut(char numericChar) {return this;}
	@Override public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {return this;}
	@Override public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {return this;}
	@Override public MenuItem setShortcut(char numericChar, char alphaChar) {return this;}
	@Override public void setShowAsAction(int actionEnum) {}
	@Override public MenuItem setShowAsActionFlags(int actionEnum) {return this;}
	@Override public MenuItem setVisible(boolean visible) {return this;}
}
