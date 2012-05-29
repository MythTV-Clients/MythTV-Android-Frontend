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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.dvr;

import android.graphics.Bitmap;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGroup implements Comparable<ProgramGroup> {

	private String name;
	private int count;
	private Bitmap banner;
	
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

	/**
	 * @return the banner
	 */
	public Bitmap getBanner() {
		return banner;
	}

	/**
	 * @param banner the banner to set
	 */
	public void setBanner( Bitmap banner ) {
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
		
		builder.append( "count=" );
		builder.append( count );
		builder.append( ", " );
		
		if( banner != null ) {
			builder.append( "banner=" );
			builder.append( banner.getWidth() + "x" + banner.getHeight() );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
