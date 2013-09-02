/**
 * 
 */
package org.mythtv.db.guide.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGuideWrapper {

	@JsonProperty( "ProgramGuide" )
	private ProgramGuide programGuide;
	
	public ProgramGuideWrapper() { }

	/**
	 * @return the programGuide
	 */
	public ProgramGuide getProgramGuide() {
		return programGuide;
	}

	/**
	 * @param programGuide the programGuide to set
	 */
	public void setProgramGuide( ProgramGuide programGuide ) {
		this.programGuide = programGuide;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "ProgramGuideWrapper [" );
		
		if( programGuide != null ) {
			builder.append( "programGuide=" );
			builder.append( programGuide );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
