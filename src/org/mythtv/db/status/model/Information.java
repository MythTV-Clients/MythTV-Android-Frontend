/**
 * 
 */
package org.mythtv.db.status.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * @author Daniel Frey
 *
 */
@Root( name = "Information" )
public class Information {

	@Attribute( name = "display", required = false )
	private String display;
	
	@Attribute( name = "name", required = false )
	private String name;
	
	@Attribute( name = "value", required = false )
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
