/**
 * 
 */
package org.mythtv.service.dvr.v27;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.dvr.RecordingConstants;
import org.mythtv.db.dvr.RecordingConstants.ContentDetails;
import org.mythtv.services.api.v027.beans.Program;
import org.mythtv.services.api.v027.beans.RecordingInfo;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class RecordingHelperV27 {

	private static final String TAG = RecordingHelperV27.class.getSimpleName();
	
	public static void processRecording( final Context context, final LocationProfile locationProfile, ArrayList<ContentProviderOperation> ops, ContentDetails details, Program program, DateTime lastModified, DateTime startTime, int count ) {
		Log.v( TAG, "processRecording : enter" );
		
		String[] recordingProjection = new String[] { details.getTableName() + "_" + RecordingConstants._ID };
		String recordingSelection = RecordingConstants.FIELD_RECORD_ID + " = ? AND " + RecordingConstants.FIELD_START_TIME + " = ? AND " + RecordingConstants.FIELD_MASTER_HOSTNAME + " = ?";
		String[] recordingSelectionArgs = new String[] { String.valueOf( program.getRecording().getRecordId() ), String.valueOf( program.getStartTime().getMillis() ), locationProfile.getHostname() };

		//Log.v( TAG, "processRecording : recording=" + program.getRecording().toString() );

		ContentValues recordingValues = convertRecordingToContentValues( locationProfile, lastModified, startTime, program.getRecording() );
		Cursor recordingCursor = context.getContentResolver().query( details.getContentUri(), recordingProjection, recordingSelection, recordingSelectionArgs, null );
		if( recordingCursor.moveToFirst() ) {
			//Log.v( TAG, "processRecording : UPDATE RECORDING " + count + ":" + program.getTitle() + ", recording=" + program.getRecording().getRecordId() );

			Long id = recordingCursor.getLong( recordingCursor.getColumnIndexOrThrow( details.getTableName() + "_" + RecordingConstants._ID ) );					
			ops.add( 
				ContentProviderOperation.newUpdate( ContentUris.withAppendedId( details.getContentUri(), id ) )
					.withValues( recordingValues )
					.withYieldAllowed( true )
					.build()
				);
		} else {
			//Log.v( TAG, "processRecording : INSERT RECORDING " + count + ":" + program.getTitle() + ", recording=" + program.getRecording().getRecordId() );

			ops.add(  
				ContentProviderOperation.newInsert( details.getContentUri() )
					.withValues( recordingValues )
					.withYieldAllowed( true )
					.build()
				);
		}
		recordingCursor.close();
		count++;

		Log.v( TAG, "processRecording : exit" );
	}

	public static void deleteRecordings( ArrayList<ContentProviderOperation> ops, ContentDetails details, DateTime today ) {
		Log.v( TAG, "deleteRecordings : enter" );
		
		ops.add(  
			ContentProviderOperation.newDelete( details.getContentUri() )
				.withSelection( details.getTableName() + "." + RecordingConstants.FIELD_LAST_MODIFIED_DATE + " < ?", new String[] { String.valueOf( today.getMillis() ) } )
				.withYieldAllowed( true )
				.build()
		);

		Log.v( TAG, "deleteRecordings : exit" );
	}

	public static ContentValues convertRecordingToContentValues( final LocationProfile locationProfile, final DateTime lastModified, final DateTime startTime, final RecordingInfo recording ) {
//		Log.v( TAG, "convertRecordingToContentValues : enter" );
		
		DateTime startTimestamp = new DateTime( DateTimeZone.UTC );
		if( null != recording.getStartTs() ) {
			startTimestamp = recording.getStartTs();
		}
//		Log.v( TAG, "convertRecordingToContentValues : startTimestamp = " + startTimestamp.toString() );
		
		DateTime endTimestamp = new DateTime( DateTimeZone.UTC );
		if( null != recording.getEndTs() ) {
			endTimestamp = recording.getEndTs();
		}
		
		ContentValues values = new ContentValues();
		values.put( RecordingConstants.FIELD_STATUS, recording.getStatus() );
		values.put( RecordingConstants.FIELD_PRIORITY, recording.getPriority() );
		values.put( RecordingConstants.FIELD_START_TS, startTimestamp.getMillis() );
		values.put( RecordingConstants.FIELD_END_TS, endTimestamp.getMillis() );
		values.put( RecordingConstants.FIELD_RECORD_ID, recording.getRecordId() );
		values.put( RecordingConstants.FIELD_REC_GROUP, null != recording.getRecGroup() ? recording.getRecGroup() : "" );
		values.put( RecordingConstants.FIELD_PLAY_GROUP, null != recording.getPlayGroup() ? recording.getPlayGroup() : "" );
		values.put( RecordingConstants.FIELD_STORAGE_GROUP, null != recording.getStorageGroup() ? recording.getStorageGroup() : "" );
		values.put( RecordingConstants.FIELD_REC_TYPE, recording.getRecType() );
		values.put( RecordingConstants.FIELD_DUP_IN_TYPE, recording.getDupInType() );
		values.put( RecordingConstants.FIELD_DUP_METHOD, recording.getDupMethod() );
		values.put( RecordingConstants.FIELD_ENCODER_ID, recording.getEncoderId() );
		values.put( RecordingConstants.FIELD_PROFILE, null != recording.getProfile() ? recording.getProfile() : "" );
		values.put( RecordingConstants.FIELD_START_TIME, startTime.getMillis() );
		values.put( RecordingConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		values.put( RecordingConstants.FIELD_LAST_MODIFIED_DATE, lastModified.getMillis() );
		
//		Log.v( TAG, "convertRecordingToContentValues : exit" );
		return values;
	}

}
