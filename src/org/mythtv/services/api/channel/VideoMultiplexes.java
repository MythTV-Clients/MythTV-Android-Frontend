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
public class VideoMultiplexes {

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
	
	@JsonProperty( "VideoMultiplexes" )
	private List<VideoMultiplex> videoMultiplexes;

	public VideoMultiplexes() { }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "VideoMultiplexes [startIndex=" );
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
		
		if( videoMultiplexes != null ) {
			builder.append( "videoMultiplexes=" );
			builder.append( videoMultiplexes );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
