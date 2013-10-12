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
/**
 * 
 */
package org.mythtv.db.frontends;

import org.mythtv.db.AbstractBaseConstants;
import org.mythtv.provider.MythtvProvider;

import android.net.Uri;

/**
 * @author dmfrey
 *
 */
public class FrontendConstants extends AbstractBaseConstants {

	public static final String TABLE_NAME = "frontends";

	public static final Uri CONTENT_URI = Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + TABLE_NAME );

	public static final String INSERT_ROW, UPDATE_ROW;

	// db fields
	public static final String FIELD_NAME = "NAME";
	public static final String FIELD_NAME_DATA_TYPE = "TEXT";

	public static final String FIELD_URL = "URL";
	public static final String FIELD_URL_DATA_TYPE = "TEXT";
	
	public static final String FIELD_AVAILABLE = "AVAILABLE";
	public static final String FIELD_AVAILABLE_DATA_TYPE = "INTEGER";

	public static final String[] COLUMN_MAP = { _ID,
		FIELD_NAME, FIELD_URL, FIELD_AVAILABLE,
		FIELD_MASTER_HOSTNAME, FIELD_LAST_MODIFIED_DATE
	};

	static {
		StringBuilder insert = new StringBuilder();
		insert.append( FIELD_NAME ).append( "," );
		insert.append( FIELD_URL ).append( "," );
		insert.append( FIELD_AVAILABLE ).append( "," );
		insert.append( FIELD_MASTER_HOSTNAME ).append( ", " );
		insert.append( FIELD_LAST_MODIFIED_DATE );
		
		StringBuilder values = new StringBuilder();
		values.append( " ) " );
		values.append( "VALUES( ?,?,?,?,? )" );
		
		StringBuilder insertFrontend = new StringBuilder();
		insertFrontend.append( "INSERT INTO " ).append( TABLE_NAME ).append( " ( " );
		insertFrontend.append( insert.toString() );
		insertFrontend.append( values.toString() );
		INSERT_ROW = insertFrontend.toString();
		
		StringBuilder update = new StringBuilder();
		update.append( FIELD_NAME ).append( " = ?, " );
		update.append( FIELD_URL ).append( " = ?, " );
		update.append( FIELD_AVAILABLE ).append( " = ?, " );
		update.append( FIELD_MASTER_HOSTNAME ).append( "= ?," );
		update.append( FIELD_LAST_MODIFIED_DATE ).append( "= ?" );
		update.append( " WHERE " );
		update.append( _ID ).append( " = ?" );
		
		StringBuilder updateFrontend = new StringBuilder();
		updateFrontend.append( "UPDATE " ).append( TABLE_NAME );
		updateFrontend.append( " SET " );
		updateFrontend.append( update.toString() );
		UPDATE_ROW = updateFrontend.toString();
		
	}

}
