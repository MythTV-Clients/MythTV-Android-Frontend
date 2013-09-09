/**
 * 
 */
package org.mythtv.service.content.v26;

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

import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class FileListHelperV26 extends AbstractBaseHelper {

	private static final String TAG = FileListHelperV26.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v026;
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static FileListHelperV26 singleton;
	
	/**
	 * Returns the one and only FileListHelperV26. init() must be called before 
	 * any 
	 * @return
	 */
	public static FileListHelperV26 getInstance() {
		if( null == singleton ) {
			
			synchronized( FileListHelperV26.class ) {

				if( null == singleton ) {
					singleton = new FileListHelperV26();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private FileListHelperV26() { }

	public List<String> process( final LocationProfile locationProfile, String storageGroupName ) {
		Log.v( TAG, "process : enter" );
		
		if( !MythAccessFactory.isServerReachable( locationProfile.getUrl() ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
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

		ResponseEntity<org.mythtv.services.api.v026.StringList> responseEntity = mMythServicesTemplate.contentOperations().getFileList( storageGroupName, ETagInfo.createEmptyETag() );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {

			org.mythtv.services.api.v026.StringList fileList = responseEntity.getBody();

			if( null != fileList.getStringList() && fileList.getStringList().length > 0 ) {
				files = load( fileList );	
			}

		}

		Log.v( TAG, "downloadFiles : exit" );
		return files;
	}
	
	private List<String> load( org.mythtv.services.api.v026.StringList versionFiles ) {
		Log.v( TAG, "load : enter" );
		
		List<String> files = new ArrayList<String>();
		
		if( null != versionFiles ) {
			
			for( String versionFile : versionFiles.getStringList() ) {
				
				files.add( versionFile );

			}
			
		}
		
		Log.v( TAG, "load : exit" );
		return files;
	}

}
