/*
 * Copyright (C) 2008 Jeffrey Sharkey, http://jsharkey.org/ This program is free
 * software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.mythtv.client.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BackendService extends Service {

	private final static String TAG = BackendService.class.getSimpleName();
	
	public final static String PREFS = "mythtv", PREF_MASTER_BACKEND = "master-backend";
	public final static String EXTRA_LIBRARY = "library", EXTRA_ADDRESS = "address", EXTRA_CODE = "code";

	protected String lastaddress = null;
	protected SharedPreferences prefs;
	private final IBinder binder = new BackendBinder();

	public void setMasterBackend( String address ) throws Exception {
		Log.v( TAG, "setMasterBackend : enter" );

		Log.d( TAG, String.format( "trying to create session with address=%s", address ) );

		// if we made it past this point, then we logged in successfully yay
		Log.d( TAG, "yay found session!  were gonna update our db a new code maybe?" );

		// save this ip address to help us start faster
		Editor edit = prefs.edit();
		edit.putString( PREF_MASTER_BACKEND, address );
		edit.commit();

		Log.v( TAG, "setMasterBackend : exit" );
	}

	@Override
	public void onCreate() {
		Log.v( TAG, "onCreate : enter" );

		Log.d( TAG, "onCreate : starting backend service" );

		this.prefs = this.getSharedPreferences( PREFS, Context.MODE_PRIVATE );

		Log.v( TAG, "onCreate : exit" );
	}

	@Override
	public void onDestroy() {
		Log.v( TAG, "onDestroy : enter" );

		// close any dns services and current status threads
		// store information about last-connected library

		Log.d( TAG, "onDestroy : stopping backend service" );

		Log.v( TAG, "onDestroy : exit" );
	}

	public class BackendBinder extends Binder {

		public BackendService getService() {
			Log.v( TAG, "BackendBinder.BackendServices.getService : enter" );
			Log.v( TAG, "BackendBinder.BackendServices.getService : exit" );
			return BackendService.this;
		}
	
	}

	@Override
	public IBinder onBind( Intent intent ) {
		Log.v( TAG, "onBind : enter" );
		Log.v( TAG, "onBind : exit" );
		return binder;
	}

}
