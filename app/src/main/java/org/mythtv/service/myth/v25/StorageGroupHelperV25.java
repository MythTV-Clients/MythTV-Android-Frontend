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
package org.mythtv.service.myth.v25;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.myth.model.StorageGroupDirectory;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v025.MythServicesTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class StorageGroupHelperV25 extends AbstractBaseHelper {

	private static final String TAG = StorageGroupHelperV25.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v025;
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static StorageGroupHelperV25 singleton;
	
	/**
	 * Returns the one and only StorageGroupHelperV25. init() must be called before 
	 * any 
	 * @return
	 */
	public static StorageGroupHelperV25 getInstance() {
		if( null == singleton ) {
			
			synchronized( StorageGroupHelperV25.class ) {

				if( null == singleton ) {
					singleton = new StorageGroupHelperV25();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private StorageGroupHelperV25() { }

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

		try {
			ResponseEntity<org.mythtv.services.api.v025.beans.StorageGroupDirList> responseEntity = mMythServicesTemplate.mythOperations().getStorageGroupDirs( storageGroupName, locationProfile.getHostname(), ETagInfo.createEmptyETag() );

			if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {

				org.mythtv.services.api.v025.beans.StorageGroupDirList storageGroupDirectoryList = responseEntity.getBody();

				if( null != storageGroupDirectoryList.getStorageGroupDirs() ) {

					if( null != storageGroupDirectoryList.getStorageGroupDirs() && storageGroupDirectoryList.getStorageGroupDirs().length > 0 ) {
						storageGroupDirectories = load( storageGroupDirectoryList.getStorageGroupDirs() );	
					}

				}

			}
		} catch( Exception e ) {
			Log.w( TAG, "downloadStorageGroups : error", e );
		}
		
		Log.v( TAG, "downloadStorageGroups : exit" );
		return storageGroupDirectories;
	}
	
	private List<StorageGroupDirectory> load( org.mythtv.services.api.v025.beans.StorageGroupDir[] versionStorageGroupDirectories ) {
		Log.v( TAG, "load : enter" );
		
		List<StorageGroupDirectory> storageGroupDirectories = new ArrayList<StorageGroupDirectory>();
		
		if( null != versionStorageGroupDirectories && versionStorageGroupDirectories.length > 0 ) {
			
			for( org.mythtv.services.api.v025.beans.StorageGroupDir versionStorageGroupDirectory : versionStorageGroupDirectories ) {
				
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
