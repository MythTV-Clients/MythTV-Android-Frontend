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
package org.mythtv.db.dvr.model;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.mythtv.db.channel.model.ChannelInfo;
import org.mythtv.db.content.model.ArtworkInfos;
import org.mythtv.services.utils.ArticleCleaner;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Daniel Frey
 *
 */
public class Program implements Serializable, Comparable<Program> {

	private static final long serialVersionUID = 4144422404144517653L;

	private long id;	
	private DateTime startTime;
	private DateTime endTime;
	private String title;
	private String subTitle;
	private String category;
	private String categoryType;
	private boolean repeat;
	private int videoProps;
	private int audioProps;
	private int subProps;
	private String seriesId;
	private String programId;
	private double stars;
	private long fileSize;
	private DateTime lastModified;
	private int programFlags;
	private String hostname;
	private String filename;
	private DateTime airDate;
	private String description;
	private String inetref;
	private int season;
	private int episode;
	private ChannelInfo channelInfo;
	private Recording recording;
	private ArtworkInfos artwork;
	
	public Program() { }

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

	/**
	 * @return the startTime
	 */
	public DateTime getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime( DateTime startTime ) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public DateTime getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime( DateTime endTime ) {
		this.endTime = endTime;
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
	 * @return the categoryType
	 */
	public String getCategoryType() {
		return categoryType;
	}

	/**
	 * @param categoryType the categoryType to set
	 */
	public void setCategoryType( String categoryType ) {
		this.categoryType = categoryType;
	}

	/**
	 * @return the repeat
	 */
	public boolean isRepeat() {
		return repeat;
	}

	/**
	 * @param repeat the repeat to set
	 */
	public void setRepeat( boolean repeat ) {
		this.repeat = repeat;
	}

	/**
	 * @return the videoProps
	 */
	public int getVideoProps() {
		return videoProps;
	}

	/**
	 * @param videoProps the videoProps to set
	 */
	public void setVideoProps( int videoProps ) {
		this.videoProps = videoProps;
	}

	/**
	 * @return the audioProps
	 */
	public int getAudioProps() {
		return audioProps;
	}

	/**
	 * @param audioProps the audioProps to set
	 */
	public void setAudioProps( int audioProps ) {
		this.audioProps = audioProps;
	}

	/**
	 * @return the subProps
	 */
	public int getSubProps() {
		return subProps;
	}

	/**
	 * @param subProps the subProps to set
	 */
	public void setSubProps( int subProps ) {
		this.subProps = subProps;
	}

	/**
	 * @return the seriesId
	 */
	public String getSeriesId() {
		return seriesId;
	}

	/**
	 * @param seriesId the seriesId to set
	 */
	public void setSeriesId( String seriesId ) {
		this.seriesId = seriesId;
	}

	/**
	 * @return the programId
	 */
	public String getProgramId() {
		return programId;
	}

	/**
	 * @param programId the programId to set
	 */
	public void setProgramId( String programId ) {
		this.programId = programId;
	}

	/**
	 * @return the stars
	 */
	public double getStars() {
		return stars;
	}

	/**
	 * @param stars the stars to set
	 */
	public void setStars( double stars ) {
		this.stars = stars;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize( long fileSize ) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the lastModified
	 */
	public DateTime getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified( DateTime lastModified ) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the programFlags
	 */
	public int getProgramFlags() {
		return programFlags;
	}

	/**
	 * @param programFlags the programFlags to set
	 */
	public void setProgramFlags( int programFlags ) {
		this.programFlags = programFlags;
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
	 * @return the airDate
	 */
	public DateTime getAirDate() {
		return airDate;
	}

	/**
	 * @param airDate the airDate to set
	 */
	public void setAirDate( DateTime airDate ) {
		this.airDate = airDate;
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
	 * @return the channelInfo
	 */
	public ChannelInfo getChannelInfo() {
		return channelInfo;
	}

	/**
	 * @param channelInfo the channelInfo to set
	 */
	public void setChannelInfo( ChannelInfo channelInfo ) {
		this.channelInfo = channelInfo;
	}

	/**
	 * @return the recording
	 */
	public Recording getRecording() {
		return recording;
	}

	/**
	 * @param recording the recording to set
	 */
	public void setRecording( Recording recording ) {
		this.recording = recording;
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
	 * @return the artwork
	 */
	public ArtworkInfos getArtwork() {
		return artwork;
	}

	/**
	 * @param artwork the artwork to set
	 */
	public void setArtwork( ArtworkInfos artwork ) {
		this.artwork = artwork;
	}

	@JsonIgnore
	public long getDurationInMinutes() {
		
		if( null == startTime ) {
			return 0;
		}
		
		if( null == endTime ) {
			return 0;
		}
		
		return ( endTime.getMillis() / 60000 ) - ( startTime.getMillis() / 60000 );
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( title == null ) ? 0 : title.hashCode() );
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( Object obj ) {
		if( this == obj ) {
			return true;
		}
		if( obj == null ) {
			return false;
		}
		if( getClass() != obj.getClass() ) {
			return false;
		}
		Program other = (Program) obj;
		return compareTo(other) == 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "Program [" );
		
		builder.append( "id=" );
		builder.append( id );
		builder.append( ", " );
		
		if( startTime != null ) {
			builder.append( "startTime=" );
			builder.append( startTime );
			builder.append( ", " );
		}
		
		if( endTime != null ) {
			builder.append( "endTime=" );
			builder.append( endTime );
			builder.append( ", " );
		}
		
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
		
		if( category != null ) {
			builder.append( "category=" );
			builder.append( category );
			builder.append( ", " );
		}
		
		if( categoryType != null ) {
			builder.append( "categoryType=" );
			builder.append( categoryType );
			builder.append( ", " );
		}
		
		builder.append( "repeat=" );
		builder.append( repeat );
		builder.append( ", " );
		
		builder.append( "videoProps=" );
		builder.append( videoProps );
		builder.append( ", " );
		
		builder.append( "audioProps=" );
		builder.append( audioProps );
		builder.append( ", " );
		
		builder.append( "subProps=" );
		builder.append( subProps );
		builder.append( ", " );
		
		if( seriesId != null ) {
			builder.append( "seriesId=" );
			builder.append( seriesId );
			builder.append( ", " );
		}
		
		if( programId != null ) {
			builder.append( "programId=" );
			builder.append( programId );
			builder.append( ", " );
		}
		
		builder.append( "stars=" );
		builder.append( stars );
		builder.append( ", " );
		
		builder.append( "fileSize=" );
		builder.append( fileSize );
		builder.append( ", " );
		
		if( lastModified != null ) {
			builder.append( "lastModified=" );
			builder.append( lastModified );
			builder.append( ", " );
		}
		
		builder.append( "programFlags=" );
		builder.append( programFlags );
		builder.append( ", " );
		
		if( hostname != null ) {
			builder.append( "hostname=" );
			builder.append( hostname );
			builder.append( ", " );
		}
		
		if( filename != null ) {
			builder.append( "filename=" );
			builder.append( filename );
			builder.append( ", " );
		}
		
		if( airDate != null ) {
			builder.append( "airDate=" );
			builder.append( airDate );
			builder.append( ", " );
		}
		
		if( description != null ) {
			builder.append( "description=" );
			builder.append( description );
			builder.append( ", " );
		}
		
		if( inetref != null ) {
			builder.append( "inetref=" );
			builder.append( inetref );
			builder.append( ", " );
		}
		
		builder.append( "season=" );
		builder.append( season );
		builder.append( ", " );
		
		builder.append( "episode=" );
		builder.append( episode );
		builder.append( ", " );
		
		if( channelInfo != null ) {
			builder.append( "channelInfo=" );
			builder.append( channelInfo );
			builder.append( ", " );
		}
		
		if( recording != null ) {
			builder.append( "recording=" );
			builder.append( recording );
			builder.append( ", " );
		}
		
		if( artwork != null ) {
			builder.append( "artwork=" );
			builder.append( artwork );
		}

		builder.append( "]" );
		
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo( Program arg ) {
		if (arg == null) {
			return 1;
		}
		if (title == null && arg.getTitle() == null)
			return 0;
		if (title == null)
			return -1;
		if (arg.getTitle() == null)
			return 1;
		
		String sThisTitle = ArticleCleaner.clean( title );
		String sOtherTitle = ArticleCleaner.clean( arg.getTitle() );

		return sThisTitle.compareTo( sOtherTitle );

	}
	
}
