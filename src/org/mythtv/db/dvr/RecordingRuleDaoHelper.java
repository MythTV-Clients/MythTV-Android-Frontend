/**
 * 
 */
package org.mythtv.db.dvr;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractDaoHelper;
import org.mythtv.db.dvr.model.RecRule;
import org.mythtv.service.util.DateUtils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author Daniel Frey
 *
 */
public class RecordingRuleDaoHelper extends AbstractDaoHelper {

	private static final String TAG = RecordingRuleDaoHelper.class.getSimpleName();
	
	private static RecordingRuleDaoHelper singleton = null;

	/**
	 * Returns the one and only RecordingRuleDaoHelper. init() must be called before 
	 * any 
	 * 
	 * @return
	 */
	public static RecordingRuleDaoHelper getInstance() {
		if( null == singleton ) {

			synchronized( RecordingRuleDaoHelper.class ) {

				if( null == singleton ) {
					singleton = new RecordingRuleDaoHelper();
				}
			
			}

		}
		
		return singleton;
	}

	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private RecordingRuleDaoHelper() {
		super();
	}

	/**
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public List<RecRule> findAll( final Context context, final LocationProfile locationProfile, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
//		Log.d( TAG, "findAll : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordingRuleDaoHelper is not initialized" );
		
		List<RecRule> recRules = new ArrayList<RecRule>();
		
		selection = appendLocationHostname( context, locationProfile, selection, RecordingRuleConstants.TABLE_NAME );
		
		Cursor cursor = context.getContentResolver().query( RecordingRuleConstants.CONTENT_URI, projection, selection, selectionArgs, sortOrder );
		while( cursor.moveToNext() ) {
			RecRule recRule = convertCursorToRecRule( cursor );
			recRules.add( recRule );
		}
		cursor.close();

//		Log.d( TAG, "findAll : exit" );
		return recRules;
	}
	
	/**
	 * @return
	 */
	public List<RecRule> findAll( final Context context, final LocationProfile locationProfile ) {
//		Log.d( TAG, "findAll : enter" );
		
		List<RecRule> recRules = findAll( context, locationProfile, null, null, null, null );
		
//		Log.d( TAG, "findAll : exit" );
		return recRules;
	}
	
	/**
	 * @param id
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public RecRule findOne( final Context context, final LocationProfile locationProfile, Long id, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
//		Log.d( TAG, "findOne : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordingRuleDaoHelper is not initialized" );
		
		RecRule recRule = null;
		
		Uri uri = RecordingRuleConstants.CONTENT_URI;
		if( null != id && id > 0 ) {
//			Log.d( TAG, "findOne : appending id=" + id );
			uri = ContentUris.withAppendedId( RecordingRuleConstants.CONTENT_URI, id );
		}
		
		selection = appendLocationHostname( context, locationProfile, selection, RecordingRuleConstants.TABLE_NAME );
		
		Cursor cursor = context.getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );
		if( cursor.moveToFirst() ) {
			recRule = convertCursorToRecRule( cursor );
		}
		cursor.close();
		
//		Log.d( TAG, "findOne : exit" );
		return recRule;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public RecRule findOne( final Context context, final LocationProfile locationProfile, final Long id ) {
//		Log.d( TAG, "findOne : enter" );
		
		RecRule recRule = findOne( context, locationProfile, id, null, null, null, null );
//		if( null != recRule ) {
//			Log.v( TAG, "findOne : recRule=" + recRule.toString() );
//		}
		
		
//		Log.d( TAG, "findOne : exit" );
		return recRule;
	}

	/**
	 * @param recRuleId
	 * @return
	 */
	public RecRule findByRecordingRuleId( final Context context, final LocationProfile locationProfile, final Long recordingRuleId ) {
//		Log.d( TAG, "findByRecordingRuleId : enter" );
		
//		Log.d( TAG, "findByRecordingRuleId : recordingRuleId=" + recordingRuleId );

		String selection = RecordingRuleConstants.FIELD_REC_RULE_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( recordingRuleId ) };
		
		RecRule recRule = findOne( context, locationProfile, null, null, selection, selectionArgs, null );
//		if( null != recRule ) {
//			Log.v( TAG, "findByRecordingRuleId : recRule=" + recRule.toString() );
//		}
				
//		Log.d( TAG, "findByRecordingRuleId : exit" );
		return recRule;
	}

	/**
	 * @param recRule
	 * @return
	 */
	public int save( final Context context, final LocationProfile locationProfile, RecRule recRule ) {
//		Log.d( TAG, "save : enter" );

//		Log.d( TAG, "save : recRule=" + recRule.toString() );

		if( null == context ) 
			throw new RuntimeException( "RecordingRuleDaoHelper is not initialized" );
		
		ContentValues values = convertRecRuleToContentValues( locationProfile, DateUtils.convertUtc( new DateTime( System.currentTimeMillis() ) ), recRule );

		String[] projection = new String[] { RecordingRuleConstants._ID };
		String selection = RecordingRuleConstants.FIELD_REC_RULE_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( recRule.getId() ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, RecordingRuleConstants.TABLE_NAME );
		
		int updated = -1;
		Cursor cursor = context.getContentResolver().query( RecordingRuleConstants.CONTENT_URI, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
//			Log.v( TAG, "save : updating existing recRule info" );
			long id = cursor.getLong( cursor.getColumnIndexOrThrow( RecordingRuleConstants._ID ) );
			
			updated = context.getContentResolver().update( ContentUris.withAppendedId( RecordingRuleConstants.CONTENT_URI, id ), values, null, null );
		} else {
//			Log.v( TAG, "save : inserting new recRule info" );
			
			Uri inserted = context.getContentResolver().insert( RecordingRuleConstants.CONTENT_URI, values );
			if( null != inserted ) {
				updated = 1;
			}
			
		}
		cursor.close();
//		Log.v( TAG, "save : updated=" + updated );

//		RecRule updatedRecRule = findByRecordingRuleId( context, locationProfile, (long) recRule.getId() );
//		Log.d( TAG, "save : updatedRecRule=" + updatedRecRule.toString() );
		
//		Log.d( TAG, "save : exit" );
		return updated;
	}

	/**
	 * @return
	 */
	public int deleteAll( final Context context ) {
//		Log.d( TAG, "deleteAll : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordingRuleDaoHelper is not initialized" );
		
		int deleted = context.getContentResolver().delete( RecordingRuleConstants.CONTENT_URI, null, null );
//		Log.v( TAG, "deleteAll : deleted=" + deleted );
		
//		Log.d( TAG, "deleteAll : exit" );
		return deleted;
	}

	/**
	 * @param id
	 * @return
	 */
	public int delete( final Context context, final Long id ) {
//		Log.d( TAG, "delete : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordingRuleDaoHelper is not initialized" );
		
		int deleted = context.getContentResolver().delete( ContentUris.withAppendedId( RecordingRuleConstants.CONTENT_URI, id ), null, null );
//		Log.v( TAG, "delete : deleted=" + deleted );
		
//		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param context
	 * @param recRule
	 * @return
	 */
	public int delete( final Context context, final LocationProfile locationProfile, RecRule recRule ) {
//		Log.d( TAG, "delete : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordingRuleDaoHelper is not initialized" );
		
		String selection = RecordingRuleConstants.FIELD_REC_RULE_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( recRule.getId() ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, RecordingRuleConstants.TABLE_NAME );
		
		int deleted = context.getContentResolver().delete( RecordingRuleConstants.CONTENT_URI, selection, selectionArgs );
//		Log.v( TAG, "delete : deleted=" + deleted );
		
//		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param cursor
	 * @return
	 */
	public RecRule convertCursorToRecRule( Cursor cursor ) {
//		Log.v( TAG, "convertCursorToRecRule : enter" );

		int recordingRuleId = -1, parentId = -1, season = -1, episode = -1, channelId = -1, day = -1, findId = -1, recPriority = -1, preferredInput = -1, startOffset = -1, endOffset = -1, filter = -1, maxEpisodes = -1, transcoder = -1, averageDelay = -1;
		String title = "", subTitle = "", description = "", category = "", seriesId = "", programId = "", inetref = "", callSign = "", time = "", type = "", searchType = "", dupMethod = "", dupIn = "", recProfile = "", recGroup = "", storageGroup = "", playGroup = "";
		boolean inactive = false, autoExpire = false, maxNewest = false, autoCommflag = false, autoTranscode = false, autoMetadata = false, autoUserJob1 = false, autoUserJob2 = false, autoUserJob3 = false, autoUserJob4 = false;
		DateTime startTime = null, endTime = null, nextRecording = null, lastRecorded = null, lastDeleted = null;
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_REC_RULE_ID ) != -1 ) {
			recordingRuleId = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_REC_RULE_ID ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_PARENT_ID ) != -1 ) {
			parentId = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_PARENT_ID ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_INACTIVE ) != -1 ) {
			inactive = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_INACTIVE ) ) == 0 ? false : true;
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_TITLE ) != -1 ) {
			title = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_TITLE ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_SUB_TITLE ) != -1 ) {
			subTitle = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_SUB_TITLE ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_DESCRIPTION ) != -1 ) {
			description = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_DESCRIPTION ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_SEASON ) != -1 ) {
			season = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_SEASON ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_EPISODE ) != -1 ) {
			episode = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_EPISODE ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_CATEGORY ) != -1 ) {
			category = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_CATEGORY ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_START_TIME ) != -1 ) {
			startTime = new DateTime( cursor.getLong( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_START_TIME ) ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_END_TIME ) != -1 ) {
			endTime = new DateTime( cursor.getLong( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_END_TIME ) ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_SERIES_ID ) != -1 ) {
			seriesId = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_SERIES_ID ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_PROGRAM_ID ) != -1 ) {
			programId = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_PROGRAM_ID ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_INETREF ) != -1 ) {
			inetref = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_INETREF ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_CHAN_ID ) != -1 ) {
			channelId = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_CHAN_ID ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_CALLSIGN ) != -1 ) {
			callSign = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_CALLSIGN ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_DAY ) != -1 ) {
			day = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_DAY ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_TIME ) != -1 ) {
			time = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_TIME ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_FIND_ID ) != -1 ) {
			findId = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_FIND_ID ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_TYPE ) != -1 ) {
			type = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_TYPE ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_SEARCH_TYPE ) != -1 ) {
			searchType = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_SEARCH_TYPE ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_REC_PRIORITY ) != -1 ) {
			recPriority = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_REC_PRIORITY ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_PREFERRED_INPUT ) != -1 ) {
			preferredInput = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_PREFERRED_INPUT ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_START_OFFSET ) != -1 ) {
			startOffset = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_START_OFFSET ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_END_OFFSET ) != -1 ) {
			endOffset = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_END_OFFSET ) );
		}
		
		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_DUP_METHOD ) != -1 ) {
			dupMethod = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_DUP_METHOD ) );
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_DUP_IN ) != -1 ) {
			dupIn = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_DUP_IN ) );
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_FILTER ) != -1 ) {
			filter = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_FILTER ) );
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_REC_PROFILE ) != -1 ) {
			recProfile = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_REC_PROFILE ) );
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_REC_GROUP ) != -1 ) {
			recGroup = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_REC_GROUP ) );
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_STORAGE_GROUP ) != -1 ) {
			storageGroup = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_STORAGE_GROUP ) );
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_PLAY_GROUP ) != -1 ) {
			playGroup = cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_PLAY_GROUP ) );
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_EXPIRE ) != -1 ) {
			autoExpire = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_EXPIRE ) ) == 0 ? false : true;
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_MAX_EPISODES ) != -1 ) {
			maxEpisodes = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_MAX_EPISODES ) );
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_MAX_NEWEST ) != -1 ) {
			maxNewest = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_MAX_NEWEST ) ) == 0 ? false : true;
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_COMMFLAG ) != -1 ) {
			autoCommflag = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_COMMFLAG ) ) == 0 ? false : true;
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_TRANSCODE ) != -1 ) {
			autoTranscode = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_TRANSCODE ) ) == 0 ? false : true;
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_METADATA ) != -1 ) {
			autoMetadata = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_METADATA ) ) == 0 ? false : true;
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_USER_JOB_1 ) != -1 ) {
			autoUserJob1 = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_USER_JOB_1 ) ) == 0 ? false : true;
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_USER_JOB_2 ) != -1 ) {
			autoUserJob2 = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_USER_JOB_2 ) ) == 0 ? false : true;
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_USER_JOB_3 ) != -1 ) {
			autoUserJob3 = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_USER_JOB_3 ) ) == 0 ? false : true;
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_USER_JOB_4 ) != -1 ) {
			autoUserJob4 = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_AUTO_USER_JOB_4 ) ) == 0 ? false : true;
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_TRANSCODER ) != -1 ) {
			transcoder = cursor.getInt( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_TRANSCODER ) );
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_NEXT_RECORDING ) != -1 ) {
			nextRecording = new DateTime( cursor.getLong( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_NEXT_RECORDING ) ) );
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_LAST_RECORDED ) != -1 ) {
			lastRecorded = new DateTime( cursor.getLong( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_LAST_RECORDED ) ) );
		}

		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_LAST_DELETED ) != -1 ) {
			lastDeleted = new DateTime( cursor.getLong( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_LAST_DELETED ) ) );
		}
		
//		if( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_MASTER_HOSTNAME ) != -1 ) {
//			Log.v( TAG, "convertCursorToRecRule : hostname=" + cursor.getString( cursor.getColumnIndex( RecordingRuleConstants.TABLE_NAME + "_" + RecordingRuleConstants.FIELD_MASTER_HOSTNAME ) ) );
//		}

		RecRule recRule = new RecRule();
		recRule.setId( recordingRuleId );
		recRule.setParentId( parentId );
		recRule.setInactive( inactive );
		recRule.setTitle( title );
		recRule.setSubTitle( subTitle );
		recRule.setDescription( description );
		recRule.setSeason( season );
		recRule.setEpisode( episode );
		recRule.setCategory( category );
		recRule.setStartTime( startTime );
		recRule.setEndTime( endTime );
		recRule.setSeriesId( seriesId );
		recRule.setProgramId( programId );
		recRule.setInetref( inetref );
		recRule.setChanId( channelId );
		recRule.setCallSign( callSign );
		recRule.setDay( day );
		recRule.setTime( time );
		recRule.setFindId( findId );
		recRule.setType( type );
		recRule.setSearchType( searchType );
		recRule.setRecPriority( recPriority );
		recRule.setPreferredInput( preferredInput );
		recRule.setStartOffset( startOffset );
		recRule.setEndOffset( endOffset );
		recRule.setDupMethod( dupMethod );
		recRule.setDupIn( dupIn );
		recRule.setFilter( filter );
		recRule.setRecProfile( recProfile );
		recRule.setRecGroup( recGroup );
		recRule.setStorageGroup( storageGroup );
		recRule.setPlayGroup( playGroup );
		recRule.setAutoExpire( autoExpire );
		recRule.setMaxEpisodes( maxEpisodes );
		recRule.setMaxNewest( maxNewest );
		recRule.setAutoCommflag( autoCommflag );
		recRule.setAutoTranscode( autoTranscode );
		recRule.setAutoMetaLookup( autoMetadata );
		recRule.setAutoUserJob1( autoUserJob1 );
		recRule.setAutoUserJob2( autoUserJob2 );
		recRule.setAutoUserJob3( autoUserJob3 );
		recRule.setAutoUserJob4( autoUserJob4 );
		recRule.setTranscoder( transcoder );
		recRule.setNextRecording( nextRecording );
		recRule.setLastRecorded( lastRecorded );
		recRule.setLastDeleted( lastDeleted );
		recRule.setAverageDelay( averageDelay );
//		Log.v( TAG, "convertCursorToRecRule : recRule=" + recRule.toString() );
		
//		Log.v( TAG, "convertCursorToRecRule : exit" );
		return recRule;
	}

	// internal helpers

	private ContentValues[] convertRecRulesToContentValuesArray( final LocationProfile locationProfile, final DateTime lastModified, final List<RecRule> recRules ) {
//		Log.v( TAG, "convertRecRulesToContentValuesArray : enter" );
		
		if( null != recRules && !recRules.isEmpty() ) {
			
			ContentValues contentValues;
			List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();

			for( RecRule recRule : recRules ) {

				contentValues = convertRecRuleToContentValues( locationProfile, lastModified, recRule );
				contentValuesArray.add( contentValues );
				
			}			
			
			if( !contentValuesArray.isEmpty() ) {
				
//				Log.v( TAG, "convertRecRulesToContentValuesArray : exit" );
				return contentValuesArray.toArray( new ContentValues[ contentValuesArray.size() ] );
			}
			
		}
		
//		Log.v( TAG, "convertRecRulesToContentValuesArray : exit, no recRules to convert" );
		return null;
	}

	private ContentValues convertRecRuleToContentValues( final LocationProfile locationProfile, final DateTime lastModified, final RecRule recRule ) {
//		Log.v( TAG, "convertRecRuleToContentValues : enter" );
		
		DateTime startTimestamp = new DateTime( DateTimeZone.UTC );
		if( null != recRule.getStartTime() ) {
			startTimestamp = recRule.getStartTime();
		}

		DateTime endTimestamp = new DateTime( DateTimeZone.UTC );
		if( null != recRule.getEndTime() ) {
			endTimestamp = recRule.getEndTime();
		}

//		Log.v( TAG, "convertRecRuleToContentValues : recRule=" + recRule.toString() );
		ContentValues values = new ContentValues();
		values.put( RecordingRuleConstants.FIELD_REC_RULE_ID, recRule.getId() );
		values.put( RecordingRuleConstants.FIELD_PARENT_ID, recRule.getParentId() );
		values.put( RecordingRuleConstants.FIELD_INACTIVE, recRule.isInactive() ? 1 : 0 );
		values.put( RecordingRuleConstants.FIELD_TITLE, recRule.getTitle() );
		values.put( RecordingRuleConstants.FIELD_SUB_TITLE, recRule.getSubTitle() );
		values.put( RecordingRuleConstants.FIELD_DESCRIPTION, recRule.getDescription() );
		values.put( RecordingRuleConstants.FIELD_SEASON, recRule.getSeason() );
		values.put( RecordingRuleConstants.FIELD_EPISODE, recRule.getEpisode() );
		values.put( RecordingRuleConstants.FIELD_CATEGORY, recRule.getCategory() );
		values.put( RecordingRuleConstants.FIELD_START_TIME, startTimestamp.getMillis() );
		values.put( RecordingRuleConstants.FIELD_END_TIME, endTimestamp.getMillis() );
		values.put( RecordingRuleConstants.FIELD_SERIES_ID, recRule.getSeriesId() );
		values.put( RecordingRuleConstants.FIELD_PROGRAM_ID, recRule.getProgramId() );
		values.put( RecordingRuleConstants.FIELD_INETREF, recRule.getInetref() );
		values.put( RecordingRuleConstants.FIELD_CHAN_ID, recRule.getChanId() );
		values.put( RecordingRuleConstants.FIELD_CALLSIGN, recRule.getCallSign() );
		values.put( RecordingRuleConstants.FIELD_DAY, recRule.getDay() );
		values.put( RecordingRuleConstants.FIELD_TIME, recRule.getTime() );
		values.put( RecordingRuleConstants.FIELD_FIND_ID, recRule.getFindId() );
		values.put( RecordingRuleConstants.FIELD_TYPE, recRule.getType() );
		values.put( RecordingRuleConstants.FIELD_SEARCH_TYPE, recRule.getSearchType() );
		values.put( RecordingRuleConstants.FIELD_REC_PRIORITY, recRule.getRecPriority() );
		values.put( RecordingRuleConstants.FIELD_PREFERRED_INPUT, recRule.getPreferredInput() );
		values.put( RecordingRuleConstants.FIELD_START_OFFSET, recRule.getStartOffset() );
		values.put( RecordingRuleConstants.FIELD_END_OFFSET, recRule.getEndOffset() );
		values.put( RecordingRuleConstants.FIELD_DUP_METHOD, recRule.getDupMethod() );
		values.put( RecordingRuleConstants.FIELD_DUP_IN, recRule.getDupIn() );
		values.put( RecordingRuleConstants.FIELD_FILTER, recRule.getFilter() );
		values.put( RecordingRuleConstants.FIELD_REC_PROFILE, recRule.getRecProfile() );
		values.put( RecordingRuleConstants.FIELD_REC_GROUP, recRule.getRecGroup() );
		values.put( RecordingRuleConstants.FIELD_STORAGE_GROUP, recRule.getStorageGroup() );
		values.put( RecordingRuleConstants.FIELD_PLAY_GROUP, recRule.getPlayGroup() );
		values.put( RecordingRuleConstants.FIELD_AUTO_EXPIRE, recRule.isAutoExpire() ? 1 : 0 );
		values.put( RecordingRuleConstants.FIELD_MAX_EPISODES, recRule.getMaxEpisodes() );
		values.put( RecordingRuleConstants.FIELD_MAX_NEWEST, recRule.isMaxNewest() ? 1 : 0 );
		values.put( RecordingRuleConstants.FIELD_AUTO_COMMFLAG, recRule.isAutoCommflag() ? 1 : 0 );
		values.put( RecordingRuleConstants.FIELD_AUTO_TRANSCODE, recRule.isAutoTranscode() ? 1 : 0);
		values.put( RecordingRuleConstants.FIELD_AUTO_METADATA, recRule.isAutoMetaLookup() ? 1 : 0 );
		values.put( RecordingRuleConstants.FIELD_AUTO_USER_JOB_1, recRule.isAutoUserJob1() ? 1 : 0 );
		values.put( RecordingRuleConstants.FIELD_AUTO_USER_JOB_2, recRule.isAutoUserJob2() ? 1 : 0 );
		values.put( RecordingRuleConstants.FIELD_AUTO_USER_JOB_3, recRule.isAutoUserJob3() ? 1 : 0 );
		values.put( RecordingRuleConstants.FIELD_AUTO_USER_JOB_4, recRule.isAutoUserJob4() ? 1 : 0 );
		values.put( RecordingRuleConstants.FIELD_TRANSCODER, recRule.getTranscoder() );
		values.put( RecordingRuleConstants.FIELD_NEXT_RECORDING, null != recRule.getNextRecording() ? recRule.getNextRecording().getMillis() : -1 );
		values.put( RecordingRuleConstants.FIELD_LAST_RECORDED, null != recRule.getLastRecorded() ? recRule.getLastRecorded().getMillis() : -1 );
		values.put( RecordingRuleConstants.FIELD_LAST_DELETED, null != recRule.getLastDeleted() ? recRule.getLastDeleted().getMillis() : -1 );
		values.put( RecordingRuleConstants.FIELD_AVERAGE_DELAY, recRule.getAverageDelay() );
		values.put( RecordingRuleConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		values.put( RecordingRuleConstants.FIELD_LAST_MODIFIED_DATE, lastModified.getMillis() );
//		Log.v( TAG, "convertRecRuleToContentValues : values=" + values.toString() );
		
//		Log.v( TAG, "convertRecRuleToContentValues : exit" );
		return values;
	}

}
