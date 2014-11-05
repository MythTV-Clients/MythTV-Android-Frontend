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

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.service.myth.v25.SettingHelperV25;
import org.mythtv.service.myth.v26.SettingHelperV26;
import org.mythtv.service.myth.v27.SettingHelperV27;
import org.mythtv.service.myth.v28.SettingHelperV28;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GetSettingTask extends AsyncTask<String, Void, String> {

	private static final String TAG = GetSettingTask.class.getSimpleName();
	
	private final Context mContext;
	private final LocationProfile mLocationProfile;
	private final TaskFinishedListener listener;
	
	public interface TaskFinishedListener {

		void onGetSettingTaskStarted();
		 
	    void onGetSettingTaskFinished( String result );
	    
	}

	public GetSettingTask( Context context, LocationProfile locationProfile, TaskFinishedListener listener ) {
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
		
        listener.onGetSettingTaskStarted();

        Log.d( TAG, "onPreExecute : exit" );
    }

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground( String... params ) {
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

		if( null == params || params.length != 2 ) {
			throw new IllegalArgumentException( "Param is required" );
		}

		String setting = null;
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( mContext, mLocationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + mLocationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}

		String settingName = params[ 0 ];
		String settingDefault = params[ 1 ];
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v025 :
				
				setting = SettingHelperV25.getInstance().process( mContext, mLocationProfile, settingName, settingDefault );
				
				break;
			case v026 :
				
				setting = SettingHelperV26.getInstance().process( mContext, mLocationProfile, settingName, settingDefault );
				
				break;
			case v027 :

				setting = SettingHelperV27.getInstance().process( mContext, mLocationProfile, settingName, settingDefault );
				
				break;
			case v028 :

				setting = SettingHelperV28.getInstance().process( mContext, mLocationProfile, settingName, settingDefault );
				
				break;
				
			default :
				
				setting = SettingHelperV27.getInstance().process( mContext, mLocationProfile, settingName, settingDefault );

				break;
		}

		Log.d( TAG, "doInBackground : exit" );
		return setting;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute( String result ) {
		Log.d( TAG, "onPostExecute : enter" );
		super.onPostExecute( result );

		listener.onGetSettingTaskFinished( result );
		
		Log.d( TAG, "onPostExecute : exit" );
	}

}
