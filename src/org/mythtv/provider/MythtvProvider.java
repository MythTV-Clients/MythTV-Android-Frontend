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
import java.util.HashMap;
import java.util.Map;

import org.mythtv.db.DatabaseHelper;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.content.LiveStreamConstants;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.RecordingConstants;
import org.mythtv.db.dvr.RecordingRuleConstants;
import org.mythtv.db.dvr.programGroup.ProgramGroupConstants;
import org.mythtv.db.frontends.FrontendConstants;
import org.mythtv.db.http.EtagConstants;
import org.mythtv.db.preferences.LocationProfileConstants;
import org.mythtv.db.preferences.PlaybackProfileConstants;
import org.mythtv.db.status.StatusConstants;

import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

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
	private static final int RECORDED_ID 					= 101;
	
	private static final String UPCOMING_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.upcoming";
	private static final String UPCOMING_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.upcoming";
	private static final int UPCOMING 					= 110;
	private static final int UPCOMING_ID 					= 111;

	private static final String GUIDE_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.guide";
	private static final String GUIDE_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.guide";
	private static final int GUIDE 						= 120;
	private static final int GUIDE_ID 					= 121;

	private static final String PROGRAM_GROUP_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.programGroup";
	private static final String PROGRAM_GROUP_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.programGroup";
	private static final int PROGRAM_GROUP 				= 130;
	private static final int PROGRAM_GROUP_ID				= 131;

	private static final String RECORDING_GUIDE_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.recordingGuide";
	private static final String RECORDING_GUIDE_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.recordingGuide";
	private static final int RECORDING_GUIDE 				= 140;
	private static final int RECORDING_GUIDE_ID			= 141;

	private static final String RECORDING_RECORDED_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.recordingRecorded";
	private static final String RECORDING_RECORDED_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.recordingRecorded";
	private static final int RECORDING_RECORDED			= 150;
	private static final int RECORDING_RECORDED_ID		= 151;

	private static final String RECORDING_UPCOMING_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.recordingUpcoming";
	private static final String RECORDING_UPCOMING_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.recordingUpcoming";
	private static final int RECORDING_UPCOMING			= 160;
	private static final int RECORDING_UPCOMING_ID		= 161;

	private static final String RECORDING_RULE_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.recordingRule";
	private static final String RECORDING_RULE_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.recordingRule";
	private static final int RECORDING_RULE				= 170;
	private static final int RECORDING_RULE_ID			= 171;


	private static final String LIVE_STREAM_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.liveStream";
	private static final String LIVE_STREAM_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.liveStream";
	private static final int LIVE_STREAM 					= 200;
	private static final int LIVE_STREAM_ID				= 201;

	private static final String CHANNEL_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.channel";
	private static final String CHANNEL_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.channel";
	private static final int CHANNELS 					= 300;
	private static final int CHANNEL_ID 					= 301;

	private static final String FRONTEND_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.frontend";
	private static final String FRONTEND_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.frontend";
	private static final int FRONTENDS 					= 400;
	private static final int FRONTEND_ID 					= 401;

	private static final String ETAG_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.etag";
	private static final String ETAG_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.etag";
	private static final int ETAGS 						= 1000;
	private static final int ETAG_ID 						= 1001;
	private static final int ETAG_ENDPOINT	 			= 1002;

	private static final String STATUS_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.status";
	private static final String STATUS_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.status";
	private static final int STATUS 						= 1100;
	private static final int STATUS_ID 					= 1101;

	private static final String LOCATION_PROFILE_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.locationProfile";
	private static final String LOCATION_PROFILE_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.locationProfile";
	private static final int LOCATION_PROFILE 			= 2000;
	private static final int LOCATION_PROFILE_ID 			= 2001;

	private static final String PLAYBACK_PROFILE_CONTENT_TYPE = "vnd.mythtv.cursor.dir/org.mythtv.playbackProfile";
	private static final String PLAYBACK_PROFILE_CONTENT_ITEM_TYPE = "vnd.mythtv.cursor.item/org.mythtv.playbackProfile";
	private static final int PLAYBACK_PROFILE 			= 2100;
	private static final int PLAYBACK_PROFILE_ID 			= 2101;

	static {
		URI_MATCHER = new UriMatcher( UriMatcher.NO_MATCH );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_RECORDED, RECORDED );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_RECORDED + "/#", RECORDED_ID );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_UPCOMING, UPCOMING );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_UPCOMING + "/#", UPCOMING_ID );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_GUIDE, GUIDE );
		URI_MATCHER.addURI( AUTHORITY, ProgramConstants.TABLE_NAME_GUIDE + "/#", GUIDE_ID );
		URI_MATCHER.addURI( AUTHORITY, ProgramGroupConstants.TABLE_NAME, PROGRAM_GROUP );
		URI_MATCHER.addURI( AUTHORITY, ProgramGroupConstants.TABLE_NAME + "/#", PROGRAM_GROUP_ID );
		URI_MATCHER.addURI( AUTHORITY, RecordingConstants.ContentDetails.GUIDE.getTableName(), RECORDING_GUIDE );
		URI_MATCHER.addURI( AUTHORITY, RecordingConstants.ContentDetails.GUIDE.getTableName() + "/#", RECORDING_GUIDE_ID );
		URI_MATCHER.addURI( AUTHORITY, RecordingConstants.ContentDetails.RECORDED.getTableName(), RECORDING_RECORDED );
		URI_MATCHER.addURI( AUTHORITY, RecordingConstants.ContentDetails.RECORDED.getTableName() + "/#", RECORDING_RECORDED_ID );
		URI_MATCHER.addURI( AUTHORITY, RecordingConstants.ContentDetails.UPCOMING.getTableName(), RECORDING_UPCOMING );
		URI_MATCHER.addURI( AUTHORITY, RecordingConstants.ContentDetails.UPCOMING.getTableName() + "/#", RECORDING_UPCOMING_ID );
		URI_MATCHER.addURI( AUTHORITY, RecordingRuleConstants.TABLE_NAME, RECORDING_RULE );
		URI_MATCHER.addURI( AUTHORITY, RecordingRuleConstants.TABLE_NAME + "/#", RECORDING_RULE_ID );
		URI_MATCHER.addURI( AUTHORITY, LiveStreamConstants.TABLE_NAME, LIVE_STREAM );
		URI_MATCHER.addURI( AUTHORITY, LiveStreamConstants.TABLE_NAME + "/#", LIVE_STREAM_ID );
		URI_MATCHER.addURI( AUTHORITY, ChannelConstants.TABLE_NAME, CHANNELS );
		URI_MATCHER.addURI( AUTHORITY, ChannelConstants.TABLE_NAME + "/#", CHANNEL_ID );
		URI_MATCHER.addURI( AUTHORITY, FrontendConstants.TABLE_NAME, FRONTENDS );
		URI_MATCHER.addURI( AUTHORITY, FrontendConstants.TABLE_NAME + "/#", FRONTEND_ID );
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
		Log.v( TAG, "onCreate : enter" );
		
		database = new DatabaseHelper( getContext() );

		Log.v( TAG, "onCreate : exit" );
		return ( null == database ? false : true );
	}
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType( Uri uri ) {
		Log.v( TAG, "getType : enter" );
		
		switch( URI_MATCHER.match( uri ) ) {
			case RECORDED:
				return RECORDED_CONTENT_TYPE;
			
			case RECORDED_ID:
				return RECORDED_CONTENT_ITEM_TYPE;
			
			case UPCOMING:
				return UPCOMING_CONTENT_TYPE;
			
			case UPCOMING_ID:
				return UPCOMING_CONTENT_ITEM_TYPE;
			
			case GUIDE:
				return GUIDE_CONTENT_TYPE;
			
			case GUIDE_ID:
				return GUIDE_CONTENT_ITEM_TYPE;
			
			case PROGRAM_GROUP:
				return PROGRAM_GROUP_CONTENT_TYPE;
			
			case PROGRAM_GROUP_ID:
				return PROGRAM_GROUP_CONTENT_ITEM_TYPE;
			
			case RECORDING_GUIDE:
				return RECORDING_GUIDE_CONTENT_TYPE;
			
			case RECORDING_GUIDE_ID:
				return RECORDING_GUIDE_CONTENT_ITEM_TYPE;
			
			case RECORDING_RECORDED:
				return RECORDING_RECORDED_CONTENT_TYPE;
			
			case RECORDING_RECORDED_ID:
				return RECORDING_RECORDED_CONTENT_ITEM_TYPE;
			
			case RECORDING_UPCOMING:
				return RECORDING_UPCOMING_CONTENT_TYPE;
			
			case RECORDING_UPCOMING_ID:
				return RECORDING_UPCOMING_CONTENT_ITEM_TYPE;
			
			case RECORDING_RULE:
				return RECORDING_RULE_CONTENT_TYPE;
			
			case RECORDING_RULE_ID:
				return RECORDING_RULE_CONTENT_ITEM_TYPE;
			
			case LIVE_STREAM:
				return LIVE_STREAM_CONTENT_TYPE;
			
			case LIVE_STREAM_ID:
				return LIVE_STREAM_CONTENT_ITEM_TYPE;
			
			case CHANNELS:
				return CHANNEL_CONTENT_TYPE;
			
			case CHANNEL_ID:
				return CHANNEL_CONTENT_ITEM_TYPE;
			
			case FRONTENDS:
				return FRONTEND_CONTENT_TYPE;
			
			case FRONTEND_ID:
				return FRONTEND_CONTENT_ITEM_TYPE;
			
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
		Log.v( TAG, "delete : enter" );
	
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

			case GUIDE:
				
				deleted = db.delete( ProgramConstants.TABLE_NAME_GUIDE, selection, selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
		
				return deleted;

			case GUIDE_ID:

				deleted = db.delete( ProgramConstants.TABLE_NAME_GUIDE, ProgramConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case PROGRAM_GROUP:
				
				deleted = db.delete( ProgramGroupConstants.TABLE_NAME, selection, selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
		
				return deleted;

			case PROGRAM_GROUP_ID:

				deleted = db.delete( ProgramGroupConstants.TABLE_NAME, ProgramGroupConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case RECORDING_GUIDE:

				deleted = db.delete( RecordingConstants.ContentDetails.GUIDE.getTableName(), selection, selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case RECORDING_GUIDE_ID:
				
				deleted = db.delete( RecordingConstants.ContentDetails.GUIDE.getTableName(), RecordingConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case RECORDING_RECORDED:

				deleted = db.delete( RecordingConstants.ContentDetails.RECORDED.getTableName(), selection, selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case RECORDING_RECORDED_ID:
				
				deleted = db.delete( RecordingConstants.ContentDetails.RECORDED.getTableName(), RecordingConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case RECORDING_UPCOMING:

				deleted = db.delete( RecordingConstants.ContentDetails.RECORDED.getTableName(), selection, selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case RECORDING_UPCOMING_ID:
				
				deleted = db.delete( RecordingConstants.ContentDetails.UPCOMING.getTableName(), RecordingConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case RECORDING_RULE:

				deleted = db.delete( RecordingRuleConstants.TABLE_NAME, selection, selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case RECORDING_RULE_ID:
				
				deleted = db.delete( RecordingRuleConstants.TABLE_NAME, RecordingRuleConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case LIVE_STREAM:

				deleted = db.delete( LiveStreamConstants.TABLE_NAME, selection, selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case LIVE_STREAM_ID:
				
				deleted = db.delete( LiveStreamConstants.TABLE_NAME, RecordingConstants._ID
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

			case FRONTENDS:

				deleted = db.delete( FrontendConstants.TABLE_NAME, selection, selectionArgs );
		
				getContext().getContentResolver().notifyChange( uri, null );
				
				return deleted;

			case FRONTEND_ID:

				deleted = db.delete( FrontendConstants.TABLE_NAME, FrontendConstants._ID
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
		Log.v( TAG, "insert : enter" );

		final SQLiteDatabase db = database.getWritableDatabase();
		
		Uri newUri = null;
		
		switch( URI_MATCHER.match( uri ) ) {
			case RECORDED:
//				System.out.println( "channelId=" + values.get( ProgramConstants.FIELD_CHANNEL_ID ) + ", startTime=" + DateUtils.dateTimeFormatterPretty.print( new DateTime( (Long) values.get( ProgramConstants.FIELD_START_TIME ) ) ) );
				
				newUri = ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_RECORDED, db.insertWithOnConflict( ProgramConstants.TABLE_NAME_RECORDED, null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case UPCOMING:
				newUri = ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_UPCOMING, db.insertWithOnConflict( ProgramConstants.TABLE_NAME_UPCOMING, null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case GUIDE:
				newUri = ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_GUIDE, db.insertWithOnConflict( ProgramConstants.TABLE_NAME_GUIDE, null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case PROGRAM_GROUP:
				newUri = ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, db.insertWithOnConflict( ProgramGroupConstants.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case RECORDING_GUIDE:
//				System.out.println( "recordId=" + values.get( RecordingConstants.FIELD_RECORD_ID ) + ", startTime=" + DateUtils.dateTimeFormatterPretty.print( new DateTime( (Long) values.get( RecordingConstants.FIELD_START_TS ) ) ) );

				newUri = ContentUris.withAppendedId( RecordingConstants.ContentDetails.GUIDE.getContentUri(), db.insertWithOnConflict( RecordingConstants.ContentDetails.GUIDE.getTableName(), null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case RECORDING_RECORDED:
//				System.out.println( "recordId=" + values.get( RecordingConstants.FIELD_RECORD_ID ) + ", startTime=" + DateUtils.dateTimeFormatterPretty.print( new DateTime( (Long) values.get( RecordingConstants.FIELD_START_TS ) ) ) );

				newUri = ContentUris.withAppendedId( RecordingConstants.ContentDetails.RECORDED.getContentUri(), db.insertWithOnConflict( RecordingConstants.ContentDetails.RECORDED.getTableName(), null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case RECORDING_UPCOMING:
//				System.out.println( "recordId=" + values.get( RecordingConstants.FIELD_RECORD_ID ) + ", startTime=" + DateUtils.dateTimeFormatterPretty.print( new DateTime( (Long) values.get( RecordingConstants.FIELD_START_TS ) ) ) );

				newUri = ContentUris.withAppendedId( RecordingConstants.ContentDetails.UPCOMING.getContentUri(), db.insertWithOnConflict( RecordingConstants.ContentDetails.UPCOMING.getTableName(), null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case RECORDING_RULE:

				newUri = ContentUris.withAppendedId( RecordingRuleConstants.CONTENT_URI, db.insertWithOnConflict( RecordingRuleConstants.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case LIVE_STREAM:

				newUri = ContentUris.withAppendedId( LiveStreamConstants.CONTENT_URI, db.insertWithOnConflict( LiveStreamConstants.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case CHANNELS:
				newUri = ContentUris.withAppendedId( ChannelConstants.CONTENT_URI, db.insertWithOnConflict( ChannelConstants.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case FRONTENDS:
				newUri = ContentUris.withAppendedId( FrontendConstants.CONTENT_URI, db.insertWithOnConflict( FrontendConstants.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case ETAGS:
				newUri = ContentUris.withAppendedId( EtagConstants.CONTENT_URI, db.insertWithOnConflict( EtagConstants.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case STATUS:
				newUri = ContentUris.withAppendedId( StatusConstants.CONTENT_URI, db.insertWithOnConflict( StatusConstants.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case LOCATION_PROFILE:
				newUri = ContentUris.withAppendedId( LocationProfileConstants.CONTENT_URI, db.insertWithOnConflict( LocationProfileConstants.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			case PLAYBACK_PROFILE:
				newUri = ContentUris.withAppendedId( PlaybackProfileConstants.CONTENT_URI, db.insertWithOnConflict( PlaybackProfileConstants.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				return newUri;
	
			default:
				throw new IllegalArgumentException( "Unknown URI " + uri );
		}
		
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	@Override
	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		//Log.v( TAG, "query : enter" );

		final SQLiteDatabase db = database.getReadableDatabase();
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		StringBuilder sb = new StringBuilder();

		Cursor cursor = null;
		
		switch( URI_MATCHER.match( uri ) ) {
			case RECORDED:
				
				sb.append( ProgramConstants.TABLE_NAME_RECORDED );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( ChannelConstants.TABLE_NAME );
				sb.append( " ON (" );
				sb.append( ChannelConstants.TABLE_NAME ).append( "." ).append( ChannelConstants.FIELD_CHAN_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_CHANNEL_ID );
				sb.append( " AND " );
				sb.append( ChannelConstants.TABLE_NAME ).append( "." ).append( ChannelConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( RecordingConstants.ContentDetails.RECORDED.getTableName() );
				sb.append( " ON (" );
				sb.append( RecordingConstants.ContentDetails.RECORDED.getTableName() ).append( "." ).append( RecordingConstants.FIELD_START_TIME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_START_TIME );
				sb.append( " AND " );
				sb.append( RecordingConstants.ContentDetails.RECORDED.getTableName() ).append( "." ).append( RecordingConstants.FIELD_RECORD_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_RECORD_ID );
				sb.append( " AND " );
				sb.append( RecordingConstants.ContentDetails.RECORDED.getTableName() ).append( "." ).append( RecordingConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( LiveStreamConstants.TABLE_NAME );
				sb.append( " ON (" );
				sb.append( LiveStreamConstants.TABLE_NAME ).append( "." ).append( LiveStreamConstants.FIELD_START_TIME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_START_TIME );
				sb.append( " AND " );
				sb.append( LiveStreamConstants.TABLE_NAME ).append( "." ).append( LiveStreamConstants.FIELD_CHAN_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_CHANNEL_ID );
				sb.append( " AND " );
				sb.append( LiveStreamConstants.TABLE_NAME ).append( "." ).append( LiveStreamConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mRecordedColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case RECORDED_ID:
				selection = ProgramConstants.TABLE_NAME_RECORDED + "." + appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				sb.append( ProgramConstants.TABLE_NAME_RECORDED );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( ChannelConstants.TABLE_NAME );
				sb.append( " ON (" );
				sb.append( ChannelConstants.TABLE_NAME ).append( "." ).append( ChannelConstants.FIELD_CHAN_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_CHANNEL_ID );
				sb.append( " AND " );
				sb.append( ChannelConstants.TABLE_NAME ).append( "." ).append( ChannelConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( RecordingConstants.ContentDetails.RECORDED.getTableName() );
				sb.append( " ON (" );
				sb.append( RecordingConstants.ContentDetails.RECORDED.getTableName() ).append( "." ).append( RecordingConstants.FIELD_START_TIME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_START_TIME );
				sb.append( " AND " );
				sb.append( RecordingConstants.ContentDetails.RECORDED.getTableName() ).append( "." ).append( RecordingConstants.FIELD_RECORD_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_RECORD_ID );
				sb.append( " AND " );
				sb.append( RecordingConstants.ContentDetails.RECORDED.getTableName() ).append( "." ).append( RecordingConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( LiveStreamConstants.TABLE_NAME );
				sb.append( " ON (" );
				sb.append( LiveStreamConstants.TABLE_NAME ).append( "." ).append( LiveStreamConstants.FIELD_START_TIME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_START_TIME );
				sb.append( " AND " );
				sb.append( LiveStreamConstants.TABLE_NAME ).append( "." ).append( LiveStreamConstants.FIELD_CHAN_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_CHANNEL_ID );
				sb.append( " AND " );
				sb.append( LiveStreamConstants.TABLE_NAME ).append( "." ).append( LiveStreamConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_RECORDED ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mRecordedColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );

				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case UPCOMING:
				
				sb.append( ProgramConstants.TABLE_NAME_UPCOMING );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( ChannelConstants.TABLE_NAME );
				sb.append( " ON (" );
				sb.append( ChannelConstants.TABLE_NAME ).append( "." ).append( ChannelConstants.FIELD_CHAN_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_UPCOMING ).append( "." ).append( ProgramConstants.FIELD_CHANNEL_ID );
				sb.append( " AND " );
				sb.append( ChannelConstants.TABLE_NAME ).append( "." ).append( ChannelConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_UPCOMING ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( RecordingConstants.ContentDetails.UPCOMING.getTableName() );
				sb.append( " ON (" );
				sb.append( RecordingConstants.ContentDetails.UPCOMING.getTableName() ).append( "." ).append( RecordingConstants.FIELD_START_TIME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_UPCOMING ).append( "." ).append( ProgramConstants.FIELD_START_TIME );
				sb.append( " AND " );
				sb.append( RecordingConstants.ContentDetails.UPCOMING.getTableName() ).append( "." ).append( RecordingConstants.FIELD_RECORD_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_UPCOMING ).append( "." ).append( ProgramConstants.FIELD_RECORD_ID );
				sb.append( " AND " );
				sb.append( RecordingConstants.ContentDetails.UPCOMING.getTableName() ).append( "." ).append( RecordingConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_UPCOMING ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mUpcomingColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
//				if( null != selectionArgs ) {
//					for( String arg : selectionArgs ) {
//						System.out.println( arg );
//					}
//				}
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );

				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case UPCOMING_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				sb.append( ProgramConstants.TABLE_NAME_UPCOMING );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( ChannelConstants.TABLE_NAME );
				sb.append( " ON (" );
				sb.append( ChannelConstants.TABLE_NAME ).append( "." ).append( ChannelConstants.FIELD_CHAN_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_UPCOMING ).append( "." ).append( ProgramConstants.FIELD_CHANNEL_ID );
				sb.append( " AND " );
				sb.append( ChannelConstants.TABLE_NAME ).append( "." ).append( ChannelConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_UPCOMING ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( RecordingConstants.ContentDetails.UPCOMING.getTableName() );
				sb.append( " ON (" );
				sb.append( RecordingConstants.ContentDetails.UPCOMING.getTableName() ).append( "." ).append( RecordingConstants.FIELD_START_TIME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_UPCOMING ).append( "." ).append( ProgramConstants.FIELD_START_TIME );
				sb.append( " AND " );
				sb.append( RecordingConstants.ContentDetails.UPCOMING.getTableName() ).append( "." ).append( RecordingConstants.FIELD_RECORD_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_UPCOMING ).append( "." ).append( ProgramConstants.FIELD_RECORD_ID );
				sb.append( " AND " );
				sb.append( RecordingConstants.ContentDetails.UPCOMING.getTableName() ).append( "." ).append( RecordingConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_UPCOMING ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mUpcomingColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );

				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case GUIDE:

				sb.append( ProgramConstants.TABLE_NAME_GUIDE );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( ChannelConstants.TABLE_NAME );
				sb.append( " ON (" );
				sb.append( ChannelConstants.TABLE_NAME ).append( "." ).append( ChannelConstants.FIELD_CHAN_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_GUIDE ).append( "." ).append( ProgramConstants.FIELD_CHANNEL_ID );
				sb.append( " AND " );
				sb.append( ChannelConstants.TABLE_NAME ).append( "." ).append( ChannelConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_GUIDE ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( RecordingConstants.ContentDetails.GUIDE.getTableName() );
				sb.append( " ON (" );
				sb.append( RecordingConstants.ContentDetails.GUIDE.getTableName() ).append( "." ).append( RecordingConstants.FIELD_START_TIME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_GUIDE ).append( "." ).append( ProgramConstants.FIELD_START_TIME );
				sb.append( " AND " );
				sb.append( RecordingConstants.ContentDetails.GUIDE.getTableName() ).append( "." ).append( RecordingConstants.FIELD_RECORD_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_GUIDE ).append( "." ).append( ProgramConstants.FIELD_RECORD_ID );
				sb.append( " AND " );
				sb.append( RecordingConstants.ContentDetails.GUIDE.getTableName() ).append( "." ).append( RecordingConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_GUIDE ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mGuideColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );

				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case GUIDE_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				sb.append( ProgramConstants.TABLE_NAME_GUIDE );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( ChannelConstants.TABLE_NAME );
				sb.append( " ON (" );
				sb.append( ChannelConstants.TABLE_NAME ).append( "." ).append( ChannelConstants.FIELD_CHAN_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_GUIDE ).append( "." ).append( ProgramConstants.FIELD_CHANNEL_ID );
				sb.append( " AND " );
				sb.append( ChannelConstants.TABLE_NAME ).append( "." ).append( ChannelConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_GUIDE ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				sb.append( " LEFT OUTER JOIN " );
				sb.append( RecordingConstants.ContentDetails.GUIDE.getTableName() );
				sb.append( " ON (" );
				sb.append( RecordingConstants.ContentDetails.GUIDE.getTableName() ).append( "." ).append( RecordingConstants.FIELD_START_TIME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_GUIDE ).append( "." ).append( ProgramConstants.FIELD_START_TIME );
				sb.append( " AND " );
				sb.append( RecordingConstants.ContentDetails.GUIDE.getTableName() ).append( "." ).append( RecordingConstants.FIELD_RECORD_ID );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_GUIDE ).append( "." ).append( ProgramConstants.FIELD_RECORD_ID );
				sb.append( " AND " );
				sb.append( RecordingConstants.ContentDetails.GUIDE.getTableName() ).append( "." ).append( RecordingConstants.FIELD_MASTER_HOSTNAME );
				sb.append( " = ");
				sb.append( ProgramConstants.TABLE_NAME_GUIDE ).append( "." ).append( ProgramConstants.FIELD_MASTER_HOSTNAME );
				sb.append( ")" );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mGuideColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );

				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case PROGRAM_GROUP:
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );

				cursor = db.query( ProgramGroupConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case PROGRAM_GROUP_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				cursor = db.query( ProgramGroupConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case RECORDING_GUIDE:
				
				sb.append( RecordingConstants.ContentDetails.GUIDE.getTableName() );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mRecordingGuideColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case RECORDING_GUIDE_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				sb.append( RecordingConstants.ContentDetails.GUIDE.getTableName() );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mRecordingGuideColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case RECORDING_RECORDED:
				
				sb.append( RecordingConstants.ContentDetails.RECORDED.getTableName() );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mRecordingRecordedColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case RECORDING_RECORDED_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				sb.append( RecordingConstants.ContentDetails.RECORDED.getTableName() );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mRecordingRecordedColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case RECORDING_UPCOMING:
				
				sb.append( RecordingConstants.ContentDetails.UPCOMING.getTableName() );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mRecordingUpcomingColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case RECORDING_UPCOMING_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				sb.append( RecordingConstants.ContentDetails.UPCOMING.getTableName() );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mRecordingUpcomingColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case RECORDING_RULE:
				
				sb.append( RecordingRuleConstants.TABLE_NAME );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mRecordingRuleColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
				
			case RECORDING_RULE_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				sb.append( RecordingRuleConstants.TABLE_NAME );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mRecordingRuleColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case LIVE_STREAM:
				
				sb.append( LiveStreamConstants.TABLE_NAME );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mLiveStreamColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case LIVE_STREAM_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				sb.append( LiveStreamConstants.TABLE_NAME );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mLiveStreamColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case CHANNELS:
				
				sb.append( ChannelConstants.TABLE_NAME );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mChannelColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case CHANNEL_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				sb.append( ChannelConstants.TABLE_NAME );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mChannelColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case FRONTENDS:
				
				sb.append( FrontendConstants.TABLE_NAME );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mFrontendColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				return cursor;
	
			case FRONTEND_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				sb.append( FrontendConstants.TABLE_NAME );
				
				queryBuilder.setTables( sb.toString() );
				queryBuilder.setProjectionMap( mFrontendColumnMap );
				
//				System.out.println( queryBuilder.buildQuery( null, selection, null, null, sortOrder, null ) );
				
				cursor = queryBuilder.query( db, null, selection, selectionArgs, null, null, sortOrder );
				
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
		//Log.v( TAG, "update : enter" );

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

			case GUIDE:
				affected = db.update( ProgramConstants.TABLE_NAME_GUIDE, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case GUIDE_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( ProgramConstants.TABLE_NAME_GUIDE, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case PROGRAM_GROUP:
				affected = db.update( ProgramGroupConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case PROGRAM_GROUP_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( ProgramGroupConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case RECORDING_GUIDE:
				affected = db.update( RecordingConstants.ContentDetails.GUIDE.getTableName(), values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case RECORDING_GUIDE_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( RecordingConstants.ContentDetails.GUIDE.getTableName(), values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case RECORDING_RECORDED:
				affected = db.update( RecordingConstants.ContentDetails.RECORDED.getTableName(), values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case RECORDING_RECORDED_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( RecordingConstants.ContentDetails.RECORDED.getTableName(), values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case RECORDING_UPCOMING:
				affected = db.update( RecordingConstants.ContentDetails.UPCOMING.getTableName(), values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case RECORDING_UPCOMING_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( RecordingConstants.ContentDetails.UPCOMING.getTableName(), values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case RECORDING_RULE:
				affected = db.update( RecordingRuleConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case RECORDING_RULE_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( RecordingRuleConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case LIVE_STREAM:
				affected = db.update( LiveStreamConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case LIVE_STREAM_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( LiveStreamConstants.TABLE_NAME, values, selection , selectionArgs );
				
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

			case FRONTENDS:
				affected = db.update( FrontendConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				return affected;

			case FRONTEND_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( FrontendConstants.TABLE_NAME, values, selection , selectionArgs );

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
			
			case GUIDE:
				Log.v( TAG, "bulkInsert : inserting programs" );
			
				db.beginTransaction();
				try {
					//standard SQL insert statement, that can be reused
					SQLiteStatement insert = db.compileStatement( ProgramConstants.INSERT_GUIDE_ROW );
					bulkInsertPrograms( insert, values );
					
					db.setTransactionSuccessful();
	            
					numInserted = values.length;
				} finally {
					db.endTransaction();
				}

				getContext().getContentResolver().notifyChange( uri, null );

				return numInserted;
			
			case PROGRAM_GROUP:
				
				db.beginTransaction();
				try {
					//standard SQL insert statement, that can be reused
					SQLiteStatement insert = db.compileStatement( ProgramGroupConstants.INSERT_ROW );
				
					for( ContentValues value : values ) {
						insert.bindString( 1, value.getAsString( ProgramGroupConstants.FIELD_PROGRAM_GROUP ) );
						insert.bindString( 2, value.getAsString( ProgramGroupConstants.FIELD_TITLE ) );
						insert.bindString( 3, value.getAsString( ProgramGroupConstants.FIELD_CATEGORY ) );
						insert.bindString( 4, value.getAsString( ProgramGroupConstants.FIELD_INETREF ) );
						insert.bindString( 5, value.getAsString( ProgramGroupConstants.FIELD_MASTER_HOSTNAME ) );
						insert.bindLong( 6, value.getAsInteger( ProgramGroupConstants.FIELD_LAST_MODIFIED_DATE ) );
						insert.execute();
					}
					db.setTransactionSuccessful();
	            
					numInserted = values.length;
				} finally {
					db.endTransaction();
				}
			
				getContext().getContentResolver().notifyChange( uri, null );

				return numInserted;

			case RECORDING_GUIDE:
				
				db.beginTransaction();
				try {
					//standard SQL insert statement, that can be reused
					SQLiteStatement insert = db.compileStatement( RecordingConstants.INSERT_RECORDING_GUIDE_ROW );
					bulkInsertRecordings( insert, values );
					
					db.setTransactionSuccessful();
	            
					numInserted = values.length;
				} finally {
					db.endTransaction();
				}
			
				getContext().getContentResolver().notifyChange( uri, null );

				return numInserted;

			case RECORDING_RECORDED:
				
				db.beginTransaction();
				try {
					//standard SQL insert statement, that can be reused
					SQLiteStatement insert = db.compileStatement( RecordingConstants.INSERT_RECORDING_RECORDED_ROW );
					bulkInsertRecordings( insert, values );
					
					db.setTransactionSuccessful();
	            
					numInserted = values.length;
				} finally {
					db.endTransaction();
				}
			
				getContext().getContentResolver().notifyChange( uri, null );

				return numInserted;

			case RECORDING_UPCOMING:
				
				db.beginTransaction();
				try {
					//standard SQL insert statement, that can be reused
					SQLiteStatement insert = db.compileStatement( RecordingConstants.INSERT_RECORDING_UPCOMING_ROW );
					bulkInsertRecordings( insert, values );
					
					db.setTransactionSuccessful();
	            
					numInserted = values.length;
				} finally {
					db.endTransaction();
				}
			
				getContext().getContentResolver().notifyChange( uri, null );

				return numInserted;

			case RECORDING_RULE:
				
				db.beginTransaction();
				try {
					//standard SQL insert statement, that can be reused
					SQLiteStatement insert = db.compileStatement( RecordingRuleConstants.INSERT_ROW );
					bulkInsertRecordings( insert, values );
					
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
						insert.bindString( 27, value.getAsString( ChannelConstants.FIELD_MASTER_HOSTNAME ) );
						insert.bindLong( 28, value.getAsInteger( ChannelConstants.FIELD_LAST_MODIFIED_DATE ) );
						insert.execute();
					}
					db.setTransactionSuccessful();
	            
					numInserted = values.length;
				} finally {
					db.endTransaction();
				}
			
				getContext().getContentResolver().notifyChange( uri, null );

				return numInserted;

			case FRONTENDS:
				
				db.beginTransaction();
				try {
					//standard SQL insert statement, that can be reused
					SQLiteStatement insert = db.compileStatement( FrontendConstants.INSERT_ROW );
				
					for( ContentValues value : values ) {
						insert.bindString( 1, value.getAsString( FrontendConstants.FIELD_NAME ) );
						insert.bindString( 2, value.getAsString( FrontendConstants.FIELD_URL ) );
						insert.bindLong( 3, value.getAsInteger( FrontendConstants.FIELD_AVAILABLE ) );
						insert.bindString( 27, value.getAsString( FrontendConstants.FIELD_MASTER_HOSTNAME ) );
						insert.bindLong( 28, value.getAsInteger( FrontendConstants.FIELD_LAST_MODIFIED_DATE ) );
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
		//Log.v( TAG, "applyBatch : enter" );

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
			insert.bindLong( 1, value.getAsLong( ProgramConstants.FIELD_START_TIME ) );
			insert.bindLong( 2, value.getAsLong( ProgramConstants.FIELD_END_TIME ) );
			insert.bindString( 3, value.getAsString( ProgramConstants.FIELD_TITLE ) );
			insert.bindString( 4, value.getAsString( ProgramConstants.FIELD_SUB_TITLE ) );
			insert.bindString( 5, value.getAsString( ProgramConstants.FIELD_CATEGORY ) );
			insert.bindString( 6, value.getAsString( ProgramConstants.FIELD_CATEGORY_TYPE ) );
			insert.bindLong( 7, value.getAsInteger( ProgramConstants.FIELD_REPEAT ) );
			insert.bindLong( 8, value.getAsInteger( ProgramConstants.FIELD_VIDEO_PROPS ) );
			insert.bindLong( 9, value.getAsInteger( ProgramConstants.FIELD_AUDIO_PROPS ) );
			insert.bindLong( 10, value.getAsInteger( ProgramConstants.FIELD_SUB_PROPS ) );
			insert.bindString( 11, value.getAsString( ProgramConstants.FIELD_SERIES_ID ) );
			insert.bindString( 12, value.getAsString( ProgramConstants.FIELD_PROGRAM_ID ) );
			insert.bindDouble( 13, value.getAsFloat( ProgramConstants.FIELD_STARS ) );
			insert.bindString( 14, value.getAsString( ProgramConstants.FIELD_FILE_SIZE ) );
			insert.bindString( 15, value.getAsString( ProgramConstants.FIELD_LAST_MODIFIED ) );
			insert.bindString( 16, value.getAsString( ProgramConstants.FIELD_PROGRAM_FLAGS ) );
			insert.bindString( 17, value.getAsString( ProgramConstants.FIELD_HOSTNAME ) );
			insert.bindString( 18, value.getAsString( ProgramConstants.FIELD_FILENAME ) );
			insert.bindString( 19, value.getAsString( ProgramConstants.FIELD_AIR_DATE ) );
			insert.bindString( 20, value.getAsString( ProgramConstants.FIELD_DESCRIPTION ) );
			insert.bindString( 21, value.getAsString( ProgramConstants.FIELD_INETREF ) );
			insert.bindString( 22, value.getAsString( ProgramConstants.FIELD_SEASON ) );
			insert.bindString( 23, value.getAsString( ProgramConstants.FIELD_EPISODE ) );
			insert.bindLong( 24, value.getAsInteger( ProgramConstants.FIELD_CHANNEL_ID ) );
			insert.bindLong( 25, value.getAsInteger( ProgramConstants.FIELD_RECORD_ID ) );
			insert.bindLong( 26, value.getAsInteger( ProgramConstants.FIELD_IN_ERROR ) );
			insert.bindString( 27, value.getAsString( ProgramConstants.FIELD_MASTER_HOSTNAME ) );
			insert.bindLong( 28, value.getAsInteger( ProgramConstants.FIELD_LAST_MODIFIED_DATE ) );
			
			insert.execute();
		}

	}
	
	private void bulkInsertRecordings( SQLiteStatement insert, ContentValues[] values ) {
		
		for( ContentValues value : values ) {
			insert.bindLong( 1, value.getAsLong( RecordingConstants._ID ) );
			insert.bindLong( 2, value.getAsInteger( RecordingConstants.FIELD_STATUS ) );
			insert.bindLong( 3, value.getAsInteger( RecordingConstants.FIELD_PRIORITY ) );
			insert.bindLong( 4, value.getAsInteger( RecordingConstants.FIELD_START_TS ) );
			insert.bindLong( 5, value.getAsInteger( RecordingConstants.FIELD_END_TS ) );
			insert.bindString( 6, value.getAsString( RecordingConstants.FIELD_REC_GROUP ) );
			insert.bindString( 7, value.getAsString( RecordingConstants.FIELD_PLAY_GROUP ) );
			insert.bindString( 8, value.getAsString( RecordingConstants.FIELD_STORAGE_GROUP ) );
			insert.bindLong( 9, value.getAsInteger( RecordingConstants.FIELD_REC_TYPE ) );
			insert.bindLong( 10, value.getAsInteger( RecordingConstants.FIELD_DUP_IN_TYPE ) );
			insert.bindLong( 11, value.getAsInteger( RecordingConstants.FIELD_DUP_METHOD ) );
			insert.bindLong( 12, value.getAsInteger( RecordingConstants.FIELD_ENCODER_ID ) );
			insert.bindString( 13, value.getAsString( RecordingConstants.FIELD_PROFILE ) );
			insert.bindString( 14, value.getAsString( RecordingConstants.FIELD_START_TIME ) );
			insert.bindString( 15, value.getAsString( RecordingConstants.FIELD_MASTER_HOSTNAME ) );
			insert.bindLong( 16, value.getAsInteger( RecordingConstants.FIELD_LAST_MODIFIED_DATE ) );

			insert.execute();
		}

	}
	
	private static final Map<String, String> mRecordedColumnMap = buildRecordedColumnMap();
	private static Map<String, String> buildRecordedColumnMap() {
		
		Map<String, String> columnMap = new HashMap<String, String>();

		String programProjection[] = ProgramConstants.COLUMN_MAP;
		for( String col : programProjection ) {

			String qualifiedCol = ProgramConstants.TABLE_NAME_RECORDED + "." + col;
			columnMap.put( qualifiedCol, qualifiedCol );
		}
		
		columnMap = buildRecordingRecordedColumnMap( columnMap );
		columnMap = buildProgramColumnMap( columnMap );
		columnMap = buildLiveStreamColumnMap( columnMap );

		return columnMap;
	}

	private static final Map<String, String> mUpcomingColumnMap = buildUpcomingColumnMap();
	private static Map<String, String> buildUpcomingColumnMap() {
		
		Map<String, String> columnMap = new HashMap<String, String>();

		String programProjection[] = ProgramConstants.COLUMN_MAP;
		for( String col : programProjection ) {

			String qualifiedCol = ProgramConstants.TABLE_NAME_UPCOMING + "." + col;
			columnMap.put( qualifiedCol, qualifiedCol );
		}
		
		columnMap = buildRecordingUpcomingColumnMap( columnMap );
		columnMap = buildProgramColumnMap( columnMap );
		
		return columnMap;
	}

	private static final Map<String, String> mGuideColumnMap = buildGuideColumnMap();
	private static Map<String, String> buildGuideColumnMap() {
		
		Map<String, String> columnMap = new HashMap<String, String>();

		String programProjection[] = ProgramConstants.COLUMN_MAP;
		for( String col : programProjection ) {

			String qualifiedCol = ProgramConstants.TABLE_NAME_GUIDE + "." + col;
			columnMap.put( qualifiedCol, qualifiedCol );
		}
		
		columnMap = buildRecordingGuideColumnMap( columnMap );

		String channelProjection[] = ChannelConstants.COLUMN_MAP;
		for( String col : channelProjection ) {

			String qualifiedCol = ChannelConstants.TABLE_NAME + "." + col;
			columnMap.put( qualifiedCol, qualifiedCol + " as " + ChannelConstants.TABLE_NAME + "_" + col );
		}
		
		return columnMap;
	}

	private static Map<String, String> buildProgramColumnMap( Map<String, String> columnMap ) {
		if( null == columnMap ) {
			columnMap = new HashMap<String, String>();
		}
		
		columnMap = buildChannelColumnMap( columnMap );
		
		return columnMap;
	}
	
	private static final Map<String, String> mLiveStreamColumnMap = buildLiveStreamColumnMap( new HashMap<String, String>() );
	private static Map<String, String> buildLiveStreamColumnMap( Map<String, String> columnMap ) {
		if( null == columnMap ) {
			columnMap = new HashMap<String, String>();
		}
		
		String channelProjection[] = LiveStreamConstants.COLUMN_MAP;
		for( String col : channelProjection ) {

			String qualifiedCol = LiveStreamConstants.TABLE_NAME + "." + col;
			columnMap.put( qualifiedCol, qualifiedCol + " as " + LiveStreamConstants.TABLE_NAME + "_" + col );
		}
		
		return columnMap;
	}

	private static final Map<String, String> mChannelColumnMap = buildChannelColumnMap( new HashMap<String, String>() );
	private static Map<String, String> buildChannelColumnMap( Map<String, String> columnMap ) {
		if( null == columnMap ) {
			columnMap = new HashMap<String, String>();
		}
		
		String channelProjection[] = ChannelConstants.COLUMN_MAP;
		for( String col : channelProjection ) {

			String qualifiedCol = ChannelConstants.TABLE_NAME + "." + col;
			columnMap.put( qualifiedCol, qualifiedCol + " as " + ChannelConstants.TABLE_NAME + "_" + col );
		}
		
		return columnMap;
	}

	private static final Map<String, String> mFrontendColumnMap = buildFrontendColumnMap( new HashMap<String, String>() );
	private static Map<String, String> buildFrontendColumnMap( Map<String, String> columnMap ) {
		if( null == columnMap ) {
			columnMap = new HashMap<String, String>();
		}
		
		String frontendProjection[] = FrontendConstants.COLUMN_MAP;
		for( String col : frontendProjection ) {

			String qualifiedCol = FrontendConstants.TABLE_NAME + "." + col;
			columnMap.put( qualifiedCol, qualifiedCol + " as " + FrontendConstants.TABLE_NAME + "_" + col );
		}
		
		return columnMap;
	}

	private static final Map<String, String> mRecordingGuideColumnMap = buildRecordingGuideColumnMap( new HashMap<String, String>() );
	private static Map<String, String> buildRecordingGuideColumnMap( Map<String, String> columnMap ) {
		if( null == columnMap ) {
			columnMap = new HashMap<String, String>();
		}
		
		columnMap = buildRecordingColumnMap( columnMap, RecordingConstants.ContentDetails.GUIDE.getTableName() );
		
		return columnMap;
	}
	
	private static final Map<String, String> mRecordingRecordedColumnMap = buildRecordingRecordedColumnMap( new HashMap<String, String>() );
	private static Map<String, String> buildRecordingRecordedColumnMap( Map<String, String> columnMap ) {
		if( null == columnMap ) {
			columnMap = new HashMap<String, String>();
		}
		
		columnMap = buildRecordingColumnMap( columnMap, RecordingConstants.ContentDetails.RECORDED.getTableName() );
		
		return columnMap;
	}
	
	private static final Map<String, String> mRecordingUpcomingColumnMap = buildRecordingUpcomingColumnMap( new HashMap<String, String>() );
	private static Map<String, String> buildRecordingUpcomingColumnMap( Map<String, String> columnMap ) {
		if( null == columnMap ) {
			columnMap = new HashMap<String, String>();
		}
		
		columnMap = buildRecordingColumnMap( columnMap, RecordingConstants.ContentDetails.UPCOMING.getTableName() );
		
		return columnMap;
	}
	
	private static Map<String, String> buildRecordingColumnMap( Map<String, String> columnMap, String tableName ) {
		if( null == columnMap ) {
			columnMap = new HashMap<String, String>();
		}
		
		String recordingProjection[] = RecordingConstants.COLUMN_MAP;
		for( String col : recordingProjection ) {

			String qualifiedCol = tableName + "." + col;
			columnMap.put( qualifiedCol, qualifiedCol + " as " + tableName + "_" + col );
		}
		
		return columnMap;
	}

	private static final Map<String, String> mRecordingRuleColumnMap = buildRecordingRuleColumnMap( new HashMap<String, String>() );
	private static Map<String, String> buildRecordingRuleColumnMap( Map<String, String> columnMap ) {
		if( null == columnMap ) {
			columnMap = new HashMap<String, String>();
		}
		
		String recordingRuleProjection[] = RecordingRuleConstants.COLUMN_MAP;
		for( String col : recordingRuleProjection ) {

			if( !"_id".equals( col ) ) {
				String qualifiedCol = RecordingRuleConstants.TABLE_NAME + "." + col;
				columnMap.put( qualifiedCol, qualifiedCol + " as " + RecordingRuleConstants.TABLE_NAME + "_" + col );
			} else {
				String qualifiedCol = RecordingRuleConstants.TABLE_NAME + "." + col;
				columnMap.put( qualifiedCol, qualifiedCol + " as " + col );
			}
		}
		
		return columnMap;
	}
	
}
