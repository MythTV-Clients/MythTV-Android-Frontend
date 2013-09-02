/**
 * 
 */
package org.mythtv.db.dvr;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.MythServicesTemplate;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class RemoveStreamTask extends AsyncTask<Integer, Void, Boolean> {

	private static final String TAG = RemoveStreamTask.class.getSimpleName();
	
	private LocationProfile mLocationProfile;
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground( Integer... params ) {
		Log.v( TAG, "doInBackground : enter" );
		
		if( null == mLocationProfile ) {
			throw new IllegalArgumentException( "locationProfile is required" );
		}
		
		Integer id = params[ 0 ];
		
		ApiVersion apiVersion = ApiVersion.valueOf( mLocationProfile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				removeLiveStreamV26( apiVersion, id );

				break;
				
			case v027 :
				removeLiveStreamV27( apiVersion, id );

				break;
				
			default :
				removeLiveStreamV26( apiVersion, id );
			
				break;
		}
		
		Log.v( TAG, "doInBackground : exit" );
		return null;
	}

	public void setLocationProfile( LocationProfile locationProfile ) {
		this.mLocationProfile = locationProfile;
	}
	
	// internal helpers
	
	private void removeLiveStreamV26( ApiVersion apiVersion, Integer id ) {
		
		if( !MythAccessFactory.isServerReachable( mLocationProfile.getUrl() ) ) {
			return;
		}
		
		org.mythtv.services.api.v026.MythServicesTemplate template = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );
		template.contentOperations().removeLiveStream( id );
	}

	private void removeLiveStreamV27( ApiVersion apiVersion, Integer id ) {
		
		if( !MythAccessFactory.isServerReachable( mLocationProfile.getUrl() ) ) {
			return;
		}
		
		org.mythtv.services.api.v027.MythServicesTemplate template = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );
		template.contentOperations().removeLiveStream( id, ETagInfo.createEmptyETag() );
	}

}
