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
package org.mythtv.db.preferences;

import org.mythtv.provider.MythtvProvider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Daniel Frey
 *
 */
public class PlaybackProfileConstants implements BaseColumns {

	public static final String TABLE_NAME = "playback_profile";
	
	public static final Uri CONTENT_URI = Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + TABLE_NAME );

	public static final String INSERT_ROW, UPDATE_ROW;

	// db fields
	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_ID_PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";
	
	public static final String FIELD_TYPE = "TYPE";
	public static final String FIELD_TYPE_DATA_TYPE = "TEXT";
	public static final String FIELD_TYPE_DEFAULT = "";

	public static final String FIELD_NAME = "NAME";
	public static final String FIELD_NAME_DATA_TYPE = "TEXT";
	public static final String FIELD_NAME_DEFAULT = "";
	
	public static final String FIELD_WIDTH = "WIDTH";
	public static final String FIELD_WIDTH_DATA_TYPE = "INTEGER";
	public static final String FIELD_WIDTH_DEFAULT = "";
	
	public static final String FIELD_HEIGHT = "HEIGHT";
	public static final String FIELD_HEIGHT_DATA_TYPE = "INTEGER";
	public static final String FIELD_HEIGHT_DEFAULT = "";
	
	public static final String FIELD_BITRATE = "BITRATE";
	public static final String FIELD_BITRATE_DATA_TYPE = "INTEGER";
	public static final String FIELD_BITRATE_DEFAULT = "800000";
	
	public static final String FIELD_AUDIO_BITRATE = "AUDIO_BITRATE";
	public static final String FIELD_AUDIO_BITRATE_DATA_TYPE = "INTEGER";
	public static final String FIELD_AUDIO_BITRATE_DEFAULT = "64000";
	
	public static final String FIELD_SAMPLE_RATE = "SAMPLE_RATE";
	public static final String FIELD_SAMPLE_RATE_DATA_TYPE = "INTEGER";
	public static final String FIELD_SAMPLE_RATE_DEFAULT = "44100";
	
	public static final String FIELD_SELECTED = "SELECTED";
	public static final String FIELD_SELECTED_DATA_TYPE = "INTEGER";
	public static final String FIELD_SELECTED_DEFAULT = "0";

	// queries
	public static final String SELECT_PLAYBACK_PROFILE =
			"select " +
				"pp._id, pp.type, pp.name, pp.width, pp.height, pp.bitrate, pp.audio_bitrate, pp.sample_rate, lp.selected " +
			"from " +
				"playback_profile pp";

	static {
		
		StringBuilder insert = new StringBuilder();
		
		insert.append( "INSERT INTO " ).append( TABLE_NAME ).append( " ( " );
		insert.append( FIELD_TYPE ).append( "," );
		insert.append( FIELD_NAME ).append( "," );
		insert.append( FIELD_WIDTH ).append( "," );
		insert.append( FIELD_HEIGHT ).append( "," );
		insert.append( FIELD_BITRATE ).append( "," );
		insert.append( FIELD_AUDIO_BITRATE ).append( "," );
		insert.append( FIELD_SAMPLE_RATE ).append( "," );
		insert.append( FIELD_SELECTED );
		insert.append( " ) " );
		insert.append( "VALUES( ?,?,?,?,?,?,?,? )" );
		
		INSERT_ROW = insert.toString();

		StringBuilder update = new StringBuilder();
		
		update.append( "UPDATE " ).append( TABLE_NAME ).append( " SET " );
		update.append( FIELD_TYPE ).append( " = ?, " );
		update.append( FIELD_NAME ).append( " = ?, " );
		update.append( FIELD_WIDTH ).append( " = ?, " );
		update.append( FIELD_HEIGHT ).append( " = ?, " );
		update.append( FIELD_BITRATE ).append( " = ?, " );
		update.append( FIELD_AUDIO_BITRATE ).append( " = ?, " );
		update.append( FIELD_SAMPLE_RATE ).append( " = ?, " );
		update.append( FIELD_SELECTED ).append( " = ? " );
		update.append( "WHERE " );
		update.append( _ID ).append( " = ?" );
		
		UPDATE_ROW = update.toString();

	}
	
}
