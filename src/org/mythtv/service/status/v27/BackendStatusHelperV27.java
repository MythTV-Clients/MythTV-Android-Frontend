/**
 * 
 */
package org.mythtv.service.status.v27;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.channel.model.ChannelInfo;
import org.mythtv.db.content.model.ArtworkInfo;
import org.mythtv.db.content.model.ArtworkInfos;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.RecordingConstants;
import org.mythtv.db.dvr.model.Program;
import org.mythtv.db.dvr.model.Recording;
import org.mythtv.db.frontends.model.Frontends;
import org.mythtv.db.myth.model.Group;
import org.mythtv.db.myth.model.Storage;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.db.status.model.BackendStatus;
import org.mythtv.db.status.model.Backends;
import org.mythtv.db.status.model.Encoder;
import org.mythtv.db.status.model.Encoders;
import org.mythtv.db.status.model.Guide;
import org.mythtv.db.status.model.Information;
import org.mythtv.db.status.model.Job;
import org.mythtv.db.status.model.JobQueue;
import org.mythtv.db.status.model.Load;
import org.mythtv.db.status.model.MachineInfo;
import org.mythtv.db.status.model.Miscellaneous;
import org.mythtv.db.status.model.Scheduled;
import org.mythtv.service.dvr.v27.ProgramHelperV27;
import org.mythtv.service.dvr.v27.RecordingHelperV27;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v027.MythServicesTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class BackendStatusHelperV27 {

	private static final String TAG = BackendStatusHelperV27.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v027;
	
	private static MythServicesTemplate mMythServicesTemplate;
	private static LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
	private static Context mContext;
	private static LocationProfile mLocationProfile;
	
	public static BackendStatus process( final Context context, final LocationProfile locationProfile ) {
		Log.d( TAG, "process : enter" );

		if( !MythAccessFactory.isServerReachable( locationProfile.getUrl() ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}
		
		mContext = context;
		mLocationProfile = locationProfile;
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );

		BackendStatus backendStatus = downloadBackendStatus();
		
		Log.d( TAG, "process : enter" );
		return backendStatus;
	}

	// internal helpers
	
	private static BackendStatus downloadBackendStatus() {
		Log.v( TAG, "downloadBackendStatus : enter" );

		ResponseEntity<org.mythtv.services.api.v027.status.beans.BackendStatus> status = mMythServicesTemplate.statusOperations().getStatus( ETagInfo.createEmptyETag() );

		if( status.getStatusCode() == HttpStatus.OK ) {
			Log.i( TAG, "BackendStatusTask.doInBackground : exit" );

			if( null != status.getBody() ) {
       			
				mLocationProfile.setConnected( true );
    			mLocationProfile.setVersion( status.getBody().getVersion() );
    			mLocationProfile.setProtocolVersion( String.valueOf( status.getBody().getProtocolVersion() ) );
    			if( null != status.getBody().getMachineInfo() ) {
    				if( null != status.getBody().getMachineInfo().getGuide() ) {
    					mLocationProfile.setNextMythFillDatabase( status.getBody().getMachineInfo().getGuide().getNext() );
    				}
    			}
    			mLocationProfileDaoHelper.save( mContext, mLocationProfile );

    			updateProgramGuide( status.getBody() );
			
				return convertBackendStatus( status.getBody() );
			} else {

				mLocationProfile.setConnected( false );
    			mLocationProfileDaoHelper.save( mContext, mLocationProfile );

			}
			
		}

		Log.v( TAG, "downloadBackendStatus : exit" );
		return null;
	}

	private static void updateProgramGuide( org.mythtv.services.api.v027.status.beans.BackendStatus status ) {
		Log.v( TAG, "updateProgramGuide : enter" );
		
		if( null != status.getScheduled() ) {
		
			if( null != status.getScheduled().getPrograms() && !status.getScheduled().getPrograms().isEmpty() ) {
			
				int count = 0;
		
				ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

				DateTime lastModified = new DateTime( DateTimeZone.UTC );
				
				for( org.mythtv.services.api.v027.beans.Program versionProgram : status.getScheduled().getPrograms() ) {

					boolean inError = false;
					
					if( null == versionProgram.getStartTime() || null == versionProgram.getEndTime() ) {
						inError = true;
					} else {
						inError = false;
					}

					DateTime startTime = versionProgram.getStartTime();

					// load upcoming program
					ProgramHelperV27.processProgram( mContext, mLocationProfile, ProgramConstants.CONTENT_URI_UPCOMING, ProgramConstants.TABLE_NAME_UPCOMING, ops, versionProgram, lastModified, startTime, count );
					// update program guide
					ProgramHelperV27.processProgram( mContext, mLocationProfile, ProgramConstants.CONTENT_URI_GUIDE, ProgramConstants.TABLE_NAME_GUIDE, ops, versionProgram, lastModified, startTime, count );

					if( !inError && null != versionProgram.getRecording() ) {
						
						if( versionProgram.getRecording().getRecordId() > 0 ) {
						
							// load upcoming recording
							RecordingHelperV27.processRecording( mContext, mLocationProfile, ops, RecordingConstants.ContentDetails.UPCOMING, versionProgram, lastModified, startTime, count );
							// update program guide recording
							RecordingHelperV27.processRecording( mContext, mLocationProfile, ops, RecordingConstants.ContentDetails.GUIDE, versionProgram, lastModified, startTime, count );

						}
						
					}

				}

			}
			
		}
		
		Log.v( TAG, "updateProgramGuide : exit" );
	}
	
	private static BackendStatus convertBackendStatus( org.mythtv.services.api.v027.status.beans.BackendStatus status ) {
		Log.v( TAG, "convertBackendStatus : enter" );
		
		BackendStatus bs = new BackendStatus();
		bs.setVersion( status.getVersion() );
		bs.setIsoDate( status.getIsoDate() );
		bs.setProtocolVersion( status.getProtocolVersion() );
		bs.setTime( status.getTime() );
		bs.setDate( status.getDate() );

		// convert encoders
		if( null != status.getEncoders() ) {
			
			Encoders encoders = new Encoders();
			
			if( null != status.getEncoders().getEncoders() && !status.getEncoders().getEncoders().isEmpty() ) {
				
				List<Encoder> encoderList = new ArrayList<Encoder>();
				
				for( org.mythtv.services.api.v027.beans.Encoder versionEncoder : status.getEncoders().getEncoders() ) {
					Encoder encoder = new Encoder();
					encoder.setId( versionEncoder.getId() );
					encoder.setConnected( versionEncoder.isConnected() );
					encoder.setDeviceLabel( "" );
					encoder.setHostname( versionEncoder.getHostName() );
					encoder.setLocal( versionEncoder.isLocal() );
					encoder.setLowOnFreeSpace( versionEncoder.isLowOnFreeSpace() );
					encoder.setSleepStatus( versionEncoder.getSleepStatus() );
					encoder.setState( versionEncoder.getState() );
					
					if( null != versionEncoder.getRecording() ) {
						encoder.setRecording( convertProgram( versionEncoder.getRecording() ) );
					}
										
					encoderList.add( encoder );
				}
			}
			
			bs.setEncoders( encoders );
		}
		
		// convert scheduled
		if( null != status.getScheduled() ) {
			
			Scheduled scheduled = new Scheduled();
			scheduled.setCount( status.getScheduled().getCount() );
			
			if( null != status.getScheduled().getPrograms() && !status.getScheduled().getPrograms().isEmpty() ) {
				
				List<Program> programs = new ArrayList<Program>();
				
				for( org.mythtv.services.api.v027.beans.Program versionProgram : status.getScheduled().getPrograms() ) {
					programs.add( convertProgram( versionProgram ) );
				}
				
				scheduled.setPrograms( programs );
			}
			
			bs.setScheduled( scheduled );
		}
		
		// convert frontends
		if( null != status.getFrontends() ) {
			
			Frontends frontends = new Frontends();
			frontends.setCount( status.getFrontends().getCount() );
			
			bs.setFrontends( frontends );
		}
		
		// convert backends
		if( null != status.getBackends() ) {
			
			Backends backends = new Backends();
			backends.setCount( status.getBackends().getCount() );
			
			bs.setBackends( backends );
		}
		
		// convert job queue
		if( null != status.getJobQueue() ) {
			
			JobQueue jobQueue = new JobQueue();
			jobQueue.setCount( status.getJobQueue().getCount() );
			
			if( null != status.getJobQueue().getJobs() && !status.getJobQueue().getJobs().isEmpty() ) {
				
				List<Job> jobs = new ArrayList<Job>();
				
				for( org.mythtv.services.api.v027.status.beans.Job versionJob : status.getJobQueue().getJobs() ) {
					Job job = new Job();
					job.setArgs( versionJob.getArgs() );
					job.setChannelId( versionJob.getChannelId() );
					
					if( null != versionJob.getCommand() ) {
						org.mythtv.services.api.v027.status.beans.Job.Command versionCommand = versionJob.getCommand();
						job.setCommand( Job.Command.valueOf( versionCommand.name() ) );
					}
				
					job.setComment( versionJob.getComment() );
					
					if( null != versionJob.getArgs() ) {
						org.mythtv.services.api.v027.status.beans.Job.Flag versionFlag = versionJob.getFlag();
						job.setFlag( Job.Flag.valueOf( versionFlag.name() ) );
					}
					
					job.setHostname( versionJob.getHostname() );
					job.setId( versionJob.getId() );
					job.setInsertTime( versionJob.getInsertTime() );
					
					if( null != versionJob.getProgram() ) {
						job.setProgram( convertProgram( versionJob.getProgram() ) );
					}
					
					job.setScheduledTime( versionJob.getScheduledTime() );
					job.setStartTime( versionJob.getStartTime() );
					job.setStartTs( versionJob.getStartTs() );
					
					if( null != versionJob.getStatus() ) {
						org.mythtv.services.api.v027.status.beans.Job.Status versionStatus = versionJob.getStatus();
						job.setStatus( Job.Status.valueOf( versionStatus.name() ) );
					}
					
					if( null != versionJob.getType() ) {
						org.mythtv.services.api.v027.status.beans.Job.Type versionType = versionJob.getType();
						job.setType( Job.Type.valueOf( versionType.name() ) );
					}
				
					jobs.add( job );
				}
				
				jobQueue.setJobs( jobs );
			}
			
			bs.setJobQueue( jobQueue );
		}
		
		// convert machine info
		if( null != status.getMachineInfo() ) {
			
			MachineInfo machineInfo = new MachineInfo();
			
			if( null != status.getMachineInfo().getGuide() ) {
				
				Guide guide = new Guide();
				guide.setComment( status.getMachineInfo().getGuide().getComment() );
				guide.setEnd( status.getMachineInfo().getGuide().getEnd() );
				guide.setGuideDays( status.getMachineInfo().getGuide().getGuideDays() );
				guide.setGuideThru( status.getMachineInfo().getGuide().getGuideThru() );
				guide.setNext( status.getMachineInfo().getGuide().getNext() );
				guide.setStart( status.getMachineInfo().getGuide().getStart() );
				guide.setStatus( status.getMachineInfo().getGuide().getStatus() );

				machineInfo.setGuide( guide );
			}
			
			if( null != status.getMachineInfo().getLoad() ) {
				
				Load load = new Load();
				load.setAverageOne( status.getMachineInfo().getLoad().getAverageOne() );
				load.setAverageTwo( status.getMachineInfo().getLoad().getAverageTwo() );
				load.setAverageThree( status.getMachineInfo().getLoad().getAverageThree() );
				
				machineInfo.setLoad( load );
			}

			if( null != status.getMachineInfo().getStorage() ) {

				Storage storage = new Storage();
				
				if( null != status.getMachineInfo().getStorage().getGroups() && !status.getMachineInfo().getStorage().getGroups().isEmpty() ) {
					List<Group> groups = new ArrayList<Group>();
					
					for( org.mythtv.services.api.v027.status.beans.Group versionGroup : status.getMachineInfo().getStorage().getGroups() ) {
						Group group = new Group();
						group.setDeleted( versionGroup.isDeleted() );
						group.setDirectory( versionGroup.getDirectory() );
						group.setExpirable( versionGroup.getExpirable() );
						group.setFree( versionGroup.getFree() );
						group.setId( versionGroup.getId() );
						group.setLiveTv( versionGroup.isLiveTv() );
						group.setTotal( versionGroup.getTotal() );
						group.setUsed( versionGroup.getUsed() ); 
						
						groups.add( group );
					}

					storage.setGroups( groups );
				}
				
				machineInfo.setStorage( storage );
			}

			bs.setMachineInfo( machineInfo );
		}
		
		// convert miscellaneous
		if( null != status.getMiscellaneous() ) {
			
			Miscellaneous misc = new Miscellaneous();
			
			if( null != status.getMiscellaneous().getInformations() && !status.getMiscellaneous().getInformations().isEmpty() ) {
				
				List<Information> infos = new ArrayList<Information>();
				
				for( org.mythtv.services.api.v027.status.beans.Information versionInfo : status.getMiscellaneous().getInformations() ) {
					Information info = new Information();
					info.setName( versionInfo.getName() );
					info.setValue( versionInfo.getValue() );
					info.setDisplay( versionInfo.getDisplay() );
					
					infos.add( info );
				}
				
				misc.setInformations( infos );
			}
			
			bs.setMiscellaneous( misc );
		}
		
		Log.v( TAG, "convertBackendStatus : exit" );
		return bs;
	}
	
	private static Program convertProgram( org.mythtv.services.api.v027.beans.Program versionProgram ) {
		
		Program program = new Program();
		program.setAirDate( new DateTime( versionProgram.getAirdate() ) );
		program.setAudioProps( versionProgram.getAudioProps() );
		program.setCategory( versionProgram.getCategory() );
		program.setDescription( versionProgram.getDescription() );
		program.setEndTime( versionProgram.getEndTime() );
		program.setEpisode( versionProgram.getEpisode() );
		program.setFilename( versionProgram.getFileName() );
		program.setFileSize( versionProgram.getFileSize() );
		program.setHostname( versionProgram.getHostName() );
		program.setInetref( versionProgram.getInetref() );
		program.setLastModified( versionProgram.getLastModified() );
		program.setProgramFlags( versionProgram.getProgramFlags() );
		program.setProgramId( versionProgram.getProgramId() );
		program.setRepeat( versionProgram.isRepeat() );
		program.setSeason( versionProgram.getSeason() );
		program.setSeriesId( versionProgram.getSeriesId() );
		program.setStars( versionProgram.getStars() );
		program.setStartTime( versionProgram.getStartTime() );
		program.setSubProps( versionProgram.getSubProps() );
		program.setSubTitle( versionProgram.getSubTitle() );
		program.setTitle( versionProgram.getTitle() );
		program.setVideoProps( versionProgram.getVideoProps() );
		
		if( null != versionProgram.getRecording() ) {
			program.setRecording( convertRecording( versionProgram.getRecording() ) );
		}
		
		if( null != versionProgram.getChannel() ) {
			program.setChannelInfo( convertChannel( versionProgram.getChannel() ) );
		}
		
		if( null != versionProgram.getArtwork() ) {
			
			ArtworkInfos artworkInfos = new ArtworkInfos();
			
			if( null != versionProgram.getArtwork().getArtworkInfos() && versionProgram.getArtwork().getArtworkInfos().length > 0 ) {
			
				List<ArtworkInfo> artworkInfoList = new ArrayList<ArtworkInfo>();
				
				for( org.mythtv.services.api.v027.beans.ArtworkInfo versionArtwork : versionProgram.getArtwork().getArtworkInfos() ) {
					artworkInfoList.add( convertArtwork( versionArtwork ) );
				}
				
				artworkInfos.setArtworkInfos( artworkInfoList );
			}
			
			program.setArtwork( artworkInfos );
		}
		
		return program;
	}

	private static Recording convertRecording( org.mythtv.services.api.v027.beans.RecordingInfo versionRecording ) {
		
		Recording recording = new Recording();
		recording.setDuplicateInType( versionRecording.getDupInType() );
		recording.setDuplicateMethod( versionRecording.getDupMethod() );
		recording.setEncoderId( versionRecording.getEncoderId() );
		recording.setEndTimestamp( versionRecording.getEndTs() );
		recording.setPlayGroup( versionRecording.getPlayGroup() );
		recording.setPriority( versionRecording.getPriority() );
		recording.setProfile( versionRecording.getProfile() );
		recording.setRecordId( versionRecording.getRecordId() );
		recording.setRecordingGroup( versionRecording.getRecGroup() );
		recording.setRecordingType( versionRecording.getRecType() );
		recording.setStartTimestamp( versionRecording.getStartTs() );
		recording.setStatus( versionRecording.getStatus() );
		recording.setStorageGroup( versionRecording.getStorageGroup() );
		
		return recording;
	}

	private static ChannelInfo convertChannel( org.mythtv.services.api.v027.beans.ChannelInfo versionChannel ) {
		
		ChannelInfo channel = new ChannelInfo();
		channel.setAtscMajorChannel( versionChannel.getATSCMajorChan() );
		channel.setAtscMinorChannel( versionChannel.getATSCMinorChan() );
		channel.setCallSign( versionChannel.getCallSign() );
		channel.setChannelFilters( versionChannel.getChanFilters() );
		channel.setChannelId( versionChannel.getChanId() );
		channel.setChannelName( versionChannel.getChannelName() );
		channel.setChannelNumber( versionChannel.getChanNum() );
		channel.setCommercialFree( versionChannel.getCommFree() );
		channel.setDefaultAuth( versionChannel.getDefaultAuth() );
		channel.setFineTune( versionChannel.getFineTune() );
		channel.setFormat( versionChannel.getFormat() );
		channel.setFrequenceTable( versionChannel.getFrequencyTable() );
		channel.setFrequency( versionChannel.getFrequency() );
		channel.setFrequencyId( versionChannel.getFrequencyId() );
		channel.setIconUrl( versionChannel.getIconURL() );
		channel.setInputId( versionChannel.getInputId() );
		channel.setModulation( versionChannel.getModulation() );
		channel.setMultiplexId( versionChannel.getMplexId() );
		channel.setNetworkId( versionChannel.getNetworkId() );
		channel.setServiceId( versionChannel.getServiceId() );
		channel.setSiStandard( versionChannel.getSIStandard() );
		channel.setSourceId( versionChannel.getSourceId() );
		channel.setTransportId( versionChannel.getTransportId() );
		channel.setUseEit( versionChannel.isUseEIT() );
		channel.setVisible( versionChannel.isVisible() );
		channel.setXmltvId( versionChannel.getXMLTVID() );
		
		return channel;
	}

	private static ArtworkInfo convertArtwork( org.mythtv.services.api.v027.beans.ArtworkInfo versionArtwork ) {
		
		ArtworkInfo artworkInfo = new ArtworkInfo();
		artworkInfo.setFilename( versionArtwork.getFileName() );
		artworkInfo.setStorageGroup( versionArtwork.getStorageGroup() );
		artworkInfo.setType( versionArtwork.getType() );
		artworkInfo.setUrl( artworkInfo.getUrl() );
		
		return artworkInfo;
	}

}
