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
import org.mythtv.client.ui.preferences.MythtvPreferenceActivity;
import org.mythtv.client.ui.preferences.MythtvPreferenceActivityHC;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.service.util.NetworkHelper;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * @author Daniel Frey
 *
 */
public class LocationActivity extends FragmentActivity {

	private static final String TAG = LocationActivity.class.getSimpleName();

	private MenuHelper mMenuHelper = MenuHelper.getInstance();
	
	private Button home, away;
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.AbstractMythtvFragmentActivity#onCreate(android.os.Bundle)
	 */
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
	    
		home.setEnabled( false );
		home.setVisibility( View.VISIBLE );
		
		away.setEnabled( false );
		away.setVisibility( View.VISIBLE );

		setupButtons();
		
	    Log.d( TAG, "onResume : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@TargetApi( 11 )
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.d( TAG, "onCreateOptionsMenu : enter" );

		mMenuHelper.prefMenuItem( menu );
		mMenuHelper.aboutMenuItem( menu );
		mMenuHelper.helpSubMenu( menu );

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
		case MenuHelper.EDIT_ID:
			Log.d( TAG, "onOptionsItemSelected : prefs selected" );

		    if( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ) {
				startActivity( new Intent( this, MythtvPreferenceActivity.class ) );
		    } else {
		    	startActivity( new Intent( this, MythtvPreferenceActivityHC.class ) );
		    }
		    
	        return true;

		case MenuHelper.ABOUT_ID:
			Log.d( TAG, "onOptionsItemSelected : about selected" );

			mMenuHelper.handleAboutMenu();
		    
	        return true;
	    
		case MenuHelper.FAQ_ID:
			
			mMenuHelper.handleFaqMenu();
			
			return true;

		case MenuHelper.TROUBLESHOOT_ID:
			
			mMenuHelper.handleTroubleshootMenu();
			
			return true;
		
		case MenuHelper.ISSUES_ID:

			mMenuHelper.handleIssuesMenu();
			
			return true;
		
		}

		Log.d( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	// internal helpers
	
	private void setupButtons() {
		Log.v( TAG, "setupButtons : enter" );
		
		//wifi
		if( NetworkHelper.getInstance().isNetworkConnected() ) {
			Log.d( TAG, "onResume : enabling home" );

			home.setEnabled( true );
			home.setVisibility( View.VISIBLE );
			
		} else {
			Log.d( TAG, "onResume : disabling home" );

			home.setEnabled( false );
			home.setVisibility( View.GONE );
		}

		away.setEnabled( true );
		away.setVisibility( View.VISIBLE );

		Log.v( TAG, "setupButtons : exit" );
	}
	
}
