/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.util;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/**
 * A <em>really</em> dumb implementation of the {@link MenuItem} interface,
 * that's only useful for our old-actionbar purposes. See
 * <code>com.android.internal.view.menu.MenuItemImpl</code> in AOSP for a more
 * complete implementation.
 */
public class SimpleMenuItem implements MenuItem {

	private SimpleMenu mMenu;

	private final int mId;
	private final int mOrder;
	private CharSequence mTitle;
	private CharSequence mTitleCondensed;
	private Drawable mIconDrawable;
	private int mIconResId = 0;
	private boolean mEnabled = true;

	public SimpleMenuItem( SimpleMenu menu, int id, int order, CharSequence title ) {
		mMenu = menu;
		mId = id;
		mOrder = order;
		mTitle = title;
	}

	public int getItemId() {
		return mId;
	}

	public int getOrder() {
		return mOrder;
	}

	public MenuItem setTitle( CharSequence title ) {
		mTitle = title;
		return this;
	}

	public MenuItem setTitle( int titleRes ) {
		return setTitle( mMenu.getContext().getString( titleRes ) );
	}

	public CharSequence getTitle() {
		return mTitle;
	}

	public MenuItem setTitleCondensed( CharSequence title ) {
		mTitleCondensed = title;
		return this;
	}

	public CharSequence getTitleCondensed() {
		return mTitleCondensed != null ? mTitleCondensed : mTitle;
	}

	public MenuItem setIcon( Drawable icon ) {
		mIconResId = 0;
		mIconDrawable = icon;
		return this;
	}

	public MenuItem setIcon( int iconResId ) {
		mIconDrawable = null;
		mIconResId = iconResId;
		return this;
	}

	public Drawable getIcon() {
		if( mIconDrawable != null ) {
			return mIconDrawable;
		}

		if( mIconResId != 0 ) {
			return mMenu.getResources().getDrawable( mIconResId );
		}

		return null;
	}

	public MenuItem setEnabled( boolean enabled ) {
		mEnabled = enabled;
		return this;
	}

	public boolean isEnabled() {
		return mEnabled;
	}

	// No-op operations. We use no-ops to allow inflation from menu XML.

	public int getGroupId() {
		return 0;
	}

	public View getActionView() {
		return null;
	}

	public MenuItem setIntent( Intent intent ) {
		// Noop
		return this;
	}

	public Intent getIntent() {
		return null;
	}

	public MenuItem setShortcut( char c, char c1 ) {
		// Noop
		return this;
	}

	public MenuItem setNumericShortcut( char c ) {
		// Noop
		return this;
	}

	public char getNumericShortcut() {
		return 0;
	}

	public MenuItem setAlphabeticShortcut( char c ) {
		// Noop
		return this;
	}

	public char getAlphabeticShortcut() {
		return 0;
	}

	public MenuItem setCheckable( boolean b ) {
		// Noop
		return this;
	}

	public boolean isCheckable() {
		return false;
	}

	public MenuItem setChecked( boolean b ) {
		// Noop
		return this;
	}

	public boolean isChecked() {
		return false;
	}

	public MenuItem setVisible( boolean b ) {
		// Noop
		return this;
	}

	public boolean isVisible() {
		return true;
	}

	public boolean hasSubMenu() {
		return false;
	}

	public SubMenu getSubMenu() {
		return null;
	}

	public MenuItem setOnMenuItemClickListener( OnMenuItemClickListener onMenuItemClickListener ) {
		// Noop
		return this;
	}

	public ContextMenu.ContextMenuInfo getMenuInfo() {
		return null;
	}

	public void setShowAsAction( int i ) {
		// Noop
	}

	public MenuItem setActionView( View view ) {
		// Noop
		return this;
	}

	public MenuItem setActionView( int i ) {
		// Noop
		return this;
	}

	@Override
	public boolean collapseActionView() {
		return false;
	}

	@Override
	public boolean expandActionView() {
		return false;
	}

	@Override
	public ActionProvider getActionProvider() {
		return null;
	}

	@Override
	public boolean isActionViewExpanded() {
		return false;
	}

	@Override
	public MenuItem setActionProvider( ActionProvider actionProvider ) {
		return null;
	}

	@Override
	public MenuItem setOnActionExpandListener( OnActionExpandListener listener ) {
		return null;
	}

	@Override
	public MenuItem setShowAsActionFlags( int actionEnum ) {
		return null;
	}

}
