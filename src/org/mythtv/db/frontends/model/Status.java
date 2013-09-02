package org.mythtv.db.frontends.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Thomas G. Kenny Jr
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
	
	
	@JsonProperty( "State" )
	private State state;
	
	
//	@JsonProperty( "ChapterTimes" )
//	@JsonProperty( "SubtitleTracks" )
//	@JsonProperty( "AudioTracks" )
	
	
	public Status(){}
	
	public State getState(){
		return state;
	}
	
	public void setState(State s){
		state = s;
	}
	
	

}
