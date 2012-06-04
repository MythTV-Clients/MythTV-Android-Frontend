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
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */

package org.mythtv.client.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = DatabaseHelper.class.getSimpleName();
	
	private static final String DATABASE_NAME = "mythtvdb";
	private static final int DATABASE_VERSION = 4;

	public static final String TABLE_LOCATION_PROFILE = "LOCATION_PROFILE";
	public static final String TABLE_LOCATION_PROFILE_ID = "_ID";
	public static final String TABLE_LOCATION_PROFILE_TYPE = "TYPE";
	public static final String TABLE_LOCATION_PROFILE_NAME = "NAME";
	public static final String TABLE_LOCATION_PROFILE_URL = "URL";
	public static final String TABLE_LOCATION_PROFILE_SELECTED = "SELECTED";
	public static final String SELECT_LOCATION_PROFILE =
			"select " +
				"lp._id, lp.type, lp.name, lp.url, lp.selected " +
			"from " +
				"location_profile lp";
	
	public static final String TABLE_PLAYBACK_PROFILE = "PLAYBACK_PROFILE";
	public static final String TABLE_PLAYBACK_PROFILE_ID = "_ID";
	public static final String TABLE_PLAYBACK_PROFILE_TYPE = "TYPE";
	public static final String TABLE_PLAYBACK_PROFILE_NAME = "NAME";
	public static final String TABLE_PLAYBACK_PROFILE_WIDTH = "WIDTH";
	public static final String TABLE_PLAYBACK_PROFILE_HEIGHT = "HEIGHT";
	public static final String TABLE_PLAYBACK_PROFILE_BITRATE = "BITRATE";
	public static final String TABLE_PLAYBACK_PROFILE_AUDIO_BITRATE = "AUDIO_BITRATE";
	public static final String TABLE_PLAYBACK_PROFILE_SAMPLE_RATE = "SAMPLE_RATE";
	public static final String TABLE_PLAYBACK_PROFILE_SELECTED = "SELECTED";
	public static final String SELECT_PLAYBACK_PROFILE =
			"select " +
				"pp._id, pp.type, pp.name, pp.width, pp.height, pp.bitrate, pp.audio_bitrate, pp.sample_rate, lp.selected " +
			"from " +
				"playback_profile pp";

	
	public DatabaseHelper( Context context ) {
		super( context, DATABASE_NAME, null, DATABASE_VERSION );
	}
	
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate( SQLiteDatabase db ) {
		Log.v( TAG, "onCreate : enter" );
		
		db.execSQL( "CREATE TABLE location_profile( _id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, name TEXT, url TEXT, selected INTEGER default 0 );" );

		createPlaybackProfiles( db );
		
		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		Log.v( TAG, "onUpgrade : enter" );

	    db.execSQL( "DROP TABLE IF EXISTS playback_profile" );

	    createPlaybackProfiles( db );
	    
		Log.v( TAG, "onUpgrade : exit" );
	}

	// internal helpers
	
	private void createPlaybackProfiles( SQLiteDatabase db ) {
		Log.v( TAG, "createPlaybackProfiles : enter" );
		
		db.execSQL( "CREATE TABLE playback_profile( _id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, name TEXT, url TEXT, width INTEGER, height INTEGER, bitrate INTEGER default 800000, audio_bitrate INTEGER default 64000, sample_rate INTEGER default 44100, selected INTEGER default 0 );" );

		ContentValues values = new ContentValues();

		values.put( TABLE_PLAYBACK_PROFILE_TYPE, "HOME" );
		values.put( TABLE_PLAYBACK_PROFILE_NAME, "Large" );
		values.put( TABLE_PLAYBACK_PROFILE_WIDTH, 1280 );
		values.put( TABLE_PLAYBACK_PROFILE_HEIGHT, 720 );
		values.put( TABLE_PLAYBACK_PROFILE_BITRATE, 8000000 );
		values.put( TABLE_PLAYBACK_PROFILE_AUDIO_BITRATE, 96000 );
		values.put( TABLE_PLAYBACK_PROFILE_SAMPLE_RATE, 48000 );
		values.put( TABLE_PLAYBACK_PROFILE_SELECTED, 1 );
		db.insert( TABLE_PLAYBACK_PROFILE, null, values );

		values = new ContentValues();
		values.put( TABLE_PLAYBACK_PROFILE_TYPE, "AWAY" );
		values.put( TABLE_PLAYBACK_PROFILE_NAME, "Large" );
		values.put( TABLE_PLAYBACK_PROFILE_WIDTH, 1280 );
		values.put( TABLE_PLAYBACK_PROFILE_HEIGHT, 720 );
		values.put( TABLE_PLAYBACK_PROFILE_BITRATE, 8000000 );
		values.put( TABLE_PLAYBACK_PROFILE_AUDIO_BITRATE, 96000 );
		values.put( TABLE_PLAYBACK_PROFILE_SAMPLE_RATE, 48000 );
		values.put( TABLE_PLAYBACK_PROFILE_SELECTED, 0 );
		db.insert( TABLE_PLAYBACK_PROFILE, null, values );

		values = new ContentValues();
		values.put( TABLE_PLAYBACK_PROFILE_TYPE, "HOME" );
		values.put( TABLE_PLAYBACK_PROFILE_NAME, "Medium" );
		values.put( TABLE_PLAYBACK_PROFILE_WIDTH, 720 );
		values.put( TABLE_PLAYBACK_PROFILE_HEIGHT, 480 );
		values.put( TABLE_PLAYBACK_PROFILE_BITRATE, 5000000 );
		values.put( TABLE_PLAYBACK_PROFILE_AUDIO_BITRATE, 96000 );
		values.put( TABLE_PLAYBACK_PROFILE_SAMPLE_RATE, 48000 );
		values.put( TABLE_PLAYBACK_PROFILE_SELECTED, 0 );
		db.insert( TABLE_PLAYBACK_PROFILE, null, values );

		values = new ContentValues();
		values.put( TABLE_PLAYBACK_PROFILE_TYPE, "AWAY" );
		values.put( TABLE_PLAYBACK_PROFILE_NAME, "Medium" );
		values.put( TABLE_PLAYBACK_PROFILE_WIDTH, 720 );
		values.put( TABLE_PLAYBACK_PROFILE_HEIGHT, 480 );
		values.put( TABLE_PLAYBACK_PROFILE_BITRATE, 5000000 );
		values.put( TABLE_PLAYBACK_PROFILE_AUDIO_BITRATE, 96000 );
		values.put( TABLE_PLAYBACK_PROFILE_SAMPLE_RATE, 48000 );
		values.put( TABLE_PLAYBACK_PROFILE_SELECTED, 1 );
		db.insert( TABLE_PLAYBACK_PROFILE, null, values );

		values = new ContentValues();
		values.put( TABLE_PLAYBACK_PROFILE_TYPE, "HOME" );
		values.put( TABLE_PLAYBACK_PROFILE_NAME, "Small" );
		values.put( TABLE_PLAYBACK_PROFILE_WIDTH, 352 );
		values.put( TABLE_PLAYBACK_PROFILE_HEIGHT, 288 );
		values.put( TABLE_PLAYBACK_PROFILE_BITRATE, 1200000 );
		values.put( TABLE_PLAYBACK_PROFILE_AUDIO_BITRATE, 96000 );
		values.put( TABLE_PLAYBACK_PROFILE_SAMPLE_RATE, 48000 );
		values.put( TABLE_PLAYBACK_PROFILE_SELECTED, 0 );
		db.insert( TABLE_PLAYBACK_PROFILE, null, values );

		values = new ContentValues();
		values.put( TABLE_PLAYBACK_PROFILE_TYPE, "AWAY" );
		values.put( TABLE_PLAYBACK_PROFILE_NAME, "Small" );
		values.put( TABLE_PLAYBACK_PROFILE_WIDTH, 352 );
		values.put( TABLE_PLAYBACK_PROFILE_HEIGHT, 288 );
		values.put( TABLE_PLAYBACK_PROFILE_BITRATE, 1200000 );
		values.put( TABLE_PLAYBACK_PROFILE_AUDIO_BITRATE, 96000 );
		values.put( TABLE_PLAYBACK_PROFILE_SAMPLE_RATE, 48000 );
		values.put( TABLE_PLAYBACK_PROFILE_SELECTED, 0 );
		db.insert( TABLE_PLAYBACK_PROFILE, null, values );
		
		Log.v( TAG, "createPlaybackProfiles : exit" );
	}
	
}
