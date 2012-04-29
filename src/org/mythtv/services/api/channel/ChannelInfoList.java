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

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class ChannelInfoList {

	@JsonProperty( "ChannelInfoList" )
	private ChannelInfos channelInfos;
	
	public ChannelInfoList() { }

	/**
	 * @return the channelInfos
	 */
	protected ChannelInfos getChannelInfos() {
		return channelInfos;
	}

	/**
	 * @param channelInfos the channelInfos to set
	 */
	protected void setChannelInfos( ChannelInfos channelInfos ) {
		this.channelInfos = channelInfos;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "ChannelInfoList [" );
		
		if( channelInfos != null ) {
			builder.append( "channelInfos=" );
			builder.append( channelInfos );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
