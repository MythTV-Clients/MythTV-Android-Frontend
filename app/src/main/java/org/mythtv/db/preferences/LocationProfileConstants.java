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
package org.mythtv.db.preferences;

import org.mythtv.provider.MythtvProvider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Daniel Frey
 *
 */
public class LocationProfileConstants implements BaseColumns {

	public static final String TABLE_NAME = "location_profile";
	
	public static final Uri CONTENT_URI = Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + TABLE_NAME );

	public static final String INSERT_ROW, UPDATE_ROW;

	// db fields
	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_ID_PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";
	
	public static final String FIELD_TYPE = "TYPE";
	public static final String FIELD_TYPE_DATA_TYPE = "TEXT";
	public static final String FIELD_TYPE_DEFAULT = "";

	public static final String FIELD_NAME = "NAME";
	public static final String FIELD_NAME_DATA_TYPE = "TEXT";
	public static final String FIELD_NAME_DEFAULT = "";
	
	public static final String FIELD_URL = "URL";
	public static final String FIELD_URL_DATA_TYPE = "TEXT";
	public static final String FIELD_URL_DEFAULT = "";
	
	public static final String FIELD_SELECTED = "SELECTED";
	public static final String FIELD_SELECTED_DATA_TYPE = "INTEGER";
	public static final String FIELD_SELECTED_DEFAULT = "0";
	
	public static final String FIELD_CONNECTED = "CONNECTED";
	public static final String FIELD_CONNECTED_DATA_TYPE = "INTEGER";
	public static final String FIELD_CONNECTED_DEFAULT = "0";

	public static final String FIELD_VERSION = "VERSION";
	public static final String FIELD_VERSION_DATA_TYPE = "TEXT";
	public static final String FIELD_VERSION_DEFAULT = "";

	public static final String FIELD_PROTOCOL_VERSION = "PROTOCOL_VERSION";
	public static final String FIELD_PROTOCOL_VERSION_DATA_TYPE = "TEXT";
	public static final String FIELD_PROTOCOL_VERSION_DEFAULT = "";

	public static final String FIELD_WOL_ADDRESS = "WOL_ADDRESS";
	public static final String FIELD_WOL_ADDRESS_DATA_TYPE = "TEXT";
	public static final String FIELD_WOL_ADDRESS_DEFAULT = "";

	public static final String FIELD_HOSTNAME = "HOSTNAME";
	public static final String FIELD_HOSTNAME_DATA_TYPE = "TEXT";
	public static final String FIELD_HOSTNAME_DEFAULT = "";

	public static final String FIELD_NEXT_MYTHFILLDATABASE = "NEXT_MYTHFILLDATABASE";
	public static final String FIELD_NEXT_MYTHFILLDATABASE_DATA_TYPE = "INTEGER";

	static {
	
		StringBuilder insert = new StringBuilder();
		
		insert.append( "INSERT INTO " ).append( TABLE_NAME ).append( " ( " );
		insert.append( FIELD_TYPE ).append( "," );
		insert.append( FIELD_NAME ).append( "," );
		insert.append( FIELD_URL ).append( "," );
		insert.append( FIELD_SELECTED ).append( "," );
		insert.append( FIELD_CONNECTED ).append( "," );
		insert.append( FIELD_VERSION ).append( "," );
		insert.append( FIELD_PROTOCOL_VERSION ).append( "," );
		insert.append( FIELD_WOL_ADDRESS ).append( "," );
		insert.append( FIELD_HOSTNAME );
		insert.append( FIELD_NEXT_MYTHFILLDATABASE ).append( "," );
		insert.append( " ) " );
		insert.append( "VALUES( ?,?,?,?,?,?,?,?,?,? )" );
		
		INSERT_ROW = insert.toString();

		StringBuilder update = new StringBuilder();
		
		update.append( "UPDATE " ).append( TABLE_NAME ).append( " SET " );
		update.append( FIELD_TYPE ).append( " = ?, " );
		update.append( FIELD_NAME ).append( " = ?, " );
		update.append( FIELD_URL ).append( " = ?, " );
		update.append( FIELD_SELECTED ).append( " = ?, " );
		update.append( FIELD_CONNECTED ).append( " = ?, " );
		update.append( FIELD_VERSION ).append( " = ?, " );
		update.append( FIELD_PROTOCOL_VERSION ).append( " = ?, " );
		update.append( FIELD_WOL_ADDRESS ).append( " = ?, " );
		update.append( FIELD_HOSTNAME ).append( " = ? " );
		update.append( FIELD_NEXT_MYTHFILLDATABASE ).append( " = ? " );
		update.append( "WHERE " );
		update.append( _ID ).append( " = ?" );
		
		UPDATE_ROW = update.toString();

	}
	
}
