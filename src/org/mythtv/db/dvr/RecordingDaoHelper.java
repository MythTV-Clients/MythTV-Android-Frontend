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
package org.mythtv.db.dvr;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractDaoHelper;
import org.mythtv.services.api.dvr.Recording;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
//import android.util.Log;


/**
 * @author Daniel Frey
 *
 */
public class RecordingDaoHelper extends AbstractDaoHelper {

//	private static final String TAG = RecordingDaoHelper.class.getSimpleName();
	
	private static RecordingDaoHelper singleton = null;

	/**
	 * Returns the one and only ChannelDaoHelper. init() must be called before 
	 * any 
	 * 
	 * @return
	 */
	public static RecordingDaoHelper getInstance() {
		if( null == singleton ) {

			synchronized( RecordingDaoHelper.class ) {

				if( null == singleton ) {
					singleton = new RecordingDaoHelper();
				}
			
			}

		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private RecordingDaoHelper() {
		super();
	}
	

	/**
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public List<Recording> findAll( final Context context, String[] projection, String selection, String[] selectionArgs, String sortOrder, final String table ) {
//		Log.d( TAG, "findAll : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordingDaoHelper is not initialized" );

		RecordingConstants.ContentDetails details = RecordingConstants.ContentDetails.getValueFromParent( table );

		List<Recording> recordings = new ArrayList<Recording>();
		
		Cursor cursor = context.getContentResolver().query( details.getContentUri(), projection, selection, selectionArgs, sortOrder );
		while( cursor.moveToNext() ) {
			Recording recording = convertCursorToRecording( cursor, table );
			recordings.add( recording );
		}
		cursor.close();

//		Log.d( TAG, "findAll : exit" );
		return recordings;
	}
	
	/**
	 * @return
	 */
	public List<Recording> finalAll( final Context context, final String table ) {
//		Log.d( TAG, "findAll : enter" );
		
		List<Recording> recordings = findAll( context, null, null, null, null, table );
		
//		Log.d( TAG, "findAll : exit" );
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
	public Recording findOne( final Context context, final Long id, String[] projection, String selection, String[] selectionArgs, String sortOrder, final String table ) {
//		Log.d( TAG, "findOne : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordingDaoHelper is not initialized" );
		
		RecordingConstants.ContentDetails details = RecordingConstants.ContentDetails.getValueFromParent( table );

		Recording recording = null;
		
		Uri uri = details.getContentUri();
		if( null != id && id > 0 ) {
			uri = ContentUris.withAppendedId( uri, id );
		}
		
		Cursor cursor = context.getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );
		if( cursor.moveToFirst() ) {
			recording = convertCursorToRecording( cursor, table );
		}
		cursor.close();
		
//		Log.d( TAG, "findOne : exit" );
		return recording;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public Recording findOne( final Context context, final Long id, final String table ) {
//		Log.d( TAG, "findOne : enter" );
		
		Recording recording = findOne( context, id, null, null, null, null, table );
		
//		Log.d( TAG, "findOne : exit" );
		return recording;
	}

	/**
	 * @return
	 */
	public int deleteAll( final Context context, final String table ) {
//		Log.d( TAG, "deleteAll : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordingDaoHelper is not initialized" );
		
		RecordingConstants.ContentDetails details = RecordingConstants.ContentDetails.getValueFromParent( table );

		int deleted = context.getContentResolver().delete( details.getContentUri(), null, null );
//		Log.v( TAG, "deleteAll : deleted=" + deleted );
		
//		Log.d( TAG, "deleteAll : exit" );
		return deleted;
	}

	public int delete( final Context context, final Long id, final String where, final String[] selectionArgs, final String table ) {
//		Log.d( TAG, "delete : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordingDaoHelper is not initialized" );
		
		RecordingConstants.ContentDetails details = RecordingConstants.ContentDetails.getValueFromParent( table );

		Uri uri = details.getContentUri();
		if( null != id && id > 0 ) {
			uri = ContentUris.withAppendedId( uri, id );
		}
		
		int deleted = context.getContentResolver().delete( uri, where, selectionArgs );
//		Log.v( TAG, "delete : deleted=" + deleted );
		
//		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param context
	 * @param id
	 * @return
	 */
	public int delete( final Context context, final Long id, final String table ) {
//		Log.d( TAG, "delete : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordingDaoHelper is not initialized" );
		
		int deleted = delete( context, id, null, null, table );
//		Log.v( TAG, "delete : deleted=" + deleted );
		
//		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param cursor
	 * @return
	 */
	public static Recording convertCursorToRecording( final Cursor cursor, final String table ) {
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

	/**
	 * @param recording
	 * @return
	 */
	public static ContentValues convertRecordingToContentValues( final LocationProfile locationProfile, final DateTime lastModified, final DateTime startTime, final Recording recording ) {
//		Log.v( TAG, "convertRecordingToContentValues : enter" );
		
		DateTime startTimestamp = null;
		if( null != recording.getStartTimestamp() ) {
			startTimestamp = new DateTime( recording.getStartTimestamp().getMillis() );
		}
//		Log.v( TAG, "convertRecordingToContentValues : startTimestamp = " + startTimestamp.toString() );
		
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
		values.put( RecordingConstants.FIELD_START_TIME, startTime.getMillis() );
		values.put( RecordingConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		values.put( RecordingConstants.FIELD_LAST_MODIFIED_DATE, lastModified.getMillis() );
		
//		Log.v( TAG, "convertRecordingToContentValues : exit" );
		return values;
	}

}
