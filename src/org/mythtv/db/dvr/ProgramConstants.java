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

/**
 * @author Daniel Frey
 *
 */
public class ProgramConstants {

	public static final String AUTHORITY = "org.mythtv.dvr.programs";
	public static final String TABLE_NAME = "program";
	
	public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/" + TABLE_NAME );

	// db fields
	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_ID_PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";
	
	public static final String FIELD_START_TIME = "START_TIME";
	public static final String FIELD_START_TIME_DATA_TYPE = "TEXT";

	public static final String FIELD_END_TIME = "END_TIME";
	public static final String FIELD_END_TIME_DATA_TYPE = "TEXT";
	
	public static final String FIELD_TITLE = "TITLE";
	public static final String FIELD_TITLE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_SUB_TITLE = "SUB_TITLE";
	public static final String FIELD_SUB_TITLE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_CATEGORY = "CATEGORY";
	public static final String FIELD_CATEGORY_DATA_TYPE = "TEXT";
	
	public static final String FIELD_CATEGORY_TYPE = "CATEGORY_TYPE";
	public static final String FIELD_CATEGORY_TYPE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_REPEAT = "REPEAT";
	public static final String FIELD_REPEAT_DATA_TYPE = "INTEGER";
	public static final String FIELD_REPEAT_DEFAULT = "0";
	
	public static final String FIELD_VIDEO_PROPS = "VIDEO_PROPS";
	public static final String FIELD_VIDEO_PROPS_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_AUDIO_PROPS = "AUDIO_PROPS";
	public static final String FIELD_AUDIO_PROPS_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_SUB_PROPS = "SUB_PROPS";
	public static final String FIELD_SUB_PROPS_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_SERIES_ID = "SERIES_ID";
	public static final String FIELD_SERIES_ID_DATA_TYPE = "TEXT";
	
	public static final String FIELD_PROGRAM_ID = "PROGRAM_ID";
	public static final String FIELD_PROGRAM_ID_DATA_TYPE = "TEXT";
	
	public static final String FIELD_STARS = "STARS";
	public static final String FIELD_STARS_DATA_TYPE = "REAL";
	
	public static final String FIELD_FILE_SIZE = "FILE_SIZE";
	public static final String FIELD_FILE_SIZE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_LAST_MODIFIED = "LAST_MODIFIED";
	public static final String FIELD_LAST_MODIFIED_DATA_TYPE = "TEXT";
	
	public static final String FIELD_PROGRAM_FLAGS = "PROGRAM_FLAGS";
	public static final String FIELD_PROGRAM_FLAGS_DATA_TYPE = "TEXT";
	
	public static final String FIELD_HOSTNAME = "HOSTNAME";
	public static final String FIELD_HOSTNAME_DATA_TYPE = "TEXT";
	
	public static final String FIELD_FILENAME = "FILENAME";
	public static final String FIELD_FILENAME_DATA_TYPE = "TEXT";
	
	public static final String FIELD_AIR_DATE = "AIR_DATE";
	public static final String FIELD_AIR_DATE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_DESCRIPTION = "DESCRIPTION";
	public static final String FIELD_DESCRIPTION_DATA_TYPE = "TEXT";
	
	public static final String FIELD_INETREF = "INETREF";
	public static final String FIELD_INETREF_DATA_TYPE = "TEXT";
	
	public static final String FIELD_SEASON = "SEASON";
	public static final String FIELD_SEASON_DATA_TYPE = "TEXT";
	
	public static final String FIELD_EPISODE = "EPISODE";
	public static final String FIELD_EPISODE_DATA_TYPE = "TEXT";

	public static final String FIELD_PROGRAM_GROUP_ID = "PROGRAM_GROUP_ID";
	public static final String FIELD_PROGRAM_GROUP_ID_DATA_TYPE = "INTEGER";

}
