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
package org.mythtv.service.dvr;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mythtv.service.dvr.DvrService.Method;
import org.mythtv.service.dvr.DvrService.Resource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class DvrServiceHelper {

	private static final String TAG = DvrServiceHelper.class.getSimpleName();
	
	public static String ACTION_REQUEST_RESULT = "REQUEST_RESULT";
	public static String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
	public static String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

	private static final String REQUEST_ID = "REQUEST_ID";
	private static final String RECORDINGS_HASHKEY = "recordings";
	private static final String UPCOMING_HASHKEY = "upcoming";
	
	private static Object lock = new Object();
	private static DvrServiceHelper instance;
	
	private Map<String,Long> pendingRequests = new HashMap<String,Long>();
	private Context ctx;

	private DvrServiceHelper( Context ctx ) {
		Log.v( TAG, "initialize : enter" );
		
		this.ctx = ctx;

		Log.v( TAG, "initialize : exit" );
	}
	
	public static DvrServiceHelper getInstance( Context ctx ) {
		Log.d( TAG, "getInstance : enter" );

		synchronized( lock ) {
			if( null == instance ){
				instance = new DvrServiceHelper( ctx );			
			}
		}

		Log.d( TAG, "getInstance : exit" );
		return instance;		
	}
	
	public boolean isRequestPending( long requestId ) {
		return pendingRequests.containsValue( requestId );
	}

	public long getRecordingsList() {
		Log.d( TAG, "getRecordingsList : enter" );

		long requestId = generateRequestID();
		pendingRequests.put( RECORDINGS_HASHKEY, requestId );
		
		ResultReceiver serviceCallback = new ResultReceiver(null){

			@Override
			protected void onReceiveResult( int resultCode, Bundle resultData ) {
				handleRecordingsResponse( resultCode, resultData );
			}
		
		};

		Intent intent = new Intent( ctx, DvrService.class );
		intent.putExtra( DvrService.METHOD_EXTRA, Method.GET.name() );
		intent.putExtra( DvrService.RESOURCE_TYPE_EXTRA, Resource.RECORDING_LISTS.name() );
		intent.putExtra( DvrService.SERVICE_CALLBACK, serviceCallback );
		intent.putExtra( REQUEST_ID, requestId );

		ctx.startService( intent );

		Log.d( TAG, "getRecordingsList : exit" );
		return requestId;
	}

	public long getUpcomingList() {
		Log.d( TAG, "getUpcomingList : enter" );

		long requestId = generateRequestID();
		pendingRequests.put( UPCOMING_HASHKEY, requestId );
		
		ResultReceiver serviceCallback = new ResultReceiver(null){

			@Override
			protected void onReceiveResult( int resultCode, Bundle resultData ) {
				handleUpcomingResponse( resultCode, resultData );
			}
		
		};

		Intent intent = new Intent( ctx, DvrService.class );
		intent.putExtra( DvrService.METHOD_EXTRA, Method.GET.name() );
		intent.putExtra( DvrService.RESOURCE_TYPE_EXTRA, Resource.UPCOMING_LISTS.name() );
		intent.putExtra( DvrService.SERVICE_CALLBACK, serviceCallback );
		intent.putExtra( REQUEST_ID, requestId );

		ctx.startService( intent );

		Log.d( TAG, "getUpcomingList : exit" );
		return requestId;
	}

	// internal helpers
	
	private long generateRequestID() {
		return UUID.randomUUID().getLeastSignificantBits();
	}

	private void handleRecordingsResponse( int resultCode, Bundle resultData ){

		Intent origIntent = (Intent) resultData.getParcelable( DvrService.ORIGINAL_INTENT_EXTRA );

		if( null != origIntent ) {
			long requestId = origIntent.getLongExtra( REQUEST_ID, 0 );

			pendingRequests.remove( RECORDINGS_HASHKEY );

			Intent resultBroadcast = new Intent( ACTION_REQUEST_RESULT );
			resultBroadcast.putExtra( EXTRA_REQUEST_ID, requestId );
			resultBroadcast.putExtra( EXTRA_RESULT_CODE, resultCode );

			ctx.sendBroadcast( resultBroadcast );
		}

	}

	private void handleUpcomingResponse( int resultCode, Bundle resultData ){

		Intent origIntent = (Intent) resultData.getParcelable( DvrService.ORIGINAL_INTENT_EXTRA );

		if( null != origIntent ) {
			long requestId = origIntent.getLongExtra( REQUEST_ID, 0 );

			pendingRequests.remove( UPCOMING_HASHKEY );

			Intent resultBroadcast = new Intent( ACTION_REQUEST_RESULT );
			resultBroadcast.putExtra( EXTRA_REQUEST_ID, requestId );
			resultBroadcast.putExtra( EXTRA_RESULT_CODE, resultCode );

			ctx.sendBroadcast( resultBroadcast );
		}

	}

}
