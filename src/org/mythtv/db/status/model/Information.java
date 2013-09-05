/**
 * 
 */
package org.mythtv.db.status.model;

import java.io.Serializable;

/**
 * @author Daniel Frey
 *
 */
public class Information implements Serializable {

	private static final long serialVersionUID = 3108320000769166596L;

	private String display;
	private String name;
	private String value;
	
	public Information() { }

	/**
	 * @return the display
	 */
	public String getDisplay() {
		return display;
	}

	/**
	 * @param display the display to set
	 */
	public void setDisplay( String display ) {
		this.display = display;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue( String value ) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Information [" );
		if( display != null ) {
			builder.append( "display=" );
			builder.append( display );
			builder.append( ", " );
		}
		if( name != null ) {
			builder.append( "name=" );
			builder.append( name );
			builder.append( ", " );
		}
		if( value != null ) {
			builder.append( "value=" );
			builder.append( value );
		}
		builder.append( "]" );
		return builder.toString();
	}
	
}
