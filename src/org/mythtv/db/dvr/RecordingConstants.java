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

import org.mythtv.provider.MythtvProvider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Daniel Frey
 *
 */
public class RecordingConstants implements BaseColumns {

	public static final String TABLE_NAME = "recording";
	
	public static final Uri CONTENT_URI = Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + TABLE_NAME );

	// db fields
	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_ID_PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";

	public static final String FIELD_STATUS = "STATUS";
	public static final String FIELD_STATUS_DATA_TYPE = "INTEGER";

	public static final String FIELD_PRIORITY = "PRIORITY";
	public static final String FIELD_PRIORITY_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_START_TS = "START_TS";
	public static final String FIELD_START_TS_DATA_TYPE = "TEXT";
	
	public static final String FIELD_END_TS = "END_TS";
	public static final String FIELD_END_TS_DATA_TYPE = "TEXT";
	
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

}
