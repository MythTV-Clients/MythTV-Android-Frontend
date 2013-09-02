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
@Root
public class Backends {

	@Attribute( required = false )
	private int count;
	
	public Backends() { }

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount( int count ) {
		this.count = count;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Backends [count=" );
		builder.append( count );
		builder.append( "]" );
		return builder.toString();
	}
	
}
