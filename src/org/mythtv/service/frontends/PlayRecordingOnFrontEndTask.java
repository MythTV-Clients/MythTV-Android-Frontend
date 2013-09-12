/**
 * 
 */
package org.mythtv.service.frontends;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.dvr.model.Program;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class PlayRecordingOnFrontEndTask extends AsyncTask<String, Void, Boolean> {

	private static final String TAG = PlayRecordingOnFrontEndTask.class.getSimpleName();

	private final Context mContext;
	private final LocationProfile mLocationProfile;
	private final Program mProgram;
	
	public PlayRecordingOnFrontEndTask( Context context, LocationProfile locationProfile, Program program ) {
		mContext = context;
		mLocationProfile = locationProfile;
		mProgram = program;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground( String... params ) {
		Log.d( TAG, "doInBackground : enter" );
		
		if( null == mContext ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == mLocationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}

		if( null == mProgram ) {
			throw new IllegalArgumentException( "Program is required" );
		}
		
		boolean started = false;
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( mContext, mLocationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + mLocationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}

		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				
				started = false;
				
				break;
			case v027 :

				org.mythtv.services.api.v027.MythServicesTemplate mythServicesTemplateV27 = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				ResponseEntity<org.mythtv.services.api.Bool> responseV27 = mythServicesTemplateV27.frontendOperations().playRecording( mProgram.getChannelInfo().getChannelId(), mProgram.getRecording().getStartTimestamp(), ETagInfo.createEmptyETag() );
				if( responseV27.getStatusCode().equals( HttpStatus.OK ) ) {
					
					if( null != responseV27.getBody() ) {
					
						started = responseV27.getBody().getValue();
					
					}
					
				}
				
				break;
				
			default :
				
				started = false;

				break;
		}

		Log.d( TAG, "doInBackground : exit" );
		return started;
	}

}
