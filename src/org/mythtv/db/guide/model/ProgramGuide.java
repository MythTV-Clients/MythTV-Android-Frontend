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
package org.mythtv.db.guide.model;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.db.channel.model.ChannelInfo;
import org.mythtv.services.api.DateTimeSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGuide implements Serializable {

	private static final long serialVersionUID = 772513807010585699L;

	@JsonProperty( "StartTime" )
	@JsonSerialize( using = DateTimeSerializer.class )
	private DateTime startTime;
	
	@JsonProperty( "EndTime" )
	@JsonSerialize( using = DateTimeSerializer.class )
	private DateTime endTime;
	
	@JsonProperty( "StartChanId" )
	private int startChannelId;
	
	@JsonProperty( "EndChanId" )
	private int endChannelId;
	
	@JsonProperty( "NumOfChannels" )
	private int numberOfChannels;
	
	@JsonProperty( "Details" )
	private boolean details;
	
	@JsonProperty( "Count" )
	private int count;
	
	@JsonProperty( "AsOf" )
	@JsonSerialize( using = DateTimeSerializer.class )
	private DateTime asOf;
	
	@JsonProperty( "Version" )
	private String version;
	
	@JsonProperty( "ProtoVer" )
	private String protocolVersion;
	
	@JsonProperty( "Channels" )
	private List<ChannelInfo> channels;
	
	public ProgramGuide() { }

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
	 * @return the endTime
	 */
	public DateTime getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime( DateTime endTime ) {
		this.endTime = endTime;
	}

	/**
	 * @return the startChannelId
	 */
	public int getStartChannelId() {
		return startChannelId;
	}

	/**
	 * @param startChannelId the startChannelId to set
	 */
	public void setStartChannelId( int startChannelId ) {
		this.startChannelId = startChannelId;
	}

	/**
	 * @return the endChannelId
	 */
	public int getEndChannelId() {
		return endChannelId;
	}

	/**
	 * @param endChannelId the endChannelId to set
	 */
	public void setEndChannelId( int endChannelId ) {
		this.endChannelId = endChannelId;
	}

	/**
	 * @return the numberOfChannels
	 */
	public int getNumberOfChannels() {
		return numberOfChannels;
	}

	/**
	 * @param numberOfChannels the numberOfChannels to set
	 */
	public void setNumberOfChannels( int numberOfChannels ) {
		this.numberOfChannels = numberOfChannels;
	}

	/**
	 * @return the details
	 */
	public boolean isDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails( boolean details ) {
		this.details = details;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount( int count ) {
		this.count = count;
	}

	/**
	 * @return the asOf
	 */
	public DateTime getAsOf() {
		return asOf;
	}

	/**
	 * @param asOf the asOf to set
	 */
	public void setAsOf( DateTime asOf ) {
		this.asOf = asOf;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion( String version ) {
		this.version = version;
	}

	/**
	 * @return the protocolVersion
	 */
	public String getProtocolVersion() {
		return protocolVersion;
	}

	/**
	 * @param protocolVersion the protocolVersion to set
	 */
	public void setProtocolVersion( String protocolVersion ) {
		this.protocolVersion = protocolVersion;
	}

	/**
	 * @return the channels
	 */
	public List<ChannelInfo> getChannels() {
		return channels;
	}

	/**
	 * @param channels the channels to set
	 */
	public void setChannels( List<ChannelInfo> channels ) {
		this.channels = channels;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "ProgramGuide [" );
		
		if( startTime != null ) {
			builder.append( "startTime=" );
			builder.append( startTime );
			builder.append( ", " );
		}
		
		if( endTime != null ) {
			builder.append( "endTime=" );
			builder.append( endTime );
			builder.append( ", " );
		}
		
		builder.append( "startChannelId=" );
		builder.append( startChannelId );
		builder.append( ", endChannelId=" );
		builder.append( endChannelId );
		builder.append( ", numberOfChannels=" );
		builder.append( numberOfChannels );
		builder.append( ", details=" );
		builder.append( details );
		builder.append( ", count=" );
		builder.append( count );
		builder.append( ", " );
		
		if( asOf != null ) {
			builder.append( "asOf=" );
			builder.append( asOf );
			builder.append( ", " );
		}
		
		if( version != null ) {
			builder.append( "version=" );
			builder.append( version );
			builder.append( ", " );
		}
		
		if( protocolVersion != null ) {
			builder.append( "protocolVersion=" );
			builder.append( protocolVersion );
			builder.append( ", " );
		}
		
		if( channels != null ) {
			builder.append( "channels=" );
			builder.append( channels );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
