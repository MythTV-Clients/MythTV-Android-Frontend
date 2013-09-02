/**
 * 
 */
package org.mythtv.db.myth.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * @author Daniel Frey
 *
 */
@Root( name = "Storage" )
public class Storage {

	@ElementList( inline = true )
	private List<Group> groups;
	
	public Storage() { }

	/**
	 * @return the groups
	 */
	public List<Group> getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups( List<Group> groups ) {
		this.groups = groups;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Storage [" );
		if( groups != null ) {
			builder.append( "groups=" );
			builder.append( groups );
		}
		builder.append( "]" );
		return builder.toString();
	}
	
}
