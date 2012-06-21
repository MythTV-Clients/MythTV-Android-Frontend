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
 * @author John Baab <rhpot1991@ubuntu.com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */

package org.mythtv.db.preferences;

/**
 * @author Daniel Frey
 *
 */
public class LocationProfileConstants {

	public static final String TABLE_NAME = "LOCATION_PROFILE";
	
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
	
	// queries
	public static final String SELECT_LOCATION_PROFILE =
			"select " +
				"lp._id, lp.type, lp.name, lp.url, lp.selected " +
			"from " +
				"location_profile lp";

}
