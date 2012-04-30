/**
 * 
 */
package org.mythtv.services.api.dvr;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class RecRuleList {

	@JsonProperty( "RecRuleList" )
	private RecRules recRules;
	
	public RecRuleList() { }

	/**
	 * @return the recRules
	 */
	public RecRules getRecRules() {
		return recRules;
	}

	/**
	 * @param recRules the recRules to set
	 */
	public void setRecRules( RecRules recRules ) {
		this.recRules = recRules;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "RecRuleList [" );
		
		if( recRules != null ) {
			builder.append( "recRules=" );
			builder.append( recRules );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
