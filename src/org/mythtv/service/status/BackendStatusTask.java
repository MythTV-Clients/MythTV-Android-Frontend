/**
 * 
 */
package org.mythtv.service.status;

import org.joda.time.DateTime;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.db.dvr.DvrEndpoint;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.db.status.model.BackendStatus;
import org.mythtv.service.channel.ChannelDownloadService;
import org.mythtv.service.dvr.RecordedService;
import org.mythtv.service.dvr.RecordingRuleDownloadService;
import org.mythtv.service.dvr.UpcomingDownloadService;
import org.mythtv.service.frontends.FrontendsDiscoveryService;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.status.v26.BackendStatusHelperV26;
import org.mythtv.service.status.v27.BackendStatusHelperV27;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.RunningServiceHelper;
import org.mythtv.services.api.ApiVersion;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class BackendStatusTask extends AsyncTask<Void, Void, BackendStatus> {

	private static final String TAG = BackendStatusTask.class.getSimpleName();
	
	private EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	private RunningServiceHelper mRunningServiceHelper = RunningServiceHelper.getInstance();
	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
	private Context mContext;
	private LocationProfile mLocationProfile;
	
	public BackendStatusTask( Context context, LocationProfile locationProfile ) {
		this.mContext = context;
		this.mLocationProfile = locationProfile;
	}
	
	@Override
	protected BackendStatus doInBackground( Void... params ) {
		Log.v( TAG, "doInBackground : enter" );

		if( null == params || params.length == 0 ) {
			throw new IllegalArgumentException( "locationProfle required" );
		}
		
		BackendStatus backendStatus = null;
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				
				backendStatus = BackendStatusHelperV26.process( mContext, mLocationProfile );
				
				break;
			case v027 :

				backendStatus = BackendStatusHelperV27.process( mContext, mLocationProfile );

				break;
				
			default :
				
				backendStatus = BackendStatusHelperV26.process( mContext, mLocationProfile );

				break;
		}

		Log.v( TAG, "doInBackground : exit" );
		return backendStatus;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute( BackendStatus result ) {
		Log.v( TAG, "onPostExecute : enter" );
		super.onPostExecute( result );

		if( null != result ) {
		
			if( mLocationProfile.getType().equals( LocationType.HOME ) ) {
			
				if( !mRunningServiceHelper.isServiceRunning( mContext, "org.mythtv.service.frontends.FrontendsDiscoveryService" ) ) {
					mContext.startService( new Intent( FrontendsDiscoveryService.ACTION_DISCOVER ) );
				}
				
			}
		
		}
		
		Log.v( TAG, "onPostExecute : exit" );
	}

}
