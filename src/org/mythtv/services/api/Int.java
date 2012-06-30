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
package org.mythtv.services.api;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author pot8oe
 *
 */
public class Int {
	
	@JsonProperty( "int" )
	private Integer integer;
	
	public Int() { }

	/**
	 * @return the integer
	 */
	public Integer getInteger() {
		return integer;
	}

	/**
	 * @param integer the integer to set
	 */
	public void setInteger( Integer integer ) {
		this.integer = integer;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "Int [" );
		
		if( integer != null ) {
			builder.append( "integer=" );
			builder.append( integer );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
