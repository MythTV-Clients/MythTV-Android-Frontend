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
package org.mythtv.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public abstract class MythtvService extends IntentService {

	protected static final String TAG = MythtvService.class.getSimpleName();
	
	public static enum Method { GET, POST, PUT, DELETE };

	protected static final int REQUEST_INVALID = -1;

	public static final String METHOD_EXTRA = "org.mythtv.service.METHOD_EXTRA";
	public static final String RESOURCE_TYPE_EXTRA = "org.mythtv.service.RESOURCE_TYPE_EXTRA";

	public static final String SERVICE_CALLBACK = "org.mythtv.service.SERVICE_CALLBACK";
	public static final String ORIGINAL_INTENT_EXTRA = "org.mythtv.service.ORIGINAL_INTENT_EXTRA";
	
	public MythtvService( String name ) {
		super( name );
		Log.v( TAG, "initialize : enter" );
		Log.v( TAG, "initialize : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.v( TAG, "onHandleIntent : enter" );

		// NO OP
		
		Log.v( TAG, "onHandleIntent : exit" );
	}

}
