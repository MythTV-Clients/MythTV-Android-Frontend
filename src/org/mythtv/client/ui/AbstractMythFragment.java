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
import org.mythtv.client.MainApplication;
import org.mythtv.db.dvr.ProgramGuideDaoHelper;
import org.mythtv.db.dvr.UpcomingDaoHelper;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.db.status.model.BackendStatus;
import org.mythtv.service.status.BackendStatusService;
import org.mythtv.service.util.RunningServiceHelper;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
//import android.widget.Toast;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractMythFragment extends Fragment implements MythtvApplicationContext {

	protected static final String TAG = AbstractMythFragment.class.getSimpleName();
	
	protected SharedPreferences preferences = null;
	protected EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	protected RunningServiceHelper mRunningServiceHelper = RunningServiceHelper.getInstance();
	protected LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	protected ProgramGuideDaoHelper mProgramGuideDaoHelper = ProgramGuideDaoHelper.getInstance();
	protected UpcomingDaoHelper mUpcomingDaoHelper = UpcomingDaoHelper.getInstance();
	
	private BackendStatusReceiver backendStatusReceiver = new BackendStatusReceiver();
	
	protected BackendStatus mStatus;
	
    // ***************************************
    // MythActivity methods
    // ***************************************
    public MainApplication getMainApplication() {

    	if( null != getActivity() ) {
    		return (MainApplication) getActivity().getApplicationContext();
    	} else {
    		return null;
    	}

    }

    /* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		preferences = getActivity().getSharedPreferences( getString( R.string.app_name ), Context.MODE_PRIVATE );

	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();

		// Unregister for broadcast
		if( null != backendStatusReceiver ) {
			try {
				getActivity().unregisterReceiver( backendStatusReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter backendStatusFilter = new IntentFilter();
		backendStatusFilter.addAction( BackendStatusService.ACTION_COMPLETE );
	    getActivity().registerReceiver( backendStatusReceiver, backendStatusFilter );

	    Log.v( TAG, "onStart : exit" );
	}

	protected void showAlertDialog( final CharSequence title, final CharSequence message ) {
	
    	this.getActivity().runOnUiThread( new Runnable() {
    		
    		/* (non-Javadoc)
    		 * @see java.lang.Runnable#run()
    		 */
    		@Override
    		public void run() {
    			AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
    			builder.setTitle( title );
    			builder.setMessage( message );
    			builder.show();
    		}

    	});
    }
	
    /*
     *  (non-Javadoc)
     *  
     *  Called at the end of BackendStatusTask.onPostExecute() when result is not null.
     */
    protected void onBackendStatusUpdated( BackendStatus result ) { }

    protected void checkBackendStatusService() {

    	startBackendStatusService();

    }
    
    private void startBackendStatusService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.status.BackendStatusService" ) ) {
			getActivity().startService( new Intent( BackendStatusService.ACTION_DOWNLOAD ) );
		}

    }
    
	private class BackendStatusReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( BackendStatusService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "BackendStatusReceiver.onReceive : " + intent.getStringExtra( BackendStatusService.EXTRA_COMPLETE ) );

	        	if( !intent.getBooleanExtra( BackendStatusService.EXTRA_COMPLETE_OFFLINE, false ) ) {
	        		
	        		if( null != intent.getSerializableExtra( BackendStatusService.EXTRA_COMPLETE_DATA ) ) {
	        			mStatus = (BackendStatus) intent.getSerializableExtra( BackendStatusService.EXTRA_COMPLETE_DATA );
	        		
	        			onBackendStatusUpdated( mStatus );
	        		}
	        		
	        	}
	        	
	        }

		}
		
	}

}
