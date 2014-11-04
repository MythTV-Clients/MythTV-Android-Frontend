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
package org.mythtv.db.status.model;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.mythtv.db.dvr.model.Program;

import android.util.SparseArray;

/**
 * @author Daniel Frey
 *
 */
public class Job implements Serializable {

	private static final long serialVersionUID = -2608035524304413292L;

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
	
		private static SparseArray<Flag> valueMap;
		
		public static Flag getValue( int code ) {
			
			if( null == valueMap ) {
				
				valueMap = new SparseArray<Flag>();
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
	
		private static SparseArray<Type> valueMap;
		
		public static Type getValue( int code ) {
			
			if( null == valueMap ) {
				
				valueMap = new SparseArray<Type>();
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
	
		private static SparseArray<Command> valueMap;
		
		public static Command getValue( int code ) {
			
			if( null == valueMap ) {
				
				valueMap = new SparseArray<Command>();
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
	
		private static SparseArray<Status> valueMap;
		
		public static Status getValue( int code ) {
			
			if( null == valueMap ) {
				
				valueMap = new SparseArray<Status>();
				for( Status status : values() ) {
					valueMap.put( status.getCode(), status );
				}
			}
			
			return valueMap.get( code );
		}
		
	};
	
	private int id;
	private int channelId;
	private DateTime startTs;
	private DateTime startTime;
	private DateTime insertTime;
	private Type type;
	private Command command;
	private Flag flag;
	private Status status;
	private DateTime statusTime;
	private DateTime scheduledTime;
	private String hostname;
	private String args;
	private String comment;
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
