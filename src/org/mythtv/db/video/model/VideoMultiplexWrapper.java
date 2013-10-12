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

/**
 * @author Sebastien Astie
 *
 */
public class VideoMultiplexWrapper {

	@JsonProperty( "VideoMultiplex" )
	private VideoMultiplex videoMultiplex;
	
	public VideoMultiplexWrapper() { }

	/**
	 * @return the videoMultiplex
	 */
	public VideoMultiplex getVideoMultiplex() {
		return videoMultiplex;
	}

	/**
	 * @param videoMultiplex the videoMultiplex to set
	 */
	public void setVideoMultiplex( VideoMultiplex videoMultiplex ) {
		this.videoMultiplex = videoMultiplex;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "VideoMultiplexWrapper [" );
		
		if( videoMultiplex != null ) {
			builder.append( "videoMultiplex=" );
			builder.append( videoMultiplex );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}

}
