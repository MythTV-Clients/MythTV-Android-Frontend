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
public class ProgramConstants implements BaseColumns {

	public enum ProgramType { RECORDED, UPCOMING, GUIDE, PREVIOUSLY_RECORDED };
	
	public static final String AUTHORITY = "org.mythtv.dvr.programs";
	public static final String TABLE_NAME = "program";
	
	public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/" + TABLE_NAME );

	public static final String INSERT_ROW, UPDATE_ROW;
	
	// db fields
	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_ID_PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";
	
	public static final String FIELD_PROGRAM_TYPE = "PROGRAM_TYPE";
	public static final String FIELD_PROGRAM_TYPE_DATA_TYPE = "TEXT";

	public static final String FIELD_PROGRAM_GROUP = "PROGRAM_GROUP";
	public static final String FIELD_PROGRAM_GROUP_DATA_TYPE = "TEXT";

	public static final String FIELD_START_DATE = "START_DATE";
	public static final String FIELD_START_DATE_DATA_TYPE = "TEXT";

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

	public static final String FIELD_CHANNEL_ID = "CHANNEL_ID";
	public static final String FIELD_CHANNEL_ID_DATA_TYPE = "TEXT";

	static {
		StringBuilder insert = new StringBuilder();
		
		insert.append( "INSERT INTO " ).append( TABLE_NAME ).append( " ( " );
		insert.append( FIELD_PROGRAM_TYPE ).append( "," );
		insert.append( FIELD_PROGRAM_GROUP ).append( "," );
		insert.append( FIELD_START_DATE ).append( "," );
		insert.append( FIELD_START_TIME ).append( "," );
		insert.append( FIELD_END_TIME ).append( "," );
		insert.append( FIELD_TITLE ).append( "," );
		insert.append( FIELD_SUB_TITLE ).append( "," );
		insert.append( FIELD_CATEGORY ).append( "," );
		insert.append( FIELD_CATEGORY_TYPE ).append( "," );
		insert.append( FIELD_REPEAT ).append( "," );
		insert.append( FIELD_VIDEO_PROPS ).append( "," );
		insert.append( FIELD_AUDIO_PROPS ).append( "," );
		insert.append( FIELD_SUB_PROPS ).append( "," );
		insert.append( FIELD_SERIES_ID ).append( "," );
		insert.append( FIELD_PROGRAM_ID ).append( "," );
		insert.append( FIELD_STARS ).append( "," );
		insert.append( FIELD_FILE_SIZE ).append( "," );
		insert.append( FIELD_LAST_MODIFIED ).append( "," );
		insert.append( FIELD_PROGRAM_FLAGS ).append( "," );
		insert.append( FIELD_HOSTNAME ).append( "," );
		insert.append( FIELD_FILENAME ).append( "," );
		insert.append( FIELD_AIR_DATE ).append( "," );
		insert.append( FIELD_DESCRIPTION ).append( "," );
		insert.append( FIELD_INETREF ).append( "," );
		insert.append( FIELD_SEASON ).append( "," );
		insert.append( FIELD_EPISODE ).append( "," );
		insert.append( FIELD_PROGRAM_GROUP_ID ).append( "," );
		insert.append( FIELD_CHANNEL_ID );
		insert.append( " ) " );
		insert.append( "VALUES( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )" );
		
		INSERT_ROW = insert.toString();
		
		StringBuilder update = new StringBuilder();
		update.append( "UPDATE " ).append( TABLE_NAME );
		update.append( " SET " );
		update.append( FIELD_PROGRAM_TYPE ).append( " = ?, " );
		update.append( FIELD_PROGRAM_GROUP ).append( " = ?, " );
		update.append( FIELD_START_DATE ).append( " = ?, " );
		update.append( FIELD_START_TIME ).append( " = ?, " );
		update.append( FIELD_END_TIME ).append( " = ?, " );
		update.append( FIELD_TITLE ).append( " = ?, " );
		update.append( FIELD_SUB_TITLE ).append( " = ?, " );
		update.append( FIELD_CATEGORY ).append( " = ?, " );
		update.append( FIELD_CATEGORY_TYPE ).append( " = ?, " );
		update.append( FIELD_REPEAT ).append( " = ?, " );
		update.append( FIELD_VIDEO_PROPS ).append( " = ?, " );
		update.append( FIELD_AUDIO_PROPS ).append( " = ?, " );
		update.append( FIELD_SUB_PROPS ).append( " = ?, " );
		update.append( FIELD_SERIES_ID ).append( " = ?, " );
		update.append( FIELD_PROGRAM_ID ).append( " = ?, " );
		update.append( FIELD_STARS ).append( " = ?, " );
		update.append( FIELD_FILE_SIZE ).append( " = ?, " );
		update.append( FIELD_LAST_MODIFIED ).append( " = ?, " );
		update.append( FIELD_PROGRAM_FLAGS ).append( " = ?, " );
		update.append( FIELD_HOSTNAME ).append( " = ?, " );
		update.append( FIELD_FILENAME ).append( " = ?, " );
		update.append( FIELD_AIR_DATE ).append( " = ?, " );
		update.append( FIELD_DESCRIPTION ).append( " = ?, " );
		update.append( FIELD_INETREF ).append( " = ?, " );
		update.append( FIELD_SEASON ).append( " = ?, " );
		update.append( FIELD_EPISODE ).append( " = ?, " );
		update.append( FIELD_PROGRAM_GROUP_ID ).append( " = ?, " );
		update.append( FIELD_CHANNEL_ID ).append( " = ?" );
		update.append( " WHERE " ).append( _ID ).append( " = ?" );
		
		UPDATE_ROW = update.toString();
	}
	
}
