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
package org.mythtv.db.captureCard.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class Lineup {

	@JsonProperty( "LineupId" )
	private String lineupId;
	
	@JsonProperty( "Name" )
	private String name;
	
	@JsonProperty( "DisplayName" )
	private String displayName;
	
	@JsonProperty( "Type" )
	private String type;
	
	@JsonProperty( "Postal" )
	private String postal;
	
	@JsonProperty( "Device" )
	private String device;
	
	public Lineup() { }

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName( String displayName ) {
		this.displayName = displayName;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType( String type ) {
		this.type = type;
	}

	/**
	 * @return the postal
	 */
	public String getPostal() {
		return postal;
	}

	/**
	 * @param postal the postal to set
	 */
	public void setPostal( String postal ) {
		this.postal = postal;
	}

	/**
	 * @return the device
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * @param device the device to set
	 */
	public void setDevice( String device ) {
		this.device = device;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "Lineup [" );
		
		if( lineupId != null ) {
			builder.append( "lineupId=" );
			builder.append( lineupId );
			builder.append( ", " );
		}
		
		if( name != null ) {
			builder.append( "name=" );
			builder.append( name );
			builder.append( ", " );
		}
		
		if( displayName != null ) {
			builder.append( "displayName=" );
			builder.append( displayName );
			builder.append( ", " );
		}
		
		if( type != null ) {
			builder.append( "type=" );
			builder.append( type );
			builder.append( ", " );
		}
		
		if( postal != null ) {
			builder.append( "postal=" );
			builder.append( postal );
			builder.append( ", " );
		}
		
		if( device != null ) {
			builder.append( "device=" );
			builder.append( device );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
