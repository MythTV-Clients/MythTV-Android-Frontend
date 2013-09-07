/**
 * 
 */
package org.mythtv.service.frontends;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class SendActionTask extends AsyncTask<String, Void, Void> {

	private static final String TAG = SendActionTask.class.getSimpleName();

	private LocationProfile mLocationProfile;
	
	public SendActionTask( LocationProfile locationProfile ) {
		mLocationProfile = locationProfile;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground( String... params ) {
		Log.d( TAG, "doInBackground : enter" );
		
		if( null == mLocationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}

		if( null == params || params.length != 2 ) {
			throw new IllegalArgumentException( "Params is required" );
		}
		
		String url = params[ 0 ];
		String action = params[ 1 ];
		
		if( !MythAccessFactory.isServerReachable( mLocationProfile.getUrl() ) ) {
			Log.w( TAG, "process : Master Backend '" + mLocationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}

		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				
				org.mythtv.services.api.v026.MythServicesTemplate mythServicesTemplateV26 = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				mythServicesTemplateV26.frontendOperations().sendAction( url, action, null, 0, 0 );

				break;
			case v027 :

				org.mythtv.services.api.v027.MythServicesTemplate mythServicesTemplateV27 = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				mythServicesTemplateV27.frontendOperations().sendAction( action, null, 0, 0, ETagInfo.createEmptyETag() );
				
				break;
				
			default :
				
				org.mythtv.services.api.v026.MythServicesTemplate mythServicesTemplateV26Default = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				mythServicesTemplateV26Default.frontendOperations().sendAction( url, action, null, 0, 0 );

				break;
		}

		Log.d( TAG, "doInBackground : exit" );
		return null;
	}

}
