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
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.services.api.video;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class VideoMetadataInfoList {

	@JsonProperty( "VideoMetadataInfoList" )
	private VideoMetadataInfos videoMetadataInfos;

	public VideoMetadataInfoList() { }

	/**
	 * @return the videoMetadataInfos
	 */
	public VideoMetadataInfos getVideoMetadataInfos() {
		return videoMetadataInfos;
	}

	/**
	 * @param videoMetadataInfos the videoMetadataInfos to set
	 */
	public void setVideoMetadataInfos( VideoMetadataInfos videoMetadataInfos ) {
		this.videoMetadataInfos = videoMetadataInfos;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "VideoMetadataInfoList [" );
		
		if( videoMetadataInfos != null ) {
			builder.append( "videoMetadataInfos=" );
			builder.append( videoMetadataInfos );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
