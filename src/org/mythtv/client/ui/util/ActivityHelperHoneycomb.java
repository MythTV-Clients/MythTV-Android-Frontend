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

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * An extension of {@link ActivityHelper} that provides Android 3.0-specific
 * functionality for Honeycomb tablets. It thus requires API level 11.
 */
public class ActivityHelperHoneycomb extends ActivityHelper {
	private Menu mOptionsMenu;

	protected ActivityHelperHoneycomb( Activity activity ) {
		super( activity );
	}

	@Override
	public void onPostCreate( Bundle savedInstanceState ) {
		// Do nothing in onPostCreate. ActivityHelper creates the old action
		// bar, we don't
		// need to for Honeycomb.
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		mOptionsMenu = menu;
		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		switch( item.getItemId() ) {
		case android.R.id.home:
			// Handle the HOME / UP affordance. Since the app is only two levels
			// deep
			// hierarchically, UP always just goes home.
			goHome();
			return true;
		}
		
		return super.onOptionsItemSelected( item );
	}

	/** {@inheritDoc} */
	@Override
	public void setupHomeActivity() {
		super.setupHomeActivity();
		// NOTE: there needs to be a content view set before this is called, so
		// this method
		// should be called in onPostCreate.
		if( UIUtils.isTablet( mActivity ) ) {
			mActivity.getActionBar().setDisplayOptions( 0, ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE );
		} else {
			mActivity.getActionBar().setDisplayOptions( ActionBar.DISPLAY_USE_LOGO, ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE );
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setupSubActivity() {
		super.setupSubActivity();
		// NOTE: there needs to be a content view set before this is called, so
		// this method
		// should be called in onPostCreate.
		if( UIUtils.isTablet( mActivity ) ) {
			mActivity.getActionBar().setDisplayOptions( ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO, ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO );
		} else {
			mActivity.getActionBar().setDisplayOptions( 0, ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO );
		}
	}

	/**
	 * No-op on Honeycomb. The action bar title always remains the same.
	 */
	@Override
	public void setActionBarTitle( CharSequence title ) {
	}

	/**
	 * No-op on Honeycomb. The action bar color always remains the same.
	 */
	@Override
	public void setActionBarColor( int color ) {
		if( !UIUtils.isTablet( mActivity ) ) {
			super.setActionBarColor( color );
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setRefreshActionButtonCompatState( boolean refreshing ) {
		// On Honeycomb, we can set the state of the refresh button by giving it
		// a custom
		// action view.
		if( mOptionsMenu == null ) {
			return;
		}

		// final MenuItem refreshItem =
		// mOptionsMenu.findItem(R.id.menu_refresh);
		// if (refreshItem != null) {
		// if (refreshing) {
		// refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
		// } else {
		// refreshItem.setActionView(null);
		// }
		// }
	}

}
