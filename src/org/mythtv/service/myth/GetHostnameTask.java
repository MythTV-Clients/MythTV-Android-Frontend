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
public class GetHostnameTask extends AsyncTask<Void, Void, String> {

	private static final String TAG = GetHostnameTask.class.getSimpleName();

	private final Context mContext;
	private final LocationProfile mLocationProfile;
	private final TaskFinishedListener listener;
	
	public interface TaskFinishedListener {

		void onGetHostnameTaskStarted();
		 
	    void onGetHostnameTaskFinished( String result );
	    
	}

	public GetHostnameTask( Context context, LocationProfile locationProfile, TaskFinishedListener listener ) {
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
		
        listener.onGetHostnameTaskStarted();

        Log.d( TAG, "onPreExecute : exit" );
    }

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground( Void... params ) {
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

		if( !NetworkHelper.getInstance().isMasterBackendConnected( mContext, mLocationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + mLocationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}

		String hostname = null;
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v025 :

				org.mythtv.services.api.v025.MythServicesTemplate mythServicesTemplateV25 = (org.mythtv.services.api.v025.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				if( null != mythServicesTemplateV25 ) {
					ResponseEntity<String> hostnameV25 = mythServicesTemplateV25.mythOperations().getHostName( ETagInfo.createEmptyETag() );
					if( hostnameV25.getStatusCode().equals( HttpStatus.OK ) ) {
						if( null != hostnameV25.getBody() && !"".equals( hostnameV25.getBody() ) ) {
							hostname = hostnameV25.getBody().split( ":" )[ 1 ];
							hostname = hostname.replaceAll( "\"", "" );
							hostname = hostname.substring( 0, hostname.length() -1 );
							hostname = hostname.trim();
						}
					}
				}
				
				break;
				
			case v026 :
				
				org.mythtv.services.api.v026.MythServicesTemplate mythServicesTemplateV26 = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				if( null != mythServicesTemplateV26 ) {
					ResponseEntity<org.mythtv.services.api.v026.StringWrapper> hostnameV26 = mythServicesTemplateV26.mythOperations().getHostName();
					if( hostnameV26.getStatusCode().equals( HttpStatus.OK ) ) {
						if( null != hostnameV26.getBody().getString() && !"".equals( hostnameV26.getBody().getString() ) ) {
							hostname = hostnameV26.getBody().getString();
						}
					}
				}
				
				break;
			case v027 :

				org.mythtv.services.api.v027.MythServicesTemplate mythServicesTemplateV27 = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				if( null != mythServicesTemplateV27 ) {
					ResponseEntity<String> hostnameV27 = mythServicesTemplateV27.mythOperations().getHostName( ETagInfo.createEmptyETag() );
					if( hostnameV27.getStatusCode().equals( HttpStatus.OK ) ) {
						if( null != hostnameV27.getBody() && !"".equals( hostnameV27.getBody() ) ) {
							hostname = hostnameV27.getBody().split( ":" )[ 1 ];
							hostname = hostname.replaceAll( "\"", "" );
							hostname = hostname.substring( 0, hostname.length() -1 );
							hostname = hostname.trim();
						}
					}
				}
				
				break;
			case v028 :

				org.mythtv.services.api.v028.MythServicesTemplate mythServicesTemplateV28 = (org.mythtv.services.api.v028.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				if( null != mythServicesTemplateV28 ) {
					ResponseEntity<String> hostnameV28 = mythServicesTemplateV28.mythOperations().getHostName( ETagInfo.createEmptyETag() );
					if( hostnameV28.getStatusCode().equals( HttpStatus.OK ) ) {
						if( null != hostnameV28.getBody() && !"".equals( hostnameV28.getBody() ) ) {
							hostname = hostnameV28.getBody().split( ":" )[ 1 ];
							hostname = hostname.replaceAll( "\"", "" );
							hostname = hostname.substring( 0, hostname.length() -1 );
							hostname = hostname.trim();
						}
					}
				}
				
				break;
				
			default :
				
				org.mythtv.services.api.v027.MythServicesTemplate mythServicesTemplateV27Default = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				if( null != mythServicesTemplateV27Default ) {
					ResponseEntity<String> hostnameV27 = mythServicesTemplateV27Default.mythOperations().getHostName( ETagInfo.createEmptyETag() );
					if( hostnameV27.getStatusCode().equals( HttpStatus.OK ) ) {
						if( null != hostnameV27.getBody() && !"".equals( hostnameV27.getBody() ) ) {
							hostname = hostnameV27.getBody().split( ":" )[ 1 ];
							hostname = hostname.replaceAll( "\"", "" );
							hostname = hostname.substring( 0, hostname.length() -1 );
							hostname = hostname.trim();
						}
					}
				}
				
				break;
		}

		Log.d( TAG, "doInBackground : exit" );
		return hostname;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute( String result ) {
		Log.d( TAG, "onPostExecute : enter" );
		super.onPostExecute( result );

		listener.onGetHostnameTaskFinished( result );
		
		Log.d( TAG, "onPostExecute : exit" );
	}

}
