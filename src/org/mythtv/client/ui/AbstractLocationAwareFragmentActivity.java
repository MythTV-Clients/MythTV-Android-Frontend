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

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

/**
 * @author Daniel Frey
 * 
 */
public abstract class AbstractLocationAwareFragmentActivity extends AbstractMythtvFragmentActivity {

	protected static final String TAG = AbstractLocationAwareFragmentActivity.class.getSimpleName();

	private Long requestId;
	private BroadcastReceiver requestReceiver;

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
		this.registerReceiver( requestReceiver, filter );

		if( requestId == null ) {
			Log.i( TAG, "onResume : loading recordedList" );

			requestId = mDvrServiceHelper.getRecordingedList();
		} else if( mDvrServiceHelper.isRequestPending( requestId ) ) {
			Log.i( TAG, "onResume : recordedList waiting" );
		} else {
			Log.i( TAG, "onResume : recordedList loaded" );
		}

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
		if( null != requestReceiver ) {
			try {
				unregisterReceiver( requestReceiver );
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

	protected void setupActionBar() {
		Log.v( TAG, "setupActionBar : enter" );

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled( true );
		}
		
		Log.v( TAG, "setupActionBar : exit" );
	}

}
