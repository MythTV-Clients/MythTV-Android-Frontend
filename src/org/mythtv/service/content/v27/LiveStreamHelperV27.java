/**
 * 
 */
package org.mythtv.service.content.v27;

import org.joda.time.DateTime;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.client.ui.preferences.PlaybackProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.content.LiveStreamConstants;
import org.mythtv.db.preferences.PlaybackProfileDaoHelper;
import org.mythtv.service.dvr.v27.RecordedHelperV27;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.Bool;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v027.MythServicesTemplate;
import org.mythtv.services.api.v027.beans.LiveStreamInfo;
import org.mythtv.services.api.v027.beans.Program;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
public class LiveStreamHelperV27 extends AbstractBaseHelper {

	private static final String TAG = LiveStreamHelperV27.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v027;
	
	private static PlaybackProfileDaoHelper mPlaybackProfileDaoHelper = PlaybackProfileDaoHelper.getInstance();

	private static LiveStreamHelperV27 singleton;
	
	/**
	 * Returns the one and only LiveStreamHelperV27. init() must be called before 
	 * any 
	 * @return
	 */
	public static LiveStreamHelperV27 getInstance() {
		if( null == singleton ) {
			
			synchronized( LiveStreamHelperV27.class ) {

				if( null == singleton ) {
					singleton = new LiveStreamHelperV27();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private LiveStreamHelperV27() { }

	public boolean create( final Context context, final LocationProfile locationProfile, final Integer channelId, final DateTime startTime ) {
		Log.v( TAG, "create : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "create : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );

			return false;
		}
		
		MythServicesTemplate mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
		PlaybackProfile selectedPlaybackProfile = null;
		LocationType locationType = locationProfile.getType();
			
		if( locationType.equals( LocationType.HOME ) ) {
			selectedPlaybackProfile = mPlaybackProfileDaoHelper.findSelectedHomeProfile( context );
		} else if( locationType.equals( LocationType.AWAY ) ) {
			selectedPlaybackProfile = mPlaybackProfileDaoHelper.findSelectedAwayProfile( context );
		}

		if( null != selectedPlaybackProfile ) {
				
			try {
				Program program = RecordedHelperV27.getInstance().findRecorded( context, locationProfile, channelId, startTime );
				
				if( null != program ) {
					ResponseEntity<LiveStreamInfo> wrapper = mMythServicesTemplate.contentOperations().
							addLiveStream( program.getRecording().getStorageGroup(), program.getFileName(), program.getHostName(), null, null,
								selectedPlaybackProfile.getHeight(), selectedPlaybackProfile.getVideoBitrate(), 
								selectedPlaybackProfile.getAudioBitrate(), selectedPlaybackProfile.getAudioSampleRate(),
								ETagInfo.createEmptyETag() );

					if( wrapper.getStatusCode().equals( HttpStatus.OK ) ) {
						LiveStreamInfo liveStreamInfo = wrapper.getBody();

						save( context, locationProfile, liveStreamInfo, program );

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

	public boolean update( final Context context, final LocationProfile locationProfile, final Integer channelId, final DateTime startTime ) {
		Log.v( TAG, "update : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "update : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );

			return false;
		}
		
		MythServicesTemplate mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
		try {
			Program program = RecordedHelperV27.getInstance().findRecorded( context, locationProfile, channelId, startTime );
			
			if( null != program ) {
				LiveStreamInfo liveStream = findLiveStream( context, locationProfile, channelId, startTime );
				
				ResponseEntity<LiveStreamInfo> wrapper = mMythServicesTemplate.contentOperations().getLiveStream( liveStream.getId(), ETagInfo.createEmptyETag() );
				if( wrapper.getStatusCode().equals( HttpStatus.OK ) ) {
					LiveStreamInfo updated = wrapper.getBody();

					if( !"Unknown status value".equalsIgnoreCase( updated.getStatusStr() ) ) {
						save( context, locationProfile, updated, program );
					} else {
						deleteLiveStream( context, locationProfile, channelId, startTime );
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
		
		try {
			Program program = RecordedHelperV27.getInstance().findRecorded( context, locationProfile, channelId, startTime );
			
			if( null != program ) {
				LiveStreamInfo liveStream = findLiveStream( context, locationProfile, channelId, startTime );
				
				ResponseEntity<Bool> wrapper = mMythServicesTemplate.contentOperations().removeLiveStream( liveStream.getId(), ETagInfo.createEmptyETag() );
				if( wrapper.getStatusCode().equals( HttpStatus.OK ) ) {
					Bool bool = wrapper.getBody();
					if( bool.getValue() ) {
						boolean removed = deleteLiveStream( context, (long) liveStream.getId() );
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
		String[] selectionArgs = new String[] { String.valueOf( channelId ), String.valueOf( startTime ) };
		
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
			throw new RuntimeException( "LiveStreamHelperV27 is not initialized" );
		
		int deleted = context.getContentResolver().delete( ContentUris.withAppendedId( LiveStreamConstants.CONTENT_URI, id ), null, null );
		if( deleted == 1 ) {
			Log.v( TAG, "deleteLiveStream : exit" );
			
			return true;
		}
		
		Log.d( TAG, "deleteLiveStream : exit, live stream NOT deleted" );
		return false;
	}

	/**
	 * @param liveStreamInfo
	 * @return
	 */
	public boolean deleteLiveStream( final Context context, final LocationProfile locationProfile, final Integer channelId, final DateTime startTime ) {
		Log.d( TAG, "deleteLiveStream : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "LiveStreamHelperV27 is not initialized" );
		
		String selection = LiveStreamConstants.FIELD_CHAN_ID + " = ? AND " + LiveStreamConstants.FIELD_START_TIME + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( channelId ), String.valueOf( startTime ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, LiveStreamConstants.TABLE_NAME );
		
		int deleted = context.getContentResolver().delete( LiveStreamConstants.CONTENT_URI, selection, selectionArgs );
		if( deleted == 1 ) {
			Log.v( TAG, "deleteLiveStream : exit" );
			
			return true;
		}
		
		Log.d( TAG, "deleteLiveStream : exit, live stream NOT deleted" );
		return false;
	}

	// internal helpers
	
	private boolean save( final Context context, final LocationProfile locationProfile, LiveStreamInfo liveStreamInfo, final Program program ) {
		Log.d( TAG, "save : enter" );

		if( null == context ) 
			throw new RuntimeException( "LiveStreamHelperV27 is not initialized" );
		
		boolean saved = false;
		
		ContentValues values = convertLiveStreamInfoToContentValues( locationProfile, liveStreamInfo, program );

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
			Log.v( TAG, "convertCursorToLiveStreamInfo : chanId=" + cursor.getString( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_CHAN_ID ) ) );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_START_TIME ) != -1 ) {
			Log.v( TAG, "convertCursorToLiveStreamInfo : startTime=" + new DateTime( cursor.getLong( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_START_TIME ) ) ) );
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
		liveStreamInfo.setRelativeURL( relativeUrl );
		liveStreamInfo.setFullURL( fullUrl );
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

	private ContentValues convertLiveStreamInfoToContentValues( final LocationProfile locationProfile, final LiveStreamInfo liveStreamInfo, final Program program ) {
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
		values.put( LiveStreamConstants.FIELD_RELATIVE_URL, liveStreamInfo.getRelativeURL() );
		values.put( LiveStreamConstants.FIELD_FULL_URL, liveStreamInfo.getFullURL() );
		values.put( LiveStreamConstants.FIELD_STATUS_STR, liveStreamInfo.getStatusStr() );
		values.put( LiveStreamConstants.FIELD_STATUS_INT, liveStreamInfo.getStatusInt() );
		values.put( LiveStreamConstants.FIELD_STATUS_MESSAGE, liveStreamInfo.getStatusMessage() );
		values.put( LiveStreamConstants.FIELD_SOURCE_FILE, liveStreamInfo.getSourceFile() );
		values.put( LiveStreamConstants.FIELD_SOURCE_HOST, liveStreamInfo.getSourceHost() );
		values.put( LiveStreamConstants.FIELD_SOURCE_WIDTH, liveStreamInfo.getSourceWidth() );
		values.put( LiveStreamConstants.FIELD_SOURCE_HEIGHT, liveStreamInfo.getSourceHeight() );
		values.put( LiveStreamConstants.FIELD_AUDIO_ONLY_BITRATE, liveStreamInfo.getAudioOnlyBitrate() );
		values.put( LiveStreamConstants.FIELD_CHAN_ID, program.getChannel().getChanId() );
		values.put( LiveStreamConstants.FIELD_START_TIME, program.getStartTime().getMillis() );
		values.put( LiveStreamConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		
//		Log.v( TAG, "convertLiveStreamToContentValues : exit" );
		return values;
	}

}
