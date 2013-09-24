/**
 * 
 */
package org.mythtv.service.channel.v26;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.MythServicesTemplate;
import org.mythtv.services.api.v026.beans.ChannelInfo;
import org.mythtv.services.api.v026.beans.ChannelInfoList;
import org.mythtv.services.api.v026.beans.ChannelInfos;
import org.mythtv.services.api.v026.beans.VideoSource;
import org.mythtv.services.api.v026.beans.VideoSourceList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
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
		
		boolean passed = true;
		
		try {
			EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( context, locationProfile, "GetVideoSourceList", null );
			
			ResponseEntity<VideoSourceList> responseEntity = mMythServicesTemplate.channelOperations().getVideoSourceList( etag );
			if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
				Log.i( TAG, "process : response returned HTTP 200" );
				
				VideoSourceList videoSourceList = responseEntity.getBody();
				if( null != videoSourceList ) {

					// holder for all downloaded channel lists
					List<ChannelInfos> allChannelLists = new ArrayList<ChannelInfos>();
					
					int nap = 1000; // 500ms & 1ms fail
					int count = 0;
					for( VideoSource videoSource : videoSourceList.getVideoSources().getVideoSources() ) {
						Log.i( TAG, "process : videoSourceId = '" + videoSource.getId() + "'" );
						
						DateTime date = mEtagDaoHelper.findDateByEndpointAndDataId( context, locationProfile, "GetChannelInfoList", String.valueOf( videoSource.getId() ) );
						if( null != date ) {
							
							DateTime now = new DateTime( DateTimeZone.UTC );
							if( now.getMillis() - date.getMillis() > 86400000 ) {

								// Download the channel listing, return list
								Log.i( TAG, "process : downloading channels" );
								ChannelInfos channelInfos = downloadChannels( context, locationProfile, videoSource.getId() );
								if( null != channelInfos ) {

									allChannelLists.add( channelInfos );

								}

								// wait a second before downloading the next one (if there are more than one video source)
								if( count < videoSourceList.getVideoSources().getVideoSources().size() - 1 ) {
									Log.i( TAG, "process : sleeping " + nap + " ms" );
									Thread.sleep( nap );
								}

								count++;
							}
							
						} else {
							
							// Download the channel listing, return list
							Log.i( TAG, "process : downloading channels" );
							ChannelInfos channelInfos = downloadChannels( context, locationProfile, videoSource.getId() );
							if( null != channelInfos ) {

								allChannelLists.add( channelInfos );

							}

							// wait a second before downloading the next one (if there are more than one video source)
							if(  count < videoSourceList.getVideoSources().getVideoSources().size() - 1 ) {
								Log.i( TAG, "process : sleeping " + nap + " ms" );
								Thread.sleep( nap );
							}

							count++;
						}
						
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
		Log.d( TAG, "findChannel : enter" );
		
		String selection = ChannelConstants.FIELD_CHAN_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( channelId ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, ChannelConstants.TABLE_NAME );
		
		ChannelInfo channel = null;
		
		Cursor cursor = context.getContentResolver().query( ChannelConstants.CONTENT_URI, null, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {

			channel = convertCursorToChannelInfo( cursor );
		}
		cursor.close();

		Log.d( TAG, "findChannel : exit" );
		return channel;
	}
	
	// internal helpers
	
	private ChannelInfos downloadChannels( final Context context, final LocationProfile locationProfile, final int sourceId ) {
		Log.v( TAG, "downloadChannels : enter" );
		
		EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( context, locationProfile, "GetChannelInfoList", String.valueOf( sourceId ) );
		
		ResponseEntity<ChannelInfoList> responseEntity = mMythServicesTemplate.channelOperations().getChannelInfoList( sourceId, 0, -1, etag );

		DateTime date = new DateTime( DateTimeZone.UTC );
		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "downloadChannels : GetChannelInfoList returned 200 OK" );

			ChannelInfoList channelInfoList = responseEntity.getBody();
			if( null != channelInfoList ) {

				etag.setEndpoint( "GetChannelInfoList" );
				etag.setDataId( sourceId );
				etag.setDate( date );
				etag.setMasterHostname( locationProfile.getHostname() );
				etag.setLastModified( date );
				mEtagDaoHelper.save( context, locationProfile, etag );

				if( null != channelInfoList.getChannelInfos() ) {
					Log.v( TAG, "downloadChannels : exit, returning channelInfos" );

					return channelInfoList.getChannelInfos();
				}

			}

		}

		if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
			Log.i( TAG, "downloadChannels : GetChannelInfoList returned 304 Not Modified" );

			etag.setLastModified( date );
			mEtagDaoHelper.save( context, locationProfile, etag );
		}

		Log.d( TAG, "downloadChannels : exit" );
		return null;
	}

	private int load( final Context context, final LocationProfile locationProfile, final List<ChannelInfos> allChannelsList ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "load : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ChannelHelperV26 is not initialized" );
		
		DateTime lastModified = new DateTime( DateTimeZone.UTC );
		
		int count = 0;
		int processed = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		for( ChannelInfos channelInfos : allChannelsList ) {
//			Log.v( TAG, "load : channelInfos iteration, channels in this source: " + channelInfos.getTotalAvailable() );
			
			for( ChannelInfo channel : channelInfos.getChannelInfos() ) {
				
				if( channel.isVisable() ) {
				
					processChannel( context, locationProfile, ops, channel, lastModified, count );
					count++;
					
					if( count > BATCH_COUNT_LIMIT ) {
//						Log.v( TAG, "process : batch update/insert" );

						processBatch( context, ops, processed, count );
						
						count = 0;
					}
				
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

	public void processChannel( final Context context, final LocationProfile locationProfile, ArrayList<ContentProviderOperation> ops, ChannelInfo channel, DateTime lastModified, int count ) {
		Log.d( TAG, "processProgram : enter" );
		
		String[] projection = new String[] { ChannelConstants._ID };
		String selection = ChannelConstants.FIELD_CHAN_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( channel.getChannelId() ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, null );

		ContentValues channelValues = convertChannelInfoToContentValues( locationProfile, lastModified, channel );
		Cursor channelCursor = context.getContentResolver().query( ChannelConstants.CONTENT_URI, projection, selection, selectionArgs, null );
		if( channelCursor.moveToFirst() ) {
			Log.v( TAG, "load : updating channel " + channel.getChannelId() );

			Long id = channelCursor.getLong( channelCursor.getColumnIndexOrThrow( ChannelConstants._ID ) );
			ops.add( 
				ContentProviderOperation.newUpdate( ContentUris.withAppendedId( ChannelConstants.CONTENT_URI, id ) )
					.withValues( channelValues )
					.withYieldAllowed( true )
					.build()
			);

		} else {
			Log.v( TAG, "load : adding channel " + channel.getChannelId() );

			ops.add(  
				ContentProviderOperation.newInsert( ChannelConstants.CONTENT_URI )
					.withValues( channelValues )
					.withYieldAllowed( true )
					.build()
			);

		}
		channelCursor.close();
		count++;

		Log.d( TAG, "processProgram : exit" );
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

		if( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_MASTER_HOSTNAME ) != -1 ) {
			Log.v( TAG, "convertCursorToChannelInfo : hostname" + cursor.getString( cursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_MASTER_HOSTNAME ) ) );
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

	public ContentValues convertChannelInfoToContentValues( final LocationProfile locationProfile, final DateTime lastModified, final ChannelInfo channelInfo ) {
//		Log.v( TAG, "convertChannelToContentValues : enter" );
		
		String formattedChannelNumber = formatChannelNumber( channelInfo.getChannelNumber() );
		if( formattedChannelNumber.startsWith( "." ) ) {
			formattedChannelNumber = formattedChannelNumber.substring( 1 );
		}

		ContentValues values = new ContentValues();
		values.put( ChannelConstants.FIELD_CHAN_ID, channelInfo.getChannelId() );
		values.put( ChannelConstants.FIELD_CHAN_NUM, channelInfo.getChannelNumber() );
		values.put( ChannelConstants.FIELD_CHAN_NUM_FORMATTED, ( null == formattedChannelNumber || formattedChannelNumber.length() == 0 ) ? 0.0 : Float.parseFloat( formattedChannelNumber ) );
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
		values.put( ChannelConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		values.put( ChannelConstants.FIELD_LAST_MODIFIED_DATE, lastModified.getMillis() );
		
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
