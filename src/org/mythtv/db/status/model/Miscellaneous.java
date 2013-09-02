/**
 * 
 */
package org.mythtv.db.status.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * @author Daniel Frey
 *
 */
@Root( name = "Miscellaneous" )
public class Miscellaneous {

	@ElementList( inline = true, required = false )
	private List<Information> informations;
	
	public Miscellaneous() { }

	/**
	 * @return the informations
	 */
	public List<Information> getInformations() {
		return informations;
	}

	/**
	 * @param informations the informations to set
	 */
	public void setInformations( List<Information> informations ) {
		this.informations = informations;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Miscellaneous [" );
		if( informations != null ) {
			builder.append( "informations=" );
			builder.append( informations );
		}
		builder.append( "]" );
		return builder.toString();
	}
	
}
