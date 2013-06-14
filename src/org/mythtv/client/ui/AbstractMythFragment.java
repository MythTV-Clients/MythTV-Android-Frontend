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

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.channel.ChannelDownloadService;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.util.MythtvServiceHelper;
import org.mythtv.service.util.RunningServiceHelper;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.channel.impl.ChannelTemplate.Endpoint;
import org.mythtv.services.api.status.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractMythFragment extends Fragment implements MythtvApplicationContext {

	protected static final String TAG = AbstractMythFragment.class.getSimpleName();
	
	protected SharedPreferences preferences = null;
	protected MythtvServiceHelper mMythtvServiceHelper = MythtvServiceHelper.getInstance();
	protected EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	protected RunningServiceHelper mRunningServiceHelper = RunningServiceHelper.getInstance();
	protected LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
	private ChannelDownloadReceiver channelDownloadReceiver = new ChannelDownloadReceiver();

	protected Status mStatus;
	
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
		if( null != channelDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( channelDownloadReceiver );
				//channelDownloadReceiver = null;
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

		IntentFilter channelDownloadFilter = new IntentFilter();
		channelDownloadFilter.addAction( ChannelDownloadService.ACTION_COMPLETE );
	    getActivity().registerReceiver( channelDownloadReceiver, channelDownloadFilter );

	    Log.v( TAG, "onStart : exit" );
	}

	protected void showAlertDialog( final CharSequence title, final CharSequence message ) {
	
    	this.getActivity().runOnUiThread( new Runnable() {
    		
    		@Override
    		public void run() {
    			AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
    			builder.setTitle( title );
    			builder.setMessage( message );
    			builder.show();
    		}

    	});
    }
	
    /**
     * We use the fragment ID as a tag as well so we try both methodes of lookup
     * 
     * @return
     */
    protected Fragment findChildFragmentByIdOrTag(int id) {
    	Fragment frag = this.getChildFragmentManager().findFragmentById( id );
    	if( null != frag ) {
    		return frag;
    	}
    	
    	frag = this.getChildFragmentManager().findFragmentByTag( Integer.toString( id ) );
	
    	return frag;
    }
	
    protected class BackendStatusTask extends AsyncTask<Void, Void, Status> {

    	private LocationProfile mLocationProfile;
    	
    	/* (non-Javadoc)
    	 * @see android.os.AsyncTask#doInBackground(Params[])
    	 */
    	@Override
    	protected org.mythtv.services.api.status.Status doInBackground( Void... params ) {
    		Log.i( TAG, "BackendStatusTask.doInBackground : enter" );

    		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );

    		try {
    			ETagInfo etag = ETagInfo.createEmptyETag();
    			ResponseEntity<org.mythtv.services.api.status.Status> status = mMythtvServiceHelper.getMythServicesApi( mLocationProfile ).statusOperations().getStatus( etag );

    			if( status.getStatusCode() == HttpStatus.OK ) {
    				Log.i( TAG, "BackendStatusTask.doInBackground : exit" );

    				return status.getBody();
    			}
    		} catch( Exception e ) {
    			Log.e( TAG, "BackendStatusTask.doInBackground : error", e );
    		}

    		Log.i( TAG, "BackendStatusTask.doInBackground : exit, status not returned" );
    		return null;
    	}

    	/*
    	 * (non-Javadoc)
    	 * 
    	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
    	 */
    	@Override
    	protected void onPostExecute( 	org.mythtv.services.api.status.Status result ) {
    		Log.i( TAG, "BackendStatusTask.onPostExecute : enter" );

    		if( null != mLocationProfile ) {
    			
        		if( null != result ) {
        			mStatus = result;

        			Log.d( TAG, "BackendStatusTask.onPostExecute : setting connected profile" );
        			mLocationProfile.setConnected( true );

        			DateTime etag = mEtagDaoHelper.findDateByEndpointAndDataId( getActivity(), mLocationProfile, Endpoint.GET_CHANNEL_INFO_LIST.name(), "" );
        			if( null != etag ) {
        				
        				DateTime now = new DateTime();
        				if( now.getMillis() - etag.getMillis() > 86400000 ) {
        					if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.channel.ChannelDownloadService" ) ) {
        						getActivity().startService( new Intent( ChannelDownloadService.ACTION_DOWNLOAD ) );
        					}
        				}
        				
        			} else {
        				if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.channel.ChannelDownloadService" ) ) {
        					getActivity().startService( new Intent( ChannelDownloadService.ACTION_DOWNLOAD ) );
        				}
        			}

        		} else {

        			Log.d( TAG, "BackendStatusTask.onPostExecute : unsetting connected profile" );
        			mLocationProfile.setConnected( false );
        		}
    		
        		mLocationProfileDaoHelper.save( getActivity(), mLocationProfile );
    		}   
    		
    		Log.i( TAG, "BackendStatusTask.onPostExecute : exit" );
    	}

    }

	private class ChannelDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( ChannelDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : progress=" + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( ChannelDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : " + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_COMPLETE ) );
	        	
        		Toast.makeText( getActivity(), "Channels Updated!", Toast.LENGTH_SHORT ).show();

//	        	if( !mRunningServiceHelper.isServiceRunning( "org.mythtv.service.guide.ProgramGuideDownloadServiceNew" ) ) {
//	    			startService( new Intent( ProgramGuideDownloadServiceNew.ACTION_DOWNLOAD ) );
//	    		}

	        }

		}
		
	}

}
