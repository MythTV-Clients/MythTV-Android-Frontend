/**
 * 
 */
package org.mythtv.db.dvr.model;

import org.mythtv.services.api.dvr.Recording;

/**
 * @author dmfrey
 *
 */
public class RecordingDelegate extends Recording {

	private static final long serialVersionUID = 4405725599090964606L;

	private long id;
	
	/**
	 * 
	 */
	public RecordingDelegate() { 
		super();
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append( "RecordingDelegate [id=" );
		builder.append( id );
		builder.append( ", recording=" );
		builder.append( super.toString() );
		builder.append( "]" );
		
		return builder.toString();
	}

}
