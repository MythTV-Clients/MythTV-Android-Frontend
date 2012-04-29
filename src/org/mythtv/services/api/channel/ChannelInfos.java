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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.services.api.channel;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class ChannelInfos {

	@JsonProperty( "StartIndex" )
	private int startIndex;

	@JsonProperty( "Count" )
	private int count;
	
	@JsonProperty( "CurrentPage" )
	private int currentPage;
	
	@JsonProperty( "TotalPages" )
	private int totalPages;
	
	@JsonProperty( "TotalAvailable" )
	private int totalAvailable;
	
	@JsonProperty( "AsOf" )
	private Date asOf;
	
	@JsonProperty( "Version" )
	private String version;
	
	@JsonProperty( "ProtoVer" )
	private String protocolVersion;
	
	@JsonProperty( "ChannelInfos" )
	private List<ChannelInfo> channelInfos;
	
	public ChannelInfos() { }

	/**
	 * @return the startIndex
	 */
	protected int getStartIndex() {
		return startIndex;
	}

	/**
	 * @param startIndex the startIndex to set
	 */
	protected void setStartIndex( int startIndex ) {
		this.startIndex = startIndex;
	}

	/**
	 * @return the count
	 */
	protected int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	protected void setCount( int count ) {
		this.count = count;
	}

	/**
	 * @return the currentPage
	 */
	protected int getCurrentPage() {
		return currentPage;
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	protected void setCurrentPage( int currentPage ) {
		this.currentPage = currentPage;
	}

	/**
	 * @return the totalPages
	 */
	protected int getTotalPages() {
		return totalPages;
	}

	/**
	 * @param totalPages the totalPages to set
	 */
	protected void setTotalPages( int totalPages ) {
		this.totalPages = totalPages;
	}

	/**
	 * @return the totalAvailable
	 */
	protected int getTotalAvailable() {
		return totalAvailable;
	}

	/**
	 * @param totalAvailable the totalAvailable to set
	 */
	protected void setTotalAvailable( int totalAvailable ) {
		this.totalAvailable = totalAvailable;
	}

	/**
	 * @return the asOf
	 */
	protected Date getAsOf() {
		return asOf;
	}

	/**
	 * @param asOf the asOf to set
	 */
	protected void setAsOf( Date asOf ) {
		this.asOf = asOf;
	}

	/**
	 * @return the version
	 */
	protected String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	protected void setVersion( String version ) {
		this.version = version;
	}

	/**
	 * @return the protocolVersion
	 */
	protected String getProtocolVersion() {
		return protocolVersion;
	}

	/**
	 * @param protocolVersion the protocolVersion to set
	 */
	protected void setProtocolVersion( String protocolVersion ) {
		this.protocolVersion = protocolVersion;
	}

	/**
	 * @return the channelInfos
	 */
	protected List<ChannelInfo> getChannelInfos() {
		return channelInfos;
	}

	/**
	 * @param channelInfos the channelInfos to set
	 */
	protected void setChannelInfos( List<ChannelInfo> channelInfos ) {
		this.channelInfos = channelInfos;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "ChannelInfos [startIndex=" );
		builder.append( startIndex );
		builder.append( ", count=" );
		builder.append( count );
		builder.append( ", currentPage=" );
		builder.append( currentPage );
		builder.append( ", totalPages=" );
		builder.append( totalPages );
		builder.append( ", totalAvailable=" );
		builder.append( totalAvailable );
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
		
		if( channelInfos != null ) {
			builder.append( "channelInfos=" );
			builder.append( channelInfos );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
