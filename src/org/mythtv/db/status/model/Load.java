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
@Root( name = "Load" )
public class Load {

	@Attribute( name = "avg1" )
	private float averageOne;
	
	@Attribute( name = "avg2" )
	private float averageTwo;
	
	@Attribute( name = "avg3" )
	private float averageThree;
	
	public Load() { }

	/**
	 * @return the averageOne
	 */
	public float getAverageOne() {
		return averageOne;
	}

	/**
	 * @param averageOne the averageOne to set
	 */
	public void setAverageOne( float averageOne ) {
		this.averageOne = averageOne;
	}

	/**
	 * @return the averageTwo
	 */
	public float getAverageTwo() {
		return averageTwo;
	}

	/**
	 * @param averageTwo the averageTwo to set
	 */
	public void setAverageTwo( float averageTwo ) {
		this.averageTwo = averageTwo;
	}

	/**
	 * @return the averageThree
	 */
	public float getAverageThree() {
		return averageThree;
	}

	/**
	 * @param averageThree the averageThree to set
	 */
	public void setAverageThree( float averageThree ) {
		this.averageThree = averageThree;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Load [averageOne=" );
		builder.append( averageOne );
		builder.append( ", averageTwo=" );
		builder.append( averageTwo );
		builder.append( ", averageThree=" );
		builder.append( averageThree );
		builder.append( "]" );
		return builder.toString();
	}
	
}
