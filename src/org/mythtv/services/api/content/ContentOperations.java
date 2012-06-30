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
package org.mythtv.services.api.content;

import java.util.Date;
import java.util.List;

public interface ContentOperations {

	/**
	 * - GET
	 * 
	 * @param storageGroup
	 * @param filename
	 * @param hostname
	 * @param maxSegments
	 * @param width
	 * @param height
	 * @param bitrate
	 * @param audioBitrate
	 * @param sampleRate
	 * @return
	 */
	LiveStreamInfo addLiveStream( String storageGroup, String filename, String hostname, int maxSegments, int width, int height, int bitrate, int audioBitrate, int sampleRate );
	
	/**
	 * - GET
	 * 
	 * @param channelId
	 * @param startTime
	 * @param maxSegments
	 * @param width
	 * @param height
	 * @param bitrate
	 * @param audioBitrate
	 * @param sampleRate
	 * @return
	 */
	LiveStreamInfo addRecordingLiveStream( int channelId, Date startTime, int maxSegments, int width, int height, int bitrate, int audioBitrate, int sampleRate );
	
	/**
	 * - GET
	 * 
	 * @param id
	 * @param maxSegments
	 * @param width
	 * @param height
	 * @param bitrate
	 * @param audioBitrate
	 * @param sampleRate
	 * @return
	 */
	LiveStreamInfo addVideoLiveStream( int id, int maxSegments, int width, int height, int bitrate, int audioBitrate, int sampleRate );
	
	/**
	 * - POST
	 * 
	 * @param url
	 * @param storageGroup
	 * @return
	 */
	boolean downloadFile( String url, String storageGroup );
	
	/**
	 * - GET
	 * 
	 * @param id
	 * @param width
	 * @param height
	 * @return
	 */
	byte[] getAlbumArt( int id, int width, int height );
	
	/**
	 * - GET
	 * 
	 * @param storageGroup
	 * @param filename
	 * @return
	 */
	byte[] getFile( String storageGroup, String filename );
	
	/**
	 * - GET
	 * 
	 * @param storageGroup
	 * @return
	 */
	List<String> getFileList( String storageGroup );
	
	/**
	 * - GET
	 * 
	 * @param filename
	 * @return
	 */
	List<LiveStreamInfo> getFilteredLiveStreamList( String filename );
	
	/**
	 * - GET
	 * 
	 * @param storageGroup
	 * @param filename
	 * @return
	 */
	String getHash( String storageGroup, String filename );
	
	/**
	 * - GET
	 * 
	 * @param storageGroup
	 * @param filename
	 * @param width
	 * @param height
	 * @return
	 */
	byte[] getImageFile( String storageGroup, String filename, int width, int height );
	
	/**
	 * - GET
	 * 
	 * @param id
	 * @return
	 */
	LiveStreamInfo getLiveStream( int id );
	
	/**
	 * - GET
	 * 
	 * @return
	 */
	List<LiveStreamInfo> getLiveStreamList();

	/**
	 * - GET
	 * 
	 * @param id
	 * @return
	 */
	byte[] getMusic( int id );
	
	/**
	 * - GET
	 * 
	 * @param channelId
	 * @param startTime
	 * @param width
	 * @param height
	 * @param secondsIn
	 * @return
	 */
	byte[] getPreviewImage( int channelId, Date startTime, int width, int height, int secondsIn );
	
	/**
	 * - GET
	 * 
	 * @param inetRef
	 * @param season
	 * @return
	 */
	List<ArtworkInfo> getProgramArtworkList( String inetRef, int season );
	
	/**
	 * - GET
	 * 
	 * @param channelId
	 * @param startTime
	 * @return
	 */
	byte[] getRecording( int channelId, Date startTime );
	
	/**
	 * - GET
	 * 
	 * @param type
	 * @param inetRef
	 * @param season
	 * @param width
	 * @param height
	 * @return
	 */
	byte[] getRecordingArtwork( String type, String inetRef, int season, int width, int height );
	
	/**
	 * - GET
	 * 
	 * @param channelId
	 * @param startTime
	 * @return
	 */
	List<ArtworkInfo> getRecordingArtworkList( int channelId, Date startTime );
	
	/**
	 * - GET
	 * 
	 * @param id
	 * @return
	 */
	byte[] getVideo( int id );
	
	/**
	 * - GET
	 * 
	 * @param type
	 * @param id
	 * @param width
	 * @param height
	 * @return
	 */
	byte[] getVideoArtwork( String type, int id, int width, int height );
	
	/**
	 * - GET
	 * 
	 * @param id
	 * @return
	 */
	boolean removeLiveStream( int id );
	
	/**
	 * - GET
	 * 
	 * @param id
	 * @return
	 */
	LiveStreamInfo stopLiveStream( int id );
	
}
