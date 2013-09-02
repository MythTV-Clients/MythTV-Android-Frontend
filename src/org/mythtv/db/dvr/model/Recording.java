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
package org.mythtv.db.dvr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;
import org.mythtv.services.api.DateTimeSerializer;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * @author Daniel Frey
 *
 */
@Root( name = "Recording" )
public class Recording implements Serializable {

	private static final long serialVersionUID = 3815054457896074557L;

	private long id;

	@JsonProperty( "Status" )
	@Attribute( name = "recStatus" )
	private int status;
	
	@JsonProperty( "Priority" )
	@Attribute( name = "recPriority" )
	private int priority;
	
	@JsonProperty( "StartTs" )
	@JsonSerialize( using = DateTimeSerializer.class )
	@Attribute( name = "recStartTs" )
	private DateTime startTimestamp;
	
	@JsonProperty( "EndTs" )
	@JsonSerialize( using = DateTimeSerializer.class )
	@Attribute( name = "recEndTs" )
	private DateTime endTimestamp;
	
	@JsonProperty( "RecordId" )
	@Attribute( name = "recordId" )
	private int recordId;
	
	@JsonProperty( "RecGroup" )
	@Attribute( name = "recGroup" )
	private String recordingGroup;
	
	@JsonProperty( "StorageGroup" )
	private String storageGroup;
	
	@JsonProperty( "PlayGroup" )
	@Attribute( name = "playGroup" )
	private String playGroup;
	
	@JsonProperty( "RecType" )
	@Attribute( name = "recType" )
	private int recordingType;
	
	@JsonProperty( "DupInType" )
	@Attribute( name = "dupInType" )
	private int duplicateInType;
	
	@JsonProperty( "DupMethod" )
	@Attribute( name = "dupMethod" )
	private int duplicateMethod;
	
	@JsonProperty( "EncoderId" )
	@Attribute( name = "encoderId" )
	private int encoderId;
	
	@JsonProperty( "Profile" )
	@Attribute( name = "recProfile" )
	private String profile;
	
	public Recording() { }

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId( long id ) {
		this.id = id;
	}

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
	public DateTime getStartTimestamp() {
		return startTimestamp;
	}

	/**
	 * @param startTimestamp the startTimestamp to set
	 */
	public void setStartTimestamp( DateTime startTimestamp ) {
		this.startTimestamp = startTimestamp;
	}

	/**
	 * @return the endTimestamp
	 */
	public DateTime getEndTimestamp() {
		return endTimestamp;
	}

	/**
	 * @param endTimestamp the endTimestamp to set
	 */
	public void setEndTimestamp( DateTime endTimestamp ) {
		this.endTimestamp = endTimestamp;
	}

	/**
	 * @return the recordId
	 */
	public int getRecordId() {
		return recordId;
	}

	/**
	 * @param recordId the recordId to set
	 */
	public void setRecordId( int recordId ) {
		this.recordId = recordId;
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
		
		builder.append( "Recording [" );
		
		builder.append( "id=" );
		builder.append( id );
		builder.append( ", " );
		
		builder.append( "status=" );
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
		
		//if( recordId != null ) {
			builder.append( "recordId=" );
			builder.append( recordId );
			builder.append( ", " );
		//}
		
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
