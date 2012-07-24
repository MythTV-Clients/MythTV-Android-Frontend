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

import org.mythtv.service.AbstractMythtvProcessor.NotifyCallback;
import org.mythtv.service.MythtvService;
import org.mythtv.service.guide.GuideProcessor.GuideProcessorCallback;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GuideService extends MythtvService {

	protected static final String TAG = GuideService.class.getSimpleName();

	public static enum Resource { GUIDE_LISTS };

    public static final String BROADCAST_ACTION = "org.mythtv.service.guide.broadcast.DOWNLOAD_STATUS";
    private final Handler handler = new Handler();
	private Intent broadcastIntent;

    private Intent mOriginalRequestIntent;
	private ResultReceiver mCallback;

	public GuideService() {
		super( "GuideService" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onCreate()
	 */
	@Override
	public void onCreate() {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate();
		
		broadcastIntent = new Intent( BROADCAST_ACTION );	

		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart( Intent intent, int startId ) {
		Log.v( TAG, "onStart : enter" );
		super.onStart( intent, startId );
		
		handler.removeCallbacks( sendUpdatesToUI );

		Log.v( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.MythtvService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent requestIntent ) {
		Log.v( TAG, "onHandleIntent : enter" );
		
		mOriginalRequestIntent = requestIntent;
		
		Method method = Method.valueOf( requestIntent.getStringExtra( METHOD_EXTRA ) );
		Resource resourceType = Resource.valueOf( requestIntent.getStringExtra( RESOURCE_TYPE_EXTRA ) );
		mCallback = requestIntent.getParcelableExtra( SERVICE_CALLBACK );

		switch( resourceType ) {
		case GUIDE_LISTS:

			if( method.equals( Method.GET ) ) {
				Log.v( TAG, "onHandleIntent : getting guide list" );
				
				GuideProcessor processor = new GuideProcessor( getApplicationContext() );
				processor.getProgramGuide( makeGuideProcessorCallback(), makeNotifyCallback() );
			} else {
				Log.w( TAG, "onHandleIntent : incorrect method for retrieving program guide" );
				
				mCallback.send( REQUEST_INVALID, getOriginalIntentBundle() );
			}
			
			break;

		default:
			Log.w( TAG, "onHandleIntent : incorrect request" );

			mCallback.send( REQUEST_INVALID, getOriginalIntentBundle() );
		
			break;
		}

		Log.v( TAG, "onHandleIntent : exit" );
	}

	protected Bundle getOriginalIntentBundle() {
		Bundle originalRequest = new Bundle();
		originalRequest.putParcelable( ORIGINAL_INTENT_EXTRA, mOriginalRequestIntent );
		
		return originalRequest;
	}

	// internal helpers
	
	private Runnable sendUpdatesToUI = new Runnable() {
	    
		public void run() {
			Log.d( TAG, "Thread.sendUpdatesToUI.run : enter" );

			sendBroadcast( broadcastIntent );

    	    Log.d( TAG, "Thread.sendUpdatesToUI.run : exit" );
    	}

	};

	private GuideProcessorCallback makeGuideProcessorCallback() {
		GuideProcessorCallback callback = new GuideProcessorCallback() {

			@Override
			public void send( int resultCode ) {
				if( null != mCallback ) {
					mCallback.send( resultCode, getOriginalIntentBundle() );
				}
			}

		};
		
		return callback;
	}

	private NotifyCallback makeNotifyCallback() {
		NotifyCallback callback = new NotifyCallback() {

			@Override
			public void notify( String message ) {
				broadcastIntent.putExtra( BROADCAST_ACTION, message );
				
				handler.postDelayed( sendUpdatesToUI, 1000 );				
			}
			
		};
		
		return callback;
	}
	
}
