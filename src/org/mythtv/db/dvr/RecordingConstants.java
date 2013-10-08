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

import java.util.HashMap;
import java.util.Map;

import org.mythtv.db.AbstractBaseConstants;
import org.mythtv.provider.MythtvProvider;

import android.net.Uri;

/**
 * @author Daniel Frey
 *
 */
public class RecordingConstants  extends AbstractBaseConstants {

	public static enum ContentDetails{
		GUIDE( "guide", "recording_guide" ),
		RECORDED( "recorded", "recording_recorded" ),
		UPCOMING( "upcoming", "recording_upcoming" );
		
		private String parent;
		private String tableName;
		
		ContentDetails( String parent, String tableName ) {
			this.parent = parent;
			this.tableName = tableName;
		}
		
		public String getParent() {
			return parent;
		}
		
		public String getTableName() {
			return tableName;
		}
		
		public Uri getContentUri() {
			return Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + getTableName() );
		}
		
		private static Map<String, ContentDetails> parentMap;
		public static ContentDetails getValueFromParent( String parent ) {
			
			if( null == parentMap ) {
				
				parentMap = new HashMap<String, ContentDetails>();
				
				for( ContentDetails details : values() ) {
					parentMap.put( details.parent, details );
				}
			}
			
			return parentMap.get( parent );
		}
		
		private static Map<String, ContentDetails> tableNameMap;
		public static ContentDetails getValueFromTableName( String tableName ) {
			
			if( null == tableNameMap ) {
				
				tableNameMap = new HashMap<String, ContentDetails>();
				
				for( ContentDetails details : values() ) {
					tableNameMap.put( details.tableName, details );
				}
			}
			
			return tableNameMap.get( tableName );
		}

	};
	
	public static final String INSERT_RECORDING_GUIDE_ROW, UPDATE_RECORDING_GUIDE_ROW, INSERT_RECORDING_RECORDED_ROW, UPDATE_RECORDING_RECORDED_ROW, INSERT_RECORDING_UPCOMING_ROW, UPDATE_RECORDING_UPCOMING_ROW;

	// db fields
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
	
	public static final String FIELD_PROGRAM_ID = "PROGRAM_ID";
	public static final String FIELD_PROGRAM_ID_DATA_TYPE = "INTEGER";

	public static final String FIELD_START_TIME = "START_TIME";
	public static final String FIELD_START_TIME_DATA_TYPE = "INTEGER";

	public static final String[] COLUMN_MAP = { _ID,
		FIELD_STATUS, FIELD_PRIORITY, FIELD_START_TS, FIELD_END_TS, FIELD_RECORD_ID, FIELD_REC_GROUP, FIELD_PLAY_GROUP, 
		FIELD_STORAGE_GROUP, FIELD_REC_TYPE, FIELD_DUP_IN_TYPE, FIELD_DUP_METHOD, FIELD_ENCODER_ID, FIELD_PROFILE, FIELD_PROGRAM_ID, 
		FIELD_START_TIME, FIELD_MASTER_HOSTNAME, FIELD_LAST_MODIFIED_DATE, FIELD_LAST_MODIFIED_TAG
	};

	static {
		
		StringBuilder insert = new StringBuilder();
		insert.append( FIELD_STATUS ).append( "," );
		insert.append( FIELD_PRIORITY ).append( "," );
		insert.append( FIELD_START_TS ).append( "," );
		insert.append( FIELD_END_TS ).append( "," );
		insert.append( FIELD_RECORD_ID ).append( "," );
		insert.append( FIELD_REC_GROUP ).append( "," );
		insert.append( FIELD_PLAY_GROUP ).append( "," );
		insert.append( FIELD_STORAGE_GROUP ).append( "," );
		insert.append( FIELD_REC_TYPE ).append( "," );
		insert.append( FIELD_DUP_IN_TYPE ).append( "," );
		insert.append( FIELD_DUP_METHOD ).append( "," );
		insert.append( FIELD_ENCODER_ID ).append( "," );
		insert.append( FIELD_PROFILE ).append( "," );
		insert.append( FIELD_PROGRAM_ID ).append( "," );
		insert.append( FIELD_START_TIME ).append( "," );
		insert.append( FIELD_MASTER_HOSTNAME ).append( "," );
		insert.append( FIELD_LAST_MODIFIED_DATE ).append( ", " );
		insert.append( FIELD_LAST_MODIFIED_TAG );
		
		StringBuilder values = new StringBuilder();
		values.append( " ) " );
		values.append( "VALUES( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )" );
		
		StringBuilder insertRecordingGuide = new StringBuilder();
		insertRecordingGuide.append( "INSERT INTO " ).append( ContentDetails.GUIDE.getTableName() ).append( " ( " );
		insertRecordingGuide.append( insert.toString() );
		insertRecordingGuide.append( values.toString() );
		INSERT_RECORDING_GUIDE_ROW = insertRecordingGuide.toString();
		
		StringBuilder insertRecordingRecorded = new StringBuilder();
		insertRecordingRecorded.append( "INSERT INTO " ).append( ContentDetails.RECORDED.getTableName() ).append( " ( " );
		insertRecordingRecorded.append( insert.toString() );
		insertRecordingRecorded.append( values.toString() );
		INSERT_RECORDING_RECORDED_ROW = insertRecordingRecorded.toString();
		
		StringBuilder insertRecordingUpcoming = new StringBuilder();
		insertRecordingUpcoming.append( "INSERT INTO " ).append( ContentDetails.UPCOMING.getTableName() ).append( " ( " );
		insertRecordingUpcoming.append( insert.toString() );
		insertRecordingUpcoming.append( values.toString() );
		INSERT_RECORDING_UPCOMING_ROW = insertRecordingUpcoming.toString();
		
		StringBuilder update = new StringBuilder();
		update.append( FIELD_STATUS ).append( " = ?, " );
		update.append( FIELD_PRIORITY ).append( " = ?, " );
		update.append( FIELD_START_TS ).append( " = ?, " );
		update.append( FIELD_END_TS ).append( " = ?, " );
		update.append( FIELD_RECORD_ID ).append( " = ?, " );
		update.append( FIELD_REC_GROUP ).append( " = ?, " );
		update.append( FIELD_PLAY_GROUP ).append( " = ?, " );
		update.append( FIELD_STORAGE_GROUP ).append( " = ?, " );
		update.append( FIELD_REC_TYPE ).append( " = ?, " );
		update.append( FIELD_DUP_IN_TYPE ).append( " = ?, " );
		update.append( FIELD_DUP_METHOD ).append( " = ?, " );
		update.append( FIELD_ENCODER_ID ).append( " = ?, " );
		update.append( FIELD_PROFILE ).append( " = ?, " );
		update.append( FIELD_PROGRAM_ID ).append( " = ?, " );
		update.append( FIELD_START_TIME ).append( " = ?, " );
		update.append( FIELD_MASTER_HOSTNAME ).append( " = ?, " );
		update.append( FIELD_LAST_MODIFIED_DATE ).append( "= ?, " );
		update.append( FIELD_LAST_MODIFIED_TAG ).append( "= ?" );
		update.append( " WHERE " );
		update.append( _ID ).append( " = ?" );
		
		StringBuilder updateRecordingGuide = new StringBuilder();
		updateRecordingGuide.append( "UPDATE " ).append( ContentDetails.GUIDE.getTableName() );
		updateRecordingGuide.append( " SET " );
		updateRecordingGuide.append( update.toString() );
		UPDATE_RECORDING_GUIDE_ROW = updateRecordingGuide.toString();
		
		StringBuilder updateRecordingRecorded = new StringBuilder();
		updateRecordingRecorded.append( "UPDATE " ).append( ContentDetails.RECORDED.getTableName() );
		updateRecordingRecorded.append( " SET " );
		updateRecordingRecorded.append( update.toString() );
		UPDATE_RECORDING_RECORDED_ROW = updateRecordingRecorded.toString();
		
		StringBuilder updateRecordingUpcoming = new StringBuilder();
		updateRecordingUpcoming.append( "UPDATE " ).append( ContentDetails.UPCOMING.getTableName() );
		updateRecordingUpcoming.append( " SET " );
		updateRecordingUpcoming.append( update.toString() );
		UPDATE_RECORDING_UPCOMING_ROW = updateRecordingUpcoming.toString();
		
	}

}
