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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.services.api.video;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;
import org.mythtv.services.api.content.ArtworkInfoList;

/**
 * @author Daniel Frey
 *
 */
public class VideoMetadataInfo {

	@JsonProperty( "Id" )
	private int	id;

	@JsonProperty( "Title" )
	private String title;

	@JsonProperty( "SubTitle" )
	private String subTitle;

	@JsonProperty( "Tagline" )
	private String tagline;

	@JsonProperty( "Director" )
	private String director;

	@JsonProperty( "Studio" )
	private String studio;

	@JsonProperty( "Description" )
	private String description;

	@JsonProperty( "Certification" )
	private String certification;

	@JsonProperty( "Inetref" )
	private String inetref;

	@JsonProperty( "Collectionref" )
	private int collectionReference;

	@JsonProperty( "HomePage" )
	private String homePage;

	@JsonProperty( "ReleaseDate" )
	private Date releaseDate;

	@JsonProperty( "AddDate" )
	private Date addDate;

	@JsonProperty( "UserRating" )
	private float userRating;

	@JsonProperty( "Length" )
	private int length;

	@JsonProperty( "PlayCount" )
	private int playCount;

	@JsonProperty( "Season" )
	private int season;

	@JsonProperty( "Episode" )
	private int episode;

	@JsonProperty( "ParentalLevel" )
	private int parentalLevel;

	@JsonProperty( "Visible" )
	private boolean visible;

	@JsonProperty( "Watched" )
	private boolean watched;

	@JsonProperty( "Processed" )
	private boolean processed;

	@JsonProperty( "ContentType" )
	private String contentType;

	@JsonProperty( "FileName" )
	private String fileName;

	@JsonProperty( "Hash" )
	private String hash;

	@JsonProperty( "HostName" )
	private String hostname;

	@JsonProperty( "Coverart" )
	private String coverart;

	@JsonProperty( "Fanart" )
	private String fanart;

	@JsonProperty( "Banner" )
	private String banner;

	@JsonProperty( "Screenshot" )
	private String screenshot;

	@JsonProperty( "Trailer" )
	private String trailer;

	@JsonProperty( "Artwork" )
	private ArtworkInfoList	artwork;

	public VideoMetadataInfo() { }

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId( int id ) {
		this.id = id;
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
	 * @return the subTitle
	 */
	public String getSubTitle() {
		return subTitle;
	}

	/**
	 * @param subTitle the subTitle to set
	 */
	public void setSubTitle( String subTitle ) {
		this.subTitle = subTitle;
	}

	/**
	 * @return the tagline
	 */
	public String getTagline() {
		return tagline;
	}

	/**
	 * @param tagline the tagline to set
	 */
	public void setTagline( String tagline ) {
		this.tagline = tagline;
	}

	/**
	 * @return the director
	 */
	public String getDirector() {
		return director;
	}

	/**
	 * @param director the director to set
	 */
	public void setDirector( String director ) {
		this.director = director;
	}

	/**
	 * @return the studio
	 */
	public String getStudio() {
		return studio;
	}

	/**
	 * @param studio the studio to set
	 */
	public void setStudio( String studio ) {
		this.studio = studio;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription( String description ) {
		this.description = description;
	}

	/**
	 * @return the certification
	 */
	public String getCertification() {
		return certification;
	}

	/**
	 * @param certification the certification to set
	 */
	public void setCertification( String certification ) {
		this.certification = certification;
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

	/**
	 * @return the collectionReference
	 */
	public int getCollectionReference() {
		return collectionReference;
	}

	/**
	 * @param collectionReference the collectionReference to set
	 */
	public void setCollectionReference( int collectionReference ) {
		this.collectionReference = collectionReference;
	}

	/**
	 * @return the homePage
	 */
	public String getHomePage() {
		return homePage;
	}

	/**
	 * @param homePage the homePage to set
	 */
	public void setHomePage( String homePage ) {
		this.homePage = homePage;
	}

	/**
	 * @return the releaseDate
	 */
	public Date getReleaseDate() {
		return releaseDate;
	}

	/**
	 * @param releaseDate the releaseDate to set
	 */
	public void setReleaseDate( Date releaseDate ) {
		this.releaseDate = releaseDate;
	}

	/**
	 * @return the addDate
	 */
	public Date getAddDate() {
		return addDate;
	}

	/**
	 * @param addDate the addDate to set
	 */
	public void setAddDate( Date addDate ) {
		this.addDate = addDate;
	}

	/**
	 * @return the userRating
	 */
	public float getUserRating() {
		return userRating;
	}

	/**
	 * @param userRating the userRating to set
	 */
	public void setUserRating( float userRating ) {
		this.userRating = userRating;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength( int length ) {
		this.length = length;
	}

	/**
	 * @return the playCount
	 */
	public int getPlayCount() {
		return playCount;
	}

	/**
	 * @param playCount the playCount to set
	 */
	public void setPlayCount( int playCount ) {
		this.playCount = playCount;
	}

	/**
	 * @return the season
	 */
	public int getSeason() {
		return season;
	}

	/**
	 * @param season the season to set
	 */
	public void setSeason( int season ) {
		this.season = season;
	}

	/**
	 * @return the episode
	 */
	public int getEpisode() {
		return episode;
	}

	/**
	 * @param episode the episode to set
	 */
	public void setEpisode( int episode ) {
		this.episode = episode;
	}

	/**
	 * @return the parentalLevel
	 */
	public int getParentalLevel() {
		return parentalLevel;
	}

	/**
	 * @param parentalLevel the parentalLevel to set
	 */
	public void setParentalLevel( int parentalLevel ) {
		this.parentalLevel = parentalLevel;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible( boolean visible ) {
		this.visible = visible;
	}

	/**
	 * @return the watched
	 */
	public boolean isWatched() {
		return watched;
	}

	/**
	 * @param watched the watched to set
	 */
	public void setWatched( boolean watched ) {
		this.watched = watched;
	}

	/**
	 * @return the processed
	 */
	public boolean isProcessed() {
		return processed;
	}

	/**
	 * @param processed the processed to set
	 */
	public void setProcessed( boolean processed ) {
		this.processed = processed;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType( String contentType ) {
		this.contentType = contentType;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName( String fileName ) {
		this.fileName = fileName;
	}

	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @param hash the hash to set
	 */
	public void setHash( String hash ) {
		this.hash = hash;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname( String hostname ) {
		this.hostname = hostname;
	}

	/**
	 * @return the coverart
	 */
	public String getCoverart() {
		return coverart;
	}

	/**
	 * @param coverart the coverart to set
	 */
	public void setCoverart( String coverart ) {
		this.coverart = coverart;
	}

	/**
	 * @return the fanart
	 */
	public String getFanart() {
		return fanart;
	}

	/**
	 * @param fanart the fanart to set
	 */
	public void setFanart( String fanart ) {
		this.fanart = fanart;
	}

	/**
	 * @return the banner
	 */
	public String getBanner() {
		return banner;
	}

	/**
	 * @param banner the banner to set
	 */
	public void setBanner( String banner ) {
		this.banner = banner;
	}

	/**
	 * @return the screenshot
	 */
	public String getScreenshot() {
		return screenshot;
	}

	/**
	 * @param screenshot the screenshot to set
	 */
	public void setScreenshot( String screenshot ) {
		this.screenshot = screenshot;
	}

	/**
	 * @return the trailer
	 */
	public String getTrailer() {
		return trailer;
	}

	/**
	 * @param trailer the trailer to set
	 */
	public void setTrailer( String trailer ) {
		this.trailer = trailer;
	}

	/**
	 * @return the artwork
	 */
	public ArtworkInfoList getArtwork() {
		return artwork;
	}

	/**
	 * @param artwork the artwork to set
	 */
	public void setArtwork( ArtworkInfoList artwork ) {
		this.artwork = artwork;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "VideoMetadataInfo [id=" );
		builder.append( id );
		builder.append( ", " );
		
		if( title != null ) {
			builder.append( "title=" );
			builder.append( title );
			builder.append( ", " );
		}
		
		if( subTitle != null ) {
			builder.append( "subTitle=" );
			builder.append( subTitle );
			builder.append( ", " );
		}
		
		if( tagline != null ) {
			builder.append( "tagline=" );
			builder.append( tagline );
			builder.append( ", " );
		}
		
		if( director != null ) {
			builder.append( "director=" );
			builder.append( director );
			builder.append( ", " );
		}
		
		if( studio != null ) {
			builder.append( "studio=" );
			builder.append( studio );
			builder.append( ", " );
		}
		
		if( description != null ) {
			builder.append( "description=" );
			builder.append( description );
			builder.append( ", " );
		}
		
		if( certification != null ) {
			builder.append( "certification=" );
			builder.append( certification );
			builder.append( ", " );
		}
		
		if( inetref != null ) {
			builder.append( "inetref=" );
			builder.append( inetref );
			builder.append( ", " );
		}
		
		builder.append( "collectionReference=" );
		builder.append( collectionReference );
		
		builder.append( ", " );
		
		if( homePage != null ) {
			builder.append( "homePage=" );
			builder.append( homePage );
			builder.append( ", " );
		}
		
		if( releaseDate != null ) {
			builder.append( "releaseDate=" );
			builder.append( releaseDate );
			builder.append( ", " );
		}
		
		if( addDate != null ) {
			builder.append( "addDate=" );
			builder.append( addDate );
			builder.append( ", " );
		}
		
		builder.append( "userRating=" );
		builder.append( userRating );
		builder.append( ", length=" );
		builder.append( length );
		builder.append( ", playCount=" );
		builder.append( playCount );
		builder.append( ", season=" );
		builder.append( season );
		builder.append( ", episode=" );
		builder.append( episode );
		builder.append( ", parentalLevel=" );
		builder.append( parentalLevel );
		builder.append( ", visible=" );
		builder.append( visible );
		builder.append( ", watched=" );
		builder.append( watched );
		builder.append( ", processed=" );
		builder.append( processed );
		builder.append( ", " );
		
		if( contentType != null ) {
			builder.append( "contentType=" );
			builder.append( contentType );
			builder.append( ", " );
		}
		
		if( fileName != null ) {
			builder.append( "fileName=" );
			builder.append( fileName );
			builder.append( ", " );
		}
		
		if( hash != null ) {
			builder.append( "hash=" );
			builder.append( hash );
			builder.append( ", " );
		}
		
		if( hostname != null ) {
			builder.append( "hostname=" );
			builder.append( hostname );
			builder.append( ", " );
		}
		
		if( coverart != null ) {
			builder.append( "coverart=" );
			builder.append( coverart );
			builder.append( ", " );
		}
		
		if( fanart != null ) {
			builder.append( "fanart=" );
			builder.append( fanart );
			builder.append( ", " );
		}
		
		if( banner != null ) {
			builder.append( "banner=" );
			builder.append( banner );
			builder.append( ", " );
		}
		
		if( screenshot != null ) {
			builder.append( "screenshot=" );
			builder.append( screenshot );
			builder.append( ", " );
		}
		
		if( trailer != null ) {
			builder.append( "trailer=" );
			builder.append( trailer );
			builder.append( ", " );
		}
		
		if( artwork != null ) {
			builder.append( "artwork=" );
			builder.append( artwork );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}