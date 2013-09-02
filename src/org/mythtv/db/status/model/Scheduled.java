/**
 * 
 */
package org.mythtv.db.status.model;

import org.mythtv.db.dvr.model.Program;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * @author Daniel Frey
 *
 */
@Root( name = "Scheduled" )
public class Scheduled {

	@Attribute
	private int count = 0;

	@ElementList( inline = true )
	private List<Program> programs;
	
	public Scheduled() { }

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
	 * @return the programs
	 */
	public List<Program> getPrograms() {
		return programs;
	}

	/**
	 * @param programs the programs to set
	 */
	public void setPrograms( List<Program> programs ) {
		this.programs = programs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Scheduled [count=" );
		builder.append( count );
		builder.append( ", " );
		if( programs != null ) {
			builder.append( "programs=" );
			builder.append( programs );
		}
		builder.append( "]" );
		return builder.toString();
	}
	
}
