/**
 * 
 */
package org.mythtv.service.dvr.v26;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.dvr.RecordingConstants;
import org.mythtv.db.dvr.RecordingConstants.ContentDetails;
import org.mythtv.services.api.v026.beans.Program;
import org.mythtv.services.api.v026.beans.Recording;

import android.content.ContentProviderOperation;
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
public class RecordingHelperV26 extends AbstractBaseHelper {

	private static final String TAG = RecordingHelperV26.class.getSimpleName();
	
	private static RecordingHelperV26 singleton;
	
	/**
	 * Returns the one and only ProgramHelperV26. init() must be called before 
	 * any 
	 * @return
	 */
	public static RecordingHelperV26 getInstance() {
		if( null == singleton ) {
			
			synchronized( RecordingHelperV26.class ) {

				if( null == singleton ) {
					singleton = new RecordingHelperV26();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private RecordingHelperV26() { }

	public void processRecording( final Context context, final LocationProfile locationProfile, ArrayList<ContentProviderOperation> ops, ContentDetails details, Program program, DateTime lastModified, DateTime startTime, int count ) {
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
	
	public void deleteRecordings( ArrayList<ContentProviderOperation> ops, ContentDetails details, DateTime today ) {
		Log.v( TAG, "deleteRecordings : enter" );
		
		ops.add(  
			ContentProviderOperation.newDelete( details.getContentUri() )
				.withSelection( details.getTableName() + "." + RecordingConstants.FIELD_LAST_MODIFIED_DATE + " < ?", new String[] { String.valueOf( today.getMillis() ) } )
				.withYieldAllowed( true )
				.build()
		);

		Log.v( TAG, "deleteRecordings : exit" );
	}

	public boolean deleteRecording( final Context context, final LocationProfile locationProfile, Uri uri, String table, Integer recordId, DateTime startTime ) {
		Log.d( TAG, "deleteProgram : enter" );
		
		String recordingSelection = table + "." + RecordingConstants.FIELD_RECORD_ID + " = ? AND " + table + "." + RecordingConstants.FIELD_START_TIME + " = ?";
		String[] recordingSelectionArgs = new String[] { String.valueOf( recordId ), String.valueOf( startTime ) };

		recordingSelection = appendLocationHostname( context, locationProfile, recordingSelection, null );

		int deleted = context.getContentResolver().delete( uri, recordingSelection, recordingSelectionArgs );
		if( deleted == 1 ) {
			Log.d( TAG, "deleteRecording : exit" );

			return true;
		}
		
		Log.d( TAG, "deleteRecording : exit" );
		return false;
	}

	public Recording convertCursorToRecording( final Cursor cursor, final String table ) {
//		Log.v( TAG, "convertCursorToRecording : enter" );

		RecordingConstants.ContentDetails details = RecordingConstants.ContentDetails.getValueFromParent( table );

		int recordId = -1, status = -1, priority = -1, recordingType = -1, duplicateInType = -1, duplicateMethod = -1, encoderId = -1;
		String recordingGroup = "", playGroup = "", storageGroup = "", profile = "";
		DateTime startTimestamp = null, endTimestamp = null;
		
		if( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_STATUS ) != -1 ) {
			status = cursor.getInt( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_STATUS ) );
		}
		
		if( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_PRIORITY ) != -1 ) {
			priority = cursor.getInt( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_PRIORITY ) );
		}
		
		if( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_START_TS ) != -1 ) {
			startTimestamp = new DateTime( cursor.getLong( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_START_TS ) ) );
		}
		
		if( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_END_TS ) != -1 ) {
			endTimestamp = new DateTime( cursor.getLong( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_END_TS ) ) );
		}
		
		if( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_RECORD_ID ) != -1 ) {
			recordId = cursor.getInt( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_RECORD_ID ) );
		}
		
		if( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_REC_GROUP ) != -1 ) {
			recordingGroup = cursor.getString( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_REC_GROUP ) );
		}
		
		if( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_PLAY_GROUP ) != -1 ) {
			playGroup = cursor.getString( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_PLAY_GROUP ) );
		}
		
		if( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_STORAGE_GROUP ) != -1 ) {
			storageGroup = cursor.getString( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_STORAGE_GROUP ) );
		}
		
		if( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_REC_TYPE ) != -1 ) {
			recordingType = cursor.getInt( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_REC_TYPE ) );
		}
		
		if( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_DUP_IN_TYPE ) != -1 ) {
			duplicateInType = cursor.getInt( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_DUP_IN_TYPE ) );
		}
		
		if( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_DUP_METHOD ) != -1 ) {
			duplicateMethod = cursor.getInt( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_DUP_METHOD ) );
		}
		
		if( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_ENCODER_ID ) != -1 ) {
			encoderId = cursor.getInt( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_ENCODER_ID ) );
		}
		
		if( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_PROFILE ) != -1 ) {
			profile = cursor.getString( cursor.getColumnIndex( details.getTableName() + "_" + RecordingConstants.FIELD_PROFILE ) );
		}
		

		Recording recording = new Recording();
		recording.setStatus( status );
		recording.setPriority( priority );
		recording.setStartTimestamp( startTimestamp );
		recording.setEndTimestamp( endTimestamp );
		recording.setRecordId( recordId );
		recording.setRecordingGroup( recordingGroup );
		recording.setPlayGroup( playGroup );
		recording.setStorageGroup( storageGroup );
		recording.setRecordingType( recordingType );
		recording.setDuplicateInType( duplicateInType );
		recording.setDuplicateMethod( duplicateMethod );
		recording.setEncoderId( encoderId );
		recording.setProfile( profile );
		
//		Log.v( TAG, "convertCursorToRecording : exit" );
		return recording;
	}

	// internal helers
	
	private ContentValues convertRecordingToContentValues( final LocationProfile locationProfile, final DateTime lastModified, final DateTime startTime, final Recording recording ) {
//		Log.v( TAG, "convertRecordingToContentValues : enter" );
		
		DateTime startTimestamp = new DateTime( DateTimeZone.UTC );
		if( null != recording.getStartTimestamp() ) {
			startTimestamp = recording.getStartTimestamp();
		}
//		Log.v( TAG, "convertRecordingToContentValues : startTimestamp = " + startTimestamp.toString() );
		
		DateTime endTimestamp = new DateTime( DateTimeZone.UTC );
		if( null != recording.getStartTimestamp() ) {
			endTimestamp = recording.getEndTimestamp();
		}
		
		ContentValues values = new ContentValues();
		values.put( RecordingConstants.FIELD_STATUS, recording.getStatus() );
		values.put( RecordingConstants.FIELD_PRIORITY, recording.getPriority() );
		values.put( RecordingConstants.FIELD_START_TS, startTimestamp.getMillis() );
		values.put( RecordingConstants.FIELD_END_TS, endTimestamp.getMillis() );
		values.put( RecordingConstants.FIELD_RECORD_ID, recording.getRecordId() );
		values.put( RecordingConstants.FIELD_REC_GROUP, null != recording.getRecordingGroup() ? recording.getRecordingGroup() : "" );
		values.put( RecordingConstants.FIELD_PLAY_GROUP, null != recording.getPlayGroup() ? recording.getPlayGroup() : "" );
		values.put( RecordingConstants.FIELD_STORAGE_GROUP, null != recording.getStorageGroup() ? recording.getStorageGroup() : "" );
		values.put( RecordingConstants.FIELD_REC_TYPE, recording.getRecordingType() );
		values.put( RecordingConstants.FIELD_DUP_IN_TYPE, recording.getDuplicateInType() );
		values.put( RecordingConstants.FIELD_DUP_METHOD, recording.getDuplicateMethod() );
		values.put( RecordingConstants.FIELD_ENCODER_ID, recording.getEncoderId() );
		values.put( RecordingConstants.FIELD_PROFILE, null != recording.getProfile() ? recording.getProfile() : "" );
		values.put( RecordingConstants.FIELD_START_TIME, startTime.getMillis() );
		values.put( RecordingConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		values.put( RecordingConstants.FIELD_LAST_MODIFIED_DATE, lastModified.getMillis() );
		
//		Log.v( TAG, "convertRecordingToContentValues : exit" );
		return values;
	}

}
