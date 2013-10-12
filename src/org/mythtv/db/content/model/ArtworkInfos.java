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
package org.mythtv.db.content.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Daniel Frey
 *
 */
public class ArtworkInfos implements Serializable {

	private static final long serialVersionUID = 223919846162208999L;

	private List<ArtworkInfo> artworkInfos;
	
	public ArtworkInfos() { }

	/**
	 * @return the artworkInfos
	 */
	public List<ArtworkInfo> getArtworkInfos() {
		return artworkInfos;
	}

	/**
	 * @param artworkInfos the artworkInfos to set
	 */
	public void setArtworkInfos( List<ArtworkInfo> artworkInfos ) {
		this.artworkInfos = artworkInfos;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "ArtworkInfos [" );
		
		if( artworkInfos != null ) {
			builder.append( "artworkInfos=" );
			builder.append( artworkInfos );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
