package org.mythtv.service.guide;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.http.EtagConstants;
import org.mythtv.provider.MythtvProvider;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.guide.ProgramGuide;
import org.mythtv.services.api.guide.ProgramGuideWrapper;
import org.mythtv.services.api.guide.impl.GuideTemplate.Endpoint;
import org.mythtv.services.utils.ArticleCleaner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
public class ProgramGuideDownloadServiceNew extends MythtvService {

	private static final String TAG = ProgramGuideDownloadServiceNew.class.getSimpleName();
	private static final DecimalFormat formatter = new DecimalFormat( "###" );
	
	public static final Integer MAX_DAYS = 2; //14;

    public static final String ACTION_DOWNLOAD = "org.mythtv.background.programGuideDownloadNew.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.programGuideDownloadNew.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.programGuideDownloadNew.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATE = "PROGRESS_DATE";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_DOWNLOADED = "COMPLETE_DOWNLOADED";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";

	private NotificationManager mNotificationManager;
	private Notification mNotification = null;
	private PendingIntent mContentIntent = null;
	private int notificationId = 1001;

	public ProgramGuideDownloadServiceNew() {
		super( "ProgamGuideDownloadServiceNew" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.MythtvService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.v( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );

		mNotificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
		
		if( !isBackendConnected() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.TRUE );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
			return;
		}

		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
			
			sendNotification();
			
			boolean newDataDownloaded = false;
			
			DateTime start = new DateTime();
			start = start.withTime( 0, 0, 0, 001 );

			try {
				for( int i = 1; i <= MAX_DAYS; i++ ) {

					ProgramGuide programGuide = download( start );
					if( null != programGuide ) {

						newDataDownloaded = process( programGuide );

					}

					start = start.plusDays( 1 );

					double percentage = ( (float) i / (float) MAX_DAYS ) * 100;
					progressUpdate( percentage );

				}
			} catch( RemoteException e ) {
				Log.e( TAG, "onHandleIntent : RemoteException", e );
			} catch( OperationApplicationException e ) {
				Log.e( TAG, "onHandleIntent : OperationApplicationException", e );
			} finally {
			
				completed();

				Intent completeIntent = new Intent( ACTION_COMPLETE );
				completeIntent.putExtra( EXTRA_COMPLETE, "Program Guide Download Service Finished" );
				completeIntent.putExtra( EXTRA_COMPLETE_DOWNLOADED, newDataDownloaded );
				sendBroadcast( completeIntent );
			
			}
			
		}
		
		Log.v( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private ProgramGuide download( DateTime start ) {
		Log.v( TAG, "download : enter" );
		
		DateTime end = new DateTime( start );
		end = end.withTime( 23, 59, 59, 999 );
		Log.i( TAG, "download : starting download for " + DateUtils.dateTimeFormatter.print( start ) + ", end time=" + DateUtils.dateTimeFormatter.print( end ) );

		String endpoint = Endpoint.GET_PROGRAM_GUIDE.name() + "_" + DateUtils.dateFormatter.print( start );
		
		Long id = null;
		ETagInfo etag = ETagInfo.createEmptyETag();
		Cursor etagCursor = getContentResolver().query( Uri.withAppendedPath( EtagConstants.CONTENT_URI, "endpoint" ), null, EtagConstants.FIELD_ENDPOINT + " = ?" ,new String[] { endpoint }, null );
		if( etagCursor.moveToFirst() ) {
			id = etagCursor.getLong( etagCursor.getColumnIndexOrThrow( EtagConstants._ID ) );
			String value = etagCursor.getString( etagCursor.getColumnIndexOrThrow( EtagConstants.FIELD_VALUE ) );
			
			etag.setETag( value );
			Log.v( TAG, "download : etag=" + etag.getETag() );
		}
		etagCursor.close();

		ResponseEntity<ProgramGuideWrapper> responseEntity = mMainApplication.getMythServicesApi().guideOperations().getProgramGuide( start, end, 1, -1, false, etag );

		try {

			if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
				ProgramGuideWrapper programGuide = responseEntity.getBody();
				
				if( null != etag.getETag() ) {
					ContentValues values = new ContentValues();
					values.put( EtagConstants.FIELD_ENDPOINT, endpoint );
					values.put( EtagConstants.FIELD_VALUE, etag.getETag() );
					values.put( EtagConstants.FIELD_DATE, ( new DateTime() ).getMillis() );

					if( null == id ) {
						Log.v( TAG, "download : adding new etag" );

						getContentResolver().insert( EtagConstants.CONTENT_URI, values );
					} else {
						Log.v( TAG, "download : updating existing etag" );

						getContentResolver().update( ContentUris.withAppendedId( EtagConstants.CONTENT_URI, id ), values, null, null );
					}
				}

				Log.v( TAG, "download : exit" );
				return programGuide.getProgramGuide();
			}
			
			if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
				
				if( null != etag.getETag() ) {
					ContentValues values = new ContentValues();
					values.put( EtagConstants.FIELD_ENDPOINT, endpoint );
					values.put( EtagConstants.FIELD_VALUE, etag.getETag() );
					values.put( EtagConstants.FIELD_DATE, ( new DateTime() ).getMillis() );

					Log.v( TAG, "download : updating existing etag" );

					getContentResolver().update( ContentUris.withAppendedId( EtagConstants.CONTENT_URI, id ), values, null, null );
				}

			}
			
		} catch( Exception e ) {
			Log.e( TAG, "download : error downloading program guide" );
		}
			
		Log.v( TAG, "download : exit" );
		return null;
	}

	private boolean process( ProgramGuide programGuide ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "process : enter" );

		List<ContentValues> insertValues = new ArrayList<ContentValues>();
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		String[] channelProjection = new String[] { ChannelConstants._ID };
		String channelSelection = ChannelConstants.FIELD_CALLSIGN + " = ?";
		
		String[] programProjection = new String[] { ProgramConstants._ID };
		String programSelection = ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + ProgramConstants.FIELD_START_TIME + " = ?";

		for( ChannelInfo channel : programGuide.getChannels() ) {

			ContentValues channelValues = convertChannelToContentValues( channel );
			Cursor channelCursor = getContentResolver().query( ChannelConstants.CONTENT_URI, channelProjection, channelSelection, new String[] { channel.getCallSign() }, null );
			if( channelCursor.getCount() == 0 ) {
				ops.add( 
						ContentProviderOperation.newInsert( ChannelConstants.CONTENT_URI )
							.withValues( channelValues )
							.build()
					);
			}
			channelCursor.close();
			
			if( null != channel.getPrograms() && !channel.getPrograms().isEmpty() ) {
				
				for( Program program : channel.getPrograms() ) {
					
					program.setChannelInfo( channel );
					
					ContentValues programValues = convertProgramToContentValues( program );

					Cursor programCursor = getContentResolver().query( ProgramConstants.CONTENT_URI_PROGRAM, programProjection, programSelection, new String[] { channel.getChannelId(), DateUtils.dateTimeFormatter.print( program.getStartTime() ) }, null );
					if( programCursor.moveToFirst() ) {
						Long id = programCursor.getLong( programCursor.getColumnIndexOrThrow( ProgramConstants._ID ) );
						ops.add( 
								ContentProviderOperation.newUpdate( ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_PROGRAM, id ) )
									.withValues( programValues )
									.build()
							);
					} else {
						insertValues.add( programValues );
					}
					programCursor.close();
					
				}
				
			}
			
			if( !ops.isEmpty() ) {
				//Log.v( TAG, "process : applying batch '" + channel.getCallSign() + "'" );
				
				getContentResolver().applyBatch( MythtvProvider.AUTHORITY, ops );

				ops = new ArrayList<ContentProviderOperation>();
			}
			
			if( !insertValues.isEmpty() ) {
				//Log.v( TAG, "process : inserting programs" );

				getContentResolver().bulkInsert( ProgramConstants.CONTENT_URI_PROGRAM, insertValues.toArray( new ContentValues[ insertValues.size() ] ) );
				
				insertValues = new ArrayList<ContentValues>();
			}
			
		}
	
		Log.v( TAG, "process : exit" );
		return true;
	}

	private ContentValues convertChannelToContentValues( ChannelInfo channelInfo ) {
		
		ContentValues values = new ContentValues();
		values.put( ChannelConstants.FIELD_CHAN_ID, channelInfo.getChannelId() );
		values.put( ChannelConstants.FIELD_CHAN_NUM, channelInfo.getChannelNumber() );
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

		return values;
	}

	private ContentValues convertProgramToContentValues( final Program program ) {
		
		long durationInMinutes = ( program.getEndTime().getMillis() / 60000 ) - ( program.getStartTime().getMillis() / 60000 );

		// Removing Grammar Articles.  English only at this time, needs internationalization
		String cleanTitle = ArticleCleaner.clean( program.getTitle() );

		DateTime startTime = new DateTime( program.getStartTime().getMillis() );
		DateTime endTime = new DateTime( program.getEndTime().getMillis() );
		
		ContentValues values = new ContentValues();
		values.put( ProgramConstants.FIELD_PROGRAM_GROUP, cleanTitle );
		values.put( ProgramConstants.FIELD_START_TIME, startTime.getMillis() );
		values.put( ProgramConstants.FIELD_END_TIME, endTime.getMillis() );
		values.put( ProgramConstants.FIELD_DURATION, durationInMinutes );
		values.put( ProgramConstants.FIELD_START_DATE, DateUtils.dateFormatter.print( startTime ) );
		values.put( ProgramConstants.FIELD_TIMESLOT_HOUR, startTime.getHourOfDay() );
		values.put( ProgramConstants.FIELD_TIMESLOT_MINUTE, startTime.getMinuteOfHour() );
		values.put( ProgramConstants.FIELD_TITLE, null != program.getTitle() ? program.getTitle() : "" );
		values.put( ProgramConstants.FIELD_SUB_TITLE, null != program.getSubTitle() ? program.getSubTitle() : "" );
		values.put( ProgramConstants.FIELD_CATEGORY, null != program.getCategory() ? program.getCategory() : "" );
		values.put( ProgramConstants.FIELD_CATEGORY_TYPE, null != program.getCategoryType() ? program.getCategoryType() : "" );
		values.put( ProgramConstants.FIELD_REPEAT, program.isRepeat() ? 1 : 0 );
		values.put( ProgramConstants.FIELD_VIDEO_PROPS, program.getVideoProps() );
		values.put( ProgramConstants.FIELD_AUDIO_PROPS, program.getAudioProps() );
		values.put( ProgramConstants.FIELD_SUB_PROPS, program.getSubProps() );
		values.put( ProgramConstants.FIELD_SERIES_ID, null != program.getSeriesId() ? program.getSeriesId() : "" );
		values.put( ProgramConstants.FIELD_PROGRAM_ID, null != program.getProgramId() ? program.getProgramId() : "" );
		values.put( ProgramConstants.FIELD_STARS, program.getStars() );
		values.put( ProgramConstants.FIELD_FILE_SIZE, null != program.getFileSize() ? program.getFileSize() : "" );
		values.put( ProgramConstants.FIELD_LAST_MODIFIED, null != program.getLastModified() ? DateUtils.dateTimeFormatter.print( program.getLastModified() ) : "" );
		values.put( ProgramConstants.FIELD_PROGRAM_FLAGS, null != program.getProgramFlags() ? program.getProgramFlags() : "" );
		values.put( ProgramConstants.FIELD_HOSTNAME, null != program.getHostname() ? program.getHostname() : "" );
		values.put( ProgramConstants.FIELD_FILENAME, null != program.getFilename() ? program.getFilename() : "" );
		values.put( ProgramConstants.FIELD_AIR_DATE, null != program.getAirDate() ? DateUtils.dateTimeFormatter.print( program.getAirDate() ) : "" );
		values.put( ProgramConstants.FIELD_DESCRIPTION, null != program.getDescription() ? program.getDescription() : "" );
		values.put( ProgramConstants.FIELD_INETREF, null != program.getInetref() ? program.getInetref() : "" );
		values.put( ProgramConstants.FIELD_SEASON, null != program.getSeason() ? program.getSeason() : "" );
		values.put( ProgramConstants.FIELD_EPISODE, null != program.getEpisode() ? program.getEpisode() : "" );
		values.put( ProgramConstants.FIELD_CHANNEL_ID, null != program.getChannelInfo() ? program.getChannelInfo().getChannelId() : "" );
		values.put( ProgramConstants.FIELD_CHANNEL_NUMBER, null != program.getChannelInfo() ? program.getChannelInfo().getChannelNumber() : "" );
		values.put( ProgramConstants.FIELD_CHANNEL_CALLSIGN, null != program.getChannelInfo() ? program.getChannelInfo().getCallSign() : "" );
		values.put( ProgramConstants.FIELD_STATUS, null != program.getRecording() ? program.getRecording().getStatus() : -1 );
		values.put( ProgramConstants.FIELD_PRIORITY, null != program.getRecording() ? program.getRecording().getPriority() : -1 );
		values.put( ProgramConstants.FIELD_START_TS, null != program.getRecording() && null != program.getRecording().getStartTimestamp() ? program.getRecording().getStartTimestamp().getMillis() : -1 );
		values.put( ProgramConstants.FIELD_END_TS, null != program.getRecording() && null != program.getRecording().getEndTimestamp() ? program.getRecording().getEndTimestamp().getMillis() : -1 );
		values.put( ProgramConstants.FIELD_RECORD_ID, null != program.getRecording() ? program.getRecording().getRecordid() : -1 );
		values.put( ProgramConstants.FIELD_REC_GROUP, null != program.getRecording() ? program.getRecording().getRecordingGroup() : "" );
		values.put( ProgramConstants.FIELD_PLAY_GROUP, null != program.getRecording() ? program.getRecording().getPlayGroup() : "" );
		values.put( ProgramConstants.FIELD_STORAGE_GROUP, null != program.getRecording() ? program.getRecording().getStorageGroup() : "" );
		values.put( ProgramConstants.FIELD_REC_TYPE, null != program.getRecording() ? program.getRecording().getRecordingType() : -1 );
		values.put( ProgramConstants.FIELD_DUP_IN_TYPE, null != program.getRecording() ? program.getRecording().getDuplicateInType() : -1 );
		values.put( ProgramConstants.FIELD_DUP_METHOD, null != program.getRecording() ? program.getRecording().getDuplicateMethod() : -1 );
		values.put( ProgramConstants.FIELD_ENCODER_ID, null != program.getRecording() ? program.getRecording().getEncoderId() : -1 );
		values.put( ProgramConstants.FIELD_PROFILE, null != program.getRecording() ? program.getRecording().getProfile() : "" );
		return values;
	}

	@SuppressWarnings( "deprecation" )
	private void sendNotification() {

		long when = System.currentTimeMillis();
		
        mNotification = new Notification( android.R.drawable.stat_notify_sync, getResources().getString( R.string.notification_sync_program_guide ), when );

        Intent notificationIntent = new Intent();
        mContentIntent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );

        mNotification.setLatestEventInfo( this, getResources().getString( R.string.app_name ), getResources().getString( R.string.notification_sync_program_guide ), mContentIntent );

        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify( notificationId, mNotification );
	
	}
	
    @SuppressWarnings( "deprecation" )
	private void progressUpdate( double percentageComplete ) {

    	CharSequence contentText = formatter.format( percentageComplete ) + "% complete";

    	mNotification.setLatestEventInfo( this, getResources().getString( R.string.notification_sync_program_guide ), contentText, mContentIntent );
    	mNotificationManager.notify( notificationId, mNotification );
    }

    private void completed()    {

    	if( null != mNotificationManager ) {
    		mNotificationManager.cancel( notificationId );
    	}
    	
    }
	
}
