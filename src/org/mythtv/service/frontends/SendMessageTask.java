/**
 * 
 */
package org.mythtv.service.frontends;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class SendMessageTask extends AsyncTask<String, Void, Void> {

	private static final String TAG = SendMessageTask.class.getSimpleName();

	private final Context mContext;
	private final LocationProfile mLocationProfile;
	
	public SendMessageTask( Context context, LocationProfile locationProfile ) {
		mContext = context;
		mLocationProfile = locationProfile;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground( String... params ) {
		Log.d( TAG, "doInBackground : enter" );
		
		if( null == mContext ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == mLocationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}

		if( null == params || params.length != 2 ) {
			throw new IllegalArgumentException( "Params is required" );
		}
		
		String url = params[ 0 ];
		String message = params[ 1 ];
		
		if( !NetworkHelper.getInstance().isFrontendConnected( mContext, mLocationProfile, url ) ) {
			Log.w( TAG, "process : Frontend @ '" + url + "' is unreachable" );
			
			return null;
		}

		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				
				org.mythtv.services.api.v026.MythServicesTemplate mythServicesTemplateV26 = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, url );

				mythServicesTemplateV26.frontendOperations().sendMessage( url, message );

				break;
			case v027 :

				org.mythtv.services.api.v027.MythServicesTemplate mythServicesTemplateV27 = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, url );

				mythServicesTemplateV27.frontendOperations().sendMessage( message, 1000, ETagInfo.createEmptyETag() );
				
				break;
				
			default :
				
				org.mythtv.services.api.v026.MythServicesTemplate mythServicesTemplateDefault = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, url );

				mythServicesTemplateDefault.frontendOperations().sendMessage( url, message );

				break;
		}

		Log.d( TAG, "doInBackground : exit" );
		return null;
	}

}
