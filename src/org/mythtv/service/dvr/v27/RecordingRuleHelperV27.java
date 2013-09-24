/**
 * 
 */
package org.mythtv.service.dvr.v27;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.dvr.RecordingRuleConstants;
import org.mythtv.db.dvr.model.RecRule;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.channel.v27.ChannelHelperV27;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v027.MythServicesTemplate;
import org.mythtv.services.api.v027.beans.ChannelInfo;
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
public class RecordingRuleHelperV27 extends AbstractBaseHelper {

	private static final String TAG = RecordingRuleHelperV27.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v027;
	
	private static final String[] recRuleProjection = new String[] { RecordingRuleConstants._ID };
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static RecordingRuleHelperV27 singleton;
	
	/**
	 * Returns the one and only RecordingRuleHelperV27. init() must be called before 
	 * any 
	 * @return
	 */
	public static RecordingRuleHelperV27 getInstance() {
		if( null == singleton ) {
			
			synchronized( RecordingRuleHelperV27.class ) {

				if( null == singleton ) {
					singleton = new RecordingRuleHelperV27();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private RecordingRuleHelperV27() { }

	public boolean process( final Context context, final LocationProfile locationProfile ) {
		Log.v( TAG, "process : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
		boolean passed = true;

		try {

			downloadRecordinRules( context, locationProfile );
			
		} catch( Exception e ) {
			Log.e( TAG, "process : error", e );
		
			passed = false;
		}

		Log.v( TAG, "process : exit" );
		return passed;
	}

	public boolean add( final Context context, final LocationProfile locationProfile, final RecRule recordingRule ) {
		Log.v( TAG, "add : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "add : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
		boolean passed = true;

		try {

			addRecordingRule( context, locationProfile, recordingRule );
			
		} catch( Exception e ) {
			Log.e( TAG, "add : error", e );
		
			passed = false;
		}

		Log.v( TAG, "add : exit" );
		return passed;
	}

	public boolean update( final Context context, final LocationProfile locationProfile, final RecRule recordingRule ) {
		Log.v( TAG, "update : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "update : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
		boolean passed = true;

		try {

			passed = updateRecordingRule( context, locationProfile, recordingRule );
			
		} catch( Exception e ) {
			Log.e( TAG, "update : error", e );
		
			passed = false;
		}

		Log.v( TAG, "update : exit" );
		return passed;
	}

	public boolean remove( final Context context, final LocationProfile locationProfile, final RecRule recordingRule ) {
		Log.v( TAG, "remove : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "remove : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
		boolean passed = true;

		try {

			passed = removeRecordingRule( context, locationProfile, recordingRule );
			
		} catch( Exception e ) {
			Log.e( TAG, "remove : error", e );
		
			passed = false;
		}

		Log.v( TAG, "remove : exit" );
		return passed;
	}

	// internal helpers
	
	private void downloadRecordinRules( final Context context, final LocationProfile locationProfile ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "downloadRecordinRules : enter" );
	
		EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( context, locationProfile, "GetRecordScheduleList", "" );
		Log.d( TAG, "downloadRecordinRules : etag=" + etag.getValue() );
		
		ResponseEntity<org.mythtv.services.api.v027.beans.RecRuleList> responseEntity = mMythServicesTemplate.dvrOperations().getRecordScheduleList( null, null, etag );

		DateTime date = new DateTime( DateTimeZone.UTC );
		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "downloadRecordinRules : GetRecordScheduleList returned 200 OK" );
			org.mythtv.services.api.v027.beans.RecRuleList recRuleList = responseEntity.getBody();

			if( null != recRuleList.getRecRules() ) {

				load( context, locationProfile, recRuleList.getRecRules() );	

				if( null != etag.getValue() ) {
					Log.i( TAG, "downloadRecordinRules : saving etag: " + etag.getValue() );
					
					etag.setEndpoint( "GetRecordScheduleList" );
					etag.setDate( date );
					etag.setMasterHostname( locationProfile.getHostname() );
					etag.setLastModified( date );
					mEtagDaoHelper.save( context, locationProfile, etag );
				}

			}

		}

		if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
			Log.i( TAG, "downloadRecordinRules : GetRecordScheduleList returned 304 Not Modified" );

			if( null != etag.getValue() ) {
				Log.i( TAG, "downloadRecordinRules : saving etag: " + etag.getValue() );

				etag.setLastModified( date );
				mEtagDaoHelper.save( context, locationProfile, etag );
			}

		}

		Log.v( TAG, "downloadRecordinRules : exit" );
	}

	private int load( final Context context, final LocationProfile locationProfile, final org.mythtv.services.api.v027.beans.RecRule[] recordingRules ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "load : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordingRuleHelperV27 is not initialized" );
		
		DateTime lastModified = new DateTime( DateTimeZone.UTC );
		
		int processed = -1;
		int count = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		for( org.mythtv.services.api.v027.beans.RecRule recordingRule : recordingRules ) {

			processRecordingRule( context, locationProfile, ops, recordingRule, lastModified, count );
			count++;
			
			if( count > BATCH_COUNT_LIMIT ) {
//				Log.i( TAG, "load : applying batch for '" + count + "' transactions, processing programs" );
				
				processBatch( context, ops, processed, count );
				
				count = 0;
			}
			
		}

		processBatch( context, ops, processed, count );

		deleteRecordingRules( context, locationProfile, ops, lastModified );
		
		processBatch( context, ops, processed, count );

//		Log.v( TAG, "load : exit" );
		return processed;
	}

	private void processRecordingRule( final Context context, final LocationProfile locationProfile, ArrayList<ContentProviderOperation> ops, org.mythtv.services.api.v027.beans.RecRule recRule, DateTime lastModified, int count ) {
		Log.d( TAG, "processRecordingRule : enter" );

		String recRuleSelection = RecordingRuleConstants.FIELD_REC_RULE_ID + " = ?";

		recRuleSelection = appendLocationHostname( context, locationProfile, recRuleSelection, RecordingRuleConstants.TABLE_NAME );

		ContentValues recRuleValues = convertRecRuleToContentValues( locationProfile, lastModified, recRule );
		Cursor recRuleCursor = context.getContentResolver().query( RecordingRuleConstants.CONTENT_URI, recRuleProjection, recRuleSelection, new String[] { String.valueOf( recRule.getId() ) }, null );
		if( recRuleCursor.moveToFirst() ) {
			Long id = recRuleCursor.getLong( recRuleCursor.getColumnIndexOrThrow( RecordingRuleConstants._ID ) );
			Log.v( TAG, "processRecordingRule : updating recRule " + id + ":" + recRule.getId() + ":" + recRule.getTitle() );

			ops.add( 
				ContentProviderOperation.newUpdate( ContentUris.withAppendedId( RecordingRuleConstants.CONTENT_URI, id ) )
					.withValues( recRuleValues )
					.build()
			);

		} else {
			Log.v( TAG, "processRecordingRule : adding recRule " + recRule.getId() + ":" + recRule.getTitle() );

			ops.add(  
				ContentProviderOperation.newInsert( RecordingRuleConstants.CONTENT_URI )
					.withValues( recRuleValues )
					.build()
			);

		}
		recRuleCursor.close();
		count++;
		
		Log.d( TAG, "processRecordingRule : exit" );
	}
	
	private void deleteRecordingRules( final Context context, final LocationProfile locationProfile, ArrayList<ContentProviderOperation> ops, DateTime lastModified ) {
		Log.d( TAG, "deleteRecordingRules : enter" );

//		Log.v( TAG, "load : remove deleted recordings" );
		String deletedSelection = RecordingRuleConstants.TABLE_NAME + "." + RecordingRuleConstants.FIELD_LAST_MODIFIED_DATE + " < ?";
		String[] deletedSelectionArgs = new String[] { String.valueOf( lastModified.getMillis() ) };
			
		deletedSelection = appendLocationHostname( context, locationProfile, deletedSelection, RecordingRuleConstants.TABLE_NAME );
			
//		Log.v( TAG, "load : deleting recRules" );
		ops.add(  
			ContentProviderOperation.newDelete( RecordingRuleConstants.CONTENT_URI )
				.withSelection( deletedSelection, deletedSelectionArgs )
				.build()
		);
		
		Log.d( TAG, "deleteRecordingRules : exit" );
	}
	
	private int addRecordingRule( final Context context, final LocationProfile locationProfile, final RecRule recordingRule ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "addRecordingRule : enter" );

		int ret = -1;
		
		org.mythtv.services.api.v027.beans.RecRule versionRecRule = convertRecRuleToRecRuleV27( recordingRule );
		if( null != versionRecRule ) {
		
			ChannelInfo channel = null;
			if( null != versionRecRule.getChanId() ) {
				channel = ChannelHelperV27.getInstance().findChannel( context, locationProfile, versionRecRule.getChanId() );
			}
			
			// update existing rule
			ResponseEntity<org.mythtv.services.api.Int> add = mMythServicesTemplate.dvrOperations().addRecordSchedule( 
				versionRecRule.getTitle(), versionRecRule.getSubTitle(), versionRecRule.getDescription(), 
				versionRecRule.getCategory(), versionRecRule.getStartTime(), versionRecRule.getEndTime(), versionRecRule.getSeriesId(), 
				versionRecRule.getProgramId(), versionRecRule.getChanId(), ( null != channel ? channel.getCallSign() : "" ), 
				versionRecRule.getFindDay(), versionRecRule.getFindTime(), versionRecRule.getParentId(), versionRecRule.isInactive(), 
				versionRecRule.getSeason(), versionRecRule.getEpisode(), versionRecRule.getInetref(), 
				versionRecRule.getType(), versionRecRule.getSearchType(), versionRecRule.getRecPriority(), 
				versionRecRule.getPreferredInput(), versionRecRule.getStartOffset(), versionRecRule.getEndOffset(), 
				versionRecRule.getDupMethod(), versionRecRule.getDupIn(), versionRecRule.getFilter(), versionRecRule.getRecProfile(), 
				versionRecRule.getRecGroup(), versionRecRule.getStorageGroup(), versionRecRule.getPlayGroup(), 
				versionRecRule.isAutoExpire(), versionRecRule.getMaxEpisodes(), versionRecRule.isMaxNewest(), 
				versionRecRule.isAutoCommflag(), versionRecRule.isAutoTranscode(), versionRecRule.isAutoMetaLookup(), 
				versionRecRule.isAutoUserJob1(), versionRecRule.isAutoUserJob2(), versionRecRule.isAutoUserJob3(), 
				versionRecRule.isAutoUserJob4(), versionRecRule.getTranscoder() 
			);
			if( add.getStatusCode().equals( HttpStatus.OK ) ) {
				
				if( add.getBody().getValue() > 0 ) {
					ret = add.getBody().getValue();
					
					downloadRecordinRules( context, locationProfile );
				}
				
			}
					
		}
		
		Log.d( TAG, "addRecordingRule : exit" );
		return ret;
	}

	private boolean updateRecordingRule( final Context context, final LocationProfile locationProfile, final RecRule recordingRule ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "updateRecordingRule : enter" );

		boolean ret = false;
		
		org.mythtv.services.api.v027.beans.RecRule versionRecRule = convertRecRuleToRecRuleV27( recordingRule );
		if( null != versionRecRule ) {
		
			ChannelInfo channel = null;
			if( null != versionRecRule.getChanId() ) {
				channel = ChannelHelperV27.getInstance().findChannel( context, locationProfile, versionRecRule.getChanId() );
			}
			
			// update existing rule
			ResponseEntity<org.mythtv.services.api.Bool> add = mMythServicesTemplate.dvrOperations().updateRecordSchedule( 
				versionRecRule.getId(), versionRecRule.getTitle(), versionRecRule.getSubTitle(), versionRecRule.getDescription(), 
				versionRecRule.getCategory(), versionRecRule.getStartTime(), versionRecRule.getEndTime(), versionRecRule.getSeriesId(), 
				versionRecRule.getProgramId(), versionRecRule.getChanId(), ( null != channel ? channel.getCallSign() : "" ), 
				versionRecRule.getFindDay(), versionRecRule.getFindTime(), versionRecRule.isInactive(), 
				versionRecRule.getSeason(), versionRecRule.getEpisode(), versionRecRule.getInetref(), 
				versionRecRule.getType(), versionRecRule.getSearchType(), versionRecRule.getRecPriority(), 
				versionRecRule.getPreferredInput(), versionRecRule.getStartOffset(), versionRecRule.getEndOffset(), 
				versionRecRule.getDupMethod(), versionRecRule.getDupIn(), versionRecRule.getFilter(), versionRecRule.getRecProfile(), 
				versionRecRule.getRecGroup(), versionRecRule.getStorageGroup(), versionRecRule.getPlayGroup(), 
				versionRecRule.isAutoExpire(), versionRecRule.getMaxEpisodes(), versionRecRule.isMaxNewest(), 
				versionRecRule.isAutoCommflag(), versionRecRule.isAutoTranscode(), versionRecRule.isAutoMetaLookup(), 
				versionRecRule.isAutoUserJob1(), versionRecRule.isAutoUserJob2(), versionRecRule.isAutoUserJob3(), 
				versionRecRule.isAutoUserJob4(), versionRecRule.getTranscoder() 
			);
			if( add.getStatusCode().equals( HttpStatus.OK ) ) {

				ret = add.getBody().getValue();
				
				if( ret ) {
					downloadRecordinRules( context, locationProfile );
				}
				
			}
					
		}
		
		Log.d( TAG, "updateRecordingRule : exit" );
		return ret;
	}

	private boolean removeRecordingRule( final Context context, final LocationProfile locationProfile, final RecRule recordingRule ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "removeRecordingRule : enter" );

		boolean ret = false;
		
		org.mythtv.services.api.v027.beans.RecRule versionRecRule = convertRecRuleToRecRuleV27( recordingRule );
		if( null != versionRecRule ) {
		
			// update existing rule
			ResponseEntity<org.mythtv.services.api.Bool> remove = mMythServicesTemplate.dvrOperations().removeRecordSchedule( versionRecRule.getId() );
			if( remove.getStatusCode().equals( HttpStatus.OK ) ) {
				ret = remove.getBody().getValue();
				
				if( ret ) {
					downloadRecordinRules( context, locationProfile );
				}
				
			}
					
		}
		
		Log.d( TAG, "removeRecordingRule : exit" );
		return ret;
	}

	private ContentValues convertRecRuleToContentValues( final LocationProfile locationProfile, final DateTime lastModified, final org.mythtv.services.api.v027.beans.RecRule recRule ) {
//		Log.v( TAG, "convertRecRuleToContentValues : enter" );
		
		DateTime startTimestamp = new DateTime( DateTimeZone.UTC );
		if( null != recRule.getStartTime() ) {
			startTimestamp = recRule.getStartTime();
		}

		DateTime endTimestamp = new DateTime( DateTimeZone.UTC );
		if( null != recRule.getEndTime() ) {
			endTimestamp = recRule.getEndTime();
		}

		LocalTime findTime = new LocalTime( DateTimeZone.UTC );
		if( null != recRule.getFindTime() ) {
			findTime = recRule.getFindTime();
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
		values.put( RecordingRuleConstants.FIELD_FIND_DAY, recRule.getFindDay() );
		values.put( RecordingRuleConstants.FIELD_FIND_TIME, findTime.getMillisOfDay() );
		values.put( RecordingRuleConstants.FIELD_FIND_ID, -1 );
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

	private org.mythtv.services.api.v027.beans.RecRule convertRecRuleToRecRuleV27( final RecRule recordingRule ) {
		
		org.mythtv.services.api.v027.beans.RecRule versionRecRule = new org.mythtv.services.api.v027.beans.RecRule();
		versionRecRule.setId( recordingRule.getId() );
		versionRecRule.setParentId( recordingRule.getParentId() );
		versionRecRule.setInactive( recordingRule.isInactive() );
		versionRecRule.setTitle( recordingRule.getTitle() );
		versionRecRule.setSubTitle( recordingRule.getSubTitle() );
		versionRecRule.setDescription( recordingRule.getDescription() );
		versionRecRule.setSeason( recordingRule.getSeason() );
		versionRecRule.setEpisode( recordingRule.getEpisode() );
		versionRecRule.setCategory( recordingRule.getCategory() );
		versionRecRule.setStartTime( recordingRule.getStartTime() );
		versionRecRule.setEndTime( recordingRule.getEndTime() );
		versionRecRule.setSeriesId( recordingRule.getSeriesId() );
		versionRecRule.setProgramId( recordingRule.getProgramId() );
		versionRecRule.setInetref( recordingRule.getInetref() );
		versionRecRule.setChanId( recordingRule.getChanId() );
		versionRecRule.setCallSign( recordingRule.getCallSign() );
		versionRecRule.setFindDay( recordingRule.getFindDay() );
		versionRecRule.setFindTime( recordingRule.getFindTime().toLocalTime() );
		versionRecRule.setType( recordingRule.getType() );
		versionRecRule.setSearchType( recordingRule.getSearchType() );
		versionRecRule.setRecPriority( recordingRule.getRecPriority() );
		versionRecRule.setPreferredInput( recordingRule.getPreferredInput() );
		versionRecRule.setStartOffset( recordingRule.getStartOffset() );
		versionRecRule.setEndOffset( recordingRule.getEndOffset() );
		versionRecRule.setDupMethod( recordingRule.getDupMethod() );
		versionRecRule.setDupIn( recordingRule.getDupIn() );
		versionRecRule.setFilter( recordingRule.getFilter() );
		versionRecRule.setRecProfile( recordingRule.getRecProfile() );
		versionRecRule.setRecGroup( recordingRule.getRecGroup() );
		versionRecRule.setStorageGroup( recordingRule.getStorageGroup() );
		versionRecRule.setPlayGroup( recordingRule.getPlayGroup() );
		versionRecRule.setAutoExpire( recordingRule.isAutoExpire() );
		versionRecRule.setMaxEpisodes( recordingRule.getMaxEpisodes() );
		versionRecRule.setMaxNewest( recordingRule.isMaxNewest() );
		versionRecRule.setAutoCommflag( recordingRule.isAutoCommflag() );
		versionRecRule.setAutoTranscode( recordingRule.isAutoTranscode() );
		versionRecRule.setAutoMetaLookup( recordingRule.isAutoMetaLookup() );
		versionRecRule.setAutoUserJob1( recordingRule.isAutoUserJob1() );
		versionRecRule.setAutoUserJob2( recordingRule.isAutoUserJob2() );
		versionRecRule.setAutoUserJob3( recordingRule.isAutoUserJob3() );
		versionRecRule.setAutoUserJob4( recordingRule.isAutoUserJob4() );
		versionRecRule.setTranscoder( recordingRule.getTranscoder() );
		versionRecRule.setNextRecording( recordingRule.getNextRecording() );
		versionRecRule.setLastRecorded( recordingRule.getLastRecorded() );
		versionRecRule.setLastDeleted( recordingRule.getLastDeleted() );
		versionRecRule.setAverageDelay( recordingRule.getAverageDelay() );
		
		
		return versionRecRule;
	}
	
}
