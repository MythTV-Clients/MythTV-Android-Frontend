/**
 * 
 */
package org.mythtv.service.myth.v26;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.myth.model.StorageGroupDirectory;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.MythServicesTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class StorageGroupHelperV26 extends AbstractBaseHelper {

	private static final String TAG = StorageGroupHelperV26.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v026;
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static StorageGroupHelperV26 singleton;
	
	/**
	 * Returns the one and only StorageGroupHelperV26. init() must be called before 
	 * any 
	 * @return
	 */
	public static StorageGroupHelperV26 getInstance() {
		if( null == singleton ) {
			
			synchronized( StorageGroupHelperV26.class ) {

				if( null == singleton ) {
					singleton = new StorageGroupHelperV26();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private StorageGroupHelperV26() { }

	public List<StorageGroupDirectory> process( final Context context, final LocationProfile locationProfile, String storageGroupName ) {
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
		
		List<StorageGroupDirectory> storageGroupDirectories = null;

		try {

			storageGroupDirectories = downloadStorageGroups( locationProfile, storageGroupName );
			
		} catch( Exception e ) {
			Log.e( TAG, "process : error", e );
		
			storageGroupDirectories = null;
		}

		Log.v( TAG, "process : exit" );
		return storageGroupDirectories;
	}

	// internal helpers
	
	private List<StorageGroupDirectory> downloadStorageGroups( final LocationProfile locationProfile, final String storageGroupName ) {
		Log.v( TAG, "downloadStorageGroups : enter" );
	
		List<StorageGroupDirectory> storageGroupDirectories = null;

		try {
			ResponseEntity<org.mythtv.services.api.v026.beans.StorageGroupDirectoryList> responseEntity = mMythServicesTemplate.mythOperations().getStorageGroupDirectories( storageGroupName, locationProfile.getHostname(), ETagInfo.createEmptyETag() );

			if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {

				org.mythtv.services.api.v026.beans.StorageGroupDirectoryList storageGroupDirectoryList = responseEntity.getBody();

				if( null != storageGroupDirectoryList.getStorageGroupDirectories() ) {

					if( null != storageGroupDirectoryList.getStorageGroupDirectories().getStorageGroupDirectories() && !storageGroupDirectoryList.getStorageGroupDirectories().getStorageGroupDirectories().isEmpty() ) {
						storageGroupDirectories = load( storageGroupDirectoryList.getStorageGroupDirectories().getStorageGroupDirectories() );	
					}

				}

			}
		} catch( Exception e ) {
			Log.w( TAG, "downloadStorageGroups : error", e );
		}
		
		Log.v( TAG, "downloadStorageGroups : exit" );
		return storageGroupDirectories;
	}
	
	private List<StorageGroupDirectory> load( List<org.mythtv.services.api.v026.beans.StorageGroupDirectory> versionStorageGroupDirectories ) {
		Log.v( TAG, "load : enter" );
		
		List<StorageGroupDirectory> storageGroupDirectories = new ArrayList<StorageGroupDirectory>();
		
		if( null != versionStorageGroupDirectories && !versionStorageGroupDirectories.isEmpty() ) {
			
			for( org.mythtv.services.api.v026.beans.StorageGroupDirectory versionStorageGroupDirectory : versionStorageGroupDirectories ) {
				
				StorageGroupDirectory storageGroupDirectory = new StorageGroupDirectory();
				storageGroupDirectory.setId( versionStorageGroupDirectory.getId() );
				storageGroupDirectory.setGroupName( versionStorageGroupDirectory.getGroupName() );
				storageGroupDirectory.setDirectoryName( versionStorageGroupDirectory.getDirectoryName() );
				storageGroupDirectory.setHostname( versionStorageGroupDirectory.getHostname() );

				storageGroupDirectories.add( storageGroupDirectory );
			}
			
		}
		
		Log.v( TAG, "load : exit" );
		return storageGroupDirectories;
	}

}
