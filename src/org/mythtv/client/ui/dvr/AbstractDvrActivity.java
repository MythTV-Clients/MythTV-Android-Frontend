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
package org.mythtv.client.ui.dvr;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythtvFragmentActivity;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.db.content.LiveStreamDaoHelper;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.util.FileHelper;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.service.util.RunningServiceHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

/**
 * @author John Baab
 * 
 * 
 */
public abstract class AbstractDvrActivity extends AbstractMythtvFragmentActivity {

	protected static final String TAG = AbstractDvrActivity.class.getSimpleName();

	protected SharedPreferences preferences = null;

	protected FileHelper mFileHelper;
	protected LiveStreamDaoHelper mLiveStreamDaoHelper;
	protected LocationProfileDaoHelper mLocationProfileDaoHelper;
	protected MenuHelper mMenuHelper;
	protected NetworkHelper mNetworkHelper;
	protected ProgramGroupDaoHelper mProgramGroupDaoHelper;
	protected RecordedDaoHelper mRecordedDaoHelper;
	protected RunningServiceHelper mRunningServiceHelper;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		setupActionBar();

		preferences = getSharedPreferences( getString( R.string.app_name ), Context.MODE_PRIVATE );
		
		mFileHelper = FileHelper.newInstance( this );
		mLiveStreamDaoHelper = new LiveStreamDaoHelper( this );
		mLocationProfileDaoHelper = new LocationProfileDaoHelper( this );
		mMenuHelper = MenuHelper.newInstance( this );
		mNetworkHelper = NetworkHelper.newInstance( this );
		mProgramGroupDaoHelper = new ProgramGroupDaoHelper( this );
		mRecordedDaoHelper = new RecordedDaoHelper( this );
		mRunningServiceHelper = RunningServiceHelper.newInstance( this );
		
		Log.v( TAG, "onCreate : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();
		
		Log.v( TAG, "onResume : exit" );
	}

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
		Log.v( TAG, "onPause : enter" );
        super.onPause();

        Log.v( TAG, "onPause : enter" );
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
		Log.v( TAG, "onDestroy : enter" );
		super.onDestroy();
        
        Log.v( TAG, "onDestroy : exit" );
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );


		Log.v( TAG, "onOptionsItemSelected : exit" );
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
	 * @return the mNetworkHelper
	 */
	public NetworkHelper getNetworkHelper() {
		return mNetworkHelper;
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

}
