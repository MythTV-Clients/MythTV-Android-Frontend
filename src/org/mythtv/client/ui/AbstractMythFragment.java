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
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.channel.ChannelDownloadService;
import org.mythtv.service.dvr.RecordedService;
import org.mythtv.service.dvr.RecordingRuleDownloadService;
import org.mythtv.service.dvr.UpcomingDownloadService;
import org.mythtv.service.frontends.FrontendsDiscoveryService;
import org.mythtv.service.guide.ProgramGuideDownloadServiceNew;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.MythtvServiceHelper;
import org.mythtv.service.util.RunningServiceHelper;
import org.mythtv.services.api.dvr.impl.DvrTemplate;
import org.mythtv.services.api.dvr.impl.DvrTemplate.Endpoint;
import org.mythtv.services.api.guide.impl.GuideTemplate;
import org.mythtv.services.api.status.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
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
	private FrontendsDiscoveryReceiver frontendsDiscoveryReceiver = new FrontendsDiscoveryReceiver();
	private ProgramGuideDownloadReceiver programGuideDownloadReceiver = new ProgramGuideDownloadReceiver();
	private RecordedDownloadReceiver recordedDownloadReceiver = new RecordedDownloadReceiver();
	private RecordingRuleDownloadReceiver recordingRuleDownloadReceiver = new RecordingRuleDownloadReceiver();
	private UpcomingDownloadReceiver upcomingDownloadReceiver = new UpcomingDownloadReceiver();
	
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
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		if( null != programGuideDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( programGuideDownloadReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		if( null != recordedDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( recordedDownloadReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		if( null != recordingRuleDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( recordingRuleDownloadReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}
		
		if( null != upcomingDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( upcomingDownloadReceiver );
				upcomingDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		if( null != frontendsDiscoveryReceiver ) {
			try {
				getActivity().unregisterReceiver( frontendsDiscoveryReceiver );
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
		channelDownloadFilter.addAction( ChannelDownloadService.ACTION_PROGRESS );
		channelDownloadFilter.addAction( ChannelDownloadService.ACTION_COMPLETE );
	    getActivity().registerReceiver( channelDownloadReceiver, channelDownloadFilter );

		IntentFilter programGuideDownloadFilter = new IntentFilter( ProgramGuideDownloadServiceNew.ACTION_DOWNLOAD );
		programGuideDownloadFilter.addAction( ProgramGuideDownloadServiceNew.ACTION_PROGRESS );
		programGuideDownloadFilter.addAction( ProgramGuideDownloadServiceNew.ACTION_COMPLETE );
        getActivity().registerReceiver( programGuideDownloadReceiver, programGuideDownloadFilter );

		IntentFilter recordedDownloadFilter = new IntentFilter( RecordedService.ACTION_DOWNLOAD );
		recordedDownloadFilter.addAction( RecordedService.ACTION_PROGRESS );
		recordedDownloadFilter.addAction( RecordedService.ACTION_COMPLETE );
        getActivity().registerReceiver( recordedDownloadReceiver, recordedDownloadFilter );

		IntentFilter upcomingDownloadFilter = new IntentFilter();
		upcomingDownloadFilter.addAction( UpcomingDownloadService.ACTION_PROGRESS );
		upcomingDownloadFilter.addAction( UpcomingDownloadService.ACTION_COMPLETE );
	    getActivity().registerReceiver( upcomingDownloadReceiver, upcomingDownloadFilter );

		IntentFilter recordingRuleDownloadFilter = new IntentFilter( RecordingRuleDownloadService.ACTION_DOWNLOAD );
		recordingRuleDownloadFilter.addAction( RecordingRuleDownloadService.ACTION_PROGRESS );
		recordingRuleDownloadFilter.addAction( RecordingRuleDownloadService.ACTION_COMPLETE );
        getActivity().registerReceiver( recordingRuleDownloadReceiver, recordingRuleDownloadFilter );

		IntentFilter frontendsDiscoveryFilter = new IntentFilter();
		frontendsDiscoveryFilter.addAction( FrontendsDiscoveryService.ACTION_PROGRESS );
		frontendsDiscoveryFilter.addAction( FrontendsDiscoveryService.ACTION_COMPLETE );
	    getActivity().registerReceiver( frontendsDiscoveryReceiver, frontendsDiscoveryFilter );

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
	
    /**
     * We use the fragment ID as a tag as well so we try both methods of lookup
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
    			EtagInfoDelegate etag = EtagInfoDelegate.createEmptyETag();
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
        			mLocationProfileDaoHelper.save( getActivity(), mLocationProfile );
        			
        			checkChannelDownloadService();

        			if( mLocationProfile.getType().equals( LocationType.HOME ) ) {
        				if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.frontends.FrontendsDiscoveryService" ) ) {
        					getActivity().startService( new Intent( FrontendsDiscoveryService.ACTION_DISCOVER ) );
        				}
        			}
        			
        		} else {

        			Log.d( TAG, "BackendStatusTask.onPostExecute : unsetting connected profile" );
        			mLocationProfile.setConnected( false );
        			mLocationProfileDaoHelper.save( getActivity(), mLocationProfile );
        			
        		}
    		
        		
    		}   
    		
    		Log.i( TAG, "BackendStatusTask.onPostExecute : exit" );
    	}

    }

    private void checkChannelDownloadService() {
 
		startChannelDownloadService();

    }
    
    private void startChannelDownloadService() {

    	if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.channel.ChannelDownloadService" ) ) {
			getActivity().startService( new Intent( ChannelDownloadService.ACTION_DOWNLOAD ) );
		}
   	
    }
    
    private void checkProgramGuideDownloadService() {

		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		DateTime guideEtag = mEtagDaoHelper.findDateByEndpointAndDataId( getActivity(), locationProfile, GuideTemplate.Endpoint.GET_PROGRAM_GUIDE.name(), "" );
		if( null != guideEtag ) {
			
			DateTime now = DateUtils.convertUtc( new DateTime() );
			if( now.getMillis() - guideEtag.getMillis() > ( 24 * 3600000 ) ) {
				startProgramGuideDownloadService();
			}
			
		} else {
		
			startProgramGuideDownloadService();
			
		}

    }
    
    private void startProgramGuideDownloadService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.guide.ProgramGuideDownloadServiceNew" ) ) {
			getActivity().startService( new Intent( ProgramGuideDownloadServiceNew.ACTION_DOWNLOAD ) );
		}

    }
    
    private void checkRecordedDownloadService() {

		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		DateTime recordedEtag = mEtagDaoHelper.findDateByEndpointAndDataId( getActivity(), locationProfile, DvrTemplate.Endpoint.GET_RECORDED_LIST.name(), "" );
		if( null != recordedEtag ) {
			
			DateTime now = DateUtils.convertUtc( new DateTime() );
			if( now.getMillis() - recordedEtag.getMillis() > 3600000 ) {
				startRecordedDownloadService();
			}
			
		} else {
		
			startRecordedDownloadService();
			
		}

    }
    
    private void startRecordedDownloadService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.RecordedDownloadService" ) ) {
			getActivity().startService( new Intent( RecordedService.ACTION_DOWNLOAD ) );
		}

    }
    
    private void checkRecordingRulesDownloadService() {

		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		DateTime recordingRuleEtag = mEtagDaoHelper.findDateByEndpointAndDataId( getActivity(), locationProfile, DvrTemplate.Endpoint.GET_RECORD_SCHEDULE_LIST.name(), "" );
		if( null != recordingRuleEtag ) {
			
			DateTime now = DateUtils.convertUtc( new DateTime() );
			if( now.getMillis() - recordingRuleEtag.getMillis() > 3600000 ) {
				startRecordingRulesDownloadService();
			}
			
		} else {
		
			startRecordingRulesDownloadService();
			
		}

    }
    
    private void startRecordingRulesDownloadService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.RecordingRuleDownloadService" ) ) {
			getActivity().startService( new Intent( RecordingRuleDownloadService.ACTION_DOWNLOAD ) );
		}

    }
    
    private void checkUpcomingDownloadService() {

		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		DateTime upcomingEtag = mEtagDaoHelper.findDateByEndpointAndDataId( getActivity(), locationProfile, Endpoint.GET_UPCOMING_LIST.name(), "" );
		if( null != upcomingEtag ) {
			
			DateTime now = DateUtils.convertUtc( new DateTime() );
			if( now.getMillis() - upcomingEtag.getMillis() > ( 6 * 3600000 ) ) {
				startUpcomingDownloadService();
			}
			
		} else {
		
			startUpcomingDownloadService();
			
		}

    }
    
    private void startUpcomingDownloadService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.UpcomingDownloadService" ) ) {
			getActivity().startService( new Intent( UpcomingDownloadService.ACTION_DOWNLOAD ) );
		}

    }
    
	private class ChannelDownloadReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( ChannelDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "ChannelDownloadReceiver.onReceive : progress=" + intent.getStringExtra( ChannelDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( ChannelDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ChannelDownloadReceiver.onReceive : " + intent.getStringExtra( ChannelDownloadService.EXTRA_COMPLETE ) );
	        	
	        	if( intent.getBooleanExtra( ChannelDownloadService.EXTRA_COMPLETE_UPTODATE, true ) ) {
	        		Toast.makeText( getActivity(), "Channels are up to date!", Toast.LENGTH_SHORT ).show();
	        	} else {
	        		Toast.makeText( getActivity(), "Channels are NOT up to date!", Toast.LENGTH_SHORT ).show();
	        	}

        		checkRecordedDownloadService();
        		checkUpcomingDownloadService();
        		checkRecordingRulesDownloadService();
        		checkProgramGuideDownloadService();
        		
	        }

		}
		
	}

	private class RecordedDownloadReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : enter" );
			
	        if ( intent.getAction().equals( RecordedService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : progress=" + intent.getStringExtra( RecordedService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( RecordedService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : complete=" + intent.getStringExtra( RecordedService.EXTRA_COMPLETE ) );
	        	
	        	LocationProfile profile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
	        	
	        	boolean inError = false;
	        	Cursor errorCursor = getActivity().getContentResolver().query( ProgramConstants.CONTENT_URI_RECORDED, new String[] { ProgramConstants._ID }, ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_IN_ERROR + " = ? AND " + ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_MASTER_HOSTNAME + " = ?", new String[] { "1", profile.getHostname() }, null );
	        	if( errorCursor.moveToFirst() ) {
	        		inError = true;
	        	}
	        	errorCursor.close();

	        	if( intent.getExtras().containsKey( RecordedService.EXTRA_COMPLETE_UPTODATE ) ) {
	        		Toast.makeText( getActivity(), "Recorded Program are up to date!" + ( inError ? " (Backend error(s) detected)" : "" ), Toast.LENGTH_SHORT ).show();
	        	} else if( intent.getExtras().containsKey( RecordedService.EXTRA_COMPLETE_OFFLINE ) ) {
	        		Toast.makeText( getActivity(), "Recorded Programs Update failed because Master Backend is not connected!", Toast.LENGTH_SHORT ).show();
	        	} else {
	        		Toast.makeText( getActivity(), "Recorded Programs updated!" + ( inError ? " (Backend error(s) detected)" : "" ), Toast.LENGTH_SHORT ).show();
	        	}
	        	
	        }

        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : exit" );
		}
		
	}

	private class RecordingRuleDownloadReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.i( TAG, "RecordingRuleDownloadReceiver.onReceive : enter" );
			
	        if ( intent.getAction().equals( RecordingRuleDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "RecordingRuleDownloadReceiver.onReceive : progress=" + intent.getStringExtra( RecordingRuleDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( RecordingRuleDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "RecordingRuleDownloadReceiver.onReceive : complete=" + intent.getStringExtra( RecordingRuleDownloadService.EXTRA_COMPLETE ) );
	        	
	        	if( intent.getExtras().containsKey( RecordingRuleDownloadService.EXTRA_COMPLETE_UPTODATE ) ) {
	        		Toast.makeText( getActivity(), "Recorded Program are up to date!", Toast.LENGTH_SHORT ).show();
	        	} else if( intent.getExtras().containsKey( RecordingRuleDownloadService.EXTRA_COMPLETE_OFFLINE ) ) {
	        		Toast.makeText( getActivity(), "Recording Rules Update failed because Master Backend is not connected!", Toast.LENGTH_SHORT ).show();
	        	} else {
	        		Toast.makeText( getActivity(), "Recording Rules updated!", Toast.LENGTH_SHORT ).show();
	        	}
	        	
	        }

        	Log.i( TAG, "RecordingRuleDownloadReceiver.onReceive : exit" );
		}
		
	}

	private class UpcomingDownloadReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( UpcomingDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "UpcomingDownloadReceiver.onReceive : " + intent.getStringExtra( UpcomingDownloadService.EXTRA_PROGRESS ) );
	        	
	        	String filename = intent.getStringExtra( UpcomingDownloadService.EXTRA_PROGRESS_FILENAME );
	        	if( null != filename && !"".equals( filename ) ) {
	        		Log.d( TAG, "UpcomingDownloadReceiver.onReceive : removing from cache" + filename );
	        	}
	        }
	        
	        if ( intent.getAction().equals( UpcomingDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "UpcomingDownloadReceiver.onReceive : " + intent.getStringExtra( UpcomingDownloadService.EXTRA_COMPLETE ) );
	        	
	        	if( intent.getExtras().containsKey( UpcomingDownloadService.EXTRA_COMPLETE_UPTODATE ) ) {
	        		Toast.makeText( getActivity(), "Upcoming Programs are up to date!", Toast.LENGTH_SHORT ).show();
	        	} else if( intent.getExtras().containsKey( UpcomingDownloadService.EXTRA_COMPLETE_OFFLINE ) ) {
	        		Toast.makeText( getActivity(), "Upcoming Programs Update failed because Master Backend is not connected!", Toast.LENGTH_SHORT ).show();
	        	} else {
	        		Toast.makeText( getActivity(), "Upcoming Programs updated!", Toast.LENGTH_SHORT ).show();
	        	}
	        }
	        
		}
		
	}

	private class ProgramGuideDownloadReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( ProgramGuideDownloadServiceNew.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : " + intent.getStringExtra( ProgramGuideDownloadServiceNew.EXTRA_PROGRESS ) );
	        	
	        }
	        
	        if ( intent.getAction().equals( ProgramGuideDownloadServiceNew.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : " + intent.getStringExtra( ProgramGuideDownloadServiceNew.EXTRA_COMPLETE ) );
	        	
	        	if( intent.getExtras().containsKey( ProgramGuideDownloadServiceNew.EXTRA_COMPLETE_UPTODATE ) ) {
	        		Toast.makeText( getActivity(), "Program Guide is up to date!", Toast.LENGTH_SHORT ).show();
	        	} else if( intent.getExtras().containsKey( ProgramGuideDownloadServiceNew.EXTRA_COMPLETE_OFFLINE ) ) {
	        		Toast.makeText( getActivity(), "Program Guide Update failed because Master Backend is not connected!", Toast.LENGTH_SHORT ).show();
	        	} else {
	        		Toast.makeText( getActivity(), "Program Guide updated!", Toast.LENGTH_SHORT ).show();
	        	}
	        }
	        
		}
		
	}

	private class FrontendsDiscoveryReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( FrontendsDiscoveryService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "FrontendsDiscoveryReceiver.onReceive : progress=" + intent.getStringExtra( FrontendsDiscoveryService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( ChannelDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "FrontendsDiscoveryReceiver.onReceive : " + intent.getStringExtra( FrontendsDiscoveryService.EXTRA_COMPLETE ) );
	        }

		}
		
	}

}
