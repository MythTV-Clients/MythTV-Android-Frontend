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
import org.mythtv.db.dvr.ProgramGroupConstants;
import org.mythtv.provider.AbstractMythtvContentProvider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGroupProvider extends AbstractMythtvContentProvider {

	private static final String TAG = ProgramGroupProvider.class.getSimpleName();

	private static final int PROGRAM_GROUPS = 1;
	private static final int PROGRAM_GROUP_ID = 2;

	/**
	 * The MIME type of a directory of events
	 */
	private static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mythtv.programGroup";

	/**
	 * The MIME type of a single event
	 */
	private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mythtv.programGroup";

	private DatabaseHelper database;
	private UriMatcher uriMatcher;

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		//Log.v( TAG, "onCreate : enter" );
		
		uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
		uriMatcher.addURI( ProgramGroupConstants.AUTHORITY, "program_group", PROGRAM_GROUPS );
		uriMatcher.addURI( ProgramGroupConstants.AUTHORITY, "program_group/#", PROGRAM_GROUP_ID );
		
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
		
		if( uriMatcher.match( uri ) == PROGRAM_GROUP_ID ) {
		//	Log.v( TAG, "query : uri segment=" + uri.getPathSegments().get( 1 ) );

			long id = Long.parseLong( uri.getPathSegments().get( 1 ) );
			selection = appendRowId( selection, id );
		}

		// Get the database and run the query
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor cursor = db.query( ProgramGroupConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );

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
		//Log.v( TAG, "getType : enter" );

		//if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
		//	Log.v( TAG, "getType : uri=" + uri.toString() );
		//}
		
		switch( uriMatcher.match( uri ) ) {
			case PROGRAM_GROUPS:
		//		Log.v( TAG, "getType : exit, program groups selected" );

				return CONTENT_TYPE;
			case PROGRAM_GROUP_ID:
		//		Log.v( TAG, "getType : exit, program group id selected" );

				return CONTENT_ITEM_TYPE;
			default:
		//		Log.w( TAG, "getType : exit, unknown uri" );

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
		if( uriMatcher.match( uri ) != PROGRAM_GROUPS ) {
			throw new IllegalArgumentException( "Unknown URI " + uri );
		}

		// Insert into database
		long id = db.insertOrThrow( ProgramGroupConstants.TABLE_NAME, null, values );

		// Notify any watchers of the change
		Uri newUri = ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, id );

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
		int affected = db.delete( ProgramGroupConstants.TABLE_NAME, BaseColumns._ID
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
		if( uriMatcher.match( uri ) != PROGRAM_GROUP_ID ) {
			throw new IllegalArgumentException( "Unknown URI " + uri );
		}

		String recordId = Long.toString( ContentUris.parseId( uri ) );
		int affected = db.update( ProgramGroupConstants.TABLE_NAME, values, BaseColumns._ID
				+ "="
				+ recordId
				+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );

		getContext().getContentResolver().notifyChange( uri, null );

		//Log.v( TAG, "update : exit" );
		return affected;
	}

}
