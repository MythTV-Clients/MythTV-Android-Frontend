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
package org.mythtv.db.channel.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class VideoSource {

	@JsonProperty( "Id" )
	private int id;
	
	@JsonProperty( "SourceName" )
	private String sourceName;
	
	@JsonProperty( "Grabber" )
	private String grabber;
	
	@JsonProperty( "UserId" )
	private String userId;
	
	@JsonProperty( "FreqTable" )
	private String frequencyTable;
	
	@JsonProperty( "LineupId" )
	private String lineupId;
	
	@JsonProperty( "Password" )
	private String password;
	
	@JsonProperty( "UseEIT" )
	private boolean useEIT;
	
	@JsonProperty( "ConfigPath" )
	private String configPath;
	
	@JsonProperty( "NITId" )
	private int nitId;
	
	public VideoSource() { }

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
	 * @return the sourceName
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * @param sourceName the sourceName to set
	 */
	public void setSourceName( String sourceName ) {
		this.sourceName = sourceName;
	}

	/**
	 * @return the grabber
	 */
	public String getGrabber() {
		return grabber;
	}

	/**
	 * @param grabber the grabber to set
	 */
	public void setGrabber( String grabber ) {
		this.grabber = grabber;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId( String userId ) {
		this.userId = userId;
	}

	/**
	 * @return the frequencyTable
	 */
	public String getFrequencyTable() {
		return frequencyTable;
	}

	/**
	 * @param frequencyTable the frequencyTable to set
	 */
	public void setFrequencyTable( String frequencyTable ) {
		this.frequencyTable = frequencyTable;
	}

	/**
	 * @return the lineupId
	 */
	public String getLineupId() {
		return lineupId;
	}

	/**
	 * @param lineupId the lineupId to set
	 */
	public void setLineupId( String lineupId ) {
		this.lineupId = lineupId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword( String password ) {
		this.password = password;
	}

	/**
	 * @return the useEIT
	 */
	public boolean isUseEIT() {
		return useEIT;
	}

	/**
	 * @param useEIT the useEIT to set
	 */
	public void setUseEIT( boolean useEIT ) {
		this.useEIT = useEIT;
	}

	/**
	 * @return the configPath
	 */
	public String getConfigPath() {
		return configPath;
	}

	/**
	 * @param configPath the configPath to set
	 */
	public void setConfigPath( String configPath ) {
		this.configPath = configPath;
	}

	/**
	 * @return the nitId
	 */
	public int getNitId() {
		return nitId;
	}

	/**
	 * @param nitId the nitId to set
	 */
	public void setNitId( int nitId ) {
		this.nitId = nitId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "VideoSource [id=" );
		builder.append( id );
		builder.append( ", " );
		
		if( sourceName != null ) {
			builder.append( "sourceName=" );
			builder.append( sourceName );
			builder.append( ", " );
		}
		
		if( grabber != null ) {
			builder.append( "grabber=" );
			builder.append( grabber );
			builder.append( ", " );
		}
		
		if( userId != null ) {
			builder.append( "userId=" );
			builder.append( userId );
			builder.append( ", " );
		}
		
		if( frequencyTable != null ) {
			builder.append( "frequencyTable=" );
			builder.append( frequencyTable );
			builder.append( ", " );
		}
		
		if( lineupId != null ) {
			builder.append( "lineupId=" );
			builder.append( lineupId );
			builder.append( ", " );
		}
		
		if( password != null ) {
			builder.append( "password=" );
			builder.append( password );
			builder.append( ", " );
		}
		
		builder.append( "useEIT=" );
		builder.append( useEIT );
		builder.append( ", " );
		
		if( configPath != null ) {
			builder.append( "configPath=" );
			builder.append( configPath );
			builder.append( ", " );
		}
		
		builder.append( "nitId=" );
		builder.append( nitId );
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
