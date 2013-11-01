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
package org.mythtv.service.content.v26;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.client.ui.preferences.PlaybackProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.content.LiveStreamConstants;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.db.preferences.PlaybackProfileDaoHelper;
import org.mythtv.service.content.LiveStreamService;
import org.mythtv.service.dvr.v26.RecordedHelperV26;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.Bool;
import org.mythtv.services.api.v026.MythServicesTemplate;
import org.mythtv.services.api.v026.beans.LiveStreamInfo;
import org.mythtv.services.api.v026.beans.LiveStreamInfoList;
import org.mythtv.services.api.v026.beans.LiveStreamInfoWrapper;
import org.mythtv.services.api.v026.beans.Program;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class LiveStreamHelperV26 extends AbstractBaseHelper {

	private static final String TAG = LiveStreamHelperV26.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v026;
	
	private static PlaybackProfileDaoHelper mPlaybackProfileDaoHelper = PlaybackProfileDaoHelper.getInstance();

	private static LiveStreamHelperV26 singleton;
	
	/**
	 * Returns the one and only LiveStreamHelperV26. init() must be called before 
	 * any 
	 * @return
	 */
	public static LiveStreamHelperV26 getInstance() {
		if( null == singleton ) {
			
			synchronized( LiveStreamHelperV26.class ) {

				if( null == singleton ) {
					singleton = new LiveStreamHelperV26();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private LiveStreamHelperV26() { }

	public boolean create( final Context context, final LocationProfile locationProfile, final Integer channelId, final DateTime startTime ) {
		Log.v( TAG, "create : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "create : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );

			return false;
		}
		
		MythServicesTemplate mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		if( null == mMythServicesTemplate ) {
			Log.w( TAG, "create : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}

		PlaybackProfile selectedPlaybackProfile = null;
		LocationType locationType = locationProfile.getType();
			
		if( locationType.equals( LocationType.HOME ) ) {
			selectedPlaybackProfile = mPlaybackProfileDaoHelper.findSelectedHomeProfile( context );
		} else if( locationType.equals( LocationType.AWAY ) ) {
			selectedPlaybackProfile = mPlaybackProfileDaoHelper.findSelectedAwayProfile( context );
		}

		if( null != selectedPlaybackProfile ) {
				
			try {
				Program program = RecordedHelperV26.getInstance().findRecorded( context, locationProfile, channelId, startTime );
				
				if( null != program ) {
					ResponseEntity<LiveStreamInfoWrapper> wrapper = mMythServicesTemplate.contentOperations().
							addLiveStream( program.getRecording().getStorageGroup(), program.getFilename(), program.getHostname(), -1, -1,
								selectedPlaybackProfile.getHeight(), selectedPlaybackProfile.getVideoBitrate(), 
								selectedPlaybackProfile.getAudioBitrate(), selectedPlaybackProfile.getAudioSampleRate() );

					if( wrapper.getStatusCode().equals( HttpStatus.OK ) ) {
						LiveStreamInfo liveStreamInfo = wrapper.getBody().getLiveStreamInfo();

						save( context, locationProfile, liveStreamInfo, channelId, startTime );

						Log.v( TAG, "create : exit" );
						return true;
					}
				
				}
				
			} catch( Exception e ) {
				Log.e( TAG, "create : error", e );
			}
			
		}
			
		Log.v( TAG, "create : exit, live stream NOT created" );
		return false;
	}

	public Integer load( final Context context, final LocationProfile locationProfile ) {
		Log.v( TAG, "load : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "update : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );

			return null;
		}
		
		MythServicesTemplate mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		if( null == mMythServicesTemplate ) {
			Log.w( TAG, "load : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}

		Integer loaded = null;
		
		try {
			
			ResponseEntity<LiveStreamInfoList> wrapper = mMythServicesTemplate.contentOperations().getLiveStreamList( EtagInfoDelegate.createEmptyETag() );
			if( wrapper.getStatusCode().equals( HttpStatus.OK ) ) {
				
				if( null != wrapper.getBody() ) {
				
					if( null != wrapper.getBody().getLiveStreamInfos() ) {
					
						if( null != wrapper.getBody().getLiveStreamInfos().getLiveStreamInfos() && !wrapper.getBody().getLiveStreamInfos().getLiveStreamInfos().isEmpty() ) {
						
							List<LiveStreamInfo> liveStreams = wrapper.getBody().getLiveStreamInfos().getLiveStreamInfos();
							loaded = load( context, locationProfile, liveStreams );
						
						}
						
					}
					
				}
				
			}
			
			Log.v( TAG, "update : exit" );
			return loaded;

		} catch( Exception e ) {
			Log.e( TAG, "update : error", e );
		}
			
		Log.v( TAG, "update : exit, live streams NOT loaded" );
		return loaded;
	}

	public boolean update( final Context context, final LocationProfile locationProfile, final long liveStreamId, final int channelId, DateTime startTime ) {
		Log.v( TAG, "update : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "update : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );

			return false;
		}
		
		MythServicesTemplate mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		if( null == mMythServicesTemplate ) {
			Log.w( TAG, "update : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
		
		try {
			
			LiveStreamInfo liveStream = findLiveStream( context, locationProfile, liveStreamId );
			if( null != liveStream ) {
				Log.v( TAG, "update : liveStream=" + liveStream.toString() );

				ResponseEntity<LiveStreamInfoWrapper> wrapper = mMythServicesTemplate.contentOperations().getLiveStream( liveStream.getId(), EtagInfoDelegate.createEmptyETag() );
				if( wrapper.getStatusCode().equals( HttpStatus.OK ) ) {
					LiveStreamInfo updated = wrapper.getBody().getLiveStreamInfo();

					if( !"Unknown status value".equalsIgnoreCase( updated.getStatusStr() ) ) {
						save( context, locationProfile, updated, channelId, startTime );
					} else {
						deleteLiveStream( context, liveStreamId );
					}

					Log.v( TAG, "update : exit" );
					return true;
				}

			}
				
		} catch( Exception e ) {
			Log.e( TAG, "update : error", e );
		}
			
		Log.v( TAG, "update : exit, live stream NOT update" );
		return false;
	}

	public boolean remove( final Context context, final LocationProfile locationProfile, final Integer channelId, final DateTime startTime ) {
		Log.v( TAG, "remove : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "remove : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );

			return false;
		}
		
		MythServicesTemplate mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		if( null == mMythServicesTemplate ) {
			Log.w( TAG, "rempve : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}

		try {
			Program program = RecordedHelperV26.getInstance().findRecorded( context, locationProfile, channelId, startTime );
			
			if( null != program ) {
				LiveStreamInfo liveStream = findLiveStream( context, locationProfile, channelId, startTime );
				
				ResponseEntity<Bool> wrapper = mMythServicesTemplate.contentOperations().removeLiveStream( liveStream.getId() );
				if( wrapper.getStatusCode().equals( HttpStatus.OK ) ) {
					
					Bool bool = wrapper.getBody();
					if( bool.getBool() == Boolean.TRUE ) {
						
						boolean removed = deleteLiveStreamByLiveStreamId( context, locationProfile, liveStream.getId() );
						if( removed ) {
							Log.v( TAG, "remove : exit" );
					
							return true;
						}
				
					}
				
				}
				
			}
				
		} catch( Exception e ) {
			Log.e( TAG, "remove : error", e );
		}
			
		Log.v( TAG, "remove : exit, live stream NOT removed" );
		return false;
	}

	public LiveStreamInfo findLiveStream( final Context context, final LocationProfile locationProfile, final Long id ) {
		Log.d( TAG, "findLiveStream : enter" );
		
		String projection[] = null;
		String selection = LiveStreamConstants.FIELD_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( id ) };
		
		LiveStreamInfo liveStreamInfo = null;
		
		Uri uri = LiveStreamConstants.CONTENT_URI;
		if( null != id && id > 0 ) {
			uri = ContentUris.withAppendedId( LiveStreamConstants.CONTENT_URI, id );
		}
		
		selection = appendLocationHostname( context, locationProfile, selection, LiveStreamConstants.TABLE_NAME );
		
		Cursor cursor = context.getContentResolver().query( uri, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
			liveStreamInfo = convertCursorToLiveStreamInfo( cursor );
		}
		cursor.close();
				
		Log.d( TAG, "findLiveStream : exit" );
		return liveStreamInfo;
	}

	public LiveStreamInfo findLiveStream( final Context context, final LocationProfile locationProfile, final Integer channelId, final DateTime startTime ) {
		Log.d( TAG, "findLiveStream : enter" );
		
		String projection[] = null;
		String selection = LiveStreamConstants.FIELD_CHAN_ID + " = ? AND " + LiveStreamConstants.FIELD_START_TIME + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( channelId ), String.valueOf( startTime.getMillis() ) };
		
		LiveStreamInfo liveStreamInfo = null;
		
		selection = appendLocationHostname( context, locationProfile, selection, LiveStreamConstants.TABLE_NAME );
		
		Cursor cursor = context.getContentResolver().query( LiveStreamConstants.CONTENT_URI, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
			liveStreamInfo = convertCursorToLiveStreamInfo( cursor );
		}
		cursor.close();
				
		Log.d( TAG, "findLiveStream : exit" );
		return liveStreamInfo;
	}

	public boolean deleteLiveStream( final Context context, final Long id ) {
		Log.d( TAG, "deleteLiveStream : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "LiveStreamHelperV26 is not initialized" );
		
		int deleted = context.getContentResolver().delete( ContentUris.withAppendedId( LiveStreamConstants.CONTENT_URI, id ), null, null );
		if( deleted == 1 ) {
			Log.v( TAG, "deleteLiveStream : exit" );
			
			return true;
		}
		
		Log.d( TAG, "deleteLiveStream : exit, live stream NOT deleted" );
		return false;
	}

	// internal helpers
	
	/**
	 * @param liveStreamId
	 * @return
	 */
	private boolean deleteLiveStreamByLiveStreamId( final Context context, final LocationProfile locationProfile, final Integer liveStreamId ) {
		Log.d( TAG, "deleteLiveStreamByLiveStreamId : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "LiveStreamHelperV26 is not initialized" );
		
		String selection = LiveStreamConstants.FIELD_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( liveStreamId ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, LiveStreamConstants.TABLE_NAME );
		
		int deleted = context.getContentResolver().delete( LiveStreamConstants.CONTENT_URI, selection, selectionArgs );
		if( deleted == 1 ) {
			Log.v( TAG, "deleteLiveStreamByLiveStreamId : exit" );
			
			return true;
		}
		
		Log.d( TAG, "deleteLiveStreamByLiveStreamId : exit, live stream NOT deleted" );
		return false;
	}

	private Integer countLiveStreamsNotComplete( final Context context, final LocationProfile locationProfile ) {
		Log.d( TAG, "countLiveStreamsNotComplete : enter" );
		
		String[] projection = new String[] { "count(" + LiveStreamConstants.TABLE_NAME + "." + LiveStreamConstants._ID + ")" };
		String selection = "NOT " + LiveStreamConstants.TABLE_NAME + "." + LiveStreamConstants.FIELD_PERCENT_COMPLETE + " = ?";
		String[] selectionArgs = new String[] { "100" };
		
		selection = appendLocationHostname( context, locationProfile, selection, LiveStreamConstants.TABLE_NAME );
		
		Integer count = null;
		
		Cursor cursor = context.getContentResolver().query( LiveStreamConstants.CONTENT_URI, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
//			Log.v( TAG, "findProgram : program=" + program.toString() );

			count = cursor.getInt( 0 );
		}
		cursor.close();

		Log.d( TAG, "countLiveStreamsNotComplete : exit" );
		return null != count ? count : 0;
	}

	private int load( final Context context, final LocationProfile locationProfile, List<LiveStreamInfo> liveStreams ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "load : enter" );
		
		if( null == context ) {
			throw new RuntimeException( "LiveStreamHelperV26 is not initialized" );
		}
		
		DateTime lastModified = new DateTime( DateTimeZone.UTC );
		
		int processed = -1;
		int count = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		for( LiveStreamInfo liveStream : liveStreams ) {

			ContentValues values = convertLiveStreamInfoToContentValues( locationProfile, liveStream, lastModified, -1, null );

			String[] projection = new String[] { LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants._ID };
			String selection = LiveStreamConstants.FIELD_ID + " = ?";
			String[] selectionArgs = new String[] { String.valueOf( liveStream.getId() ) };
			
			selection = appendLocationHostname( context, locationProfile, selection, LiveStreamConstants.TABLE_NAME );
			
			Cursor cursor = context.getContentResolver().query( LiveStreamConstants.CONTENT_URI, projection, selection, selectionArgs, null );
			if( cursor.moveToFirst() ) {
				Log.v( TAG, "load : updating existing liveStream info" );
				long id = cursor.getLong( cursor.getColumnIndexOrThrow( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants._ID ) );
				
				context.getContentResolver().update( ContentUris.withAppendedId( LiveStreamConstants.CONTENT_URI, id ), values, null, null );
			}
			cursor.close();
			count++;
			
			if( count > BATCH_COUNT_LIMIT ) {
				Log.i( TAG, "load : applying batch for '" + count + "' transactions, processing programs" );
				
				processBatch( context, ops, processed, count );

				count = 0;
				
			}

		}
		
		processBatch( context, ops, processed, count );
		
		Log.v( TAG, "load : remove deleted liveStreams" );
		String deletedSelection = LiveStreamConstants.TABLE_NAME + "." + LiveStreamConstants.FIELD_LAST_MODIFIED + " < ?";
		String[] deletedSelectionArgs = new String[] { String.valueOf( lastModified.getMillis() ) };
			
		deletedSelection = appendLocationHostname( context, locationProfile, deletedSelection, LiveStreamConstants.TABLE_NAME );
			
		ops.add(  
			ContentProviderOperation.newDelete( LiveStreamConstants.CONTENT_URI )
				.withSelection( deletedSelection, deletedSelectionArgs )
				.withYieldAllowed( true )
				.build()
		);

		processBatch( context, ops, processed, count );
		
		Intent progressIntent = new Intent( LiveStreamService.ACTION_PROGRESS );
		context.sendBroadcast( progressIntent );
			
		if( countLiveStreamsNotComplete( context, locationProfile ) > 0 ) {
			Log.d( TAG, "load : further updates are required" );
			
			try {
				Thread.sleep( 15000 );
			} catch( InterruptedException e ) {
				Log.e( TAG, "load : error", e );
			}
			
			processed = load( context, locationProfile );
		}

		Log.d( TAG, "load : exit" );
		return processed;
	}
	
	private boolean save( final Context context, final LocationProfile locationProfile, LiveStreamInfo liveStreamInfo, int channelId, DateTime startTime ) {
		Log.d( TAG, "save : enter" );

		if( null == context ) 
			throw new RuntimeException( "LiveStreamHelperV26 is not initialized" );
		
		DateTime lastModified = new DateTime( DateTimeZone.UTC );

		boolean saved = false;
		
		ContentValues values = convertLiveStreamInfoToContentValues( locationProfile, liveStreamInfo, lastModified, channelId, startTime );

		String[] projection = new String[] { LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants._ID };
		String selection = LiveStreamConstants.FIELD_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( liveStreamInfo.getId() ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, LiveStreamConstants.TABLE_NAME );
		
		Cursor cursor = context.getContentResolver().query( LiveStreamConstants.CONTENT_URI, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
			Log.v( TAG, "save : updating existing liveStream info" );
			long id = cursor.getLong( cursor.getColumnIndexOrThrow( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants._ID ) );
			
			int updated = context.getContentResolver().update( ContentUris.withAppendedId( LiveStreamConstants.CONTENT_URI, id ), values, null, null );
			if( updated == 1 ) {
				saved = true;
			}
		} else {
			Log.v( TAG, "save : inserting new liveStream info" );
			Uri url = context.getContentResolver().insert( LiveStreamConstants.CONTENT_URI, values );
			if( ContentUris.parseId( url ) > 0 ) {
				saved = true;
			}
		}
		cursor.close();

		Log.d( TAG, "save : exit" );
		return saved;
	}

	private LiveStreamInfo convertCursorToLiveStreamInfo( Cursor cursor ) {
//		Log.v( TAG, "convertCursorToLiveStreamInfo : enter" );

		int  id = -1, width = -1, height = -1, bitrate = -1, audioBitrate = -1, segmentSize = -1, maxSegments = -1, startSegment = -1, currentSegment = -1, segmentCount = -1, percentComplete = -1, statusInt = -1, sourceWidth = -1, sourceHeight = -1, audioOnlyBitrate = -1;
		String relativeUrl = "", fullUrl = "", statusStr = "", statusMessage = "", sourceFile = "", sourceHost = "";
		DateTime created = null, lastModified = null;
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_ID ) != -1 ) {
			id = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_ID ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_WIDTH ) != -1 ) {
			width = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_WIDTH ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_HEIGHT ) != -1 ) {
			height = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_HEIGHT ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_BITRATE ) != -1 ) {
			bitrate = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_BITRATE ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_AUDIO_BITRATE ) != -1 ) {
			audioBitrate = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_AUDIO_BITRATE ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_SEGMENT_SIZE ) != -1 ) {
			segmentSize = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_SEGMENT_SIZE ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_MAX_SEGMENTS ) != -1 ) {
			maxSegments = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_MAX_SEGMENTS ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_START_SEGMENT ) != -1 ) {
			startSegment = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_START_SEGMENT ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_CURRENT_SEGMENT ) != -1 ) {
			currentSegment = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_CURRENT_SEGMENT ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_SEGMENT_COUNT ) != -1 ) {
			segmentCount = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_SEGMENT_COUNT ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_PERCENT_COMPLETE ) != -1 ) {
			percentComplete = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_PERCENT_COMPLETE ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_CREATED ) != -1 ) {
			created = new DateTime( cursor.getLong( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_CREATED ) ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_LAST_MODIFIED ) != -1 ) {
			lastModified = new DateTime( cursor.getLong( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_LAST_MODIFIED ) ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_RELATIVE_URL ) != -1 ) {
			relativeUrl = cursor.getString( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_RELATIVE_URL ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_FULL_URL ) != -1 ) {
			fullUrl = cursor.getString( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_FULL_URL ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_STATUS_STR ) != -1 ) {
			statusStr = cursor.getString( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_STATUS_STR ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_STATUS_INT ) != -1 ) {
			statusInt = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_STATUS_INT ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_STATUS_MESSAGE ) != -1 ) {
			statusMessage = cursor.getString( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_STATUS_MESSAGE ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_SOURCE_FILE ) != -1 ) {
			sourceFile = cursor.getString( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_SOURCE_FILE ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_SOURCE_HOST ) != -1 ) {
			sourceHost = cursor.getString( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_SOURCE_HOST ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_SOURCE_WIDTH ) != -1 ) {
			sourceWidth = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_SOURCE_WIDTH ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_SOURCE_HEIGHT ) != -1 ) {
			sourceHeight = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_SOURCE_HEIGHT ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_AUDIO_ONLY_BITRATE ) != -1 ) {
			audioOnlyBitrate = cursor.getInt( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_AUDIO_ONLY_BITRATE ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_CHAN_ID ) != -1 ) {
			//Log.v( TAG, "convertCursorToLiveStreamInfo : chanId=" + cursor.getString( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_CHAN_ID ) ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_START_TIME ) != -1 ) {
			//Log.v( TAG, "convertCursorToLiveStreamInfo : startTime=" + new DateTime( cursor.getLong( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_START_TIME ) ) ) );
		}

		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_MASTER_HOSTNAME ) != -1 ) {
			Log.v( TAG, "convertCursorToLiveStreamInfo : hostname=" + cursor.getString( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_MASTER_HOSTNAME ) ) );
		}

		LiveStreamInfo liveStreamInfo = new LiveStreamInfo();
		liveStreamInfo.setId( id );
		liveStreamInfo.setWidth( width );
		liveStreamInfo.setHeight( height );
		liveStreamInfo.setBitrate( bitrate );
		liveStreamInfo.setAudioBitrate( audioBitrate );
		liveStreamInfo.setSegmentSize( segmentSize );
		liveStreamInfo.setMaxSegments( maxSegments );
		liveStreamInfo.setStartSegment( startSegment );
		liveStreamInfo.setCurrentSegment( currentSegment );
		liveStreamInfo.setSegmentCount( segmentCount );
		liveStreamInfo.setPercentComplete( percentComplete );
		liveStreamInfo.setCreated( created );
		liveStreamInfo.setLastModified( lastModified );
		liveStreamInfo.setRelativeUrl( relativeUrl );
		liveStreamInfo.setFullUrl( fullUrl );
		liveStreamInfo.setStatusStr( statusStr );
		liveStreamInfo.setStatusInt( statusInt );
		liveStreamInfo.setStatusMessage( statusMessage );
		liveStreamInfo.setSourceFile( sourceFile );
		liveStreamInfo.setSourceHost( sourceHost );
		liveStreamInfo.setSourceWidth( sourceWidth );
		liveStreamInfo.setSourceHeight( sourceHeight );
		liveStreamInfo.setAudioOnlyBitrate( audioOnlyBitrate );
		
//		Log.v( TAG, "convertCursorToLiveStreamInfo : exit" );
		return liveStreamInfo;
	}

	private ContentValues convertLiveStreamInfoToContentValues( final LocationProfile locationProfile, final LiveStreamInfo liveStreamInfo, final DateTime lastModified, final int channelId, final DateTime startTime ) {
//		Log.v( TAG, "convertLiveStreamToContentValues : enter" );
		
		ContentValues values = new ContentValues();
		values.put( LiveStreamConstants.FIELD_ID, liveStreamInfo.getId() );
		values.put( LiveStreamConstants.FIELD_WIDTH, liveStreamInfo.getWidth() );
		values.put( LiveStreamConstants.FIELD_HEIGHT, liveStreamInfo.getHeight() );
		values.put( LiveStreamConstants.FIELD_BITRATE, liveStreamInfo.getBitrate() );
		values.put( LiveStreamConstants.FIELD_AUDIO_BITRATE, liveStreamInfo.getAudioBitrate() );
		values.put( LiveStreamConstants.FIELD_SEGMENT_SIZE, liveStreamInfo.getSegmentSize() );
		values.put( LiveStreamConstants.FIELD_MAX_SEGMENTS, liveStreamInfo.getMaxSegments() );
		values.put( LiveStreamConstants.FIELD_START_SEGMENT, liveStreamInfo.getStartSegment() );
		values.put( LiveStreamConstants.FIELD_CURRENT_SEGMENT, liveStreamInfo.getCurrentSegment() );
		values.put( LiveStreamConstants.FIELD_SEGMENT_COUNT, liveStreamInfo.getSegmentCount() );
		values.put( LiveStreamConstants.FIELD_PERCENT_COMPLETE, liveStreamInfo.getPercentComplete() );
		values.put( LiveStreamConstants.FIELD_CREATED, null != liveStreamInfo.getCreated() ? liveStreamInfo.getCreated().getMillis() : -1 );
		values.put( LiveStreamConstants.FIELD_LAST_MODIFIED, null != liveStreamInfo.getLastModified() ? liveStreamInfo.getLastModified().getMillis() : -1 );
		values.put( LiveStreamConstants.FIELD_RELATIVE_URL, liveStreamInfo.getRelativeUrl() );
		values.put( LiveStreamConstants.FIELD_FULL_URL, liveStreamInfo.getFullUrl() );
		values.put( LiveStreamConstants.FIELD_STATUS_STR, liveStreamInfo.getStatusStr() );
		values.put( LiveStreamConstants.FIELD_STATUS_INT, liveStreamInfo.getStatusInt() );
		values.put( LiveStreamConstants.FIELD_STATUS_MESSAGE, liveStreamInfo.getStatusMessage() );
		values.put( LiveStreamConstants.FIELD_SOURCE_FILE, liveStreamInfo.getSourceFile() );
		values.put( LiveStreamConstants.FIELD_SOURCE_HOST, liveStreamInfo.getSourceHost() );
		values.put( LiveStreamConstants.FIELD_SOURCE_WIDTH, liveStreamInfo.getSourceWidth() );
		values.put( LiveStreamConstants.FIELD_SOURCE_HEIGHT, liveStreamInfo.getSourceHeight() );
		values.put( LiveStreamConstants.FIELD_AUDIO_ONLY_BITRATE, liveStreamInfo.getAudioOnlyBitrate() );
		
		if( channelId > 0 ) {
			values.put( LiveStreamConstants.FIELD_CHAN_ID, channelId );
		}
		
		if( null != startTime ) {
			values.put( LiveStreamConstants.FIELD_START_TIME, startTime.getMillis() );
		}
		
		values.put( LiveStreamConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		values.put( LiveStreamConstants.FIELD_LAST_MODIFIED, lastModified.getMillis() );
		
//		Log.v( TAG, "convertLiveStreamToContentValues : exit" );
		return values;
	}

}
