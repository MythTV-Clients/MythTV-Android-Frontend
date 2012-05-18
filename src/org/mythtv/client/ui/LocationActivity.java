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
package org.mythtv.client.ui;

import org.mythtv.R;
import org.mythtv.client.db.MythtvDatabaseManager;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.MythtvPreferenceActivity;
import org.mythtv.client.ui.preferences.PlaybackProfile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * @author Daniel Frey
 *
 */
public class LocationActivity extends AbstractMythtvFragmentActivity {

	private static final String TAG = LocationActivity.class.getSimpleName();
	private static final int EDIT_ID = Menu.FIRST + 2;

	private Button home, away;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.d( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_location );
		
		home = (Button) findViewById( R.id.btn_home );
		away = (Button) findViewById( R.id.btn_away );
		
		Log.d( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	public void onResume() {
	    Log.d( TAG, "onResume : enter" );
	    super.onResume();
	    
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );

		MythtvDatabaseManager db = new MythtvDatabaseManager( this );
		
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );

		LocationProfile selectedAwayLocationProfile = db.fetchSelectedAwayLocationProfile();
		if( null != selectedAwayLocationProfile ) {
			Log.v( TAG, "onResume : setting selected Away Location Profile" );
			
			getApplicationContext().setSelectedAwayLocationProfile( selectedAwayLocationProfile );
		}

		PlaybackProfile selectedAwayPlaybackProfile = db.fetchSelectedAwayPlaybackProfile();
		if( null != selectedAwayPlaybackProfile ) {
			Log.v( TAG, "onResume : setting selected Away Playback Profile" );
			
			getApplicationContext().setSelectedAwayPlaybackProfile( selectedAwayPlaybackProfile );
		}

		//wifi
		State wifi = connectivityManager.getNetworkInfo( 1 ).getState();
		if( wifi == State.CONNECTED || wifi == State.CONNECTING ) {
			Log.d( TAG, "onResume : enabling home" );

			home.setEnabled( true );
			home.setVisibility( View.VISIBLE );
			
			LocationProfile selectedHomeLocationProfile = db.fetchSelectedHomeLocationProfile();
			if( null != selectedHomeLocationProfile ) {
				Log.v( TAG, "onResume : setting selected Home Location Profile" );
				
				getApplicationContext().setSelectedHomeLocationProfile( selectedHomeLocationProfile );
			}

			PlaybackProfile selectedHomePlaybackProfile = db.fetchSelectedHomePlaybackProfile();
			if( null != selectedHomePlaybackProfile ) {
				Log.v( TAG, "onResume : setting selected Home Playback Profile" );
				
				getApplicationContext().setSelectedHomePlaybackProfile( selectedHomePlaybackProfile );
			}
		} else {
			Log.d( TAG, "onResume : disabling home" );

			home.setEnabled( false );
			home.setVisibility( View.GONE );
		}
		
	    Log.d( TAG, "onResume : exit" );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mythtv.client.ui.BaseActivity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.d( TAG, "onCreateOptionsMenu : enter" );

	    MenuItem prefs = menu.add( Menu.NONE, EDIT_ID, Menu.NONE, "Prefs" );
    	prefs.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
	    
		Log.d( TAG, "onCreateOptionsMenu : exit" );
		return super.onCreateOptionsMenu( menu );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mythtv.client.ui.BaseActivity#onOptionsItemSelected(android.view.
	 * MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.d( TAG, "onOptionsItemSelected : enter" );

		switch( item.getItemId() ) {
		case EDIT_ID:
			Log.d( TAG, "onOptionsItemSelected : prefs selected" );

			startActivity( new Intent( this, MythtvPreferenceActivity.class ) );
			
	        return true;
		}

		Log.d( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

}
