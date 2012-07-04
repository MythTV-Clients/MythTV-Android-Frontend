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
package org.mythtv.db;

import static android.provider.BaseColumns._ID;

import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.content.ArtworkConstants;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.ProgramGroupConstants;
import org.mythtv.db.dvr.RecordingConstants;
import org.mythtv.db.dvr.UpcomingConstants;
import org.mythtv.db.preferences.LocationProfileConstants;
import org.mythtv.db.preferences.PlaybackProfileConstants;

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
	private static final int DATABASE_VERSION = 12;

	public DatabaseHelper( Context context ) {
		super( context, DATABASE_NAME, null, DATABASE_VERSION );
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
		
		dropProgramGroup( db );
		createProgramGroup( db );
		
		dropProgram( db );
		createProgram( db );
		
		dropRecording( db );
		createRecording( db );
		
		dropChannel( db );
		createChannel( db );
		
		dropArtwork( db );
		createArtwork( db );
		
		dropUpcoming( db );
		createUpcoming( db );

		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		Log.v( TAG, "onUpgrade : enter" );

		if( oldVersion < 5 ) {
			Log.v( TAG, "onUpgrade : upgrading to db version 5" );
				
			dropPlaybackProfiles( db );
			createPlaybackProfiles( db );
		}

		if( oldVersion < 12 ) {
			Log.v( TAG, "onUpgrade : upgrading to db version 12" );

			dropProgramGroup( db );
			createProgramGroup( db );

			dropProgram( db );
			createProgram( db );

			dropRecording( db );
			createRecording( db );

			dropChannel( db );
			createChannel( db );

			dropProgramChannels( db );
			createProgramChannels( db );

			dropArtwork( db );
			createArtwork( db );

			dropProgramArtworks( db );
			createProgramArtworks( db );
		}

		if( oldVersion < 13 ) {
			Log.v( TAG, "onUpgrade : upgrading to db version 13" );

			dropUpcoming( db );
			createUpcoming( db );
		}
		
		Log.v( TAG, "onUpgrade : exit" );
	}

	// internal helpers
	
	private void createLocationProfiles( SQLiteDatabase db ) {
		Log.v( TAG, "createLocationProfiles : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + LocationProfileConstants.TABLE_NAME + " (" );
		sqlBuilder.append( _ID ).append( " " ).append( LocationProfileConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( LocationProfileConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( LocationProfileConstants.FIELD_TYPE ).append( " " ).append( LocationProfileConstants.FIELD_TYPE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LocationProfileConstants.FIELD_NAME ).append( " " ).append( LocationProfileConstants.FIELD_NAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LocationProfileConstants.FIELD_URL ).append( " " ).append( LocationProfileConstants.FIELD_URL_DATA_TYPE ).append( ", " );
		sqlBuilder.append( LocationProfileConstants.FIELD_SELECTED ).append( " " ).append( LocationProfileConstants.FIELD_SELECTED_DATA_TYPE ).append( " default" ).append( LocationProfileConstants.FIELD_SELECTED_DEFAULT );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createLocationProfiles : sql=" + sql );
		}
		db.execSQL( sql );

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
		sqlBuilder.append( _ID ).append( " " ).append( PlaybackProfileConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( PlaybackProfileConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
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
	
	private void createProgramGroup( SQLiteDatabase db ) {
		Log.v( TAG, "createProgramGroup : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + ProgramGroupConstants.TABLE_NAME + " (" );
		sqlBuilder.append( _ID ).append( " " ).append( ProgramGroupConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( ProgramGroupConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( ProgramGroupConstants.FIELD_PROGRAM_GROUP ).append( " " ).append( ProgramGroupConstants.FIELD_PROGRAM_GROUP_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramGroupConstants.FIELD_INETREF ).append( " " ).append( ProgramGroupConstants.FIELD_INETREF_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ProgramGroupConstants.FIELD_BANNER_URL ).append( " " ).append( ProgramGroupConstants.FIELD_BANNER_URL_DATA_TYPE );
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
	
	private void createProgram( SQLiteDatabase db ) {
		Log.v( TAG, "createProgram : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + ProgramConstants.TABLE_NAME + " (" );
		sqlBuilder.append( _ID ).append( " " ).append( ProgramConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( ProgramConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
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
		sqlBuilder.append( ProgramConstants.FIELD_PROGRAM_GROUP_ID ).append( " " ).append( ProgramConstants.FIELD_PROGRAM_GROUP_ID_DATA_TYPE );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createProgram : sql=" + sql );
		}
		db.execSQL( sql );
	
		Log.v( TAG, "createProgram : exit" );
	}
	
	private void dropProgram( SQLiteDatabase db ) {
		Log.v( TAG, "dropProgram : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + ProgramConstants.TABLE_NAME );
		
		Log.v( TAG, "dropProgram : exit" );
	}
	
	private void createUpcoming( SQLiteDatabase db ) {
		Log.v( TAG, "createUpcoming : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + UpcomingConstants.TABLE_NAME + " (" );
		sqlBuilder.append( _ID ).append( " " ).append( UpcomingConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( UpcomingConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_START_TIME ).append( " " ).append( UpcomingConstants.FIELD_START_TIME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_END_TIME ).append( " " ).append( UpcomingConstants.FIELD_END_TIME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_TITLE ).append( " " ).append( UpcomingConstants.FIELD_TITLE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_SUB_TITLE ).append( " " ).append( UpcomingConstants.FIELD_SUB_TITLE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_CATEGORY ).append( " " ).append( UpcomingConstants.FIELD_CATEGORY_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_CATEGORY_TYPE ).append( " " ).append( UpcomingConstants.FIELD_CATEGORY_TYPE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_REPEAT ).append( " " ).append( UpcomingConstants.FIELD_REPEAT_DATA_TYPE ).append( " default " ).append( UpcomingConstants.FIELD_REPEAT_DEFAULT ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_VIDEO_PROPS ).append( " " ).append( UpcomingConstants.FIELD_VIDEO_PROPS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_AUDIO_PROPS ).append( " " ).append( UpcomingConstants.FIELD_AUDIO_PROPS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_SUB_PROPS ).append( " " ).append( UpcomingConstants.FIELD_SUB_PROPS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_SERIES_ID ).append( " " ).append( UpcomingConstants.FIELD_SERIES_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_PROGRAM_ID ).append( " " ).append( UpcomingConstants.FIELD_PROGRAM_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_STARS ).append( " " ).append( UpcomingConstants.FIELD_STARS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_FILE_SIZE ).append( " " ).append( UpcomingConstants.FIELD_FILE_SIZE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_LAST_MODIFIED ).append( " " ).append( UpcomingConstants.FIELD_LAST_MODIFIED_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_PROGRAM_FLAGS ).append( " " ).append( UpcomingConstants.FIELD_PROGRAM_FLAGS_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_HOSTNAME ).append( " " ).append( UpcomingConstants.FIELD_HOSTNAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_FILENAME ).append( " " ).append( UpcomingConstants.FIELD_FILENAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_AIR_DATE ).append( " " ).append( UpcomingConstants.FIELD_AIR_DATE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_DESCRIPTION ).append( " " ).append( UpcomingConstants.FIELD_DESCRIPTION_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_INETREF ).append( " " ).append( UpcomingConstants.FIELD_INETREF_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_SEASON ).append( " " ).append( UpcomingConstants.FIELD_SEASON_DATA_TYPE ).append( ", " );
		sqlBuilder.append( UpcomingConstants.FIELD_EPISODE ).append( " " ).append( UpcomingConstants.FIELD_EPISODE_DATA_TYPE );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createUpcoming : sql=" + sql );
		}
		db.execSQL( sql );
	
		Log.v( TAG, "createUpcoming : exit" );
	}
	
	private void dropUpcoming( SQLiteDatabase db ) {
		Log.v( TAG, "dropUpcoming : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + UpcomingConstants.TABLE_NAME );
		
		Log.v( TAG, "dropUpcoming : exit" );
	}
	
	private void createRecording( SQLiteDatabase db ) {
		Log.v( TAG, "createRecording : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + RecordingConstants.TABLE_NAME + " (" );
		sqlBuilder.append( _ID ).append( " " ).append( RecordingConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( RecordingConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
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
		sqlBuilder.append( RecordingConstants.FIELD_PROGRAM_ID ).append( " " ).append( RecordingConstants.FIELD_PROGRAM_ID_DATA_TYPE );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createRecording : sql=" + sql );
		}
		db.execSQL( sql );
	
		Log.v( TAG, "createRecording : exit" );
	}

	private void dropRecording( SQLiteDatabase db ) {
		Log.v( TAG, "dropRecording : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + RecordingConstants.TABLE_NAME );
		
		Log.v( TAG, "dropRecording : exit" );
	}
	
	private void createChannel( SQLiteDatabase db ) {
		Log.v( TAG, "createChannel : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + ChannelConstants.TABLE_NAME + " (" );
		sqlBuilder.append( _ID ).append( " " ).append( ChannelConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( ChannelConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_CHAN_ID ).append( " " ).append( ChannelConstants.FIELD_CHAN_ID_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ChannelConstants.FIELD_CHAN_NUM ).append( " " ).append( ChannelConstants.FIELD_CHAN_NUM_DATA_TYPE ).append( ", " );
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
		sqlBuilder.append( ChannelConstants.FIELD_DEFAULT_AUTH ).append( " " ).append( ChannelConstants.FIELD_DEFAULT_AUTH_DATA_TYPE );
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
	
	private void createProgramChannels( SQLiteDatabase db ) {
		Log.v( TAG, "createProgramChannels : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE PROGRAM_CHANNELS (" );
		sqlBuilder.append( "PROGRAM_ID INTEGER, " );
		sqlBuilder.append( "CHANNEL_ID INTEGER, " );
		sqlBuilder.append( "PRIMARY KEY( PROGRAM_ID, CHANNEL_ID ) " );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createProgramChannels : sql=" + sql );
		}
		db.execSQL( sql );
	
		Log.v( TAG, "createProgramChannels : exit" );
	}
	
	private void dropProgramChannels( SQLiteDatabase db ) {
		Log.v( TAG, "dropProgramChannels : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS PROGRAM_CHANNELS" );
		
		Log.v( TAG, "dropProgramChannels : exit" );
	}
	
	private void createArtwork( SQLiteDatabase db ) {
		Log.v( TAG, "createArtwork : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + ArtworkConstants.TABLE_NAME + " (" );
		sqlBuilder.append( _ID ).append( " " ).append( ArtworkConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( ArtworkConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( ArtworkConstants.FIELD_URL ).append( " " ).append( ArtworkConstants.FIELD_URL_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ArtworkConstants.FIELD_FILE_NAME ).append( " " ).append( ArtworkConstants.FIELD_FILE_NAME_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ArtworkConstants.FIELD_STORAGE_GROUP ).append( " " ).append( ArtworkConstants.FIELD_STORAGE_GROUP_DATA_TYPE ).append( ", " );
		sqlBuilder.append( ArtworkConstants.FIELD_TYPE ).append( " " ).append( ArtworkConstants.FIELD_TYPE_DATA_TYPE );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createArtwork : sql=" + sql );
		}
		db.execSQL( sql );
	
		Log.v( TAG, "createArtwork : exit" );
	}
	
	private void dropArtwork( SQLiteDatabase db ) {
		Log.v( TAG, "dropArtwork : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + ArtworkConstants.TABLE_NAME );
		
		Log.v( TAG, "dropArtwork : exit" );
	}
	
	private void createProgramArtworks( SQLiteDatabase db ) {
		Log.v( TAG, "createProgramArtworks : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE PROGRAM_ARTWORKS (" );
		sqlBuilder.append( "PROGRAM_ID INTEGER, " );
		sqlBuilder.append( "ARTWORK_ID INTEGER, " );
		sqlBuilder.append( "PRIMARY KEY( PROGRAM_ID, ARTWORK_ID ) " );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createProgramArtworks : sql=" + sql );
		}
		db.execSQL( sql );
	
		Log.v( TAG, "createProgramArtworks : exit" );
	}
	
	private void dropProgramArtworks( SQLiteDatabase db ) {
		Log.v( TAG, "dropProgramArtworks : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS PROGRAM_ARTWORKS" );
		
		Log.v( TAG, "dropProgramArtworks : exit" );
	}
	
}
