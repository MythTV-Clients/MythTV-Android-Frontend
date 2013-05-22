/**
 * 
 */
package org.mythtv.service.dvr;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.client.ui.preferences.PlaybackProfile;
import org.mythtv.db.content.LiveStreamDaoHelper;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.db.preferences.PlaybackProfileDaoHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.Bool;
import org.mythtv.services.api.content.LiveStreamInfo;
import org.mythtv.services.api.content.LiveStreamInfoWrapper;
import org.mythtv.services.api.dvr.Program;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class LiveStreamService extends MythtvService {

	private static final String TAG = LiveStreamService.class.getSimpleName();

    public static final String ACTION_CREATE = "org.mythtv.background.liveStream.ACTION_DOWNLOAD";
    public static final String ACTION_UPDATE = "org.mythtv.background.liveStream.ACTION_UPDATE";
    public static final String ACTION_REMOVE = "org.mythtv.background.liveStream.ACTION_REMOVE";

    public static final String ACTION_PROGRESS = "org.mythtv.background.liveStream.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.liveStream.ACTION_COMPLETE";

    public static final String KEY_CHANNEL_ID = "KEY_CHANNEL_ID";
    public static final String KEY_START_TIMESTAMP = "KEY_START_TIMESTAMP";
    
    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATA = "PROGRESS_DATA";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";

	private NotificationManager mNotificationManager;
	private int notificationId;

	private LiveStreamDaoHelper mLiveStreamDaoHelper = LiveStreamDaoHelper.getInstance();
	private PlaybackProfileDaoHelper mPlaybackProfileDaoHelper = PlaybackProfileDaoHelper.getInstance();
	private RecordedDaoHelper mRecordedDaoHelper = RecordedDaoHelper.getInstance();
	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
	public LiveStreamService() {
		super( "LiveStreamService" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.MythtvService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );

		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( this );
		if( !NetworkHelper.getInstance().isMasterBackendConnected( this, locationProfile ) ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.TRUE );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
			return;
		}

		mNotificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

		Program program = loadRecorded( intent, locationProfile );

		if( intent.getAction().equals( ACTION_CREATE ) ) {
    		Log.i( TAG, "onHandleIntent : CREATE action selected" );

    		createLiveStream( locationProfile, program );
		}
		
		if( intent.getAction().equals( ACTION_UPDATE ) ) {
    		Log.i( TAG, "onHandleIntent : UPDATE action selected" );

    		updateLiveStream( locationProfile );
		}
		
		if( intent.getAction().equals( ACTION_REMOVE ) ) {
    		Log.i( TAG, "onHandleIntent : REMOVE action selected" );

    		removeLiveStream( locationProfile, program );
		}
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers

	private Program loadRecorded( final Intent intent, final LocationProfile locationProfile ) {
		
		Bundle extras = intent.getExtras();
		int channelId = extras.getInt( KEY_CHANNEL_ID );
		long startTimestamp = extras.getLong( KEY_START_TIMESTAMP );

		return mRecordedDaoHelper.findOne( this, locationProfile, channelId, new DateTime( startTimestamp ) );
	}
	
	private void createLiveStream( final LocationProfile locationProfile, final Program program ) {
		Log.v( TAG, "createLiveStream : enter" );
		
		PlaybackProfile selectedPlaybackProfile = null;
		LocationType locationType = locationProfile.getType();
		
		if( locationType.equals( LocationType.HOME ) ) {
			selectedPlaybackProfile = mPlaybackProfileDaoHelper.findSelectedHomeProfile( this );
		} else if( locationType.equals( LocationType.AWAY ) ) {
			selectedPlaybackProfile = mPlaybackProfileDaoHelper.findSelectedAwayProfile( this );
		} else {
			Log.e( TAG, "createLiveStream : Unknown Location!" );
		}

		if( null != selectedPlaybackProfile ) {
			
			Log.v( TAG, "createLiveStream : calling api" );
			ResponseEntity<LiveStreamInfoWrapper> wrapper = mMythtvServiceHelper.getMythServicesApi( locationProfile ).contentOperations().
							addLiveStream( null, program.getFilename(), program.getHostname(), -1, -1,
									selectedPlaybackProfile.getHeight(), selectedPlaybackProfile.getVideoBitrate(), 
									selectedPlaybackProfile.getAudioBitrate(), selectedPlaybackProfile.getAudioSampleRate() );
		
			if( wrapper.getStatusCode().equals( HttpStatus.OK ) ) {
				saveLiveStreamLocal( locationProfile, wrapper.getBody().getLiveStreamInfo(), program );

				Log.v( TAG, "createLiveStream : live stream removed" );
			}
		
		}
		
		Log.v( TAG, "createLiveStream : exit" );
	}

	private void updateLiveStream( final LocationProfile locationProfile ) {
		Log.v( TAG, "updateLiveStream : enter" );
		
		
		
		Log.v( TAG, "updateLiveStream : exit" );
	}

	private void saveLiveStreamLocal( final LocationProfile locationProfile, final LiveStreamInfo liveStreamInfo, final Program program ) {
		Log.v( TAG, "saveLiveStreamLocal : enter" );
		
		mLiveStreamDaoHelper.save( this, locationProfile, liveStreamInfo, program );
		
		Log.v( TAG, "saveLiveStreamLocal : exit" );
	}
	

	private void removeLiveStream( final LocationProfile locationProfile, final Program program ) {
		Log.v( TAG, "removeLiveStream : enter" );
		
        LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByProgram( this, locationProfile, program );
		if( null != liveStreamInfo ) {
			ResponseEntity<Bool> wrapper = mMythtvServiceHelper.getMythServicesApi( locationProfile ).contentOperations().removeLiveStream( liveStreamInfo.getId() );

			if( wrapper.getBody().getBool().booleanValue() ) {

				mLiveStreamDaoHelper.delete( this, locationProfile, liveStreamInfo );

				Log.v( TAG, "removeLive Stream : live stream removed" );
			}

		}
		
		Log.v( TAG, "removeLiveStream : exit" );
	}

	private void removeLiveStreamLocal( final LocationProfile locationProfile, final LiveStreamInfo liveStreamInfo ) {
		Log.v( TAG, "removeLiveStreamLocal : enter" );
		
		mLiveStreamDaoHelper.delete( this, locationProfile, liveStreamInfo );
		
		Log.v( TAG, "removeLiveStreamLocal : exit" );
	}

	@SuppressWarnings( "deprecation" )
	private void sendNotification() {

		long when = System.currentTimeMillis();
		notificationId = (int) when;
		
        Notification mNotification = new Notification( android.R.drawable.stat_notify_sync, getResources().getString( R.string.notification_sync_recordings ), when );

        Intent notificationIntent = new Intent();
        PendingIntent mContentIntent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );

        mNotification.setLatestEventInfo( this, getResources().getString( R.string.app_name ), getResources().getString( R.string.notification_sync_recordings ), mContentIntent );

        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify( notificationId, mNotification );
	
	}
	
    private void completed()    {
        
    	if( null != mNotificationManager ) {
        	mNotificationManager.cancel( notificationId );
    	}
    
    }

}
