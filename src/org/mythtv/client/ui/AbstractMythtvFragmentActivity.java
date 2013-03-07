/**
 * This file is part of MythTV Android Frontend
 *
 * MythTV Android Frontend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MythTV Android Frontend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MythTV Android Frontend.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This software can be found at <https://github.com/MythTV-Clients/MythTV-Android-Frontend/>
 */
package org.mythtv.client.ui;

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.db.content.LiveStreamDaoHelper;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.util.MythtvServiceHelper;
import org.mythtv.service.util.RunningServiceHelper;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractMythtvFragmentActivity extends FragmentActivity implements MythtvApplicationContext {

	protected static final String TAG = AbstractMythtvFragmentActivity.class.getSimpleName();

	protected SharedPreferences preferences = null;
	protected LiveStreamDaoHelper mLiveStreamDaoHelper = LiveStreamDaoHelper.getInstance();
	protected MythtvServiceHelper mMythtvServiceHelper = MythtvServiceHelper.getInstance();
	protected LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	protected MenuHelper mMenuHelper = MenuHelper.getInstance();
	protected ProgramGroupDaoHelper mProgramGroupDaoHelper = ProgramGroupDaoHelper.getInstance();
	protected RecordedDaoHelper mRecordedDaoHelper = RecordedDaoHelper.getInstance();
	protected RunningServiceHelper mRunningServiceHelper = RunningServiceHelper.getInstance();
	
	//***************************************
    // MythActivity methods
    //***************************************
	public MainApplication getMainApplication() {
		return (MainApplication) super.getApplicationContext();
	}

	
	//***************************************
    // FragmentActivity methods
    //***************************************
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		setupActionBar();

		preferences = getSharedPreferences( getString( R.string.app_name ), Context.MODE_PRIVATE );
		
		// Fetch screen height and width, to use as our max size when loading images as this activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
        
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        Log.v( TAG, "onCreate : device hxw - " + height + " x " + width );
		
		Log.v( TAG, "onCreate : exit" );
	}


	@TargetApi( 11 )
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );

	    mMenuHelper.aboutMenuItem( this, menu );
	    mMenuHelper.helpSubMenu( this, menu );
	    
		Log.v( TAG, "onCreateOptionsMenu : exit" );
		return super.onCreateOptionsMenu( menu );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.d( TAG, "onOptionsItemSelected : enter" );

		switch( item.getItemId() ) {			
		case MenuHelper.ABOUT_ID:
			Log.d( TAG, "onOptionsItemSelected : about selected" );

			mMenuHelper.handleAboutMenu( this );
		    
	        return true;
	    
		case MenuHelper.FAQ_ID:
			
			mMenuHelper.handleFaqMenu( this );
			
			return true;

		case MenuHelper.TROUBLESHOOT_ID:
			
			mMenuHelper.handleTroubleshootMenu( this );
			
			return true;
		
		case MenuHelper.ISSUES_ID:

			mMenuHelper.handleIssuesMenu( this );
			
			return true;
		
		}

		Log.d( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}
	
	
	/**
	 * @return the preferences
	 */
	public SharedPreferences getSharedPreferences() {
		return preferences;
	}

	public LiveStreamDaoHelper getLiveStreamDaoHelper() {
		return mLiveStreamDaoHelper;
	}
	
	/**
	 * @return the mLocationProfileDaoHelper
	 */
	public LocationProfileDaoHelper getLocationProfileDaoHelper() {
		return mLocationProfileDaoHelper;
	}

	/**
	 * @return the mMenuHelper
	 */
	public MenuHelper getMenuHelper() {
		return mMenuHelper;
	}

	/**
	 * @return the mProgramGroupDaoHelper
	 */
	public ProgramGroupDaoHelper getProgramGroupDaoHelper() {
		return mProgramGroupDaoHelper;
	}

	/**
	 * @return the mRecordedDaoHelper
	 */
	public RecordedDaoHelper getRecordedDaoHelper() {
		return mRecordedDaoHelper;
	}

	/**
	 * @return the mRunningServiceHelper
	 */
	public RunningServiceHelper getRunningServiceHelper() {
		return mRunningServiceHelper;
	}


	// internal helpers
	
	@TargetApi( 11 )
	protected void setupActionBar() {
		Log.v( TAG, "setupActionBar : enter" );

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled( true );
		}
		
		Log.v( TAG, "setupActionBar : exit" );
	}


}
