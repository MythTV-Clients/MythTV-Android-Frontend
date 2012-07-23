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
package org.mythtv.service.channel;

import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.service.AbstractMythtvProcessor;
import org.mythtv.services.api.channel.ChannelInfo;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
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
		Log.v( TAG, "updateChannelContentProvider : enter" );
		
		if( null != channel ) {
			
			Log.v( TAG, "updateChannelContentProvider : channelInfo=" + channel.toString() );
				
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
				id = cursor.getInt( cursor.getColumnIndexOrThrow( BaseColumns._ID ) );
			} else {
				Uri contentUri = mContext.getContentResolver().insert( ChannelConstants.CONTENT_URI, values );
				id = ContentUris.parseId( contentUri );
			}
			cursor.close();
			
			Log.v( TAG, "updateChannelContentProvider : exit" );
			return id;
		}
		
		Log.v( TAG, "updateChannelContentProvider : exit, channel info is empty" );
		return null;
	}

}
