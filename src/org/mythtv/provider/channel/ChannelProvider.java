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
package org.mythtv.provider.channel;

import org.mythtv.db.DatabaseHelper;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.provider.AbstractMythtvContentProvider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ChannelProvider extends AbstractMythtvContentProvider {

	private static final String TAG = ChannelProvider.class.getSimpleName();

	private static final int CHANNELS = 1;
	private static final int CHANNEL_ID = 2;

	/**
	 * The MIME type of a directory of events
	 */
	private static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mythtv.channel";

	/**
	 * The MIME type of a single event
	 */
	private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mythtv.channel";

	private DatabaseHelper database;
	private UriMatcher uriMatcher;

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		//Log.v( TAG, "onCreate : enter" );
		
		uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
		uriMatcher.addURI( ChannelConstants.AUTHORITY, "channel", CHANNELS );
		uriMatcher.addURI( ChannelConstants.AUTHORITY, "channel/#", CHANNEL_ID );
		
		database = new DatabaseHelper( getContext() );
		
		//Log.v( TAG, "onCreate : exit" );
		return true;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		//Log.v( TAG, "query : enter" );
		
		//if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
		//	Log.v( TAG, "query : uri=" + uri.toString() );
		//}
		
		if( uriMatcher.match( uri ) == CHANNEL_ID ) {
		//	Log.v( TAG, "query : uri segment=" + uri.getPathSegments().get( 1 ) );

			long id = Long.parseLong( uri.getPathSegments().get( 1 ) );
			selection = appendRowId( selection, id );
		}

		// Get the database and run the query
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor cursor = db.query( ChannelConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );

		// Tell the cursor what uri to watch, so it knows when its
		// source data changes
		cursor.setNotificationUri( getContext().getContentResolver(), uri );
		
		//Log.v( TAG, "query : exit" );
		return cursor;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType( Uri uri ) {
		Log.v( TAG, "getType : enter" );

		//if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
		//	Log.v( TAG, "getType : uri=" + uri.toString() );
		//}
		
		switch( uriMatcher.match( uri ) ) {
			case CHANNELS:
				//Log.v( TAG, "getType : exit, channels selected" );

				return CONTENT_TYPE;
			case CHANNEL_ID:
				//Log.v( TAG, "getType : exit, channel id selected" );

				return CONTENT_ITEM_TYPE;
			default:
				//Log.w( TAG, "getType : exit, unknown uri" );

				throw new IllegalArgumentException( "Unknown URI " + uri );
		}
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert( Uri uri, ContentValues values ) {
		//Log.v( TAG, "insert : enter" );

		//if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
		//	Log.v( TAG, "insert : uri=" + uri.toString() );
		//}
		
		SQLiteDatabase db = database.getWritableDatabase();

		// Validate the requested uri
		if( uriMatcher.match( uri ) != CHANNELS ) {
			throw new IllegalArgumentException( "Unknown URI " + uri );
		}

		// Insert into database
		long id = db.insertOrThrow( ChannelConstants.TABLE_NAME, null, values );

		// Notify any watchers of the change
		Uri newUri = ContentUris.withAppendedId( ChannelConstants.CONTENT_URI, id );

		getContext().getContentResolver().notifyChange( newUri, null );
		
		//Log.v( TAG, "insert : exit" );
		return newUri;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs ) {
		//Log.v( TAG, "delete : enter" );
		
		//if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
		//	Log.v( TAG, "delete : uri=" + uri.toString() );
		//}

		SQLiteDatabase db = database.getWritableDatabase();

		String recordId = Long.toString( ContentUris.parseId( uri ) );
		int affected = db.delete( ChannelConstants.TABLE_NAME, BaseColumns._ID
				+ "="
				+ recordId
				+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
		//Log.v( TAG, "delete : exit" );
		return affected;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {
		//Log.v( TAG, "update : enter" );

		//if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
		//	Log.v( TAG, "update : uri=" + uri.toString() );
		//}
		
		SQLiteDatabase db = database.getWritableDatabase();

		// Validate the requested uri
		if( uriMatcher.match( uri ) != CHANNEL_ID ) {
			throw new IllegalArgumentException( "Unknown URI " + uri );
		}

		String recordId = Long.toString( ContentUris.parseId( uri ) );
		int affected = db.update( ChannelConstants.TABLE_NAME, values, BaseColumns._ID
				+ "="
				+ recordId
				+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );

		getContext().getContentResolver().notifyChange( uri, null );

		//Log.v( TAG, "update : exit" );
		return affected;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#bulkInsert(android.net.Uri, android.content.ContentValues[])
	 */
	@Override
	public int bulkInsert( Uri uri, ContentValues[] values ) {
		
		final SQLiteDatabase db = database.getWritableDatabase();

		final int match = uriMatcher.match( uri );
		switch( match ) {
			case CHANNELS:
				int numInserted = 0;
			
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
				throw new UnsupportedOperationException( "unsupported uri: " + uri );
		
		}

	}

}
