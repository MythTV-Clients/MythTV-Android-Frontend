package org.mythtv.services.api;

import org.codehaus.jackson.annotate.JsonProperty;

public class Bool {
	
	@JsonProperty( "bool" )
	private Boolean bool;
	
	public Bool() { }

	/**
	 * @return the bool
	 */
	public Boolean getBool() {
		return bool;
	}

	/**
	 * @param bool the bool to set
	 */
	public void setBool( Boolean bool ) {
		this.bool = bool;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "Bool [" );
		
		if( bool != null ) {
			builder.append( "bool=" );
			builder.append( bool );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}