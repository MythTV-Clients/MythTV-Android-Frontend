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
package org.mythtv.db.status.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.mythtv.db.dvr.model.Program;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @author Daniel Frey
 *
 */
@Root( name = "Encoder" )
public class Encoder {

	@JsonProperty( "Id" )
	@Attribute
	private int id;
	
	@JsonProperty( "HostName" )
	@Attribute
	private String hostname;
	
	@JsonProperty( "Local" )
	@Attribute
	private boolean local;
	
	@JsonProperty( "Connected" )
	@Attribute
	private boolean connected;
	
	@JsonProperty( "State" )
	@Attribute
	private int state;
	
	@JsonProperty( "SleepStatus" )
	@Attribute( name = "sleepstatus" )
	private int sleepStatus;
	
	@JsonIgnore
	@Attribute( name = "devlabel" )
	private String deviceLabel;
	
	@JsonProperty( "LowOnFreeSpace" )
	@Attribute( required = false )
	private boolean lowOnFreeSpace;
	
	@JsonProperty( "Recording" )
	@Element( name = "Program", required = false )
	private Program recording;
	
	public Encoder() { }

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
	 * @return the local
	 */
	public boolean isLocal() {
		return local;
	}

	/**
	 * @param local the local to set
	 */
	public void setLocal( boolean local ) {
		this.local = local;
	}

	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * @param connected the connected to set
	 */
	public void setConnected( boolean connected ) {
		this.connected = connected;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState( int state ) {
		this.state = state;
	}

	/**
	 * @return the sleepStatus
	 */
	public int getSleepStatus() {
		return sleepStatus;
	}

	/**
	 * @param sleepStatus the sleepStatus to set
	 */
	public void setSleepStatus( int sleepStatus ) {
		this.sleepStatus = sleepStatus;
	}

	/**
	 * @return the deviceLabel
	 */
	public String getDeviceLabel() {
		return deviceLabel;
	}

	/**
	 * @param deviceLabel the deviceLabel to set
	 */
	public void setDeviceLabel( String deviceLabel ) {
		this.deviceLabel = deviceLabel;
	}

	/**
	 * @return the lowOnFreeSpace
	 */
	public boolean isLowOnFreeSpace() {
		return lowOnFreeSpace;
	}

	/**
	 * @param lowOnFreeSpace the lowOnFreeSpace to set
	 */
	public void setLowOnFreeSpace( boolean lowOnFreeSpace ) {
		this.lowOnFreeSpace = lowOnFreeSpace;
	}

	/**
	 * @return the recording
	 */
	public Program getRecording() {
		return recording;
	}

	/**
	 * @param recording the recording to set
	 */
	public void setRecording( Program recording ) {
		this.recording = recording;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Encoder [id=" );
		builder.append( id );
		builder.append( ", " );
		if( hostname != null ) {
			builder.append( "hostname=" );
			builder.append( hostname );
			builder.append( ", " );
		}
		builder.append( "local=" );
		builder.append( local );
		builder.append( ", connected=" );
		builder.append( connected );
		builder.append( ", state=" );
		builder.append( state );
		builder.append( ", sleepStatus=" );
		builder.append( sleepStatus );
		builder.append( ", " );
		if( deviceLabel != null ) {
			builder.append( "deviceLabel=" );
			builder.append( deviceLabel );
			builder.append( ", " );
		}
		builder.append( "lowOnFreeSpace=" );
		builder.append( lowOnFreeSpace );
		builder.append( ", " );
		if( recording != null ) {
			builder.append( "recording=" );
			builder.append( recording );
		}
		builder.append( "]" );
		return builder.toString();
	}
	
}
