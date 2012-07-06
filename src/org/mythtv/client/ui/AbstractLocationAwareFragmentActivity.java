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
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui;

import org.mythtv.service.dvr.DvrServiceHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

/**
 * @author Daniel Frey
 * 
 */
public abstract class AbstractLocationAwareFragmentActivity extends AbstractMythtvFragmentActivity {

	protected static final String TAG = AbstractLocationAwareFragmentActivity.class.getSimpleName();

	private ProgramListReceiver programListReceiver;
	private UpcomingReceiver upcomingReceiver;
	
	private DvrServiceHelper mDvrServiceHelper;

	// ***************************************
	// FragmentActivity methods
	// ***************************************
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );
		
		resources = getResources();

		setupActionBar();

		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();

		mDvrServiceHelper = DvrServiceHelper.getInstance( this );

		IntentFilter programListFilter = new IntentFilter( DvrServiceHelper.PROGRAM_LIST_RESULT );
		programListFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
        programListReceiver = new ProgramListReceiver();
        registerReceiver( programListReceiver, programListFilter );
        mDvrServiceHelper.getRecordingsList();
        
		IntentFilter upcomingFilter = new IntentFilter( DvrServiceHelper.UPCOMING_RESULT );
		upcomingFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
		upcomingReceiver = new UpcomingReceiver();
        registerReceiver( upcomingReceiver, upcomingFilter );
		mDvrServiceHelper.getUpcomingList();
		
		Log.v( TAG, "onResume : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.v( TAG, "onPause : enter" );
		super.onPause();

		// Unregister for broadcast
		if( null != programListReceiver ) {
			try {
				unregisterReceiver( programListReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}
		
		if( null != upcomingReceiver ) {
			try {
				unregisterReceiver( upcomingReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}

		Log.v( TAG, "onPause : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );

		switch( item.getItemId() ) {
			case android.R.id.home:
				// app icon in action bar clicked; go home
				Intent intent = new Intent( this, LocationActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				startActivity( intent );
				return true;
		}

		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	// internal helpers

	private class ProgramListReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			Log.v( TAG, "ProgramListReceiver.onReceive : enter" );
			
			Log.v( TAG, "ProgramListReceiver.onReceive : exit" );
		}
		
	}
	
	private class UpcomingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			Log.v( TAG, "UpcomingReceiver.onReceive : enter" );
			
			Log.v( TAG, "UpcomingReceiver.onReceive : exit" );
		}
		
	}

}
