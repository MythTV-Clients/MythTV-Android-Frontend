/**
 * 
 */
package org.mythtv.db.channel.model;

import org.mythtv.services.api.channel.ChannelInfo;

/**
 * @author dmfrey
 *
 */
public class ChannelDelegate extends ChannelInfo {

	private static final long serialVersionUID = -1851275815249447522L;

	private long id;
	
	/**
	 * 
	 */
	public ChannelDelegate() {
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
	public void setId( long id ) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		builder.append( "ChannelDelegate [id=" );
		builder.append( id );
		builder.append( ", channel=" );
		builder.append( super.toString() );
		builder.append( "]" );

		return builder.toString();
	}

}
