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

/**
 * @author Daniel Frey
 *
 */
public class ProgramList {

	@JsonProperty( "ProgramList" )
	private Programs programs;
	
	/**
	 * 
	 */
	public ProgramList() { }

	/**
	 * @return the programs
	 */
	public Programs getPrograms() {
		return programs;
	}

	/**
	 * @param programs the programs to set
	 */
	public void setPrograms( Programs programs ) {
		this.programs = programs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "ProgramList [" );
		
		if( programs != null ) {
			builder.append( "programs=" );
			builder.append( programs );
		}
		
		builder.append( "]" );

		return builder.toString();
	}

}
