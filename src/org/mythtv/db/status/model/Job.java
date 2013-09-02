/**
 * 
 */
package org.mythtv.db.status.model;

import org.joda.time.DateTime;
import org.mythtv.db.dvr.model.Program;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Frey
 *
 */
@Root( name = "Job" )
public class Job {

	public enum Flag {
		NO_FLAGS( 0 ),
		USE_CUTLIST( 1 ),
		LIVE_RECORDING( 2 ),
		EXTERNAL( 4 );
			
		int code;
			
		Flag( int code ) {
			this.code = code;
		}
			
		int getCode() {
			return code;
		}
	
		private static Map<Integer, Flag> valueMap;
		
		public static Flag getValue( int code ) {
			
			if( null == valueMap ) {
				
				valueMap = new HashMap<Integer, Flag>();
				for( Flag flag : values() ) {
					valueMap.put( flag.getCode(), flag );
				}
			}
			
			return valueMap.get( code );
		}
		
	};
	
	public enum Type {
		SYSTEM_JOB( 0 ),
		TRANSCODE( 1 ),
		COMMERCIAL_FLAGGING( 2 ),
		USER_JOB_1( 256 ),
		USER_JOB_2( 512 ),
		USER_JOB_3( 1024 ),
		USER_JOB_4( 2048 );
			
		int code;
			
		Type( int code ) {
			this.code = code;
		}
			
		int getCode() {
			return code;
		}
	
		private static Map<Integer, Type> valueMap;
		
		public static Type getValue( int code ) {
			
			if( null == valueMap ) {
				
				valueMap = new HashMap<Integer, Type>();
				for( Type type : values() ) {
					valueMap.put( type.getCode(), type );
				}
			}
			
			return valueMap.get( code );
		}
		
	};
	
	public enum Command {
		RUN( 0 ),
		PAUSE( 1 ),
		RESUME( 2 ),
		STOP( 4 ),
		RESTART( 8 );
			
		int code;
			
		Command( int code ) {
			this.code = code;
		}
			
		int getCode() {
			return code;
		}
	
		private static Map<Integer, Command> valueMap;
		
		public static Command getValue( int code ) {
			
			if( null == valueMap ) {
				
				valueMap = new HashMap<Integer, Command>();
				for( Command command : values() ) {
					valueMap.put( command.getCode(), command );
				}
			}
			
			return valueMap.get( code );
		}
		
	};
	
	public enum Status {
		NO_FLAGS( 0 ),
		QUEUED( 1 ),
		PENDING( 2 ),
		STARTING( 3 ),
		RUNNING( 4 ),
		STOPPING( 5 ),
		PAUSED( 6 ),
		RETRY( 7 ),
		ERRORING( 8 ),
		ABORTING( 9 ),
		DONE( 256 ),
		FINISHED( 272 ),
		ABORTED( 288 ),
		ERRORED( 304 ),
		CANCELLED( 320 );
			
		int code;
			
		Status( int code ) {
			this.code = code;
		}
			
		int getCode() {
			return code;
		}
	
		private static Map<Integer, Status> valueMap;
		
		public static Status getValue( int code ) {
			
			if( null == valueMap ) {
				
				valueMap = new HashMap<Integer, Status>();
				for( Status status : values() ) {
					valueMap.put( status.getCode(), status );
				}
			}
			
			return valueMap.get( code );
		}
		
	};
	
	@Attribute
	private int id;
	
	@Attribute( name = "chanId" )
	private int channelId;
	
	@Attribute( name = "startTs" )
	private DateTime startTs;
	
	@Attribute( name = "startTime" )
	private DateTime startTime;
	
	@Attribute
	private DateTime insertTime;
	
	@Attribute( name = "type", required = false )
	private Type type;
	
	@Attribute( name = "cmds", required = false )
	private Command command;
	
	@Attribute( name = "flags", required = false )
	private Flag flag;
	
	@Attribute( name = "status", required = false )
	private Status status;
	
	@Attribute
	private DateTime statusTime;
	
	@Attribute( name = "schedTime" )
	private DateTime scheduledTime;
	
	@Attribute
	private String hostname;
	
	@Attribute
	private String args;
	
	private String comment;
	
	@Element( name = "Program" )
	private Program program;
	
	public Job() { }

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId( int id ) {
		this.id = id;
	}

	/**
	 * @return the channelId
	 */
	public int getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId( int channelId ) {
		this.channelId = channelId;
	}

	/**
	 * @return the startTime
	 */
	public DateTime getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime( DateTime startTime ) {
		this.startTime = startTime;
	}

	/**
	 * @return the insertTime
	 */
	public DateTime getInsertTime() {
		return insertTime;
	}

	/**
	 * @param insertTime the insertTime to set
	 */
	public void setInsertTime( DateTime insertTime ) {
		this.insertTime = insertTime;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType( Type type ) {
		this.type = type;
	}

	/**
	 * @return the command
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand( Command command ) {
		this.command = command;
	}

	/**
	 * @return the flag
	 */
	public Flag getFlag() {
		return flag;
	}

	/**
	 * @param flag the flag to set
	 */
	public void setFlag( Flag flag ) {
		this.flag = flag;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus( Status status ) {
		this.status = status;
	}

	/**
	 * @return the statusTime
	 */
	public DateTime getStatusTime() {
		return statusTime;
	}

	/**
	 * @param statusTime the statusTime to set
	 */
	public void setStatusTime( DateTime statusTime ) {
		this.statusTime = statusTime;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname( String hostname ) {
		this.hostname = hostname;
	}

	/**
	 * @return the args
	 */
	public String getArgs() {
		return args;
	}

	/**
	 * @param args the args to set
	 */
	public void setArgs( String args ) {
		this.args = args;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment( String comment ) {
		this.comment = comment;
	}

	/**
	 * @return the startTs
	 */
	public DateTime getStartTs() {
		return startTs;
	}

	/**
	 * @param startTs the startTs to set
	 */
	public void setStartTs( DateTime startTs ) {
		this.startTs = startTs;
	}

	/**
	 * @return the scheduledTime
	 */
	public DateTime getScheduledTime() {
		return scheduledTime;
	}

	/**
	 * @param scheduledTime the scheduledTime to set
	 */
	public void setScheduledTime( DateTime scheduledTime ) {
		this.scheduledTime = scheduledTime;
	}

	/**
	 * @return the program
	 */
	public Program getProgram() {
		return program;
	}

	/**
	 * @param program the program to set
	 */
	public void setProgram( Program program ) {
		this.program = program;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Job [id=" );
		builder.append( id );
		builder.append( ", channelId=" );
		builder.append( channelId );
		builder.append( ", " );
		if( startTs != null ) {
			builder.append( "startTs=" );
			builder.append( startTs );
			builder.append( ", " );
		}
		if( startTime != null ) {
			builder.append( "startTime=" );
			builder.append( startTime );
			builder.append( ", " );
		}
		if( insertTime != null ) {
			builder.append( "insertTime=" );
			builder.append( insertTime );
			builder.append( ", " );
		}
		if( type != null ) {
			builder.append( "type=" );
			builder.append( type );
			builder.append( ", " );
		}
		if( command != null ) {
			builder.append( "command=" );
			builder.append( command );
			builder.append( ", " );
		}
		if( flag != null ) {
			builder.append( "flag=" );
			builder.append( flag );
			builder.append( ", " );
		}
		if( status != null ) {
			builder.append( "status=" );
			builder.append( status );
			builder.append( ", " );
		}
		if( statusTime != null ) {
			builder.append( "statusTime=" );
			builder.append( statusTime );
			builder.append( ", " );
		}
		if( scheduledTime != null ) {
			builder.append( "scheduledTime=" );
			builder.append( scheduledTime );
			builder.append( ", " );
		}
		if( hostname != null ) {
			builder.append( "hostname=" );
			builder.append( hostname );
			builder.append( ", " );
		}
		if( args != null ) {
			builder.append( "args=" );
			builder.append( args );
			builder.append( ", " );
		}
		if( comment != null ) {
			builder.append( "comment=" );
			builder.append( comment );
			builder.append( ", " );
		}
		if( program != null ) {
			builder.append( "program=" );
			builder.append( program );
		}
		builder.append( "]" );
		return builder.toString();
	}
	
}
