/**
 * 
 */
package org.mythtv.db.frontends.model;

import java.io.Serializable;

/**
 * @author Daniel Frey
 *
 */
public class Frontends implements Serializable {

	private static final long serialVersionUID = -3141516546562897095L;

	private int count;

	public Frontends() { }

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
		builder.append( "Frontends [count=" );
		builder.append( count );
		builder.append( "]" );
		return builder.toString();
	}
	
}
