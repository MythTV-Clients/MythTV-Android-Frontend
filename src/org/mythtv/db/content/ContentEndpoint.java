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
