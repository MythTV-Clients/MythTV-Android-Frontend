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

import org.mythtv.db.dvr.RecordingConstants;
import org.mythtv.service.AbstractMythtvProcessor;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.dvr.Recording;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class RecordingProcessor extends AbstractMythtvProcessor {

	protected static final String TAG = RecordingProcessor.class.getSimpleName();

	public RecordingProcessor( Context context ) {
		super( context );
		Log.v( TAG, "initialize : enter" );

		Log.v( TAG, "initialize : exit" );
	}

	public Long updateRecordingContentProvider( Recording recording, long programId ) {
//		Log.v( TAG, "updateRecordingContentProvider : enter" );
		
		if( null != recording ) {
			
			if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
				Log.v( TAG, "updateRecordingContentProvider : recording=" + recording.toString() );
			}
			
			Long id = null;
			
			ContentValues values = new ContentValues();
			values.put( RecordingConstants.FIELD_STATUS, recording.getStatus() );
			values.put( RecordingConstants.FIELD_PRIORITY, recording.getPriority() );
			values.put( RecordingConstants.FIELD_START_TS, null != recording.getStartTimestamp() ? DateUtils.dateTimeFormatter.format( recording.getStartTimestamp() ) : "" );
			values.put( RecordingConstants.FIELD_END_TS, null != recording.getEndTimestamp() ? DateUtils.dateTimeFormatter.format( recording.getEndTimestamp() ) : "" );
			values.put( RecordingConstants.FIELD_RECORD_ID, recording.getRecordid() );
			values.put( RecordingConstants.FIELD_REC_GROUP, null != recording.getRecordingGroup() ? recording.getRecordingGroup() : "" );
			values.put( RecordingConstants.FIELD_STORAGE_GROUP, null != recording.getStorageGroup() ? recording.getStorageGroup() : "" );
			values.put( RecordingConstants.FIELD_PLAY_GROUP, null != recording.getPlayGroup() ? recording.getPlayGroup() : "" );
			values.put( RecordingConstants.FIELD_REC_TYPE, recording.getRecordingType() );
			values.put( RecordingConstants.FIELD_DUP_IN_TYPE, recording.getDuplicateInType() );
			values.put( RecordingConstants.FIELD_DUP_METHOD, recording.getDuplicateMethod() );
			values.put( RecordingConstants.FIELD_ENCODER_ID, recording.getEncoderId() );
			values.put( RecordingConstants.FIELD_PROFILE, null != recording.getProfile() ? recording.getProfile() : "" );
			values.put( RecordingConstants.FIELD_PROGRAM_ID, programId );
				
			Cursor cursor = mContext.getContentResolver().query( RecordingConstants.CONTENT_URI, null, RecordingConstants.FIELD_PROGRAM_ID + " = ?", new String[] { "" + programId }, null );
			if( cursor.moveToFirst() ) {
				id = cursor.getLong( cursor.getColumnIndexOrThrow( RecordingConstants._ID ) );
				mContext.getContentResolver().update( ContentUris.withAppendedId( RecordingConstants.CONTENT_URI, id ), values, null, null );
			} else {
				Uri newRecording = mContext.getContentResolver().insert( RecordingConstants.CONTENT_URI, values );
				id = ContentUris.parseId( newRecording );
			}
			cursor.close();
			
//			Log.v( TAG, "updateRecordingContentProvider : exit" );
			return id;
		}
		
		Log.v( TAG, "updateRecordingContentProvider : exit, no record found" );
		return null;
	}

	public int removeRecording( Long programId ) {
		Log.v( TAG, "removeRecording : enter" );
		
		int count = 0;
		
		Cursor cursor = mContext.getContentResolver().query( RecordingConstants.CONTENT_URI, new String[] { RecordingConstants._ID }, RecordingConstants.FIELD_PROGRAM_ID + " = ?", new String[] { "" + programId }, null );
		if( cursor.moveToFirst() ) {
			Long recordingId = cursor.getLong( cursor.getColumnIndexOrThrow( RecordingConstants._ID ) );
			count = mContext.getContentResolver().delete( ContentUris.withAppendedId( RecordingConstants.CONTENT_URI, recordingId ), null, null );
		}
		cursor.close();
		
		Log.v( TAG, "removeRecording : exit" );
		return count;
	}

}
