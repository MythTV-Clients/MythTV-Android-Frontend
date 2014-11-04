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
 * 
 */
package org.mythtv.db.content;

/**
 * @author Daniel Frey
 *
 */
public enum ContentEndpoint {
	ADD_LIVE_STREAM( "AddLiveStream" ),
	ADD_RECORDING_LIVE_STREAM( "AddRecordingLiveStream" ),
	ADD_VIDEO_LIVE_STREAM( "AddVideoLiveStream" ),
	DOWNLOAD_FILE( "DownloadFile" ),
	GET_ALBUM_ART( "GetAlbumArt" ),
	GET_FILE( "GetFile"),
	GET_FILE_LIST( "GetFileList" ),
	GET_FILTERED_LIVE_STREAM_LIST( "GetFilteredLiveStreamList" ),
	GET_HASH( "GetHash" ),
	GET_IMAGE_FILE( "GetImageFile" ),
	GET_LIVE_STREAM( "GetLiveStream" ),
	GET_LIVE_STREAM_LIST( "GetLiveStreamList" ),
	GET_MUSIC( "GetMusic" ),
	GET_PREVIEW_IMAGE( "GetPreviewImage" ),
	GET_PROGRAM_ARTWORK_LIST( "GetProgramArtworkList" ),
	GET_RECORDING( "GetRecording" ),
	GET_RECORDING_ARTWORK( "GetRecordingArtwork" ),
	GET_RECORDING_ARTWORK_LIST( "GetRecordingArtworkList" ),
	GET_VIDEO( "GetVideo" ),
	GET_VIDEO_ARTWORK( "GetVideoArtwork"),
	REMOVE_LIVE_STREAM( "RemoveLiveStream" ),
	STOP_LIVE_STREAM( "StopLiveStream" );
	
	private String endpoint;
	
	private ContentEndpoint( String endpoint ) {
		this.endpoint = endpoint;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

}
