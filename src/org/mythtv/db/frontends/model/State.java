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
package org.mythtv.db.frontends.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Thomas G. Kenny Jr
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {

	
	private List<StateStringItem> states;
	
	@JsonProperty("CurrentLocation")
	private String currentLocation;
	
	
	public State() { }
	
	
	
	/**
	 * @return the states
	 */
	public List<StateStringItem> getStates() {
		return states;
	}

	/**
	 * @param states the states to set
	 */
	public void setStates( List<StateStringItem> states ) {
		this.states = states;
	}
	
	public String getCurrentLocation(){
		return currentLocation;
	}
	
	public void setCurrentLocation(String location){
		currentLocation = location;
	}
	
	
	
//	/* (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		
//		builder.append( "State [" );
//		
//		if( states != null ) {
//			builder.append( states );
//		}
//		
//		builder.append( "]" );
//	
//		return builder.toString();
//	}
	
}
