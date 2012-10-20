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
package org.mythtv.provider;

import java.util.ArrayList;

import org.mythtv.db.DatabaseHelper;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.dvr.ProgramConstants;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
//import org.mythtv.db.dvr.ProgramGroupConstants;

/**
 * @author Daniel Frey
 *
 */
public class MythtvProvider extends AbstractMythtvContentProvider {

	private static final String TAG = MythtvProvider.class.getSimpleName();
	
	public static final String AUTHORITY = "org.mythtv.frontend";
	
	private static final UriMatcher URI_MATCHER;

	private static final String RECORDED_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.recorded";
	private static final String RECORDED_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.recorded";
	private static final int RECORDED 					= 100;
	private static final int RECORDED_ID 				= 101;
	private static final int RECORDED_GROUPS 			= 102;

	private static final String CHANNEL_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.channel";
	private static final String CHANNEL_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.channel";
	private static final int CHANNELS 					= 200;
	private static final int CHANNEL_ID 				= 201;

	static {
		URI_MATCHER = new UriMatcher( UriMatcher.NO_MATCH );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_RECORDED, RECORDED );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_RECORDED + "/#", RECORDED_ID );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_RECORDED + "/programGroups", RECORDED_GROUPS );
		URI_MATCHER.addURI( AUTHORITY, ChannelConstants.TABLE_NAME, CHANNELS );
		URI_MATCHER.addURI( AUTHORITY, ChannelConstants.TABLE_NAME + "/#", CHANNEL_ID );
	}

	private DatabaseHelper database = null;

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		
		database = new DatabaseHelper( getContext() );

		return ( null == database ? false : true );
	}
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType( Uri uri ) {
		
		switch( URI_MATCHER.match( uri ) ) {
			case RECORDED:
				return RECORDED_CONTENT_TYPE;
			
			case RECORDED_ID:
				return RECORDED_CONTENT_ITEM_TYPE;
			
			case RECORDED_GROUPS:
				return RECORDED_CONTENT_TYPE;
			
			case CHANNELS:
				return CHANNEL_CONTENT_TYPE;
			
			case CHANNEL_ID:
				return CHANNEL_CONTENT_ITEM_TYPE;
			
			default:
				throw new IllegalArgumentException( "Unknown URI " + uri );
		}
		
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs ) {
		
		final SQLiteDatabase db = database.getWritableDatabase();
		
		switch( URI_MATCHER.match( uri ) ) {
			case RECORDED:
				return db.delete( ProgramConstants.TABLE_NAME_RECORDED, selection, selectionArgs );
		
			case RECORDED_ID:
				return db.delete( ProgramConstants.TABLE_NAME_RECORDED, ProgramConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
			case CHANNELS:
				return db.delete( ChannelConstants.TABLE_NAME, selection, selectionArgs );
		
			case CHANNEL_ID:
				return db.delete( ChannelConstants.TABLE_NAME, ChannelConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
			default:
				throw new IllegalArgumentException( "Unknown URI " + uri );
		}
		
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert( Uri uri, ContentValues values ) {
		
		final SQLiteDatabase db = database.getWritableDatabase();
		
		Uri newUri = null;
		
		switch( URI_MATCHER.match( uri ) ) {
			case RECORDED:
				newUri = ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_RECORDED, db.insertOrThrow( ProgramConstants.TABLE_NAME_RECORDED, null, values ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case CHANNELS:
				newUri = ContentUris.withAppendedId( ChannelConstants.CONTENT_URI, db.insertOrThrow( ChannelConstants.TABLE_NAME, null, values ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			default:
				throw new IllegalArgumentException( "Unknown URI " + uri );
		}
		
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		
		final SQLiteDatabase db = database.getReadableDatabase();
		
		Cursor cursor = null;
		
		switch( URI_MATCHER.match( uri ) ) {
			case RECORDED:
				cursor = db.query( ProgramConstants.TABLE_NAME_RECORDED, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case RECORDED_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				cursor = db.query( ProgramConstants.TABLE_NAME_RECORDED, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case RECORDED_GROUPS:
				cursor = db.query( ProgramConstants.TABLE_NAME_RECORDED, projection, selection, selectionArgs, ProgramConstants.FIELD_PROGRAM_GROUP, "COUNT(" + ProgramConstants.FIELD_PROGRAM_GROUP + ") > 0", sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case CHANNELS:
				
				cursor = db.query( ChannelConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case CHANNEL_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				cursor = db.query( ChannelConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return null;
	
			default:
				throw new IllegalArgumentException( "Unknown URI " + uri );
		}
		
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {

		final SQLiteDatabase db = database.getWritableDatabase();

		int affected = 0;
		
		switch( URI_MATCHER.match( uri ) ) {
			case RECORDED:
				affected = db.update( ProgramConstants.TABLE_NAME_RECORDED, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case RECORDED_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( ProgramConstants.TABLE_NAME_RECORDED, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case CHANNELS:
				affected = db.update( ChannelConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case CHANNEL_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( ChannelConstants.TABLE_NAME, values, selection , selectionArgs );

				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			default:
				throw new IllegalArgumentException( "Unknown URI: " + uri );
		}
		
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#bulkInsert(android.net.Uri, android.content.ContentValues[])
	 */
	@Override
	public int bulkInsert( Uri uri, ContentValues[] values ) {
		Log.v( TAG, "bulkInsert : enter" );
		
		final SQLiteDatabase db = database.getWritableDatabase();

		int numInserted = 0;

		switch( URI_MATCHER.match( uri ) ) {
			case RECORDED:
				Log.v( TAG, "bulkInsert : inserting programs" );
			
				db.beginTransaction();
				try {
					//standard SQL insert statement, that can be reused
					SQLiteStatement insert = db.compileStatement( ProgramConstants.INSERT_RECORDED_ROW );
				
					for( ContentValues value : values ) {
						insert.bindString( 1, value.getAsString( ProgramConstants.FIELD_PROGRAM_GROUP ) );
						insert.bindLong( 2, value.getAsLong( ProgramConstants.FIELD_START_TIME ) );
						insert.bindLong( 3, value.getAsLong( ProgramConstants.FIELD_END_TIME ) );
						insert.bindLong( 4, value.getAsLong( ProgramConstants.FIELD_DURATION ) );
						insert.bindString( 5, value.getAsString( ProgramConstants.FIELD_START_DATE ) );
						insert.bindString( 6, value.getAsString( ProgramConstants.FIELD_TIMESLOT_HOUR ) );
						insert.bindString( 7, value.getAsString( ProgramConstants.FIELD_TIMESLOT_MINUTE ) );
						insert.bindString( 8, value.getAsString( ProgramConstants.FIELD_TITLE ) );
						insert.bindString( 9, value.getAsString( ProgramConstants.FIELD_SUB_TITLE ) );
						insert.bindString( 10, value.getAsString( ProgramConstants.FIELD_CATEGORY ) );
						insert.bindString( 11, value.getAsString( ProgramConstants.FIELD_CATEGORY_TYPE ) );
						insert.bindLong( 12, value.getAsInteger( ProgramConstants.FIELD_REPEAT ) );
						insert.bindLong( 13, value.getAsInteger( ProgramConstants.FIELD_VIDEO_PROPS ) );
						insert.bindLong( 14, value.getAsInteger( ProgramConstants.FIELD_AUDIO_PROPS ) );
						insert.bindLong( 15, value.getAsInteger( ProgramConstants.FIELD_SUB_PROPS ) );
						insert.bindString( 16, value.getAsString( ProgramConstants.FIELD_SERIES_ID ) );
						insert.bindString( 17, value.getAsString( ProgramConstants.FIELD_PROGRAM_ID ) );
						insert.bindDouble( 18, value.getAsFloat( ProgramConstants.FIELD_STARS ) );
						insert.bindString( 19, value.getAsString( ProgramConstants.FIELD_FILE_SIZE ) );
						insert.bindString( 20, value.getAsString( ProgramConstants.FIELD_LAST_MODIFIED ) );
						insert.bindString( 21, value.getAsString( ProgramConstants.FIELD_PROGRAM_FLAGS ) );
						insert.bindString( 22, value.getAsString( ProgramConstants.FIELD_HOSTNAME ) );
						insert.bindString( 23, value.getAsString( ProgramConstants.FIELD_FILENAME ) );
						insert.bindString( 24, value.getAsString( ProgramConstants.FIELD_AIR_DATE ) );
						insert.bindString( 25, value.getAsString( ProgramConstants.FIELD_DESCRIPTION ) );
						insert.bindString( 26, value.getAsString( ProgramConstants.FIELD_INETREF ) );
						insert.bindString( 27, value.getAsString( ProgramConstants.FIELD_SEASON ) );
						insert.bindString( 28, value.getAsString( ProgramConstants.FIELD_EPISODE ) );
						insert.bindString( 29, value.getAsString( ProgramConstants.FIELD_CHANNEL_NUMBER ) );
						insert.bindLong( 30, value.getAsInteger( ProgramConstants.FIELD_STATUS ) );
						insert.bindLong( 31, value.getAsInteger( ProgramConstants.FIELD_PRIORITY ) );
						insert.bindLong( 31, value.getAsLong( ProgramConstants.FIELD_START_TS ) );
						insert.bindLong( 32, value.getAsLong( ProgramConstants.FIELD_END_TS ) );
						insert.bindLong( 33, value.getAsInteger( ProgramConstants.FIELD_RECORD_ID ) );
						insert.bindString( 34, value.getAsString( ProgramConstants.FIELD_REC_GROUP ) );
						insert.bindString( 35, value.getAsString( ProgramConstants.FIELD_PLAY_GROUP ) );
						insert.bindString( 36, value.getAsString( ProgramConstants.FIELD_STORAGE_GROUP ) );
						insert.bindLong( 37, value.getAsInteger( ProgramConstants.FIELD_REC_TYPE ) );
						insert.bindLong( 38, value.getAsInteger( ProgramConstants.FIELD_DUP_IN_TYPE ) );
						insert.bindLong( 39, value.getAsInteger( ProgramConstants.FIELD_DUP_METHOD ) );
						insert.bindLong( 40, value.getAsInteger( ProgramConstants.FIELD_ENCODER_ID ) );
						insert.bindString( 41, value.getAsString( ProgramConstants.FIELD_PROFILE ) );
						
						insert.execute();
					}
					db.setTransactionSuccessful();
	            
					numInserted = values.length;
				} finally {
					db.endTransaction();
				}

				getContext().getContentResolver().notifyChange( uri, null );

				return numInserted;
			
			case CHANNELS:
			
				db.beginTransaction();
				try {
					//standard SQL insert statement, that can be reused
					SQLiteStatement insert = db.compileStatement( ChannelConstants.INSERT_ROW );
				
					for( ContentValues value : values ) {
						insert.bindString( 1, value.getAsString( ChannelConstants.FIELD_CHAN_ID ) );
						insert.bindString( 2, value.getAsString( ChannelConstants.FIELD_CHAN_NUM ) );
						insert.bindString( 3, value.getAsString( ChannelConstants.FIELD_CALLSIGN ) );
						insert.bindString( 4, value.getAsString( ChannelConstants.FIELD_ICON_URL ) );
						insert.bindString( 5, value.getAsString( ChannelConstants.FIELD_CHANNEL_NAME ) );
						insert.bindLong( 6, value.getAsInteger( ChannelConstants.FIELD_MPLEX_ID ) );
						insert.bindLong( 7, value.getAsInteger( ChannelConstants.FIELD_TRANSPORT_ID ) );
						insert.bindLong( 8, value.getAsInteger( ChannelConstants.FIELD_SERVICE_ID ) );
						insert.bindLong( 9, value.getAsInteger( ChannelConstants.FIELD_NETWORK_ID ) );
						insert.bindLong( 10, value.getAsInteger( ChannelConstants.FIELD_ATSC_MAJOR_CHAN ) );
						insert.bindLong( 11, value.getAsInteger( ChannelConstants.FIELD_ATSC_MINOR_CHAN ) );
						insert.bindString( 12, value.getAsString( ChannelConstants.FIELD_FORMAT ) );
						insert.bindString( 13, value.getAsString( ChannelConstants.FIELD_MODULATION ) );
						insert.bindLong( 14, value.getAsInteger( ChannelConstants.FIELD_FREQUENCY ) );
						insert.bindString( 15, value.getAsString( ChannelConstants.FIELD_FREQUENCY_ID ) );
						insert.bindString( 16, value.getAsString( ChannelConstants.FIELD_FREQUENCY_TABLE ) );
						insert.bindLong( 17, value.getAsInteger( ChannelConstants.FIELD_FINE_TUNE ) );
						insert.bindString( 18, value.getAsString( ChannelConstants.FIELD_SIS_STANDARD ) );
						insert.bindString( 19, value.getAsString( ChannelConstants.FIELD_CHAN_FILTERS ) );
						insert.bindLong( 20, value.getAsInteger( ChannelConstants.FIELD_SOURCE_ID ) );
						insert.bindLong( 21, value.getAsInteger( ChannelConstants.FIELD_INPUT_ID ) );
						insert.bindLong( 22, value.getAsInteger( ChannelConstants.FIELD_COMM_FREE ) );
						insert.bindLong( 23, value.getAsInteger( ChannelConstants.FIELD_USE_EIT ) );
						insert.bindLong( 24, value.getAsInteger( ChannelConstants.FIELD_VISIBLE ) );
						insert.bindString( 25, value.getAsString( ChannelConstants.FIELD_XMLTV_ID ) );
						insert.bindString( 26, value.getAsString( ChannelConstants.FIELD_DEFAULT_AUTH ) );
						insert.execute();
					}
					db.setTransactionSuccessful();
	            
					numInserted = values.length;
				} finally {
					db.endTransaction();
				}
			
				getContext().getContentResolver().notifyChange( uri, null );

				return numInserted;

			default:
				throw new UnsupportedOperationException( "Unsupported URI: " + uri );
		
		}

	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#applyBatch(java.util.ArrayList)
	 */
	@Override
	public ContentProviderResult[] applyBatch( ArrayList<ContentProviderOperation> operations )	throws OperationApplicationException {
	
		final SQLiteDatabase db = database.getWritableDatabase();
		db.beginTransaction();
		try {
			final int numOperations = operations.size();
			final ContentProviderResult[] results = new ContentProviderResult[ numOperations ];
			for( int i = 0; i < numOperations; i++ ) {
				results[ i ] = operations.get( i ).apply( this, results, i );
			}
			db.setTransactionSuccessful();
			return results;
		} finally {
			db.endTransaction();
		}

	}

}
