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
package org.mythtv.client.ui;

import org.mythtv.client.MainApplication;
import org.mythtv.db.status.StatusConstants;
import org.mythtv.db.status.StatusConstants.StatusKey;
import org.mythtv.service.util.RunningServiceHelper;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractMythFragment extends Fragment implements MythtvApplicationContext {

	protected static final String TAG = AbstractMythFragment.class.getSimpleName();
	
	protected RunningServiceHelper mRunningServiceHelper;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mRunningServiceHelper = new RunningServiceHelper( getActivity() );
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	//***************************************
    // MythActivity methods
    //***************************************
	public MainApplication getMainApplication() {
		return (MainApplication) getActivity().getApplicationContext();
	}

	public boolean isMasterBackendConnected() {
		Log.v( TAG, "isMasterBackendConnected : enter" );
		
		Boolean connected = Boolean.FALSE;
		
		Cursor cursor = getActivity().getContentResolver().query( StatusConstants.CONTENT_URI, new String[] { StatusConstants.FIELD_VALUE }, StatusConstants.FIELD_KEY + " = ?", new String[] { StatusKey.MASTER_BACKEND_CONNECTED.name() }, null );
		if( cursor.moveToFirst() ) {
			connected = Boolean.valueOf( cursor.getString( cursor.getColumnIndexOrThrow( StatusConstants.FIELD_VALUE ) ) );
		}
		cursor.close();
		
		Log.v( TAG, "isMasterBackendConnected : exit" );
		return connected.booleanValue();
	}
	
}
