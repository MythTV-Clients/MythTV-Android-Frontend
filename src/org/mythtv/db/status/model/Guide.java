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

import org.joda.time.DateTime;

/**
 * @author Daniel Frey
 *
 */
public class Guide implements Serializable {

	private static final long serialVersionUID = 1077881628105945136L;

	private DateTime guideThru;
	private String status;
	private DateTime next;
	private String end;
	private int guideDays;
	private String start;
	private String comment;
	
	public Guide() { }

	/**
	 * @return the guideThru
	 */
	public DateTime getGuideThru() {
		return guideThru;
	}

	/**
	 * @param guideThru the guideThru to set
	 */
	public void setGuideThru( DateTime guideThru ) {
		this.guideThru = guideThru;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus( String status ) {
		this.status = status;
	}

	/**
	 * @return the next
	 */
	public DateTime getNext() {
		return next;
	}

	/**
	 * @param next the next to set
	 */
	public void setNext( DateTime next ) {
		this.next = next;
	}

	/**
	 * @return the end
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd( String end ) {
		this.end = end;
	}

	/**
	 * @return the guideDays
	 */
	public int getGuideDays() {
		return guideDays;
	}

	/**
	 * @param guideDays the guideDays to set
	 */
	public void setGuideDays( int guideDays ) {
		this.guideDays = guideDays;
	}

	/**
	 * @return the start
	 */
	public String getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart( String start ) {
		this.start = start;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment( String comment ) {
		this.comment = comment;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Guide [" );
		if( guideThru != null ) {
			builder.append( "guideThru=" );
			builder.append( guideThru );
			builder.append( ", " );
		}
		if( status != null ) {
			builder.append( "status=" );
			builder.append( status );
			builder.append( ", " );
		}
		if( next != null ) {
			builder.append( "next=" );
			builder.append( next );
			builder.append( ", " );
		}
		if( end != null ) {
			builder.append( "end=" );
			builder.append( end );
			builder.append( ", " );
		}
		builder.append( "guideDays=" );
		builder.append( guideDays );
		builder.append( ", " );
		if( start != null ) {
			builder.append( "start=" );
			builder.append( start );
			builder.append( ", " );
		}
		if( comment != null ) {
			builder.append( "comment=" );
			builder.append( comment );
		}
		builder.append( "]" );
		return builder.toString();
	}
	
}
