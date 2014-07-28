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
package org.mythtv.service.channel.v26;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.MythServiceApiRuntimeException;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.MythServicesTemplate;
import org.mythtv.services.api.v026.beans.ChannelInfo;
import org.mythtv.services.api.v026.beans.ChannelInfoList;
import org.mythtv.services.api.v026.beans.VideoSource;
import org.mythtv.services.api.v026.beans.VideoSourceList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ChannelHelperV26 extends AbstractBaseHelper {

	private static final String TAG = ChannelHelperV26.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v026;
	
	private static MythServicesTemplate mMythServicesTemplate;
	
	private static ChannelHelperV26 singleton;
	
	/**
	 * Returns the one and only ChannelHelperV26. init() must be called before 
	 * any 
	 * @return
	 */
	public static ChannelHelperV26 getInstance() {
		if( null == singleton ) {
			
			synchronized( ChannelHelperV26.class ) {

				if( null == singleton ) {
					singleton = new ChannelHelperV26();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private ChannelHelperV26() { }

	public boolean process( final Context context, final LocationProfile locationProfile ) {
		Log.v( TAG, "process : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );

			return false;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		if( null == mMythServicesTemplate ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}

		boolean passed = true;
		
		try {
			ResponseEntity<VideoSourceList> responseEntity = mMythServicesTemplate.channelOperations().getVideoSourceList( ETagInfo.createEmptyETag() );
			if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
				Log.i( TAG, "process : response returned HTTP 200" );
				
				VideoSourceList videoSourceList = responseEntity.getBody();
				if( null != videoSourceList ) {

					// holder for all downloaded channel lists
					List<ChannelInfo> allChannelLists = new ArrayList<ChannelInfo>();
					
					int nap = 1000; // 500ms & 1ms fail
					int count = 0;
					for( VideoSource videoSource : videoSourceList.getVideoSources() ) {
						Log.i( TAG, "process : videoSourceId = '" + videoSource.getId() + "'" );
						
						// Download the channel listing, return list
						Log.v( TAG, "process : downloading channels" );
						ChannelInfo[] channelInfos = downloadChannels( context, locationProfile, videoSource.getId() );
						if( null != channelInfos ) {

							allChannelLists.addAll( Arrays.asList( channelInfos ) );

						}

						// wait a second before downloading the next one (if there are more than one video source)
						if( count < videoSourceList.getVideoSources().length - 1 ) {
							Log.i( TAG, "process : sleeping " + nap + " ms" );
							Thread.sleep( nap );
						}

						count++;
					}

					// Process the combined lists of downloaded channels
					if( null != allChannelLists && !allChannelLists.isEmpty() ) {
						Log.i( TAG, "process : process all channels" );

						int channelsProcessed = load( context, locationProfile, allChannelLists );
						Log.v( TAG, "process : channelsProcessed=" + channelsProcessed );
						
					}
					
				}

			}					
		
		} catch( Exception e ) {
			Log.e( TAG, "process : error", e );

			passed = false;
		}
		
		Log.v( TAG, "process : exit" );
		return passed;
	}
	
	public ChannelInfo findChannel( final Context context, final LocationProfile locationProfile, Integer channelId ) {
//		Log.d( TAG, "findChannel : enter" );
		
		String selection = ChannelConstants.FIELD_CHAN_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( channelId ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, ChannelConstants.TABLE_NAME );
		
		ChannelInfo channel = null;
		
		Cursor cursor = context.getContentResolver().query( ChannelConstants.CONTENT_URI, null, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {

			channel = convertCursorToChannelInfo( cursor );
		}
		cursor.close();

//		Log.d( TAG, "findChannel : exit" );
		return channel;
	}
	
	// internal helpers
	
	private ChannelInfo[] downloadChannels( final Context context, final LocationProfile locationProfile, final int sourceId ) throws MythServiceApiRuntimeException {
		Log.v( TAG, "downloadChannels : enter" );
		
		ResponseEntity<ChannelInfoList> responseEntity = mMythServicesTemplate.channelOperations().getChannelInfoList( sourceId, 0, null, ETagInfo.createEmptyETag() );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "downloadChannels : GetChannelInfoList returned 200 OK" );

			ChannelInfoList channelInfoList = responseEntity.getBody();
			if( null != channelInfoList ) {

				if( null != channelInfoList.getChannelInfos() ) {
					Log.v( TAG, "downloadChannels : exit, returning channelInfos" );

					return channelInfoList.getChannelInfos();
				}

			}

		}

		Log.d( TAG, "downloadChannels : exit" );
		return null;
	}

	private int load( final Context context, final LocationProfile locationProfile, final List<ChannelInfo> allChannelsList ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "load : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ChannelHelperV26 is not initialized" );
		
		DateTime lastModified = new DateTime( DateTimeZone.UTC );
		
		int count = 0;
		int processed = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		for( ChannelInfo channel : allChannelsList ) {

			// channel.isVisible() == null was the cause of some NPEs.
			if( null != channel && null != channel.isVisible() && channel.isVisible() ) {

				processChannel( context, locationProfile, ops, channel );
				count++;
				
				if( count > BATCH_COUNT_LIMIT ) {
//					Log.v( TAG, "process : batch update/insert" );

					processBatch( context, ops, processed, count );
					
					count = 0;
				}

			}

		}

		processBatch( context, ops, processed, count );

		// Done with the updates/inserts, remove any 'stale' channels
//		Log.v( TAG, "load : deleting channels no longer present on mythtv backend" );
		deleteChannels( context, locationProfile, ops, lastModified );

		processBatch( context, ops, processed, count );
		
		Log.d( TAG, "load : exit" );
		return processed;
	}

	public void processChannel( final Context context, final LocationProfile locationProfile, ArrayList<ContentProviderOperation> ops, ChannelInfo channel ) {
//		Log.d( TAG, "processChannel : enter" );

		if( channel.getSourceId() < 1 || ( null == channel.getXMLTVID() || "".equals( channel.getXMLTVID() ) ) ) {
			Log.d( TAG, "processChannel : channelId=" + channel.getChanId() + ", channelNumber=" + channel.getChanNum() + ", callSign=" + channel.getCallSign() + ", sourceId=" + channel.getSourceId() + ", xmltvid=" + channel.getXMLTVID() );
		}
		
		ContentValues channelValues = convertChannelInfoToContentValues( locationProfile, channel );
		ops.add(  
			ContentProviderOperation.newInsert( ChannelConstants.CONTENT_URI )
				.withValues( channelValues )
				.withYieldAllowed( true )
				.build()
		);

//		Log.d( TAG, "processChannel : exit" );
	}

	private void deleteChannels( final Context context, final LocationProfile locationProfile, ArrayList<ContentProviderOperation> ops, DateTime today ) {
		Log.v( TAG, "deleteChannels : enter" );

		String channelDeleteSelection = ChannelConstants.FIELD_LAST_MODIFIED_DATE + " < ?";
		String[] channelDeleteSelectionArgs = new String[] { String.valueOf( today.getMillis() ) };
		
		channelDeleteSelection = appendLocationHostname( context, locationProfile, channelDeleteSelection, ChannelConstants.TABLE_NAME );
		
		ops.add(  
			ContentProviderOperation.newDelete( ChannelConstants.CONTENT_URI )
				.withSelection( channelDeleteSelection, channelDeleteSelectionArgs )
				.withYieldAllowed( true )
				.build()
		);
		
		Log.v( TAG, "deleteChannels : exit" );
	}
	
	public ChannelInfo convertCursorToChannelInfo( Cursor cursor ) {
//		Log.v( TAG, "convertCursorToChannelInfo : enter" );

		int  channelId = -1, multiplexId = -1, transportId = -1, serviceId = -1, networkId = -1, atscMajorChannel = -1, atscMinorChannel = -1, fineTune = -1, sourceId = -1, inputId = -1, commercialFree = -1, useEit = -1, visible = -1;
		long frequency = -1;
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
			frequency = cursor.getLong( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_FREQUENCY ) );
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

		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_MASTER_HOSTNAME ) != -1 ) {
			//Log.v( TAG, "convertCursorToChannelInfo : hostname " + cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_MASTER_HOSTNAME ) ) );
		}

		ChannelInfo channelInfo = new ChannelInfo();
		channelInfo.setChanId( channelId );
		channelInfo.setChanNum( channelNumber );
		channelInfo.setCallSign( callsign );
		channelInfo.setIconURL( iconUrl );
		channelInfo.setChannelName( channelName );
		channelInfo.setMplexId( multiplexId );
		channelInfo.setTransportId( transportId );
		channelInfo.setServiceId( serviceId );
		channelInfo.setNetworkId( networkId );
		channelInfo.setATSCMajorChan( atscMajorChannel );
		channelInfo.setATSCMinorChan( atscMinorChannel );
		channelInfo.setFormat( format );
		channelInfo.setModulation( modulation );
		channelInfo.setFrequency( frequency );
		channelInfo.setFrequencyId( frequencyId );
		channelInfo.setFrequencyTable( frequencyTable );
		channelInfo.setFineTune( fineTune );
		channelInfo.setSIStandard( sisStandard );
		channelInfo.setChanFilters( channelFilters );
		channelInfo.setSourceId( sourceId );
		channelInfo.setInputId( inputId );
		channelInfo.setCommFree( commercialFree );
		channelInfo.setUseEIT( useEit == 1 ? true : false );
		channelInfo.setVisible( visible == 1 ? true : false );
		channelInfo.setXMLTVID( xmltvId );
		channelInfo.setDefaultAuth( defaultAuth );
		
//		Log.v( TAG, "convertCursorToChannelInfo : exit" );
		return channelInfo;
	}

	public ContentValues convertChannelInfoToContentValues( final LocationProfile locationProfile, final ChannelInfo channelInfo ) {
//		Log.v( TAG, "convertChannelToContentValues : enter" );
		
		String formattedChannelNumber = formatChannelNumber( channelInfo.getChanNum() );
		if( formattedChannelNumber.startsWith( "." ) ) {
			//formattedChannelNumber = formattedChannelNumber.substring( 1 );
			formattedChannelNumber = formattedChannelNumber.replaceFirst( "[\\._]", "9999" );
		}

		ContentValues values = new ContentValues();
		values.put( ChannelConstants.FIELD_CHAN_ID, channelInfo.getChanId() );
		values.put( ChannelConstants.FIELD_CHAN_NUM, channelInfo.getChanNum() );
		values.put( ChannelConstants.FIELD_CHAN_NUM_FORMATTED, ( null == formattedChannelNumber || formattedChannelNumber.length() == 0 ) ? 0.0 : Float.parseFloat( formattedChannelNumber ) );
		values.put( ChannelConstants.FIELD_CALLSIGN, channelInfo.getCallSign() );
		values.put( ChannelConstants.FIELD_ICON_URL, channelInfo.getIconURL() );
		values.put( ChannelConstants.FIELD_CHANNEL_NAME, channelInfo.getChannelName() );
		values.put( ChannelConstants.FIELD_MPLEX_ID, channelInfo.getMplexId() );
		values.put( ChannelConstants.FIELD_TRANSPORT_ID, channelInfo.getTransportId() );
		values.put( ChannelConstants.FIELD_SERVICE_ID, channelInfo.getServiceId() );
		values.put( ChannelConstants.FIELD_NETWORK_ID, channelInfo.getNetworkId() );
		values.put( ChannelConstants.FIELD_ATSC_MAJOR_CHAN, channelInfo.getATSCMajorChan() );
		values.put( ChannelConstants.FIELD_ATSC_MINOR_CHAN, channelInfo.getATSCMinorChan() );
		values.put( ChannelConstants.FIELD_FORMAT, channelInfo.getFormat() );
		values.put( ChannelConstants.FIELD_MODULATION, channelInfo.getModulation() );
		values.put( ChannelConstants.FIELD_FREQUENCY, channelInfo.getFrequency() );
		values.put( ChannelConstants.FIELD_FREQUENCY_ID, channelInfo.getFrequencyId() );
		values.put( ChannelConstants.FIELD_FREQUENCY_TABLE, channelInfo.getFrequencyTable() );
		values.put( ChannelConstants.FIELD_FINE_TUNE, channelInfo.getFineTune() );
		values.put( ChannelConstants.FIELD_SIS_STANDARD, channelInfo.getSIStandard() );
		values.put( ChannelConstants.FIELD_CHAN_FILTERS, channelInfo.getChanFilters() );
		values.put( ChannelConstants.FIELD_SOURCE_ID, channelInfo.getSourceId() );
		values.put( ChannelConstants.FIELD_INPUT_ID, channelInfo.getInputId() );
		values.put( ChannelConstants.FIELD_COMM_FREE, channelInfo.getCommFree() );
		values.put( ChannelConstants.FIELD_USE_EIT, ( channelInfo.isUseEIT() ? 1 : 0 ) );
		values.put( ChannelConstants.FIELD_VISIBLE, ( channelInfo.isVisible() ? 1 : 0 ) );
		values.put( ChannelConstants.FIELD_XMLTV_ID, channelInfo.getXMLTVID() );
		values.put( ChannelConstants.FIELD_DEFAULT_AUTH, channelInfo.getDefaultAuth() );
		values.put( ChannelConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		values.put( ChannelConstants.FIELD_LAST_MODIFIED_DATE, new DateTime().getMillis() );
		
//		Log.v( TAG, "convertChannelToContentValues : exit" );
		return values;
	}

	private String formatChannelNumber( String value ) {
		
		if( null == value || "".equals( value ) ) {
			return null;
		}
		
		char delimiter = '_';
		for( char c : value.toCharArray() ) {
			String test = String.valueOf( c ); 
			if( !test.matches( "\\d" ) ) {
				delimiter = c;
			}
		}
		
		value = value.replace( delimiter, '.' );
		
		return value;
	}

}
