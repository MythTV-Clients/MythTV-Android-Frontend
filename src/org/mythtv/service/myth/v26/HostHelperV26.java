/**
 * 
 */
package org.mythtv.service.myth.v26;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.MythServicesTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class HostHelperV26 extends AbstractBaseHelper {

	private static final String TAG = HostHelperV26.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v026;
	
	private static MythServicesTemplate mMythServicesTemplate;

	public static List<String> process( final LocationProfile locationProfile ) {
		Log.v( TAG, "process : enter" );
		
		if( !MythAccessFactory.isServerReachable( locationProfile.getUrl() ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
		List<String> hosts = null;

		try {

			hosts = downloadHosts( locationProfile );
			
		} catch( Exception e ) {
			Log.e( TAG, "process : error", e );
		
			hosts = null;
		}

		Log.v( TAG, "process : exit" );
		return hosts;
	}

	// internal helpers
	
	private static List<String> downloadHosts( final LocationProfile locationProfile ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "downloadHosts : enter" );
	
		List<String> hosts = null;

		ResponseEntity<org.mythtv.services.api.v026.StringList> responseEntity = mMythServicesTemplate.mythOperations().getHosts( ETagInfo.createEmptyETag() );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {

			org.mythtv.services.api.v026.StringList hostList = responseEntity.getBody();

			if( null != hostList.getStringList() && hostList.getStringList().length > 0 ) {
				hosts = load( hostList );	
			}

		}

		Log.v( TAG, "downloadHosts : exit" );
		return hosts;
	}
	
	private static List<String> load( org.mythtv.services.api.v026.StringList versionHosts ) {
		Log.v( TAG, "load : enter" );
		
		List<String> hosts = new ArrayList<String>();
		
		if( null != versionHosts ) {
			
			for( String versionHost : versionHosts.getStringList() ) {
				
				hosts.add( versionHost );

			}
			
		}
		
		Log.v( TAG, "load : exit" );
		return hosts;
	}

}
