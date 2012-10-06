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

import org.mythtv.R;
import org.mythtv.client.ui.preferences.MythtvPreferenceActivity;
import org.mythtv.client.ui.preferences.MythtvPreferenceActivityHC;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Bundle;
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

	private Button home;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.d( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_location );
		
		home = (Button) findViewById( R.id.btn_home );
		
		Log.d( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	public void onResume() {
	    Log.d( TAG, "onResume : enter" );
	    super.onResume();
	    
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );

		//wifi
		State wifi = connectivityManager.getNetworkInfo( 1 ).getState();
		if( wifi == State.CONNECTED || wifi == State.CONNECTING ) {
			Log.d( TAG, "onResume : enabling home" );

			home.setEnabled( true );
			home.setVisibility( View.VISIBLE );
			
		} else {
			Log.d( TAG, "onResume : disabling home" );

			home.setEnabled( false );
			home.setVisibility( View.GONE );
		}
		
	    Log.d( TAG, "onResume : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@TargetApi( 11 )
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.d( TAG, "onCreateOptionsMenu : enter" );

	    MenuItem prefs = menu.add( Menu.NONE, EDIT_ID, Menu.NONE, "Prefs" );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	prefs.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
	    	prefs.setIcon( android.R.drawable.ic_menu_preferences );
	    }
	    
		Log.d( TAG, "onCreateOptionsMenu : exit" );
		return super.onCreateOptionsMenu( menu );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.d( TAG, "onOptionsItemSelected : enter" );

		switch( item.getItemId() ) {
		case EDIT_ID:
			Log.d( TAG, "onOptionsItemSelected : prefs selected" );

		    if( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ) {
				startActivity( new Intent( this, MythtvPreferenceActivity.class ) );
		    } else {
		    	startActivity( new Intent( this, MythtvPreferenceActivityHC.class ) );
		    }
		    
	        return true;
		}

		Log.d( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

}
