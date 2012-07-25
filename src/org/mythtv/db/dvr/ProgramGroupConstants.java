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
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.db.dvr;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGroupConstants implements BaseColumns {

	public static final String AUTHORITY = "org.mythtv.dvr.programGroups";
	public static final String TABLE_NAME = "program_group";
	
	public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/" + TABLE_NAME );

	// db fields
	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_ID_PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";

	public static final String FIELD_PROGRAM_TYPE = "PROGRAM_TYPE";
	public static final String FIELD_PROGRAM_TYPE_DATA_TYPE = "TEXT";

	public static final String FIELD_PROGRAM_GROUP = "PROGRAM_GROUP";
	public static final String FIELD_PROGRAM_GROUP_DATA_TYPE = "TEXT";

	public static final String FIELD_PROGRAM_GROUP_SORT = "PROGRAM_GROUP_SORT";
	public static final String FIELD_PROGRAM_GROUP_SORT_DATA_TYPE = "TEXT";

	public static final String FIELD_INETREF = "INETREF";
	public static final String FIELD_INETREF_DATA_TYPE = "TEXT";

	public static final String FIELD_BANNER_URL = "BANNER_URL";
	public static final String FIELD_BANNER_URL_DATA_TYPE = "TEXT";

}
