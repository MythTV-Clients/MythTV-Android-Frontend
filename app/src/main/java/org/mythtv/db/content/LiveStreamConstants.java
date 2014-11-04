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
package org.mythtv.db.content;

import org.mythtv.db.AbstractBaseConstants;
import org.mythtv.provider.MythtvProvider;

import android.net.Uri;

/**
 * @author Daniel Frey
 *
 */
public class LiveStreamConstants extends AbstractBaseConstants {

	public static final String TABLE_NAME = "live_stream";
	
	public static final Uri CONTENT_URI = Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + TABLE_NAME );

	public static final String INSERT_ROW;

	// db fields
	public static final String FIELD_ID = "ID";
	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_WIDTH = "WIDTH";
	public static final String FIELD_WIDTH_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_HEIGHT = "HEIGHT";
	public static final String FIELD_HEIGHT_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_BITRATE = "BITRATE";
	public static final String FIELD_BITRATE_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_AUDIO_BITRATE = "AUDIO_BITRATE";
	public static final String FIELD_AUDIO_BITRATE_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_SEGMENT_SIZE = "SEGMENT_SIZE";
	public static final String FIELD_SEGMENT_SIZE_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_MAX_SEGMENTS = "MAX_SEGMENTS";
	public static final String FIELD_MAX_SEGMENTS_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_START_SEGMENT = "START_SEGMENT";
	public static final String FIELD_START_SEGMENT_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_CURRENT_SEGMENT = "CURRENT_SEGMENT";
	public static final String FIELD_CURRENT_SEGMENT_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_SEGMENT_COUNT = "SEGMENT_COUNT";
	public static final String FIELD_SEGMENT_COUNT_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_PERCENT_COMPLETE = "PERCENT_COMPLETE";
	public static final String FIELD_PERCENT_COMPLETE_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_CREATED = "CREATED";
	public static final String FIELD_CREATED_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_LAST_MODIFIED = "LAST_MODIFIED";
	public static final String FIELD_LAST_MODIFIED_DATA_TYPE = "TEXT";
	
	public static final String FIELD_RELATIVE_URL = "RELATIVE_URL";
	public static final String FIELD_RELATIVE_URL_DATA_TYPE = "TEXT";
	
	public static final String FIELD_FULL_URL = "FULL_URL";
	public static final String FIELD_FULL_URL_DATA_TYPE = "TEXT";
	
	public static final String FIELD_STATUS_STR = "STATUS_STR";
	public static final String FIELD_STATUS_STR_DATA_TYPE = "TEXT";
	
	public static final String FIELD_STATUS_INT = "STATUS_INT";
	public static final String FIELD_STATUS_INT_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_STATUS_MESSAGE = "STATUS_MESSAGE";
	public static final String FIELD_STATUS_MESSAGE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_SOURCE_FILE = "SOURCE_FILE";
	public static final String FIELD_SOURCE_FILE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_SOURCE_HOST = "SOURCE_HOST";
	public static final String FIELD_SOURCE_HOST_DATA_TYPE = "TEXT";
	
	public static final String FIELD_SOURCE_WIDTH = "SOURCE_WIDTH";
	public static final String FIELD_SOURCE_WIDTH_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_SOURCE_HEIGHT = "SOURCE_HEIGHT";
	public static final String FIELD_SOURCE_HEIGHT_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_AUDIO_ONLY_BITRATE = "AUDIO_ONLY_BITRATE";
	public static final String FIELD_AUDIO_ONLY_BITRATE_DATA_TYPE = "INTEGER";

	// Program Key Fields
	public static final String FIELD_START_TIME = "START_TIME";
	public static final String FIELD_START_TIME_DATA_TYPE = "INTEGER";

	public static final String FIELD_CHAN_ID = "CHAN_ID";
	public static final String FIELD_CHAN_ID_DATA_TYPE = "INTEGER";

	
	public static final String[] COLUMN_MAP = { _ID,
		FIELD_ID, FIELD_WIDTH, FIELD_HEIGHT, FIELD_BITRATE, FIELD_AUDIO_BITRATE, FIELD_SEGMENT_SIZE, FIELD_MAX_SEGMENTS, FIELD_START_SEGMENT, FIELD_CURRENT_SEGMENT,
		FIELD_SEGMENT_COUNT, FIELD_PERCENT_COMPLETE, FIELD_CREATED, FIELD_LAST_MODIFIED, FIELD_RELATIVE_URL, FIELD_FULL_URL, FIELD_STATUS_STR,
		FIELD_STATUS_INT, FIELD_STATUS_MESSAGE, FIELD_SOURCE_FILE, FIELD_SOURCE_HOST, FIELD_SOURCE_WIDTH, FIELD_SOURCE_WIDTH, FIELD_SOURCE_HEIGHT,
		FIELD_AUDIO_ONLY_BITRATE, FIELD_START_TIME, FIELD_CHAN_ID
	};
	
	static {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "INSERT INTO " ).append( TABLE_NAME ).append( " ( " );
		sb.append( FIELD_ID ).append( "," );
		sb.append( FIELD_WIDTH ).append( "," );
		sb.append( FIELD_HEIGHT ).append( "," );
		sb.append( FIELD_BITRATE ).append( "," );
		sb.append( FIELD_AUDIO_BITRATE ).append( "," );
		sb.append( FIELD_SEGMENT_SIZE ).append( "," );
		sb.append( FIELD_MAX_SEGMENTS ).append( "," );
		sb.append( FIELD_START_SEGMENT ).append( "," );
		sb.append( FIELD_CURRENT_SEGMENT ).append( "," );
		sb.append( FIELD_SEGMENT_COUNT ).append( "," );
		sb.append( FIELD_PERCENT_COMPLETE ).append( "," );
		sb.append( FIELD_CREATED ).append( "," );
		sb.append( FIELD_LAST_MODIFIED ).append( "," );
		sb.append( FIELD_RELATIVE_URL ).append( "," );
		sb.append( FIELD_FULL_URL ).append( "," );
		sb.append( FIELD_STATUS_STR ).append( "," );
		sb.append( FIELD_STATUS_INT ).append( "," );
		sb.append( FIELD_STATUS_MESSAGE ).append( "," );
		sb.append( FIELD_SOURCE_FILE ).append( "," );
		sb.append( FIELD_SOURCE_HOST ).append( "," );
		sb.append( FIELD_SOURCE_WIDTH ).append( "," );
		sb.append( FIELD_SOURCE_HEIGHT ).append( "," );
		sb.append( FIELD_AUDIO_ONLY_BITRATE ).append( "," );
		sb.append( FIELD_START_TIME ).append( "," );
		sb.append( FIELD_CHAN_ID );
		sb.append( " ) " );
		sb.append( "VALUES( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )" );
		
		INSERT_ROW = sb.toString();
	}

}
