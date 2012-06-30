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
package org.mythtv.services.api.myth;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class DatabaseInfo {

	@JsonProperty( "Host" )
	private String host;
	
	@JsonProperty( "Ping" )
	private boolean ping;
	
	@JsonProperty( "Port" )
	private int port;
	
	@JsonProperty( "UserName" )
	private String username;
	
	@JsonProperty( "Password" )
	private String password;
	
	@JsonProperty( "Name" )
	private String name;
	
	@JsonProperty( "Type" )
	private String type;
	
	@JsonProperty( "LocalEnabled" )
	private boolean localEnabled;
	
	@JsonProperty( "LocalHostName" )
	private String localHostName;
	
	public DatabaseInfo() { }

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost( String host ) {
		this.host = host;
	}

	/**
	 * @return the ping
	 */
	public boolean isPing() {
		return ping;
	}

	/**
	 * @param ping the ping to set
	 */
	public void setPing( boolean ping ) {
		this.ping = ping;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort( int port ) {
		this.port = port;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername( String username ) {
		this.username = username;
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
	 * @return the localEnabled
	 */
	public boolean isLocalEnabled() {
		return localEnabled;
	}

	/**
	 * @param localEnabled the localEnabled to set
	 */
	public void setLocalEnabled( boolean localEnabled ) {
		this.localEnabled = localEnabled;
	}

	/**
	 * @return the localHostName
	 */
	public String getLocalHostName() {
		return localHostName;
	}

	/**
	 * @param localHostName the localHostName to set
	 */
	public void setLocalHostName( String localHostName ) {
		this.localHostName = localHostName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "DatabaseInfo [" );
		
		if( host != null ) {
			builder.append( "host=" );
			builder.append( host );
			builder.append( ", " );
		}
		
		builder.append( "ping=" );
		builder.append( ping );
		builder.append( ", port=" );
		builder.append( port );
		builder.append( ", " );
		
		if( username != null ) {
			builder.append( "username=" );
			builder.append( username );
			builder.append( ", " );
		}
		
		if( password != null ) {
			builder.append( "password=" );
			builder.append( password );
			builder.append( ", " );
		}
		
		if( name != null ) {
			builder.append( "name=" );
			builder.append( name );
			builder.append( ", " );
		}
		
		if( type != null ) {
			builder.append( "type=" );
			builder.append( type );
			builder.append( ", " );
		}
		
		builder.append( "localEnabled=" );
		builder.append( localEnabled );
		builder.append( ", " );
		
		if( localHostName != null ) {
			builder.append( "localHostName=" );
			builder.append( localHostName );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
