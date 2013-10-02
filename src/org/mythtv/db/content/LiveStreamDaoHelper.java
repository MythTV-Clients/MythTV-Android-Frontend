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
import org.mythtv.db.channel.model.ChannelInfo;
import org.mythtv.db.content.model.LiveStreamInfo;
import org.mythtv.db.dvr.model.Program;

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
	public List<LiveStreamInfo> findAll( final Context context, final LocationProfile locationProfile, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.d( TAG, "findAll : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == locationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		List<LiveStreamInfo> liveStreamInfos = new ArrayList<LiveStreamInfo>();
		
		selection = appendLocationHostname( context, locationProfile, selection, LiveStreamConstants.TABLE_NAME );
		
		Cursor cursor = context.getContentResolver().query( LiveStreamConstants.CONTENT_URI, projection, selection, selectionArgs, sortOrder );
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
	public List<LiveStreamInfo> finalAll( final Context context, final LocationProfile locationProfile ) {
		Log.d( TAG, "findAll : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == locationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		List<LiveStreamInfo> liveStreamInfos = findAll( context, locationProfile, null, null, null, null );
		
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
	public LiveStreamInfo findOne( final Context context, final LocationProfile locationProfile, final Long id, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.d( TAG, "findOne : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == locationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		LiveStreamInfo liveStreamInfo = null;
		
		Uri uri = LiveStreamConstants.CONTENT_URI;
		if( null != id && id > 0 ) {
			Log.d( TAG, "findOne : appending id=" + id );
			uri = ContentUris.withAppendedId( LiveStreamConstants.CONTENT_URI, id );
		}
		
		selection = appendLocationHostname( context, locationProfile, selection, LiveStreamConstants.TABLE_NAME );
		
		Cursor cursor = context.getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );
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
	public LiveStreamInfo findOne( final Context context, final LocationProfile locationProfile, final Long id ) {
		Log.d( TAG, "findOne : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == locationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		LiveStreamInfo liveStreamInfo = findOne( context, locationProfile, id, null, null, null, null );
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
	public LiveStreamInfo findByLiveStreamId( final Context context, final LocationProfile locationProfile, final Long liveStreamId ) {
		Log.d( TAG, "findByLiveStreamId : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == locationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		String selection = LiveStreamConstants.FIELD_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( liveStreamId ) };
		
		LiveStreamInfo liveStreamInfo = findOne( context, locationProfile, null, null, selection, selectionArgs, null );
		if( null != liveStreamInfo ) {
			Log.v( TAG, "findByLiveStreamId : liveStreamInfo=" + liveStreamInfo.toString() );
		}
				
		Log.d( TAG, "findByLiveStreamId : exit" );
		return liveStreamInfo;
	}

	public LiveStreamInfo findByProgram( final Context context, final LocationProfile locationProfile, final Program program ) {
		Log.d( TAG, "findByProgram : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == locationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		if( null == program.getChannelInfo() ) {
			Log.d( TAG, "findByProgram : channel has not been set" );
			
			return null;
		}
		
		if( null == program.getStartTime() ) {
			Log.d( TAG, "findByProgram : startTime has not been set" );
			
			return null;
		}
		
		try {
			String selection = LiveStreamConstants.FIELD_CHAN_ID + " = ? AND " + LiveStreamConstants.FIELD_START_TIME + " = ?";
			String[] selectionArgs = new String[] { String.valueOf( program.getChannelInfo().getChannelId() ), String.valueOf( program.getStartTime().getMillis() ) };

			LiveStreamInfo liveStreamInfo = findOne( context, locationProfile, null, null, selection, selectionArgs, null );
			if( null != liveStreamInfo ) {
				Log.v( TAG, "findByProgram : liveStreamInfo=" + liveStreamInfo.toString() );
			}

			Log.d( TAG, "findByProgram : exit" );
			return liveStreamInfo;
		} catch( Exception e ) {
			Log.w( TAG, e );
		}
		
		Log.d( TAG, "findByProgram : exit, liveStreamInfo is null" );
		return null;
	}

	/**
	 * @param liveStreamInfo
	 * @return
	 */
	public int save( final Context context, final LocationProfile locationProfile, LiveStreamInfo liveStreamInfo, final Program program ) {
		Log.d( TAG, "save : enter" );

		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == locationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		ContentValues values = convertLiveStreamInfoToContentValues( locationProfile, liveStreamInfo, program );

		String[] projection = new String[] { LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants._ID };
		String selection = LiveStreamConstants.FIELD_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( liveStreamInfo.getId() ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, LiveStreamConstants.TABLE_NAME );
		
		int updated = -1;
		Cursor cursor = context.getContentResolver().query( LiveStreamConstants.CONTENT_URI, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
			Log.v( TAG, "save : updating existing liveStream info" );
			long id = cursor.getLong( cursor.getColumnIndexOrThrow( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants._ID ) );
			
			updated = context.getContentResolver().update( ContentUris.withAppendedId( LiveStreamConstants.CONTENT_URI, id ), values, null, null );
		} else {
			Log.v( TAG, "save : inserting new liveStream info" );
			Uri url = context.getContentResolver().insert( LiveStreamConstants.CONTENT_URI, values );
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
	public int deleteAll( final Context context ) {
		Log.d( TAG, "deleteAll : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		int deleted = context.getContentResolver().delete( LiveStreamConstants.CONTENT_URI, null, null );
		Log.v( TAG, "deleteAll : deleted=" + deleted );
		
		Log.d( TAG, "deleteAll : exit" );
		return deleted;
	}

	/**
	 * @param id
	 * @return
	 */
	public int delete( final Context context, final Long id ) {
		Log.d( TAG, "delete : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		int deleted = context.getContentResolver().delete( ContentUris.withAppendedId( LiveStreamConstants.CONTENT_URI, id ), null, null );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param liveStreamInfo
	 * @return
	 * 
	 * @deprecated
	 */
	public int delete( final Context context, final LocationProfile locationProfile, LiveStreamInfo liveStreamInfo ) {
		Log.d( TAG, "delete : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "LiveStreamDaoHelper is not initialized" );
		
		String selection = LiveStreamConstants.FIELD_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( liveStreamInfo.getId() ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, LiveStreamConstants.TABLE_NAME );
		
		int deleted = context.getContentResolver().delete( LiveStreamConstants.CONTENT_URI, selection, selectionArgs );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param cursor
	 * @return
	 */
	public static LiveStreamInfo convertCursorToLiveStreamInfo( Cursor cursor ) {
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

	public static ContentValues[] convertLiveStreamInfosToContentValuesArray( final Context context, final LocationProfile locationProfile, final List<LiveStreamInfo> liveStreamInfos ) {
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
				
				contentValues = convertLiveStreamInfoToContentValues( locationProfile, liveStreamInfo, program );
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

	public static ContentValues convertLiveStreamInfoToContentValues( final LocationProfile locationProfile, final LiveStreamInfo liveStreamInfo, final Program program ) {
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
		values.put( LiveStreamConstants.FIELD_CHAN_ID, program.getChannelInfo().getChannelId() );
		values.put( LiveStreamConstants.FIELD_START_TIME, program.getStartTime().getMillis() );
		values.put( LiveStreamConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		
//		Log.v( TAG, "convertLiveStreamToContentValues : exit" );
		return values;
	}

}
