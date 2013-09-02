/**
 * 
 */
package org.mythtv.db.myth;

/**
 * @author Daniel Frey
 *
 */
public enum MythEndpoint {
    ADD_STORAGE_GROUP_DIR("AddStorageGroupDir"),
	GET_CONNECTION_INFO( "GetConnectionInfo" ),
	GET_HOST_NAME( "GetHostName" ),
	GET_HOSTS( "GetHosts" ),
	GET_KEYS( "GetKeys" ),
	GET_PROFILE_TEXT( "ProfileText" ),
	GET_PROFILE_UPDATED( "ProfileUpdated" ),
	GET_PROFILE_URL( "ProfileURL" ),
	GET_SETTING( "GetSetting" ),
    GET_STORAGE_GROUP_DIRS( "GetStorageGroupDirs"),
	GET_TIMEZONE( "GetTimeZone" ),
    REMOVE_STORAGE_GROUP_DIR("RemoveStorageGroupDir");
			
	private String endpoint;
	
	private MythEndpoint( String endpoint ) {
		this.endpoint = endpoint;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

}
