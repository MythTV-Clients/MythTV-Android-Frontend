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
	
	public static final String FIELD_HOSTNAME = "HOSTNAME";
	public static final String FIELD_HOSTNAME_DATA_TYPE = "TEXT";

}
