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
package org.mythtv.service.myth;

import java.util.List;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.service.myth.v25.HostHelperV25;
import org.mythtv.service.myth.v26.HostHelperV26;
import org.mythtv.service.myth.v27.HostHelperV27;
import org.mythtv.service.myth.v28.HostHelperV28;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GetHostsTask extends AsyncTask<Void, Void, List<String>> {

	private static final String TAG = GetHostsTask.class.getSimpleName();
	
	private final Context mContext;
	private final LocationProfile mLocationProfile;
	private final TaskFinishedListener listener;
	
	public interface TaskFinishedListener {

		void onGetHostsTaskStarted();
		 
	    void onGetHostsTaskFinished( List<String> result );
	    
	}

	public GetHostsTask( Context context, LocationProfile locationProfile, TaskFinishedListener listener ) {
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
		
        listener.onGetHostsTaskStarted();

        Log.d( TAG, "onPreExecute : exit" );
    }

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected List<String> doInBackground( Void... params ) {
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

		List<String> hosts = null;
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( mContext, mLocationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + mLocationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}

		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v025 :
				
				hosts = HostHelperV25.getInstance().process( mContext, mLocationProfile );
				
				break;
			case v026 :
				
				hosts = HostHelperV26.getInstance().process( mContext, mLocationProfile );
				
				break;
			case v027 :

				hosts = HostHelperV27.getInstance().process( mContext, mLocationProfile );
				
				break;
			case v028 :

				hosts = HostHelperV28.getInstance().process( mContext, mLocationProfile );
				
				break;
				
			default :
				
				hosts = HostHelperV27.getInstance().process( mContext, mLocationProfile );

				break;
		}

		Log.d( TAG, "doInBackground : exit" );
		return hosts;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute( List<String> result ) {
		Log.d( TAG, "onPostExecute : enter" );
		super.onPostExecute( result );

		listener.onGetHostsTaskFinished( result );
		
		Log.d( TAG, "onPostExecute : exit" );
	}

}
