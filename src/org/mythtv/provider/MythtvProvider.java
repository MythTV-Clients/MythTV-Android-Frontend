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
package org.mythtv.provider;

import org.mythtv.db.DatabaseHelper;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.dvr.ProgramConstants;
//import org.mythtv.db.dvr.ProgramGroupConstants;
import org.mythtv.db.dvr.RecordingConstants;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.text.TextUtils;

/**
 * @author Daniel Frey
 *
 */
public class MythtvProvider extends AbstractMythtvContentProvider {

	public static final String AUTHORITY = "org.mythtv.provider.MythtvProvider";
	
	private static final UriMatcher URI_MATCHER;

	private static final String PROGRAM_CONTENT_TYPE = "vnd.mythtv.cursor.dir/program";
	private static final String PROGRAM_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/program";
	private static final int PROGRAMS = 1;
	private static final int PROGRAM_ID = 2;
	private static final int PROGRAM_GROUPS = 3;

	private static final String RECORDING_CONTENT_TYPE = "vnd.mythtv.cursor.dir/recording";
	private static final String RECORDING_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/recording";
	private static final int RECORDINGS = 4;
	private static final int RECORDING_ID = 5;

	private static final String CHANNEL_CONTENT_TYPE = "vnd.mythtv.cursor.dir/channel";
	private static final String CHANNEL_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/channel";
	private static final int CHANNELS = 6;
	private static final int CHANNEL_ID = 7;

	static {
		URI_MATCHER = new UriMatcher( UriMatcher.NO_MATCH );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME, PROGRAMS );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME + "/#", PROGRAM_ID );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME + "/programGroups", PROGRAM_GROUPS );
		URI_MATCHER.addURI( AUTHORITY, RecordingConstants.TABLE_NAME, RECORDINGS );
		URI_MATCHER.addURI( AUTHORITY, RecordingConstants.TABLE_NAME + "/#", RECORDING_ID );
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
			case PROGRAMS:
				return PROGRAM_CONTENT_TYPE;
			
			case PROGRAM_ID:
				return PROGRAM_CONTENT_ITEM_TYPE;
			
			case PROGRAM_GROUPS:
				return PROGRAM_CONTENT_TYPE;
			
			case RECORDINGS:
				return RECORDING_CONTENT_TYPE;
			
			case RECORDING_ID:
				return RECORDING_CONTENT_ITEM_TYPE;
			
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
			case PROGRAMS:
				return db.delete( ProgramConstants.TABLE_NAME, selection, selectionArgs );
		
			case PROGRAM_ID:
				return db.delete( ProgramConstants.TABLE_NAME, ProgramConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
			case RECORDINGS:
				return db.delete( RecordingConstants.TABLE_NAME, selection, selectionArgs );
		
			case RECORDING_ID:
				return db.delete( RecordingConstants.TABLE_NAME, ProgramConstants._ID
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
			case PROGRAMS:
				newUri = ContentUris.withAppendedId( ProgramConstants.CONTENT_URI, db.insertOrThrow( ProgramConstants.TABLE_NAME, null, values ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case RECORDINGS:
				newUri = ContentUris.withAppendedId( RecordingConstants.CONTENT_URI, db.insertOrThrow( RecordingConstants.TABLE_NAME, null, values ) );
				
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
			case PROGRAMS:
				cursor = db.query( ProgramConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case PROGRAM_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				cursor = db.query( ProgramConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case PROGRAM_GROUPS:
				cursor = db.query( ProgramConstants.TABLE_NAME, projection, selection, selectionArgs, ProgramConstants.FIELD_PROGRAM_GROUP, "COUNT(" + ProgramConstants.FIELD_PROGRAM_GROUP + ") > 0", sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case RECORDINGS:
				cursor = db.query( RecordingConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case RECORDING_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				cursor = db.query( RecordingConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
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
			case PROGRAMS:
				affected = db.update( ProgramConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case PROGRAM_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( ProgramConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case RECORDINGS:
				affected = db.update( RecordingConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case RECORDING_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( RecordingConstants.TABLE_NAME, values, selection , selectionArgs );
				
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
	
		final SQLiteDatabase db = database.getWritableDatabase();

		int numInserted = 0;

		switch( URI_MATCHER.match( uri ) ) {
			case PROGRAMS:
			
				db.beginTransaction();
				try {
					//standard SQL insert statement, that can be reused
					SQLiteStatement insert = db.compileStatement( ProgramConstants.INSERT_ROW );
				
					for( ContentValues value : values ) {
						insert.bindString( 1, value.getAsString( ProgramConstants.FIELD_PROGRAM_TYPE ) );
						insert.bindString( 2, value.getAsString( ProgramConstants.FIELD_PROGRAM_GROUP ) );
						insert.bindString( 3, value.getAsString( ProgramConstants.FIELD_START_DATE ) );
						insert.bindString( 4, value.getAsString( ProgramConstants.FIELD_START_TIME ) );
						insert.bindString( 5, value.getAsString( ProgramConstants.FIELD_END_TIME ) );
						insert.bindString( 6, value.getAsString( ProgramConstants.FIELD_TITLE ) );
						insert.bindString( 7, value.getAsString( ProgramConstants.FIELD_SUB_TITLE ) );
						insert.bindString( 8, value.getAsString( ProgramConstants.FIELD_CATEGORY ) );
						insert.bindString( 9, value.getAsString( ProgramConstants.FIELD_CATEGORY_TYPE ) );
						insert.bindLong( 10, value.getAsInteger( ProgramConstants.FIELD_REPEAT ) );
						insert.bindLong( 11, value.getAsInteger( ProgramConstants.FIELD_VIDEO_PROPS ) );
						insert.bindLong( 12, value.getAsInteger( ProgramConstants.FIELD_AUDIO_PROPS ) );
						insert.bindLong( 13, value.getAsInteger( ProgramConstants.FIELD_SUB_PROPS ) );
						insert.bindString( 14, value.getAsString( ProgramConstants.FIELD_SERIES_ID ) );
						insert.bindString( 15, value.getAsString( ProgramConstants.FIELD_PROGRAM_ID ) );
						insert.bindDouble( 16, value.getAsFloat( ProgramConstants.FIELD_STARS ) );
						insert.bindString( 17, value.getAsString( ProgramConstants.FIELD_FILE_SIZE ) );
						insert.bindString( 18, value.getAsString( ProgramConstants.FIELD_LAST_MODIFIED ) );
						insert.bindString( 19, value.getAsString( ProgramConstants.FIELD_PROGRAM_FLAGS ) );
						insert.bindString( 20, value.getAsString( ProgramConstants.FIELD_HOSTNAME ) );
						insert.bindString( 21, value.getAsString( ProgramConstants.FIELD_FILENAME ) );
						insert.bindString( 22, value.getAsString( ProgramConstants.FIELD_AIR_DATE ) );
						insert.bindString( 23, value.getAsString( ProgramConstants.FIELD_DESCRIPTION ) );
						insert.bindString( 24, value.getAsString( ProgramConstants.FIELD_INETREF ) );
						insert.bindString( 25, value.getAsString( ProgramConstants.FIELD_SEASON ) );
						insert.bindString( 26, value.getAsString( ProgramConstants.FIELD_EPISODE ) );
						insert.bindString( 27, value.getAsString( ProgramConstants.FIELD_CHANNEL_ID ) );
						insert.execute();
					}
					db.setTransactionSuccessful();
	            
					numInserted = values.length;
				} finally {
					db.endTransaction();
				}
			
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
			
				return numInserted;

			default:
				throw new UnsupportedOperationException( "Unsupported URI: " + uri );
		
		}

	}

}
