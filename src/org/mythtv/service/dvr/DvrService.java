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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */

package org.mythtv.service.dvr;

import org.mythtv.service.MythtvService;
import org.mythtv.service.dvr.RecordingListProcessor.RecordingListProcessorCallback;

import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class DvrService extends MythtvService {

	private static final String TAG = DvrService.class.getSimpleName();

	public static enum Method { GET, POST, PUT, DELETE };
	public static enum Resource { RECORDING_LISTS };

	private Intent mOriginalRequestIntent;
	private ResultReceiver mCallback;

	public DvrService() {
		super( "DvrService" );
	}

	@Override
	protected void onHandleIntent( Intent requestIntent ) {
		Log.v( TAG, "onHandleIntent : enter" );
		
		mOriginalRequestIntent = requestIntent;
		
		Method method = Method.valueOf( requestIntent.getStringExtra( METHOD_EXTRA ) );
		Resource resourceType = Resource.valueOf( requestIntent.getStringExtra( RESOURCE_TYPE_EXTRA ) );
		mCallback = requestIntent.getParcelableExtra( SERVICE_CALLBACK );

		switch( resourceType ) {
		case RECORDING_LISTS:

			if( method.equals( Method.GET ) ) {
				Log.v( TAG, "onHandleIntent : getting recording list" );
				
				RecordingListProcessor processor = new RecordingListProcessor( getApplicationContext() );
				processor.getRecordedList( makeRecordingListProcessorCallback() );
			} else {
				Log.w( TAG, "onHandleIntent : incorrect method for retrieving recording list" );
				
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
	
	private RecordingListProcessorCallback makeRecordingListProcessorCallback() {
		RecordingListProcessorCallback callback = new RecordingListProcessorCallback() {

			@Override
			public void send( int resultCode ) {
				if( null != mCallback ) {
					mCallback.send( resultCode, getOriginalIntentBundle() );
				}
			}
		};
		
		return callback;
	}


}
