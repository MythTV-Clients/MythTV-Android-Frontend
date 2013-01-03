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

import java.util.ArrayList;
import java.util.List;

import org.mythtv.db.AbstractDaoHelper;
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
public class ChannelDaoHelper extends AbstractDaoHelper {

	private static final String TAG = ChannelDaoHelper.class.getSimpleName();
	
	/**
	 * @param context
	 */
	public ChannelDaoHelper( Context context ) {
		super( context );
	}

	/**
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public List<ChannelInfo> findAll( String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.d( TAG, "findAll : enter" );
		
		List<ChannelInfo> channelInfos = new ArrayList<ChannelInfo>();
		
		selection = appendLocationHostname( selection, ChannelConstants.TABLE_NAME );
		
		Cursor cursor = mContext.getContentResolver().query( ChannelConstants.CONTENT_URI, projection, selection, selectionArgs, sortOrder );
		while( cursor.moveToNext() ) {
			ChannelInfo channelInfo = convertCursorToChannelInfo( cursor );
			channelInfos.add( channelInfo );
		}
		cursor.close();

		Log.d( TAG, "findAll : exit" );
		return channelInfos;
	}
	
	/**
	 * @return
	 */
	public List<ChannelInfo> finalAll() {
		Log.d( TAG, "findAll : enter" );
		
		List<ChannelInfo> channelInfos = findAll( null, null, null, null );
		
		Log.d( TAG, "findAll : exit" );
		return channelInfos;
	}
	
	/**
	 * @param id
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public ChannelInfo findOne( Long id, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.d( TAG, "findOne : enter" );
		
		ChannelInfo channelInfo = null;
		
		Uri uri = ChannelConstants.CONTENT_URI;
		if( null != id && id > 0 ) {
			Log.d( TAG, "findOne : appending id=" + id );
			uri = ContentUris.withAppendedId( ChannelConstants.CONTENT_URI, id );
		}
		
		selection = appendLocationHostname( selection, ChannelConstants.TABLE_NAME );
		
		Cursor cursor = mContext.getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );
		if( cursor.moveToFirst() ) {
			channelInfo = convertCursorToChannelInfo( cursor );
		}
		cursor.close();
		
		Log.d( TAG, "findOne : exit" );
		return channelInfo;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public ChannelInfo findOne( Long id ) {
		Log.d( TAG, "findOne : enter" );
		
		ChannelInfo channelInfo = findOne( id, null, null, null, null );
		if( null != channelInfo ) {
			Log.v( TAG, "findOne : channelInfo=" + channelInfo.toString() );
		}
		
		
		Log.d( TAG, "findOne : exit" );
		return channelInfo;
	}

	/**
	 * @param channelId
	 * @return
	 */
	public ChannelInfo findByChannelId( Long channelId ) {
		Log.d( TAG, "findByChannelId : enter" );
		
		String selection = ChannelConstants.FIELD_CHAN_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( channelId ) };
		
		ChannelInfo channelInfo = findOne( null, null, selection, selectionArgs, null );
		if( null != channelInfo ) {
			Log.v( TAG, "findByChannelId : channelInfo=" + channelInfo.toString() );
		}
				
		Log.d( TAG, "findByChannelId : exit" );
		return channelInfo;
	}

	/**
	 * @param channelInfo
	 * @return
	 */
	public int save( ChannelInfo channelInfo ) {
		Log.d( TAG, "save : enter" );

		ContentValues values = convertChannelInfoToContentValues( channelInfo );

		String[] projection = new String[] { ChannelConstants._ID };
		String selection = ChannelConstants.FIELD_CHAN_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( channelInfo.getChannelId() ) };
		
		selection = appendLocationHostname( selection, ChannelConstants.TABLE_NAME );
		
		int updated = -1;
		Cursor cursor = mContext.getContentResolver().query( ChannelConstants.CONTENT_URI, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
			Log.v( TAG, "save : updating existing channel info" );
			long id = cursor.getLong( cursor.getColumnIndexOrThrow( ChannelConstants._ID ) );
			
			updated = mContext.getContentResolver().update( ContentUris.withAppendedId( ChannelConstants.CONTENT_URI, id ), values, null, null );
		}
		cursor.close();
		Log.v( TAG, "save : updated=" + updated );

		Log.d( TAG, "save : exit" );
		return updated;
	}

	/**
	 * @return
	 */
	public int deleteAll() {
		Log.d( TAG, "deleteAll : enter" );
		
		int deleted = mContext.getContentResolver().delete( ChannelConstants.CONTENT_URI, null, null );
		Log.v( TAG, "deleteAll : deleted=" + deleted );
		
		Log.d( TAG, "deleteAll : exit" );
		return deleted;
	}

	/**
	 * @param id
	 * @return
	 */
	public int delete( Long id ) {
		Log.d( TAG, "delete : enter" );
		
		int deleted = mContext.getContentResolver().delete( ContentUris.withAppendedId( ChannelConstants.CONTENT_URI, id ), null, null );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param channelInfo
	 * @return
	 */
	public int delete( ChannelInfo channelInfo ) {
		Log.d( TAG, "delete : enter" );
		
		String selection = ChannelConstants.FIELD_CHAN_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( channelInfo.getChannelId() ) };
		
		selection = appendLocationHostname( selection, ChannelConstants.TABLE_NAME );
		
		int deleted = mContext.getContentResolver().delete( ChannelConstants.CONTENT_URI, selection, selectionArgs );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	public int load( List<ChannelInfo> channelInfos ) {
		Log.d( TAG, "load : enter" );
		
		int loaded = -1;
		
		ContentValues[] contentValuesArray = convertChannelInfosToContentValuesArray( channelInfos );
		if( null != contentValuesArray ) {
			Log.v( TAG, "processChannels : channels=" + contentValuesArray.length );

			loaded = mContext.getContentResolver().bulkInsert( ChannelConstants.CONTENT_URI, contentValuesArray );
			Log.v( TAG, "load : loaded=" + loaded );
		}
		
		
		Log.d( TAG, "load : exit" );
		return loaded;
	}
	
	/**
	 * @param cursor
	 * @return
	 */
	public ChannelInfo convertCursorToChannelInfo( Cursor cursor ) {
//		Log.v( TAG, "convertCursorToChannelInfo : enter" );

		int  channelId = -1, multiplexId = -1, transportId = -1, serviceId = -1, networkId = -1, atscMajorChannel = -1, atscMinorChannel = -1, frequency = -1, fineTune = -1, sourceId = -1, inputId = -1, commercialFree = -1, useEit = -1, visible = -1;
		String channelNumber = "", callsign = "", iconUrl = "", channelName = "", format = "", modulation = "", frequencyId = "", frequencyTable = "", sisStandard = "", channelFilters = "", xmltvId = "", defaultAuth = "";
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CHAN_ID ) != -1 ) {
			channelId = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CHAN_ID ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CHAN_NUM ) != -1 ) {
			channelNumber = cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CHAN_NUM ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CALLSIGN ) != -1 ) {
			callsign = cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CALLSIGN ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_ICON_URL ) != -1 ) {
			iconUrl = cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_ICON_URL ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CHANNEL_NAME ) != -1 ) {
			channelName = cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CHANNEL_NAME ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_MPLEX_ID ) != -1 ) {
			multiplexId = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_MPLEX_ID ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_TRANSPORT_ID ) != -1 ) {
			transportId = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_TRANSPORT_ID ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_SERVICE_ID ) != -1 ) {
			serviceId = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_SERVICE_ID ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_NETWORK_ID ) != -1 ) {
			networkId = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_NETWORK_ID ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_ATSC_MAJOR_CHAN ) != -1 ) {
			atscMajorChannel = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_ATSC_MAJOR_CHAN ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_ATSC_MINOR_CHAN ) != -1 ) {
			atscMinorChannel = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_ATSC_MINOR_CHAN ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_FORMAT ) != -1 ) {
			format = cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_FORMAT ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_MODULATION ) != -1 ) {
			modulation = cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_MODULATION ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_FREQUENCY ) != -1 ) {
			frequency = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_FREQUENCY ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_FREQUENCY_ID ) != -1 ) {
			frequencyId = cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_FREQUENCY_ID ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_FREQUENCY_TABLE ) != -1 ) {
			frequencyTable = cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_FREQUENCY_TABLE ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_FINE_TUNE ) != -1 ) {
			fineTune = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_FINE_TUNE ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_SIS_STANDARD ) != -1 ) {
			sisStandard = cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_SIS_STANDARD ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CHAN_FILTERS ) != -1 ) {
			channelFilters = cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CHAN_FILTERS ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_SOURCE_ID ) != -1 ) {
			sourceId = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_SOURCE_ID ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_INPUT_ID ) != -1 ) {
			inputId = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_INPUT_ID ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_COMM_FREE ) != -1 ) {
			commercialFree = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_COMM_FREE ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_USE_EIT ) != -1 ) {
			useEit = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_USE_EIT ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_VISIBLE ) != -1 ) {
			visible = cursor.getInt( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_VISIBLE ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_XMLTV_ID ) != -1 ) {
			xmltvId = cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_XMLTV_ID ) );
		}
		
		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_DEFAULT_AUTH ) != -1 ) {
			defaultAuth = cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_DEFAULT_AUTH ) );
		}

		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_HOSTNAME ) != -1 ) {
			Log.v( TAG, "convertCursorToChannelInfo : hostname" + cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_HOSTNAME ) ) );
		}

		ChannelInfo channelInfo = new ChannelInfo();
		channelInfo.setChannelId( channelId );
		channelInfo.setChannelNumber( channelNumber );
		channelInfo.setCallSign( callsign );
		channelInfo.setIconUrl( iconUrl );
		channelInfo.setChannelName( channelName );
		channelInfo.setMultiplexId( multiplexId );
		channelInfo.setTransportId( transportId );
		channelInfo.setServiceId( serviceId );
		channelInfo.setNetworkId( networkId );
		channelInfo.setAtscMajorChannel( atscMajorChannel );
		channelInfo.setAtscMinorChannel( atscMinorChannel );
		channelInfo.setFormat( format );
		channelInfo.setModulation( modulation );
		channelInfo.setFrequency( frequency );
		channelInfo.setFrequencyId( frequencyId );
		channelInfo.setFrequenceTable( frequencyTable );
		channelInfo.setFineTune( fineTune );
		channelInfo.setSiStandard( sisStandard );
		channelInfo.setChannelFilters( channelFilters );
		channelInfo.setSourceId( sourceId );
		channelInfo.setInputId( inputId );
		channelInfo.setCommercialFree( commercialFree );
		channelInfo.setUseEit( useEit == 1 ? true : false );
		channelInfo.setVisable( visible == 1 ? true : false );
		channelInfo.setXmltvId( xmltvId );
		channelInfo.setDefaultAuth( defaultAuth );
		
//		Log.v( TAG, "convertCursorToChannelInfo : exit" );
		return channelInfo;
	}

	// internal helpers

	private ContentValues[] convertChannelInfosToContentValuesArray( final List<ChannelInfo> channelInfos ) {
//		Log.v( TAG, "convertChannelInfosToContentValuesArray : enter" );
		
		if( null != channelInfos && !channelInfos.isEmpty() ) {
			
			ContentValues contentValues;
			List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();

			for( ChannelInfo channelInfo : channelInfos ) {

				contentValues = convertChannelInfoToContentValues( channelInfo );
				contentValuesArray.add( contentValues );
				
			}			
			
			if( !contentValuesArray.isEmpty() ) {
				
//				Log.v( TAG, "convertChannelInfosToContentValuesArray : exit" );
				return contentValuesArray.toArray( new ContentValues[ contentValuesArray.size() ] );
			}
			
		}
		
//		Log.v( TAG, "convertChannelInfosToContentValuesArray : exit, no channelInfos to convert" );
		return null;
	}

	private ContentValues convertChannelInfoToContentValues( final ChannelInfo channelInfo ) {
//		Log.v( TAG, "convertChannelToContentValues : enter" );
		
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
		values.put( ChannelConstants.FIELD_HOSTNAME, mLocationProfile.getHostname() );
		
//		Log.v( TAG, "convertChannelToContentValues : exit" );
		return values;
	}

}
