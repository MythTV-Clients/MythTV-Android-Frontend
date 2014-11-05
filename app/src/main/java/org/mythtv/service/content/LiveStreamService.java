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
/**
 * 
 */
package org.mythtv.service.content;

import org.joda.time.DateTime;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.content.LiveStreamDaoHelper;
import org.mythtv.db.content.model.LiveStreamInfo;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.model.Program;
import org.mythtv.service.MythtvService;
import org.mythtv.service.content.v25.LiveStreamHelperV25;
import org.mythtv.service.content.v26.LiveStreamHelperV26;
import org.mythtv.service.content.v27.LiveStreamHelperV27;
import org.mythtv.service.content.v28.LiveStreamHelperV28;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class LiveStreamService extends MythtvService {

	private static final String TAG = LiveStreamService.class.getSimpleName();

    public static final String ACTION_PLAY = "org.mythtv.background.liveStream.ACTION_PLAY";
    public static final String ACTION_CREATE = "org.mythtv.background.liveStream.ACTION_CREATE";
    public static final String ACTION_LOAD = "org.mythtv.background.liveStream.ACTION_LOAD";
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
	private RecordedDaoHelper mRecordedDaoHelper = RecordedDaoHelper.getInstance();
	
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

			PowerManager mgr = (PowerManager) getSystemService( Context.POWER_SERVICE );
			WakeLock wakeLock = mgr.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, "LiveStreamServiceCreate" );
    		
    		try {
        		createLiveStream( locationProfile, program );
			} catch( InterruptedException e ) {
				Log.e( TAG, "onHandleIntent : error", e );
				
				sendCompleteError();
			}
    		finally {
				
    			if( wakeLock.isHeld() ) {
    				wakeLock.release();
    			}
				
			}
    		
    		Log.d( TAG, "onHandleIntent : exit, create" );
    		return;
		}
		
		if( intent.getAction().equals( ACTION_LOAD ) ) {
    		Log.i( TAG, "onHandleIntent : LOAD action selected" );

			PowerManager mgr = (PowerManager) getSystemService( Context.POWER_SERVICE );
			WakeLock wakeLock = mgr.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, "LiveStreamServiceLoad" );

			try {
			
				loadLiveStreams( locationProfile );
			
			} finally {
				
    			if( wakeLock.isHeld() ) {
    				wakeLock.release();
    			}
				
			}
			
    		Log.d( TAG, "onHandleIntent : exit, remove" );
    		return;
		}
		
		if( intent.getAction().equals( ACTION_REMOVE ) ) {
    		Log.i( TAG, "onHandleIntent : REMOVE action selected" );

			PowerManager mgr = (PowerManager) getSystemService( Context.POWER_SERVICE );
			WakeLock wakeLock = mgr.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, "LiveStreamServiceRemove" );

			try {
    		
				removeLiveStream( locationProfile, program );
			
			} finally {
				
    			if( wakeLock.isHeld() ) {
    				wakeLock.release();
    			}
				
			}
			
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

	private void sendCompleteError() {
		Log.v( TAG, "sendComplete : enter" );
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "HLS Processing Error" );
		completeIntent.putExtra( EXTRA_COMPLETE_ERROR, "HLS Processing Failed" );
		
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "sendComplete : exit" );
	}

	private void sendComplete( final LiveStreamInfo liveStreamInfo ) {
		Log.v( TAG, "sendComplete : enter" );
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "HLS Processing Complete" );
		
		if( null != liveStreamInfo ) {
			completeIntent.putExtra( EXTRA_COMPLETE_ID, liveStreamInfo.getId() );
		}
		
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
		completeIntent.putExtra( EXTRA_COMPLETE_ERROR, "Recorded program HLS not found" );
		
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
		Log.v( TAG, "loadRecorded : enter" );
		
		Bundle extras = intent.getExtras();
		int channelId = extras.getInt( KEY_CHANNEL_ID );
		long startTimestamp = extras.getLong( KEY_START_TIMESTAMP );
		Log.v( TAG, "loadRecorded : channelId=" + channelId + ", startTimestamp=" + startTimestamp );

		Log.v( TAG, "loadRecorded : exit" );
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
	
	private boolean createLiveStream( final LocationProfile locationProfile, final Program program ) throws InterruptedException {
		Log.v( TAG, "createLiveStream : enter" );
		
		boolean created = false;
		
		ApiVersion apiVersion = ApiVersion.valueOf( locationProfile.getVersion() );
		switch( apiVersion ) {
			case v025 :

				created = LiveStreamHelperV25.getInstance().create( this, locationProfile, program.getChannelInfo().getChannelId(), program.getStartTime() );

				break;

			case v026 :
				
				created = LiveStreamHelperV26.getInstance().create( this, locationProfile, program.getChannelInfo().getChannelId(), program.getStartTime() );
				
				break;
			case v027 :

				created = LiveStreamHelperV27.getInstance().create( this, locationProfile, program.getChannelInfo().getChannelId(), program.getStartTime() );

				break;
//			case v028 :

//				created = LiveStreamHelperV28.getInstance().create( this, locationProfile, program.getChannelInfo().getChannelId(), program.getStartTime() );

//				break;
				
			default :
				
				created = LiveStreamHelperV27.getInstance().create( this, locationProfile, program.getChannelInfo().getChannelId(), program.getStartTime() );

				break;
		}

		if( created ) {
		
			LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByProgram( this, locationProfile, program );
			if( null != liveStreamInfo ) {

				sendComplete( liveStreamInfo );
				
			}
		
		}
		
		Log.v( TAG, "createLiveStream : exit" );
		return created;
	}

	private void loadLiveStreams( final LocationProfile locationProfile ) {
		Log.v( TAG, "loadLiveStreams : enter" );
		
		Integer loaded = null;
		
		ApiVersion apiVersion = ApiVersion.valueOf( locationProfile.getVersion() );
		switch( apiVersion ) {
			case v025 :
				
				loaded = LiveStreamHelperV25.getInstance().load( this, locationProfile );
				
				break;
			case v026 :
				
				loaded = LiveStreamHelperV26.getInstance().load( this, locationProfile );
				
				break;
			case v027 :

				loaded = LiveStreamHelperV27.getInstance().load( this, locationProfile );

				break;
//			case v028 :

//				loaded = LiveStreamHelperV28.getInstance().load( this, locationProfile );

//				break;

			default :
				
				loaded = LiveStreamHelperV26.getInstance().load( this, locationProfile );

				break;
		}

		if( null != loaded ) {
			
			sendComplete( null );
		
		}

		Log.v( TAG, "loadLiveStreams : exit" );
	}

	private void removeLiveStream( final LocationProfile locationProfile, final Program program ) {
		Log.v( TAG, "removeLiveStream : enter" );
		
		boolean removed = false;
		
		ApiVersion apiVersion = ApiVersion.valueOf( locationProfile.getVersion() );
		switch( apiVersion ) {
			case v025 :
				
				removed = LiveStreamHelperV25.getInstance().remove( this, locationProfile, program.getChannelInfo().getChannelId(), program.getStartTime() );
				
				break;
			case v026 :
				
				removed = LiveStreamHelperV26.getInstance().remove( this, locationProfile, program.getChannelInfo().getChannelId(), program.getStartTime() );
				
				break;
			case v027 :

				removed = LiveStreamHelperV27.getInstance().remove( this, locationProfile, program.getChannelInfo().getChannelId(), program.getStartTime() );

				break;
//			case v028 :

//				removed = LiveStreamHelperV28.getInstance().remove( this, locationProfile, program.getChannelInfo().getChannelId(), program.getStartTime() );

//				break;
				
			default :
				
				removed = LiveStreamHelperV27.getInstance().remove( this, locationProfile, program.getChannelInfo().getChannelId(), program.getStartTime() );

				break;
		}

		if( removed ) {
			
			LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByProgram( this, locationProfile, program );
			if( null == liveStreamInfo ) {
				sendCompleteRemove();
			}
			
		}
		
		Log.v( TAG, "removeLiveStream : exit" );
	}

}
