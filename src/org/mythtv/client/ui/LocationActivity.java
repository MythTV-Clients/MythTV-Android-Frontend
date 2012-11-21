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
import org.mythtv.service.UpgradeCleanupService;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
	private static final int EDIT_ID = Menu.FIRST + 100;

	private UpgradeCleanupReceiver upgradeCleanupReceiver = new UpgradeCleanupReceiver();
	
	private Button home, away;
	
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.d( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_location );
		
		home = (Button) findViewById( R.id.btn_home );
		away = (Button) findViewById( R.id.btn_away );
		
		mProgressDialog = new ProgressDialog( this );
		
		Log.d( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.d( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter upgradeCleanupFilter = new IntentFilter( UpgradeCleanupService.ACTION_PROGRAMS_CLEANUP );
		upgradeCleanupFilter.addAction( UpgradeCleanupService.ACTION_PROGRESS );
		upgradeCleanupFilter.addAction( UpgradeCleanupService.ACTION_COMPLETE );
        registerReceiver( upgradeCleanupReceiver, upgradeCleanupFilter );

		Log.d( TAG, "onStart : exit" );
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

		startService( new Intent( UpgradeCleanupService.ACTION_PROGRAMS_CLEANUP ) );
		
		setupButtons();
		
	    Log.d( TAG, "onResume : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		Log.d( TAG, "onStop : enter" );
		super.onStop();

		// Unregister for broadcast
		if( null != upgradeCleanupReceiver ) {
			try {
				unregisterReceiver( upgradeCleanupReceiver );
				upgradeCleanupReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}

		Log.d( TAG, "onStop : exit" );
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

	// internal helpers
	
	private void setupButtons() {
		Log.v( TAG, "setupButtons : enter" );
		
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

		away.setEnabled( true );
		away.setVisibility( View.VISIBLE );

		Log.v( TAG, "setupButtons : exit" );
	}
	
	private class UpgradeCleanupReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( UpgradeCleanupService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "UpgradeCleanupReceiver.onReceive : progess=" + intent.getStringExtra( UpgradeCleanupService.EXTRA_PROGESS ) );
	        	
	        	// add processing dialog here
	    		mProgressDialog = ProgressDialog.show( LocationActivity.this, "Please wait...", "Upgrading...", true );
	    		mProgressDialog.getWindow().setGravity( Gravity.TOP );
	    		mProgressDialog.setCancelable( false );
	        	
	        }

	        if ( intent.getAction().equals( UpgradeCleanupService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "UpgradeCleanupReceiver.onReceive : complete=" + intent.getStringExtra( UpgradeCleanupService.EXTRA_COMPLETE ) );
	        	
	        	if( null != intent.getStringExtra( UpgradeCleanupService.EXTRA_COMPLETE ) && !"".equals( intent.getStringExtra( UpgradeCleanupService.EXTRA_COMPLETE ) ) ) {

	        		// remove processing dialog here
	        	    if( mProgressDialog!=null ) {
	        			mProgressDialog.dismiss();
	        			mProgressDialog = null;
	        		}

	        	}

	        	setupButtons();
	        }

		}
		
	}

}
