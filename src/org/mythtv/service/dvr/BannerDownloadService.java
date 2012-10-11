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
package org.mythtv.service.dvr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.mythtv.service.MythtvService;
import org.mythtv.services.api.ETagInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class BannerDownloadService extends MythtvService {

	private static final String TAG = BannerDownloadService.class.getSimpleName();

	public static final String BANNER_INETREF = "INETREF";
	public static final String BANNER_FILE_EXT = ".png";
	public static final String BANNER_FILE_NA_EXT = ".na";
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.bannerDownload.ACTION_DOWNLOAD";
    public static final String ACTION_COMPLETE = "org.mythtv.background.bannerDownload.ACTION_COMPLETE";

    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_FILENAME = "COMPLETE_FILENAME";

	private File imageCache = null;

	public BannerDownloadService() {
		super( "BannerDownloadService" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );
		
		imageCache = mFileHelper.getProgramImagesDataDirectory();
		if( null == imageCache || !imageCache.exists() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Program Image Cache location can not be found" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, imageCache does not exist" );
			return;
		}

		ResponseEntity<String> hostname = mMainApplication.getMythServicesApi().mythOperations().getHostName();
		if( null == hostname || "".equals( hostname ) ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
			return;
		}

		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		String filename = "";
    		try {
    			filename = download( intent );
    		} catch( Exception e ) {
    			Log.e( TAG, "onHandleIntent : error", e );
    		} finally {
    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Banner Download Service Finished" );
    			completeIntent.putExtra( EXTRA_COMPLETE_FILENAME, filename );
    			sendBroadcast( completeIntent );
    		}
		}
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private String download( Intent intent ) throws Exception {
		Log.v( TAG, "download : enter" );
		
		String inetref = intent.getStringExtra( BANNER_INETREF );
		
		boolean imageNotAvailable = false;
		File checkImageNA = new File( imageCache, inetref + BANNER_FILE_NA_EXT );
		if( checkImageNA.exists() ) {
			imageNotAvailable = true;
		}

		String filename = "";
		File image = new File( imageCache, inetref + BANNER_FILE_EXT );
		if( !image.exists() && !imageNotAvailable ) {
				
			try {
				ETagInfo eTag = ETagInfo.createEmptyETag();
				ResponseEntity<byte[]> responseEntity = mMainApplication.getMythServicesApi().contentOperations().getRecordingArtwork( "Banner", inetref, -1, -1, -1, eTag );
				if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
					byte[] bytes = responseEntity.getBody();
					Bitmap bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.length );

					String name = image.getAbsolutePath();
					FileOutputStream fos = new FileOutputStream( name );
					bitmap.compress( Bitmap.CompressFormat.PNG, 100, fos );
					fos.flush();
					fos.close();
					
					filename = image.getName();
				}
			} catch( Exception e ) {
				Log.e( TAG, "download : error creating image file", e );

				File imageNA = new File( imageCache, inetref + BANNER_FILE_NA_EXT );
				if( !imageNA.exists() ) {
					try {
						imageNA.createNewFile();
						
						filename = imageNA.getName();
					} catch( IOException e1 ) {
						Log.e( TAG, "download : error creating image na file", e1 );
						
						throw new Exception( e1 );
					}
				}
			}

		}

		Log.v( TAG, "download : exit" );
		return filename;
	}
	
}
