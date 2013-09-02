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
package org.mythtv.db.video.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;
import org.mythtv.services.api.DateTimeSerializer;

import java.util.List;

/**
 * @author Daniel Frey
 *
 */
public class VideoLookups {

	@JsonProperty( "Count" )
	private int count;
	
	@JsonProperty( "AsOf" )
	@JsonSerialize( using = DateTimeSerializer.class )
	private DateTime asOf;
	
	@JsonProperty( "Version" )
	private String version;
	
	@JsonProperty( "ProtoVer" )
	private String protocolVersion;
	
	@JsonProperty( "VideoLookups" )
	private List<VideoLookup> videoLookups;
	
	public VideoLookups() { }

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
	 * @return the videoLookups
	 */
	public List<VideoLookup> getVideoLookups() {
		return videoLookups;
	}

	/**
	 * @param videoLookups the videoLookups to set
	 */
	public void setVideoLookups( List<VideoLookup> videoLookups ) {
		this.videoLookups = videoLookups;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "VideoLookups [count=" );
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
		
		if( videoLookups != null ) {
			builder.append( "videoLookups=" );
			builder.append( videoLookups );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
