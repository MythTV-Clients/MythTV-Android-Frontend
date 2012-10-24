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

import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.service.MythtvService;
import org.mythtv.services.api.ETagInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class BannerDownloadService extends MythtvService {

	private static final String TAG = BannerDownloadService.class.getSimpleName();

	public static final String BANNER_RECORDED_ID = "RECORDED_ID";
	public static final String BANNER_FILE = "banner.png";
	public static final String BANNER_FILE_NA = "banner.na";
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.bannerDownload.ACTION_DOWNLOAD";
    public static final String ACTION_COMPLETE = "org.mythtv.background.bannerDownload.ACTION_COMPLETE";

    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_FILENAME = "COMPLETE_FILENAME";

	private File programGroupsDirectory = null;

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
		
		programGroupsDirectory = mFileHelper.getProgramGroupsDataDirectory();
		if( null == programGroupsDirectory || !programGroupsDirectory.exists() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Program group location can not be found" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, programGroupsDirectory does not exist" );
			return;
		}

		
		if( !isBackendConnected() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
			return;
		}

		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		try {
    			download( intent );
    		} catch( Exception e ) {
    			Log.e( TAG, "onHandleIntent : error", e );
    		} finally {
    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Banner Download Service Finished" );
    			sendBroadcast( completeIntent );
    		}
		}
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void download( Intent intent ) throws Exception {
		Log.v( TAG, "download : enter" );
		
		Long recordedId  = intent.getLongExtra( BANNER_RECORDED_ID, -1L );
		
		Cursor cursor = getContentResolver().query( ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_RECORDED, recordedId ), null, null, null, null );
		if( cursor.moveToFirst() ) {
	        String title = cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_TITLE ) );
	        String inetref = cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_INETREF ) );

			File programGroupDirectory = mFileHelper.getProgramGroupDirectory( title );

			File bannerExists = new File( programGroupDirectory, BANNER_FILE );
			if( bannerExists.exists() ) {
				Log.v( TAG, "download : exit, banner exists" );
				
				return;
			}
			
			boolean bannerNotAvailable = false;
			File checkImageNA = new File( programGroupDirectory, BANNER_FILE_NA );
			if( checkImageNA.exists() ) {
				bannerNotAvailable = true;
			}

			File banner = new File( programGroupDirectory, BANNER_FILE );
			if( !banner.exists() && !bannerNotAvailable ) {
					
				try {
					ETagInfo eTag = ETagInfo.createEmptyETag();
					ResponseEntity<byte[]> responseEntity = mMainApplication.getMythServicesApi().contentOperations().getRecordingArtwork( "Banner", inetref, -1, -1, -1, eTag );
					if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
						byte[] bytes = responseEntity.getBody();
						Bitmap bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.length );

						String name = banner.getAbsolutePath();
						FileOutputStream fos = new FileOutputStream( name );
						bitmap.compress( Bitmap.CompressFormat.PNG, 100, fos );
						fos.flush();
						fos.close();

						Log.i( TAG, "download : banner image retreived:" + banner.getAbsolutePath() );
					}
				} catch( Exception e ) {
					Log.e( TAG, "download : error creating image file", e );

					File bannerNA = new File( programGroupDirectory, BANNER_FILE_NA );
					if( !bannerNA.exists() ) {
						try {
							bannerNA.createNewFile();
						} catch( IOException e1 ) {
							Log.e( TAG, "download : error creating image na file", e1 );
							
							throw new Exception( e1 );
						}
					}
				}
			}
		}
		cursor.close();
		
		Log.v( TAG, "download : exit" );
	}
	
}
