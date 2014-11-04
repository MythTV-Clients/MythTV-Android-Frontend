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
package org.mythtv.db.status;

import org.mythtv.provider.MythtvProvider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Daniel Frey
 *
 */
public class StatusConstants implements BaseColumns {

	public static final String TABLE_NAME = "status";

	public static final Uri CONTENT_URI = Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + TABLE_NAME );

	public static final String INSERT_STATUS_ROW, UPDATE_STATUS_ROW;
	
	public static enum StatusKey {
		MASTER_BACKEND_CONNECTED
	}
	
	// db fields
	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_ID_PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";
	
	public static final String FIELD_KEY = "KEY";
	public static final String FIELD_KEY_DATA_TYPE = "TEXT NOT NULL";

	public static final String FIELD_VALUE = "VALUE";
	public static final String FIELD_VALUE_DATA_TYPE = "TEXT NOT NULL";

	public static final String FIELD_DATE = "DATE";
	public static final String FIELD_DATE_DATA_TYPE = "INTEGER";
	

	static {
		StringBuilder insert = new StringBuilder();
		insert.append( FIELD_KEY ).append( "," );
		insert.append( FIELD_VALUE ).append( "," );
		insert.append( FIELD_DATE );
		
		StringBuilder values = new StringBuilder();
		values.append( " ) " );
		values.append( "VALUES( ?,?,? )" );
		
		StringBuilder insertStatus = new StringBuilder();
		insertStatus.append( "INSERT INTO " ).append( TABLE_NAME ).append( " ( " );
		insertStatus.append( insert.toString() );
		insertStatus.append( values.toString() );
		INSERT_STATUS_ROW = insertStatus.toString();
		
		StringBuilder update = new StringBuilder();
		update.append( FIELD_KEY ).append( " = ?, " );
		update.append( FIELD_VALUE ).append( " = ?, " );
		update.append( FIELD_DATE ).append( " = ?" );
		update.append( " WHERE " );
		update.append( _ID ).append( " = ?" );
		
		StringBuilder updateStatus = new StringBuilder();
		updateStatus.append( "UPDATE " ).append( TABLE_NAME );
		updateStatus.append( " SET " );
		updateStatus.append( update.toString() );
		UPDATE_STATUS_ROW = updateStatus.toString();
		
	}
	
}
