/**
 * 
 */
package org.mythtv.service.dvr;

import org.joda.time.DateTime;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.client.ui.preferences.PlaybackProfile;
import org.mythtv.db.content.LiveStreamDaoHelper;
import org.mythtv.db.content.model.LiveStreamInfo;
import org.mythtv.db.content.model.LiveStreamInfoWrapper;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.model.Program;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.db.preferences.PlaybackProfileDaoHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.Bool;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class LiveStreamService extends MythtvService {

	private static final String TAG = LiveStreamService.class.getSimpleName();

    public static final String ACTION_PLAY = "org.mythtv.background.liveStream.ACTION_PLAY";
    public static final String ACTION_CREATE = "org.mythtv.background.liveStream.ACTION_CREATE";
    public static final String ACTION_UPDATE = "org.mythtv.background.liveStream.ACTION_UPDATE";
    public static final String ACTION_REMOVE = "org.mythtv.background.liveStream.ACTION_REMOVE";

    public static final String ACTION_PROGRESS = "org.mythtv.background.liveStream.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.liveStream.ACTION_COMPLETE";

    public static final String KEY_CHANNEL_ID = "KEY_CHANNEL_ID";
    public static final String KEY_START_TIMESTAMP = "KEY_START_TIMESTAMP";
    
    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_ID = "PROGRESS_ID";
    public static final String EXTRA_PROGRESS_DATA = "PROGRESS_DATA";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_ID = "COMPLETE_ID";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";
    public static final String EXTRA_COMPLETE_ERROR = "COMPLETE_ERROR";
    public static final String EXTRA_COMPLETE_PLAY = "COMPLETE_PLAY";
    public static final String EXTRA_COMPLETE_REMOVE = "COMPLETE_REMOVE";
    public static final String EXTRA_COMPLETE_RAW = "COMPLETE_RAW";

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
			
			sendCompleteOffline();

			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
			return;
		}

		Program program = loadRecorded( intent, locationProfile );
		if( null == program ) {
			
			sendCompleteRecordedProgramNotFound();
			
			Log.d( TAG, "onHandleIntent : exit, Recorded program not found" );
			return;
		}
		
		if( intent.getAction().equals( ACTION_PLAY ) ) {
    		Log.i( TAG, "onHandleIntent : PLAY action selected" );

    		playLiveStream( locationProfile, program );
    		
    		Log.d( TAG, "onHandleIntent : exit, play" );
    		return;
		}

		if( intent.getAction().equals( ACTION_CREATE ) ) {
    		Log.i( TAG, "onHandleIntent : CREATE action selected" );

    		createLiveStream( locationProfile, program );
    		
    		Log.d( TAG, "onHandleIntent : exit, create" );
    		return;
		}
		
		if( intent.getAction().equals( ACTION_UPDATE ) ) {
    		Log.i( TAG, "onHandleIntent : UPDATE action selected" );

    		try {
				updateLiveStream( locationProfile, program );
			} catch( InterruptedException e ) {
				Log.e( TAG, "onHandleIntent : error", e );
			}
    		
    		Log.d( TAG, "onHandleIntent : exit, update" );
    		return;
		}
		
		if( intent.getAction().equals( ACTION_REMOVE ) ) {
    		Log.i( TAG, "onHandleIntent : REMOVE action selected" );

    		removeLiveStream( locationProfile, program );
    		
    		Log.d( TAG, "onHandleIntent : exit, remove" );
    		return;
		}
		
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.d( TAG, "onDestroy : enter" );
		super.onDestroy();

		Log.d( TAG, "onDestroy : exit" );
	}

	// internal helpers

	private void sendProgress( final LiveStreamInfo liveStreamInfo ) {
		Log.v( TAG, "sendProgress : enter" );
		
		Intent progressIntent = new Intent( ACTION_PROGRESS );
		progressIntent.putExtra( EXTRA_PROGRESS, "HLS Processing Update" );
		progressIntent.putExtra( EXTRA_PROGRESS_ID, liveStreamInfo.getId() );
		progressIntent.putExtra( EXTRA_PROGRESS_DATA, liveStreamInfo.getPercentComplete() );
		
		sendBroadcast( progressIntent );
		
		Log.v( TAG, "sendProgress : exit" );
	}
	
	private void sendComplete( final LiveStreamInfo liveStreamInfo ) {
		Log.v( TAG, "sendComplete : enter" );
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "HLS Processing Complete" );
		completeIntent.putExtra( EXTRA_COMPLETE_ID, liveStreamInfo.getId() );
		
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "sendComplete : exit" );
	}

	private void sendCompleteOffline() {
		Log.v( TAG, "sendCompleteOffline : enter" );
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
		completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.TRUE );
		
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "sendCompleteOffline : exit" );
	}

	private void sendCompleteRecordedProgramNotFound() {
		Log.v( TAG, "sendCompleteRecordedProgramNotFound : enter" );
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE_ERROR, "Recorded program not found" );
		
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "sendCompleteRecordedProgramNotFound : exit" );
	}

	private void sendCompletePlay( final LiveStreamInfo liveStreamInfo ) {
		Log.v( TAG, "sendCompletePlay : enter" );
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE_PLAY, Boolean.TRUE );
		completeIntent.putExtra( EXTRA_COMPLETE_ID, liveStreamInfo.getId() );
		completeIntent.putExtra( EXTRA_COMPLETE_RAW, Boolean.FALSE );
		
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "sendCompletePlay : exit" );
	}

	private void sendCompletePlayRaw() {
		Log.v( TAG, "sendCompletePlay : enter" );
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE_PLAY, Boolean.TRUE );
		completeIntent.putExtra( EXTRA_COMPLETE_RAW, Boolean.TRUE );
		
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "sendCompletePlay : exit" );
	}

	private void sendCompleteRemove() {
		Log.v( TAG, "sendCompleteRemove : enter" );
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE_REMOVE, Boolean.TRUE );
		completeIntent.putExtra( EXTRA_COMPLETE, "Program HLS Removed!" );
		
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "sendCompleteRemove : exit" );
	}

	private Program loadRecorded( final Intent intent, final LocationProfile locationProfile ) {
		
		Bundle extras = intent.getExtras();
		int channelId = extras.getInt( KEY_CHANNEL_ID );
		long startTimestamp = extras.getLong( KEY_START_TIMESTAMP );

		return mRecordedDaoHelper.findOne( this, locationProfile, channelId, new DateTime( startTimestamp ) );
	}
	
	private void playLiveStream( final LocationProfile locationProfile, final Program program ) {
		Log.v( TAG, "playLiveStream : enter" );
		
	    LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByProgram( this, locationProfile, program );
	    if( null != liveStreamInfo && liveStreamInfo.getPercentComplete() > 2 ) {
	    	sendCompletePlay( liveStreamInfo );
	    } else {
	    	sendCompletePlayRaw();
	    }
		
		Log.v( TAG, "playLiveStream : exit" );
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
			
			try {
				Log.v( TAG, "createLiveStream : calling api" );
				ResponseEntity<LiveStreamInfoWrapper> wrapper = mMythtvServiceHelper.getMythServicesApi( locationProfile ).contentOperations().
						addLiveStream( null, program.getFilename(), program.getHostname(), -1, -1,
								selectedPlaybackProfile.getHeight(), selectedPlaybackProfile.getVideoBitrate(), 
								selectedPlaybackProfile.getAudioBitrate(), selectedPlaybackProfile.getAudioSampleRate() );

				if( wrapper.getStatusCode().equals( HttpStatus.OK ) ) {
					LiveStreamInfo liveStreamInfo = wrapper.getBody().getLiveStreamInfo();

					saveLiveStreamLocal( locationProfile, liveStreamInfo, program );

					Log.v( TAG, "createLiveStream : live stream created" );
				}
			} catch( Exception e ) {
				Log.e( TAG, "createLiveStream : error", e );
			}
		
		}
		
		Log.v( TAG, "createLiveStream : exit" );
	}

	private void updateLiveStream( final LocationProfile locationProfile, final Program program ) throws InterruptedException {
		Log.v( TAG, "updateLiveStream : enter" );
		
	    LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByProgram( this, locationProfile, program );
		if( null != liveStreamInfo && liveStreamInfo.getPercentComplete() < 100 ) {
			Thread.sleep( 10000 );
			EtagInfoDelegate eTag = EtagInfoDelegate.createEmptyETag();

			try {
				ResponseEntity<LiveStreamInfoWrapper> wrapper = mMythtvServiceHelper.getMythServicesApi( locationProfile ).contentOperations().getLiveStream( liveStreamInfo.getId(), eTag );
				if( wrapper.getStatusCode().equals( HttpStatus.OK ) ) {

					// save updated live stream info to database
					LiveStreamInfo liveStreamStatus = wrapper.getBody().getLiveStreamInfo();
					Log.v( TAG, "updateLiveStream : liveStreamInfo=" + liveStreamInfo.toString() );
					if( !"Unknown status value".equalsIgnoreCase( liveStreamInfo.getStatusStr() ) ) {
						saveLiveStreamLocal( locationProfile, liveStreamStatus, program );

/*						if( liveStreamInfo.getPercentComplete() < 100 ) {
							updateLiveStream( locationProfile, program );
						}
*/					} else {
						removeLiveStreamLocal( locationProfile, liveStreamInfo );
					}

				}
			} catch( Exception e ) {
				Log.e( TAG, "updateLiveStream : error", e );
			}
			
		}
		
		stopSelf();

		Log.v( TAG, "updateLiveStream : exit" );
	}

	private void saveLiveStreamLocal( final LocationProfile locationProfile, final LiveStreamInfo liveStreamInfo, final Program program ) {
		Log.v( TAG, "saveLiveStreamLocal : enter" );
		
		mLiveStreamDaoHelper.save( this, locationProfile, liveStreamInfo, program );

		if( liveStreamInfo.getPercentComplete() < 100 ) {
			sendProgress( liveStreamInfo );
			
//			try {
//				updateLiveStream( locationProfile, program );
//			} catch( InterruptedException e ) {
//				Log.e( TAG, "saveLiveStreamLocal : error", e );
//			}
			
		} else {
			sendComplete( liveStreamInfo );
		}
		
		stopSelf();

		Log.v( TAG, "saveLiveStreamLocal : exit" );
	}
	

	private void removeLiveStream( final LocationProfile locationProfile, final Program program ) {
		Log.v( TAG, "removeLiveStream : enter" );
		
        LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByProgram( this, locationProfile, program );
		if( null != liveStreamInfo ) {
			
			try {
				ResponseEntity<Bool> wrapper = mMythtvServiceHelper.getMythServicesApi( locationProfile ).contentOperations().removeLiveStream( liveStreamInfo.getId() );
				Log.v( TAG, "removeLiveStream : wrapper=" + wrapper.getStatusCode() );
				
				if( wrapper.getStatusCode().equals( HttpStatus.OK ) ) {
					if( wrapper.getBody().getBool().booleanValue() ) {

						removeLiveStreamLocal( locationProfile, liveStreamInfo );

						Log.v( TAG, "removeLive Stream : live stream removed" );

						sendCompleteRemove();
						
					}
					
				}
			} catch( Exception e ) {
				Log.e( TAG, "removeLiveStream : error", e );
			}

		}

		stopSelf();

		Log.v( TAG, "removeLiveStream : exit" );
	}

	private void removeLiveStreamLocal( final LocationProfile locationProfile, final LiveStreamInfo liveStreamInfo ) {
		Log.v( TAG, "removeLiveStreamLocal : enter" );
		
		int deleted = mLiveStreamDaoHelper.delete( this, locationProfile, liveStreamInfo );
		Log.v( TAG, "removeLiveStreamLocal : deleted=" + deleted );
		
		Log.v( TAG, "removeLiveStreamLocal : exit" );
	}

}
