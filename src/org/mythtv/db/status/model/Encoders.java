/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * This software can be found at <https://github.com/MythTV-Android/MythTV-Service-API/>
 *
 */
package org.mythtv.db.status.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Daniel Frey
 *
 */
public class Encoders implements Serializable {

	private static final long serialVersionUID = 5159257234976125609L;

	private int count;
	private List<Encoder> encoders;
	
	public Encoders() { }

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return the encoders
	 */
	public List<Encoder> getEncoders() {
		return encoders;
	}

	/**
	 * @param encoders the encoders to set
	 */
	public void setEncoders( List<Encoder> encoders ) {
		this.encoders = encoders;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "Encoders [" );
		
		builder.append( "count=" );
		builder.append( count );
		
		if( encoders != null ) {
			builder.append( "encoders=" );
			builder.append( encoders );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
