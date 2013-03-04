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
package org.mythtv.db.content;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractDaoHelper;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.content.LiveStreamInfo;
import org.mythtv.services.api.dvr.Program;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class LiveStreamDaoHelper extends AbstractDaoHelper {

	protected static final String TAG = LiveStreamDaoHelper.class.getSimpleName();

	private static LiveStreamDaoHelper singleton = null;

	/**
	 * Returns the one and only LiveStreamDaoHelper. init() must be called before 
	 * any 
	 * 
	 * @return
	 */
	public static LiveStreamDaoHelper getInstance() {
		if( null == singleton ) {

			synchronized( LiveStreamDaoHelper.class ) {

				if( null == singleton ) {
					singleton = new LiveStreamDaoHelper();
				}
			
			}

		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private LiveStreamDaoHelper() {
		super();
	}
	
	/**
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public List<LiveStreamInfo> findAll( String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.d( TAG, "findAll : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LiveStreamDaoHelper is not initialized" );
		
		List<LiveStreamInfo> liveStreamInfos = new ArrayList<LiveStreamInfo>();
		
		selection = appendLocationHostname( selection, LiveStreamConstants.TABLE_NAME );
		
		Cursor cursor = mContext.getContentResolver().query( LiveStreamConstants.CONTENT_URI, projection, selection, selectionArgs, sortOrder );
		while( cursor.moveToNext() ) {
			LiveStreamInfo liveStreamInfo = convertCursorToLiveStreamInfo( cursor );
			liveStreamInfos.add( liveStreamInfo );
		}
		cursor.close();

		Log.d( TAG, "findAll : exit" );
		return liveStreamInfos;
	}
	
	/**
	 * @return
	 */
	public List<LiveStreamInfo> finalAll() {
		Log.d( TAG, "findAll : enter" );
		
		List<LiveStreamInfo> liveStreamInfos = findAll( null, null, null, null );
		
		Log.d( TAG, "findAll : exit" );
		return liveStreamInfos;
	}
	
	/**
	 * @param id
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public LiveStreamInfo findOne( Long id, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.d( TAG, "findOne : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LiveStreamDaoHelper is not initialized" );
		
		LiveStreamInfo liveStreamInfo = null;
		
		Uri uri = LiveStreamConstants.CONTENT_URI;
		if( null != id && id > 0 ) {
			Log.d( TAG, "findOne : appending id=" + id );
			uri = ContentUris.withAppendedId( LiveStreamConstants.CONTENT_URI, id );
		}
		
		selection = appendLocationHostname( selection, LiveStreamConstants.TABLE_NAME );
		
		Cursor cursor = mContext.getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );
		if( cursor.moveToFirst() ) {
			liveStreamInfo = convertCursorToLiveStreamInfo( cursor );
		}
		cursor.close();
		
		Log.d( TAG, "findOne : exit" );
		return liveStreamInfo;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public LiveStreamInfo findOne( Long id ) {
		Log.d( TAG, "findOne : enter" );
		
		LiveStreamInfo liveStreamInfo = findOne( id, null, null, null, null );
		if( null != liveStreamInfo ) {
			Log.v( TAG, "findOne : liveStreamInfo=" + liveStreamInfo.toString() );
		}
				
		Log.d( TAG, "findOne : exit" );
		return liveStreamInfo;
	}

	/**
	 * @param liveStreamId
	 * @return
	 */
	public LiveStreamInfo findByLiveStreamId( Long liveStreamId ) {
		Log.d( TAG, "findByLiveStreamId : enter" );
		
		String selection = LiveStreamConstants.FIELD_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( liveStreamId ) };
		
		LiveStreamInfo liveStreamInfo = findOne( null, null, selection, selectionArgs, null );
		if( null != liveStreamInfo ) {
			Log.v( TAG, "findByLiveStreamId : liveStreamInfo=" + liveStreamInfo.toString() );
		}
				
		Log.d( TAG, "findByLiveStreamId : exit" );
		return liveStreamInfo;
	}

	public LiveStreamInfo findByProgram( Program program ) {
		Log.d( TAG, "findByProgram : enter" );
		
		String selection = LiveStreamConstants.FIELD_CHAN_ID + " = ? AND " + LiveStreamConstants.FIELD_START_TIME + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( program.getChannelInfo().getChannelId() ), String.valueOf( program.getStartTime().getMillis() ) };
		
		LiveStreamInfo liveStreamInfo = findOne( null, null, selection, selectionArgs, null );
		if( null != liveStreamInfo ) {
			Log.v( TAG, "findByProgram : liveStreamInfo=" + liveStreamInfo.toString() );
		}
				
		Log.d( TAG, "findByProgram : exit" );
		return liveStreamInfo;
	}

	/**
	 * @param liveStreamInfo
	 * @return
	 */
	public int save( LiveStreamInfo liveStreamInfo, Program program ) {
		Log.d( TAG, "save : enter" );

		if( !this.isInitialized() ) 
			throw new RuntimeException( "LiveStreamDaoHelper is not initialized" );
		
		ContentValues values = convertLiveStreamInfoToContentValues( liveStreamInfo, program );

		String[] projection = new String[] { LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants._ID };
		String selection = LiveStreamConstants.FIELD_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( liveStreamInfo.getId() ) };
		
		selection = appendLocationHostname( selection, LiveStreamConstants.TABLE_NAME );
		
		int updated = -1;
		Cursor cursor = mContext.getContentResolver().query( LiveStreamConstants.CONTENT_URI, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
			Log.v( TAG, "save : updating existing liveStream info" );
			long id = cursor.getLong( cursor.getColumnIndexOrThrow( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants._ID ) );
			
			updated = mContext.getContentResolver().update( ContentUris.withAppendedId( LiveStreamConstants.CONTENT_URI, id ), values, null, null );
		} else {
			Log.v( TAG, "save : inserting new liveStream info" );
			Uri url = mContext.getContentResolver().insert( LiveStreamConstants.CONTENT_URI, values );
			if( ContentUris.parseId( url ) > 0 ) {
				updated = 1;
			}
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
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LiveStreamDaoHelper is not initialized" );
		
		int deleted = mContext.getContentResolver().delete( LiveStreamConstants.CONTENT_URI, null, null );
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
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LiveStreamDaoHelper is not initialized" );
		
		int deleted = mContext.getContentResolver().delete( ContentUris.withAppendedId( LiveStreamConstants.CONTENT_URI, id ), null, null );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param liveStreamInfo
	 * @return
	 */
	public int delete( LiveStreamInfo liveStreamInfo ) {
		Log.d( TAG, "delete : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LiveStreamDaoHelper is not initialized" );
		
		String selection = LiveStreamConstants.FIELD_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( liveStreamInfo.getId() ) };
		
		selection = appendLocationHostname( selection, LiveStreamConstants.TABLE_NAME );
		
		int deleted = mContext.getContentResolver().delete( LiveStreamConstants.CONTENT_URI, selection, selectionArgs );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	public int load( List<LiveStreamInfo> liveStreamInfos ) {
		Log.d( TAG, "load : enter" );
		
		if( !this.isInitialized() ) 
			throw new RuntimeException( "LiveStreamDaoHelper is not initialized" );
		
		int loaded = -1;
		
		ContentValues[] contentValuesArray = convertLiveStreamInfosToContentValuesArray( liveStreamInfos );
		if( null != contentValuesArray ) {
			Log.v( TAG, "processLiveStreams : liveStreams=" + contentValuesArray.length );

			loaded = mContext.getContentResolver().bulkInsert( LiveStreamConstants.CONTENT_URI, contentValuesArray );
			Log.v( TAG, "load : loaded=" + loaded );
		}
		
		
		Log.d( TAG, "load : exit" );
		return loaded;
	}
	
	/**
	 * @param cursor
	 * @return
	 */
	public LiveStreamInfo convertCursorToLiveStreamInfo( Cursor cursor ) {
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

		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_HOSTNAME ) != -1 ) {
			Log.v( TAG, "convertCursorToLiveStreamInfo : hostname=" + cursor.getString( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_HOSTNAME ) ) );
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

	// internal helpers

	private ContentValues[] convertLiveStreamInfosToContentValuesArray( final List<LiveStreamInfo> liveStreamInfos ) {
		Log.v( TAG, "convertLiveStreamInfosToContentValuesArray : enter" );
		
		if( null != liveStreamInfos && !liveStreamInfos.isEmpty() ) {
			
			ContentValues contentValues;
			List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();

			for( LiveStreamInfo liveStreamInfo : liveStreamInfos ) {

				Program program = new Program();
				
				String sourcefile = liveStreamInfo.getSourceFile();
				sourcefile = sourcefile.substring( sourcefile.lastIndexOf( '/' ) );
				sourcefile = sourcefile.substring( 0, sourcefile.indexOf( '.' ) - 1 );
				String[] sourceFileParts = sourcefile.split( "_" );
				
				Integer channelId = Integer.parseInt( sourceFileParts[ 0 ] );
				DateTime startTime = new DateTime( Long.parseLong( sourceFileParts[ 1 ] ) );
				
				program.setStartTime( startTime );
				
				ChannelInfo channelInfo = new ChannelInfo();
				channelInfo.setChannelId( channelId );
				program.setChannelInfo( channelInfo );
				
				contentValues = convertLiveStreamInfoToContentValues( liveStreamInfo, program );
				contentValuesArray.add( contentValues );
				
			}			
			
			if( !contentValuesArray.isEmpty() ) {
				
				Log.v( TAG, "convertLiveStreamInfosToContentValuesArray : exit" );
				return contentValuesArray.toArray( new ContentValues[ contentValuesArray.size() ] );
			}
			
		}
		
		Log.v( TAG, "convertLiveStreamInfosToContentValuesArray : exit, no liveStreamInfos to convert" );
		return null;
	}

	private ContentValues convertLiveStreamInfoToContentValues( final LiveStreamInfo liveStreamInfo, final Program program ) {
//		Log.v( TAG, "convertLiveStreamToContentValues : enter" );
		
		LocationProfile mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile();
		
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
		values.put( LiveStreamConstants.FIELD_CHAN_ID, program.getChannelInfo().getChannelId() );
		values.put( LiveStreamConstants.FIELD_START_TIME, program.getStartTime().getMillis() );
		values.put( LiveStreamConstants.FIELD_HOSTNAME, mLocationProfile.getHostname() );
		
//		Log.v( TAG, "convertLiveStreamToContentValues : exit" );
		return values;
	}

}
