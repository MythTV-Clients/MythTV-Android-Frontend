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

import org.mythtv.services.api.dvr.DvrOperations;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.ProgramList;
import org.mythtv.services.api.dvr.RecRule;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class DvrTemplate extends AbstractDvrOperations implements DvrOperations {

	private static final String TAG = DvrTemplate.class.getSimpleName();
	
	private final RestTemplate restTemplate;
	
	/**
	 * @param restTemplate
	 * @param apiUrlBase
	 */
	public DvrTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		Log.v( TAG, "initialize : enter" );
		
		this.restTemplate = restTemplate;

		Log.v( TAG, "initialize : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#addRecordingSchedule(int, java.util.Date, int, boolean, int, int, java.lang.String, int, java.lang.String, java.lang.String, int, int, int, int, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, int, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, int)
	 */
	@Override
	public int addRecordingSchedule( int channelId, Date startTime, int parentId, boolean interactive, int season, int episode, String inetRef, int findId, String type, String searchType, int recordingPriority, int perferredInput, int startOffset, int endOffset, String duplicateMethod, String duplicateIn, int filter, String recordingProfile, String recordingGroup, String storageGroup, String playGroup, boolean autoExpire, int maxEpisodes, boolean maxNewest, boolean autoCommercialFlag, boolean autoTranscode, boolean autoMetadataLookup, boolean autoUserJob1, boolean autoUserJob2, boolean autoUserJob3, boolean autoUserJob4, int transcoder ) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#disableRecordingSchedule(int)
	 */
	@Override
	public boolean disableRecordingSchedule( int recordingId ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#enableRecordingSchedule(int)
	 */
	@Override
	public boolean enableRecordingSchedule( int recordingId ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getConflictList(int, int)
	 */
	@Override
	public List<Program> getConflictList( int startIndex, int count ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getExpiringList(int, int)
	 */
	@Override
	public List<Program> getExpiringList( int startIndex, int count ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getFiltererRecordedList(boolean, int, int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Program> getFiltererRecordedList( boolean descending, int startIndex, int count, String titleRegEx, String recordingGroup, String storageGroup ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getRecordSchedule(int)
	 */
	@Override
	public RecRule getRecordSchedule( int recordId ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getRecordScheduleList(int, int)
	 */
	@Override
	public List<RecRule> getRecordScheduleList( int startIndex, int count ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getRecorded(int, java.util.Date)
	 */
	@Override
	public Program getRecorded( int channelId, Date startTime ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getRecordedList()
	 */
	@Override
	public List<Program> getRecordedList() {
		Log.v( TAG, "getRecordedList : enter" );

		ResponseEntity<ProgramList> responseEntity = restTemplate.exchange( buildUri( "GetRecordedList" ), HttpMethod.GET, getRequestEntity(), ProgramList.class );
		ProgramList programList = responseEntity.getBody();
		
		Log.v( TAG, "getRecordedList : exit" );
		return programList.getPrograms().getPrograms();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getRecordedList(int, int, boolean)
	 */
	@Override
	public List<Program> getRecordedList( int startIndex, int count, boolean descending ) {
		Log.v( TAG, "getRecordedList( int, int, boolean ) : enter" );

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		
		if( startIndex > 0 ) {
			parameters.add( "StartIndex", "" + startIndex );
		}
		
		if( count > 0 ) {
			parameters.add( "Count", "" + count );
		}

		if( descending ) {
			parameters.add( "Descending", "true" );
		}

		ResponseEntity<ProgramList> responseEntity = restTemplate.exchange( buildUri( "GetRecordedList", parameters ), HttpMethod.GET, getRequestEntity(), ProgramList.class );
		ProgramList programList = responseEntity.getBody();
		
		Log.v( TAG, "getRecordedList( int, int, boolean ) : exit" );
		return programList.getPrograms().getPrograms();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getUpcomingList(int, int, boolean)
	 */
	@Override
	public List<Program> getUpcomingList( int startIndex, int count, boolean showAll ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#removeRecordingSchedule(int)
	 */
	@Override
	public boolean removeRecordingSchedule( int recordingId ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#removeRecorded(int, java.util.Date)
	 */
	@Override
	public boolean removeRecorded( int channelId, Date startTime ) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
