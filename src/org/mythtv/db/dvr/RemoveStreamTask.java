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
package org.mythtv.db.dvr;

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
public class RemoveStreamTask extends AsyncTask<Integer, Void, Boolean> {

	private static final String TAG = RemoveStreamTask.class.getSimpleName();
	
	private Context mContext;
	private LocationProfile mLocationProfile;
	
	public RemoveStreamTask( Context context, LocationProfile locationProfile ) {
		mContext = context;
		mLocationProfile = locationProfile;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground( Integer... params ) {
		Log.v( TAG, "doInBackground : enter" );
		
		if( null == mContext ) {
			throw new IllegalArgumentException( "mContext is required" );
		}
		
		if( null == mLocationProfile ) {
			throw new IllegalArgumentException( "mLocationProfile is required" );
		}
		
		Integer id = params[ 0 ];
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v025 :
				removeLiveStreamV25( id );

				break;
				
			case v026 :
				removeLiveStreamV26( id );

				break;
				
			case v027 :
				removeLiveStreamV27( id );

				break;

			case v028 :
				removeLiveStreamV28( id );

				break;
	
			default :
				removeLiveStreamV27( id );
			
				break;

		}
		
		Log.v( TAG, "doInBackground : exit" );
		return null;
	}

	// internal helpers
	
	private void removeLiveStreamV25( Integer id ) {
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( mContext, mLocationProfile ) ) {
			return;
		}
		
		org.mythtv.services.api.v026.MythServicesTemplate template = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( ApiVersion.v025, mLocationProfile.getUrl() );
		template.contentOperations().removeLiveStream( id );
	}

	private void removeLiveStreamV26( Integer id ) {
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( mContext, mLocationProfile ) ) {
			return;
		}
		
		org.mythtv.services.api.v026.MythServicesTemplate template = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( ApiVersion.v026, mLocationProfile.getUrl() );
		template.contentOperations().removeLiveStream( id );
	}

	private void removeLiveStreamV27( Integer id ) {
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( mContext, mLocationProfile ) ) {
			return;
		}
		
		org.mythtv.services.api.v027.MythServicesTemplate template = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( ApiVersion.v027, mLocationProfile.getUrl() );
		template.contentOperations().removeLiveStream( id, ETagInfo.createEmptyETag() );
	}

	private void removeLiveStreamV28( Integer id ) {
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( mContext, mLocationProfile ) ) {
			return;
		}
		
		org.mythtv.services.api.v028.MythServicesTemplate template = (org.mythtv.services.api.v028.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( ApiVersion.v028, mLocationProfile.getUrl() );
		template.contentOperations().removeLiveStream( id, ETagInfo.createEmptyETag() );
	}
}
