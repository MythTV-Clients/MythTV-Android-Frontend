/**
 * 
 */
package org.mythtv.service.myth;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.service.myth.v26.SettingHelperV26;
import org.mythtv.service.myth.v27.SettingHelperV27;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GetSettingTask extends AsyncTask<String, Void, String> {

	private static final String TAG = GetSettingTask.class.getSimpleName();
	
	private final LocationProfile mLocationProfile;
	private final TaskFinishedListener listener;
	
	public interface TaskFinishedListener {

		void onGetSettingTaskStarted();
		 
	    void onGetSettingTaskFinished( String result );
	    
	}

	public GetSettingTask( LocationProfile locationProfile, TaskFinishedListener listener ) {
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

		if( null == mLocationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}

		if( null == listener ) {
			throw new IllegalArgumentException( "TaskFinishedListener is required" );
		}

		if( null == params || params.length != 1 ) {
			throw new IllegalArgumentException( "Param is required" );
		}

		String setting = null;
		
		if( !MythAccessFactory.isServerReachable( mLocationProfile.getUrl() ) ) {
			Log.w( TAG, "process : Master Backend '" + mLocationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}

		String settingName = params[ 0 ];
		String settingDefault = params[ 1 ];
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				
				setting = SettingHelperV26.getInstance().process( mLocationProfile, settingName, settingDefault );
				
				break;
			case v027 :

				setting = SettingHelperV27.getInstance().process( mLocationProfile, settingName, settingDefault );
				
				break;
				
			default :
				
				setting = SettingHelperV26.getInstance().process( mLocationProfile, settingName, settingDefault );

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
