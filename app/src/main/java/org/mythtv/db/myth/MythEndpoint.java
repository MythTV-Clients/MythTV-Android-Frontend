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
