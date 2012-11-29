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
package org.mythtv.db.http;

import org.mythtv.db.AbstractBaseConstants;
import org.mythtv.provider.MythtvProvider;

import android.net.Uri;

/**
 * @author Daniel Frey
 *
 */
public class EtagConstants extends AbstractBaseConstants {

	public static final String TABLE_NAME = "etag";

	public static final Uri CONTENT_URI = Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + TABLE_NAME );

	public static final String INSERT_ETAG_ROW, UPDATE_ETAG_ROW;
	
	// db fields
	public static final String FIELD_ENDPOINT = "ENDPOINT";
	public static final String FIELD_ENDPOINT_DATA_TYPE = "TEXT NOT NULL";

	public static final String FIELD_VALUE = "VALUE";
	public static final String FIELD_VALUE_DATA_TYPE = "TEXT NOT NULL";

	public static final String FIELD_DATA_ID = "DATA_ID";
	public static final String FIELD_DATA_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_DATA_ID_DEFAULT = "0";

	public static final String FIELD_DATE = "DATE";
	public static final String FIELD_DATE_DATA_TYPE = "INTEGER";
	

	static {
		StringBuilder insert = new StringBuilder();
		insert.append( FIELD_VALUE ).append( "," );
		insert.append( FIELD_DATA_ID ).append( "," );
		insert.append( FIELD_DATE ).append( "," );
		insert.append( FIELD_LOCATION_URL );
		
		StringBuilder values = new StringBuilder();
		values.append( " ) " );
		values.append( "VALUES( ?,?,?,? )" );
		
		StringBuilder insertEtag = new StringBuilder();
		insertEtag.append( "INSERT INTO " ).append( TABLE_NAME ).append( " ( " );
		insertEtag.append( insert.toString() );
		insertEtag.append( values.toString() );
		INSERT_ETAG_ROW = insertEtag.toString();
		
		StringBuilder update = new StringBuilder();
		update.append( FIELD_VALUE ).append( " = ?, " );
		update.append( FIELD_DATA_ID ).append( " = ?, " );
		update.append( FIELD_DATE ).append( " = ?, " );
		update.append( FIELD_LOCATION_URL ).append( " = ?" );
		update.append( " WHERE " );
		update.append( _ID ).append( " = ?" );
		
		StringBuilder updateEtag = new StringBuilder();
		updateEtag.append( "UPDATE " ).append( TABLE_NAME );
		updateEtag.append( " SET " );
		updateEtag.append( update.toString() );
		UPDATE_ETAG_ROW = updateEtag.toString();
		
	}
	
}
