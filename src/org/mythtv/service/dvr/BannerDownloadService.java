/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.service.dvr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.mythtv.service.MythtvService;
import org.mythtv.services.api.ETagInfo;

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
		
        if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		download( intent );
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void download( Intent intent ) {
//		Log.v( TAG, "download : enter" );
		
		boolean newDataDownloaded = false;
		String filename = "";
		
		String inetref = intent.getStringExtra( BANNER_INETREF );
		
		File imageCache = mFileHelper.getProgramImagesDataDirectory();
		if( imageCache.exists() ) {

			boolean imageNotAvailable = false;
			File checkImageNA = new File( imageCache, inetref + BANNER_FILE_NA_EXT );
			if( checkImageNA.exists() ) {
				imageNotAvailable = true;
			}

			File image = new File( imageCache, inetref + BANNER_FILE_EXT );
			if( !image.exists() && !imageNotAvailable ) {
				
				try {
					ETagInfo eTag = ETagInfo.createEmptyETag();
					byte[] bytes = mMainApplication.getMythServicesApi().contentOperations().getRecordingArtwork( "Banner", inetref, -1, -1, -1, eTag );
					Bitmap bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.length );

	                String name = image.getAbsolutePath();
	                FileOutputStream fos = new FileOutputStream( name );
	                bitmap.compress( Bitmap.CompressFormat.PNG, 100, fos );
	                fos.flush();
	                fos.close();

	                newDataDownloaded = true;
					filename = image.getName();
				} catch( Exception e ) {
					Log.e( TAG, "download : error creating image file", e );

					File imageNA = new File( imageCache, inetref + BANNER_FILE_NA_EXT );
					if( !imageNA.exists() ) {
						try {
							imageNA.createNewFile();
						} catch( IOException e1 ) {
							Log.e( TAG, "download : error creating image na file", e1 );
						}
					}
				}

			}

		}
		
		if( newDataDownloaded ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Banner Download Service Finished" );
			completeIntent.putExtra( EXTRA_COMPLETE_FILENAME, filename );
			sendBroadcast( completeIntent );
		}
		
//		Log.v( TAG, "download : exit" );
	}
	
}
