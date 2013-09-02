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

import java.util.List;

/**
 * @author Daniel Frey
 *
 */
public class Lineups {

	@JsonProperty( "Lineups" )
	private List<Lineup> lineups;
	
	public Lineups() { }

	/**
	 * @return the lineups
	 */
	public List<Lineup> getLineups() {
		return lineups;
	}

	/**
	 * @param lineups the lineups to set
	 */
	public void setLineups( List<Lineup> lineups ) {
		this.lineups = lineups;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "Lineups [" );
		
		if( lineups != null ) {
			builder.append( "lineups=" );
			builder.append( lineups );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
