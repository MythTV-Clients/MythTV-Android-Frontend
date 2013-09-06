/**
 * 
 */
package org.mythtv.service.myth;

import java.util.List;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.service.myth.v26.HostHelperV26;
import org.mythtv.service.myth.v27.HostHelperV27;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GetHostsTask extends AsyncTask<Void, Void, List<String>> {

	private static final String TAG = GetHostsTask.class.getSimpleName();
	
	private final LocationProfile mLocationProfile;
	private final TaskFinishedListener listener;
	
	public interface TaskFinishedListener {

		void onGetHostsTaskStarted();
		 
	    void onGetHostsTaskFinished( List<String> result );
	    
	}

	public GetHostsTask( LocationProfile locationProfile, TaskFinishedListener listener ) {
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

		if( null == mLocationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}

		if( null == listener ) {
			throw new IllegalArgumentException( "TaskFinishedListener is required" );
		}

		List<String> hosts = null;
		
		if( !MythAccessFactory.isServerReachable( mLocationProfile.getUrl() ) ) {
			Log.w( TAG, "process : Master Backend '" + mLocationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}

		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				
				hosts = HostHelperV26.process( mLocationProfile );
				
				break;
			case v027 :

				hosts = HostHelperV27.process( mLocationProfile );
				
				break;
				
			default :
				
				hosts = HostHelperV26.process( mLocationProfile );

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
