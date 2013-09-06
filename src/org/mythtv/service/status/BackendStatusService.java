/**
 * 
 */
package org.mythtv.service.status;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.db.status.model.BackendStatus;
import org.mythtv.service.MythtvService;
import org.mythtv.service.channel.ChannelDownloadService;
import org.mythtv.service.dvr.RecordedService;
import org.mythtv.service.dvr.RecordingRuleService;
import org.mythtv.service.dvr.UpcomingDownloadService;
import org.mythtv.service.frontends.FrontendsDiscoveryService;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.status.v26.BackendStatusHelperV26;
import org.mythtv.service.status.v27.BackendStatusHelperV27;
import org.mythtv.service.util.RunningServiceHelper;
import org.mythtv.services.api.ApiVersion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class BackendStatusService extends MythtvService {

	private static final String TAG = BackendStatusService.class.getSimpleName();

    public static final String ACTION_DOWNLOAD = "org.mythtv.background.status.ACTION_DOWNLOAD";
    public static final String ACTION_COMPLETE = "org.mythtv.background.status.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATA = "PROGRESS_DATA";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_DATA = "COMPLETE_DATA";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";

    private static final RunningServiceHelper mRunningServiceHelper = RunningServiceHelper.getInstance();
    
	private ChannelDownloadReceiver channelDownloadReceiver = new ChannelDownloadReceiver();
	private FrontendsDiscoveryReceiver frontendsDiscoveryReceiver = new FrontendsDiscoveryReceiver();
	private ProgramGuideDownloadReceiver programGuideDownloadReceiver = new ProgramGuideDownloadReceiver();
	private RecordedDownloadReceiver recordedDownloadReceiver = new RecordedDownloadReceiver();
	private RecordingRuleDownloadReceiver recordingRuleDownloadReceiver = new RecordingRuleDownloadReceiver();
	private UpcomingDownloadReceiver upcomingDownloadReceiver = new UpcomingDownloadReceiver();

	private LocationProfile mLocationProfile;
	
	public BackendStatusService() {
		super( "BackendStatusService" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onCreate()
	 */
	@Override
	public void onCreate() {
		Log.d( TAG, "onCreate : enter" );
		super.onCreate();

		IntentFilter channelDownloadFilter = new IntentFilter();
		channelDownloadFilter.addAction( ChannelDownloadService.ACTION_PROGRESS );
		channelDownloadFilter.addAction( ChannelDownloadService.ACTION_COMPLETE );
	    registerReceiver( channelDownloadReceiver, channelDownloadFilter );

		IntentFilter programGuideDownloadFilter = new IntentFilter( ProgramGuideDownloadService.ACTION_DOWNLOAD );
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_PROGRESS );
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_COMPLETE );
        registerReceiver( programGuideDownloadReceiver, programGuideDownloadFilter );

		IntentFilter recordedDownloadFilter = new IntentFilter( RecordedService.ACTION_DOWNLOAD );
		recordedDownloadFilter.addAction( RecordedService.ACTION_PROGRESS );
		recordedDownloadFilter.addAction( RecordedService.ACTION_COMPLETE );
        registerReceiver( recordedDownloadReceiver, recordedDownloadFilter );

		IntentFilter upcomingDownloadFilter = new IntentFilter();
		upcomingDownloadFilter.addAction( UpcomingDownloadService.ACTION_PROGRESS );
		upcomingDownloadFilter.addAction( UpcomingDownloadService.ACTION_COMPLETE );
	    registerReceiver( upcomingDownloadReceiver, upcomingDownloadFilter );

		IntentFilter recordingRuleDownloadFilter = new IntentFilter( RecordingRuleService.ACTION_DOWNLOAD );
		recordingRuleDownloadFilter.addAction( RecordingRuleService.ACTION_PROGRESS );
		recordingRuleDownloadFilter.addAction( RecordingRuleService.ACTION_COMPLETE );
        registerReceiver( recordingRuleDownloadReceiver, recordingRuleDownloadFilter );

		IntentFilter frontendsDiscoveryFilter = new IntentFilter();
		frontendsDiscoveryFilter.addAction( FrontendsDiscoveryService.ACTION_PROGRESS );
		frontendsDiscoveryFilter.addAction( FrontendsDiscoveryService.ACTION_COMPLETE );
	    registerReceiver( frontendsDiscoveryReceiver, frontendsDiscoveryFilter );

		
		Log.d( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.MythtvService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );
	
		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( this );
		
		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );
    		
    		BackendStatus backendStatus = null;
		
			ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
			switch( apiVersion ) {
				case v026 :
				
					backendStatus = BackendStatusHelperV26.process( this, mLocationProfile );
				
					break;
				case v027 :

					backendStatus = BackendStatusHelperV27.process( this, mLocationProfile );

					break;
				
				default :
				
					backendStatus = BackendStatusHelperV26.process( this, mLocationProfile );

					break;
			}
		
			if( null == backendStatus ) {
				sendCompleteNotConnected();
			} else {
    			
				checkFrontendDiscoveryService();
				checkChannelDownloadService();
				
				sendComplete( backendStatus );
			}
			
		}
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.d( TAG, "onDestroy : enter" );
		super.onDestroy();
		
		// Unregister for broadcast
		if( null != channelDownloadReceiver ) {
			try {
				unregisterReceiver( channelDownloadReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onDestroy : error", e );
			}
		}

		if( null != programGuideDownloadReceiver ) {
			try {
				unregisterReceiver( programGuideDownloadReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onDestroy : error", e );
			}
		}

		if( null != recordedDownloadReceiver ) {
			try {
				unregisterReceiver( recordedDownloadReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onDestroy : error", e );
			}
		}

		if( null != recordingRuleDownloadReceiver ) {
			try {
				unregisterReceiver( recordingRuleDownloadReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onDestroy : error", e );
			}
		}
		
		if( null != upcomingDownloadReceiver ) {
			try {
				unregisterReceiver( upcomingDownloadReceiver );
				upcomingDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onDestroy : error", e );
			}
		}

		if( null != frontendsDiscoveryReceiver ) {
			try {
				unregisterReceiver( frontendsDiscoveryReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onDestroy : error", e );
			}
		}

		Log.d( TAG, "onDestroy : exit" );
	}

	private void sendComplete( BackendStatus backendStatus ) { 
		Log.v( TAG, "sendComplete : enter" );
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "Backend Status Download Service Finished" );
		completeIntent.putExtra( EXTRA_COMPLETE_DATA, backendStatus );
		completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.FALSE );
		
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "sendComplete : exit" );
	}
	
	private void sendCompleteNotConnected() {
		Log.v( TAG, "sendCompleteNotConnected : enter" );
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "Backend Status Download Service Finished" );
		completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.TRUE );
		
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "sendCompleteNotConnected : exit" );
	}
	
    private void checkFrontendDiscoveryService() {
   	 
    	startFrontendDiscoveryService();

    }
    
    private void startFrontendDiscoveryService() {

		if( mLocationProfile.getType().equals( LocationType.HOME ) ) {
			
			if( !mRunningServiceHelper.isServiceRunning( BackendStatusService.this, "org.mythtv.service.frontends.FrontendsDiscoveryService" ) ) {
				startService( new Intent( FrontendsDiscoveryService.ACTION_DISCOVER ) );
			}
			
		}
   	
    }
    
    private void checkChannelDownloadService() {
    	 
		startChannelDownloadService();

    }
    
    private void startChannelDownloadService() {

    	if( !mRunningServiceHelper.isServiceRunning( BackendStatusService.this, "org.mythtv.service.channel.ChannelDownloadService" ) ) {
    		startService( new Intent( ChannelDownloadService.ACTION_DOWNLOAD ) );
		}
   	
    }
    
    private void checkProgramGuideDownloadService() {

		startProgramGuideDownloadService();

    }
    
    private void startProgramGuideDownloadService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( BackendStatusService.this, "org.mythtv.service.guide.ProgramGuideDownloadService" ) ) {
			startService( new Intent( ProgramGuideDownloadService.ACTION_DOWNLOAD ) );
		}

    }
    
    private void checkRecordedDownloadService() {

		startRecordedDownloadService();
		
    }
    
    private void startRecordedDownloadService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( BackendStatusService.this, "org.mythtv.service.dvr.RecordedDownloadService" ) ) {
			startService( new Intent( RecordedService.ACTION_DOWNLOAD ) );
		}

    }
    
    private void checkRecordingRulesDownloadService() {

		startRecordingRulesDownloadService();
		
    }
    
    private void startRecordingRulesDownloadService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( BackendStatusService.this, "org.mythtv.service.dvr.RecordingRuleService" ) ) {
			BackendStatusService.this.startService( new Intent( RecordingRuleService.ACTION_DOWNLOAD ) );
		}

    }
    
    private void checkUpcomingDownloadService() {

    	startUpcomingDownloadService();
		
    }
    
    private void startUpcomingDownloadService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( BackendStatusService.this, "org.mythtv.service.dvr.UpcomingDownloadService" ) ) {
			startService( new Intent( UpcomingDownloadService.ACTION_DOWNLOAD ) );
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
	        	
//	        	if( intent.getBooleanExtra( ChannelDownloadService.EXTRA_COMPLETE_UPTODATE, true ) ) {
//	        		Toast.makeText( getActivity(), "Channels are up to date!", Toast.LENGTH_SHORT ).show();
//	        	} else {
//	        		Toast.makeText( getActivity(), "Channels are NOT up to date!", Toast.LENGTH_SHORT ).show();
//	        	}

        		checkRecordedDownloadService();
        		
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
	        	
//	        	LocationProfile profile = mLocationProfileDaoHelper.findConnectedProfile( BackendStatusService.this );
	        	
//	        	boolean inError = false;
//	        	Cursor errorCursor = getContentResolver().query( ProgramConstants.CONTENT_URI_RECORDED, new String[] { ProgramConstants._ID }, ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_IN_ERROR + " = ? AND " + ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_MASTER_HOSTNAME + " = ?", new String[] { "1", profile.getHostname() }, null );
//	        	if( errorCursor.moveToFirst() ) {
//	        		inError = true;
//	        	}
//	        	errorCursor.close();

//	        	if( intent.getExtras().containsKey( RecordedService.EXTRA_COMPLETE_UPTODATE ) ) {
//	        		Toast.makeText( getActivity(), "Recorded Programs are up to date!" + ( inError ? " (Backend error(s) detected)" : "" ), Toast.LENGTH_SHORT ).show();
//	        	} else if( intent.getExtras().containsKey( RecordedService.EXTRA_COMPLETE_OFFLINE ) ) {
//	        		Toast.makeText( getActivity(), "Recorded Programs Update failed because Master Backend is not connected!", Toast.LENGTH_SHORT ).show();
//	        	} else {
//	        		Toast.makeText( getActivity(), "Recorded Programs updated!" + ( inError ? " (Backend error(s) detected)" : "" ), Toast.LENGTH_SHORT ).show();
//	        	}
	        	
        		checkUpcomingDownloadService();

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
			
	        if ( intent.getAction().equals( RecordingRuleService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "RecordingRuleDownloadReceiver.onReceive : progress=" + intent.getStringExtra( RecordingRuleService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( RecordingRuleService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "RecordingRuleDownloadReceiver.onReceive : complete=" + intent.getStringExtra( RecordingRuleService.EXTRA_COMPLETE ) );
	        	
//	        	if( intent.getExtras().containsKey( RecordingRuleService.EXTRA_COMPLETE_UPTODATE ) ) {
//	        		Toast.makeText( getActivity(), "Recording Rules are up to date!", Toast.LENGTH_SHORT ).show();
//	        	} else if( intent.getExtras().containsKey( RecordingRuleService.EXTRA_COMPLETE_OFFLINE ) ) {
//	        		Toast.makeText( getActivity(), "Recording Rules Update failed because Master Backend is not connected!", Toast.LENGTH_SHORT ).show();
//	        	} else {
//	        		Toast.makeText( getActivity(), "Recording Rules updated!", Toast.LENGTH_SHORT ).show();
//	        	}
	        	
        		checkProgramGuideDownloadService();

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
	        	
//	        	if( intent.getExtras().containsKey( UpcomingDownloadService.EXTRA_COMPLETE_UPTODATE ) ) {
//	        		Toast.makeText( getActivity(), "Upcoming Programs are up to date!", Toast.LENGTH_SHORT ).show();
//	        	} else if( intent.getExtras().containsKey( UpcomingDownloadService.EXTRA_COMPLETE_OFFLINE ) ) {
//	        		Toast.makeText( getActivity(), "Upcoming Programs Update failed because Master Backend is not connected!", Toast.LENGTH_SHORT ).show();
//	        	} else {
//	        		Toast.makeText( getActivity(), "Upcoming Programs updated!", Toast.LENGTH_SHORT ).show();
//	        	}

        		checkRecordingRulesDownloadService();

	        }
	        
		}
		
	}

	private class ProgramGuideDownloadReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( ProgramGuideDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : " + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_PROGRESS ) );
	        	
	        }
	        
	        if ( intent.getAction().equals( ProgramGuideDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : " + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_COMPLETE ) );
	        	
//	        	if( intent.getExtras().containsKey( ProgramGuideDownloadService.EXTRA_COMPLETE_UPTODATE ) ) {
//	        		Toast.makeText( getActivity(), "Program Guide is up to date!", Toast.LENGTH_SHORT ).show();
//	        	} else if( intent.getExtras().containsKey( ProgramGuideDownloadService.EXTRA_COMPLETE_OFFLINE ) ) {
//	        		Toast.makeText( getActivity(), "Program Guide Update failed because Master Backend is not connected!", Toast.LENGTH_SHORT ).show();
//	        	} else {
//	        		Toast.makeText( getActivity(), "Program Guide updated!", Toast.LENGTH_SHORT ).show();
//	        	}
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
