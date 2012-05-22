package org.mythtv.services.api.frontend;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Thomas G. Kenny Jr
 *
 */
public class Bool {

	@JsonProperty( "bool" )
	private Boolean bool;
	
	public Bool() { }

	/**
	 * @return the boolean
	 */
	public Boolean getBool() {
		return bool;
	}

	/**
	 * @param b the boolean to set
	 */
	public void setBool( Boolean b ) {
		this.bool = b;
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
			//builder.append( ", " );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
}
