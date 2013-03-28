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
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.util.MythtvServiceHelper;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.status.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractMythFragment extends Fragment implements MythtvApplicationContext {

	protected static final String TAG = AbstractMythFragment.class.getSimpleName();
	
	protected MythtvServiceHelper mMythtvServiceHelper = MythtvServiceHelper.getInstance();
	protected LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
	protected Status mStatus;
	
	//***************************************
    // MythActivity methods
    //***************************************
	public MainApplication getMainApplication() {
		
		if( null != getActivity() ) {
			return (MainApplication) getActivity().getApplicationContext();
		} else {
			return null;
		}
	
	}

	protected void showAlertDialog( final CharSequence title, final CharSequence message ) {
		this.getActivity().runOnUiThread( new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
				builder.setTitle( title );
				builder.setMessage( message );
				builder.show();
			}

		} );
	}
	
	protected class BackendStatusTask extends AsyncTask<Void, Void, Status> {

		@Override
		protected org.mythtv.services.api.status.Status doInBackground( Void... params ) {
			Log.i( TAG, "BackendStatusTask.doInBackground : enter" );
			
			LocationProfile mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );

			ETagInfo etag = ETagInfo.createEmptyETag();
			ResponseEntity<org.mythtv.services.api.status.Status> status = mMythtvServiceHelper.getMythServicesApi( mLocationProfile ).statusOperations().getStatus( etag );
			if( status.getStatusCode() == HttpStatus.OK ) {
				Log.i( TAG, "BackendStatusTask.doInBackground : exit" );
			
				return status.getBody();
			}
			
			Log.i( TAG, "BackendStatusTask.doInBackground : exit, status not returned" );
			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute( org.mythtv.services.api.status.Status result ) {
			Log.i( TAG, "BackendStatusTask.onPostExecute : enter" );
			super.onPostExecute( result );

			if( null != result ) {
				mStatus = result;
			}
			
			Log.i( TAG, "BackendStatusTask.onPostExecute : exit" );
		}
		
	}

}
