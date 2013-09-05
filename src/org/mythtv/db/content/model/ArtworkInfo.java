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
package org.mythtv.db.content.model;

import java.io.Serializable;

/**
 * @author Daniel Frey
 *
 */
public class ArtworkInfo implements Serializable {

	private static final long serialVersionUID = 3848873347031400915L;

	private Long id;
	private String url;
	private String filename;
	private String storageGroup;
	private String type;
	
	public ArtworkInfo() { }

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId( Long id ) {
		this.id = id;
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
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename( String filename ) {
		this.filename = filename;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "ArtworkInfo [" );
		
		if( null != id ) {
			builder.append( "id=" );
			builder.append( id );
			builder.append( ", " );
		}
		
		if( url != null ) {
			builder.append( "url=" );
			builder.append( url );
			builder.append( ", " );
		}
		
		if( filename != null ) {
			builder.append( "filename=" );
			builder.append( filename );
			builder.append( ", " );
		}
		
		if( storageGroup != null ) {
			builder.append( "storageGroup=" );
			builder.append( storageGroup );
			builder.append( ", " );
		}
		
		if( type != null ) {
			builder.append( "type=" );
			builder.append( type );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
