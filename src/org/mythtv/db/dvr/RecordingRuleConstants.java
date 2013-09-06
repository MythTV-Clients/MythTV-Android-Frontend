/**
 * 
 */
package org.mythtv.db.dvr;

import org.mythtv.db.AbstractBaseConstants;
import org.mythtv.provider.MythtvProvider;

import android.net.Uri;

/**
 * @author Daniel Frey
 *
 */
public class RecordingRuleConstants extends AbstractBaseConstants {

	public static final String TABLE_NAME = "recording_rule";
	
	public static final Uri CONTENT_URI = Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + TABLE_NAME );

	public static final String INSERT_ROW;

	// db fields
	public static final String FIELD_REC_RULE_ID = "REC_RULE_ID";
	public static final String FIELD_REC_RULE_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_PARENT_ID = "PARENT_ID";
	public static final String FIELD_PARENT_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_INACTIVE = "INACTIVE";
	public static final String FIELD_INACTIVE_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_TITLE = "TITLE";
	public static final String FIELD_TITLE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_SUB_TITLE = "SUB_TITLE";
	public static final String FIELD_SUB_TITLE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_DESCRIPTION = "DESCRIPTION";
	public static final String FIELD_DESCRIPTION_DATA_TYPE = "TEXT";
	
	public static final String FIELD_SEASON = "SEASON";
	public static final String FIELD_SEASON_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_EPISODE = "EPISODE";
	public static final String FIELD_EPISODE_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_CATEGORY = "CATEGORY";
	public static final String FIELD_CATEGORY_DATA_TYPE = "TEXT";
	
	public static final String FIELD_START_TIME = "START_TIME";
	public static final String FIELD_START_TIME_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_END_TIME = "END_TIME";
	public static final String FIELD_END_TIME_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_SERIES_ID = "SERIES_ID";
	public static final String FIELD_SERIES_ID_DATA_TYPE = "TEXT";
	
	public static final String FIELD_PROGRAM_ID = "PROGRAM_ID";
	public static final String FIELD_PROGRAM_ID_DATA_TYPE = "TEXT";
	
	public static final String FIELD_INETREF = "INETREF";
	public static final String FIELD_INETREF_DATA_TYPE = "TEXT";
	
	public static final String FIELD_CHAN_ID = "CHAN_ID";
	public static final String FIELD_CHAN_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_CALLSIGN = "CALLSIGN";
	public static final String FIELD_CALLSIGN_DATA_TYPE = "TEXT";
	
	public static final String FIELD_DAY = "DAY";
	public static final String FIELD_DAY_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_FIND_DAY = "FIND_DAY";
	public static final String FIELD_FIND_DAY_DATA_TYPE = "INTEGER";

	public static final String FIELD_TIME = "TIME";
	public static final String FIELD_TIME_DATA_TYPE = "TEXT";
	
	public static final String FIELD_FIND_TIME = "FIND_TIME";
	public static final String FIELD_FIND_TIME_DATA_TYPE = "TEXT";
	
	public static final String FIELD_FIND_ID = "FIND_ID";
	public static final String FIELD_FIND_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_TYPE = "TYPE";
	public static final String FIELD_TYPE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_SEARCH_TYPE = "SEARCH_TYPE";
	public static final String FIELD_SEARCH_TYPE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_REC_PRIORITY = "REC_PRIORTIY";
	public static final String FIELD_REC_PRIORITY_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_PREFERRED_INPUT = "PREFERRED_INPUT";
	public static final String FIELD_PREFERRED_INPUT_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_START_OFFSET = "START_OFFSET";
	public static final String FIELD_START_OFFSET_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_END_OFFSET = "END_OFFSET";
	public static final String FIELD_END_OFFSET_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_DUP_METHOD = "DUP_METHOD";
	public static final String FIELD_DUP_METHOD_DATA_TYPE = "TEXT";
	
	public static final String FIELD_DUP_IN = "DUP_IN";
	public static final String FIELD_DUP_IN_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_FILTER = "FILTER";
	public static final String FIELD_FILTER_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_REC_PROFILE = "REC_PROFILE";
	public static final String FIELD_REC_PROFILE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_REC_GROUP = "REC_GROUP";
	public static final String FIELD_REC_GROUP_DATA_TYPE = "TEXT";
	
	public static final String FIELD_STORAGE_GROUP = "STORAGE_GROUP";
	public static final String FIELD_STORAGE_GROUP_DATA_TYPE = "TEXT";
	
	public static final String FIELD_PLAY_GROUP = "PLAY_GROUP";
	public static final String FIELD_PLAY_GROUP_DATA_TYPE = "TEXT";
	
	public static final String FIELD_AUTO_EXPIRE = "AUTO_EXPIRE";
	public static final String FIELD_AUTO_EXPIRE_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_MAX_EPISODES = "MAX_EPISODES";
	public static final String FIELD_MAX_EPISODES_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_MAX_NEWEST = "MAX_NEWEST";
	public static final String FIELD_MAX_NEWEST_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_AUTO_COMMFLAG = "AUTO_COMMFLAG";
	public static final String FIELD_AUTO_COMMFLAG_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_AUTO_TRANSCODE = "AUTO_TRANSCODE";
	public static final String FIELD_AUTO_TRANSCODE_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_AUTO_METADATA = "AUTO_METADATA";
	public static final String FIELD_AUTO_METADATA_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_AUTO_USER_JOB_1 = "AUTO_USER_JOB_1";
	public static final String FIELD_AUTO_USER_JOB_1_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_AUTO_USER_JOB_2 = "AUTO_USER_JOB_2";
	public static final String FIELD_AUTO_USER_JOB_2_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_AUTO_USER_JOB_3 = "AUTO_USER_JOB_3";
	public static final String FIELD_AUTO_USER_JOB_3_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_AUTO_USER_JOB_4 = "AUTO_USER_JOB_4";
	public static final String FIELD_AUTO_USER_JOB_4_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_TRANSCODER = "TRANSCODER";
	public static final String FIELD_TRANSCODER_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_NEXT_RECORDING = "NEXT_RECORDING";
	public static final String FIELD_NEXT_RECORDING_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_LAST_RECORDED = "LAST_RECORDED";
	public static final String FIELD_LAST_RECORDED_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_LAST_DELETED = "LAST_DELETED";
	public static final String FIELD_LAST_DELETED_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_AVERAGE_DELAY = "AVERAGE_DELAY";
	public static final String FIELD_AVERAGE_DELAY_DATA_TYPE = "INTEGER";
	
	public static final String[] COLUMN_MAP = { _ID,
		FIELD_REC_RULE_ID, FIELD_PARENT_ID, FIELD_INACTIVE, FIELD_TITLE, FIELD_SUB_TITLE, FIELD_DESCRIPTION, FIELD_SEASON, FIELD_EPISODE,
		FIELD_CATEGORY, FIELD_START_TIME, FIELD_END_TIME, FIELD_SERIES_ID, FIELD_PROGRAM_ID, FIELD_INETREF, FIELD_CHAN_ID, FIELD_CALLSIGN,
		FIELD_DAY, FIELD_FIND_DAY, FIELD_TIME, FIELD_FIND_TIME, FIELD_FIND_ID, FIELD_TYPE, FIELD_SEARCH_TYPE, FIELD_REC_PRIORITY, FIELD_PREFERRED_INPUT, FIELD_START_OFFSET,
		FIELD_END_OFFSET, FIELD_DUP_METHOD, FIELD_DUP_IN, FIELD_FILTER, FIELD_REC_PROFILE, FIELD_REC_GROUP, FIELD_STORAGE_GROUP,
		FIELD_PLAY_GROUP, FIELD_AUTO_EXPIRE, FIELD_MAX_EPISODES, FIELD_MAX_NEWEST, FIELD_AUTO_COMMFLAG, FIELD_AUTO_TRANSCODE,
		FIELD_AUTO_METADATA, FIELD_AUTO_USER_JOB_1, FIELD_AUTO_USER_JOB_2, FIELD_AUTO_USER_JOB_3, FIELD_AUTO_USER_JOB_4, FIELD_TRANSCODER,
		FIELD_NEXT_RECORDING, FIELD_LAST_RECORDED, FIELD_LAST_DELETED, FIELD_AVERAGE_DELAY,
		FIELD_MASTER_HOSTNAME, FIELD_LAST_MODIFIED_DATE
	};

	static {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "INSERT INTO " ).append( TABLE_NAME ).append( " ( " );
		sb.append( FIELD_REC_RULE_ID ).append( "," );
		sb.append( FIELD_PARENT_ID ).append( "," );
		sb.append( FIELD_INACTIVE ).append( "," );
		sb.append( FIELD_TITLE ).append( "," );
		sb.append( FIELD_SUB_TITLE ).append( "," );
		sb.append( FIELD_DESCRIPTION ).append( "," );
		sb.append( FIELD_SEASON ).append( "," );
		sb.append( FIELD_EPISODE ).append( "," );
		sb.append( FIELD_CATEGORY ).append( "," );
		sb.append( FIELD_START_TIME ).append( "," );
		sb.append( FIELD_END_TIME ).append( "," );
		sb.append( FIELD_SERIES_ID ).append( "," );
		sb.append( FIELD_PROGRAM_ID ).append( "," );
		sb.append( FIELD_INETREF ).append( "," );
		sb.append( FIELD_CHAN_ID ).append( "," );
		sb.append( FIELD_CALLSIGN ).append( "," );
		sb.append( FIELD_DAY ).append( "," );
		sb.append( FIELD_FIND_DAY ).append( "," );
		sb.append( FIELD_TIME ).append( "," );
		sb.append( FIELD_FIND_TIME ).append( "," );
		sb.append( FIELD_FIND_ID ).append( "," );
		sb.append( FIELD_TYPE ).append( "," );
		sb.append( FIELD_SEARCH_TYPE ).append( "," );
		sb.append( FIELD_REC_PRIORITY ).append( "," );
		sb.append( FIELD_PREFERRED_INPUT ).append( "," );
		sb.append( FIELD_START_OFFSET ).append( "," );
		sb.append( FIELD_END_OFFSET ).append( "," );
		sb.append( FIELD_DUP_METHOD ).append( "," );
		sb.append( FIELD_DUP_IN ).append( "," );
		sb.append( FIELD_FILTER ).append( "," );
		sb.append( FIELD_REC_PROFILE ).append( "," );
		sb.append( FIELD_REC_GROUP ).append( "," );
		sb.append( FIELD_STORAGE_GROUP ).append( "," );
		sb.append( FIELD_PLAY_GROUP ).append( "," );
		sb.append( FIELD_AUTO_EXPIRE ).append( "," );
		sb.append( FIELD_MAX_EPISODES ).append( "," );
		sb.append( FIELD_MAX_NEWEST ).append( "," );
		sb.append( FIELD_AUTO_COMMFLAG ).append( "," );
		sb.append( FIELD_AUTO_TRANSCODE ).append( "," );
		sb.append( FIELD_AUTO_METADATA ).append( "," );
		sb.append( FIELD_AUTO_USER_JOB_1 ).append( "," );
		sb.append( FIELD_AUTO_USER_JOB_2 ).append( "," );
		sb.append( FIELD_AUTO_USER_JOB_3 ).append( "," );
		sb.append( FIELD_AUTO_USER_JOB_4 ).append( "," );
		sb.append( FIELD_TRANSCODER ).append( "," );
		sb.append( FIELD_NEXT_RECORDING ).append( "," );
		sb.append( FIELD_LAST_RECORDED ).append( "," );
		sb.append( FIELD_LAST_DELETED ).append( "," );
		sb.append( FIELD_AVERAGE_DELAY ).append( "," );
		sb.append( FIELD_MASTER_HOSTNAME ).append( "," );
		sb.append( FIELD_LAST_MODIFIED_DATE );
		sb.append( " ) " );
		sb.append( "VALUES( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )" );

		INSERT_ROW = sb.toString();
	}
	
}
