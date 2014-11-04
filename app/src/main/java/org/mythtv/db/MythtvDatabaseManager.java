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

import static android.provider.BaseColumns._ID;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public class MythtvDatabaseManager {

	private static final String TAG = MythtvDatabaseManager.class.getSimpleName();

	private DatabaseHelper helper;
	private SQLiteDatabase db;

	/**
	 * @param context
	 */
	public MythtvDatabaseManager( final Context context ) {
		Log.v( TAG, "initialize : enter" );

		this.helper = new DatabaseHelper( context );

		Log.v( TAG, "initialize : exit" );
	}

	/**
	 * 
	 */
	private void open() {
		Log.v( TAG, "open : enter" );

		if( null != db ) {
			close();
		}

		db = helper.getWritableDatabase();

		Log.v( TAG, "open : exit" );
	}

	/**
	 * 
	 */
	private void close() {
		Log.v( TAG, "close : enter" );

		helper.close();
		db = null;

		Log.v( TAG, "open : exit" );

	}

	public Boolean fetchCleanupProgramGuide() {
		Log.v( TAG, "fetchCleanupProgramGuide : enter" );
		Log.v( TAG, "fetchCleanupProgramGuide : exit" );
		return fetchCleanupValueByKey( "CLEANUP_PROGRAM_GUIDE" );
	}

	public boolean updateCleanup( String key ) {
		Log.v( TAG, "updateCleanup : enter" );

		open();

		ContentValues args = new ContentValues();
		args.put( "VALUE", "false" );

		int rows = db.update( "CLEANUP", args, "KEY=?", new String[] { key } );
	
		close();

		Log.v( TAG, "updateCleanup : exit" );
		return rows > 0;
	}

	// internal helpers

	private Boolean fetchCleanupValueByKey( String key ) {
		Log.v( TAG, "fetchCleanupValueByKey : enter" );
		
		open();
		
		Boolean value = Boolean.FALSE;
		
		try {
			Cursor cursor = db.query( 
						"CLEANUP", 
						new String[] { _ID, "KEY", "VALUE" }, 
						"KEY=?", 
						new String[] { key }, 
						null, null, null 
					  );
			
			if( cursor.getCount() == 1 && cursor.moveToFirst() ) {
				Log.v( TAG, "fetchCleanupValueByKey : cleanup key found" );

				String sValue = cursor.getString( 2 );
				value = Boolean.valueOf( sValue );
			}
			
			cursor.close();
			
		} catch( SQLException e ) {
			Log.e( TAG, "fetchCleanupValueByKey : error", e );
		} finally {
			close();
		}
		
		Log.v( TAG, "fetchCleanupValueByKey : exit" );
		return value;
	}

}
