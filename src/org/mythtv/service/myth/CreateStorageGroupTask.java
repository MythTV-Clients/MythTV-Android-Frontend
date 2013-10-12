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
public class CreateStorageGroupTask extends AsyncTask<String, Void, Boolean> {

	private static final String TAG = CreateStorageGroupTask.class.getSimpleName();
	
	private final Context mContext;
	private final LocationProfile mLocationProfile;
	private final TaskFinishedListener listener;
	
	public interface TaskFinishedListener {

		void onCreateStorageGroupTaskStarted();
		 
	    void onCreateStorageGroupTaskFinished( boolean result );
	    
	}

	public CreateStorageGroupTask( Context context, LocationProfile locationProfile, TaskFinishedListener listener ) {
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
		
        listener.onCreateStorageGroupTaskStarted();

        Log.d( TAG, "onPreExecute : exit" );
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

		if( null == listener ) {
			throw new IllegalArgumentException( "TaskFinishedListener is required" );
		}

		if( null == params || params.length != 2 ) {
			throw new IllegalArgumentException( "String params are required" );
		}

		boolean created = false;
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( mContext, mLocationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + mLocationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}

		String groupName = params[ 0 ];
		String directory = params[ 1 ];
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v025 :

				org.mythtv.services.api.v025.MythServicesTemplate mythServicesTemplateV25 = (org.mythtv.services.api.v025.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				if( null != mythServicesTemplateV25 ) {
					ResponseEntity<org.mythtv.services.api.Bool> responseV25 = mythServicesTemplateV25.mythOperations().addStorageGroupDir( groupName, directory, mLocationProfile.getHostname() );
					if( responseV25.getStatusCode().equals( HttpStatus.OK ) ) {

						if( null != responseV25.getBody() ) {

							created = responseV25.getBody().getValue();

						}

					}
				}
				
				break;
				
			case v026 :
				
				org.mythtv.services.api.v026.MythServicesTemplate mythServicesTemplateV26 = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				if( null != mythServicesTemplateV26 ) {
					ResponseEntity<org.mythtv.services.api.v026.Bool> responseV26 = mythServicesTemplateV26.mythOperations().addStorageGroupDir( groupName, directory, mLocationProfile.getHostname() );
					if( responseV26.getStatusCode().equals( HttpStatus.OK ) ) {

						if( null != responseV26.getBody() ) {

							created = responseV26.getBody().getBool().booleanValue();

						}

					}
				}
				
				break;
			case v027 :

				org.mythtv.services.api.v027.MythServicesTemplate mythServicesTemplateV27 = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				if( null != mythServicesTemplateV27 ) {
					ResponseEntity<org.mythtv.services.api.Bool> responseV27 = mythServicesTemplateV27.mythOperations().addStorageGroupDir( groupName, directory, mLocationProfile.getHostname() );
					if( responseV27.getStatusCode().equals( HttpStatus.OK ) ) {

						if( null != responseV27.getBody() ) {

							created = responseV27.getBody().getValue();

						}

					}
				}
				
				break;
				
			default :
				
				org.mythtv.services.api.v027.MythServicesTemplate mythServicesTemplateV27Default = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );

				if( null != mythServicesTemplateV27Default ) {
					ResponseEntity<org.mythtv.services.api.Bool> responseV27 = mythServicesTemplateV27Default.mythOperations().addStorageGroupDir( groupName, directory, mLocationProfile.getHostname() );
					if( responseV27.getStatusCode().equals( HttpStatus.OK ) ) {

						if( null != responseV27.getBody() ) {

							created = responseV27.getBody().getValue();

						}

					}
				}

				break;
		}

		Log.d( TAG, "doInBackground : exit" );
		return created;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute( Boolean result ) {
		Log.d( TAG, "onPostExecute : enter" );
		super.onPostExecute( result );

		Log.d( TAG, "onPostExecute : result=" + result );
		listener.onCreateStorageGroupTaskFinished( result );
		
		Log.d( TAG, "onPostExecute : exit" );
	}

}
