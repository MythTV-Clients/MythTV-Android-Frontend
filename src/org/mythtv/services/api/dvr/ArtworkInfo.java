/**
 * 
 */
package org.mythtv.services.api.dvr;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class ArtworkInfo {

	@JsonProperty( "URL" )
	private String url;
	
	@JsonProperty( "FileName" )
	private String filename;
	
	@JsonProperty( "StorageGroup" )
	private String storageGroup;
	
	@JsonProperty( "Type" )
	private String type;
	
	public ArtworkInfo() { }

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl( String url ) {
		this.url = url;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename( String filename ) {
		this.filename = filename;
	}

	/**
	 * @return the storageGroup
	 */
	public String getStorageGroup() {
		return storageGroup;
	}

	/**
	 * @param storageGroup the storageGroup to set
	 */
	public void setStorageGroup( String storageGroup ) {
		this.storageGroup = storageGroup;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType( String type ) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "ArtworkInfo [" );
		
		if( url != null ) {
			builder.append( "url=" );
			builder.append( url );
			builder.append( ", " );
		}
		
		if( filename != null ) {
			builder.append( "filename=" );
			builder.append( filename );
			builder.append( ", " );
		}
		
		if( storageGroup != null ) {
			builder.append( "storageGroup=" );
			builder.append( storageGroup );
			builder.append( ", " );
		}
		
		if( type != null ) {
			builder.append( "type=" );
			builder.append( type );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
