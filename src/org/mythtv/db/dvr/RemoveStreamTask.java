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

	// internal helpers
	
	private void removeLiveStreamV26( ApiVersion apiVersion, Integer id ) {
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( mContext, mLocationProfile ) ) {
			return;
		}
		
		org.mythtv.services.api.v026.MythServicesTemplate template = (org.mythtv.services.api.v026.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );
		template.contentOperations().removeLiveStream( id );
	}

	private void removeLiveStreamV27( ApiVersion apiVersion, Integer id ) {
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( mContext, mLocationProfile ) ) {
			return;
		}
		
		org.mythtv.services.api.v027.MythServicesTemplate template = (org.mythtv.services.api.v027.MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( apiVersion, mLocationProfile.getUrl() );
		template.contentOperations().removeLiveStream( id, ETagInfo.createEmptyETag() );
	}

}
