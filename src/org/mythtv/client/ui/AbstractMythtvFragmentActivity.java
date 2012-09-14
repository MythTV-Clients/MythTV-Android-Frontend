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

import org.mythtv.client.MainApplication;
import org.mythtv.service.guide.ProgramGuideDownloadService;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractMythtvFragmentActivity extends FragmentActivity implements MythtvApplicationContext {

	protected static final String TAG = AbstractMythtvFragmentActivity.class.getSimpleName();

	private static final int ABOUT_ID = Menu.FIRST + 1;

	protected Resources resources;

	private ProgramGuideDownloadReceiver programGuideDownloaderReceiver = new ProgramGuideDownloadReceiver();
	
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
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		resources = getResources();
		
		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter programGuideDownloadFilter = new IntentFilter();
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_PROGRESS );
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_COMPLETE );
		programGuideDownloadFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
	    registerReceiver( programGuideDownloaderReceiver, programGuideDownloadFilter );
	    
		Log.v( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();

		// Unregister for broadcast
		if( null != programGuideDownloaderReceiver ) {
			try {
				unregisterReceiver( programGuideDownloaderReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		Log.v( TAG, "onStop : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@TargetApi( 11 )
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );

	    MenuItem prefs = menu.add( Menu.NONE, ABOUT_ID, Menu.NONE, "ABOUT" );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	prefs.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
	    	prefs.setIcon( android.R.drawable.ic_menu_info_details );
	    }
	    
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
		case ABOUT_ID:
			Log.d( TAG, "onOptionsItemSelected : prefs selected" );

		    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		    Fragment prev = getSupportFragmentManager().findFragmentByTag( "aboutDialog" );
		    if( null != prev ) {
		        ft.remove( prev );
		    }
		    ft.addToBackStack( null );

		    DialogFragment newFragment = AboutDialogFragment.newInstance();
		    newFragment.show( ft, "aboutDialog" );
		    
	        return true;
		}

		Log.d( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
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

	private class ProgramGuideDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( ProgramGuideDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : progress=" + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( ProgramGuideDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : complete=" + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_COMPLETE ) );
	        	
	        	Toast.makeText( AbstractMythtvFragmentActivity.this, "Program Guide updated!", Toast.LENGTH_SHORT ).show();
	        }

		}
		
	}

}
