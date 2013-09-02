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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;
import org.mythtv.db.content.model.ArtworkItem;
import org.mythtv.services.api.DateTimeSerializer;

import java.util.List;

/**
 * @author Daniel Frey
 *
 */
public class VideoLookup {

	@JsonProperty( "Title" )
	private String title;

	@JsonProperty( "SubTitle" )
	private String subTitle;

	@JsonProperty( "Season" )
	private int season;

	@JsonProperty( "Episode" )
	private int episode;

	@JsonProperty( "Year" )
	private int year;

	@JsonProperty( "Tagline" )
	private String tagline;

	@JsonProperty( "Description" )
	private String description;

	@JsonProperty( "Certification" )
	private String certification;

	@JsonProperty( "Inetref" )
	private String inetReference;

	@JsonProperty( "Collectionref" )
	private String collectionReference;

	@JsonProperty( "HomePage" )
	private String homePage;

	@JsonProperty( "ReleaseDate" )
	@JsonSerialize( using = DateTimeSerializer.class )
	private DateTime releaseDate;

	@JsonProperty( "UserRating" )
	private float userRating;

	@JsonProperty( "Length" )
	private int length;

	@JsonProperty( "Language" )
	private String language;

	@JsonProperty( "Countries" )
	private List<String> countries;

	@JsonProperty( "Popularity" )
	private int popularity;

	@JsonProperty( "Budget" )
	private int budget;

	@JsonProperty( "Revenue" )
	private int revenue;

	@JsonProperty( "IMDB" )
	private String imdb;

	@JsonProperty( "TMSRef" )
	private String tmsReference;

	@JsonProperty( "Artwork" )
	private List<ArtworkItem> artworks;

	public VideoLookup() { }

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
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear( int year ) {
		this.year = year;
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
	 * @return the inetReference
	 */
	public String getInetReference() {
		return inetReference;
	}

	/**
	 * @param inetReference the inetReference to set
	 */
	public void setInetReference( String inetReference ) {
		this.inetReference = inetReference;
	}

	/**
	 * @return the collectionReference
	 */
	public String getCollectionReference() {
		return collectionReference;
	}

	/**
	 * @param collectionReference the collectionReference to set
	 */
	public void setCollectionReference( String collectionReference ) {
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
	public DateTime getReleaseDate() {
		return releaseDate;
	}

	/**
	 * @param releaseDate the releaseDate to set
	 */
	public void setReleaseDate( DateTime releaseDate ) {
		this.releaseDate = releaseDate;
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
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage( String language ) {
		this.language = language;
	}

	/**
	 * @return the countries
	 */
	public List<String> getCountries() {
		return countries;
	}

	/**
	 * @param countries the countries to set
	 */
	public void setCountries( List<String> countries ) {
		this.countries = countries;
	}

	/**
	 * @return the popularity
	 */
	public int getPopularity() {
		return popularity;
	}

	/**
	 * @param popularity the popularity to set
	 */
	public void setPopularity( int popularity ) {
		this.popularity = popularity;
	}

	/**
	 * @return the budget
	 */
	public int getBudget() {
		return budget;
	}

	/**
	 * @param budget the budget to set
	 */
	public void setBudget( int budget ) {
		this.budget = budget;
	}

	/**
	 * @return the revenue
	 */
	public int getRevenue() {
		return revenue;
	}

	/**
	 * @param revenue the revenue to set
	 */
	public void setRevenue( int revenue ) {
		this.revenue = revenue;
	}

	/**
	 * @return the imdb
	 */
	public String getImdb() {
		return imdb;
	}

	/**
	 * @param imdb the imdb to set
	 */
	public void setImdb( String imdb ) {
		this.imdb = imdb;
	}

	/**
	 * @return the tmsReference
	 */
	public String getTmsReference() {
		return tmsReference;
	}

	/**
	 * @param tmsReference the tmsReference to set
	 */
	public void setTmsReference( String tmsReference ) {
		this.tmsReference = tmsReference;
	}

	/**
	 * @return the artworks
	 */
	public List<ArtworkItem> getArtworks() {
		return artworks;
	}

	/**
	 * @param artworks the artworks to set
	 */
	public void setArtworks( List<ArtworkItem> artworks ) {
		this.artworks = artworks;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "VideoLookup [" );
		
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
		
		builder.append( "season=" );
		builder.append( season );
		builder.append( ", episode=" );
		builder.append( episode );
		builder.append( ", year=" );
		builder.append( year );
		builder.append( ", " );
		
		if( tagline != null ) {
			builder.append( "tagline=" );
			builder.append( tagline );
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
		
		if( inetReference != null ) {
			builder.append( "inetReference=" );
			builder.append( inetReference );
			builder.append( ", " );
		}
		
		if( collectionReference != null ) {
			builder.append( "collectionReference=" );
			builder.append( collectionReference );
			builder.append( ", " );
		}
		
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
		
		builder.append( "userRating=" );
		builder.append( userRating );
		builder.append( ", length=" );
		builder.append( length );
		builder.append( ", " );
		
		if( language != null ) {
			builder.append( "language=" );
			builder.append( language );
			builder.append( ", " );
		}
		
		if( countries != null ) {
			builder.append( "countries=" );
			builder.append( countries );
			builder.append( ", " );
		}
		
		builder.append( "popularity=" );
		builder.append( popularity );
		builder.append( ", budget=" );
		builder.append( budget );
		builder.append( ", revenue=" );
		builder.append( revenue );
		builder.append( ", " );
		
		if( imdb != null ) {
			builder.append( "imdb=" );
			builder.append( imdb );
			builder.append( ", " );
		}
		
		if( tmsReference != null ) {
			builder.append( "tmsReference=" );
			builder.append( tmsReference );
			builder.append( ", " );
		}
		
		if( artworks != null ) {
			builder.append( "artworks=" );
			builder.append( artworks );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
