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
package org.mythtv.service.status.v26;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
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
import org.mythtv.service.dvr.v26.ProgramHelperV26;
import org.mythtv.service.dvr.v26.RecordingHelperV26;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.MythServicesTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class BackendStatusHelperV26 extends AbstractBaseHelper {

	private static final String TAG = BackendStatusHelperV26.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v026;
	
	private static MythServicesTemplate mMythServicesTemplate;
	private static LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
	private static Context mContext;
	private static LocationProfile mLocationProfile;
	
	private static BackendStatusHelperV26 singleton;
	
	/**
	 * Returns the one and only BackendStatusHelperV26. init() must be called before 
	 * any 
	 * @return
	 */
	public static BackendStatusHelperV26 getInstance() {
		if( null == singleton ) {
			
			synchronized( BackendStatusHelperV26.class ) {

				if( null == singleton ) {
					singleton = new BackendStatusHelperV26();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private BackendStatusHelperV26() { }

	public BackendStatus process( final Context context, final LocationProfile locationProfile ) {
		Log.d( TAG, "process : enter" );

		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}
		
		mContext = context;
		mLocationProfile = locationProfile;
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		if( null == mMythServicesTemplate ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return null;
		}
		
		BackendStatus backendStatus = null;
		try {
			backendStatus = downloadBackendStatus();
		} catch( Exception e ) {
			Log.e( TAG, "process : error", e );
		}
		
		Log.d( TAG, "process : exit" );
		return backendStatus;
	}

	// internal helpers
	
	private BackendStatus downloadBackendStatus() throws RemoteException, OperationApplicationException {
		Log.v( TAG, "downloadBackendStatus : enter" );

		ResponseEntity<org.mythtv.services.api.v026.beans.BackendStatus> status = mMythServicesTemplate.statusOperations().getStatus( ETagInfo.createEmptyETag() );

		if( status.getStatusCode() == HttpStatus.OK ) {

			if( null != status.getBody() ) {
       			
				ApiVersion apiVersion = MythAccessFactory.getMythVersion( mLocationProfile.getUrl() );
				mLocationProfile.setVersion( apiVersion.name() );
				
				mLocationProfile.setConnected( true );
    			mLocationProfile.setProtocolVersion( String.valueOf( status.getBody().getProtocolVersion() ) );
    			if( null != status.getBody().getMachineInfo() ) {
    				if( null != status.getBody().getMachineInfo().getGuide() ) {
    					mLocationProfile.setNextMythFillDatabase( status.getBody().getMachineInfo().getGuide().getNext() );
    				}
    			}
    			mLocationProfileDaoHelper.save( mContext, mLocationProfile );

    			updateProgramGuide( mContext, status.getBody() );
			
				return convertBackendStatus( status.getBody() );
			} else {

				mLocationProfile.setConnected( false );
    			mLocationProfileDaoHelper.save( mContext, mLocationProfile );

			}
			
		}

		Log.v( TAG, "downloadBackendStatus : exit" );
		return null;
	}

	private void updateProgramGuide( final Context mContext, org.mythtv.services.api.v026.beans.BackendStatus status ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "updateProgramGuide : enter" );
		
		if( null != status.getScheduled() ) {
		
			if( null != status.getScheduled().getPrograms() && !status.getScheduled().getPrograms().isEmpty() ) {
			
				String tag = UUID.randomUUID().toString();
				int processed = -1;
				int count = 0;
		
				ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

				for( org.mythtv.services.api.v026.beans.Program versionProgram : status.getScheduled().getPrograms() ) {

					boolean inError = false;
					
					if( null == versionProgram.getStartTime() || null == versionProgram.getEndTime() ) {
						inError = true;
					} else {
						inError = false;
					}

					// load upcoming program
					ProgramHelperV26.getInstance().processProgram( mContext, mLocationProfile, ProgramConstants.CONTENT_URI_UPCOMING, ProgramConstants.TABLE_NAME_UPCOMING, ops, versionProgram, tag );
					count++;

					// update program guide
					ProgramHelperV26.getInstance().processProgram( mContext, mLocationProfile, ProgramConstants.CONTENT_URI_GUIDE, ProgramConstants.TABLE_NAME_GUIDE, ops, versionProgram, tag );
					count++;

					if( !inError && null != versionProgram.getRecording() ) {
						
						if( versionProgram.getRecording().getRecordId() > 0 ) {
						
							// load upcoming recording
							RecordingHelperV26.getInstance().processRecording( mContext, mLocationProfile, ops, RecordingConstants.ContentDetails.UPCOMING, versionProgram, tag );
							count++;

							// update program guide recording
							RecordingHelperV26.getInstance().processRecording( mContext, mLocationProfile, ops, RecordingConstants.ContentDetails.GUIDE, versionProgram, tag );
							count++;

						}
						
					}

					if( count > BATCH_COUNT_LIMIT ) {
//						Log.i( TAG, "load : applying batch for '" + count + "' transactions, processing programs" );
						
						processBatch( mContext, ops, processed, count );

						count = 0;
					}
					
				}

			}
			
		}
		
		Log.v( TAG, "updateProgramGuide : exit" );
	}
	
	private BackendStatus convertBackendStatus( org.mythtv.services.api.v026.beans.BackendStatus status ) {
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
				
				for( org.mythtv.services.api.v026.beans.Encoder versionEncoder : status.getEncoders().getEncoders() ) {
					Encoder encoder = new Encoder();
					encoder.setId( versionEncoder.getId() );
					encoder.setConnected( versionEncoder.isConnected() );
					encoder.setDeviceLabel( versionEncoder.getDeviceLabel() );
					encoder.setHostname( versionEncoder.getHostname() );
					encoder.setLocal( versionEncoder.isLocal() );
					encoder.setLowOnFreeSpace( versionEncoder.isLowOnFreeSpace() );
					encoder.setSleepStatus( versionEncoder.getSleepStatus() );
					encoder.setState( versionEncoder.getState() );
					
					if( null != versionEncoder.getRecording() ) {
						encoder.setRecording( convertProgram( versionEncoder.getRecording() ) );
					}
										
					encoderList.add( encoder );
				}
				
				encoders.setEncoders( encoderList );
			}
			
			bs.setEncoders( encoders );
		}
		
		// convert scheduled
		if( null != status.getScheduled() ) {
			
			Scheduled scheduled = new Scheduled();
			scheduled.setCount( status.getScheduled().getCount() );
			
			if( null != status.getScheduled().getPrograms() && !status.getScheduled().getPrograms().isEmpty() ) {
				
				List<Program> programs = new ArrayList<Program>();
				
				for( org.mythtv.services.api.v026.beans.Program versionProgram : status.getScheduled().getPrograms() ) {
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
				
				for( org.mythtv.services.api.v026.beans.Job versionJob : status.getJobQueue().getJobs() ) {
					Job job = new Job();
					job.setArgs( versionJob.getArgs() );
					job.setChannelId( versionJob.getChannelId() );
					
					if( null != versionJob.getCommand() ) {
						org.mythtv.services.api.v026.beans.Job.Command versionCommand = versionJob.getCommand();
						job.setCommand( Job.Command.valueOf( versionCommand.name() ) );
					}
				
					job.setComment( versionJob.getComment() );
					
					if( null != versionJob.getArgs() ) {
						org.mythtv.services.api.v026.beans.Job.Flag versionFlag = versionJob.getFlag();
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
						org.mythtv.services.api.v026.beans.Job.Status versionStatus = versionJob.getStatus();
						job.setStatus( Job.Status.valueOf( versionStatus.name() ) );
					}
					
					if( null != versionJob.getType() ) {
						org.mythtv.services.api.v026.beans.Job.Type versionType = versionJob.getType();
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
					
					for( org.mythtv.services.api.v026.beans.Group versionGroup : status.getMachineInfo().getStorage().getGroups() ) {
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
				
				for( org.mythtv.services.api.v026.beans.Information versionInfo : status.getMiscellaneous().getInformations() ) {
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
	
	private Program convertProgram( org.mythtv.services.api.v026.beans.Program versionProgram ) {
		
		Program program = new Program();
		
		try {
			program.setAirDate( null != versionProgram.getAirDate() ? DateUtils.dateFormatter.print( versionProgram.getAirDate() ) : "" );
		} catch( Exception e ) {
			program.setAirDate( "" );
		}
		
		program.setAudioProps( versionProgram.getAudioProps() );
		program.setCategory( versionProgram.getCategory() );
		program.setDescription( versionProgram.getDescription() );
		program.setEndTime( versionProgram.getEndTime() );
		
		try {
			program.setEpisode( Integer.parseInt( versionProgram.getEpisode() ) );
		} catch( NumberFormatException e ) {
			program.setEpisode( -1 );
		}
		
		program.setFilename( versionProgram.getFilename() );
		
		try {
			program.setFileSize( Integer.parseInt( versionProgram.getFileSize() ) );
		} catch( NumberFormatException e ) {
			program.setFileSize( -1 );
		}
		
		program.setHostname( versionProgram.getHostname() );
		program.setInetref( versionProgram.getInetref() );
		program.setLastModified( versionProgram.getLastModified() );
		program.setProgramFlags( versionProgram.getProgramFlags() );
		program.setProgramId( versionProgram.getProgramId() );
		program.setRepeat( versionProgram.isRepeat() );
		
		try {
			program.setSeason( Integer.parseInt( versionProgram.getSeason() ) );
		} catch( NumberFormatException e ) {
			program.setSeason( -1 );
		}

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
		
		if( null != versionProgram.getChannelInfo() ) {
			program.setChannelInfo( convertChannel( versionProgram.getChannelInfo() ) );
		}
		
		if( null != versionProgram.getArtwork() ) {
			
			ArtworkInfos artworkInfos = new ArtworkInfos();
			
			if( null != versionProgram.getArtwork().getArtworkInfos() && !versionProgram.getArtwork().getArtworkInfos().isEmpty() ) {
			
				List<ArtworkInfo> artworkInfoList = new ArrayList<ArtworkInfo>();
				
				for( org.mythtv.services.api.v026.beans.ArtworkInfo versionArtwork : versionProgram.getArtwork().getArtworkInfos() ) {
					artworkInfoList.add( convertArtwork( versionArtwork ) );
				}
				
				artworkInfos.setArtworkInfos( artworkInfoList );
			}
			
			program.setArtwork( artworkInfos );
		}
		
		return program;
	}

	private Recording convertRecording( org.mythtv.services.api.v026.beans.Recording versionRecording ) {
		
		Recording recording = new Recording();
		recording.setDuplicateInType( versionRecording.getDuplicateInType() );
		recording.setDuplicateMethod( versionRecording.getDuplicateMethod() );
		recording.setEncoderId( versionRecording.getEncoderId() );
		recording.setEndTimestamp( versionRecording.getEndTimestamp() );
		recording.setPlayGroup( versionRecording.getPlayGroup() );
		recording.setPriority( versionRecording.getPriority() );
		recording.setProfile( versionRecording.getProfile() );
		recording.setRecordId( versionRecording.getRecordId() );
		recording.setRecordingGroup( versionRecording.getRecordingGroup() );
		recording.setRecordingType( versionRecording.getRecordingType() );
		recording.setStartTimestamp( versionRecording.getStartTimestamp() );
		recording.setStatus( versionRecording.getStatus() );
		recording.setStorageGroup( versionRecording.getStorageGroup() );
		
		return recording;
	}

	private ChannelInfo convertChannel( org.mythtv.services.api.v026.beans.ChannelInfo versionChannel ) {
		
		ChannelInfo channel = new ChannelInfo();
		channel.setAtscMajorChannel( versionChannel.getAtscMajorChannel() );
		channel.setAtscMinorChannel( versionChannel.getAtscMinorChannel() );
		channel.setCallSign( versionChannel.getCallSign() );
		channel.setChannelFilters( versionChannel.getChannelFilters() );
		channel.setChannelId( versionChannel.getChannelId() );
		channel.setChannelName( versionChannel.getChannelName() );
		channel.setChannelNumber( versionChannel.getChannelNumber() );
		channel.setCommercialFree( versionChannel.getCommercialFree() );
		channel.setDefaultAuth( versionChannel.getDefaultAuth() );
		channel.setFineTune( versionChannel.getFineTune() );
		channel.setFormat( versionChannel.getFormat() );
		channel.setFrequenceTable( versionChannel.getFrequenceTable() );
		channel.setFrequency( versionChannel.getFrequency() );
		channel.setFrequencyId( versionChannel.getFrequencyId() );
		channel.setIconUrl( versionChannel.getIconUrl() );
		channel.setInputId( versionChannel.getInputId() );
		channel.setModulation( versionChannel.getModulation() );
		channel.setMultiplexId( versionChannel.getMultiplexId() );
		channel.setNetworkId( versionChannel.getNetworkId() );
		channel.setServiceId( versionChannel.getServiceId() );
		channel.setSiStandard( versionChannel.getSiStandard() );
		channel.setSourceId( versionChannel.getSourceId() );
		channel.setTransportId( versionChannel.getTransportId() );
		channel.setUseEit( versionChannel.isUseEit() );
		channel.setVisible( versionChannel.isVisable() );
		channel.setXmltvId( versionChannel.getXmltvId() );
		
		return channel;
	}

	private ArtworkInfo convertArtwork( org.mythtv.services.api.v026.beans.ArtworkInfo versionArtwork ) {
		
		ArtworkInfo artworkInfo = new ArtworkInfo();
		artworkInfo.setFilename( versionArtwork.getFilename() );
		artworkInfo.setStorageGroup( versionArtwork.getStorageGroup() );
		artworkInfo.setType( versionArtwork.getType() );
		artworkInfo.setUrl( artworkInfo.getUrl() );
		
		return artworkInfo;
	}

}
