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
package org.mythtv.db;

import org.joda.time.DateTime;
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
import org.mythtv.db.status.StatusConstants.StatusKey;
import org.mythtv.service.util.DateUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Daniel Frey
 * @author John Baab
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = DatabaseHelper.class.getSimpleName();
	
	private static final String DATABASE_NAME = "mythtvdb";
	private static final int DATABASE_VERSION = 118;

	public DatabaseHelper( Context context ) {
		super( context, DATABASE_NAME, null, DATABASE_VERSION );
		Log.v( TAG, "initialize : enter" );

		Log.v( TAG, "initialize : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onOpen(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onOpen( SQLiteDatabase db ) {
		Log.v( TAG, "onOpen : enter" );
		super.onOpen( db );

		if( !db.isReadOnly() ) {
			Log.i( TAG, "onOpen : turning on referencial integrity" );

			db.execSQL( "PRAGMA foreign_keys = ON;" );
		}
		
		Log.v( TAG, "onOpen : exit" );
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate( SQLiteDatabase db ) {
		Log.v( TAG, "onCreate : enter" );
		
		dropLocationProfiles( db );
		createLocationProfiles( db );
		
		dropPlaybackProfiles( db );
		createPlaybackProfiles( db );
		
		dropStatus( db );
		createStatus( db );

		dropEtag( db );
		createEtag( db );
		
		dropChannel( db );
		createChannel( db );
		
		dropFrontend( db );
		createFrontend( db );
		
		dropProgram( db, ProgramConstants.TABLE_NAME_RECORDED );
		createProgram( db, ProgramConstants.TABLE_NAME_RECORDED );
		
		dropProgram( db, ProgramConstants.TABLE_NAME_UPCOMING );
		createProgram( db, ProgramConstants.TABLE_NAME_UPCOMING );

		dropProgram( db, ProgramConstants.TABLE_NAME_GUIDE );
		createProgram( db, ProgramConstants.TABLE_NAME_GUIDE );

		dropProgramGroup( db );
		createProgramGroup( db );
		
		dropRecording( db, RecordingConstants.ContentDetails.GUIDE.getTableName() );
		createRecording( db, RecordingConstants.ContentDetails.GUIDE.getTableName() );
		
		dropRecording( db, RecordingConstants.ContentDetails.RECORDED.getTableName() );
		createRecording( db, RecordingConstants.ContentDetails.RECORDED.getTableName() );

		dropRecording( db, RecordingConstants.ContentDetails.UPCOMING.getTableName() );
		createRecording( db, RecordingConstants.ContentDetails.UPCOMING.getTableName() );

		dropLiveStream( db );
		createLiveStream( db );
		
		dropRecordingRule( db );
		createRecordingRule( db );
		
		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		Log.v( TAG, "onUpgrade : enter" );

		if( oldVersion < 118 ) {
			Log.v( TAG, "onUpgrade : upgrading to db version 118" );

			onCreate( db );

		}

		Log.v( TAG, "onUpgrade : exit" );
	}

	// internal helpers
	
	private void dropStatus( SQLiteDatabase db ) {
		Log.v( TAG, "dropStatus : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + StatusConstants.TABLE_NAME );
		
		Log.v( TAG, "dropStatus : exit" );
	}

	private void createStatus( SQLiteDatabase db ) {
		Log.v( TAG, "createStatus : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + StatusConstants.TABLE_NAME + " (" );
		sqlBuilder.append( StatusConstants._ID ).append( " " ).append( EtagConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( StatusConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( StatusConstants.FIELD_KEY ).append( " " ).append( StatusConstants.FIELD_KEY_DATA_TYPE ).append( ", " );
		sqlBuilder.append( StatusConstants.FIELD_VALUE ).append( " " ).append( StatusConstants.FIELD_VALUE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( StatusConstants.FIELD_DATE ).append( " " ).append( StatusConstants.FIELD_DATE_DATA_TYPE );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createStatus : sql=" + sql );
		}
		db.execSQL( sql );

		ContentValues values = new ContentValues();
		values.put( StatusConstants.FIELD_KEY, StatusKey.MASTER_BACKEND_CONNECTED.name() );
		values.put( StatusConstants.FIELD_VALUE, "FALSE" );
		values.put( StatusConstants.FIELD_DATE, DateUtils.convertUtc( new DateTime() ).getMillis() );
		db.insert( StatusConstants.TABLE_NAME, null, values );

		Log.v( TAG, "createStatus : exit" );
	}
	
	private void dropEtag( SQLiteDatabase db ) {
		Log.v( TAG, "dropEtag : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + EtagConstants.TABLE_NAME );
		
		Log.v( TAG, "dropEtag : exit" );
	}

	private void createEtag( SQLiteDatabase db ) {
		Log.v( TAG, "createEtag : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + EtagConstants.TABLE_NAME + " (" );
		sqlBuilder.append( EtagConstants._ID ).append( " " ).append( EtagConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( EtagConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( EtagConstants.FIELD_ENDPOINT ).append( " " ).append( EtagConstants.FIELD_ENDPOINT_DATA_TYPE ).append( ", " );
		sqlBuilder.append( EtagConstants.FIELD_VALUE ).append( " " ).append( EtagConstants.FIELD_VALUE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( EtagConstants.FIELD_DATA_ID ).append( " " ).append( EtagConstants.FIELD_DATA_ID_DATA_TYPE ).append( " default" ).append( EtagConstants.FIELD_DATA_ID_DEFAULT ).append( ", " );
		sqlBuilder.append( EtagConstants.FIELD_DATE ).append( " " ).append( EtagConstants.FIELD_DATE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( EtagConstants.FIELD_MASTER_HOSTNAME ).append( " " ).append( EtagConstants.FIELD_MASTER_HOSTNAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( EtagConstants.FIELD_LAST_MODIFIED_DATE ).append( " " ).append( EtagConstants.FIELD_LAST_MODIFIED_DATE_DATA_TYPE );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createEtag : sql=" + sql );
		}
		db.execSQL( sql );

		Log.v( TAG, "createEtag : exit" );
	}
	
	private void createLocationProfiles( SQLiteDatabase db ) {
		Log.v( TAG, "createLocationProfiles : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + LocationProfileConstants.TABLE_NAME + " (" );
		sqlBuilder.append( LocationProfileConstants._ID ).append( " " ).append( LocationProfileConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( LocationProfileConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( LocationProfileConstants.FIELD_TYPE ).append( " " ).append( LocationProfileConstants.FIELD_TYPE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LocationProfileConstants.FIELD_NAME ).append( " " ).append( LocationProfileConstants.FIELD_NAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LocationProfileConstants.FIELD_URL ).append( " " ).append( LocationProfileConstants.FIELD_URL_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LocationProfileConstants.FIELD_SELECTED ).append( " " ).append( LocationProfileConstants.FIELD_SELECTED_DATA_TYPE ).append( " default " ).append( LocationProfileConstants.FIELD_SELECTED_DEFAULT ).append( ", " );
		sqlBuilder.append( LocationProfileConstants.FIELD_CONNECTED ).append( " " ).append( LocationProfileConstants.FIELD_CONNECTED_DATA_TYPE ).append( " default " ).append( LocationProfileConstants.FIELD_CONNECTED_DEFAULT ).append( ", " );
		sqlBuilder.append( LocationProfileConstants.FIELD_VERSION ).append( " " ).append( LocationProfileConstants.FIELD_VERSION_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LocationProfileConstants.FIELD_PROTOCOL_VERSION ).append( " " ).append( LocationProfileConstants.FIELD_PROTOCOL_VERSION_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LocationProfileConstants.FIELD_WOL_ADDRESS ).append( " " ).append( LocationProfileConstants.FIELD_WOL_ADDRESS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LocationProfileConstants.FIELD_HOSTNAME ).append( " " ).append( LocationProfileConstants.FIELD_HOSTNAME_DATA_TYPE );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createLocationProfiles : sql=" + sql );
		}
		db.execSQL( sql );

//		ContentValues values = new ContentValues();
//
//		values.put( LocationProfileConstants.FIELD_TYPE, "HOME" );
//		values.put( LocationProfileConstants.FIELD_NAME, "Home" );
//		values.put( LocationProfileConstants.FIELD_URL, "http://192.168.10.200:6544/" );
//		values.put( LocationProfileConstants.FIELD_SELECTED, 1 );
//		values.put( LocationProfileConstants.FIELD_VERSION, "0.26" );
//		values.put( LocationProfileConstants.FIELD_PROTOCOL_VERSION, "75" );
//		values.put( LocationProfileConstants.FIELD_WOL_ADDRESS, "50:e5:49:d9:02:db" );
//		values.put( LocationProfileConstants.FIELD_HOSTNAME, "mythcenter" );
//		db.insert( LocationProfileConstants.TABLE_NAME, null, values );
//
//		values = new ContentValues();
//		values.put( LocationProfileConstants.FIELD_TYPE, "AWAY" );
//		values.put( LocationProfileConstants.FIELD_NAME, "Emulator" );
//		values.put( LocationProfileConstants.FIELD_URL, "http://10.0.2.2:6544/" );
//		values.put( LocationProfileConstants.FIELD_SELECTED, 1 );
//		values.put( LocationProfileConstants.FIELD_VERSION, "0.26" );
//		values.put( LocationProfileConstants.FIELD_PROTOCOL_VERSION, "75" );
//		values.put( LocationProfileConstants.FIELD_WOL_ADDRESS, "" );
//		values.put( LocationProfileConstants.FIELD_HOSTNAME, "mythcenter" );
//		db.insert( LocationProfileConstants.TABLE_NAME, null, values );
//		
//		values = new ContentValues();
//		values.put( LocationProfileConstants.FIELD_TYPE, "AWAY" );
//		values.put( LocationProfileConstants.FIELD_NAME, "Home" );
//		values.put( LocationProfileConstants.FIELD_URL, "http://192.168.10.200:6544/" );
//		values.put( LocationProfileConstants.FIELD_SELECTED, 0 );
//		values.put( LocationProfileConstants.FIELD_VERSION, "0.26" );
//		values.put( LocationProfileConstants.FIELD_PROTOCOL_VERSION, "75" );
//		values.put( LocationProfileConstants.FIELD_WOL_ADDRESS, "" );
//		values.put( LocationProfileConstants.FIELD_HOSTNAME, "mythcenter" );
//		db.insert( LocationProfileConstants.TABLE_NAME, null, values );
//		
//		values = new ContentValues();
//		values.put( LocationProfileConstants.FIELD_TYPE, "AWAY" );
//		values.put( LocationProfileConstants.FIELD_NAME, "Tunnel" );
//		values.put( LocationProfileConstants.FIELD_URL, "http://localhost:6544/" );
//		values.put( LocationProfileConstants.FIELD_SELECTED, 0 );
//		values.put( LocationProfileConstants.FIELD_VERSION, "0.26" );
//		values.put( LocationProfileConstants.FIELD_PROTOCOL_VERSION, "75" );
//		values.put( LocationProfileConstants.FIELD_WOL_ADDRESS, "" );
//		values.put( LocationProfileConstants.FIELD_HOSTNAME, "mythcenter" );
//		db.insert( LocationProfileConstants.TABLE_NAME, null, values );
		
		Log.v( TAG, "createLocationProfiles : exit" );
	}
	
	private void dropLocationProfiles( SQLiteDatabase db ) {
		Log.v( TAG, "dropLocationProfiles : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + LocationProfileConstants.TABLE_NAME );
		
		Log.v( TAG, "dropLocationProfiles : exit" );
	}
	
	private void createPlaybackProfiles( SQLiteDatabase db ) {
		Log.v( TAG, "createPlaybackProfiles : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + PlaybackProfileConstants.TABLE_NAME + " (" );
		sqlBuilder.append( PlaybackProfileConstants._ID ).append( " " ).append( PlaybackProfileConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( PlaybackProfileConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( PlaybackProfileConstants.FIELD_TYPE ).append( " " ).append( PlaybackProfileConstants.FIELD_TYPE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( PlaybackProfileConstants.FIELD_NAME ).append( " " ).append( PlaybackProfileConstants.FIELD_NAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( PlaybackProfileConstants.FIELD_WIDTH ).append( " " ).append( PlaybackProfileConstants.FIELD_WIDTH_DATA_TYPE ).append( ", " );
		sqlBuilder.append( PlaybackProfileConstants.FIELD_HEIGHT ).append( " " ).append( PlaybackProfileConstants.FIELD_HEIGHT_DATA_TYPE ).append( ", " );
		sqlBuilder.append( PlaybackProfileConstants.FIELD_BITRATE ).append( " " ).append( PlaybackProfileConstants.FIELD_BITRATE_DATA_TYPE ).append( " default " ).append( PlaybackProfileConstants.FIELD_BITRATE_DEFAULT ).append( ", " );
		sqlBuilder.append( PlaybackProfileConstants.FIELD_AUDIO_BITRATE ).append( " " ).append( PlaybackProfileConstants.FIELD_AUDIO_BITRATE_DATA_TYPE ).append( " default " ).append( PlaybackProfileConstants.FIELD_AUDIO_BITRATE_DEFAULT ).append( ", " );
		sqlBuilder.append( PlaybackProfileConstants.FIELD_SAMPLE_RATE ).append( " " ).append( PlaybackProfileConstants.FIELD_SAMPLE_RATE_DATA_TYPE ).append( " default " ).append( PlaybackProfileConstants.FIELD_SAMPLE_RATE_DEFAULT ).append( ", " );
		sqlBuilder.append( PlaybackProfileConstants.FIELD_SELECTED ).append( " " ).append( PlaybackProfileConstants.FIELD_SELECTED_DATA_TYPE ).append( " default " ).append( PlaybackProfileConstants.FIELD_SELECTED_DEFAULT );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createPlaybackProfiles : sql=" + sql );
		}
		db.execSQL( sql );

		ContentValues values = new ContentValues();

		values.put( PlaybackProfileConstants.FIELD_TYPE, "HOME" );
		values.put( PlaybackProfileConstants.FIELD_NAME, "720p" );
		values.put( PlaybackProfileConstants.FIELD_WIDTH, 1280 );
		values.put( PlaybackProfileConstants.FIELD_HEIGHT, 720 );
		values.put( PlaybackProfileConstants.FIELD_BITRATE, 2000000 );
		values.put( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, 192000 );
		values.put( PlaybackProfileConstants.FIELD_SAMPLE_RATE, 48000 );
		values.put( PlaybackProfileConstants.FIELD_SELECTED, 0 );
		db.insert( PlaybackProfileConstants.TABLE_NAME, null, values );

		values = new ContentValues();
		values.put( PlaybackProfileConstants.FIELD_TYPE, "AWAY" );
		values.put( PlaybackProfileConstants.FIELD_NAME, "720p" );
		values.put( PlaybackProfileConstants.FIELD_WIDTH, 1280 );
		values.put( PlaybackProfileConstants.FIELD_HEIGHT, 720 );
		values.put( PlaybackProfileConstants.FIELD_BITRATE, 2000000 );
		values.put( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, 192000 );
		values.put( PlaybackProfileConstants.FIELD_SAMPLE_RATE, 48000 );
		values.put( PlaybackProfileConstants.FIELD_SELECTED, 0 );
		db.insert( PlaybackProfileConstants.TABLE_NAME, null, values );
		
		values = new ContentValues();
		values.put( PlaybackProfileConstants.FIELD_TYPE, "HOME" );
		values.put( PlaybackProfileConstants.FIELD_NAME, "630p" );
		values.put( PlaybackProfileConstants.FIELD_WIDTH, 1120 );
		values.put( PlaybackProfileConstants.FIELD_HEIGHT, 630 );
		values.put( PlaybackProfileConstants.FIELD_BITRATE, 1500000 );
		values.put( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, 192000 );
		values.put( PlaybackProfileConstants.FIELD_SAMPLE_RATE, 48000 );
		values.put( PlaybackProfileConstants.FIELD_SELECTED, 0 );
		db.insert( PlaybackProfileConstants.TABLE_NAME, null, values );

		values = new ContentValues();
		values.put( PlaybackProfileConstants.FIELD_TYPE, "AWAY" );
		values.put( PlaybackProfileConstants.FIELD_NAME, "630p" );
		values.put( PlaybackProfileConstants.FIELD_WIDTH, 1120 );
		values.put( PlaybackProfileConstants.FIELD_HEIGHT, 630 );
		values.put( PlaybackProfileConstants.FIELD_BITRATE, 1500000 );
		values.put( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, 192000 );
		values.put( PlaybackProfileConstants.FIELD_SAMPLE_RATE, 48000 );
		values.put( PlaybackProfileConstants.FIELD_SELECTED, 0 );
		db.insert( PlaybackProfileConstants.TABLE_NAME, null, values );
		
		values = new ContentValues();
		values.put( PlaybackProfileConstants.FIELD_TYPE, "HOME" );
		values.put( PlaybackProfileConstants.FIELD_NAME, "540p" );
		values.put( PlaybackProfileConstants.FIELD_WIDTH, 960 );
		values.put( PlaybackProfileConstants.FIELD_HEIGHT, 540 );
		values.put( PlaybackProfileConstants.FIELD_BITRATE, 1000000 );
		values.put( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, 128000 );
		values.put( PlaybackProfileConstants.FIELD_SAMPLE_RATE, 48000 );
		values.put( PlaybackProfileConstants.FIELD_SELECTED, 1 );
		db.insert( PlaybackProfileConstants.TABLE_NAME, null, values );

		values = new ContentValues();
		values.put( PlaybackProfileConstants.FIELD_TYPE, "AWAY" );
		values.put( PlaybackProfileConstants.FIELD_NAME, "540p" );
		values.put( PlaybackProfileConstants.FIELD_WIDTH, 960 );
		values.put( PlaybackProfileConstants.FIELD_HEIGHT, 540 );
		values.put( PlaybackProfileConstants.FIELD_BITRATE, 1000000 );
		values.put( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, 128000 );
		values.put( PlaybackProfileConstants.FIELD_SAMPLE_RATE, 48000 );
		values.put( PlaybackProfileConstants.FIELD_SELECTED, 0 );
		db.insert( PlaybackProfileConstants.TABLE_NAME, null, values );

		values = new ContentValues();
		values.put( PlaybackProfileConstants.FIELD_TYPE, "HOME" );
		values.put( PlaybackProfileConstants.FIELD_NAME, "480p" );
		values.put( PlaybackProfileConstants.FIELD_WIDTH, 854 );
		values.put( PlaybackProfileConstants.FIELD_HEIGHT, 480 );
		values.put( PlaybackProfileConstants.FIELD_BITRATE, 800000 );
		values.put( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, 128000 );
		values.put( PlaybackProfileConstants.FIELD_SAMPLE_RATE, 48000 );
		values.put( PlaybackProfileConstants.FIELD_SELECTED, 0 );
		db.insert( PlaybackProfileConstants.TABLE_NAME, null, values );

		values = new ContentValues();
		values.put( PlaybackProfileConstants.FIELD_TYPE, "AWAY" );
		values.put( PlaybackProfileConstants.FIELD_NAME, "480p" );
		values.put( PlaybackProfileConstants.FIELD_WIDTH, 854 );
		values.put( PlaybackProfileConstants.FIELD_HEIGHT, 480 );
		values.put( PlaybackProfileConstants.FIELD_BITRATE, 800000 );
		values.put( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, 128000 );
		values.put( PlaybackProfileConstants.FIELD_SAMPLE_RATE, 48000 );
		values.put( PlaybackProfileConstants.FIELD_SELECTED, 0 );
		db.insert( PlaybackProfileConstants.TABLE_NAME, null, values );

		values = new ContentValues();
		values.put( PlaybackProfileConstants.FIELD_TYPE, "HOME" );
		values.put( PlaybackProfileConstants.FIELD_NAME, "360p" );
		values.put( PlaybackProfileConstants.FIELD_WIDTH, 640 );
		values.put( PlaybackProfileConstants.FIELD_HEIGHT, 360 );
		values.put( PlaybackProfileConstants.FIELD_BITRATE, 500000 );
		values.put( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, 96000 );
		values.put( PlaybackProfileConstants.FIELD_SAMPLE_RATE, 48000 );
		values.put( PlaybackProfileConstants.FIELD_SELECTED, 0 );
		db.insert( PlaybackProfileConstants.TABLE_NAME, null, values );

		values = new ContentValues();
		values.put( PlaybackProfileConstants.FIELD_TYPE, "AWAY" );
		values.put( PlaybackProfileConstants.FIELD_NAME, "360p" );
		values.put( PlaybackProfileConstants.FIELD_WIDTH, 640 );
		values.put( PlaybackProfileConstants.FIELD_HEIGHT, 360 );
		values.put( PlaybackProfileConstants.FIELD_BITRATE, 500000 );
		values.put( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, 96000 );
		values.put( PlaybackProfileConstants.FIELD_SAMPLE_RATE, 48000 );
		values.put( PlaybackProfileConstants.FIELD_SELECTED, 1 );
		db.insert( PlaybackProfileConstants.TABLE_NAME, null, values );
		
		values = new ContentValues();
		values.put( PlaybackProfileConstants.FIELD_TYPE, "HOME" );
		values.put( PlaybackProfileConstants.FIELD_NAME, "270p" );
		values.put( PlaybackProfileConstants.FIELD_WIDTH, 480 );
		values.put( PlaybackProfileConstants.FIELD_HEIGHT, 270 );
		values.put( PlaybackProfileConstants.FIELD_BITRATE, 300000 );
		values.put( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, 96000 );
		values.put( PlaybackProfileConstants.FIELD_SAMPLE_RATE, 48000 );
		values.put( PlaybackProfileConstants.FIELD_SELECTED, 0 );
		db.insert( PlaybackProfileConstants.TABLE_NAME, null, values );

		values = new ContentValues();
		values.put( PlaybackProfileConstants.FIELD_TYPE, "AWAY" );
		values.put( PlaybackProfileConstants.FIELD_NAME, "270p" );
		values.put( PlaybackProfileConstants.FIELD_WIDTH, 480 );
		values.put( PlaybackProfileConstants.FIELD_HEIGHT, 270 );
		values.put( PlaybackProfileConstants.FIELD_BITRATE, 300000 );
		values.put( PlaybackProfileConstants.FIELD_AUDIO_BITRATE, 96000 );
		values.put( PlaybackProfileConstants.FIELD_SAMPLE_RATE, 48000 );
		values.put( PlaybackProfileConstants.FIELD_SELECTED, 0 );
		db.insert( PlaybackProfileConstants.TABLE_NAME, null, values );
		
		Log.v( TAG, "createPlaybackProfiles : exit" );
	}

	private void dropPlaybackProfiles( SQLiteDatabase db ) {
		Log.v( TAG, "dropPlaybackProfiles : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + PlaybackProfileConstants.TABLE_NAME );
		
		Log.v( TAG, "dropPlaybackProfiles : exit" );
	}
	
	private void createProgram( SQLiteDatabase db, String tableName ) {
		Log.v( TAG, "createProgram : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + tableName + " (" );
		sqlBuilder.append( ProgramConstants._ID ).append( " " ).append( ProgramConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( ProgramConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_START_TIME ).append( " " ).append( ProgramConstants.FIELD_START_TIME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_END_TIME ).append( " " ).append( ProgramConstants.FIELD_END_TIME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_TITLE ).append( " " ).append( ProgramConstants.FIELD_TITLE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_SUB_TITLE ).append( " " ).append( ProgramConstants.FIELD_SUB_TITLE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_CATEGORY ).append( " " ).append( ProgramConstants.FIELD_CATEGORY_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_CATEGORY_TYPE ).append( " " ).append( ProgramConstants.FIELD_CATEGORY_TYPE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_REPEAT ).append( " " ).append( ProgramConstants.FIELD_REPEAT_DATA_TYPE ).append( " default " ).append( ProgramConstants.FIELD_REPEAT_DEFAULT ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_VIDEO_PROPS ).append( " " ).append( ProgramConstants.FIELD_VIDEO_PROPS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_AUDIO_PROPS ).append( " " ).append( ProgramConstants.FIELD_AUDIO_PROPS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_SUB_PROPS ).append( " " ).append( ProgramConstants.FIELD_SUB_PROPS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_SERIES_ID ).append( " " ).append( ProgramConstants.FIELD_SERIES_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_PROGRAM_ID ).append( " " ).append( ProgramConstants.FIELD_PROGRAM_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_STARS ).append( " " ).append( ProgramConstants.FIELD_STARS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_FILE_SIZE ).append( " " ).append( ProgramConstants.FIELD_FILE_SIZE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_LAST_MODIFIED ).append( " " ).append( ProgramConstants.FIELD_LAST_MODIFIED_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_PROGRAM_FLAGS ).append( " " ).append( ProgramConstants.FIELD_PROGRAM_FLAGS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_HOSTNAME ).append( " " ).append( ProgramConstants.FIELD_HOSTNAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_FILENAME ).append( " " ).append( ProgramConstants.FIELD_FILENAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_AIR_DATE ).append( " " ).append( ProgramConstants.FIELD_AIR_DATE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_DESCRIPTION ).append( " " ).append( ProgramConstants.FIELD_DESCRIPTION_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_INETREF ).append( " " ).append( ProgramConstants.FIELD_INETREF_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_SEASON ).append( " " ).append( ProgramConstants.FIELD_SEASON_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_EPISODE ).append( " " ).append( ProgramConstants.FIELD_EPISODE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_CHANNEL_ID ).append( " " ).append( ProgramConstants.FIELD_CHANNEL_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_RECORD_ID ).append( " " ).append( ProgramConstants.FIELD_RECORD_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_IN_ERROR ).append( " " ).append( ProgramConstants.FIELD_IN_ERROR_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_MASTER_HOSTNAME ).append( " " ).append( ProgramConstants.FIELD_MASTER_HOSTNAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramConstants.FIELD_LAST_MODIFIED_DATE ).append( " " ).append( ProgramConstants.FIELD_LAST_MODIFIED_DATE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( "UNIQUE(" ).append( ProgramConstants.FIELD_RECORD_ID ).append( ", " ).append( ProgramConstants.FIELD_START_TIME ).append( ", " ).append( ProgramConstants.FIELD_MASTER_HOSTNAME ).append( ")" ).append( ", " );
		sqlBuilder.append( "UNIQUE(" ).append( ProgramConstants.FIELD_CHANNEL_ID ).append( ", " ).append( ProgramConstants.FIELD_START_TIME ).append( ", " ).append( ProgramConstants.FIELD_MASTER_HOSTNAME ).append( ") " );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createProgram : sql=" + sql );
		}
		db.execSQL( sql );
	
//		db.execSQL( "CREATE INDEX program_group_idx ON " + tableName + " (" + ProgramConstants.FIELD_PROGRAM_GROUP + "," + ProgramConstants.FIELD_PROGRAM_TYPE + ")" );
		
		Log.v( TAG, "createProgram : exit" );
	}
	
	private void dropProgram( SQLiteDatabase db, String tableName ) {
		Log.v( TAG, "dropProgram : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + tableName );
		
		Log.v( TAG, "dropProgram : exit" );
	}
	
	private void createChannel( SQLiteDatabase db ) {
		Log.v( TAG, "createChannel : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + ChannelConstants.TABLE_NAME + " (" );
		sqlBuilder.append( ChannelConstants._ID ).append( " " ).append( ChannelConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( ChannelConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_CHAN_ID ).append( " " ).append( ChannelConstants.FIELD_CHAN_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_CHAN_NUM ).append( " " ).append( ChannelConstants.FIELD_CHAN_NUM_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_CHAN_NUM_FORMATTED ).append( " " ).append( ChannelConstants.FIELD_CHAN_NUM_FORMATTED_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_CALLSIGN ).append( " " ).append( ChannelConstants.FIELD_CALLSIGN_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_ICON_URL ).append( " " ).append( ChannelConstants.FIELD_ICON_URL_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_CHANNEL_NAME ).append( " " ).append( ChannelConstants.FIELD_CHANNEL_NAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_MPLEX_ID ).append( " " ).append( ChannelConstants.FIELD_MPLEX_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_TRANSPORT_ID ).append( " " ).append( ChannelConstants.FIELD_TRANSPORT_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_SERVICE_ID ).append( " " ).append( ChannelConstants.FIELD_SERVICE_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_NETWORK_ID ).append( " " ).append( ChannelConstants.FIELD_NETWORK_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_ATSC_MAJOR_CHAN ).append( " " ).append( ChannelConstants.FIELD_ATSC_MAJOR_CHAN_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_ATSC_MINOR_CHAN ).append( " " ).append( ChannelConstants.FIELD_ATSC_MINOR_CHAN_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_FORMAT ).append( " " ).append( ChannelConstants.FIELD_FORMAT_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_MODULATION ).append( " " ).append( ChannelConstants.FIELD_MODULATION_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_FREQUENCY ).append( " " ).append( ChannelConstants.FIELD_FREQUENCY_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_FREQUENCY_ID ).append( " " ).append( ChannelConstants.FIELD_FREQUENCY_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_FREQUENCY_TABLE ).append( " " ).append( ChannelConstants.FIELD_FREQUENCY_TABLE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_FINE_TUNE ).append( " " ).append( ChannelConstants.FIELD_FINE_TUNE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_SIS_STANDARD ).append( " " ).append( ChannelConstants.FIELD_SIS_STANDARD_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_CHAN_FILTERS ).append( " " ).append( ChannelConstants.FIELD_CHAN_FILTERS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_SOURCE_ID ).append( " " ).append( ChannelConstants.FIELD_SOURCE_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_INPUT_ID ).append( " " ).append( ChannelConstants.FIELD_INPUT_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_COMM_FREE ).append( " " ).append( ChannelConstants.FIELD_COMM_FREE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_USE_EIT ).append( " " ).append( ChannelConstants.FIELD_USE_EIT_DATA_TYPE ).append( " default " ).append( ChannelConstants.FIELD_USE_EIT_DEFAULT ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_VISIBLE ).append( " " ).append( ChannelConstants.FIELD_VISIBLE_DATA_TYPE ).append( " default " ).append( ChannelConstants.FIELD_VISIBLE_DEFAULT ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_XMLTV_ID ).append( " " ).append( ChannelConstants.FIELD_XMLTV_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_DEFAULT_AUTH ).append( " " ).append( ChannelConstants.FIELD_DEFAULT_AUTH_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_MASTER_HOSTNAME ).append( " " ).append( ChannelConstants.FIELD_MASTER_HOSTNAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_LAST_MODIFIED_DATE ).append( " " ).append( ChannelConstants.FIELD_LAST_MODIFIED_DATE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( "UNIQUE(" ).append( ChannelConstants.FIELD_CHAN_ID ).append( ", " ).append( ChannelConstants.FIELD_MASTER_HOSTNAME ).append( ")" );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createChannel : sql=" + sql );
		}
		db.execSQL( sql );
	
		Log.v( TAG, "createChannel : exit" );
	}
	
	private void dropChannel( SQLiteDatabase db ) {
		Log.v( TAG, "dropChannel : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + ChannelConstants.TABLE_NAME );
		
		Log.v( TAG, "dropChannel : exit" );
	}
	
	private void createFrontend( SQLiteDatabase db ) {
		Log.v( TAG, "createFrontend : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + FrontendConstants.TABLE_NAME + " (" );
		sqlBuilder.append( FrontendConstants._ID ).append( " " ).append( FrontendConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( FrontendConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( FrontendConstants.FIELD_NAME ).append( " " ).append( FrontendConstants.FIELD_NAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( FrontendConstants.FIELD_URL ).append( " " ).append( FrontendConstants.FIELD_URL_DATA_TYPE ).append( ", " );
		sqlBuilder.append( FrontendConstants.FIELD_AVAILABLE ).append( " " ).append( FrontendConstants.FIELD_AVAILABLE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( FrontendConstants.FIELD_MASTER_HOSTNAME ).append( " " ).append( FrontendConstants.FIELD_MASTER_HOSTNAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( FrontendConstants.FIELD_LAST_MODIFIED_DATE ).append( " " ).append( FrontendConstants.FIELD_LAST_MODIFIED_DATE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( "UNIQUE(" ).append( FrontendConstants.FIELD_NAME ).append( ", " ).append( FrontendConstants.FIELD_URL ).append( ", " ).append( FrontendConstants.FIELD_MASTER_HOSTNAME ).append( ")" );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createFrontend : sql=" + sql );
		}
		db.execSQL( sql );
	
		Log.v( TAG, "createFrontend : exit" );
	}
	
	private void dropFrontend( SQLiteDatabase db ) {
		Log.v( TAG, "dropFrontend : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + FrontendConstants.TABLE_NAME );
		
		Log.v( TAG, "dropFrontend : exit" );
	}
	
	private void createProgramGroup( SQLiteDatabase db ) {
		Log.v( TAG, "createProgramGroup : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + ProgramGroupConstants.TABLE_NAME + " (" );
		sqlBuilder.append( ProgramGroupConstants._ID ).append( " " ).append( ProgramGroupConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( ProgramGroupConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( ProgramGroupConstants.FIELD_PROGRAM_GROUP ).append( " " ).append( ProgramGroupConstants.FIELD_PROGRAM_GROUP_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramGroupConstants.FIELD_TITLE ).append( " " ).append( ProgramGroupConstants.FIELD_TITLE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramGroupConstants.FIELD_CATEGORY ).append( " " ).append( ProgramGroupConstants.FIELD_CATEGORY_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramGroupConstants.FIELD_INETREF ).append( " " ).append( ProgramGroupConstants.FIELD_INETREF_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramGroupConstants.FIELD_MASTER_HOSTNAME ).append( " " ).append( ProgramGroupConstants.FIELD_MASTER_HOSTNAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramGroupConstants.FIELD_LAST_MODIFIED_DATE ).append( " " ).append( ProgramGroupConstants.FIELD_LAST_MODIFIED_DATE_DATA_TYPE );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createProgramGroup : sql=" + sql );
		}
		db.execSQL( sql );
	
		Log.v( TAG, "createProgramGroup : exit" );
	}
	
	private void dropProgramGroup( SQLiteDatabase db ) {
		Log.v( TAG, "dropProgramGroup : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + ProgramGroupConstants.TABLE_NAME );
		
		Log.v( TAG, "dropProgramGroup : exit" );
	}
	
	private void createRecording( SQLiteDatabase db, String tableName ) {
		Log.v( TAG, "createRecording : enter" );
		
		RecordingConstants.ContentDetails details = RecordingConstants.ContentDetails.getValueFromTableName( tableName );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + tableName + " (" );
		sqlBuilder.append( RecordingConstants._ID ).append( " " ).append( RecordingConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( RecordingConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_STATUS ).append( " " ).append( RecordingConstants.FIELD_STATUS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_PRIORITY ).append( " " ).append( RecordingConstants.FIELD_PRIORITY_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_START_TS ).append( " " ).append( RecordingConstants.FIELD_START_TS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_END_TS ).append( " " ).append( RecordingConstants.FIELD_END_TS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_RECORD_ID ).append( " " ).append( RecordingConstants.FIELD_RECORD_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_REC_GROUP ).append( " " ).append( RecordingConstants.FIELD_REC_GROUP_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_PLAY_GROUP ).append( " " ).append( RecordingConstants.FIELD_PLAY_GROUP_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_STORAGE_GROUP ).append( " " ).append( RecordingConstants.FIELD_STORAGE_GROUP_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_REC_TYPE ).append( " " ).append( RecordingConstants.FIELD_REC_TYPE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_DUP_IN_TYPE ).append( " " ).append( RecordingConstants.FIELD_DUP_IN_TYPE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_DUP_METHOD ).append( " " ).append( RecordingConstants.FIELD_DUP_METHOD_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_ENCODER_ID ).append( " " ).append( RecordingConstants.FIELD_ENCODER_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_PROFILE ).append( " " ).append( RecordingConstants.FIELD_PROFILE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_PROGRAM_ID ).append( " " ).append( RecordingConstants.FIELD_PROGRAM_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_START_TIME ).append( " " ).append( RecordingConstants.FIELD_START_TIME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_MASTER_HOSTNAME ).append( " " ).append( RecordingConstants.FIELD_MASTER_HOSTNAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingConstants.FIELD_LAST_MODIFIED_DATE ).append( " " ).append( RecordingConstants.FIELD_LAST_MODIFIED_DATE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( "UNIQUE(" ).append( RecordingConstants.FIELD_RECORD_ID ).append( ", " ).append( RecordingConstants.FIELD_START_TIME ).append( ", " ).append( RecordingConstants.FIELD_MASTER_HOSTNAME ).append( ")" ).append( ", " );
		sqlBuilder.append( "FOREIGN KEY (" + RecordingConstants.FIELD_START_TIME + "," + RecordingConstants.FIELD_RECORD_ID + "," + RecordingConstants.FIELD_MASTER_HOSTNAME + ") REFERENCES " + details.getParent() + " (" + ProgramConstants.FIELD_START_TIME + "," + ProgramConstants.FIELD_RECORD_ID + "," + ProgramConstants.FIELD_MASTER_HOSTNAME + ") ON DELETE CASCADE " );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createRecording : sql=" + sql );
		}
		db.execSQL( sql );
	
		Log.v( TAG, "createRecording : exit" );
	}
	
	private void dropRecording( SQLiteDatabase db, String tableName ) {
		Log.v( TAG, "dropRecording : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + tableName );
		
		Log.v( TAG, "dropRecording : exit" );
	}
	
	private void createLiveStream( SQLiteDatabase db ) {
		Log.v( TAG, "createLiveStream : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + LiveStreamConstants.TABLE_NAME + " (" );
		sqlBuilder.append( LiveStreamConstants._ID ).append( " " ).append( ChannelConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( ChannelConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_ID ).append( " " ).append( LiveStreamConstants.FIELD_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_WIDTH ).append( " " ).append( LiveStreamConstants.FIELD_WIDTH_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_HEIGHT ).append( " " ).append( LiveStreamConstants.FIELD_HEIGHT_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_BITRATE ).append( " " ).append( LiveStreamConstants.FIELD_BITRATE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_AUDIO_BITRATE ).append( " " ).append( LiveStreamConstants.FIELD_AUDIO_BITRATE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_SEGMENT_SIZE ).append( " " ).append( LiveStreamConstants.FIELD_SEGMENT_SIZE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_MAX_SEGMENTS ).append( " " ).append( LiveStreamConstants.FIELD_MAX_SEGMENTS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_START_SEGMENT ).append( " " ).append( LiveStreamConstants.FIELD_START_SEGMENT_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_CURRENT_SEGMENT ).append( " " ).append( LiveStreamConstants.FIELD_CURRENT_SEGMENT_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_SEGMENT_COUNT ).append( " " ).append( LiveStreamConstants.FIELD_SEGMENT_COUNT_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_PERCENT_COMPLETE ).append( " " ).append( LiveStreamConstants.FIELD_PERCENT_COMPLETE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_CREATED ).append( " " ).append( LiveStreamConstants.FIELD_CREATED_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_LAST_MODIFIED ).append( " " ).append( LiveStreamConstants.FIELD_LAST_MODIFIED_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_RELATIVE_URL ).append( " " ).append( LiveStreamConstants.FIELD_RELATIVE_URL_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_FULL_URL ).append( " " ).append( LiveStreamConstants.FIELD_FULL_URL_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_STATUS_STR ).append( " " ).append( LiveStreamConstants.FIELD_STATUS_STR_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_STATUS_INT ).append( " " ).append( LiveStreamConstants.FIELD_STATUS_INT_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_STATUS_MESSAGE ).append( " " ).append( LiveStreamConstants.FIELD_STATUS_MESSAGE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_SOURCE_FILE ).append( " " ).append( LiveStreamConstants.FIELD_SOURCE_FILE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_SOURCE_HOST ).append( " " ).append( LiveStreamConstants.FIELD_SOURCE_HOST_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_SOURCE_WIDTH ).append( " " ).append( LiveStreamConstants.FIELD_SOURCE_WIDTH_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_SOURCE_HEIGHT ).append( " " ).append( LiveStreamConstants.FIELD_SOURCE_HEIGHT_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_AUDIO_ONLY_BITRATE ).append( " " ).append( LiveStreamConstants.FIELD_AUDIO_ONLY_BITRATE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_START_TIME ).append( " " ).append( LiveStreamConstants.FIELD_START_TIME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_CHAN_ID ).append( " " ).append( LiveStreamConstants.FIELD_CHAN_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LiveStreamConstants.FIELD_MASTER_HOSTNAME ).append( " " ).append( LiveStreamConstants.FIELD_MASTER_HOSTNAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( "UNIQUE(" ).append( LiveStreamConstants.FIELD_START_TIME ).append( ", " ).append( LiveStreamConstants.FIELD_CHAN_ID ).append( ", " ).append( LiveStreamConstants.FIELD_MASTER_HOSTNAME ).append( ")" ).append( ", " );
		sqlBuilder.append( "FOREIGN KEY (" + LiveStreamConstants.FIELD_START_TIME + "," + LiveStreamConstants.FIELD_CHAN_ID + "," + LiveStreamConstants.FIELD_MASTER_HOSTNAME + ") REFERENCES " + ProgramConstants.TABLE_NAME_RECORDED + " (" + ProgramConstants.FIELD_START_TIME + "," + ProgramConstants.FIELD_CHANNEL_ID + "," + ProgramConstants.FIELD_MASTER_HOSTNAME + ") ON DELETE CASCADE " );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createLiveStream : sql=" + sql );
		}
		db.execSQL( sql );
	
		Log.v( TAG, "createLiveStream : exit" );
	}
	
	private void dropLiveStream( SQLiteDatabase db ) {
		Log.v( TAG, "dropLiveStream : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + LiveStreamConstants.TABLE_NAME );
		
		Log.v( TAG, "dropLiveStream : exit" );
	}
	

	private void createRecordingRule( SQLiteDatabase db ) {
		Log.v( TAG, "createRecordingRule : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + RecordingRuleConstants.TABLE_NAME + " (" );
		sqlBuilder.append( RecordingRuleConstants._ID ).append( " " ).append( RecordingRuleConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( RecordingRuleConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_REC_RULE_ID ).append( " " ).append( RecordingRuleConstants.FIELD_REC_RULE_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_PARENT_ID ).append( " " ).append( RecordingRuleConstants.FIELD_PARENT_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_INACTIVE ).append( " " ).append( RecordingRuleConstants.FIELD_INACTIVE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_TITLE ).append( " " ).append( RecordingRuleConstants.FIELD_TITLE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_SUB_TITLE ).append( " " ).append( RecordingRuleConstants.FIELD_SUB_TITLE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_DESCRIPTION ).append( " " ).append( RecordingRuleConstants.FIELD_DESCRIPTION_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_SEASON ).append( " " ).append( RecordingRuleConstants.FIELD_SEASON_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_EPISODE ).append( " " ).append( RecordingRuleConstants.FIELD_EPISODE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_CATEGORY ).append( " " ).append( RecordingRuleConstants.FIELD_CATEGORY_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_START_TIME ).append( " " ).append( RecordingRuleConstants.FIELD_START_TIME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_END_TIME ).append( " " ).append( RecordingRuleConstants.FIELD_END_TIME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_SERIES_ID ).append( " " ).append( RecordingRuleConstants.FIELD_SERIES_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_PROGRAM_ID ).append( " " ).append( RecordingRuleConstants.FIELD_PROGRAM_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_INETREF ).append( " " ).append( RecordingRuleConstants.FIELD_INETREF_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_CHAN_ID ).append( " " ).append( RecordingRuleConstants.FIELD_CHAN_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_CALLSIGN ).append( " " ).append( RecordingRuleConstants.FIELD_CALLSIGN_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_DAY ).append( " " ).append( RecordingRuleConstants.FIELD_DAY_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_TIME ).append( " " ).append( RecordingRuleConstants.FIELD_TIME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_FIND_ID ).append( " " ).append( RecordingRuleConstants.FIELD_FIND_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_TYPE ).append( " " ).append( RecordingRuleConstants.FIELD_TYPE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_SEARCH_TYPE ).append( " " ).append( RecordingRuleConstants.FIELD_SEARCH_TYPE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_REC_PRIORITY ).append( " " ).append( RecordingRuleConstants.FIELD_REC_PRIORITY_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_PREFERRED_INPUT ).append( " " ).append( RecordingRuleConstants.FIELD_PREFERRED_INPUT_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_START_OFFSET ).append( " " ).append( RecordingRuleConstants.FIELD_START_OFFSET_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_END_OFFSET ).append( " " ).append( RecordingRuleConstants.FIELD_END_OFFSET_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_DUP_METHOD ).append( " " ).append( RecordingRuleConstants.FIELD_DUP_METHOD_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_DUP_IN ).append( " " ).append( RecordingRuleConstants.FIELD_DUP_IN_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_FILTER ).append( " " ).append( RecordingRuleConstants.FIELD_FILTER_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_REC_PROFILE ).append( " " ).append( RecordingRuleConstants.FIELD_REC_PROFILE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_REC_GROUP ).append( " " ).append( RecordingRuleConstants.FIELD_REC_GROUP_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_STORAGE_GROUP ).append( " " ).append( RecordingRuleConstants.FIELD_STORAGE_GROUP_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_PLAY_GROUP ).append( " " ).append( RecordingRuleConstants.FIELD_PLAY_GROUP_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_AUTO_EXPIRE ).append( " " ).append( RecordingRuleConstants.FIELD_AUTO_EXPIRE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_MAX_EPISODES ).append( " " ).append( RecordingRuleConstants.FIELD_MAX_EPISODES_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_MAX_NEWEST ).append( " " ).append( RecordingRuleConstants.FIELD_MAX_NEWEST_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_AUTO_COMMFLAG ).append( " " ).append( RecordingRuleConstants.FIELD_AUTO_COMMFLAG_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_AUTO_TRANSCODE ).append( " " ).append( RecordingRuleConstants.FIELD_AUTO_TRANSCODE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_AUTO_METADATA ).append( " " ).append( RecordingRuleConstants.FIELD_AUTO_METADATA_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_AUTO_USER_JOB_1 ).append( " " ).append( RecordingRuleConstants.FIELD_AUTO_USER_JOB_1_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_AUTO_USER_JOB_2 ).append( " " ).append( RecordingRuleConstants.FIELD_AUTO_USER_JOB_2_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_AUTO_USER_JOB_3 ).append( " " ).append( RecordingRuleConstants.FIELD_AUTO_USER_JOB_3_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_AUTO_USER_JOB_4 ).append( " " ).append( RecordingRuleConstants.FIELD_AUTO_USER_JOB_4_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_TRANSCODER ).append( " " ).append( RecordingRuleConstants.FIELD_TRANSCODER_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_NEXT_RECORDING ).append( " " ).append( RecordingRuleConstants.FIELD_NEXT_RECORDING_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_LAST_RECORDED ).append( " " ).append( RecordingRuleConstants.FIELD_LAST_RECORDED_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_LAST_DELETED ).append( " " ).append( RecordingRuleConstants.FIELD_LAST_DELETED_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_AVERAGE_DELAY ).append( " " ).append( RecordingRuleConstants.FIELD_AVERAGE_DELAY_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_MASTER_HOSTNAME ).append( " " ).append( RecordingRuleConstants.FIELD_MASTER_HOSTNAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( RecordingRuleConstants.FIELD_LAST_MODIFIED_DATE ).append( " " ).append( RecordingRuleConstants.FIELD_LAST_MODIFIED_DATE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( "UNIQUE(" ).append( RecordingRuleConstants.FIELD_REC_RULE_ID ).append( ", " ).append( RecordingRuleConstants.FIELD_MASTER_HOSTNAME ).append( ")" );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createRecordingRule : sql=" + sql );
		}
		db.execSQL( sql );
	
		Log.v( TAG, "createRecordingRule : exit" );
	}
	
	private void dropRecordingRule( SQLiteDatabase db ) {
		Log.v( TAG, "dropRecordingRule : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + RecordingRuleConstants.TABLE_NAME );
		
		Log.v( TAG, "dropRecordingRule : exit" );
	}
	
}
