/**
 * 
 */
package org.mythtv.db.dvr;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
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
public class RecordingDaoHelper {

	private static final String TAG = RecordingDaoHelper.class.getSimpleName();
	
	private Context mContext;
	
	/**
	 * @param context
	 */
	public RecordingDaoHelper( Context context ) {
		this.mContext = context;
	}

	/**
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public List<Recording> findAll( String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.d( TAG, "findAll : enter" );
		
		List<Recording> recordings = new ArrayList<Recording>();
		
		Cursor cursor = mContext.getContentResolver().query( RecordingConstants.CONTENT_URI, projection, selection, selectionArgs, sortOrder );
		while( cursor.moveToNext() ) {
			Recording recording = convertCursorToRecording( cursor );
			recordings.add( recording );
		}
		cursor.close();

		Log.d( TAG, "findAll : exit" );
		return recordings;
	}
	
	/**
	 * @return
	 */
	public List<Recording> finalAll() {
		Log.d( TAG, "findAll : enter" );
		
		List<Recording> recordings = findAll( null, null, null, null );
		
		Log.d( TAG, "findAll : exit" );
		return recordings;
	}
	
	/**
	 * @param id
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public Recording findOne( Long id, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.d( TAG, "findOne : enter" );
		
		Recording recording = null;
		
		Uri uri = RecordingConstants.CONTENT_URI;
		if( null != id && id > 0 ) {
			uri = ContentUris.withAppendedId( RecordingConstants.CONTENT_URI, id );
		}
		
		Cursor cursor = mContext.getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );
		if( cursor.moveToFirst() ) {
			recording = convertCursorToRecording( cursor );
		}
		cursor.close();
		
		Log.d( TAG, "findOne : exit" );
		return recording;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public Recording findOne( Long id ) {
		Log.d( TAG, "findOne : enter" );
		
		Recording recording = findOne( id, null, null, null, null );
		
		Log.d( TAG, "findOne : exit" );
		return recording;
	}

	/**
	 * @param recording
	 * @return
	 */
	public int save( Recording recording ) {
		Log.d( TAG, "save : enter" );

		ContentValues values = convertRecordingToContentValues( recording );

		int updated = -1;
		Cursor cursor = mContext.getContentResolver().query( ContentUris.withAppendedId( RecordingConstants.CONTENT_URI, recording.getRecordId() ), null, null, null, null );
		if( cursor.moveToFirst() ) {
			Log.v( TAG, "save : updating existing channel info" );

			updated = mContext.getContentResolver().update( ContentUris.withAppendedId( RecordingConstants.CONTENT_URI, recording.getRecordId() ), values, null, null );
		} else {
			Uri inserted = mContext.getContentResolver().insert( ContentUris.withAppendedId( RecordingConstants.CONTENT_URI, recording.getRecordId() ), values );
			if( null != inserted ) {
				updated = 1;
			}
		}
		cursor.close();
		Log.v( TAG, "save : updated=" + updated );

		Log.d( TAG, "save : exit" );
		return updated;
	}

	/**
	 * @return
	 */
	public int deleteAll() {
		Log.d( TAG, "deleteAll : enter" );
		
		int deleted = mContext.getContentResolver().delete( RecordingConstants.CONTENT_URI, null, null );
		Log.v( TAG, "deleteAll : deleted=" + deleted );
		
		Log.d( TAG, "deleteAll : exit" );
		return deleted;
	}

	/**
	 * @param id
	 * @return
	 */
	public int delete( Long id ) {
		Log.d( TAG, "delete : enter" );
		
		int deleted = mContext.getContentResolver().delete( ContentUris.withAppendedId( RecordingConstants.CONTENT_URI, id ), null, null );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param recordings
	 * @return
	 */
	public int load( List<Recording> recordings ) {
		Log.d( TAG, "load : enter" );
		
		int loaded = -1;
		
		ContentValues[] contentValuesArray = convertRecordingsToContentValuesArray( recordings );
		if( null != contentValuesArray ) {
			Log.v( TAG, "processChannels : channels=" + contentValuesArray.length );

			loaded = mContext.getContentResolver().bulkInsert( RecordingConstants.CONTENT_URI, contentValuesArray );
			Log.v( TAG, "load : loaded=" + loaded );
		}
		
		
		Log.d( TAG, "load : exit" );
		return loaded;
	}
	
	/**
	 * @param cursor
	 * @return
	 */
	public Recording convertCursorToRecording( Cursor cursor ) {
//		Log.v( TAG, "convertCursorToRecording : enter" );

		long id = -1, recordId = -1;
		int status = -1, priority = -1, recordingType = -1, duplicateInType = -1, duplicateMethod = -1, encoderId = -1;
		String recordingGroup = "", playGroup = "", storageGroup = "", profile = "";
		DateTime startTimestamp = null, endTimestamp = null;
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants._ID ) != -1 ) {
			id = cursor.getInt( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants._ID ) );
		}
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_STATUS ) != -1 ) {
			status = cursor.getInt( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_STATUS ) );
		}
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_PRIORITY ) != -1 ) {
			priority = cursor.getInt( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_PRIORITY ) );
		}
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_START_TS ) != -1 ) {
			startTimestamp = new DateTime( cursor.getLong( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_START_TS ) ) );
		}
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_END_TS ) != -1 ) {
			endTimestamp = new DateTime( cursor.getLong( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_END_TS ) ) );
		}
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_RECORD_ID ) != -1 ) {
			recordId = cursor.getLong( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_RECORD_ID ) );
		}
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_REC_GROUP ) != -1 ) {
			recordingGroup = cursor.getString( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_REC_GROUP ) );
		}
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_PLAY_GROUP ) != -1 ) {
			playGroup = cursor.getString( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_PLAY_GROUP ) );
		}
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_STORAGE_GROUP ) != -1 ) {
			storageGroup = cursor.getString( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_STORAGE_GROUP ) );
		}
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_REC_TYPE ) != -1 ) {
			recordingType = cursor.getInt( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_REC_TYPE ) );
		}
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_DUP_IN_TYPE ) != -1 ) {
			duplicateInType = cursor.getInt( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_DUP_IN_TYPE ) );
		}
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_DUP_METHOD ) != -1 ) {
			duplicateMethod = cursor.getInt( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_DUP_METHOD ) );
		}
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_ENCODER_ID ) != -1 ) {
			encoderId = cursor.getInt( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_ENCODER_ID ) );
		}
		
		if( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_PROFILE ) != -1 ) {
			profile = cursor.getString( cursor.getColumnIndex( RecordingConstants.TABLE_NAME + "_" + RecordingConstants.FIELD_PROFILE ) );
		}
		

		Recording recording = new Recording();
		recording.setRecordId( id );
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
		//recording.setProgramId();
		
//		Log.v( TAG, "convertCursorToRecording : exit" );
		return recording;
	}

	/**
	 * @param recording
	 * @return
	 */
	public ContentValues convertRecordingToContentValues( final Recording recording ) {
//		Log.v( TAG, "convertRecordingToContentValues : enter" );
		
		DateTime startTimestamp = null;
		if( null != recording.getStartTimestamp() ) {
			startTimestamp = new DateTime( recording.getStartTimestamp().getMillis() );
		}
		
		DateTime endTimestamp = null;
		if( null != recording.getStartTimestamp() ) {
			endTimestamp = new DateTime( recording.getEndTimestamp().getMillis() );
		}
		
		ContentValues values = new ContentValues();
		values.put( RecordingConstants.FIELD_STATUS, recording.getStatus() );
		values.put( RecordingConstants.FIELD_PRIORITY, recording.getPriority() );
		values.put( RecordingConstants.FIELD_START_TS, null != startTimestamp ? startTimestamp.getMillis() : -1 );
		values.put( RecordingConstants.FIELD_END_TS, null != endTimestamp ? endTimestamp.getMillis() : -1 );
		values.put( RecordingConstants.FIELD_RECORD_ID, recording.getRecordId() );
		values.put( RecordingConstants.FIELD_REC_GROUP, null != recording.getRecordingGroup() ? recording.getRecordingGroup() : "" );
		values.put( RecordingConstants.FIELD_PLAY_GROUP, null != recording.getPlayGroup() ? recording.getPlayGroup() : "" );
		values.put( RecordingConstants.FIELD_STORAGE_GROUP, null != recording.getStorageGroup() ? recording.getStorageGroup() : "" );
		values.put( RecordingConstants.FIELD_REC_TYPE, recording.getRecordingType() );
		values.put( RecordingConstants.FIELD_DUP_IN_TYPE, recording.getDuplicateInType() );
		values.put( RecordingConstants.FIELD_DUP_METHOD, recording.getDuplicateMethod() );
		values.put( RecordingConstants.FIELD_ENCODER_ID, recording.getEncoderId() );
		values.put( RecordingConstants.FIELD_PROFILE, null != recording.getProfile() ? recording.getProfile() : "" );
		//values.put( RecordingConstants.FIELD_PROGRAM_ID, null );
		
//		Log.v( TAG, "convertRecordingToContentValues : exit" );
		return values;
	}

	// internal helpers

	private ContentValues[] convertRecordingsToContentValuesArray( final List<Recording> recordings ) {
//		Log.v( TAG, "convertRecordingsToContentValuesArray : enter" );
		
		if( null != recordings && !recordings.isEmpty() ) {
			
			ContentValues contentValues;
			List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();

			for( Recording recording : recordings ) {

				contentValues = convertRecordingToContentValues( recording );
				contentValuesArray.add( contentValues );

			}			
			
			if( !contentValuesArray.isEmpty() ) {
				
//				Log.v( TAG, "convertRecordingsToContentValuesArray : exit" );
				return contentValuesArray.toArray( new ContentValues[ contentValuesArray.size() ] );
			}
			
		}
		
//		Log.v( TAG, "convertRecordingsToContentValuesArray : exit, no recordings to convert" );
		return null;
	}

}
