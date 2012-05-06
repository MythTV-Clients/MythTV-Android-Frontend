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
	private static final int DATABASE_VERSION = 1;
	
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
	
	public DatabaseHelper( Context context ) {
		super( context, DATABASE_NAME, null, DATABASE_VERSION );
	}
	
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate( SQLiteDatabase db ) {
		Log.v( TAG, "onCreate : enter" );
		
		db.execSQL( "CREATE TABLE location_profile (_id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, name TEXT, url TEXT, selected INTEGER default 0 );" );
		
		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		Log.v( TAG, "onUpgrade : enter" );

		Log.v( TAG, "onUpgrade : exit" );
	}

}
