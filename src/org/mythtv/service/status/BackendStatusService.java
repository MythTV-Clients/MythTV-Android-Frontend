/**
 * 
 */
package org.mythtv.service.status;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.status.model.BackendStatus;
import org.mythtv.service.MythtvService;
import org.mythtv.service.status.v26.BackendStatusHelperV26;
import org.mythtv.service.status.v27.BackendStatusHelperV27;
import org.mythtv.services.api.ApiVersion;

import android.content.Intent;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class BackendStatusService extends MythtvService {

	private static final String TAG = BackendStatusService.class.getSimpleName();

    public static final String ACTION_DOWNLOAD = "org.mythtv.background.status.ACTION_DOWNLOAD";
    public static final String ACTION_COMPLETE = "org.mythtv.background.status.ACTION_COMPLETE";

    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_DATA = "COMPLETE_DATA";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";

	private BackendStatus mBackendStatus = null;
	private LocationProfile mLocationProfile;
	
	public BackendStatusService() {
		super( "BackendStatusService" );
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.service.MythtvService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );
	
		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( this );
		
		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );
    		
 			ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
			switch( apiVersion ) {
				case v026 :
				
					mBackendStatus = BackendStatusHelperV26.getInstance().process( this, mLocationProfile );
				
					break;
				case v027 :

					mBackendStatus = BackendStatusHelperV27.getInstance().process( this, mLocationProfile );

					break;
				
				default :
				
					mBackendStatus = BackendStatusHelperV26.getInstance().process( this, mLocationProfile );

					break;
			}
		
			if( null == mBackendStatus ) {
				sendCompleteNotConnected();
			} else {
	        	sendComplete();
			}
			
		}
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void sendComplete() { 
		Log.v( TAG, "sendComplete : enter" );
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "Backend Status Download Service Finished" );
		completeIntent.putExtra( EXTRA_COMPLETE_DATA, mBackendStatus );
		completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.FALSE );
		
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "sendComplete : exit" );
	}
	
	private void sendCompleteNotConnected() {
		Log.v( TAG, "sendCompleteNotConnected : enter" );
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "Backend Status Download Service Finished" );
		completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.TRUE );
		
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "sendCompleteNotConnected : exit" );
	}
	
}
