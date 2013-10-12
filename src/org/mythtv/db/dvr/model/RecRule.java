/**
 * This file is part of MythTV Android Frontend
 *
 * MythTV Android Frontend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MythTV Android Frontend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MythTV Android Frontend.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This software can be found at <https://github.com/MythTV-Clients/MythTV-Android-Frontend/>
 */
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

/**
 * @author Daniel Frey
 *
 */
public class RecRule implements Serializable {

	private static final long serialVersionUID = -2245536436650006298L;

	private int id;
	private int parentId;
	private boolean inactive;
	private String title;
	private String subTitle;
	private String description;
	private int season;
	private int episode;
	private String category;
	private DateTime startTime;
	private DateTime endTime;
	private String seriesId;
	private String programId;
	private String inetref;
	private int chanId;
	private String callSign;
	private int day;
    private int findDay;
	private String time;
    private DateTime findTime;
	private int findId;
	private String type;
	private String searchType;
	private int recPriority;
	private int preferredInput;
	private int startOffset;
	private int endOffset;
	private String dupMethod;
	private String dupIn;
	private int filter;
	private String recProfile;
	private String recGroup;
	private String storageGroup;
	private String playGroup;
	private boolean autoExpire;
	private int maxEpisodes;
	private boolean maxNewest;
	private boolean autoCommflag;
	private boolean autoTranscode;
	private boolean autoMetaLookup;
	private boolean autoUserJob1;
	private boolean autoUserJob2;
	private boolean autoUserJob3;
	private boolean autoUserJob4;
	private int transcoder;
	private DateTime nextRecording;
	private DateTime lastRecorded;
	private DateTime lastDeleted;
	private int averageDelay;

	public RecRule() { }

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
	 * @return the parentId
	 */
	public int getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId( int parentId ) {
		this.parentId = parentId;
	}

	/**
	 * @return the inactive
	 */
	public boolean isInactive() {
		return inactive;
	}

	/**
	 * @param inactive the inactive to set
	 */
	public void setInactive( boolean inactive ) {
		this.inactive = inactive;
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
	 * @return the chanId
	 */
	public int getChanId() {
		return chanId;
	}

	/**
	 * @param chanId the chanId to set
	 */
	public void setChanId( int chanId ) {
		this.chanId = chanId;
	}

	/**
	 * @return the callSign
	 */
	public String getCallSign() {
		return callSign;
	}

	/**
	 * @param callSign the callSign to set
	 */
	public void setCallSign( String callSign ) {
		this.callSign = callSign;
	}

	/**
	 * @return the day
	 */
	public int getDay() {
		return day;
	}

	/**
	 * @param day the day to set
	 */
	public void setDay( int day ) {
		this.day = day;
	}

	/**
	 * @return the findDay
	 */
	public int getFindDay() {
		return findDay;
	}

	/**
	 * @param findDay the findDay to set
	 */
	public void setFindDay( int findDay ) {
		this.findDay = findDay;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime( String time ) {
		this.time = time;
	}

	/**
	 * @return the findTime
	 */
	public DateTime getFindTime() {
		return findTime;
	}

	/**
	 * @param findTime the findTime to set
	 */
	public void setFindTime( DateTime findTime ) {
		this.findTime = findTime;
	}

	/**
	 * @return the findId
	 */
	public int getFindId() {
		return findId;
	}

	/**
	 * @param findId the findId to set
	 */
	public void setFindId( int findId ) {
		this.findId = findId;
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

	/**
	 * @return the searchType
	 */
	public String getSearchType() {
		return searchType;
	}

	/**
	 * @param searchType the searchType to set
	 */
	public void setSearchType( String searchType ) {
		this.searchType = searchType;
	}

	/**
	 * @return the recPriority
	 */
	public int getRecPriority() {
		return recPriority;
	}

	/**
	 * @param recPriority the recPriority to set
	 */
	public void setRecPriority( int recPriority ) {
		this.recPriority = recPriority;
	}

	/**
	 * @return the preferredInput
	 */
	public int getPreferredInput() {
		return preferredInput;
	}

	/**
	 * @param preferredInput the preferredInput to set
	 */
	public void setPreferredInput( int preferredInput ) {
		this.preferredInput = preferredInput;
	}

	/**
	 * @return the startOffset
	 */
	public int getStartOffset() {
		return startOffset;
	}

	/**
	 * @param startOffset the startOffset to set
	 */
	public void setStartOffset( int startOffset ) {
		this.startOffset = startOffset;
	}

	/**
	 * @return the endOffset
	 */
	public int getEndOffset() {
		return endOffset;
	}

	/**
	 * @param endOffset the endOffset to set
	 */
	public void setEndOffset( int endOffset ) {
		this.endOffset = endOffset;
	}

	/**
	 * @return the dupMethod
	 */
	public String getDupMethod() {
		return dupMethod;
	}

	/**
	 * @param dupMethod the dupMethod to set
	 */
	public void setDupMethod( String dupMethod ) {
		this.dupMethod = dupMethod;
	}

	/**
	 * @return the dupIn
	 */
	public String getDupIn() {
		return dupIn;
	}

	/**
	 * @param dupIn the dupIn to set
	 */
	public void setDupIn( String dupIn ) {
		this.dupIn = dupIn;
	}

	/**
	 * @return the filter
	 */
	public int getFilter() {
		return filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter( int filter ) {
		this.filter = filter;
	}

	/**
	 * @return the recProfile
	 */
	public String getRecProfile() {
		return recProfile;
	}

	/**
	 * @param recProfile the recProfile to set
	 */
	public void setRecProfile( String recProfile ) {
		this.recProfile = recProfile;
	}

	/**
	 * @return the recGroup
	 */
	public String getRecGroup() {
		return recGroup;
	}

	/**
	 * @param recGroup the recGroup to set
	 */
	public void setRecGroup( String recGroup ) {
		this.recGroup = recGroup;
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
	 * @return the playGroup
	 */
	public String getPlayGroup() {
		return playGroup;
	}

	/**
	 * @param playGroup the playGroup to set
	 */
	public void setPlayGroup( String playGroup ) {
		this.playGroup = playGroup;
	}

	/**
	 * @return the autoExpire
	 */
	public boolean isAutoExpire() {
		return autoExpire;
	}

	/**
	 * @param autoExpire the autoExpire to set
	 */
	public void setAutoExpire( boolean autoExpire ) {
		this.autoExpire = autoExpire;
	}

	/**
	 * @return the maxEpisodes
	 */
	public int getMaxEpisodes() {
		return maxEpisodes;
	}

	/**
	 * @param maxEpisodes the maxEpisodes to set
	 */
	public void setMaxEpisodes( int maxEpisodes ) {
		this.maxEpisodes = maxEpisodes;
	}

	/**
	 * @return the maxNewest
	 */
	public boolean isMaxNewest() {
		return maxNewest;
	}

	/**
	 * @param maxNewest the maxNewest to set
	 */
	public void setMaxNewest( boolean maxNewest ) {
		this.maxNewest = maxNewest;
	}

	/**
	 * @return the autoCommflag
	 */
	public boolean isAutoCommflag() {
		return autoCommflag;
	}

	/**
	 * @param autoCommflag the autoCommflag to set
	 */
	public void setAutoCommflag( boolean autoCommflag ) {
		this.autoCommflag = autoCommflag;
	}

	/**
	 * @return the autoTranscode
	 */
	public boolean isAutoTranscode() {
		return autoTranscode;
	}

	/**
	 * @param autoTranscode the autoTranscode to set
	 */
	public void setAutoTranscode( boolean autoTranscode ) {
		this.autoTranscode = autoTranscode;
	}

	/**
	 * @return the autoMetaLookup
	 */
	public boolean isAutoMetaLookup() {
		return autoMetaLookup;
	}

	/**
	 * @param autoMetaLookup the autoMetaLookup to set
	 */
	public void setAutoMetaLookup( boolean autoMetaLookup ) {
		this.autoMetaLookup = autoMetaLookup;
	}

	/**
	 * @return the autoUserJob1
	 */
	public boolean isAutoUserJob1() {
		return autoUserJob1;
	}

	/**
	 * @param autoUserJob1 the autoUserJob1 to set
	 */
	public void setAutoUserJob1( boolean autoUserJob1 ) {
		this.autoUserJob1 = autoUserJob1;
	}

	/**
	 * @return the autoUserJob2
	 */
	public boolean isAutoUserJob2() {
		return autoUserJob2;
	}

	/**
	 * @param autoUserJob2 the autoUserJob2 to set
	 */
	public void setAutoUserJob2( boolean autoUserJob2 ) {
		this.autoUserJob2 = autoUserJob2;
	}

	/**
	 * @return the autoUserJob3
	 */
	public boolean isAutoUserJob3() {
		return autoUserJob3;
	}

	/**
	 * @param autoUserJob3 the autoUserJob3 to set
	 */
	public void setAutoUserJob3( boolean autoUserJob3 ) {
		this.autoUserJob3 = autoUserJob3;
	}

	/**
	 * @return the autoUserJob4
	 */
	public boolean isAutoUserJob4() {
		return autoUserJob4;
	}

	/**
	 * @param autoUserJob4 the autoUserJob4 to set
	 */
	public void setAutoUserJob4( boolean autoUserJob4 ) {
		this.autoUserJob4 = autoUserJob4;
	}

	/**
	 * @return the transcoder
	 */
	public int getTranscoder() {
		return transcoder;
	}

	/**
	 * @param transcoder the transcoder to set
	 */
	public void setTranscoder( int transcoder ) {
		this.transcoder = transcoder;
	}

	/**
	 * @return the nextRecording
	 */
	public DateTime getNextRecording() {
		return nextRecording;
	}

	/**
	 * @param nextRecording the nextRecording to set
	 */
	public void setNextRecording( DateTime nextRecording ) {
		this.nextRecording = nextRecording;
	}

	/**
	 * @return the lastRecorded
	 */
	public DateTime getLastRecorded() {
		return lastRecorded;
	}

	/**
	 * @param lastRecorded the lastRecorded to set
	 */
	public void setLastRecorded( DateTime lastRecorded ) {
		this.lastRecorded = lastRecorded;
	}

	/**
	 * @return the lastDeleted
	 */
	public DateTime getLastDeleted() {
		return lastDeleted;
	}

	/**
	 * @param lastDeleted the lastDeleted to set
	 */
	public void setLastDeleted( DateTime lastDeleted ) {
		this.lastDeleted = lastDeleted;
	}

	/**
	 * @return the averageDelay
	 */
	public int getAverageDelay() {
		return averageDelay;
	}

	/**
	 * @param averageDelay the averageDelay to set
	 */
	public void setAverageDelay( int averageDelay ) {
		this.averageDelay = averageDelay;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "RecRule [id=" );
		builder.append( id );
		builder.append( ", parentId=" );
		builder.append( parentId );
		builder.append( ", inactive=" );
		builder.append( inactive );
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
		
		if( description != null ) {
			builder.append( "description=" );
			builder.append( description );
			builder.append( ", " );
		}
		
		builder.append( "season=" );
		builder.append( season );
		builder.append( ", episode=" );
		builder.append( episode );
		builder.append( ", " );
		
		if( category != null ) {
			builder.append( "category=" );
			builder.append( category );
			builder.append( ", " );
		}
		
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
		
		if( inetref != null ) {
			builder.append( "inetref=" );
			builder.append( inetref );
			builder.append( ", " );
		}
		
		builder.append( "chanId=" );
		builder.append( chanId );
		builder.append( ", " );
		
		if( callSign != null ) {
			builder.append( "callSign=" );
			builder.append( callSign );
			builder.append( ", " );
		}
		
		builder.append( "day=" );
		builder.append( day );
		builder.append( ", " );
		
		if( time != null ) {
			builder.append( "time=" );
			builder.append( time );
			builder.append( ", " );
		}
		
		builder.append( "findId=" );
		builder.append( findId );
		builder.append( ", " );
		
		if( type != null ) {
			builder.append( "type=" );
			builder.append( type );
			builder.append( ", " );
		}
		
		if( searchType != null ) {
			builder.append( "searchType=" );
			builder.append( searchType );
			builder.append( ", " );
		}
		
		builder.append( "recPriority=" );
		builder.append( recPriority );
		builder.append( ", preferredInput=" );
		builder.append( preferredInput );
		builder.append( ", startOffset=" );
		builder.append( startOffset );
		builder.append( ", endOffset=" );
		builder.append( endOffset );
		builder.append( ", " );
		
		if( dupMethod != null ) {
			builder.append( "dupMethod=" );
			builder.append( dupMethod );
			builder.append( ", " );
		}
		
		if( dupIn != null ) {
			builder.append( "dupIn=" );
			builder.append( dupIn );
			builder.append( ", " );
		}
		
		builder.append( "filter=" );
		builder.append( filter );
		builder.append( ", " );
		
		if( recProfile != null ) {
			builder.append( "recProfile=" );
			builder.append( recProfile );
			builder.append( ", " );
		}
		
		if( recGroup != null ) {
			builder.append( "recGroup=" );
			builder.append( recGroup );
			builder.append( ", " );
		}
		
		if( storageGroup != null ) {
			builder.append( "storageGroup=" );
			builder.append( storageGroup );
			builder.append( ", " );
		}
		
		if( playGroup != null ) {
			builder.append( "playGroup=" );
			builder.append( playGroup );
			builder.append( ", " );
		}
		
		builder.append( "autoExpire=" );
		builder.append( autoExpire );
		builder.append( ", maxEpisodes=" );
		builder.append( maxEpisodes );
		builder.append( ", maxNewest=" );
		builder.append( maxNewest );
		builder.append( ", autoCommflag=" );
		builder.append( autoCommflag );
		builder.append( ", autoTranscode=" );
		builder.append( autoTranscode );
		builder.append( ", autoMetaLookup=" );
		builder.append( autoMetaLookup );
		builder.append( ", autoUserJob1=" );
		builder.append( autoUserJob1 );
		builder.append( ", autoUserJob2=" );
		builder.append( autoUserJob2 );
		builder.append( ", autoUserJob3=" );
		builder.append( autoUserJob3 );
		builder.append( ", autoUserJob4=" );
		builder.append( autoUserJob4 );
		builder.append( ", transcoder=" );
		builder.append( transcoder );
		builder.append( ", " );
		
		if( nextRecording != null ) {
			builder.append( "nextRecording=" );
			builder.append( nextRecording );
			builder.append( ", " );
		}
		
		if( lastRecorded != null ) {
			builder.append( "lastRecorded=" );
			builder.append( lastRecorded );
			builder.append( ", " );
		}
		
		if( lastDeleted != null ) {
			builder.append( "lastDeleted=" );
			builder.append( lastDeleted );
			builder.append( ", " );
		}
		
		builder.append( "averageDelay=" );
		builder.append( averageDelay );
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
