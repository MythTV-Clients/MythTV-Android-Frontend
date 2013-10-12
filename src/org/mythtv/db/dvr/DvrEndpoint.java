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
 * 
 */
package org.mythtv.db.dvr;

/**
 * @author Daniel Frey
 *
 */
public enum DvrEndpoint {
	ADD_RECORD_SCHEDULE( "AddRecordSchedule" ),
	DISABLE_RECORD_SCHEDULE( "DisableRecordSchedule" ),
	ENABLE_RECORD_SCHEDULE( "EnableRecordSchedule" ),
	GET_CONFLICT_LIST( "GetConflictList" ),
	GET_ENCODER_LIST( "GetEncoderList" ),
	GET_EXPIRING_LIST( "GetExpiringList"),
	GET_FILTERED_RECORDED_LIST( "GetFilteredRecordedList" ),
	GET_RECORD_SCHEDULE( "GetRecordSchedule" ),
	GET_RECORD_SCHEDULE_LIST( "GetRecordScheduleList" ),
	GET_RECORDED( "GetRecorded" ),
	GET_RECORDED_LIST( "GetRecordedList" ),
	GET_UPCOMING_LIST( "GetUpcomingList" ),
	REMOVE_RECORD_SCHEDULE( "RemoveRecordSchedule" ),
	REMOVE_RECORDED( "RemoveRecorded" );
	
	private String endpoint;
	
	private DvrEndpoint( String endpoint ) {
		this.endpoint = endpoint;
	}
	
	public String getEndpoint() {
		return endpoint;
	}
	
}
