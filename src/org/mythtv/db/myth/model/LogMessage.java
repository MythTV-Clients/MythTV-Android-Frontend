/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * This software can be found at <https://github.com/MythTV-Android/MythTV-Service-API/>
 *
 */
package org.mythtv.db.myth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;
import org.mythtv.services.api.DateTimeSerializer;

/**
 * @author Daniel Frey
 *
 */
public class LogMessage {

	@JsonProperty( "HostName" )
	private String hostname;
	
	@JsonProperty( "Application" )
	private String application;
	
	@JsonProperty( "PID" )
	private int pid;
	
	@JsonProperty( "TID" )
	private int tid;
	
	@JsonProperty( "Thread" )
	private String thread;
	
	@JsonProperty( "Filename" )
	private String filename;
	
	@JsonProperty( "Line" )
	private int line;
	
	@JsonProperty( "Function" )
	private String function;
	
	@JsonProperty( "Time" )
	@JsonSerialize( using = DateTimeSerializer.class )
	private DateTime time;
	
	@JsonProperty( "Level" )
	private String level;
	
	@JsonProperty( "Message" )
	private String message;
	
	public LogMessage() { }

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
	 * @return the application
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * @param application the application to set
	 */
	public void setApplication( String application ) {
		this.application = application;
	}

	/**
	 * @return the pid
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * @param pid the pid to set
	 */
	public void setPid( int pid ) {
		this.pid = pid;
	}

	/**
	 * @return the tid
	 */
	public int getTid() {
		return tid;
	}

	/**
	 * @param tid the tid to set
	 */
	public void setTid( int tid ) {
		this.tid = tid;
	}

	/**
	 * @return the thread
	 */
	public String getThread() {
		return thread;
	}

	/**
	 * @param thread the thread to set
	 */
	public void setThread( String thread ) {
		this.thread = thread;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename( String filename ) {
		this.filename = filename;
	}

	/**
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @param line the line to set
	 */
	public void setLine( int line ) {
		this.line = line;
	}

	/**
	 * @return the function
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * @param function the function to set
	 */
	public void setFunction( String function ) {
		this.function = function;
	}

	/**
	 * @return the time
	 */
	public DateTime getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime( DateTime time ) {
		this.time = time;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel( String level ) {
		this.level = level;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage( String message ) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "LogMessage [" );
		
		if( hostname != null ) {
			builder.append( "hostname=" );
			builder.append( hostname );
			builder.append( ", " );
		}
		
		if( application != null ) {
			builder.append( "application=" );
			builder.append( application );
			builder.append( ", " );
		}
		
		builder.append( "pid=" );
		builder.append( pid );
		builder.append( ", tid=" );
		builder.append( tid );
		builder.append( ", " );
		
		if( thread != null ) {
			builder.append( "thread=" );
			builder.append( thread );
			builder.append( ", " );
		}
		
		if( filename != null ) {
			builder.append( "filename=" );
			builder.append( filename );
			builder.append( ", " );
		}
		
		builder.append( "line=" );
		builder.append( line );
		builder.append( ", " );
		
		if( function != null ) {
			builder.append( "function=" );
			builder.append( function );
			builder.append( ", " );
		}
		
		if( time != null ) {
			builder.append( "time=" );
			builder.append( time );
			builder.append( ", " );
		}
		
		if( level != null ) {
			builder.append( "level=" );
			builder.append( level );
			builder.append( ", " );
		}
		
		if( message != null ) {
			builder.append( "message=" );
			builder.append( message );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
