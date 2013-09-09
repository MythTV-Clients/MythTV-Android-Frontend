/**
 * 
 */
package org.mythtv.service.frontends;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.service.frontends.v26.StatusHelperV26;
import org.mythtv.service.frontends.v27.StatusHelperV27;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GetStatusTask extends AsyncTask<String, Void, org.mythtv.db.frontends.model.Status> {

	private static final String TAG = GetStatusTask.class.getSimpleName();
	
	private final LocationProfile mLocationProfile;
	private final TaskFinishedListener listener;
	
	public interface TaskFinishedListener {

		void onGetStatusTaskStarted();
		 
	    void onGetStatusTaskFinished( org.mythtv.db.frontends.model.Status result );
	    
	}

	public GetStatusTask( LocationProfile locationProfile, TaskFinishedListener listener ) {
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

		if( null == mLocationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}

		if( null == listener ) {
			throw new IllegalArgumentException( "TaskFinishedListener is required" );
		}

		if( null == params || params.length != 1 ) {
			throw new IllegalArgumentException( "Params is required" );
		}

		if( !MythAccessFactory.isServerReachable( mLocationProfile.getUrl() ) ) {
			Log.w( TAG, "process : Master Backend '" + mLocationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}

		String url = params[ 0 ];
		
		org.mythtv.db.frontends.model.Status status = null;
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				
				status = StatusHelperV26.getInstance().process( mLocationProfile, url );
				
				break;
			case v027 :

				status = StatusHelperV27.getInstance().process( mLocationProfile, url );
				
				break;
				
			default :
				
				status = StatusHelperV26.getInstance().process( mLocationProfile, url );

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
