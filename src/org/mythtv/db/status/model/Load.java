/**
 * This file is part of MythTV Android Frontend
 *
 * MythTV Android Frontend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MythTV Android Frontend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MythTV Android Frontend.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This software can be found at <https://github.com/MythTV-Clients/MythTV-Android-Frontend/>
 */
/**
 * 
 */
package org.mythtv.db.status.model;

import java.io.Serializable;

/**
 * @author Daniel Frey
 *
 */
public class Load implements Serializable {

	private static final long serialVersionUID = -6703872551756748411L;

	private float averageOne;
	private float averageTwo;
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
