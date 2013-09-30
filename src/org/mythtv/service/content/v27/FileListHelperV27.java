/**
 * 
 */
package org.mythtv.service.content.v27;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
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
public class FileListHelperV27 extends AbstractBaseHelper {

	private static final String TAG = FileListHelperV27.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v027;
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static FileListHelperV27 singleton;
	
	/**
	 * Returns the one and only FileListHelperV27. init() must be called before 
	 * any 
	 * @return
	 */
	public static FileListHelperV27 getInstance() {
		if( null == singleton ) {
			
			synchronized( FileListHelperV27.class ) {

				if( null == singleton ) {
					singleton = new FileListHelperV27();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private FileListHelperV27() { }

	public List<String> process( final Context context, final LocationProfile locationProfile, String storageGroupName ) {
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

		List<String> files = null;

		try {

			files = downloadFiles( storageGroupName );
			
		} catch( Exception e ) {
			Log.e( TAG, "process : error", e );
		
			files = null;
		}

		Log.v( TAG, "process : exit" );
		return files;
	}

	// internal helpers
	
	private List<String> downloadFiles( final String storageGroupName ) {
		Log.v( TAG, "downloadFiles : enter" );
	
		List<String> files = null;

		ResponseEntity<org.mythtv.services.api.ArrayOfString> responseEntity = mMythServicesTemplate.contentOperations().getFileList( storageGroupName, ETagInfo.createEmptyETag() );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {

			org.mythtv.services.api.ArrayOfString fileList = responseEntity.getBody();

			if( null != fileList ) {
				
				if( null != fileList.getValue() && fileList.getValue().length > 0 ) {
					
					files = load( fileList.getValue() );
				}
				
			}

		}

		Log.v( TAG, "downloadFiles : exit" );
		return files;
	}
	
	private List<String> load( String[] versionFiles ) {
		Log.v( TAG, "load : enter" );
		
		List<String> files = new ArrayList<String>();
		
		if( null != versionFiles && versionFiles.length > 0 ) {
			
			for( String versionFile : versionFiles ) {
				
				files.add( versionFile );

			}
			
		}
		
		Log.v( TAG, "load : exit" );
		return files;
	}

}
