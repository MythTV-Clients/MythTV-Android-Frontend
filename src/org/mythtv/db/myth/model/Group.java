/**
 * 
 */
package org.mythtv.db.myth.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * @author Daniel Frey
 *
 */
@Root( name = "Group" )
public class Group {

	@Attribute( name = "dir" )
	private String directory;
	
	@Attribute
	private String id;
	
	@Attribute
	private int free;
	
	@Attribute( required = false )
	private boolean deleted;
	
	@Attribute
	private int total;
	
	@Attribute
	private int used;
	
	@Attribute( required = false )
	private int expirable;
	
	@Attribute( name = "livetv", required = false )
	private boolean liveTv;

	public Group() { }

	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param directory the directory to set
	 */
	public void setDirectory( String directory ) {
		this.directory = directory;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId( String id ) {
		this.id = id;
	}

	/**
	 * @return the free
	 */
	public int getFree() {
		return free;
	}

	/**
	 * @param free the free to set
	 */
	public void setFree( int free ) {
		this.free = free;
	}

	/**
	 * @return the deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted( boolean deleted ) {
		this.deleted = deleted;
	}

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal( int total ) {
		this.total = total;
	}

	/**
	 * @return the used
	 */
	public int getUsed() {
		return used;
	}

	/**
	 * @param used the used to set
	 */
	public void setUsed( int used ) {
		this.used = used;
	}

	/**
	 * @return the expirable
	 */
	public int getExpirable() {
		return expirable;
	}

	/**
	 * @param expirable the expirable to set
	 */
	public void setExpirable( int expirable ) {
		this.expirable = expirable;
	}

	/**
	 * @return the liveTv
	 */
	public boolean isLiveTv() {
		return liveTv;
	}

	/**
	 * @param liveTv the liveTv to set
	 */
	public void setLiveTv( boolean liveTv ) {
		this.liveTv = liveTv;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Group [" );
		if( directory != null ) {
			builder.append( "directory=" );
			builder.append( directory );
			builder.append( ", " );
		}
		if( id != null ) {
			builder.append( "id=" );
			builder.append( id );
			builder.append( ", " );
		}
		builder.append( "free=" );
		builder.append( free );
		builder.append( ", deleted=" );
		builder.append( deleted );
		builder.append( ", total=" );
		builder.append( total );
		builder.append( ", used=" );
		builder.append( used );
		builder.append( ", expirable=" );
		builder.append( expirable );
		builder.append( ", liveTv=" );
		builder.append( liveTv );
		builder.append( "]" );
		return builder.toString();
	}
	
}
