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
public class ConnectionInfo {

	@JsonProperty( "Version" )
	private VersionInfo version;
	
	@JsonProperty( "Database" )
	private DatabaseInfo database;
	
	@JsonProperty( "WOL" )
	private WOLInfo wol;
	
	public ConnectionInfo() { }

	/**
	 * @return the version
	 */
	public VersionInfo getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion( VersionInfo version ) {
		this.version = version;
	}

	/**
	 * @return the database
	 */
	public DatabaseInfo getDatabase() {
		return database;
	}

	/**
	 * @param database the database to set
	 */
	public void setDatabase( DatabaseInfo database ) {
		this.database = database;
	}

	/**
	 * @return the wol
	 */
	public WOLInfo getWol() {
		return wol;
	}

	/**
	 * @param wol the wol to set
	 */
	public void setWol( WOLInfo wol ) {
		this.wol = wol;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "ConnectionInfo [" );
		
		if( version != null ) {
			builder.append( "version=" );
			builder.append( version );
			builder.append( ", " );
		}
		
		if( database != null ) {
			builder.append( "database=" );
			builder.append( database );
			builder.append( ", " );
		}
		
		if( wol != null ) {
			builder.append( "wol=" );
			builder.append( wol );
		}
		
		builder.append( "]" );
		
		return builder.toString();
	}
	
}
