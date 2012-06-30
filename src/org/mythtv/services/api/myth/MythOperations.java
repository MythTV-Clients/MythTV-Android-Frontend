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
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.services.api.myth;

import java.util.Date;
import java.util.List;

/**
 * @author Daniel Frey
 *
 */
public interface MythOperations {

	/**
	 * - POST
	 * 
	 * @param groupName
	 * @param directoryName
	 * @param hostName
	 * @return
	 */
	boolean addStorageGroupDir( String groupName, String directoryName, String hostName );
	
	/**
	 * - POST
	 * 
	 * @return
	 */
	boolean backupDatabase();
	
	/**
	 * - POST
	 * 
	 * @param username
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	boolean changePassword( String username, String oldPassword, String newPassword );
	
	/**
	 * - POST
	 * 
	 * @param repair
	 * @return
	 */
	boolean checkDatabase( boolean repair );
	
	/**
	 * - GET
	 * 
	 * @param pin
	 * @return
	 */
	ConnectionInfo getConnectionInfo( String pin );
	
	/**
	 * - GET
	 * 
	 * @return
	 */
	String getHostName();
	
	/**
	 * - GET
	 * 
	 * @return
	 */
	List<String> getHosts();
	
	/**
	 * - GET
	 * 
	 * @return
	 */
	List<String> getKeys();
	
	/**
	 * - GET
	 * 
	 * @param hostname
	 * @param application
	 * @param pid
	 * @param tid
	 * @param thread
	 * @param filename
	 * @param line
	 * @param function
	 * @param from
	 * @param to
	 * @param level
	 * @param messageContains
	 * @return
	 */
	List<LogMessage> getLogs( String hostname, String application, int pid, int tid, String thread, String filename, int line, String function, Date from, Date to, String level, String messageContains );
	
	/**
	 * - GET
	 * 
	 * @param hostname
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	List<Setting> getSetting( String hostname, String key, String defaultValue );
	
	/**
	 * - GET
	 * 
	 * @param groupName
	 * @param hostname
	 * @return
	 */
	List<StorageGroupDirectory> getStoreageGroupDirectories( String groupName, String hostname );
	
	/**
	 * - GET
	 * 
	 * @return
	 */
	TimeZoneInfo getTimeZoneInfo();
	
	/**
	 * - POST
	 * 
	 * @return
	 */
	boolean profileDelete();
	
	/**
	 * - POST
	 * 
	 * @return
	 */
	boolean profileSubmit();
	
	/**
	 * - GET
	 * 
	 * @return
	 */
	String profileText();
	
	/**
	 * - GET
	 * 
	 * @return
	 */
	String profileUrl();
	
	/**
	 * - GET
	 * 
	 * @return
	 */
	String profileUpdated();
	
	/**
	 * - POST
	 * 
	 * @param hostname
	 * @param key
	 * @param value
	 * @return
	 */
	boolean putSetting( String hostname, String key, String value );
	
	/**
	 * - POST
	 * 
	 * @param groupName
	 * @param directoryName
	 * @param hostname
	 * @return
	 */
	boolean removeStorageGroupDirectory( String groupName, String directoryName, String hostname );
	
	/**
	 * - GET
	 * 
	 * @param message
	 * @param address
	 * @param udpPort
	 * @param timeout
	 * @return
	 */
	boolean sendMessage( String message, String address, int udpPort, int timeout );
	
	/**
	 * - POST
	 * 
	 * @param hostname
	 * @param username
	 * @param password
	 * @param databaseName
	 * @param databasePort
	 * @return
	 */
	boolean testDatabaseSettings( String hostname, String username, String password, String databaseName, int databasePort );
	
}
