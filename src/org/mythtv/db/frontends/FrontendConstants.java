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
