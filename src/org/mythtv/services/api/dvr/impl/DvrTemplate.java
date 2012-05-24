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
package org.mythtv.services.api.dvr.impl;

import java.util.Date;
import java.util.List;

import org.mythtv.services.api.Bool;
import org.mythtv.services.api.Int;
import org.mythtv.services.api.dvr.DvrOperations;
import org.mythtv.services.api.dvr.Encoder;
import org.mythtv.services.api.dvr.EncoderList;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.ProgramList;
import org.mythtv.services.api.dvr.RecRule;
import org.mythtv.services.api.dvr.RecRuleList;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class DvrTemplate extends AbstractDvrOperations implements DvrOperations {

	private final RestTemplate restTemplate;
	
	/**
	 * @param restTemplate
	 * @param apiUrlBase
	 */
	public DvrTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		
		this.restTemplate = restTemplate;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#addRecordingSchedule(int, java.util.Date, int, boolean, int, int, java.lang.String, int, java.lang.String, java.lang.String, int, int, int, int, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, int, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, int)
	 */
	@Override
	public int addRecordingSchedule( int channelId, Date startTime, int parentId, boolean interactive, int season, int episode, String inetRef, int findId, String type, String searchType, int recordingPriority, int perferredInput, int startOffset, int endOffset, String duplicateMethod, String duplicateIn, int filter, String recordingProfile, String recordingGroup, String storageGroup, String playGroup, boolean autoExpire, int maxEpisodes, boolean maxNewest, boolean autoCommercialFlag, boolean autoTranscode, boolean autoMetadataLookup, boolean autoUserJob1, boolean autoUserJob2, boolean autoUserJob3, boolean autoUserJob4, int transcoder ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "ChanId", "" + channelId );
		parameters.add( "StartTime", sdf.format(  startTime ) );
		parameters.add( "ParentId", "" + parentId );
		parameters.add( "Inactive", Boolean.toString( interactive ) );
		parameters.add( "Season", "" + season );
		parameters.add( "Episode", "" + episode );
		parameters.add( "Inetref", inetRef );
		parameters.add( "FindId", "" + findId );
		parameters.add( "Type", type );
		parameters.add( "SearchType", searchType );
		parameters.add( "RecPriority", "" + recordingPriority );
		parameters.add( "PreferredInput", "" + perferredInput );
		parameters.add( "StartOffset", "" + startOffset );
		parameters.add( "EndOffset", "" + endOffset );
		parameters.add( "DupMethod", duplicateMethod );
		parameters.add( "DupIn", duplicateIn );
		parameters.add( "Filter", "" + filter );
		parameters.add( "RecProfile", recordingProfile );
		parameters.add( "RecGroup", recordingGroup );
		parameters.add( "StorageGroup", storageGroup );
		parameters.add( "PlayGroup", playGroup );
		parameters.add( "AutoExpire", Boolean.toString( autoExpire ) );
		parameters.add( "MaxEpisodes", "" + maxEpisodes );
		parameters.add( "MaxNewest", Boolean.toString( maxNewest ) );
		parameters.add( "AutoCommflag", Boolean.toString( autoCommercialFlag ) );
		parameters.add( "AutoTranscode", Boolean.toString( autoTranscode ) );
		parameters.add( "AutoMetaLookup", Boolean.toString( autoMetadataLookup ) );
		parameters.add( "AutoUserJob1", Boolean.toString( autoUserJob1 ) );
		parameters.add( "AutoUserJob2", Boolean.toString( autoUserJob2 ) );
		parameters.add( "AutoUserJob3", Boolean.toString( autoUserJob3 ) );
		parameters.add( "AutoUserJob4", Boolean.toString( autoUserJob4 ) );
		parameters.add( "Transcoder", "" + transcoder );

		ResponseEntity<Int> responseEntity = restTemplate.exchange( buildUri( "DisableRecordSchedule", parameters ), HttpMethod.GET, getRequestEntity(), Int.class );
		Int i = responseEntity.getBody();

		return i.getInteger();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#disableRecordingSchedule(int)
	 */
	@Override
	public boolean disableRecordingSchedule( int recordingId ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "RecordId", "" + recordingId );
		
		ResponseEntity<Bool> responseEntity = restTemplate.exchange( buildUri( "DisableRecordSchedule", parameters ), HttpMethod.GET, getRequestEntity(), Bool.class );
		Bool bool = responseEntity.getBody();
		
		return bool.getBool();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#enableRecordingSchedule(int)
	 */
	@Override
	public boolean enableRecordingSchedule( int recordingId ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "RecordId", "" + recordingId );
		
		ResponseEntity<Bool> responseEntity = restTemplate.exchange( buildUri( "EnableRecordSchedule", parameters ), HttpMethod.GET, getRequestEntity(), Bool.class );
		Bool bool = responseEntity.getBody();
		
		return bool.getBool();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getConflictList(int, int)
	 */
	@Override
	public List<Program> getConflictList( int startIndex, int count ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		
		if( startIndex > 0 ) {
			parameters.add( "StartIndex", "" + startIndex );
		}
		
		if( count > 0 ) {
			parameters.add( "Count", "" + count );
		}

		ResponseEntity<ProgramList> responseEntity = restTemplate.exchange( buildUri( "GetConflictList", parameters ), HttpMethod.GET, getRequestEntity(), ProgramList.class );
		ProgramList programList = responseEntity.getBody();
		
		return programList.getPrograms().getPrograms();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getEncoderList()
	 */
	@Override
	public List<Encoder> getEncoderList() {

		ResponseEntity<EncoderList> responseEntity = restTemplate.exchange( buildUri( "GetConflictList" ), HttpMethod.GET, getRequestEntity(), EncoderList.class );
		EncoderList encoderList = responseEntity.getBody();
		
		return encoderList.getEncoders().getEncoders();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getExpiringList(int, int)
	 */
	@Override
	public List<Program> getExpiringList( int startIndex, int count ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		
		if( startIndex > 0 ) {
			parameters.add( "StartIndex", "" + startIndex );
		}
		
		if( count > 0 ) {
			parameters.add( "Count", "" + count );
		}

		ResponseEntity<ProgramList> responseEntity = restTemplate.exchange( buildUri( "GetExpiringList", parameters ), HttpMethod.GET, getRequestEntity(), ProgramList.class );
		ProgramList programList = responseEntity.getBody();
		
		return programList.getPrograms().getPrograms();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getFiltererRecordedList(boolean, int, int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Program> getFiltererRecordedList( boolean descending, int startIndex, int count, String titleRegEx, String recordingGroup, String storageGroup ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Descending", Boolean.toString( descending ) );
		
		if( startIndex > 0 ) {
			parameters.add( "StartIndex", "" + startIndex );
		}
		
		if( count > 0 ) {
			parameters.add( "Count", "" + count );
		}

		if( null != titleRegEx && !"".equals( titleRegEx ) ) {
			parameters.add( "TitleRegEx", titleRegEx );
		}

		if( null != recordingGroup && !"".equals( recordingGroup ) ) {
			parameters.add( "RecGroup", recordingGroup );
		}

		if( null != storageGroup && !"".equals( storageGroup ) ) {
			parameters.add( "StorageGroup", storageGroup );
		}

		ResponseEntity<ProgramList> responseEntity = restTemplate.exchange( buildUri( "GetFilteredRecordedList", parameters ), HttpMethod.GET, getRequestEntity(), ProgramList.class );
		ProgramList programList = responseEntity.getBody();
		
		return programList.getPrograms().getPrograms();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getRecordSchedule(int)
	 */
	@Override
	public RecRule getRecordSchedule( int recordId ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "RecordId", "" + recordId );
		
		ResponseEntity<RecRule> responseEntity = restTemplate.exchange( buildUri( "GetRecordSchedule", parameters ), HttpMethod.GET, getRequestEntity(), RecRule.class );
		RecRule recRule = responseEntity.getBody();
		
		return recRule;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getRecordScheduleList(int, int)
	 */
	@Override
	public List<RecRule> getRecordScheduleList( int startIndex, int count ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		
		if( startIndex > 0 ) {
			parameters.add( "StartIndex", "" + startIndex );
		}
		
		if( count > 0 ) {
			parameters.add( "Count", "" + count );
		}

		ResponseEntity<RecRuleList> responseEntity = restTemplate.exchange( buildUri( "GetRecordScheduleList", parameters ), HttpMethod.GET, getRequestEntity(), RecRuleList.class );
		RecRuleList recRuleList = responseEntity.getBody();
		
		return recRuleList.getRecRules().getRecRules();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getRecorded(int, java.util.Date)
	 */
	@Override
	public Program getRecorded( int channelId, Date startTime ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "ChanId", "" + channelId );
		parameters.add( "StartTime", sdf.format( startTime ) );

		ResponseEntity<Program> responseEntity = restTemplate.exchange( buildUri( "GetRecorded" ), HttpMethod.GET, getRequestEntity(), Program.class );
		Program program = responseEntity.getBody();
		
		return program;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getRecordedList()
	 */
	@Override
	public List<Program> getRecordedList() {

		ResponseEntity<ProgramList> responseEntity = restTemplate.exchange( buildUri( "GetRecordedList" ), HttpMethod.GET, getRequestEntity(), ProgramList.class );
		ProgramList programList = responseEntity.getBody();
		
		return programList.getPrograms().getPrograms();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getRecordedList(int, int, boolean)
	 */
	@Override
	public List<Program> getRecordedList( int startIndex, int count, boolean descending ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		
		if( startIndex > 0 ) {
			parameters.add( "StartIndex", "" + startIndex );
		}
		
		if( count > 0 ) {
			parameters.add( "Count", "" + count );
		}

		parameters.add( "Descending", Boolean.toString( descending ) );

		ResponseEntity<ProgramList> responseEntity = restTemplate.exchange( buildUri( "GetRecordedList", parameters ), HttpMethod.GET, getRequestEntity(), ProgramList.class );
		ProgramList programList = responseEntity.getBody();
		
		return programList.getPrograms().getPrograms();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getUpcomingList(int, int, boolean)
	 */
	@Override
	public List<Program> getUpcomingList( int startIndex, int count, boolean showAll ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		
		if( startIndex > 0 ) {
			parameters.add( "StartIndex", "" + startIndex );
		}
		
		if( count > 0 ) {
			parameters.add( "Count", "" + count );
		}

		parameters.add( "ShowAll", Boolean.toString( showAll ) );

		ResponseEntity<ProgramList> responseEntity = restTemplate.exchange( buildUri( "GetUpcomingList", parameters ), HttpMethod.GET, getRequestEntity(), ProgramList.class );
		ProgramList programList = responseEntity.getBody();
		
		return programList.getPrograms().getPrograms();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#removeRecordingSchedule(int)
	 */
	@Override
	public boolean removeRecordingSchedule( int recordingId ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "RecordId", "" + recordingId );
		
		ResponseEntity<Bool> responseEntity = restTemplate.exchange( buildUri( "RemoveRecordSchedule", parameters ), HttpMethod.GET, getRequestEntity(), Bool.class );
		Bool bool = responseEntity.getBody();
		
		return bool.getBool();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#removeRecorded(int, java.util.Date)
	 */
	@Override
	public boolean removeRecorded( int channelId, Date startTime ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "ChanId", "" + channelId );
		parameters.add( "StartTime", sdf.format( startTime ) );
		
		ResponseEntity<Bool> responseEntity = restTemplate.exchange( buildUri( "RemoveRecorded", parameters ), HttpMethod.GET, getRequestEntity(), Bool.class );
		Bool bool = responseEntity.getBody();
		
		return bool.getBool();
	}
	
}
