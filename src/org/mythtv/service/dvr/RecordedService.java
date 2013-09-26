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
package org.mythtv.service.dvr;

import org.joda.time.DateTime;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.service.MythtvService;
import org.mythtv.service.dvr.v26.RecordedHelperV26;
import org.mythtv.service.dvr.v27.RecordedHelperV27;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.MythServiceApiRuntimeException;

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
public class RecordedService extends MythtvService {

	private static final String TAG = RecordedService.class.getSimpleName();

    public static final String ACTION_DOWNLOAD = "org.mythtv.background.recorded.ACTION_DOWNLOAD";
    public static final String ACTION_REMOVE = "org.mythtv.background.recorded.ACTION_REMOVE";
    public static final String ACTION_PROGRESS = "org.mythtv.background.recorded.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.recorded.ACTION_COMPLETE";

	public static final String KEY_CHANNEL_ID = "KEY_CHANNEL_ID";
    public static final String KEY_START_TIMESTAMP = "KEY_START_TIMESTAMP";
    public static final String KEY_RECORD_ID = "KEY_RECORD_ID";
    
    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATA = "PROGRESS_DATA";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_UPTODATE = "COMPLETE_UPTODATE";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";
    
	public RecordedService() {
		super( "RecordedDownloadService" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );
		
		boolean passed = true;
		
		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( this );
		
		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

			PowerManager mgr = (PowerManager) getSystemService( Context.POWER_SERVICE );
			WakeLock wakeLock = mgr.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, "RecordedServiceDownload" );

			try {

    			ApiVersion apiVersion = ApiVersion.valueOf( locationProfile.getVersion() );
    			switch( apiVersion ) {
    				case v026 :
    					
    					passed = RecordedHelperV26.getInstance().process( this, locationProfile );
    					
    					break;
    				case v027 :

    					passed = RecordedHelperV27.getInstance().process( this, locationProfile );

    					break;
    					
    				default :
    					
    					passed = RecordedHelperV26.getInstance().process( this, locationProfile );

    					break;
    			}

			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error", e );
				
				passed = false;
			} finally {

    			if( wakeLock.isHeld() ) {
    				wakeLock.release();
    			}
				
    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Recorded Programs Download Service Finished" );
   				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, passed );
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		if ( intent.getAction().equals( ACTION_REMOVE ) ) {
    		Log.i( TAG, "onHandleIntent : REMOVE action selected" );

    		boolean deleted = false;
    		try {

    			Bundle extras = intent.getExtras();
    			int channelId = extras.getInt( KEY_CHANNEL_ID );
    			long startTimestamp = extras.getLong( KEY_START_TIMESTAMP );
    			int recordId = extras.getInt( KEY_RECORD_ID );
    			
    			if( channelId == 0 || startTimestamp == 0 ) {
    				passed = false;
    			} else {
    				deleted = removeRecorded( locationProfile, channelId, new DateTime( startTimestamp ), recordId );
    			}
    			
			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error", e );
				
				passed = false;
			} finally {

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, ( "Episode" + ( !deleted ? " NOT " : " " ) + "deleted!" ) );
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private Boolean removeRecorded( final LocationProfile locationProfile, int channelId, DateTime startTimestamp, int recordId ) throws MythServiceApiRuntimeException {
		Log.v( TAG, "removeRecorded : enter" );
		
		boolean removed = false;

		ApiVersion apiVersion = ApiVersion.valueOf( locationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				
				removed = RecordedHelperV26.getInstance().deleteRecorded( this, locationProfile, channelId, startTimestamp, recordId );
				
				break;
			case v027 :

				removed = RecordedHelperV27.getInstance().deleteRecorded( this, locationProfile, channelId, startTimestamp, recordId );

				break;
				
			default :
				
				removed = RecordedHelperV26.getInstance().deleteRecorded( this, locationProfile, channelId, startTimestamp, recordId );

				break;
		}

		Log.v( TAG, "removeRecorded : exit" );
		return removed;
	}
	
}
