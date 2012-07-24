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

import org.mythtv.service.guide.GuideService;
import org.mythtv.service.guide.GuideServiceHelper;

import android.app.ProgressDialog;
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

	private GuideReceiver guideReceiver;
	private NotifyReceiver notifyReceiver;
	
	private GuideServiceHelper mGuideServiceHelper;
	
	private ProgressDialog mProgressDialog;
	
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

		notifyReceiver = new NotifyReceiver();
		registerReceiver( notifyReceiver, new IntentFilter( GuideService.BROADCAST_ACTION ) );

		mGuideServiceHelper = GuideServiceHelper.getInstance( this );

		IntentFilter guideFilter = new IntentFilter( GuideServiceHelper.GUIDE_RESULT );
		guideFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
        guideReceiver = new GuideReceiver();
        registerReceiver( guideReceiver, guideFilter );
        mGuideServiceHelper.getGuideList();

		Log.v( TAG, "onResume : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.v( TAG, "onPause : enter" );
		super.onPause();

		if( null != mProgressDialog ) {
			mProgressDialog.dismiss();
		}

		// Unregister for broadcast
		if( null != notifyReceiver ) {
			try {
				unregisterReceiver( notifyReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}
		
		if( null != guideReceiver ) {
			try {
				unregisterReceiver( guideReceiver );
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

	private class GuideReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			Log.v( TAG, "GuideReceiver.onReceive : enter" );
			
    		if( null != mProgressDialog ) {
    			mProgressDialog.dismiss();
    		}

			Log.v( TAG, "GuideReceiver.onReceive : exit" );
		}
		
	}
	
    private class NotifyReceiver extends BroadcastReceiver {
    	
        @Override
        public void onReceive( Context context, Intent intent ) {
    		Log.d( TAG, "onReceive : enter" );

    		if( null == mProgressDialog ) {
        		mProgressDialog = ProgressDialog.show( context, "Loading Program Guide", "", true, false );
    		}
    		
    		String message = intent.getExtras().getString( GuideService.BROADCAST_ACTION );
    		
    		mProgressDialog.setMessage( message );
    		
    		Log.d( TAG, "onReceive : exit" );
        }
        
    };

}
