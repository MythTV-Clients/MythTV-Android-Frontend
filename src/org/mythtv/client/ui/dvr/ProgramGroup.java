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
package org.mythtv.client.ui.dvr;

import java.util.List;

import org.mythtv.services.api.dvr.Program;

import android.graphics.drawable.Drawable;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGroup implements Comparable<ProgramGroup> {

	private String name;
	private List<Program> recordings;
	private Drawable banner;
	
	public ProgramGroup() { }

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
	 * @return the recordings
	 */
	public List<Program> getRecordings() {
		return recordings;
	}

	/**
	 * @param recordings the recordings to set
	 */
	public void setRecordings( List<Program> recordings ) {
		this.recordings = recordings;
	}

	/**
	 * @return the banner
	 */
	public Drawable getBanner() {
		return banner;
	}

	/**
	 * @param banner the banner to set
	 */
	public void setBanner( Drawable banner ) {
		this.banner = banner;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		
		int result = 1;
		result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( Object obj ) {
		if( this == obj ) {
			return true;
		}
		
		if( obj == null ) {
			return false;
		}
		
		if( getClass() != obj.getClass() ) {
			return false;
		}
		
		ProgramGroup other = (ProgramGroup) obj;
		if( name == null ) {
			if( other.name != null ) {
				return false;
			}
		} else if( !name.equals( other.name ) ) {
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo( ProgramGroup another ) {
		return this.name.compareTo( another.name ); 
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "ProgramGroup [" );
		
		if( name != null ) {
			builder.append( "name=" );
			builder.append( name );
			builder.append( ", " );
		}
		
		if( recordings != null ) {
			builder.append( "recordings=" );
			builder.append( recordings.size() );
			builder.append( ", " );
		}
		
		builder.append( "banner=" );
		builder.append( null == banner ? "not set" : "set" );

		builder.append( "]" );
	
		return builder.toString();
	}
	
}
