package org.mythtv.services.api;

import org.codehaus.jackson.annotate.JsonProperty;

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
