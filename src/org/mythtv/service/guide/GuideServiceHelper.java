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
package org.mythtv.service.guide;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mythtv.service.MythtvService.Method;
import org.mythtv.service.guide.GuideService.Resource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GuideServiceHelper {

	private static final String TAG = GuideServiceHelper.class.getSimpleName();
	
	public static String GUIDE_RESULT = "GUIDE_RESULT";

	public static String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
	public static String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

	private static final String REQUEST_ID = "REQUEST_ID";
	private static final String GUIDE_HASHKEY = "guide";
	
	private static Object lock = new Object();
	private static GuideServiceHelper instance;
	
	private Map<String,Long> pendingRequests = new HashMap<String,Long>();
	private Context ctx;

	private GuideServiceHelper( Context ctx ) {
		Log.v( TAG, "initialize : enter" );
		
		this.ctx = ctx;

		Log.v( TAG, "initialize : exit" );
	}
	
	public static GuideServiceHelper getInstance( Context ctx ) {
		Log.d( TAG, "getInstance : enter" );

		synchronized( lock ) {
			if( null == instance ){
				instance = new GuideServiceHelper( ctx );			
			}
		}

		Log.d( TAG, "getInstance : exit" );
		return instance;		
	}
	
	public boolean isRequestPending( long requestId ) {
		return pendingRequests.containsValue( requestId );
	}

	public long getGuideList() {
		Log.d( TAG, "getGuideList : enter" );

		long requestId = generateRequestID();
		pendingRequests.put( GUIDE_HASHKEY, requestId );
		
		ResultReceiver serviceCallback = new ResultReceiver( null ){

			@Override
			protected void onReceiveResult( int resultCode, Bundle resultData ) {
				handleGuideResponse( resultCode, resultData );
			}
		
		};

		Intent intent = new Intent( ctx, GuideService.class );
		intent.putExtra( GuideService.METHOD_EXTRA, Method.GET.name() );
		intent.putExtra( GuideService.RESOURCE_TYPE_EXTRA, Resource.GUIDE_LISTS.name() );
		intent.putExtra( GuideService.SERVICE_CALLBACK, serviceCallback );
		intent.putExtra( REQUEST_ID, requestId );

		ctx.startService( intent );

		Log.d( TAG, "getGuideList : exit" );
		return requestId;
	}

	// internal helpers
	
	private long generateRequestID() {
		return UUID.randomUUID().getLeastSignificantBits();
	}

	private void handleGuideResponse( int resultCode, Bundle resultData ){

		Intent origIntent = (Intent) resultData.getParcelable( GuideService.ORIGINAL_INTENT_EXTRA );

		if( null != origIntent ) {
			long requestId = origIntent.getLongExtra( REQUEST_ID, 0 );

			pendingRequests.remove( GUIDE_HASHKEY );

			Intent resultBroadcast = new Intent( GUIDE_RESULT );
			resultBroadcast.putExtra( EXTRA_REQUEST_ID, requestId );
			resultBroadcast.putExtra( EXTRA_RESULT_CODE, resultCode );

			ctx.sendBroadcast( resultBroadcast );
		}

	}

}
