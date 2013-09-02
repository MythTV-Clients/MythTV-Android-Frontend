/**
 * 
 */
package org.mythtv.service.channel.v27;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v027.MythServicesTemplate;
import org.mythtv.services.api.v027.beans.ChannelInfo;
import org.mythtv.services.api.v027.beans.ChannelInfoList;
import org.mythtv.services.api.v027.beans.VideoSource;
import org.mythtv.services.api.v027.beans.VideoSourceList;
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
public class ChannelHelperV27 extends AbstractBaseHelper {

	private static final String TAG = ChannelHelperV27.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v027;
	
	private static MythServicesTemplate mMythServicesTemplate;
	
	public static boolean process( final Context context, final LocationProfile locationProfile ) {
		Log.v( TAG, "process : enter" );
		
		if( !MythAccessFactory.isServerReachable( locationProfile.getUrl() ) ) {
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
					List<ChannelInfo> allChannelLists = new ArrayList<ChannelInfo>();
					
					int nap = 1000; // 500ms & 1ms fail
					int count = 0;
					for( VideoSource videoSource : videoSourceList.getVideoSources() ) {
						Log.i( TAG, "process : videoSourceId = '" + videoSource.getId() + "'" );
						
						DateTime date = mEtagDaoHelper.findDateByEndpointAndDataId( context, locationProfile, "GetChannelInfoList", String.valueOf( videoSource.getId() ) );
						if( null != date ) {
							
							DateTime now = new DateTime( DateTimeZone.UTC );
							if( now.getMillis() - date.getMillis() > 86400000 ) {

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
							
						} else {
							
							// Download the channel listing, return list
							Log.v( TAG, "process : downloading channels" );
							ChannelInfo[] channelInfos = downloadChannels( context, locationProfile, videoSource.getId() );
							if( null != channelInfos ) {

								allChannelLists.addAll( Arrays.asList( channelInfos ) );

							}

							// wait a second before downloading the next one (if there are more than one video source)
							if(  count < videoSourceList.getVideoSources().length - 1 ) {
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
		
		Log.v( TAG, "downloadVideoSources : exit" );
		return passed;
	}
	
	// internal helpers
	
	private static ChannelInfo[] downloadChannels( final Context context, final LocationProfile locationProfile, final int sourceId ) {
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

	private static int load( final Context context, final LocationProfile locationProfile, final List<ChannelInfo> allChannelsList ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "load : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ChannelHelperV27 is not initialized" );
		
		DateTime lastModified = new DateTime( DateTimeZone.UTC );
		
		int count = 0;
		int processed = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		for( ChannelInfo channel : allChannelsList ) {

			if( channel.isVisible() ) {

				processChannel( context, locationProfile, ops, channel, lastModified, count );

				if( count > BATCH_COUNT_LIMIT ) {
//					Log.v( TAG, "process : batch update/insert" );

					processBatch( context, ops, processed, count );
				}

			}

			processBatch( context, ops, processed, count );

		}

		// Done with the updates/inserts, remove any 'stale' channels
//		Log.v( TAG, "load : deleting channels no longer present on mythtv backend" );
		deleteChannels( context, locationProfile, ops, lastModified );

		processBatch( context, ops, processed, count );
		
		Log.d( TAG, "load : exit" );
		return processed;
	}

	public static void processChannel( final Context context, final LocationProfile locationProfile, ArrayList<ContentProviderOperation> ops, ChannelInfo channel, DateTime lastModified, int count ) {
		Log.d( TAG, "processProgram : enter" );
		
		String[] projection = new String[] { ChannelConstants._ID };
		String selection = ChannelConstants.FIELD_CHAN_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( channel.getChanId() ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, null );

		ContentValues channelValues = convertChannelInfoToContentValues( locationProfile, lastModified, channel );
		Cursor channelCursor = context.getContentResolver().query( ChannelConstants.CONTENT_URI, projection, selection, selectionArgs, null );
		if( channelCursor.moveToFirst() ) {
//			Log.v( TAG, "load : updating channel " + channel.getChannelId() );

			Long id = channelCursor.getLong( channelCursor.getColumnIndexOrThrow( ChannelConstants._ID ) );
			ops.add( 
				ContentProviderOperation.newUpdate( ContentUris.withAppendedId( ChannelConstants.CONTENT_URI, id ) )
					.withValues( channelValues )
					.withYieldAllowed( true )
					.build()
			);

		} else {
//			Log.v( TAG, "load : adding channel " + channel.getChannelId() );

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

	private static void deleteChannels( final Context context, final LocationProfile locationProfile, ArrayList<ContentProviderOperation> ops, DateTime today ) {
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
	
	public static ContentValues convertChannelInfoToContentValues( final LocationProfile locationProfile, final DateTime lastModified, final ChannelInfo channelInfo ) {
//		Log.v( TAG, "convertChannelToContentValues : enter" );
		
		String formattedChannelNumber = formatChannelNumber( channelInfo.getChanNum() );
		if( formattedChannelNumber.startsWith( "." ) ) {
			formattedChannelNumber = formattedChannelNumber.substring( 1 );
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
		values.put( ChannelConstants.FIELD_LAST_MODIFIED_DATE, lastModified.getMillis() );
		
//		Log.v( TAG, "convertChannelToContentValues : exit" );
		return values;
	}

	private static String formatChannelNumber( String value ) {
		
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
