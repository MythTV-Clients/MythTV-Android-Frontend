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
			case v025 :

				org.mythtv.services.api.v025.MythServicesTemplate mythServicesTemplateV25 = (org.mythtv.services.api.v025.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, url );

				if( null != mythServicesTemplateV25 ) {
					mythServicesTemplateV25.frontendOperations().sendMessage( message, 1000, ETagInfo.createEmptyETag() );
				}
				
				break;
				
			case v026 :
				
				org.mythtv.services.api.v026.MythServicesTemplate mythServicesTemplateV26 = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, url );

				if( null != mythServicesTemplateV26 ) {
					mythServicesTemplateV26.frontendOperations().sendMessage( url, message );
				}
				
				break;
			case v027 :

				org.mythtv.services.api.v027.MythServicesTemplate mythServicesTemplateV27 = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, url );

				if( null != mythServicesTemplateV27 ) {
					mythServicesTemplateV27.frontendOperations().sendMessage( message, 1000, ETagInfo.createEmptyETag() );
				}
				
				break;
			case v028 :

				org.mythtv.services.api.v028.MythServicesTemplate mythServicesTemplateV28 = (org.mythtv.services.api.v028.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, url );

				if( null != mythServicesTemplateV28 ) {
					mythServicesTemplateV28.frontendOperations().sendMessage( message, 1000, ETagInfo.createEmptyETag() );
				}
				
				break;
				
			default :
				
				org.mythtv.services.api.v027.MythServicesTemplate mythServicesTemplateDefault = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, url );

				if( null != mythServicesTemplateDefault ) {
					mythServicesTemplateDefault.frontendOperations().sendMessage( message, 1000, ETagInfo.createEmptyETag() );
				}
				
				break;
		}

		Log.d( TAG, "doInBackground : exit" );
		return null;
	}

}
