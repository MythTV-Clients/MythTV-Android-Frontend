/**
 * 
 */
package org.mythtv.service.frontends.v26;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.frontends.model.State;
import org.mythtv.db.frontends.model.StateStringItem;
import org.mythtv.db.frontends.model.Status;
import org.mythtv.service.myth.v26.HostHelperV26;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.MythServicesTemplate;
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
public class StatusHelperV26 extends AbstractBaseHelper {

	private static final String TAG = HostHelperV26.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v026;
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static StatusHelperV26 singleton;
	
	/**
	 * Returns the one and only StatusHelperV26. init() must be called before 
	 * any 
	 * @return
	 */
	public static StatusHelperV26 getInstance() {
		if( null == singleton ) {
			
			synchronized( StatusHelperV26.class ) {

				if( null == singleton ) {
					singleton = new StatusHelperV26();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private StatusHelperV26() { }

	public Status process( final Context context, final LocationProfile locationProfile, final String url ) {
		Log.v( TAG, "process : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
		Status status = null;

		try {

			status = downloadStatus( locationProfile, url );
			
		} catch( Exception e ) {
			Log.e( TAG, "process : error", e );
		
			status = null;
		}

		Log.v( TAG, "process : exit" );
		return status;
	}

	// internal helpers
	
	private Status downloadStatus( final LocationProfile locationProfile, final String url ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "downloadHosts : enter" );
	
		Status status = null;

		ResponseEntity<org.mythtv.services.api.v026.beans.FrontendStatus> responseEntity = mMythServicesTemplate.frontendOperations().getStatus( url, ETagInfo.createEmptyETag() );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {

			org.mythtv.services.api.v026.beans.FrontendStatus versionStatus = responseEntity.getBody();

			if( null != versionStatus.getStatus() ) {
				status = load( versionStatus.getStatus() );	
			}

		}

		Log.v( TAG, "downloadHosts : exit" );
		return status;
	}
	
	private Status load( org.mythtv.services.api.v026.beans.Status versionStatus ) {
		Log.v( TAG, "load : enter" );
		
		Status status = new Status();
		
		if( null != versionStatus.getState() ) {
			
			State state = new State();
			state.setCurrentLocation( versionStatus.getState().getCurrentLocation() );
			
			if( null != versionStatus.getState().getStates() && !versionStatus.getState().getStates().isEmpty() ) {
				
				List<StateStringItem> items = new ArrayList<StateStringItem>();
				for( org.mythtv.services.api.v026.beans.StateStringItem versionItem : versionStatus.getState().getStates() ) {
					StateStringItem item = new StateStringItem();
					item.setKey( versionItem.getKey() );
					item.setValue( versionItem.getValue() );
					
					items.add( item );
				}
				
				state.setStates( items );
			}
			
			status.setState( state );
		}
		
		Log.v( TAG, "load : exit" );
		return status;
	}

}
