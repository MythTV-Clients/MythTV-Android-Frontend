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
package org.mythtv.provider.dvr;

import org.mythtv.db.DatabaseHelper;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.provider.AbstractMythtvContentProvider;

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
public class ProgramProvider extends AbstractMythtvContentProvider {

	private static final int PROGRAMS = 1;
	private static final int PROGRAM_ID = 2;

	/**
	 * The MIME type of a directory of programs
	 */
	private static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mythtv.program";

	/**
	 * The MIME type of a single program
	 */
	private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mythtv.program";

	private DatabaseHelper database;
	private UriMatcher uriMatcher;

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		
		uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
		uriMatcher.addURI( ProgramConstants.AUTHORITY, "program", PROGRAMS );
		uriMatcher.addURI( ProgramConstants.AUTHORITY, "program/#", PROGRAM_ID );
		
		database = new DatabaseHelper( getContext() );
		
		return true;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		
		if( uriMatcher.match( uri ) == PROGRAM_ID ) {

			long id = Long.parseLong( uri.getPathSegments().get( 1 ) );
			selection = appendRowId( selection, id );
		}

		// Get the database and run the query
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor cursor = db.query( ProgramConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );

		// Tell the cursor what uri to watch, so it knows when its
		// source data changes
		cursor.setNotificationUri( getContext().getContentResolver(), uri );
		
		return cursor;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType( Uri uri ) {
		
		switch( uriMatcher.match( uri ) ) {
			case PROGRAMS:

				return CONTENT_TYPE;
			case PROGRAM_ID:

				return CONTENT_ITEM_TYPE;
			default:

				throw new IllegalArgumentException( "Unknown URI " + uri );
		}
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert( Uri uri, ContentValues values ) {
		
		SQLiteDatabase db = database.getWritableDatabase();

		// Validate the requested uri
		if( uriMatcher.match( uri ) != PROGRAMS ) {
			throw new IllegalArgumentException( "Unknown URI " + uri );
		}

		// Insert into database
		long id = db.insertOrThrow( ProgramConstants.TABLE_NAME, null, values );

		// Notify any watchers of the change
		Uri newUri = ContentUris.withAppendedId( ProgramConstants.CONTENT_URI, id );

		getContext().getContentResolver().notifyChange( newUri, null );
		
		return newUri;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs ) {
		
		SQLiteDatabase db = database.getWritableDatabase();

		String recordId = Long.toString( ContentUris.parseId( uri ) );
		int affected = db.delete( ProgramConstants.TABLE_NAME, ProgramConstants._ID
				+ "="
				+ recordId
				+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
		return affected;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {

		SQLiteDatabase db = database.getWritableDatabase();
		
		// Validate the requested uri
		if( uriMatcher.match( uri ) == PROGRAM_ID ) {

			long id = Long.parseLong( uri.getPathSegments().get( 1 ) );
			selection = appendRowId( selection, id );
		}

		int affected = db.update( ProgramConstants.TABLE_NAME, values, selection , selectionArgs );

		getContext().getContentResolver().notifyChange( uri, null );

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
			case PROGRAMS:
				int numInserted = 0;
			
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
						insert.bindLong( 27, value.getAsInteger( ProgramConstants.FIELD_PROGRAM_GROUP_ID ) );
						insert.bindLong( 28, value.getAsInteger( ProgramConstants.FIELD_CHANNEL_ID ) );
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
