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
package org.mythtv.service.channel;

import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.service.AbstractMythtvProcessor;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.channel.ChannelInfos;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ChannelProcessor extends AbstractMythtvProcessor {

	protected static final String TAG = ChannelProcessor.class.getSimpleName();

	public ChannelProcessor( Context context ) {
		super( context );
		Log.v( TAG, "initialize : enter" );

		Log.v( TAG, "initialize : exit" );
	}

	public int deleteChannels() {
		Log.v( TAG, "deleteChannels : enter" );
		
		int deleted = mContext.getContentResolver().delete( ChannelConstants.CONTENT_URI, null, null );
		Log.v( TAG, "deleteChannels : channels deleted=" + deleted );

		Log.v( TAG, "deleteChannels : exit" );
		return deleted;
	}
	
	public int processChannels( ChannelInfos channelInfos ) {
		Log.v( TAG, "processChannels : enter" );

		int result = 0;
		
		if( null != channelInfos ) {
			
			ContentValues[] contentValuesArray = convertChannelsToContentValuesArray( channelInfos );
			result = mContext.getContentResolver().bulkInsert( ChannelConstants.CONTENT_URI, contentValuesArray );
			Log.v( TAG, "processChannels : channels added=" + result );
		}
		
		Log.v( TAG, "processChannels : exit" );
		return result;
	}

	// internal helpers
	
	private ContentValues[] convertChannelsToContentValuesArray( final ChannelInfos channelInfos ) {
		
		if( null != channelInfos ) {
			
			int i = 0;
			ContentValues contentValues;
			ContentValues[] contentValuesArray = new ContentValues[ channelInfos.getChannelInfos().size() ];
			for( ChannelInfo channelInfo : channelInfos.getChannelInfos() ) {
				
				contentValues = convertChannelToContentValues( channelInfo );
				contentValuesArray[ i ] = contentValues;
				
				i++;
			}
			
			return contentValuesArray;
		}
		
		return null;
	}

	private ContentValues convertChannelToContentValues( ChannelInfo channelInfo ) {
		
		ContentValues values = new ContentValues();
		values.put( ChannelConstants.FIELD_CHAN_ID, channelInfo.getChannelId() );
		values.put( ChannelConstants.FIELD_CHAN_NUM, channelInfo.getChannelNumber() );
		values.put( ChannelConstants.FIELD_CALLSIGN, channelInfo.getCallSign() );
		values.put( ChannelConstants.FIELD_ICON_URL, channelInfo.getIconUrl() );
		values.put( ChannelConstants.FIELD_CHANNEL_NAME, channelInfo.getChannelName() );
		values.put( ChannelConstants.FIELD_MPLEX_ID, channelInfo.getMultiplexId() );
		values.put( ChannelConstants.FIELD_TRANSPORT_ID, channelInfo.getTransportId() );
		values.put( ChannelConstants.FIELD_SERVICE_ID, channelInfo.getServiceId() );
		values.put( ChannelConstants.FIELD_NETWORK_ID, channelInfo.getNetworkId() );
		values.put( ChannelConstants.FIELD_ATSC_MAJOR_CHAN, channelInfo.getAtscMajorChannel() );
		values.put( ChannelConstants.FIELD_ATSC_MINOR_CHAN, channelInfo.getAtscMinorChannel() );
		values.put( ChannelConstants.FIELD_FORMAT, channelInfo.getFormat() );
		values.put( ChannelConstants.FIELD_MODULATION, channelInfo.getModulation() );
		values.put( ChannelConstants.FIELD_FREQUENCY, channelInfo.getFrequency() );
		values.put( ChannelConstants.FIELD_FREQUENCY_ID, channelInfo.getFrequencyId() );
		values.put( ChannelConstants.FIELD_FREQUENCY_TABLE, channelInfo.getFrequenceTable() );
		values.put( ChannelConstants.FIELD_FINE_TUNE, channelInfo.getFineTune() );
		values.put( ChannelConstants.FIELD_SIS_STANDARD, channelInfo.getSiStandard() );
		values.put( ChannelConstants.FIELD_CHAN_FILTERS, channelInfo.getChannelFilters() );
		values.put( ChannelConstants.FIELD_SOURCE_ID, channelInfo.getSourceId() );
		values.put( ChannelConstants.FIELD_INPUT_ID, channelInfo.getInputId() );
		values.put( ChannelConstants.FIELD_COMM_FREE, channelInfo.getCommercialFree() );
		values.put( ChannelConstants.FIELD_USE_EIT, ( channelInfo.isUseEit() ? 1 : 0 ) );
		values.put( ChannelConstants.FIELD_VISIBLE, ( channelInfo.isVisable() ? 1 : 0 ) );
		values.put( ChannelConstants.FIELD_XMLTV_ID, channelInfo.getXmltvId() );
		values.put( ChannelConstants.FIELD_DEFAULT_AUTH, channelInfo.getDefaultAuth() );

		return values;
	}
	
}
