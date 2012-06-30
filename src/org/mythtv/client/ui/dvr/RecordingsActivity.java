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
package org.mythtv.client.ui.dvr;

import org.mythtv.R;
import org.mythtv.service.dvr.DvrServiceHelper;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author Daniel Frey
 * 
 */
public class RecordingsActivity extends AbstractRecordingsActivity implements RecordingsFragment.OnProgramGroupListener {

	private static final String TAG = RecordingsActivity.class.getSimpleName();
	private static final int REFRESH_ID = Menu.FIRST + 2;
	
	private long requestId;
	private BroadcastReceiver requestReceiver;

	private DvrServiceHelper mDvrServiceHelper;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.i( TAG, "onCreate : enter" );
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_dvr_recordings );

		RecordingsFragment recordingsFragment = (RecordingsFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_program_groups );
		recordingsFragment.setOnProgramGroupListener( this );

		Log.i( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@TargetApi( 11 )
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );

		MenuItem refresh = menu.add( Menu.NONE, REFRESH_ID, Menu.NONE, "Refresh" );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	refresh.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
	    }
		
		Log.v( TAG, "onCreateOptionsMenu : exit" );
		return true;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractRecordingsActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
		case REFRESH_ID:
			Log.d( TAG, "onOptionsItemSelected : refresh selected" );

			requestId = mDvrServiceHelper.getRecordingedList();
		    
	        return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();

		IntentFilter filter = new IntentFilter( DvrServiceHelper.ACTION_REQUEST_RESULT );
		requestReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive( Context context, Intent intent ) {

				long resultRequestId = intent.getLongExtra( DvrServiceHelper.EXTRA_REQUEST_ID, 0 );

				Log.d( TAG, "Received intent " + intent.getAction() + ", request ID " + resultRequestId );

				if( resultRequestId == requestId ) {

					Log.d( TAG, "Result is for our request ID" );
					
					int resultCode = intent.getIntExtra( DvrServiceHelper.EXTRA_RESULT_CODE, 0 );

					Log.d( TAG, "Result code = " + resultCode );

					if( resultCode == 200 ) {
						Log.d( TAG, "Updating UI with new data" );
					} else {
						Log.d( TAG, "error occurred" );
					}
				} else {
					Log.d( TAG, "Result is NOT for our request ID" );
				}

			}
		};

		mDvrServiceHelper = DvrServiceHelper.getInstance( this );
		registerReceiver( requestReceiver, filter );

		Log.v( TAG, "onResume : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	public void onPause() {
		Log.v( TAG, "onPause : enter" );
		super.onPause();

		// Unregister for broadcast
		if( null != requestReceiver ) {
			try {
				unregisterReceiver( requestReceiver );
				requestReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}
		
		Log.v( TAG, "onPause : exit" );
	}

	public void onProgramGroupSelected( String programGroup ) {
		Log.d( TAG, "onProgramGroupSelected : enter" );
		
		if( null != findViewById( R.id.fragment_dvr_program_group ) ) {
			Log.v( TAG, "onProgramGroupSelected : adding program group to pane" );
			FragmentManager manager = getSupportFragmentManager();

			ProgramGroupFragment programGroupFragment = (ProgramGroupFragment) manager.findFragmentById( R.id.fragment_dvr_program_group );
			FragmentTransaction transaction = manager.beginTransaction();

			if( null == programGroupFragment ) {
				Log.v( TAG, "onProgramGroupSelected : creating new programGroupFragment" );
				programGroupFragment = new ProgramGroupFragment();
			
				transaction
					.add( R.id.fragment_dvr_program_group, programGroupFragment )
					.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
					.addToBackStack( null )
					.commit();
			}
			
			Log.v( TAG, "onProgramGroupSelected : setting program group to display" );
			programGroupFragment.loadPrograms( programGroup );
		} else {
			Log.v( TAG, "onProgramGroupSelected : starting program group activity" );

			Intent i = new Intent( this, ProgramGroupActivity.class );
			i.putExtra( ProgramGroupActivity.EXTRA_PROGRAM_GROUP_KEY, programGroup );
			startActivity( i );
		}

		Log.d( TAG, "onProgramGroupSelected : exit" );
	}

}
