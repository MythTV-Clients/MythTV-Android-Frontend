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
 * 
 */
package org.mythtv.db.captureCard.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Sebastien Astie
 *
 */
public class CaptureCardWrapper {

	@JsonProperty( "CaptureCard" )
	private CaptureCard captureCard;
	
	public CaptureCardWrapper() { }

	/**
	 * @return the captureCard
	 */
	public CaptureCard getCaptureCard() {
		return captureCard;
	}

	/**
	 * @param captureCard the captureCard to set
	 */
	public void setCaptureCard( CaptureCard captureCard ) {
		this.captureCard = captureCard;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "CaptureCardWrapper [" );
		
		if( captureCard != null ) {
			builder.append( "captureCard=" );
			builder.append( captureCard );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
