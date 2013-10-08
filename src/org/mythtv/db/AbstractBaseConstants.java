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
package org.mythtv.db;

import android.provider.BaseColumns;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractBaseConstants implements BaseColumns {

	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_ID_PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";
	
	public static final String FIELD_MASTER_HOSTNAME = "MASTER_HOSTNAME";
	public static final String FIELD_MASTER_HOSTNAME_DATA_TYPE = "TEXT";

	public static final String FIELD_LAST_MODIFIED_DATE = "LAST_MODIFIED_DATE";
	public static final String FIELD_LAST_MODIFIED_DATE_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_LAST_MODIFIED_TAG = "LAST_MODIFIED_TAG";
	public static final String FIELD_LAST_MODIFIED_TAG_DATA_TYPE = "TEXT";
	
}
