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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * @author John Baab <rhpot1991@ubuntu.com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */

package org.mythtv.db;

import static android.provider.BaseColumns._ID;

import org.mythtv.db.dvr.ProgramConstants;
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
	private static final int DATABASE_VERSION = 6;

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
		
		dropProgram( db );
		createProgram( db );
		
		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		Log.v( TAG, "onUpgrade : enter" );

		switch( newVersion ) {
			case 5:
				Log.v( TAG, "onUpgrade : upgrading to db version 5" );
				
				dropPlaybackProfiles( db );
				createPlaybackProfiles( db );

				break;
			case 6:
				Log.v( TAG, "onUpgrade : upgrading to db version 6" );
				
				dropProgram( db );
				createProgram( db );
				
				break;
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
		sqlBuilder.append( ProgramConstants.FIELD_EPISODE ).append( " " ).append( ProgramConstants.FIELD_EPISODE_DATA_TYPE );
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
	
}
