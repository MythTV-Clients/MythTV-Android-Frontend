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
package org.mythtv.services.api.content;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class LiveStreamInfo {

	@JsonProperty( "id" )
	private int id;
	
	@JsonProperty( "Width" )
	private int width;
	
	@JsonProperty( "Height" )
	private int height;
	
	@JsonProperty( "Bitrate" )
	private int bitrate;
	
	@JsonProperty( "AudioBitrate" )
	private int audioBitrate;
	
	@JsonProperty( "SegmentSize" )
	private int segmentSize;
	
	@JsonProperty( "MaxSegments" )
	private int maxSegments;
	
	@JsonProperty( "StartSegment" )
	private int startSegment;
	
	@JsonProperty( "CurrentSegment" )
	private int currentSegment;
	
	@JsonProperty( "SegmentCount" )
	private int segmentCount;
	
	@JsonProperty( "PercentComplete" )
	private int percentComplete;
	
	@JsonProperty( "Created" )
	private Date created;
	
	@JsonProperty( "LastModified" )
	private Date lastModified;
	
	@JsonProperty( "RelativeURL" )
	private String relativeUrl;
	
	@JsonProperty( "FullURL" )
	private String fullUrl;
	
	@JsonProperty( "StatusStr" )
	private String statusStr;
	
	@JsonProperty( "StatusInt" )
	private int statusInt;
	
	@JsonProperty( "StatusMessage" )
	private String statusMessage;
	
	@JsonProperty( "SourceFile" )
	private String sourceFile;
	
	@JsonProperty( "SourceHost" )
	private String sourceHost;
	
	@JsonProperty( "SourceWidth" )
	private int sourceWidth;
	
	@JsonProperty( "SourceHeight" )
	private int sourceHeight;
	
	@JsonProperty( "AudioOnlyBitrate" )
	private int audioOnlyBitrate;
	
	public LiveStreamInfo() { }

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId( int id ) {
		this.id = id;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth( int width ) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight( int height ) {
		this.height = height;
	}

	/**
	 * @return the bitrate
	 */
	public int getBitrate() {
		return bitrate;
	}

	/**
	 * @param bitrate the bitrate to set
	 */
	public void setBitrate( int bitrate ) {
		this.bitrate = bitrate;
	}

	/**
	 * @return the audioBitrate
	 */
	public int getAudioBitrate() {
		return audioBitrate;
	}

	/**
	 * @param audioBitrate the audioBitrate to set
	 */
	public void setAudioBitrate( int audioBitrate ) {
		this.audioBitrate = audioBitrate;
	}

	/**
	 * @return the segmentSize
	 */
	public int getSegmentSize() {
		return segmentSize;
	}

	/**
	 * @param segmentSize the segmentSize to set
	 */
	public void setSegmentSize( int segmentSize ) {
		this.segmentSize = segmentSize;
	}

	/**
	 * @return the maxSegments
	 */
	public int getMaxSegments() {
		return maxSegments;
	}

	/**
	 * @param maxSegments the maxSegments to set
	 */
	public void setMaxSegments( int maxSegments ) {
		this.maxSegments = maxSegments;
	}

	/**
	 * @return the startSegment
	 */
	public int getStartSegment() {
		return startSegment;
	}

	/**
	 * @param startSegment the startSegment to set
	 */
	public void setStartSegment( int startSegment ) {
		this.startSegment = startSegment;
	}

	/**
	 * @return the currentSegment
	 */
	public int getCurrentSegment() {
		return currentSegment;
	}

	/**
	 * @param currentSegment the currentSegment to set
	 */
	public void setCurrentSegment( int currentSegment ) {
		this.currentSegment = currentSegment;
	}

	/**
	 * @return the segmentCount
	 */
	public int getSegmentCount() {
		return segmentCount;
	}

	/**
	 * @param segmentCount the segmentCount to set
	 */
	public void setSegmentCount( int segmentCount ) {
		this.segmentCount = segmentCount;
	}

	/**
	 * @return the percentComplete
	 */
	public int getPercentComplete() {
		return percentComplete;
	}

	/**
	 * @param percentComplete the percentComplete to set
	 */
	public void setPercentComplete( int percentComplete ) {
		this.percentComplete = percentComplete;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated( Date created ) {
		this.created = created;
	}

	/**
	 * @return the lastModified
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified( Date lastModified ) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the relativeUrl
	 */
	public String getRelativeUrl() {
		return relativeUrl;
	}

	/**
	 * @param relativeUrl the relativeUrl to set
	 */
	public void setRelativeUrl( String relativeUrl ) {
		this.relativeUrl = relativeUrl;
	}

	/**
	 * @return the fullUrl
	 */
	public String getFullUrl() {
		return fullUrl;
	}

	/**
	 * @param fullUrl the fullUrl to set
	 */
	public void setFullUrl( String fullUrl ) {
		this.fullUrl = fullUrl;
	}

	/**
	 * @return the statusStr
	 */
	public String getStatusStr() {
		return statusStr;
	}

	/**
	 * @param statusStr the statusStr to set
	 */
	public void setStatusStr( String statusStr ) {
		this.statusStr = statusStr;
	}

	/**
	 * @return the statusInt
	 */
	public int getStatusInt() {
		return statusInt;
	}

	/**
	 * @param statusInt the statusInt to set
	 */
	public void setStatusInt( int statusInt ) {
		this.statusInt = statusInt;
	}

	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}

	/**
	 * @param statusMessage the statusMessage to set
	 */
	public void setStatusMessage( String statusMessage ) {
		this.statusMessage = statusMessage;
	}

	/**
	 * @return the sourceFile
	 */
	public String getSourceFile() {
		return sourceFile;
	}

	/**
	 * @param sourceFile the sourceFile to set
	 */
	public void setSourceFile( String sourceFile ) {
		this.sourceFile = sourceFile;
	}

	/**
	 * @return the sourceHost
	 */
	public String getSourceHost() {
		return sourceHost;
	}

	/**
	 * @param sourceHost the sourceHost to set
	 */
	public void setSourceHost( String sourceHost ) {
		this.sourceHost = sourceHost;
	}

	/**
	 * @return the sourceWidth
	 */
	public int getSourceWidth() {
		return sourceWidth;
	}

	/**
	 * @param sourceWidth the sourceWidth to set
	 */
	public void setSourceWidth( int sourceWidth ) {
		this.sourceWidth = sourceWidth;
	}

	/**
	 * @return the sourceHeight
	 */
	public int getSourceHeight() {
		return sourceHeight;
	}

	/**
	 * @param sourceHeight the sourceHeight to set
	 */
	public void setSourceHeight( int sourceHeight ) {
		this.sourceHeight = sourceHeight;
	}

	/**
	 * @return the audioOnlyBitrate
	 */
	public int getAudioOnlyBitrate() {
		return audioOnlyBitrate;
	}

	/**
	 * @param audioOnlyBitrate the audioOnlyBitrate to set
	 */
	public void setAudioOnlyBitrate( int audioOnlyBitrate ) {
		this.audioOnlyBitrate = audioOnlyBitrate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "LiveStreamInfo [id=" );
		builder.append( id );
		builder.append( ", width=" );
		builder.append( width );
		builder.append( ", height=" );
		builder.append( height );
		builder.append( ", bitrate=" );
		builder.append( bitrate );
		builder.append( ", audioBitrate=" );
		builder.append( audioBitrate );
		builder.append( ", segmentSize=" );
		builder.append( segmentSize );
		builder.append( ", maxSegments=" );
		builder.append( maxSegments );
		builder.append( ", startSegment=" );
		builder.append( startSegment );
		builder.append( ", currentSegment=" );
		builder.append( currentSegment );
		builder.append( ", segmentCount=" );
		builder.append( segmentCount );
		builder.append( ", percentComplete=" );
		builder.append( percentComplete );
		builder.append( ", " );
		
		if( created != null ) {
			builder.append( "created=" );
			builder.append( created );
			builder.append( ", " );
		}
		
		if( lastModified != null ) {
			builder.append( "lastModified=" );
			builder.append( lastModified );
			builder.append( ", " );
		}
		
		if( relativeUrl != null ) {
			builder.append( "relativeUrl=" );
			builder.append( relativeUrl );
			builder.append( ", " );
		}
		
		if( fullUrl != null ) {
			builder.append( "fullUrl=" );
			builder.append( fullUrl );
			builder.append( ", " );
		}
		
		if( statusStr != null ) {
			builder.append( "statusStr=" );
			builder.append( statusStr );
			builder.append( ", " );
		}

		builder.append( "statusInt=" );
		builder.append( statusInt );
		builder.append( ", " );
		
		if( statusMessage != null ) {
			builder.append( "statusMessage=" );
			builder.append( statusMessage );
			builder.append( ", " );
		}
		
		if( sourceFile != null ) {
			builder.append( "sourceFile=" );
			builder.append( sourceFile );
			builder.append( ", " );
		}
		
		if( sourceHost != null ) {
			builder.append( "sourceHost=" );
			builder.append( sourceHost );
			builder.append( ", " );
		}
		
		builder.append( "sourceWidth=" );
		builder.append( sourceWidth );
		builder.append( ", sourceHeight=" );
		builder.append( sourceHeight );
		builder.append( ", audioOnlyBitrate=" );
		builder.append( audioOnlyBitrate );
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
