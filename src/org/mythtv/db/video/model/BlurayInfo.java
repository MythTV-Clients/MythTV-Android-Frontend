/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * This software can be found at <https://github.com/MythTV-Android/MythTV-Service-API/>
 *
 */
package org.mythtv.db.video.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class BlurayInfo {

	@JsonProperty( "Path" )
	private String path;
	
	@JsonProperty( "Title" )
	private String title;
	
	@JsonProperty( "AltTitle" )
	private String altTitle;
	
	@JsonProperty( "DiscLang" )
	private String discLang;
	
	@JsonProperty( "DiscNum" )
	private int discNumber;
	
	@JsonProperty( "TotalDiscNum" )
	private int totalDiscNumber;
	
	@JsonProperty( "TitleCount" )
	private int titleCount;
	
	@JsonProperty( "ThumbCount" )
	private int thumbCount;
	
	@JsonProperty( "ThumbPath" )
	private String thumbPath;
	
	@JsonProperty( "TopMenuSupported" )
	private boolean topMenuSupported;
	
	@JsonProperty( "FirstPlaySupported" )
	private boolean firstPlaySupported;
	
	@JsonProperty( "NumHDMVTitles" )
	private int numberHDMVTitles;
	
	@JsonProperty( "NumBDJTitles" )
	private int numberBDJTitles;
	
	@JsonProperty( "NumUnsupportedTitles" )
	private int numberUnsupportedTitles;
	
	@JsonProperty( "AACSDetected" )
	private boolean aacSDetected;
	
	@JsonProperty( "LibAACSDetected" )
	private boolean libAACSDetected;
	
	@JsonProperty( "AACSHandled" )
	private boolean aacSHandled;
 	
	@JsonProperty( "BDPlusDetected" )
	private boolean bdPlusDetected;
	
	@JsonProperty( "LibBDPlusDetected" )
	private boolean libBDPlusDetected;
	
	@JsonProperty( "BDPlusHandled" )
	private boolean bdPlusHandled;

	public BlurayInfo() { }

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath( String path ) {
		this.path = path;
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
	 * @return the altTitle
	 */
	public String getAltTitle() {
		return altTitle;
	}

	/**
	 * @param altTitle the altTitle to set
	 */
	public void setAltTitle( String altTitle ) {
		this.altTitle = altTitle;
	}

	/**
	 * @return the discLang
	 */
	public String getDiscLang() {
		return discLang;
	}

	/**
	 * @param discLang the discLang to set
	 */
	public void setDiscLang( String discLang ) {
		this.discLang = discLang;
	}

	/**
	 * @return the discNumber
	 */
	public int getDiscNumber() {
		return discNumber;
	}

	/**
	 * @param discNumber the discNumber to set
	 */
	public void setDiscNumber( int discNumber ) {
		this.discNumber = discNumber;
	}

	/**
	 * @return the totalDiscNumber
	 */
	public int getTotalDiscNumber() {
		return totalDiscNumber;
	}

	/**
	 * @param totalDiscNumber the totalDiscNumber to set
	 */
	public void setTotalDiscNumber( int totalDiscNumber ) {
		this.totalDiscNumber = totalDiscNumber;
	}

	/**
	 * @return the titleCount
	 */
	public int getTitleCount() {
		return titleCount;
	}

	/**
	 * @param titleCount the titleCount to set
	 */
	public void setTitleCount( int titleCount ) {
		this.titleCount = titleCount;
	}

	/**
	 * @return the thumbCount
	 */
	public int getThumbCount() {
		return thumbCount;
	}

	/**
	 * @param thumbCount the thumbCount to set
	 */
	public void setThumbCount( int thumbCount ) {
		this.thumbCount = thumbCount;
	}

	/**
	 * @return the thumbPath
	 */
	public String getThumbPath() {
		return thumbPath;
	}

	/**
	 * @param thumbPath the thumbPath to set
	 */
	public void setThumbPath( String thumbPath ) {
		this.thumbPath = thumbPath;
	}

	/**
	 * @return the topMenuSupported
	 */
	public boolean isTopMenuSupported() {
		return topMenuSupported;
	}

	/**
	 * @param topMenuSupported the topMenuSupported to set
	 */
	public void setTopMenuSupported( boolean topMenuSupported ) {
		this.topMenuSupported = topMenuSupported;
	}

	/**
	 * @return the firstPlaySupported
	 */
	public boolean isFirstPlaySupported() {
		return firstPlaySupported;
	}

	/**
	 * @param firstPlaySupported the firstPlaySupported to set
	 */
	public void setFirstPlaySupported( boolean firstPlaySupported ) {
		this.firstPlaySupported = firstPlaySupported;
	}

	/**
	 * @return the numberHDMVTitles
	 */
	public int getNumberHDMVTitles() {
		return numberHDMVTitles;
	}

	/**
	 * @param numberHDMVTitles the numberHDMVTitles to set
	 */
	public void setNumberHDMVTitles( int numberHDMVTitles ) {
		this.numberHDMVTitles = numberHDMVTitles;
	}

	/**
	 * @return the numberBDJTitles
	 */
	public int getNumberBDJTitles() {
		return numberBDJTitles;
	}

	/**
	 * @param numberBDJTitles the numberBDJTitles to set
	 */
	public void setNumberBDJTitles( int numberBDJTitles ) {
		this.numberBDJTitles = numberBDJTitles;
	}

	/**
	 * @return the numberUnsupportedTitles
	 */
	public int getNumberUnsupportedTitles() {
		return numberUnsupportedTitles;
	}

	/**
	 * @param numberUnsupportedTitles the numberUnsupportedTitles to set
	 */
	public void setNumberUnsupportedTitles( int numberUnsupportedTitles ) {
		this.numberUnsupportedTitles = numberUnsupportedTitles;
	}

	/**
	 * @return the aacSDetected
	 */
	public boolean isAacSDetected() {
		return aacSDetected;
	}

	/**
	 * @param aacSDetected the aacSDetected to set
	 */
	public void setAacSDetected( boolean aacSDetected ) {
		this.aacSDetected = aacSDetected;
	}

	/**
	 * @return the libAACSDetected
	 */
	public boolean isLibAACSDetected() {
		return libAACSDetected;
	}

	/**
	 * @param libAACSDetected the libAACSDetected to set
	 */
	public void setLibAACSDetected( boolean libAACSDetected ) {
		this.libAACSDetected = libAACSDetected;
	}

	/**
	 * @return the aacSHandled
	 */
	public boolean isAacSHandled() {
		return aacSHandled;
	}

	/**
	 * @param aacSHandled the aacSHandled to set
	 */
	public void setAacSHandled( boolean aacSHandled ) {
		this.aacSHandled = aacSHandled;
	}

	/**
	 * @return the bdPlusDetected
	 */
	public boolean isBdPlusDetected() {
		return bdPlusDetected;
	}

	/**
	 * @param bdPlusDetected the bdPlusDetected to set
	 */
	public void setBdPlusDetected( boolean bdPlusDetected ) {
		this.bdPlusDetected = bdPlusDetected;
	}

	/**
	 * @return the libBDPlusDetected
	 */
	public boolean isLibBDPlusDetected() {
		return libBDPlusDetected;
	}

	/**
	 * @param libBDPlusDetected the libBDPlusDetected to set
	 */
	public void setLibBDPlusDetected( boolean libBDPlusDetected ) {
		this.libBDPlusDetected = libBDPlusDetected;
	}

	/**
	 * @return the bdPlusHandled
	 */
	public boolean isBdPlusHandled() {
		return bdPlusHandled;
	}

	/**
	 * @param bdPlusHandled the bdPlusHandled to set
	 */
	public void setBdPlusHandled( boolean bdPlusHandled ) {
		this.bdPlusHandled = bdPlusHandled;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "BlurayInfo [" );
		
		if( path != null ) {
			builder.append( "path=" );
			builder.append( path );
			builder.append( ", " );
		}
		
		if( title != null ) {
			builder.append( "title=" );
			builder.append( title );
			builder.append( ", " );
		}
		
		if( altTitle != null ) {
			builder.append( "altTitle=" );
			builder.append( altTitle );
			builder.append( ", " );
		}
		
		if( discLang != null ) {
			builder.append( "discLang=" );
			builder.append( discLang );
			builder.append( ", " );
		}
		
		builder.append( "discNumber=" );
		builder.append( discNumber );
		builder.append( ", totalDiscNumber=" );
		builder.append( totalDiscNumber );
		builder.append( ", titleCount=" );
		builder.append( titleCount );
		builder.append( ", thumbCount=" );
		builder.append( thumbCount );
		builder.append( ", " );
		
		if( thumbPath != null ) {
			builder.append( "thumbPath=" );
			builder.append( thumbPath );
			builder.append( ", " );
		}
		
		builder.append( "topMenuSupported=" );
		builder.append( topMenuSupported );
		builder.append( ", firstPlaySupported=" );
		builder.append( firstPlaySupported );
		builder.append( ", numberHDMVTitles=" );
		builder.append( numberHDMVTitles );
		builder.append( ", numberBDJTitles=" );
		builder.append( numberBDJTitles );
		builder.append( ", numberUnsupportedTitles=" );
		builder.append( numberUnsupportedTitles );
		builder.append( ", aacSDetected=" );
		builder.append( aacSDetected );
		builder.append( ", libAACSDetected=" );
		builder.append( libAACSDetected );
		builder.append( ", aacSHandled=" );
		builder.append( aacSHandled );
		builder.append( ", bdPlusDetected=" );
		builder.append( bdPlusDetected );
		builder.append( ", libBDPlusDetected=" );
		builder.append( libBDPlusDetected );
		builder.append( ", bdPlusHandled=" );
		builder.append( bdPlusHandled );
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
