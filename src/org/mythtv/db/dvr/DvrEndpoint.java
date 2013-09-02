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
