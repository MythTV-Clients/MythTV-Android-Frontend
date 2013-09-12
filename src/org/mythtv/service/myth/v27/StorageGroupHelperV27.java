/**
 * 
 */
package org.mythtv.service.myth.v27;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.myth.model.StorageGroupDirectory;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v027.MythServicesTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class StorageGroupHelperV27 extends AbstractBaseHelper {

	private static final String TAG = StorageGroupHelperV27.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v027;
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static StorageGroupHelperV27 singleton;
	
	/**
	 * Returns the one and only StorageGroupHelperV27. init() must be called before 
	 * any 
	 * @return
	 */
	public static StorageGroupHelperV27 getInstance() {
		if( null == singleton ) {
			
			synchronized( StorageGroupHelperV27.class ) {

				if( null == singleton ) {
					singleton = new StorageGroupHelperV27();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private StorageGroupHelperV27() { }

	public List<StorageGroupDirectory> process( final Context context, final LocationProfile locationProfile, String storageGroupName ) {
		Log.v( TAG, "process : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
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

		ResponseEntity<org.mythtv.services.api.v027.beans.StorageGroupDirList> responseEntity = mMythServicesTemplate.mythOperations().getStorageGroupDirs( storageGroupName, locationProfile.getHostname(), ETagInfo.createEmptyETag() );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {

			org.mythtv.services.api.v027.beans.StorageGroupDirList storageGroupDirectoryList = responseEntity.getBody();

			if( null != storageGroupDirectoryList.getStorageGroupDirs() ) {
			
				if( null != storageGroupDirectoryList.getStorageGroupDirs() && storageGroupDirectoryList.getStorageGroupDirs().length > 0 ) {
					storageGroupDirectories = load( storageGroupDirectoryList.getStorageGroupDirs() );	
				}

			}

		}

		Log.v( TAG, "downloadStorageGroups : exit" );
		return storageGroupDirectories;
	}
	
	private List<StorageGroupDirectory> load( org.mythtv.services.api.v027.beans.StorageGroupDir[] versionStorageGroupDirectories ) {
		Log.v( TAG, "load : enter" );
		
		List<StorageGroupDirectory> storageGroupDirectories = new ArrayList<StorageGroupDirectory>();
		
		if( null != versionStorageGroupDirectories && versionStorageGroupDirectories.length > 0 ) {
			
			for( org.mythtv.services.api.v027.beans.StorageGroupDir versionStorageGroupDirectory : versionStorageGroupDirectories ) {
				
				StorageGroupDirectory storageGroupDirectory = new StorageGroupDirectory();
				storageGroupDirectory.setId( versionStorageGroupDirectory.getId() );
				storageGroupDirectory.setGroupName( versionStorageGroupDirectory.getGroupName() );
				storageGroupDirectory.setDirectoryName( versionStorageGroupDirectory.getDirName() );
				storageGroupDirectory.setHostname( versionStorageGroupDirectory.getHostName() );

				storageGroupDirectories.add( storageGroupDirectory );
			}
			
		}
		
		Log.v( TAG, "load : exit" );
		return storageGroupDirectories;
	}

}
