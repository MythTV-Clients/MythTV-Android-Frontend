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

import org.mythtv.R;
import org.mythtv.client.ui.HomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A class that handles some common activity-related functionality in the app,
 * such as setting up the action bar. This class provides functionality useful
 * for both phones and tablets, and does not require any Android 3.0-specific
 * features.
 */
public class ActivityHelper {
	
	private final static String TAG = ActivityHelper.class.getSimpleName();
	
	protected Activity mActivity;

	/**
	 * Factory method for creating {@link ActivityHelper} objects for a given
	 * activity. Depending on which device the app is running, either a basic
	 * helper or Honeycomb-specific helper will be returned.
	 */
	public static ActivityHelper createInstance( Activity activity ) {
		Log.v( TAG, "createInstance : enter" );
		Log.v( TAG, "createInstance : exit" );
		return UIUtils.isHoneycomb() ? new ActivityHelperHoneycomb( activity ) : new ActivityHelper( activity );
	}

	protected ActivityHelper( Activity activity ) {
		mActivity = activity;
	}

	public void onPostCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onPostCreate : exit" );

		// Create the action bar
		SimpleMenu menu = new SimpleMenu( mActivity );
		mActivity.onCreatePanelMenu( Window.FEATURE_OPTIONS_PANEL, menu );
		// TODO: call onPreparePanelMenu here as well
		for( int i = 0; i < menu.size(); i++ ) {
			MenuItem item = menu.getItem( i );
			addActionButtonCompatFromMenuItem( item );
		}

		Log.v( TAG, "onPostCreate : exit" );
	}

	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );

		//mActivity.getMenuInflater().inflate( R.menu.main_menu, menu );

		Log.v( TAG, "onCreateOptionsMenu : exit" );
		return true;
	}

	public boolean onOptionsItemSelected( MenuItem item ) {
		switch( item.getItemId() ) {
//		case R.id.menu_search:
//			goSearch();
//			return true;
		}
		
		return false;
	}

	public boolean onKeyDown( int keyCode, KeyEvent event ) {
		if( keyCode == KeyEvent.KEYCODE_MENU ) {
			return true;
		}
		return false;
	}

	public boolean onKeyLongPress( int keyCode, KeyEvent event ) {
		if( keyCode == KeyEvent.KEYCODE_BACK ) {
			goHome();
			return true;
		}
		return false;
	}

	/**
	 * Method, to be called in <code>onPostCreate</code>, that sets up this
	 * activity as the home activity for the app.
	 */
	public void setupHomeActivity() {
	}

	/**
	 * Method, to be called in <code>onPostCreate</code>, that sets up this
	 * activity as a sub-activity in the app.
	 */
	public void setupSubActivity() {
	}

	/**
	 * Invoke "home" action, returning to
	 * {@link com.google.SetupActivity.apps.iosched.ui.HomeActivity}.
	 */
	public void goHome() {
		if( mActivity instanceof HomeActivity ) {
			return;
		}

		final Intent intent = new Intent( mActivity, HomeActivity.class );
		intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
		mActivity.startActivity( intent );

//		if( !UIUtils.isHoneycomb() ) {
//			mActivity.overridePendingTransition( R.anim.home_enter, R.anim.home_exit );
//		}
	}

	/**
	 * Invoke "search" action, triggering a default search.
	 */
	public void goSearch() {
		mActivity.startSearch( null, false, Bundle.EMPTY, false );
	}

	/**
	 * Sets up the action bar with the given title and accent color. If title is
	 * null, then the app logo will be shown instead of a title. Otherwise, a
	 * home button and title are visible. If color is null, then the default
	 * colorstrip is visible.
	 */
	public void setupActionBar( CharSequence title, int color ) {
		Log.v( TAG, "setupActionBar : enter" );
		
		final ViewGroup actionBarCompat = getActionBarCompat();
		if( actionBarCompat == null ) {
			Log.v( TAG, "setupActionBar : exit, honeycomb or later" );

			return;
		}

		LinearLayout.LayoutParams springLayoutParams = new LinearLayout.LayoutParams( 0, ViewGroup.LayoutParams.FILL_PARENT );
		springLayoutParams.weight = 1;

		View.OnClickListener homeClickListener = new View.OnClickListener() {
			public void onClick( View view ) {
				goHome();
			}
		};

		if( title != null ) {
			// Add Home button
			addActionButtonCompat( R.drawable.ic_launcher, R.string.app_name, homeClickListener, true );

			// Add title text
			TextView titleText = new TextView( mActivity, null );
			titleText.setLayoutParams( springLayoutParams );
			titleText.setText( title );
			actionBarCompat.addView( titleText );

		} else {
			// Add logo
			ImageButton logo = new ImageButton( mActivity, null );
			logo.setOnClickListener( homeClickListener );
			actionBarCompat.addView( logo );

			// Add spring (dummy view to align future children to the right)
			View spring = new View( mActivity );
			spring.setLayoutParams( springLayoutParams );
			actionBarCompat.addView( spring );
		}

		setActionBarColor( color );

		Log.v( TAG, "setupActionBar : exit" );
	}

	/**
	 * Sets the action bar color to the given color.
	 */
	public void setActionBarColor( int color ) {
		if( color == 0 ) {
			return;
		}

//		final View colorstrip = mActivity.findViewById( R.id.colorstrip );
//		if( colorstrip == null ) {
//			return;
//		}
//
//		colorstrip.setBackgroundColor( color );
	}

	/**
	 * Sets the action bar title to the given string.
	 */
	public void setActionBarTitle( CharSequence title ) {
		Log.v( TAG, "setActionBarTitle : enter" );
		
		ViewGroup actionBar = getActionBarCompat();
		if( actionBar == null ) {
			Log.v( TAG, "setActionBarTitle : exit, no actionBar" );

			return;
		}

		TextView titleText = (TextView) actionBar.findViewById( R.id.actionbar_compat_text );
		if( titleText != null ) {
			titleText.setText( title );
		}

		Log.v( TAG, "setActionBarTitle : exit" );
	}

	/**
	 * Returns the {@link ViewGroup} for the action bar on phones (compatibility
	 * action bar). Can return null, and will return null on Honeycomb.
	 */
	public ViewGroup getActionBarCompat() {
		Log.v( TAG, "getActionBarCompat : enter" );
		Log.v( TAG, "getActionBarCompat : exit" );
		return (ViewGroup) mActivity.findViewById( R.id.actionbar_compat );
	}

	/**
	 * Adds an action bar button to the compatibility action bar (on phones).
	 */
	private View addActionButtonCompat( int iconResId, int textResId, View.OnClickListener clickListener, boolean separatorAfter ) {
		Log.v( TAG, "addActionButtonCompat : enter" );

		final ViewGroup actionBar = getActionBarCompat();
		if( actionBar == null ) {
			Log.v( TAG, "addActionButtonCompat : exit, no actionBar" );

			return null;
		}

		// Create the separator
		ImageView separator = new ImageView( mActivity, null );
		separator.setLayoutParams( new ViewGroup.LayoutParams( 2, ViewGroup.LayoutParams.FILL_PARENT ) );

		// Create the button
		ImageButton actionButton = new ImageButton( mActivity, null );
		actionButton.setLayoutParams( new ViewGroup.LayoutParams( (int) mActivity.getResources().getDimension( R.dimen.actionbar_compat_height ), ViewGroup.LayoutParams.FILL_PARENT ) );
		actionButton.setImageResource( iconResId );
		actionButton.setScaleType( ImageView.ScaleType.CENTER );
		actionButton.setContentDescription( mActivity.getResources().getString( textResId ) );
		actionButton.setOnClickListener( clickListener );

		// Add separator and button to the action bar in the desired order

		if( !separatorAfter ) {
			actionBar.addView( separator );
		}

		actionBar.addView( actionButton );

		if( separatorAfter ) {
			actionBar.addView( separator );
		}

		Log.v( TAG, "addActionButtonCompat : exit" );
		return actionButton;
	}

	/**
	 * Adds an action button to the compatibility action bar, using menu
	 * information from a {@link MenuItem}. If the menu item ID is
	 * <code>menu_refresh</code>, the menu item's state can be changed to show a
	 * loading spinner using
	 * {@link ActivityHelper#setRefreshActionButtonCompatState(boolean)}.
	 */
	private View addActionButtonCompatFromMenuItem( final MenuItem item ) {
		Log.v( TAG, "addActionButtonCompatFromMenuItem : enter" );

		final ViewGroup actionBar = getActionBarCompat();
		if( actionBar == null ) {
			Log.v( TAG, "addActionButtonCompatFromMenuItem : exit, no actionBar" );

			return null;
		}

		// Create the separator
		ImageView separator = new ImageView( mActivity, null );
		separator.setLayoutParams( new ViewGroup.LayoutParams( 2, ViewGroup.LayoutParams.FILL_PARENT ) );

		// Create the button
		ImageButton actionButton = new ImageButton( mActivity, null );
		actionButton.setId( item.getItemId() );
		actionButton.setLayoutParams( new ViewGroup.LayoutParams( (int) mActivity.getResources().getDimension( R.dimen.actionbar_compat_height ), ViewGroup.LayoutParams.FILL_PARENT ) );
		actionButton.setImageDrawable( item.getIcon() );
		actionButton.setScaleType( ImageView.ScaleType.CENTER );
		actionButton.setContentDescription( item.getTitle() );
		actionButton.setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				mActivity.onMenuItemSelected( Window.FEATURE_OPTIONS_PANEL, item );
			}
		} );

		actionBar.addView( separator );
		actionBar.addView( actionButton );

//		if( item.getItemId() == R.id.menu_refresh ) {
//			// Refresh buttons should be stateful, and allow for indeterminate
//			// progress indicators,
//			// so add those.
//			int buttonWidth = mActivity.getResources().getDimensionPixelSize( R.dimen.actionbar_compat_height );
//			int buttonWidthDiv3 = buttonWidth / 3;
//			ProgressBar indicator = new ProgressBar( mActivity, null );
//			LinearLayout.LayoutParams indicatorLayoutParams = new LinearLayout.LayoutParams( buttonWidthDiv3, buttonWidthDiv3 );
//			indicatorLayoutParams.setMargins( buttonWidthDiv3, buttonWidthDiv3, buttonWidth - 2 * buttonWidthDiv3, 0 );
//			indicator.setLayoutParams( indicatorLayoutParams );
//			indicator.setVisibility( View.GONE );
//			indicator.setId( R.id.menu_refresh_progress );
//			actionBar.addView( indicator );
//		}

		Log.v( TAG, "addActionButtonCompatFromMenuItem : exit" );
		return actionButton;
	}

	/**
	 * Sets the indeterminate loading state of a refresh button added with
	 * {@link ActivityHelper#addActionButtonCompatFromMenuItem(android.view.MenuItem)}
	 * (where the item ID was menu_refresh).
	 */
	public void setRefreshActionButtonCompatState( boolean refreshing ) {
//		View refreshButton = mActivity.findViewById( R.id.menu_refresh );
//		View refreshIndicator = mActivity.findViewById( R.id.menu_refresh_progress );
//
//		if( refreshButton != null ) {
//			refreshButton.setVisibility( refreshing ? View.GONE : View.VISIBLE );
//		}
//		if( refreshIndicator != null ) {
//			refreshIndicator.setVisibility( refreshing ? View.VISIBLE : View.GONE );
//		}
	}

}
