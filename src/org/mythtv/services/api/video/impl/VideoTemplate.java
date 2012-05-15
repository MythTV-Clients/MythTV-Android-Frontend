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
package org.mythtv.services.api.video.impl;

import java.util.List;

import org.mythtv.services.api.video.BlurayInfo;
import org.mythtv.services.api.video.VideoLookup;
import org.mythtv.services.api.video.VideoMetadataInfo;
import org.mythtv.services.api.video.VideoOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class VideoTemplate extends AbstractVideoOperations implements VideoOperations {

	private final RestTemplate restTemplate;

	public VideoTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#addVideo(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean addVideo( String filename, String hostname ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#getBluray(java.lang.String)
	 */
	@Override
	public BlurayInfo getBluray( String path ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#getVideo(int)
	 */
	@Override
	public VideoMetadataInfo getVideo( int id ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#getVideByFilename(java.lang.String)
	 */
	@Override
	public VideoMetadataInfo getVideByFilename( String filename ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#getVideoList(boolean, int, int)
	 */
	@Override
	public List<VideoMetadataInfo> getVideoList( boolean descending, int startIndex, int count ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#lookupVideo(java.lang.String, java.lang.String, java.lang.String, int, int, java.lang.String, boolean)
	 */
	@Override
	public List<VideoLookup> lookupVideo( String title, String subtitle, String inetRef, int season, int episode, String grabberType, boolean allowGeneric ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#removeVideoFromDatabase(int)
	 */
	@Override
	public boolean removeVideoFromDatabase( int id ) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
