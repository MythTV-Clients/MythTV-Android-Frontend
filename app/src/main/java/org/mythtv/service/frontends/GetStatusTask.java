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
package org.mythtv.service.frontends;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.service.frontends.v25.StatusHelperV25;
import org.mythtv.service.frontends.v26.StatusHelperV26;
import org.mythtv.service.frontends.v27.StatusHelperV27;
import org.mythtv.service.frontends.v28.StatusHelperV28;
import org.mythtv.services.api.ApiVersion;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GetStatusTask extends AsyncTask<String, Void, org.mythtv.db.frontends.model.Status> {

	private static final String TAG = GetStatusTask.class.getSimpleName();
	
	private final Context mContext;
	private final LocationProfile mLocationProfile;
	private final TaskFinishedListener listener;
	
	public interface TaskFinishedListener {

		void onGetStatusTaskStarted();
		 
	    void onGetStatusTaskFinished( org.mythtv.db.frontends.model.Status result );
	    
	}

	public GetStatusTask( Context context, LocationProfile locationProfile, TaskFinishedListener listener ) {
		this.mContext = context;
		this.mLocationProfile = locationProfile;
		this.listener = listener;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
    protected void onPreExecute() {
		Log.d( TAG, "onPreExecute : enter" );
		
        listener.onGetStatusTaskStarted();

        Log.d( TAG, "onPreExecute : exit" );
    }

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected org.mythtv.db.frontends.model.Status doInBackground( String... params ) {
		Log.d( TAG, "doInBackground : enter" );

		if( null == mContext ) {
			throw new IllegalArgumentException( "Context is required" );
		}

		if( null == mLocationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}

		if( null == listener ) {
			throw new IllegalArgumentException( "TaskFinishedListener is required" );
		}

		if( null == params || params.length != 1 ) {
			throw new IllegalArgumentException( "Params is required" );
		}

		String url = params[ 0 ];
		
		org.mythtv.db.frontends.model.Status status = null;
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v025 :
				
				status = StatusHelperV25.getInstance().process( mContext, mLocationProfile, url );
				
				break;
			case v026 :
				
				status = StatusHelperV26.getInstance().process( mContext, mLocationProfile, url );
				
				break;
			case v027 :

				status = StatusHelperV27.getInstance().process( mContext, mLocationProfile, url );
				
				break;
			case v028 :

				status = StatusHelperV28.getInstance().process( mContext, mLocationProfile, url );
				
				break;
				
			default :
				
				status = StatusHelperV27.getInstance().process( mContext, mLocationProfile, url );

				break;
		}

		Log.d( TAG, "doInBackground : exit" );
		return status;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute( org.mythtv.db.frontends.model.Status result ) {
		Log.d( TAG, "onPostExecute : enter" );
		super.onPostExecute( result );

		listener.onGetStatusTaskFinished( result );
		
		Log.d( TAG, "onPostExecute : exit" );
	}


}
