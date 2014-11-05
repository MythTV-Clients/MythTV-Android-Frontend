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
package org.mythtv.service.myth.v28;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.MythServiceApiRuntimeException;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v027.MythServicesTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class HostHelperV28 extends AbstractBaseHelper {

	private static final String TAG = HostHelperV28.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v027;
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static HostHelperV28 singleton;
	
	/**
	 * Returns the one and only HostHelperV28. init() must be called before 
	 * any 
	 * @return
	 */
	public static HostHelperV28 getInstance() {
		if( null == singleton ) {
			
			synchronized( HostHelperV28.class ) {

				if( null == singleton ) {
					singleton = new HostHelperV28();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private HostHelperV28() { }

	public List<String> process( final Context context, final LocationProfile locationProfile ) {
		Log.v( TAG, "process : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		if( null == mMythServicesTemplate ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}
		
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
	
	private List<String> downloadHosts( final LocationProfile locationProfile ) throws MythServiceApiRuntimeException, RemoteException, OperationApplicationException {
		Log.v( TAG, "downloadHosts : enter" );
	
		List<String> hosts = null;

		ResponseEntity<org.mythtv.services.api.ArrayOfString> responseEntity = mMythServicesTemplate.mythOperations().getHosts( ETagInfo.createEmptyETag() );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {

			org.mythtv.services.api.ArrayOfString hostList = responseEntity.getBody();

			if( null != hostList.getValue() && hostList.getValue().length > 0 ) {
				hosts = load( hostList );	
			}

		}

		Log.v( TAG, "downloadHosts : exit" );
		return hosts;
	}
	
	private List<String> load( org.mythtv.services.api.ArrayOfString versionHosts ) {
		Log.v( TAG, "load : enter" );
		
		List<String> hosts = new ArrayList<String>();
		
		if( null != versionHosts ) {
			
			for( String versionHost : versionHosts.getValue() ) {
				
				hosts.add( versionHost );

			}
			
		}
		
		Log.v( TAG, "load : exit" );
		return hosts;
	}

}
