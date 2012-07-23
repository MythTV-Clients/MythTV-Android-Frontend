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

import java.util.ArrayList;
import java.util.List;

import org.mythtv.db.content.ArtworkConstants;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.service.AbstractMythtvProcessor;
import org.mythtv.services.api.content.ArtworkInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.ProgramList;
import org.springframework.http.ResponseEntity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ProgramListProcessor extends AbstractMythtvProcessor {

	protected static final String TAG = ProgramListProcessor.class.getSimpleName();

	private ProgramProcessor programProcessor;
	private ProgramGroupProcessor programGroupProcessor;
	
	public interface RecordingListProcessorCallback {

		void send( int resultCode );

	}

	public interface UpcomingListProcessorCallback {

		void send( int resultCode );

	}

	public ProgramListProcessor( Context context ) {
		super( context );
		Log.v( TAG, "initialize : enter" );
		
		programProcessor = new ProgramProcessor( context );
		programGroupProcessor = new ProgramGroupProcessor( context );
		
		Log.v( TAG, "initialize : exit" );
	}

	public void getRecordedList( RecordingListProcessorCallback callback ) {
		Log.v( TAG, "getRecordedList : enter" );

		ResponseEntity<ProgramList> entity = application.getMythServicesApi().dvrOperations().getRecordedListResponseEntity();
		if( Log.isLoggable( TAG, Log.INFO ) ) {
			Log.i( TAG, "getRecordedList : entity status code = " + entity.getStatusCode().toString() );
		}
		
		switch( entity.getStatusCode() ) {
			case OK :
				processProgramList( entity.getBody(), ProgramConstants.ProgramType.RECORDED );
				break;
			default :
				break;
		}
		
		callback.send( entity.getStatusCode().value() );
		
		Log.v( TAG, "getRecordedList : exit" );
	}

	public void getUpcomingList( UpcomingListProcessorCallback callback ) {
		Log.v( TAG, "getUpcomingList : enter" );

		ResponseEntity<ProgramList> entity = application.getMythServicesApi().dvrOperations().getUpcomingListResponseEntity();
		if( Log.isLoggable( TAG, Log.DEBUG ) ) {
			Log.d( TAG, "getUpcomingList : entity status code = " + entity.getStatusCode().toString() );
		}
		
		switch( entity.getStatusCode() ) {
			case OK :
				processProgramList( entity.getBody(), ProgramConstants.ProgramType.UPCOMING );
				break;
			default :
				break;
		}
		
		callback.send( entity.getStatusCode().value() );
		
		Log.v( TAG, "getUpcomingList : exit" );
	}

	// internal helpers
	
	private void processProgramList( ProgramList programList, ProgramConstants.ProgramType programType ) {
		Log.v( TAG, "processProgramList : enter" );

		if( null != programList && null != programList.getPrograms() && ( null != programList.getPrograms().getPrograms() && !programList.getPrograms().getPrograms().isEmpty() ) ) {

			Log.v( TAG, "processProgramList : " + programType.name() + ", count=" + programList.getPrograms().getPrograms().size() );

			List<Long> programGroupIds = new ArrayList<Long>();
			List<Long> programIds = new ArrayList<Long>();
			
			for( Program program : programList.getPrograms().getPrograms() ) {
				
				if( !"livetv".equalsIgnoreCase( program.getRecording().getRecordingGroup() ) ) {
					long programGroupId = programGroupProcessor.updateProgramGroupContentProvider( program, programType );
					
					if( !programGroupIds.contains( programGroupId ) ) {
						programGroupIds.add( programGroupId );
					}
					
					long programId = programProcessor.updateProgramContentProvider( program, programGroupId, programType );
					programIds.add( programId );
					
					updateArtworkContentProvider( program, programId );
				}
			}

			int deletedPrograms = programProcessor.removeDeletedPrograms( programIds, programType );
			Log.d( TAG, "processProgramList : deleted programs=" + deletedPrograms );

			int deletedProgramGroups = programGroupProcessor.removeDeletedProgramGroups( programGroupIds, programType );
			Log.d( TAG, "processProgramList : deleted program groups=" + deletedProgramGroups );
		}
		
		Cursor cursor = mContext.getContentResolver().query( ProgramConstants.CONTENT_URI, new String[] { BaseColumns._ID }, ProgramConstants.FIELD_PROGRAM_TYPE + " = ?", new String[] { programType.name() }, null );
		Log.v( TAG, "processProgramList : " + programType.name() + " - total count=" + cursor.getCount() );
		
		Log.v( TAG, "processProgramList : exit" );
	}

	private void updateArtworkContentProvider( Program program, long programId ) {
		//Log.v( TAG, "updateArtworkContentProvider : enter" );
		
		if( null != program.getArtwork() && ( null != program.getArtwork().getArtworkInfos() && !program.getArtwork().getArtworkInfos().isEmpty() ) ) {
			
			ContentValues values;

			for( ArtworkInfo artwork : program.getArtwork().getArtworkInfos() ) {
		//		Log.v( TAG, "updateArtworkContentProvider : artwork=" + artwork.toString() );
				
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
		
		//Log.v( TAG, "updateArtworkContentProvider : exit" );
	}
	
}
