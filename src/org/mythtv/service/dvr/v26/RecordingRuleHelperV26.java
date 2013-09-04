/**
 * 
 */
package org.mythtv.service.dvr.v26;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.dvr.DvrEndpoint;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.RecordingRuleConstants;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.MythServicesTemplate;
import org.mythtv.services.api.v026.beans.RecRule;
import org.mythtv.services.api.v026.beans.RecRuleList;
import org.mythtv.services.api.v026.impl.DvrTemplate;
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
public class RecordingRuleHelperV26 extends AbstractBaseHelper {

	private static final String TAG = RecordingRuleHelperV26.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v026;
	
	private static final String[] recRuleProjection = new String[] { RecordingRuleConstants._ID };
	
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

			downloadRecordinRules( context, locationProfile );
			
		} catch( Exception e ) {
			Log.e( TAG, "process : error", e );
		
			passed = false;
		}

		Log.v( TAG, "process : exit" );
		return passed;
	}

	// internal helpers
	
	private static void downloadRecordinRules( final Context context, final LocationProfile locationProfile ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "downloadRecordinRules : enter" );
	
		EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( context, locationProfile, DvrTemplate.Endpoint.GET_RECORD_SCHEDULE_LIST.name(), "" );
		
		ResponseEntity<RecRuleList> responseEntity = mMythServicesTemplate.dvrOperations().getRecordScheduleList( -1, -1, etag );

		DateTime date = new DateTime( DateTimeZone.UTC );
		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "downloadRecordinRules : " + DvrEndpoint.GET_RECORD_SCHEDULE_LIST.getEndpoint() + " returned 200 OK" );
			RecRuleList recRuleList = responseEntity.getBody();

			if( null != recRuleList.getRecRules() ) {

				load( context, locationProfile, recRuleList.getRecRules().getRecRules() );	

				if( null != etag.getValue() ) {
					Log.i( TAG, "downloadRecordinRules : saving etag: " + etag.getValue() );
					
					etag.setEndpoint( DvrEndpoint.GET_RECORD_SCHEDULE_LIST.name() );
					etag.setDate( date );
					etag.setMasterHostname( locationProfile.getHostname() );
					etag.setLastModified( date );
					mEtagDaoHelper.save( context, locationProfile, etag );
				}

			}

		}

		if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
			Log.i( TAG, "downloadRecordinRules : " + DvrEndpoint.GET_RECORD_SCHEDULE_LIST.getEndpoint() + " returned 304 Not Modified" );

			if( null != etag.getValue() ) {
				Log.i( TAG, "downloadRecordinRules : saving etag: " + etag.getValue() );

				etag.setLastModified( date );
				mEtagDaoHelper.save( context, locationProfile, etag );
			}

		}

		Log.v( TAG, "downloadRecordinRules : exit" );
	}

	private static int load( final Context context, final LocationProfile locationProfile, final List<RecRule> recordingRules ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "load : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordingRuleHelperV27 is not initialized" );
		
		DateTime lastModified = new DateTime( DateTimeZone.UTC );
		
		int processed = -1;
		int count = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		for( RecRule recordingRule : recordingRules ) {

			processRecordingRule( context, locationProfile, ops, recordingRule, lastModified, count );

			if( count > BATCH_COUNT_LIMIT ) {
//				Log.i( TAG, "load : applying batch for '" + count + "' transactions, processing programs" );
				
				processBatch( context, ops, processed, count );

			}
			
		}

		processBatch( context, ops, processed, count );

		deleteRecordingRules( context, locationProfile, ops, lastModified );
		
		processBatch( context, ops, processed, count );

//		Log.v( TAG, "load : exit" );
		return processed;
	}

	private static void processRecordingRule( final Context context, final LocationProfile locationProfile, ArrayList<ContentProviderOperation> ops, RecRule recRule, DateTime lastModified, int count ) {
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
	
	private static void deleteRecordingRules( final Context context, final LocationProfile locationProfile, ArrayList<ContentProviderOperation> ops, DateTime lastModified ) {
		Log.d( TAG, "deleteRecordingRules : enter" );

//		Log.v( TAG, "load : remove deleted recordings" );
		String deletedSelection = RecordingRuleConstants.TABLE_NAME + "." + RecordingRuleConstants.FIELD_LAST_MODIFIED_DATE + " < ?";
		String[] deletedSelectionArgs = new String[] { String.valueOf( lastModified.getMillis() ) };
			
		deletedSelection = appendLocationHostname( context, locationProfile, deletedSelection, ProgramConstants.TABLE_NAME_RECORDED );
			
//		Log.v( TAG, "load : deleting recRules" );
		ops.add(  
			ContentProviderOperation.newDelete( RecordingRuleConstants.CONTENT_URI )
				.withSelection( deletedSelection, deletedSelectionArgs )
				.build()
		);
		
		Log.d( TAG, "deleteRecordingRules : exit" );
	}
	
	private static ContentValues convertRecRuleToContentValues( final LocationProfile locationProfile, final DateTime lastModified, final RecRule recRule ) {
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
