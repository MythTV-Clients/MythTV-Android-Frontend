/**
 * 
 */
package org.mythtv.services.api.frontend;

/**
 * @author Daniel Frey
 *
 */
public class State {

	private String key;
	private String value;
	
	public State() { }

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey( String key ) {
		this.key = key;
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
		
		builder.append( "State [" );
		
		if( key != null ) {
			builder.append( "key=" );
			builder.append( key );
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
