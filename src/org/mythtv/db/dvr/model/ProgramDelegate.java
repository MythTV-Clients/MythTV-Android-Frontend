/**
 * 
 */
package org.mythtv.db.dvr.model;

import org.mythtv.services.api.dvr.Program;

/**
 * @author dmfrey
 *
 */
public class ProgramDelegate extends Program {

	private static final long serialVersionUID = 756554413240507680L;

	private long id;
	
	/**
	 * 
	 */
	public ProgramDelegate() {	
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
		builder.append( "ProgramDelegate [id=" );
		builder.append( id );
		builder.append( ", program=" );
		builder.append( super.toString() );
		builder.append( "]" );
		
		return builder.toString();
	}

}
