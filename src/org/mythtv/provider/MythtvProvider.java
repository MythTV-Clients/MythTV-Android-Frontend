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
import org.mythtv.db.http.EtagConstants;
import org.mythtv.db.preferences.LocationProfileConstants;
import org.mythtv.db.preferences.PlaybackProfileConstants;
import org.mythtv.db.status.StatusConstants;

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

	private static final String UPCOMING_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.upcoming";
	private static final String UPCOMING_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.upcoming";
	private static final int UPCOMING 					= 110;
	private static final int UPCOMING_ID 				= 111;
	private static final int UPCOMING_START_DATE		= 112;

	private static final String PROGRAM_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.program";
	private static final String PROGRAM_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.program";
	private static final int PROGRAM 					= 120;
	private static final int PROGRAM_ID 				= 121;

	private static final String CHANNEL_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.channel";
	private static final String CHANNEL_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.channel";
	private static final int CHANNELS 					= 200;
	private static final int CHANNEL_ID 				= 201;

	private static final String ETAG_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.etag";
	private static final String ETAG_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.etag";
	private static final int ETAGS 					= 1000;
	private static final int ETAG_ID 				= 1001;
	private static final int ETAG_ENDPOINT	 		= 1002;

	private static final String STATUS_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.status";
	private static final String STATUS_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.status";
	private static final int STATUS 				= 1100;
	private static final int STATUS_ID 				= 1101;

	private static final String LOCATION_PROFILE_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.locationProfile";
	private static final String LOCATION_PROFILE_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.locationProfile";
	private static final int LOCATION_PROFILE 		= 2000;
	private static final int LOCATION_PROFILE_ID 	= 2001;

	private static final String PLAYBACK_PROFILE_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.playbackProfile";
	private static final String PLAYBACK_PROFILE_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.playbackProfile";
	private static final int PLAYBACK_PROFILE 		= 2100;
	private static final int PLAYBACK_PROFILE_ID 	= 2101;

	static {
		URI_MATCHER = new UriMatcher( UriMatcher.NO_MATCH );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_RECORDED, RECORDED );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_RECORDED + "/#", RECORDED_ID );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_RECORDED + "/programGroups", RECORDED_GROUPS );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_UPCOMING, UPCOMING );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_UPCOMING + "/#", UPCOMING_ID );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_UPCOMING + "/startDate", UPCOMING_START_DATE );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_PROGRAM, PROGRAM );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_PROGRAM + "/#", PROGRAM_ID );
		URI_MATCHER.addURI( AUTHORITY, ChannelConstants.TABLE_NAME, CHANNELS );
		URI_MATCHER.addURI( AUTHORITY, ChannelConstants.TABLE_NAME + "/#", CHANNEL_ID );
		URI_MATCHER.addURI( AUTHORITY, EtagConstants.TABLE_NAME, ETAGS );
		URI_MATCHER.addURI( AUTHORITY, EtagConstants.TABLE_NAME + "/#", ETAG_ID );
		URI_MATCHER.addURI( AUTHORITY, EtagConstants.TABLE_NAME + "/endpoint", ETAG_ENDPOINT );
		URI_MATCHER.addURI( AUTHORITY, StatusConstants.TABLE_NAME, STATUS );
		URI_MATCHER.addURI( AUTHORITY, StatusConstants.TABLE_NAME + "/#", STATUS_ID );
		URI_MATCHER.addURI( AUTHORITY, LocationProfileConstants.TABLE_NAME, LOCATION_PROFILE );
		URI_MATCHER.addURI( AUTHORITY, LocationProfileConstants.TABLE_NAME + "/#", LOCATION_PROFILE_ID );
		URI_MATCHER.addURI( AUTHORITY, PlaybackProfileConstants.TABLE_NAME, PLAYBACK_PROFILE );
		URI_MATCHER.addURI( AUTHORITY, PlaybackProfileConstants.TABLE_NAME + "/#", PLAYBACK_PROFILE_ID );
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
			
			case UPCOMING:
				return UPCOMING_CONTENT_TYPE;
			
			case UPCOMING_ID:
				return UPCOMING_CONTENT_ITEM_TYPE;
			
			case UPCOMING_START_DATE:
				return UPCOMING_CONTENT_TYPE;
			
			case PROGRAM:
				return PROGRAM_CONTENT_TYPE;
			
			case PROGRAM_ID:
				return PROGRAM_CONTENT_ITEM_TYPE;
			
			case CHANNELS:
				return CHANNEL_CONTENT_TYPE;
			
			case CHANNEL_ID:
				return CHANNEL_CONTENT_ITEM_TYPE;
			
			case ETAGS:
				return ETAG_CONTENT_TYPE;
			
			case ETAG_ID:
				return ETAG_CONTENT_ITEM_TYPE;
			
			case ETAG_ENDPOINT:
				return ETAG_CONTENT_ITEM_TYPE;
			
			case STATUS:
				return STATUS_CONTENT_TYPE;
			
			case STATUS_ID:
				return STATUS_CONTENT_ITEM_TYPE;
			
			case LOCATION_PROFILE:
				return LOCATION_PROFILE_CONTENT_TYPE;
			
			case LOCATION_PROFILE_ID:
				return LOCATION_PROFILE_CONTENT_ITEM_TYPE;
			
			case PLAYBACK_PROFILE:
				return PLAYBACK_PROFILE_CONTENT_TYPE;
			
			case PLAYBACK_PROFILE_ID:
				return PLAYBACK_PROFILE_CONTENT_ITEM_TYPE;
			
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
		
		int deleted;
		
		switch( URI_MATCHER.match( uri ) ) {
			case RECORDED:

				deleted = db.delete( ProgramConstants.TABLE_NAME_RECORDED, selection, selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case RECORDED_ID:
				
				deleted = db.delete( ProgramConstants.TABLE_NAME_RECORDED, ProgramConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case UPCOMING:
				
				deleted = db.delete( ProgramConstants.TABLE_NAME_UPCOMING, selection, selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
		
				return deleted;

			case UPCOMING_ID:

				deleted = db.delete( ProgramConstants.TABLE_NAME_UPCOMING, ProgramConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case PROGRAM:
				
				deleted = db.delete( ProgramConstants.TABLE_NAME_PROGRAM, selection, selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
		
				return deleted;

			case PROGRAM_ID:

				deleted = db.delete( ProgramConstants.TABLE_NAME_PROGRAM, ProgramConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case CHANNELS:

				deleted = db.delete( ChannelConstants.TABLE_NAME, selection, selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case CHANNEL_ID:

				deleted = db.delete( ChannelConstants.TABLE_NAME, ChannelConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case ETAGS:

				deleted = db.delete( EtagConstants.TABLE_NAME, selection, selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;
			
			case ETAG_ID:

				deleted = db.delete( EtagConstants.TABLE_NAME, EtagConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case STATUS:

				deleted = db.delete( StatusConstants.TABLE_NAME, selection, selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;
			
			case STATUS_ID:

				deleted = db.delete( StatusConstants.TABLE_NAME, StatusConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case LOCATION_PROFILE:

				deleted = db.delete( LocationProfileConstants.TABLE_NAME, selection, selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;
			
			case LOCATION_PROFILE_ID:

				deleted = db.delete( LocationProfileConstants.TABLE_NAME, LocationProfileConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case PLAYBACK_PROFILE:

				deleted = db.delete( PlaybackProfileConstants.TABLE_NAME, selection, selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;
			
			case PLAYBACK_PROFILE_ID:

				deleted = db.delete( PlaybackProfileConstants.TABLE_NAME, PlaybackProfileConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

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
	
			case UPCOMING:
				newUri = ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_UPCOMING, db.insertOrThrow( ProgramConstants.TABLE_NAME_UPCOMING, null, values ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case PROGRAM:
				newUri = ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_PROGRAM, db.insertOrThrow( ProgramConstants.TABLE_NAME_PROGRAM, null, values ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case CHANNELS:
				newUri = ContentUris.withAppendedId( ChannelConstants.CONTENT_URI, db.insertOrThrow( ChannelConstants.TABLE_NAME, null, values ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case ETAGS:
				newUri = ContentUris.withAppendedId( EtagConstants.CONTENT_URI, db.insertOrThrow( EtagConstants.TABLE_NAME, null, values ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case STATUS:
				newUri = ContentUris.withAppendedId( StatusConstants.CONTENT_URI, db.insertOrThrow( StatusConstants.TABLE_NAME, null, values ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case LOCATION_PROFILE:
				newUri = ContentUris.withAppendedId( LocationProfileConstants.CONTENT_URI, db.insertOrThrow( LocationProfileConstants.TABLE_NAME, null, values ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case PLAYBACK_PROFILE:
				newUri = ContentUris.withAppendedId( PlaybackProfileConstants.CONTENT_URI, db.insertOrThrow( PlaybackProfileConstants.TABLE_NAME, null, values ) );
				
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
	
			case UPCOMING:
				cursor = db.query( ProgramConstants.TABLE_NAME_UPCOMING, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case UPCOMING_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				cursor = db.query( ProgramConstants.TABLE_NAME_UPCOMING, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case UPCOMING_START_DATE:
				cursor = db.query( ProgramConstants.TABLE_NAME_UPCOMING, projection, selection, selectionArgs, ProgramConstants.FIELD_START_DATE, "COUNT(" + ProgramConstants.FIELD_PROGRAM_GROUP + ") > 0", sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case PROGRAM:
				cursor = db.query( ProgramConstants.TABLE_NAME_PROGRAM, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case PROGRAM_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				cursor = db.query( ProgramConstants.TABLE_NAME_PROGRAM, projection, selection, selectionArgs, null, null, sortOrder );
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
				
				return cursor;
	
			case ETAGS:
				
				cursor = db.query( EtagConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case ETAG_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				cursor = db.query( EtagConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case ETAG_ENDPOINT:
				cursor = db.query( EtagConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;

			case STATUS:
				
				cursor = db.query( StatusConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case STATUS_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				cursor = db.query( StatusConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case LOCATION_PROFILE:
				
				cursor = db.query( LocationProfileConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case LOCATION_PROFILE_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				cursor = db.query( LocationProfileConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case PLAYBACK_PROFILE:
				
				cursor = db.query( PlaybackProfileConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case PLAYBACK_PROFILE_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				cursor = db.query( PlaybackProfileConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
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

			case UPCOMING:
				affected = db.update( ProgramConstants.TABLE_NAME_UPCOMING, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case UPCOMING_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( ProgramConstants.TABLE_NAME_UPCOMING, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case PROGRAM:
				affected = db.update( ProgramConstants.TABLE_NAME_PROGRAM, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case PROGRAM_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( ProgramConstants.TABLE_NAME_PROGRAM, values, selection , selectionArgs );
				
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

			case ETAGS:
				affected = db.update( EtagConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case ETAG_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( EtagConstants.TABLE_NAME, values, selection , selectionArgs );

				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case STATUS:
				affected = db.update( StatusConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case STATUS_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( StatusConstants.TABLE_NAME, values, selection , selectionArgs );

				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case LOCATION_PROFILE:
				affected = db.update( LocationProfileConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case LOCATION_PROFILE_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( LocationProfileConstants.TABLE_NAME, values, selection , selectionArgs );

				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case PLAYBACK_PROFILE:
				affected = db.update( PlaybackProfileConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case PLAYBACK_PROFILE_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( PlaybackProfileConstants.TABLE_NAME, values, selection , selectionArgs );

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
				Log.v( TAG, "bulkInsert : inserting recorded programs" );
			
				db.beginTransaction();
				try {
					//standard SQL insert statement, that can be reused
					SQLiteStatement insert = db.compileStatement( ProgramConstants.INSERT_RECORDED_ROW );
					bulkInsertPrograms( insert, values );
					
					db.setTransactionSuccessful();
	            
					numInserted = values.length;
				} finally {
					db.endTransaction();
				}

				getContext().getContentResolver().notifyChange( uri, null );

				return numInserted;
			
			case UPCOMING:
				Log.v( TAG, "bulkInsert : inserting upcoming programs" );
			
				db.beginTransaction();
				try {
					//standard SQL insert statement, that can be reused
					SQLiteStatement insert = db.compileStatement( ProgramConstants.INSERT_UPCOMING_ROW );
					bulkInsertPrograms( insert, values );
					
					db.setTransactionSuccessful();
	            
					numInserted = values.length;
				} finally {
					db.endTransaction();
				}

				getContext().getContentResolver().notifyChange( uri, null );

				return numInserted;
			
			case PROGRAM:
				Log.v( TAG, "bulkInsert : inserting programs" );
			
				db.beginTransaction();
				try {
					//standard SQL insert statement, that can be reused
					SQLiteStatement insert = db.compileStatement( ProgramConstants.INSERT_PROGRAM_ROW );
					bulkInsertPrograms( insert, values );
					
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
						insert.bindString( 1, value.getAsString( ChannelConstants._ID ) );
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

	// internal helpers
	
	private void bulkInsertPrograms( SQLiteStatement insert, ContentValues[] values ) {
		
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
			insert.bindString( 29, value.getAsString( ProgramConstants.FIELD_CHANNEL_ID ) );
			insert.bindString( 30, value.getAsString( ProgramConstants.FIELD_CHANNEL_NUMBER ) );
			insert.bindString( 31, value.getAsString( ProgramConstants.FIELD_CHANNEL_CALLSIGN ) );
			insert.bindLong( 32, value.getAsInteger( ProgramConstants.FIELD_STATUS ) );
			insert.bindLong( 33, value.getAsInteger( ProgramConstants.FIELD_PRIORITY ) );
			insert.bindLong( 34, value.getAsLong( ProgramConstants.FIELD_START_TS ) );
			insert.bindLong( 35, value.getAsLong( ProgramConstants.FIELD_END_TS ) );
			insert.bindLong( 36, value.getAsInteger( ProgramConstants.FIELD_RECORD_ID ) );
			insert.bindString( 37, value.getAsString( ProgramConstants.FIELD_REC_GROUP ) );
			insert.bindString( 38, value.getAsString( ProgramConstants.FIELD_PLAY_GROUP ) );
			insert.bindString( 39, value.getAsString( ProgramConstants.FIELD_STORAGE_GROUP ) );
			insert.bindLong( 40, value.getAsInteger( ProgramConstants.FIELD_REC_TYPE ) );
			insert.bindLong( 41, value.getAsInteger( ProgramConstants.FIELD_DUP_IN_TYPE ) );
			insert.bindLong( 42, value.getAsInteger( ProgramConstants.FIELD_DUP_METHOD ) );
			insert.bindLong( 43, value.getAsInteger( ProgramConstants.FIELD_ENCODER_ID ) );
			insert.bindString( 44, value.getAsString( ProgramConstants.FIELD_PROFILE ) );
			
			insert.execute();
		}

	}
}
