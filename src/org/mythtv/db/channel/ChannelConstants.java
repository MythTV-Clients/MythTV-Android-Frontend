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
package org.mythtv.db.channel;

import org.mythtv.db.AbstractBaseConstants;
import org.mythtv.provider.MythtvProvider;

import android.net.Uri;

/**
 * @author Daniel Frey
 *
 */
public class ChannelConstants extends AbstractBaseConstants {

	public static final String TABLE_NAME = "channel";
	
	public static final Uri CONTENT_URI = Uri.parse( "content://" + MythtvProvider.AUTHORITY + "/" + TABLE_NAME );

	public static final String INSERT_ROW;
	
	// db fields
	public static final String FIELD_CHAN_ID = "CHAN_ID";
	public static final String FIELD_CHAN_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_CHAN_NUM = "CHAN_NUM";
	public static final String FIELD_CHAN_NUM_DATA_TYPE = "TEXT";
	
	public static final String FIELD_CHAN_NUM_FORMATTED = "CHAN_NUM_FORMATTED";
	public static final String FIELD_CHAN_NUM_FORMATTED_DATA_TYPE = "TEXT";
	
	public static final String FIELD_CALLSIGN = "CALLSIGN";
	public static final String FIELD_CALLSIGN_DATA_TYPE = "TEXT";
	
	public static final String FIELD_ICON_URL = "ICON_URL";
	public static final String FIELD_ICON_URL_DATA_TYPE = "TEXT";
	
	public static final String FIELD_CHANNEL_NAME = "CHANNEL_NAME";
	public static final String FIELD_CHANNEL_NAME_DATA_TYPE = "TEXT";
	
	public static final String FIELD_MPLEX_ID = "MPLEX_ID";
	public static final String FIELD_MPLEX_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_TRANSPORT_ID = "TRANSPORT_ID";
	public static final String FIELD_TRANSPORT_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_SERVICE_ID = "SERVICE_ID";
	public static final String FIELD_SERVICE_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_NETWORK_ID = "NETWORK_ID";
	public static final String FIELD_NETWORK_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_ATSC_MAJOR_CHAN = "ATSC_MAJOR_CHAN";
	public static final String FIELD_ATSC_MAJOR_CHAN_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_ATSC_MINOR_CHAN = "ATSC_MINOR_CHAN";
	public static final String FIELD_ATSC_MINOR_CHAN_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_FORMAT = "FORMAT";
	public static final String FIELD_FORMAT_DATA_TYPE = "TEXT";
	
	public static final String FIELD_MODULATION = "MODULATION";
	public static final String FIELD_MODULATION_DATA_TYPE = "TEXT";
	
	public static final String FIELD_FREQUENCY = "FREQUENCY";
	public static final String FIELD_FREQUENCY_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_FREQUENCY_ID = "FREQUENCY_ID";
	public static final String FIELD_FREQUENCY_ID_DATA_TYPE = "TEXT";
	
	public static final String FIELD_FREQUENCY_TABLE = "FREQUENCY_TABLE";
	public static final String FIELD_FREQUENCY_TABLE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_FINE_TUNE = "FINE_TUNE";
	public static final String FIELD_FINE_TUNE_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_SIS_STANDARD = "SIS_STANDARD";
	public static final String FIELD_SIS_STANDARD_DATA_TYPE = "TEXT";
	
	public static final String FIELD_CHAN_FILTERS = "CHAN_FILTERS";
	public static final String FIELD_CHAN_FILTERS_DATA_TYPE = "TEXT";
	
	public static final String FIELD_SOURCE_ID = "SOURCE_ID";
	public static final String FIELD_SOURCE_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_INPUT_ID = "INPUT_ID";
	public static final String FIELD_INPUT_ID_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_COMM_FREE = "COMM_FREE";
	public static final String FIELD_COMM_FREE_DATA_TYPE = "INTEGER";
	
	public static final String FIELD_USE_EIT = "USE_EIT";
	public static final String FIELD_USE_EIT_DATA_TYPE = "INTEGER";
	public static final String FIELD_USE_EIT_DEFAULT = "0";
	
	public static final String FIELD_VISIBLE = "VISIBLE";
	public static final String FIELD_VISIBLE_DATA_TYPE = "INTEGER";
	public static final String FIELD_VISIBLE_DEFAULT = "0";
	
	public static final String FIELD_XMLTV_ID = "XMLTV_ID";
	public static final String FIELD_XMLTV_ID_DATA_TYPE = "TEXT";

	public static final String FIELD_DEFAULT_AUTH = "DEFAULT_AUTH";
	public static final String FIELD_DEFAULT_AUTH_DATA_TYPE = "TEXT";

	public static final String[] COLUMN_MAP = { _ID,
		FIELD_CHAN_ID, FIELD_CHAN_NUM, FIELD_CHAN_NUM_FORMATTED, FIELD_CALLSIGN, FIELD_ICON_URL, FIELD_CHANNEL_NAME, FIELD_MPLEX_ID, FIELD_TRANSPORT_ID, FIELD_SERVICE_ID,
		FIELD_NETWORK_ID, FIELD_ATSC_MAJOR_CHAN, FIELD_ATSC_MINOR_CHAN, FIELD_FORMAT, FIELD_MODULATION, FIELD_FREQUENCY, FIELD_FREQUENCY_ID,
		FIELD_FREQUENCY_TABLE, FIELD_FINE_TUNE, FIELD_SIS_STANDARD, FIELD_CHAN_FILTERS, FIELD_SOURCE_ID, FIELD_INPUT_ID, FIELD_SOURCE_ID,
		FIELD_INPUT_ID, FIELD_COMM_FREE, FIELD_USE_EIT, FIELD_VISIBLE, FIELD_XMLTV_ID, FIELD_DEFAULT_AUTH, FIELD_LAST_MODIFIED_DATE
	};
	
	static {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "INSERT INTO " ).append( TABLE_NAME ).append( " ( " );
		sb.append( FIELD_CHAN_ID ).append( "," );
		sb.append( FIELD_CHAN_NUM ).append( "," );
		sb.append( FIELD_CHAN_NUM_FORMATTED ).append( "," );
		sb.append( FIELD_CALLSIGN ).append( "," );
		sb.append( FIELD_ICON_URL ).append( "," );
		sb.append( FIELD_CHANNEL_NAME ).append( "," );
		sb.append( FIELD_MPLEX_ID ).append( "," );
		sb.append( FIELD_TRANSPORT_ID ).append( "," );
		sb.append( FIELD_SERVICE_ID ).append( "," );
		sb.append( FIELD_NETWORK_ID ).append( "," );
		sb.append( FIELD_ATSC_MAJOR_CHAN ).append( "," );
		sb.append( FIELD_ATSC_MINOR_CHAN ).append( "," );
		sb.append( FIELD_FORMAT ).append( "," );
		sb.append( FIELD_MODULATION ).append( "," );
		sb.append( FIELD_FREQUENCY ).append( "," );
		sb.append( FIELD_FREQUENCY_ID ).append( "," );
		sb.append( FIELD_FREQUENCY_TABLE ).append( "," );
		sb.append( FIELD_FINE_TUNE ).append( "," );
		sb.append( FIELD_SIS_STANDARD ).append( "," );
		sb.append( FIELD_CHAN_FILTERS ).append( "," );
		sb.append( FIELD_SOURCE_ID ).append( "," );
		sb.append( FIELD_INPUT_ID ).append( "," );
		sb.append( FIELD_COMM_FREE ).append( "," );
		sb.append( FIELD_USE_EIT ).append( "," );
		sb.append( FIELD_VISIBLE ).append( "," );
		sb.append( FIELD_XMLTV_ID ).append( "," );
		sb.append( FIELD_DEFAULT_AUTH ).append( "," );
		sb.append( FIELD_MASTER_HOSTNAME ).append( "," );
		sb.append( FIELD_LAST_MODIFIED_DATE );
		sb.append( " ) " );
		sb.append( "VALUES( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )" );
		
		INSERT_ROW = sb.toString();
	}

}
