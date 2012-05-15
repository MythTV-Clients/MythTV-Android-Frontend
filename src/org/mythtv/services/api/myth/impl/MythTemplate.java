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
package org.mythtv.services.api.myth.impl;

import java.util.Date;
import java.util.List;

import org.mythtv.services.api.myth.ConnectionInfo;
import org.mythtv.services.api.myth.LogMessage;
import org.mythtv.services.api.myth.MythOperations;
import org.mythtv.services.api.myth.Setting;
import org.mythtv.services.api.myth.StorageGroupDirectory;
import org.mythtv.services.api.myth.TimeZoneInfo;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class MythTemplate extends AbstractMythOperations implements MythOperations {

	private final RestTemplate restTemplate;

	public MythTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#addStorageGroupDir(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean addStorageGroupDir( String groupName, String directoryName, String hostName ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#backupDatabase()
	 */
	@Override
	public boolean backupDatabase() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#changePassword(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean changePassword( String username, String oldPassword, String newPassword ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#checkDatabase(boolean)
	 */
	@Override
	public boolean checkDatabase( boolean repair ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#getConnectionInfo(java.lang.String)
	 */
	@Override
	public ConnectionInfo getConnectionInfo( String pin ) {
		
		ResponseEntity<ConnectionInfo> responseEntity = restTemplate.exchange( buildUri( "GetConnectionInfo" ), HttpMethod.GET, getRequestEntity(), ConnectionInfo.class );
		ConnectionInfo connectionInfo = responseEntity.getBody();
		
		return connectionInfo;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#getHostName()
	 */
	@Override
	public String getHostName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#getHosts()
	 */
	@Override
	public List<String> getHosts() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#getKeys()
	 */
	@Override
	public List<String> getKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#getLogs(java.lang.String, java.lang.String, int, int, java.lang.String, java.lang.String, int, java.lang.String, java.util.Date, java.util.Date, java.lang.String, java.lang.String)
	 */
	@Override
	public List<LogMessage> getLogs( String hostname, String application, int pid, int tid, String thread, String filename, int line, String function, Date from, Date to, String level, String messageContains ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#getSetting(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Setting> getSetting( String hostname, String key, String defaultValue ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#getStoreageGroupDirectories(java.lang.String, java.lang.String)
	 */
	@Override
	public List<StorageGroupDirectory> getStoreageGroupDirectories( String groupName, String hostname ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#getTimeZoneInfo()
	 */
	@Override
	public TimeZoneInfo getTimeZoneInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#profileDelete()
	 */
	@Override
	public boolean profileDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#profileSubmit()
	 */
	@Override
	public boolean profileSubmit() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#profileText()
	 */
	@Override
	public String profileText() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#profileUrl()
	 */
	@Override
	public String profileUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#profileUpdated()
	 */
	@Override
	public String profileUpdated() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#putSetting(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean putSetting( String hostname, String key, String value ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#removeStorageGroupDirectory(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean removeStorageGroupDirectory( String groupName, String directoryName, String hostname ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#sendMessage(java.lang.String, java.lang.String, int, int)
	 */
	@Override
	public boolean sendMessage( String message, String address, int udpPort, int timeout ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.myth.MythOperations#testDatabaseSettings(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public boolean testDatabaseSettings( String hostname, String username, String password, String databaseName, int databasePort ) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
