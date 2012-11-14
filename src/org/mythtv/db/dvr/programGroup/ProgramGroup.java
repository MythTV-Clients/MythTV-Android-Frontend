/**
 * 
 */
package org.mythtv.db.dvr.programGroup;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGroup {

	private Long id;
	private String programGroup;
	private String title;
	private String category;
	private String inetref;
	
	public ProgramGroup() { }

	public ProgramGroup( Long id, String programGroup, String title, String category, String inetref ) {
		this.id = id;
		this.programGroup = programGroup;
		this.title = title;
		this.category = category;
		this.inetref = inetref;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId( Long id ) {
		this.id = id;
	}

	/**
	 * @return the programGroup
	 */
	public String getProgramGroup() {
		return programGroup;
	}

	/**
	 * @param programGroup the programGroup to set
	 */
	public void setProgramGroup( String programGroup ) {
		this.programGroup = programGroup;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle( String title ) {
		this.title = title;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory( String category ) {
		this.category = category;
	}

	/**
	 * @return the inetref
	 */
	public String getInetref() {
		return inetref;
	}

	/**
	 * @param inetref the inetref to set
	 */
	public void setInetref( String inetref ) {
		this.inetref = inetref;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "ProgramGroup [" );
		if( id != null ) {
			builder.append( "id=" );
			builder.append( id );
			builder.append( ", " );
		}
		if( programGroup != null ) {
			builder.append( "programGroup=" );
			builder.append( programGroup );
			builder.append( ", " );
		}
		if( title != null ) {
			builder.append( "title=" );
			builder.append( title );
			builder.append( ", " );
		}
		if( category != null ) {
			builder.append( "category=" );
			builder.append( category );
			builder.append( ", " );
		}
		if( inetref != null ) {
			builder.append( "inetref=" );
			builder.append( inetref );
		}
		builder.append( "]" );
		return builder.toString();
	}
	
}
