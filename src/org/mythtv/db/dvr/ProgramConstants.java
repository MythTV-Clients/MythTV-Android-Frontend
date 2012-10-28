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
package org.mythtv.db.dvr;

import org.mythtv.provider.MythtvProvider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Daniel Frey
 *
 */
public class ProgramConstants implements BaseColumns {

	public static final String TABLE_NAME_PROGRAM = "program";
	public static final String TABLE_NAME_RECORDED = "recorded";
	public static final String TABLE_NAME_UPCOMING = "upcoming";

	public static final Uri CONTENT_URI_PROGRAM = Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + TABLE_NAME_PROGRAM );
	public static final Uri CONTENT_URI_RECORDED = Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + TABLE_NAME_RECORDED );
	public static final Uri CONTENT_URI_UPCOMING = Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + TABLE_NAME_UPCOMING );

	public static final String INSERT_PROGRAM_ROW, UPDATE_PROGRAM_ROW, INSERT_RECORDED_ROW, UPDATE_RECORDED_ROW, INSERT_UPCOMING_ROW, UPDATE_UPCOMING_ROW;
	
	// db fields
	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_ID_PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";
	
	public static final String FIELD_PROGRAM_GROUP = "PROGRAM_GROUP";
	public static final String FIELD_PROGRAM_GROUP_DATA_TYPE = "TEXT";

	public static final String FIELD_START_TIME = "START_TIME";
	public static final String FIELD_START_TIME_DATA_TYPE = "INTEGER";

	public static final String FIELD_END_TIME = "END_TIME";
	public static final String FIELD_END_TIME_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_DURATION = "DURATION";
	public static final String FIELD_DURATION_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_START_DATE = "START_DATE";
	public static final String FIELD_START_DATE_DATA_TYPE = "TEXT";

	public static final String FIELD_TIMESLOT_HOUR = "TIMESLOT_HOUR";
	public static final String FIELD_TIMESLOT_HOUR_DATA_TYPE = "TEXT";

	public static final String FIELD_TIMESLOT_MINUTE = "TIMESLOT";
	public static final String FIELD_TIMESLOT_MINUTE_DATA_TYPE = "TEXT";

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

	public static final String FIELD_CHANNEL_ID = "CHANNEL_ID";
	public static final String FIELD_CHANNEL_ID_DATA_TYPE = "TEXT";

	public static final String FIELD_CHANNEL_NUMBER = "CHANNEL_NUMBER";
	public static final String FIELD_CHANNEL_NUMBER_DATA_TYPE = "TEXT";

	public static final String FIELD_STATUS = "STATUS";
	public static final String FIELD_STATUS_DATA_TYPE = "INTEGER";

	public static final String FIELD_PRIORITY = "PRIORITY";
	public static final String FIELD_PRIORITY_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_START_TS = "START_TS";
	public static final String FIELD_START_TS_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_END_TS = "END_TS";
	public static final String FIELD_END_TS_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_RECORD_ID = "RECORD_ID";
	public static final String FIELD_RECORD_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_REC_GROUP = "REC_GROUP";
	public static final String FIELD_REC_GROUP_DATA_TYPE = "TEXT";
	
	public static final String FIELD_PLAY_GROUP = "PLAY_GROUP";
	public static final String FIELD_PLAY_GROUP_DATA_TYPE = "TEXT";
	
	public static final String FIELD_STORAGE_GROUP = "STORAGE_GROUP";
	public static final String FIELD_STORAGE_GROUP_DATA_TYPE = "TEXT";
	
	public static final String FIELD_REC_TYPE = "REC_TYPE";
	public static final String FIELD_REC_TYPE_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_DUP_IN_TYPE = "DUP_IN_TYPE";
	public static final String FIELD_DUP_IN_TYPE_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_DUP_METHOD = "DUP_METHOD";
	public static final String FIELD_DUP_METHOD_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_ENCODER_ID = "ENCODER_ID";
	public static final String FIELD_ENCODER_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_PROFILE = "PROFILE";
	public static final String FIELD_PROFILE_DATA_TYPE = "TEXT";

	static {
		StringBuilder insert = new StringBuilder();
		insert.append( FIELD_PROGRAM_GROUP ).append( "," );
		insert.append( FIELD_START_TIME ).append( "," );
		insert.append( FIELD_END_TIME ).append( "," );
		insert.append( FIELD_DURATION ).append( "," );
		insert.append( FIELD_START_DATE ).append( "," );
		insert.append( FIELD_TIMESLOT_HOUR ).append( "," );
		insert.append( FIELD_TIMESLOT_MINUTE ).append( "," );
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
		insert.append( FIELD_CHANNEL_ID ).append( ", " );
		insert.append( FIELD_CHANNEL_NUMBER ).append( ", " );
		insert.append( FIELD_STATUS ).append( ", " );
		insert.append( FIELD_PRIORITY ).append( ", " );
		insert.append( FIELD_START_TS ).append( ", " );
		insert.append( FIELD_END_TS ).append( ", " );
		insert.append( FIELD_RECORD_ID ).append( ", " );
		insert.append( FIELD_REC_GROUP ).append( ", " );
		insert.append( FIELD_PLAY_GROUP ).append( ", " );
		insert.append( FIELD_STORAGE_GROUP ).append( ", " );
		insert.append( FIELD_REC_TYPE ).append( ", " );
		insert.append( FIELD_DUP_IN_TYPE ).append( ", " );
		insert.append( FIELD_DUP_METHOD ).append( ", " );
		insert.append( FIELD_ENCODER_ID ).append( ", " );
		insert.append( FIELD_PROFILE );

		
		StringBuilder values = new StringBuilder();
		values.append( " ) " );
		values.append( "VALUES( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )" );
		
		StringBuilder insertProgram = new StringBuilder();
		insertProgram.append( "INSERT INTO " ).append( TABLE_NAME_PROGRAM ).append( " ( " );
		insertProgram.append( insert.toString() );
		insertProgram.append( values.toString() );
		INSERT_PROGRAM_ROW = insertProgram.toString();
		
		StringBuilder insertRecorded = new StringBuilder();
		insertRecorded.append( "INSERT INTO " ).append( TABLE_NAME_RECORDED ).append( " ( " );
		insertRecorded.append( insert.toString() );
		insertRecorded.append( values.toString() );
		INSERT_RECORDED_ROW = insertRecorded.toString();
		
		StringBuilder insertUpcoming = new StringBuilder();
		insertUpcoming.append( "INSERT INTO " ).append( TABLE_NAME_UPCOMING ).append( " ( " );
		insertUpcoming.append( insert.toString() );
		insertUpcoming.append( values.toString() );
		INSERT_UPCOMING_ROW = insertUpcoming.toString();
		
		StringBuilder update = new StringBuilder();
		update.append( FIELD_PROGRAM_GROUP ).append( " = ?, " );
		update.append( FIELD_END_TIME ).append( " = ?, " );
		update.append( FIELD_DURATION ).append( " = ?, " );
		update.append( FIELD_START_DATE ).append( " = ?, " );
		update.append( FIELD_TIMESLOT_HOUR ).append( " = ?, " );
		update.append( FIELD_TIMESLOT_MINUTE ).append( " = ?, " );
		update.append( FIELD_TITLE ).append( " = ?, " );
		update.append( FIELD_SUB_TITLE ).append( " = ?, " );
		update.append( FIELD_CATEGORY ).append( " = ?, " );
		update.append( FIELD_CATEGORY_TYPE ).append( " = ?, " );
		update.append( FIELD_REPEAT ).append( " = ?, " );
		update.append( FIELD_VIDEO_PROPS ).append( " = ?, " );
		update.append( FIELD_AUDIO_PROPS ).append( " = ?, " );
		update.append( FIELD_SUB_PROPS ).append( " = ?, " );
		update.append( FIELD_SERIES_ID ).append( " = ?, " );
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
		update.append( FIELD_CHANNEL_ID ).append( " = ?" );
		update.append( FIELD_CHANNEL_NUMBER ).append( " = ?" );
		update.append( FIELD_STATUS ).append( " = ?" );
		update.append( FIELD_PRIORITY ).append( " = ?" );
		update.append( FIELD_START_TS ).append( " = ?" );
		update.append( FIELD_END_TS ).append( " = ?" );
		update.append( FIELD_RECORD_ID ).append( " = ?" );
		update.append( FIELD_REC_GROUP ).append( " = ?" );
		update.append( FIELD_PLAY_GROUP ).append( " = ?" );
		update.append( FIELD_STORAGE_GROUP ).append( " = ?" );
		update.append( FIELD_REC_TYPE ).append( " = ?" );
		update.append( FIELD_DUP_IN_TYPE ).append( " = ?" );
		update.append( FIELD_DUP_METHOD ).append( " = ?" );
		update.append( FIELD_ENCODER_ID ).append( " = ?" );
		update.append( FIELD_PROFILE ).append( " = ?" );
		update.append( " WHERE " );
		update.append( FIELD_PROGRAM_ID ).append( " = ? AND " );
		update.append( FIELD_START_TIME ).append( " = ?" );
		
		StringBuilder updateProgram = new StringBuilder();
		updateProgram.append( "UPDATE " ).append( TABLE_NAME_PROGRAM );
		updateProgram.append( " SET " );
		updateProgram.append( update.toString() );
		UPDATE_PROGRAM_ROW = updateProgram.toString();
		
		StringBuilder updateRecorded = new StringBuilder();
		updateRecorded.append( "UPDATE " ).append( TABLE_NAME_RECORDED );
		updateRecorded.append( " SET " );
		updateRecorded.append( update.toString() );
		UPDATE_RECORDED_ROW = updateRecorded.toString();
		
		StringBuilder updateUpcoming = new StringBuilder();
		updateUpcoming.append( "UPDATE " ).append( TABLE_NAME_UPCOMING );
		updateUpcoming.append( " SET " );
		updateUpcoming.append( update.toString() );
		UPDATE_UPCOMING_ROW = updateUpcoming.toString();
	}
	
}
