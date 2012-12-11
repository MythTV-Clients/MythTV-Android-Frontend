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
package org.mythtv.client.ui.preferences;

/**
 * @author Daniel Frey
 *
 */
public class LocationProfile {

	public static enum LocationType{ HOME, AWAY };
	
	private long id;
	private LocationType type;
	private String name;
	private String url;
	private boolean selected;
	private boolean connected;
	private String version;
	private String protocolVersion;
	private String wolAddress;
	private String hostname;
	
	public LocationProfile() { }

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
	 * @return the type
	 */
	public LocationType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType( LocationType type ) {
		this.type = type;
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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl( String url ) {
		this.url = url;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected( boolean selected ) {
		this.selected = selected;
	}

	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * @param connected the connected to set
	 */
	public void setConnected( boolean connected ) {
		this.connected = connected;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion( String version ) {
		this.version = version;
	}

	/**
	 * @return the protocolVersion
	 */
	public String getProtocolVersion() {
		return protocolVersion;
	}

	/**
	 * @param protocolVersion the protocolVersion to set
	 */
	public void setProtocolVersion( String protocolVersion ) {
		this.protocolVersion = protocolVersion;
	}

	/**
	 * @return the wolAddress
	 */
	public String getWolAddress() {
		return wolAddress;
	}

	/**
	 * @param wolAddress the wolAddress to set
	 */
	public void setWolAddress( String wolAddress ) {
		this.wolAddress = wolAddress;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname( String hostname ) {
		this.hostname = hostname;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "LocationProfile [id=" );
		builder.append( id );
		builder.append( ", " );
		
		if( type != null ) {
			builder.append( "type=" );
			builder.append( type );
			builder.append( ", " );
		}
		
		if( name != null ) {
			builder.append( "name=" );
			builder.append( name );
			builder.append( ", " );
		}
		
		if( url != null ) {
			builder.append( "url=" );
			builder.append( url );
			builder.append( ", " );
		}
		
		if( version != null ) {
			builder.append( "version=" );
			builder.append( version );
			builder.append( ", " );
		}
		
		if( protocolVersion != null ) {
			builder.append( "protocolVersion=" );
			builder.append( protocolVersion );
			builder.append( ", " );
		}
		
		if( wolAddress != null ) {
			builder.append( "wolAddress=" );
			builder.append( wolAddress );
			builder.append( ", " );
		}
		
		if( hostname != null ) {
			builder.append( "hostname=" );
			builder.append( hostname );
			builder.append( ", " );
		}
		
		builder.append( "selected=" );
		builder.append( selected );
		builder.append( ", " );
		
		builder.append( "connected=" );
		builder.append( connected );
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
