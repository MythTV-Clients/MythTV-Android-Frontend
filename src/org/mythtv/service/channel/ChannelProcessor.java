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

import java.util.ArrayList;
import java.util.List;

import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.service.AbstractMythtvProcessor;
import org.mythtv.services.api.channel.ChannelInfo;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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

	public Long updateChannelContentProvider( ChannelInfo channel ) {
//		Log.v( TAG, "updateChannelContentProvider : enter" );
		
		if( null != channel ) {
			
			if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
				Log.v( TAG, "updateChannelContentProvider : channelInfo=" + channel.toString() );
			}
			
			ContentValues values = new ContentValues();
			values.put( ChannelConstants.FIELD_CHAN_ID, channel.getChannelId() );
			values.put( ChannelConstants.FIELD_CHAN_NUM, channel.getChannelNumber() );
			values.put( ChannelConstants.FIELD_CALLSIGN, channel.getCallSign() );
			values.put( ChannelConstants.FIELD_ICON_URL, channel.getIconUrl() );
			values.put( ChannelConstants.FIELD_CHANNEL_NAME, channel.getChannelName() );
			values.put( ChannelConstants.FIELD_MPLEX_ID, channel.getMultiplexId() );
			values.put( ChannelConstants.FIELD_TRANSPORT_ID, channel.getTransportId() );
			values.put( ChannelConstants.FIELD_SERVICE_ID, channel.getServiceId() );
			values.put( ChannelConstants.FIELD_NETWORK_ID, channel.getNetworkId() );
			values.put( ChannelConstants.FIELD_ATSC_MAJOR_CHAN, channel.getAtscMajorChannel() );
			values.put( ChannelConstants.FIELD_ATSC_MINOR_CHAN, channel.getAtscMinorChannel() );
			values.put( ChannelConstants.FIELD_FORMAT, channel.getFormat() );
			values.put( ChannelConstants.FIELD_MODULATION, channel.getModulation() );
			values.put( ChannelConstants.FIELD_FREQUENCY, channel.getFrequency() );
			values.put( ChannelConstants.FIELD_FREQUENCY_ID, channel.getFrequencyId() );
			values.put( ChannelConstants.FIELD_FREQUENCY_TABLE, channel.getFrequenceTable() );
			values.put( ChannelConstants.FIELD_FINE_TUNE, channel.getFineTune() );
			values.put( ChannelConstants.FIELD_SIS_STANDARD, channel.getSiStandard() );
			values.put( ChannelConstants.FIELD_CHAN_FILTERS, channel.getChannelFilters() );
			values.put( ChannelConstants.FIELD_SOURCE_ID, channel.getSourceId() );
			values.put( ChannelConstants.FIELD_INPUT_ID, channel.getInputId() );
			values.put( ChannelConstants.FIELD_COMM_FREE, channel.getCommercialFree() );
			values.put( ChannelConstants.FIELD_USE_EIT, channel.isUseEit() );
			values.put( ChannelConstants.FIELD_VISIBLE, channel.isVisable() );
			values.put( ChannelConstants.FIELD_XMLTV_ID, channel.getXmltvId() );
			values.put( ChannelConstants.FIELD_DEFAULT_AUTH, channel.getDefaultAuth() );

			long id = 0;
			Cursor cursor = mContext.getContentResolver().query( ChannelConstants.CONTENT_URI, null, ChannelConstants.FIELD_CHAN_ID + " = ? and " + ChannelConstants.FIELD_SOURCE_ID + " = ?", new String[] { "" + channel.getChannelId(), "" + channel.getSourceId() }, null );
			if( cursor.moveToFirst() ) {
				id = cursor.getInt( cursor.getColumnIndexOrThrow( ChannelConstants._ID ) );
			} else {
				Uri contentUri = mContext.getContentResolver().insert( ChannelConstants.CONTENT_URI, values );
				id = ContentUris.parseId( contentUri );
			}
			cursor.close();
			
			return id;
		}
		
		Log.v( TAG, "updateChannelContentProvider : exit, channel info is empty" );
		return null;
	}

	public Long batchUpdateChannelContentProvider( List<ChannelInfo> channels ) {
		
		long numberInserted = 0;
		
		if( null != channels && !channels.isEmpty() ) {

			List<String> lChannels = new ArrayList<String>();

			Cursor cursor = mContext.getContentResolver().query( ChannelConstants.CONTENT_URI, new String[] { ChannelConstants._ID }, null, null, null );
			if( cursor.getCount() == 0 ) {
				List<ChannelInfo> filtered = new ArrayList<ChannelInfo>();
				for( ChannelInfo channel : channels ) {
					if( !lChannels.contains( channel.getChannelNumber() ) ) {
						if( channel.isVisable() ) {
							filtered.add( channel );
						
							lChannels.add( channel.getChannelNumber() );
						}
					}
				}
				
				int count = 0;
				ContentValues values;
				ContentValues[] valuesArray = new ContentValues[ filtered.size() ];
				for( ChannelInfo channel : filtered ) {
					values = new ContentValues();
					values.put( ChannelConstants.FIELD_CHAN_ID, null != channel.getChannelId() ? channel.getChannelId() : "" );
					values.put( ChannelConstants.FIELD_CHAN_NUM, channel.getChannelNumber() );
					values.put( ChannelConstants.FIELD_CALLSIGN, channel.getCallSign() );
					values.put( ChannelConstants.FIELD_ICON_URL, channel.getIconUrl() );
					values.put( ChannelConstants.FIELD_CHANNEL_NAME, channel.getChannelName() );
					values.put( ChannelConstants.FIELD_MPLEX_ID, channel.getMultiplexId() );
					values.put( ChannelConstants.FIELD_TRANSPORT_ID, channel.getTransportId() );
					values.put( ChannelConstants.FIELD_SERVICE_ID, channel.getServiceId() );
					values.put( ChannelConstants.FIELD_NETWORK_ID, channel.getNetworkId() );
					values.put( ChannelConstants.FIELD_ATSC_MAJOR_CHAN, channel.getAtscMajorChannel() );
					values.put( ChannelConstants.FIELD_ATSC_MINOR_CHAN, channel.getAtscMinorChannel() );
					values.put( ChannelConstants.FIELD_FORMAT, channel.getFormat() );
					values.put( ChannelConstants.FIELD_MODULATION, channel.getModulation() );
					values.put( ChannelConstants.FIELD_FREQUENCY, channel.getFrequency() );
					values.put( ChannelConstants.FIELD_FREQUENCY_ID, channel.getFrequencyId() );
					values.put( ChannelConstants.FIELD_FREQUENCY_TABLE, channel.getFrequenceTable() );
					values.put( ChannelConstants.FIELD_FINE_TUNE, channel.getFineTune() );
					values.put( ChannelConstants.FIELD_SIS_STANDARD, channel.getSiStandard() );
					values.put( ChannelConstants.FIELD_CHAN_FILTERS, channel.getChannelFilters() );
					values.put( ChannelConstants.FIELD_SOURCE_ID, channel.getSourceId() );
					values.put( ChannelConstants.FIELD_INPUT_ID, channel.getInputId() );
					values.put( ChannelConstants.FIELD_COMM_FREE, channel.getCommercialFree() );
					values.put( ChannelConstants.FIELD_USE_EIT, channel.isUseEit() ? 1 : 0 );
					values.put( ChannelConstants.FIELD_VISIBLE, channel.isVisable() ? 1 : 0 );
					values.put( ChannelConstants.FIELD_XMLTV_ID, channel.getXmltvId() );
					values.put( ChannelConstants.FIELD_DEFAULT_AUTH, channel.getDefaultAuth() );
					valuesArray[ count ] = values;

					count++;

				}
				numberInserted = mContext.getContentResolver().bulkInsert( ChannelConstants.CONTENT_URI, valuesArray );
			}
			cursor.close();
		}

		return numberInserted;
	}
	
}
