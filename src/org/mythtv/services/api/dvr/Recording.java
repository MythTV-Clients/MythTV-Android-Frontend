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
package org.mythtv.services.api.dvr;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class Recording {

	@JsonProperty( "Status" )
	private int status;
	
	@JsonProperty( "Priority" )
	private int priority;
	
	@JsonProperty( "StartTs" )
	private Date startTimestamp;
	
	@JsonProperty( "EndTs" )
	private Date endTimestamp;
	
	@JsonProperty( "RecordId" )
	private int recordid;
	
	@JsonProperty( "RecGroup" )
	private String recordingGroup;
	
	@JsonProperty( "StorageGroup" )
	private String storageGroup;
	
	@JsonProperty( "PlayGroup" )
	private String playGroup;
	
	@JsonProperty( "RecType" )
	private int recordingType;
	
	@JsonProperty( "DupInType" )
	private int duplicateInType;
	
	@JsonProperty( "DupMethod" )
	private int duplicateMethod;
	
	@JsonProperty( "EncoderId" )
	private int encoderId;
	
	@JsonProperty( "Profile" )
	private String profile;
	
	public Recording() { }

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus( int status ) {
		this.status = status;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority( int priority ) {
		this.priority = priority;
	}

	/**
	 * @return the startTimestamp
	 */
	public Date getStartTimestamp() {
		return startTimestamp;
	}

	/**
	 * @param startTimestamp the startTimestamp to set
	 */
	public void setStartTimestamp( Date startTimestamp ) {
		this.startTimestamp = startTimestamp;
	}

	/**
	 * @return the endTimestamp
	 */
	public Date getEndTimestamp() {
		return endTimestamp;
	}

	/**
	 * @param endTimestamp the endTimestamp to set
	 */
	public void setEndTimestamp( Date endTimestamp ) {
		this.endTimestamp = endTimestamp;
	}

	/**
	 * @return the recordid
	 */
	public int getRecordid() {
		return recordid;
	}

	/**
	 * @param recordid the recordid to set
	 */
	public void setRecordid( int recordid ) {
		this.recordid = recordid;
	}

	/**
	 * @return the recordingGroup
	 */
	public String getRecordingGroup() {
		return recordingGroup;
	}

	/**
	 * @param recordingGroup the recordingGroup to set
	 */
	public void setRecordingGroup( String recordingGroup ) {
		this.recordingGroup = recordingGroup;
	}

	/**
	 * @return the storageGroup
	 */
	public String getStorageGroup() {
		return storageGroup;
	}

	/**
	 * @param storageGroup the storageGroup to set
	 */
	public void setStorageGroup( String storageGroup ) {
		this.storageGroup = storageGroup;
	}

	/**
	 * @return the playGroup
	 */
	public String getPlayGroup() {
		return playGroup;
	}

	/**
	 * @param playGroup the playGroup to set
	 */
	public void setPlayGroup( String playGroup ) {
		this.playGroup = playGroup;
	}

	/**
	 * @return the recordingType
	 */
	public int getRecordingType() {
		return recordingType;
	}

	/**
	 * @param recordingType the recordingType to set
	 */
	public void setRecordingType( int recordingType ) {
		this.recordingType = recordingType;
	}

	/**
	 * @return the duplicateInType
	 */
	public int getDuplicateInType() {
		return duplicateInType;
	}

	/**
	 * @param duplicateInType the duplicateInType to set
	 */
	public void setDuplicateInType( int duplicateInType ) {
		this.duplicateInType = duplicateInType;
	}

	/**
	 * @return the duplicateMethod
	 */
	public int getDuplicateMethod() {
		return duplicateMethod;
	}

	/**
	 * @param duplicateMethod the duplicateMethod to set
	 */
	public void setDuplicateMethod( int duplicateMethod ) {
		this.duplicateMethod = duplicateMethod;
	}

	/**
	 * @return the encoderId
	 */
	public int getEncoderId() {
		return encoderId;
	}

	/**
	 * @param encoderId the encoderId to set
	 */
	public void setEncoderId( int encoderId ) {
		this.encoderId = encoderId;
	}

	/**
	 * @return the profile
	 */
	public String getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile( String profile ) {
		this.profile = profile;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "Recording [status=" );
		
		builder.append( status );
		builder.append( ", priority=" );
		builder.append( priority );
		builder.append( ", " );
		
		if( startTimestamp != null ) {
			builder.append( "startTimestamp=" );
			builder.append( startTimestamp );
			builder.append( ", " );
		}
		
		if( endTimestamp != null ) {
			builder.append( "endTimestamp=" );
			builder.append( endTimestamp );
			builder.append( ", " );
		}
		
		builder.append( "recordid=" );
		builder.append( recordid );
		builder.append( ", " );
		
		if( recordingGroup != null ) {
			builder.append( "recordingGroup=" );
			builder.append( recordingGroup );
			builder.append( ", " );
		}
		
		if( storageGroup != null ) {
			builder.append( "storageGroup=" );
			builder.append( storageGroup );
			builder.append( ", " );
		}
		
		if( playGroup != null ) {
			builder.append( "playGroup=" );
			builder.append( playGroup );
			builder.append( ", " );
		}
		
		builder.append( "recordingType=" );
		builder.append( recordingType );
		builder.append( ", duplicateInType=" );
		builder.append( duplicateInType );
		builder.append( ", duplicateMethod=" );
		builder.append( duplicateMethod );
		builder.append( ", encoderId=" );
		builder.append( encoderId );
		builder.append( ", " );
		
		if( profile != null ) {
			builder.append( "profile=" );
			builder.append( profile );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
