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
package org.mythtv.services.api.dvr;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class RecRule {

	@JsonProperty( "Id" )
	private int id;

	@JsonProperty( "ParentId" )
	private int parentId;

	@JsonProperty( "Inactive" )
	private boolean inactive;

	@JsonProperty( "Title" )
	private String title;

	@JsonProperty( "SubTitle" )
	private String subTitle;

	@JsonProperty( "Description" )
	private String description;

	@JsonProperty( "Season" )
	private int season;

	@JsonProperty( "Episode" )
	private int episode;

	@JsonProperty( "Category" )
	private String category;

	@JsonProperty( "StartTime" )
	private Date startTime;

	@JsonProperty( "EndTime" )
	private Date endTime;

	@JsonProperty( "SeriesId" )
	private String seriesId;

	@JsonProperty( "ProgramId" )
	private String programId;

	@JsonProperty( "Inetref" )
	private String inetref;

	@JsonProperty( "ChanId" )
	private int chanId;

	@JsonProperty( "CallSign" )
	private String callSign;

	@JsonProperty( "Day" )
	private int day;

	@JsonProperty( "Time" )
	private Date time;

	@JsonProperty( "FindId" )
	private int findId;

	@JsonProperty( "Type" )
	private String type;

	@JsonProperty( "SearchType" )
	private String searchType;

	@JsonProperty( "RecPriority" )
	private int recPriority;

	@JsonProperty( "PreferredInput" )
	private int preferredInput;

	@JsonProperty( "StartOffset" )
	private int startOffset;

	@JsonProperty( "EndOffset" )
	private int endOffset;

	@JsonProperty( "DupMethod" )
	private String dupMethod;

	@JsonProperty( "DupIn" )
	private String dupIn;

	@JsonProperty( "Filter" )
	private int filter;

	@JsonProperty( "RecProfile" )
	private String recProfile;

	@JsonProperty( "RecGroup" )
	private String recGroup;

	@JsonProperty( "StorageGroup" )
	private String storageGroup;

	@JsonProperty( "PlayGroup" )
	private String playGroup;

	@JsonProperty( "AutoExpire" )
	private boolean autoExpire;

	@JsonProperty( "MaxEpisodes" )
	private int maxEpisodes;

	@JsonProperty( "MaxNewest" )
	private boolean maxNewest;

	@JsonProperty( "AutoCommflag" )
	private boolean autoCommflag;

	@JsonProperty( "AutoTranscode" )
	private boolean autoTranscode;

	@JsonProperty( "AutoMetaLookup" )
	private boolean autoMetaLookup;

	@JsonProperty( "AutoUserJob1" )
	private boolean autoUserJob1;

	@JsonProperty( "AutoUserJob2" )
	private boolean autoUserJob2;

	@JsonProperty( "AutoUserJob3" )
	private boolean autoUserJob3;

	@JsonProperty( "AutoUserJob4" )
	private boolean autoUserJob4;

	@JsonProperty( "Transcoder" )
	private int transcoder;

	@JsonProperty( "NextRecording" )
	private Date nextRecording;

	@JsonProperty( "LastRecorded" )
	private Date lastRecorded;

	@JsonProperty( "LastDeleted" )
	private Date lastDeleted;

	@JsonProperty( "AverageDelay" )
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
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime( Date startTime ) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime( Date endTime ) {
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
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime( Date time ) {
		this.time = time;
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
	public Date getNextRecording() {
		return nextRecording;
	}

	/**
	 * @param nextRecording the nextRecording to set
	 */
	public void setNextRecording( Date nextRecording ) {
		this.nextRecording = nextRecording;
	}

	/**
	 * @return the lastRecorded
	 */
	public Date getLastRecorded() {
		return lastRecorded;
	}

	/**
	 * @param lastRecorded the lastRecorded to set
	 */
	public void setLastRecorded( Date lastRecorded ) {
		this.lastRecorded = lastRecorded;
	}

	/**
	 * @return the lastDeleted
	 */
	public Date getLastDeleted() {
		return lastDeleted;
	}

	/**
	 * @param lastDeleted the lastDeleted to set
	 */
	public void setLastDeleted( Date lastDeleted ) {
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
