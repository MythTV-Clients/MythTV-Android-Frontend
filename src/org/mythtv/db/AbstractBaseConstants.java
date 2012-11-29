/**
 * 
 */
package org.mythtv.db;

import android.provider.BaseColumns;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractBaseConstants implements BaseColumns {

	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_ID_PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";
	
	public static final String FIELD_LOCATION_URL = "LOCATION_URL";
	public static final String FIELD_LOCATION_URL_DATA_TYPE = "TEXT";

}
