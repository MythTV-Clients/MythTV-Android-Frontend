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
