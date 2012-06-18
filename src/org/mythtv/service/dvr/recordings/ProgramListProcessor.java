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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */

package org.mythtv.service.dvr.recordings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.mythtv.client.MainApplication;
import org.mythtv.db.content.ArtworkConstants;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.services.api.content.ArtworkInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.ProgramList;
import org.springframework.http.ResponseEntity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ProgramListProcessor {

	protected static final String TAG = ProgramListProcessor.class.getSimpleName();

	private static final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );
	
	private MainApplication application;
	private Context mContext;

	public interface RecordingListProcessorCallback {

		void send( int resultCode );

	}

	public ProgramListProcessor( Context context ) {
		Log.v( TAG, "initialize : enter" );
		
		mContext = context;
		application = (MainApplication) context.getApplicationContext();
		
		Log.v( TAG, "initialize : exit" );
	}

	public void getRecordedList( RecordingListProcessorCallback callback ) {
		Log.d( TAG, "getRecordedList : enter" );

		ResponseEntity<ProgramList> entity = application.getMythServicesApi().dvrOperations().getRecordedListResponseEntity();
		Log.d( TAG, "getRecordedList : entity status code = " + entity.getStatusCode().toString() );
		
		updateProgramContentProvider( entity.getBody() );
		
		callback.send( entity.getStatusCode().value() );
		
		Log.d( TAG, "getRecordedList : exit" );
	}

	// internal helpers
	
	private void updateProgramContentProvider( ProgramList programList ) {
		Log.v( TAG, "updateProgramContentProvider : enter" );

		if( null != programList && null != programList.getPrograms() && ( null != programList.getPrograms().getPrograms() && !programList.getPrograms().getPrograms().isEmpty() ) ) {

			ContentValues values;
			
			List<String> programIds = new ArrayList<String>();
			
			for( Program program : programList.getPrograms().getPrograms() ) {
				Log.v( TAG, "updateProgramContentProvider : program=" + program.toString() );
				
				if( !"livetv".equalsIgnoreCase( program.getRecording().getRecordingGroup() ) ) {
				values = new ContentValues();
				values.put( ProgramConstants.FIELD_START_TIME, null != program.getStartTime() ? sdf.format( program.getStartTime() ) : "" );
				values.put( ProgramConstants.FIELD_END_TIME, null != program.getEndTime() ? sdf.format( program.getEndTime() ) : "" );
				values.put( ProgramConstants.FIELD_TITLE, null != program.getTitle() ? program.getTitle() : "" );
				values.put( ProgramConstants.FIELD_SUB_TITLE, null != program.getSubTitle() ? program.getSubTitle() : "" );
				values.put( ProgramConstants.FIELD_CATEGORY, null != program.getCategory() ? program.getCategory() : "" );
				values.put( ProgramConstants.FIELD_CATEGORY_TYPE, null != program.getCategoryType() ? program.getCategoryType() : "" );
				values.put( ProgramConstants.FIELD_REPEAT, program.isRepeat() );
				values.put( ProgramConstants.FIELD_VIDEO_PROPS, program.getVideoProps() );
				values.put( ProgramConstants.FIELD_AUDIO_PROPS, program.getAudioProps() );
				values.put( ProgramConstants.FIELD_SUB_PROPS, program.getSubProps() );
				values.put( ProgramConstants.FIELD_SERIES_ID, null != program.getSeriesId() ? program.getSeriesId() : "" );
				values.put( ProgramConstants.FIELD_PROGRAM_ID, null != program.getProgramId() ? program.getProgramId() : "" );
				values.put( ProgramConstants.FIELD_STARS, program.getStars() );
				values.put( ProgramConstants.FIELD_FILE_SIZE, null != program.getFileSize() ? program.getFileSize() : "" );
				values.put( ProgramConstants.FIELD_LAST_MODIFIED, null != program.getLastModified() ? sdf.format( program.getLastModified() ) : "" );
				values.put( ProgramConstants.FIELD_PROGRAM_FLAGS, null != program.getProgramFlags() ? program.getProgramFlags() : "" );
				values.put( ProgramConstants.FIELD_HOSTNAME, null != program.getHostname() ? program.getHostname() : "" );
				values.put( ProgramConstants.FIELD_FILENAME, null != program.getFilename() ? program.getFilename() : "" );
				values.put( ProgramConstants.FIELD_AIR_DATE, null != program.getAirDate() ? sdf.format( program.getAirDate() ) : "" );
				values.put( ProgramConstants.FIELD_DESCRIPTION, null != program.getDescription() ? program.getDescription() : "" );
				values.put( ProgramConstants.FIELD_INETREF, null != program.getInetref() ? program.getInetref() : "" );
				values.put( ProgramConstants.FIELD_SEASON, null != program.getSeason() ? program.getSeason() : "" );
				values.put( ProgramConstants.FIELD_EPISODE, null != program.getEpisode() ? program.getEpisode() : "" );

				Cursor cursor = mContext.getContentResolver().query( ProgramConstants.CONTENT_URI, null, ProgramConstants.FIELD_PROGRAM_ID + " = ?", new String[] { program.getProgramId() }, null );
				if( cursor.moveToFirst() ) {
					int id = cursor.getInt( cursor.getColumnIndexOrThrow( BaseColumns._ID ) );
					mContext.getContentResolver().update( ContentUris.withAppendedId( ProgramConstants.CONTENT_URI, id ), values, null, null );
				} else {
					mContext.getContentResolver().insert( ProgramConstants.CONTENT_URI, values );
				}
				cursor.close();
				
				programIds.add( program.getProgramId() );

				updateArtworkContentProvider( program );
				}
			}
			Log.v( TAG, "updateProgramContentProvider : programIds=" + programIds.toString() );

			if( !programIds.isEmpty() ) {
				Log.v( TAG, "updateProgramContentProvider : looking up programs to remove" );

				StringBuilder sb = new StringBuilder();
				for( int i = 0; i < programIds.size(); i++ ) {
					sb.append( "'" ).append( programIds.get( i ) ).append( "'" );
					
					if( i < programIds.size() - 1 ) {
						sb.append( ", " );
					}
				}
				Log.v( TAG, "updateProgramContentProvider : sb=" + sb.toString() );
				
				List<Integer> deleteIds = new ArrayList<Integer>();
				Cursor cursor = mContext.getContentResolver().query( ProgramConstants.CONTENT_URI, new String[] { BaseColumns._ID }, ProgramConstants.FIELD_PROGRAM_ID + " not in (" + sb.toString() + ")", null, null );
				while( cursor.moveToNext() ) {
					int id = cursor.getInt( cursor.getColumnIndexOrThrow( BaseColumns._ID ) );
					deleteIds.add( id );

					Log.v( TAG, "updateProgramContentProvider : queing for deletion, id=" + id );
				}
				cursor.close();
				
				if( !deleteIds.isEmpty() ) {
					for( Integer id : deleteIds ) {
						int deleted = mContext.getContentResolver().delete( ContentUris.withAppendedId( ProgramConstants.CONTENT_URI, id ), null, null );
						
						Log.v( TAG, "updateProgramContentProvider : deleted, id=" + deleted );
					}
				}
			}
		}
		
		Log.v( TAG, "updateProgramContentProvider : exit" );
	}

	private void updateArtworkContentProvider( Program program ) {
		Log.v( TAG, "updateArtworkContentProvider : enter" );
		
		if( null != program.getArtwork() && ( null != program.getArtwork().getArtworkInfos() && !program.getArtwork().getArtworkInfos().isEmpty() ) ) {
			
			ContentValues values;

			for( ArtworkInfo artwork : program.getArtwork().getArtworkInfos() ) {
				Log.v( TAG, "updateArtworkContentProvider : artwork=" + artwork.toString() );
				
				values = new ContentValues();
				values.put( ArtworkConstants.FIELD_URL, null != artwork.getUrl() ? artwork.getUrl() : "" );
				values.put( ArtworkConstants.FIELD_FILE_NAME, null != artwork.getFilename() ? artwork.getFilename() : "" );
				values.put( ArtworkConstants.FIELD_STORAGE_GROUP, null != artwork.getStorageGroup() ? artwork.getStorageGroup() : "" );
				values.put( ArtworkConstants.FIELD_TYPE, null != artwork.getType() ? artwork.getType() : "" );
				
				Cursor cursor = mContext.getContentResolver().query( ArtworkConstants.CONTENT_URI, null, ArtworkConstants.FIELD_URL + " = ?", new String[] { artwork.getUrl() }, null );
				if( cursor.moveToFirst() ) {
					//int id = cursor.getInt( cursor.getColumnIndexOrThrow( BaseColumns._ID ) );
					//mContext.getContentResolver().update( ContentUris.withAppendedId( ArtworkConstants.CONTENT_URI, id ), values, null, null );
				} else {
					mContext.getContentResolver().insert( ArtworkConstants.CONTENT_URI, values );
				}
				cursor.close();
				
			}

		}
		
		Log.v( TAG, "updateArtworkContentProvider : exit" );
	}
	
}
